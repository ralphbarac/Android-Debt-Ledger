package cs4474.g9.debtledger.ui.contacts.select;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.LoginRepository;
import cs4474.g9.debtledger.data.model.LoggedInUser;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class SelectContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        final ImageView myAvatar = findViewById(R.id.my_avatar);
        final TextView myAvatarCharacter = findViewById(R.id.my_avatar_character);

        LoggedInUser loggedInUser = LoginRepository.getInstance().getLoggedInUser();
        myAvatar.setColorFilter(ColourGenerator.generateFromName(loggedInUser.getFirstName(), loggedInUser.getLastName()));
        myAvatarCharacter.setText(loggedInUser.getFirstName().substring(0, 1));

        // TODO: Contact query system
        List<UserAccount> contacts = new ArrayList<>();
        contacts.add(new UserAccount("Randal", "Smith", "rmsith@uwo.ca"));
        final RecyclerView selectContactView = findViewById(R.id.contacts_list);
        selectContactView.setHasFixedSize(true);
        selectContactView.setLayoutManager(new LinearLayoutManager(this));
        final RecyclerView.Adapter selectContactAdapter = new SelectContactAdapter(contacts);
        selectContactView.setAdapter(selectContactAdapter);
    }
}
