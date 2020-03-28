package cs4474.g9.debtledger.ui.contacts.select;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ContactManager;
import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.ui.groups.GroupMembersWrapper;

public class SelectGroupMembersActivity extends AppCompatActivity implements OnContactChecked {

    public static final String SELECTED_GROUP_MEMBERS = "selected_group_members";

    private List<UserAccount> selectedContacts;

    private boolean isMenuIconEnabled = false;

    private SelectMultipleContactsAdapter multipleContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group_members);

        GroupMembersWrapper wrapper = (GroupMembersWrapper) getIntent().getSerializableExtra(SELECTED_GROUP_MEMBERS);
        selectedContacts = wrapper.getGroupMembers();
        isMenuIconEnabled = !selectedContacts.isEmpty();

        UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();

        ContactManager manager = new ContactManager();
        List<UserAccount> contacts = new ArrayList<>();
        Result result = manager.getAllContactsOf(loggedInUser);
        if (result instanceof Result.Success) {
            contacts = (List<UserAccount>) ((Result.Success) result).getData();
        } else {
            Toast.makeText(this, R.string.failure_contacts, Toast.LENGTH_SHORT).show();
        }

        final RecyclerView selectMultipleContactsView = findViewById(R.id.contacts_list);
        selectMultipleContactsView.setHasFixedSize(true);
        selectMultipleContactsView.setLayoutManager(new LinearLayoutManager(this));

        multipleContactsAdapter = new SelectMultipleContactsAdapter(contacts, selectedContacts);
        multipleContactsAdapter.addOnContactCheckedListener(this);
        selectMultipleContactsView.setAdapter(multipleContactsAdapter);
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

}
