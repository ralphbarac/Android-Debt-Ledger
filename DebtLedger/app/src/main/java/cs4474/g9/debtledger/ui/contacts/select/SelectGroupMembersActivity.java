package cs4474.g9.debtledger.ui.contacts.select;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.ui.groups.GroupMembersWrapper;
import cs4474.g9.debtledger.ui.shared.LoadableRecyclerView;
import cs4474.g9.debtledger.ui.shared.OnActionButtonClickedListener;

public class SelectGroupMembersActivity extends AppCompatActivity implements OnContactChecked, OnActionButtonClickedListener {

    public static final String SELECTED_GROUP_MEMBERS = "selected_group_members";

    private List<UserAccount> selectedContacts;

    private boolean isMenuIconEnabled = false;

    private LoadableRecyclerView selectMultipleContactsView;
    private SelectMultipleContactsAdapter multipleContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group_members);

        GroupMembersWrapper wrapper = (GroupMembersWrapper) getIntent().getSerializableExtra(SELECTED_GROUP_MEMBERS);
        selectedContacts = wrapper.getGroupMembers();
        isMenuIconEnabled = !selectedContacts.isEmpty();

        selectMultipleContactsView = findViewById(R.id.contacts_list);
        selectMultipleContactsView.setHasFixedSize(true);
        selectMultipleContactsView.setLayoutManager(new LinearLayoutManager(this));
        selectMultipleContactsView.addOnActionButtonClickedClickListener(this);

        multipleContactsAdapter = new SelectMultipleContactsAdapter();
        multipleContactsAdapter.addOnContactCheckedListener(this);
        selectMultipleContactsView.setAdapter(multipleContactsAdapter);

        UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();
        makeRequestForContacts(loggedInUser);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel/terminate any requests that are still running or queued
        ConnectionAdapter.getInstance().cancelAllRequests(hashCode());
    }

    @Override
    public void onFailedToLoadActionButtonClicked() {
        // Retry getting contacts
        makeRequestForContacts(LoginRepository.getInstance().getLoggedInUser());
    }

    @Override
    public void onEmptyActionButtonClicked() {
        // No button, so this should be impossible :)
    }

    private void returnResult() {
        Intent data = new Intent();
        selectedContacts = multipleContactsAdapter.getSelectedContacts();
        GroupMembersWrapper wrapper = new GroupMembersWrapper(selectedContacts);
        data.putExtra(SELECTED_GROUP_MEMBERS, wrapper);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onContactChecked(UserAccount contact) {
        selectedContacts = multipleContactsAdapter.getSelectedContacts();
        boolean shouldMenuIconBeEnabled = !selectedContacts.isEmpty();

        if (isMenuIconEnabled != shouldMenuIconBeEnabled) {
            isMenuIconEnabled = shouldMenuIconBeEnabled;
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onContactUnchecked(UserAccount contact) {
        selectedContacts = multipleContactsAdapter.getSelectedContacts();
        boolean shouldMenuIconBeEnabled = !selectedContacts.isEmpty();

        if (isMenuIconEnabled != shouldMenuIconBeEnabled) {
            isMenuIconEnabled = shouldMenuIconBeEnabled;
            invalidateOptionsMenu();
        }
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
                                multipleContactsAdapter.setContacts(new ArrayList<>(), new ArrayList<>());
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

}
