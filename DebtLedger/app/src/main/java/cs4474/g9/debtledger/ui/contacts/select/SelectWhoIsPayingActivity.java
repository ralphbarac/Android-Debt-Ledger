package cs4474.g9.debtledger.ui.contacts.select;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import cs4474.g9.debtledger.logic.ColourGenerator;

public class SelectWhoIsPayingActivity extends AppCompatActivity implements OnContactSelected {

    public static final String SELECTED_CONTACT = "selected_contact";

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


        ContactManager manager = new ContactManager();
        List<UserAccount> contacts = new ArrayList<>();
        Result result = manager.getAllContactsOf(loggedInUser);
        if (result instanceof Result.Success) {
            contacts = (List<UserAccount>) ((Result.Success) result).getData();
        } else {
            Toast.makeText(this, R.string.failure_contacts, Toast.LENGTH_SHORT).show();
        }

        RecyclerView selectContactView = findViewById(R.id.contacts_list);
        selectContactView.setHasFixedSize(true);
        selectContactView.setLayoutManager(new LinearLayoutManager(this));

        SelectContactAdapter selectContactAdapter = new SelectContactAdapter(contacts);
        selectContactAdapter.addOnContactSelectedListener(this);
        selectContactView.setAdapter(selectContactAdapter);
    }

    @Override
    public void onContactSelected(UserAccount contact) {
        Intent data = new Intent();
        data.putExtra(SELECTED_CONTACT, contact);
        setResult(RESULT_OK, data);
        finish();
    }
}
