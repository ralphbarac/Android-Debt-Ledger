package cs4474.g9.debtledger.ui.contacts.select;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.ContactManager;
import cs4474.g9.debtledger.data.GroupManager;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.MainActivity;
import cs4474.g9.debtledger.ui.groups.select.OnGroupChecked;
import cs4474.g9.debtledger.ui.groups.select.SelectMultipleGroupsAdapter;
import cs4474.g9.debtledger.ui.shared.LoadableRecyclerView;
import cs4474.g9.debtledger.ui.shared.OnActionButtonClickedListener;
import cs4474.g9.debtledger.ui.transaction.WhoOwesWrapper;

public class SelectWhoOwesActivity extends AppCompatActivity implements OnContactChecked, OnGroupChecked {

    public static final String SELECTED_CONTACTS = "selected_contacts";
    public static final String ADD_NEW_CONTACT = "add_new_contact";
    public static final String ADD_NEW_GROUP = "add_new_group";

    private boolean selectedSelf;
    private List<Group> selectedGroups;
    private List<UserAccount> selectedContacts;

    private boolean isMenuIconEnabled = false;

    private LoadableRecyclerView selectMultipleContactsView;
    private SelectMultipleContactsAdapter multipleContactsAdapter;
    private LoadableRecyclerView selectMultipleGroupsView;
    private SelectMultipleGroupsAdapter multipleGroupsAdapter;

