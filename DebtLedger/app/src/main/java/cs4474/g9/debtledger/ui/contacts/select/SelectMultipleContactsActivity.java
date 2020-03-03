package cs4474.g9.debtledger.ui.contacts.select;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class SelectMultipleContactsActivity extends AppCompatActivity implements OnContactChecked {

    public static final String SELECTED_CONTACTS = "selected_contacts";

    private boolean selectedSelf;
    private List<Object> selectedGroups;
    private List<UserAccount> selectedContacts;

    private boolean isMenuIconEnabled = false;

    private SelectMultipleContactsAdapter multipleContactsAdapter;

    private CheckBox myCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_multiple_contacts);

        SelectMultipleContactsWrapper wrapper = (SelectMultipleContactsWrapper) getIntent().getSerializableExtra(SELECTED_CONTACTS);
        selectedSelf = wrapper.isSelectedSelf();
        selectedGroups = wrapper.getSelectedGroups();
        selectedContacts = wrapper.getSelectedContacts();
        isMenuIconEnabled = selectedSelf || !selectedContacts.isEmpty();

        final ImageView myAvatar = findViewById(R.id.my_avatar);
        final TextView myAvatarCharacter = findViewById(R.id.my_avatar_character);
        myCheckBox = findViewById(R.id.my_check_box);

        UserAccount loggedInUser = LoginRepository.getInstance(this).getLoggedInUser();
        myAvatar.setColorFilter(ColourGenerator.generateFromName(loggedInUser.getFirstName(), loggedInUser.getLastName()));
        myAvatarCharacter.setText(loggedInUser.getFirstName().substring(0, 1));

        myCheckBox.setChecked(selectedSelf);
        myCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedSelf = isChecked;
                if (isChecked) {
                    onContactChecked();
                } else {
                    onContactUnchecked();
                }
            }
        });

        findViewById(R.id.my_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCheckBox.setChecked(!myCheckBox.isChecked());
                selectedSelf = myCheckBox.isChecked();
                if (myCheckBox.isChecked()) {
                    onContactChecked();
                } else {
                    onContactUnchecked();
                }
            }
        });


        // TODO: Contact query system
        List<UserAccount> contacts = new ArrayList<>();
        contacts.add(new UserAccount("Randal", "Smith", "rmsith@uwo.ca"));

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
        SelectMultipleContactsWrapper wrapper = new SelectMultipleContactsWrapper(selectedSelf, selectedGroups, selectedContacts);
        data.putExtra(SELECTED_CONTACTS, wrapper);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onContactChecked() {
        selectedContacts = multipleContactsAdapter.getSelectedContacts();
        boolean shouldMenuIconBeEnabled = selectedSelf || !selectedContacts.isEmpty();

        if (isMenuIconEnabled != shouldMenuIconBeEnabled) {
            isMenuIconEnabled = shouldMenuIconBeEnabled;
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onContactUnchecked() {
        selectedContacts = multipleContactsAdapter.getSelectedContacts();
        boolean shouldMenuIconBeEnabled = selectedSelf || !selectedContacts.isEmpty();

        if (isMenuIconEnabled != shouldMenuIconBeEnabled) {
            isMenuIconEnabled = shouldMenuIconBeEnabled;
            invalidateOptionsMenu();
        }
    }

}
