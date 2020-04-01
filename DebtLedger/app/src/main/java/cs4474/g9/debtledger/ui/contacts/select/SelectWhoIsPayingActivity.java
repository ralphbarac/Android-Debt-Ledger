package cs4474.g9.debtledger.ui.contacts.select;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.ContactManager;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.MainActivity;
import cs4474.g9.debtledger.ui.shared.LoadableRecyclerView;
import cs4474.g9.debtledger.ui.shared.OnActionButtonClickedListener;

public class SelectWhoIsPayingActivity extends AppCompatActivity implements OnContactSelected, OnActionButtonClickedListener {

    public static final String SELECTED_CONTACT = "selected_contact";
    public static final String ADD_NEW_CONTACT = "add_new_contact";

    private LoadableRecyclerView selectContactView;
    private SelectContactAdapter selectContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_who_is_paying);

        final ImageView myAvatar = findViewById(R.id.my_avatar);
        final TextView myAvatarCharacter = findViewById(R.id.my_avatar_character);
        final View myContactContainer = findViewById(R.id.my_contact_container);

        final UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();
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

        makeRequestForContacts(loggedInUser);
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
        Intent toContacts = new Intent(this, MainActivity.class);
        toContacts.putExtra(ADD_NEW_CONTACT, true);
        toContacts.putExtra(MainActivity.CHANGE_TAB, MainActivity.CONTACTS_TAB);
        setResult(RESULT_CANCELED, toContacts);
        finish();
    }

    private void makeRequestForContacts(UserAccount loggedInUser) {
        selectContactView.onBeginLoading();

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
                                selectContactAdapter.setContacts(new ArrayList<>());
                                return;
                            }

                            // On success
                            selectContactAdapter.setContacts(ContactManager.parseContactsFromJson(response));
                        } catch (Exception e) {
                            // On parse error, set select contacts view to fail to finish loading mode
                            selectContactView.onFailToFinishLoading();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, set select contacts view to fail to finish loading mode
                        Log.d("CONTACTS", error.toString());
                        selectContactView.onFailToFinishLoading();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }
}
