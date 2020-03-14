package cs4474.g9.debtledger.ui.contacts.select;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ContactManager;
import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.MainActivity;
import cs4474.g9.debtledger.ui.shared.LoadableRecyclerView;
import cs4474.g9.debtledger.ui.shared.OnActionButtonClickedListener;

public class SelectWhoIsPayingActivity extends AppCompatActivity implements OnContactSelected, OnActionButtonClickedListener {

    public static final String SELECTED_CONTACT = "selected_contact";
    public static final String ADD_NEW_CONTACT = "add_new_contact";

    private AsyncTask<UserAccount, Void, Result> getContactsProcess;

    private LoadableRecyclerView selectContactView;
    private SelectContactAdapter selectContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_who_is_paying);

        final ImageView myAvatar = findViewById(R.id.my_avatar);
        final TextView myAvatarCharacter = findViewById(R.id.my_avatar_character);
        final View myContactContainer = findViewById(R.id.my_contact_container);

        final UserAccount loggedInUser = LoginRepository.getInstance(this).getLoggedInUser();
        myAvatar.setColorFilter(ColourGenerator.generateFromName(loggedInUser.getFirstName(), loggedInUser.getLastName()));
        myAvatarCharacter.setText(loggedInUser.getFirstName().substring(0, 1));
        myContactContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactSelected(loggedInUser);
            }
        });

        selectContactView = findViewById(R.id.contacts_list);
        selectContactView.setHasFixedSize(true);
        selectContactView.setLayoutManager(new LinearLayoutManager(this));
        selectContactView.addOnActionButtonClickedClickListener(this);

        selectContactAdapter = new SelectContactAdapter();
        selectContactAdapter.addOnContactSelectedListener(this);
        selectContactView.setAdapter(selectContactAdapter);

        getContactsProcess = new GetContactsProcess();
        getContactsProcess.execute(loggedInUser);
    }

    @Override
    public void onContactSelected(UserAccount contact) {
        Intent data = new Intent();
        data.putExtra(SELECTED_CONTACT, contact);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getContactsProcess != null) {
            getContactsProcess.cancel(true);
        }
    }

    @Override
    public void onFailedToLoadActionButtonClicked() {
        // Retry getting contacts
        if (getContactsProcess == null || getContactsProcess.getStatus() == AsyncTask.Status.FINISHED) {
            getContactsProcess = new GetContactsProcess();
            getContactsProcess.execute(LoginRepository.getInstance(this).getLoggedInUser());
        }
    }

    @Override
    public void onEmptyActionButtonClicked() {
        Intent toContacts = new Intent(this, MainActivity.class);
        toContacts.putExtra(ADD_NEW_CONTACT, true);
        toContacts.putExtra(MainActivity.CHANGE_TAB, MainActivity.CONTACTS_TAB);
        setResult(RESULT_CANCELED, toContacts);
        finish();
    }

    private final class GetContactsProcess extends AsyncTask<UserAccount, Void, Result> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            selectContactView.onBeginLoading();
        }

        @Override
        protected Result doInBackground(UserAccount... params) {
            UserAccount loggedInUser = params[0];
            ContactManager manager = new ContactManager();
            // TODO: Remove sleep
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(250, 1500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return manager.getAllContactsOf(loggedInUser);
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            if (result instanceof Result.Success) {
                List<UserAccount> contacts;
                contacts = (List<UserAccount>) ((Result.Success) result).getData();
                selectContactAdapter.setContacts(contacts);
            } else {
                selectContactView.onFailToFinishLoading();
            }
        }

    }
}
