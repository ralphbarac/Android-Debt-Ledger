package cs4474.g9.debtledger.ui.groups;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.GroupManager;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.contacts.select.SelectGroupMembersActivity;

public class CreateEditGroupActivity extends AppCompatActivity implements OnMemberRemoved {

    public final static String MODE = "action";
    public final static int CREATE_MODE = 0;
    public final static int EDIT_MODE = 1;
    public final static String GROUP = "group";

    final int SELECT_GROUP_MEMBERS = 0;

    private boolean isCreateMode;
    private boolean isFormSubmittable = false;

    private String name;
    private String old_name;
    private long group_id;
    private List<UserAccount> members;

    private ImageView groupAvatar;
    private TextView groupAvatarCharacter;
    private TextView groupNameTitle;
    private TextView memberCount;
    private TextInputEditText groupNameInput;

    private RecyclerView groupMembersView;
    private GroupMemberListAdapter groupMembersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_group);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.getIntExtra(MODE, CREATE_MODE) == EDIT_MODE) {
            isCreateMode = false;
            setTitle(R.string.title_edit_group);
            Group group = (Group) intent.getSerializableExtra(GROUP);
            name = group.getGroupName();
            old_name = group.getGroupName();
            group_id = group.getId();
            members = group.getGroupMembers();
        } else {
            isCreateMode = true;
            setTitle(R.string.title_add_group);
            name = "";
            members = new ArrayList<>();
        }

        groupAvatar = findViewById(R.id.group_avatar);
        groupAvatarCharacter = findViewById(R.id.group_avatar_character);
        groupNameTitle = findViewById(R.id.group_name_title);
        memberCount = findViewById(R.id.member_count);
        groupNameInput = findViewById(R.id.group_name_input);

        if (!isCreateMode) {
            groupNameInput.setText(name);
        }
        groupNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = s.toString();
                updateFieldsInPlaceOfViewModelsObserver();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        groupMembersView = findViewById(R.id.group_members_list);
        groupMembersView.setHasFixedSize(true);
        groupMembersView.setLayoutManager(new LinearLayoutManager(this));

        groupMembersAdapter = new GroupMemberListAdapter(members);
        groupMembersAdapter.addOnMemberRemovedListener(this);
        groupMembersView.setAdapter(groupMembersAdapter);

        updateFieldsInPlaceOfViewModelsObserver();
    }

    private void updateFieldsInPlaceOfViewModelsObserver() {
        groupNameTitle.setText(name);
        memberCount.setText(
                members.size() == 0
                        ? "No members"
                        : String.format(Locale.CANADA, "%d member%s", members.size(), members.size() > 1 ? "s" : "")
        );

        if (groupNameTitle.length() == 0) {
            groupAvatar.setColorFilter(Color.BLACK);
            groupAvatarCharacter.setText("");
        } else {
            groupAvatar.setColorFilter(ColourGenerator.generateFromGroupName(name));
            groupAvatarCharacter.setText(name.substring(0, 1));
        }

        boolean shouldFormBeSubmittable;
        if (groupNameTitle.length() == 0 || members.size() == 0) {
            shouldFormBeSubmittable = false;
        } else {
            shouldFormBeSubmittable = true;
        }
        if (shouldFormBeSubmittable != isFormSubmittable) {
            isFormSubmittable = shouldFormBeSubmittable;
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // When clicking X in top left, mimic behaviour of back
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_edit_group, menu);

        MenuItem item = menu.getItem(0);
        if (isFormSubmittable) {
            item.setEnabled(true);
            item.getIcon().mutate().setAlpha(255);
        } else {
            item.setEnabled(false);
            item.getIcon().mutate().setAlpha(130);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit:
                createOrSaveGroup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_GROUP_MEMBERS) {
            if (resultCode == RESULT_OK) {
                Serializable value = data.getSerializableExtra(SelectGroupMembersActivity.SELECTED_GROUP_MEMBERS);
                if (value instanceof GroupMembersWrapper) {
                    GroupMembersWrapper wrapper = (GroupMembersWrapper) value;
                    members = wrapper.getGroupMembers();
                    groupMembersAdapter.setGroupMembers(members);
                    updateFieldsInPlaceOfViewModelsObserver();
                }
            }
        }
    }

    public void addGroupMembers(View view) {
        Intent toSelectGroupMembers = new Intent(this, SelectGroupMembersActivity.class);
        GroupMembersWrapper wrapper = new GroupMembersWrapper(members);
        toSelectGroupMembers.putExtra(SelectGroupMembersActivity.SELECTED_GROUP_MEMBERS, wrapper);
        startActivityForResult(toSelectGroupMembers, SELECT_GROUP_MEMBERS);
    }

    @Override
    public void onGroupMemberRemoved(List<UserAccount> newListOfMembers) {
        members = newListOfMembers;
        updateFieldsInPlaceOfViewModelsObserver();
    }

    public void createOrSaveGroup() {
        if (isCreateMode) { // add new group
            UserAccount owner = LoginRepository.getInstance().getLoggedInUser();
            JSONObject add_info;
            try {
                add_info = GroupManager.createJsonForAddGroup(owner.getId(), name);
                addGroup(add_info);
            } catch (JSONException e) {
                throw new RuntimeException();
            }
        } else { // edit group
            if (!old_name.equals(name)) {
                updateGroup(group_id);
            }

            JSONObject set_info;
            try {
                set_info = GroupManager.createJsonForSetGroupMembers(group_id, members);
                setGroupMembers(set_info);
            } catch (JSONException e) {
                throw new RuntimeException();
            }
        }
    }

    public void updateGroup(long id) {
        RedirectableJsonArrayRequest updateRequest = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + "/contact_group/update/" + id + "/" + name + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("UPDATE GROUP", response.toString());
                        try {
                            if (response.getJSONObject(0).has("error") || response.getJSONObject(0).has("failure")) {
                                // If error, do nothing...
                                throw new Exception();
                            } else {
                                // On success, do nothing
                            }
                        } catch (Exception e) {
                            Toast.makeText(CreateEditGroupActivity.this, "Error Adding Group", Toast.LENGTH_SHORT);
                            throw new RuntimeException();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display add failed to user
                        Log.d("UPDATE GROUP", error.toString());
                        Toast.makeText(CreateEditGroupActivity.this, "Error Adding Group", Toast.LENGTH_SHORT);
                    }
                }
        );
        ConnectionAdapter.getInstance().addToRequestQueue(updateRequest, hashCode());
    }

    public void addGroup(JSONObject data) {
        RedirectableJsonArrayRequest addRequest = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + "/contact_group/add/" + data.toString() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("ADD GROUP", response.toString());
                        try {
                            if (response.getJSONObject(0).has("error") ||
                                    response.getJSONObject(0).has("failure")) {
                                // If error, do nothing...
                                throw new Exception();
                            } else {
                                // On success, set members
                                int group_id = response.getJSONObject(0).getInt("id");
                                JSONObject set_info;
                                set_info = GroupManager.createJsonForSetGroupMembers(group_id, members);
                                setGroupMembers(set_info);
                            }
                        } catch (Exception e) {
                            Toast.makeText(CreateEditGroupActivity.this, "Error Adding Group", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display add failed to user
                        Log.d("ADD GROUP", error.toString());
                        Toast.makeText(CreateEditGroupActivity.this, "Error Adding Group", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        ConnectionAdapter.getInstance().addToRequestQueue(addRequest, hashCode());
    }

    public void setGroupMembers(JSONObject data) {
        RedirectableJsonArrayRequest setRequest = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + "/contact_group_member/set/" + data.toString() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("SET GROUP MEMBERS", response.toString());
                        try {
                            // If error, do nothing...
                            if (response.getJSONObject(0).has("error") ||
                                    response.getJSONObject(0).has("failure")) {
                                throw new Exception();
                            } else {
                                // On success, notify user and exit activity
                                if (isCreateMode) {
                                    Toast.makeText(CreateEditGroupActivity.this, "Group Added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CreateEditGroupActivity.this, "Group Edited", Toast.LENGTH_SHORT).show();
                                }

                                if (isCreateMode) {
                                    setResult(RESULT_OK);
                                } else {
                                    Intent data = new Intent();
                                    data.putExtra(GROUP, new Group(group_id, name, members));
                                    setResult(RESULT_OK, data);
                                }
                                finish();
                            }
                        } catch (Exception e) {
                            // On parse error, display set failed to user
                            Toast.makeText(CreateEditGroupActivity.this, "Set Group Members Failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display set failed to user
                        Log.d("SET GROUP DATA", error.toString());
                        Toast.makeText(CreateEditGroupActivity.this, "Set Group Members Failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        ConnectionAdapter.getInstance().addToRequestQueue(setRequest, hashCode());
    }
}
