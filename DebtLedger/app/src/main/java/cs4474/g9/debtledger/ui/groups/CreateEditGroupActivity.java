package cs4474.g9.debtledger.ui.groups;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
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
        // TODO: Create or Save Group
    }
}