    private CheckBox myCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_who_owes);

        WhoOwesWrapper wrapper = (WhoOwesWrapper) getIntent().getSerializableExtra(SELECTED_CONTACTS);
        selectedSelf = wrapper.isSelectedSelf();
        selectedGroups = wrapper.getSelectedGroups();
        selectedContacts = wrapper.getSelectedContacts();
        isMenuIconEnabled = selectedSelf || !selectedContacts.isEmpty();

        final ImageView myAvatar = findViewById(R.id.my_avatar);
        final TextView myAvatarCharacter = findViewById(R.id.my_avatar_character);
        myCheckBox = findViewById(R.id.my_check_box);

        final UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();
        myAvatar.setColorFilter(ColourGenerator.generateFromName(loggedInUser.getFirstName(), loggedInUser.getLastName()));
        myAvatarCharacter.setText(loggedInUser.getFirstName().substring(0, 1));

        myCheckBox.setChecked(selectedSelf);
        myCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedSelf = isChecked;
                if (isChecked) {
                    onContactChecked(loggedInUser);
                } else {
                    onContactUnchecked(loggedInUser);
                }
            }
        });

        findViewById(R.id.my_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCheckBox.setChecked(!myCheckBox.isChecked());
                selectedSelf = myCheckBox.isChecked();
                if (myCheckBox.isChecked()) {
                    onContactChecked(loggedInUser);
                } else {
                    onContactUnchecked(loggedInUser);
                }
            }
        });

        selectMultipleGroupsView = findViewById(R.id.groups_list);
        selectMultipleGroupsView.addOnActionButtonClickedClickListener(new OnActionButtonClickedListener() {
            @Override
            public void onFailedToLoadActionButtonClicked() {
                makeRequestForGroups(loggedInUser);
            }

            @Override
            public void onEmptyActionButtonClicked() {
                Intent toGroups = new Intent(SelectWhoOwesActivity.this, MainActivity.class);
                toGroups.putExtra(ADD_NEW_GROUP, true);
                toGroups.putExtra(MainActivity.CHANGE_TAB, MainActivity.GROUPS_TAB);
                setResult(RESULT_CANCELED, toGroups);
                finish();
            }
        });
        selectMultipleGroupsView.setHasFixedSize(true);
        selectMultipleGroupsView.setLayoutManager(new LinearLayoutManager(this));

        multipleGroupsAdapter = new SelectMultipleGroupsAdapter();
        multipleGroupsAdapter.addOnGroupCheckedListener(this);
        selectMultipleGroupsView.setAdapter(multipleGroupsAdapter);

        selectMultipleContactsView = findViewById(R.id.contacts_list);
        selectMultipleContactsView.addOnActionButtonClickedClickListener(new OnActionButtonClickedListener() {
            @Override
            public void onFailedToLoadActionButtonClicked() {
                makeRequestForContacts(loggedInUser);
            }

            @Override
            public void onEmptyActionButtonClicked() {
                Intent toContacts = new Intent(SelectWhoOwesActivity.this, MainActivity.class);
                toContacts.putExtra(ADD_NEW_CONTACT, true);
                toContacts.putExtra(MainActivity.CHANGE_TAB, MainActivity.CONTACTS_TAB);
                setResult(RESULT_CANCELED, toContacts);
                finish();
            }
        });
        selectMultipleContactsView.setHasFixedSize(true);
        selectMultipleContactsView.setLayoutManager(new LinearLayoutManager(this));

        multipleContactsAdapter = new SelectMultipleContactsAdapter();
        multipleContactsAdapter.addOnContactCheckedListener(this);
        selectMultipleContactsView.setAdapter(multipleContactsAdapter);

        makeRequestForContacts(loggedInUser);
        makeRequestForGroups(loggedInUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_multiple_contacts, menu);

        MenuItem item = menu.getItem(0);
        if (isMenuIconEnabled) {
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
            case R.id.confirm_selection:
                returnResult();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void returnResult() {
        Intent data = new Intent();
        selectedContacts = multipleContactsAdapter.getSelectedContacts();
        WhoOwesWrapper wrapper = new WhoOwesWrapper(selectedSelf, selectedGroups, selectedContacts);
        data.putExtra(SELECTED_CONTACTS, wrapper);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onContactChecked(UserAccount contact) {
        selectedContacts = multipleContactsAdapter.getSelectedContacts();
        updateMenuIconIfNecessary();
    }

    @Override
    public void onContactUnchecked(UserAccount contact) {
        selectedContacts = multipleContactsAdapter.getSelectedContacts();
        multipleGroupsAdapter.unselectGroupsIfNecessary(contact);
        selectedGroups = multipleGroupsAdapter.getSelectedGroups();
        updateMenuIconIfNecessary();
    }

    @Override
    public void onGroupChecked(Group group) {
        selectedGroups = multipleGroupsAdapter.getSelectedGroups();
        multipleContactsAdapter.selectContactsFromGroup(group);
        selectedContacts = multipleContactsAdapter.getSelectedContacts();
        updateMenuIconIfNecessary();
    }

    @Override
    public void onGroupUnchecked(Group group) {
        multipleGroupsAdapter.unselectGroupsIfNecessary(group);
        selectedGroups = multipleGroupsAdapter.getSelectedGroups();
        multipleContactsAdapter.unselectContactsFromGroup(group);
        selectedContacts = multipleContactsAdapter.getSelectedContacts();
        updateMenuIconIfNecessary();
    }

    private void updateMenuIconIfNecessary() {
        boolean shouldMenuIconBeEnabled = selectedSelf || !selectedContacts.isEmpty();

        if (isMenuIconEnabled != shouldMenuIconBeEnabled) {
            isMenuIconEnabled = shouldMenuIconBeEnabled;
            invalidateOptionsMenu();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel/terminate any requests that are still running or queued
        ConnectionAdapter.getInstance().cancelAllRequests(hashCode());
    }

    private void makeRequestForContacts(UserAccount loggedInUser) {
        selectMultipleContactsView.onBeginLoading();

        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + ContactManager.LIST_END_POINT + "/" + loggedInUser.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("CONTACTS", response.toString());

                        try {
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("empty")) {
                                multipleContactsAdapter.setContacts(new ArrayList<>(), selectedContacts);
                            } else {
                                // On success
                                multipleContactsAdapter.setContacts(ContactManager.parseContactsFromJson(response), selectedContacts);
                            }
                        } catch (Exception e) {
                            // On parse error, set select contacts view to fail to finish loading mode
                            selectMultipleContactsView.onFailToFinishLoading();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, set select contacts view to fail to finish loading mode
                        Log.d("CONTACTS", error.toString());
                        selectMultipleContactsView.onFailToFinishLoading();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

    private void makeRequestForGroups(UserAccount loggedInUser) {
        selectMultipleGroupsView.onBeginLoading();

        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + GroupManager.LIST_END_POINT + "/" + loggedInUser.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GROUPS", response.toString());

                        try {
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("empty")) {
                                multipleGroupsAdapter.setGroups(new ArrayList<>(), selectedGroups);
                            } else {
                                // On success
                                multipleGroupsAdapter.setGroups(GroupManager.parseGroupsFromJson(response), selectedGroups);
                            }
                        } catch (Exception e) {
                            // On parse error, set select groups view to fail to finish loading mode
                            selectMultipleGroupsView.onFailToFinishLoading();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, set select groups view to fail to finish loading mode
                        Log.d("GROUPS", error.toString());
                        selectMultipleGroupsView.onFailToFinishLoading();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

}
