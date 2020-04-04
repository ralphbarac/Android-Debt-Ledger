package cs4474.g9.debtledger;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Locale;

import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.TransactionManager;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.BalanceCalculator;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.contacts.ContactHistoryListAdapter;
import cs4474.g9.debtledger.ui.shared.LoadableRecyclerView;

public class ViewContactActivity extends AppCompatActivity {

    public static final String CONTACT = "contact";

    private UserAccount contactAccount;
    private ContactHistoryListAdapter historyAdapter;
    private LoadableRecyclerView transactionHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);


        contactAccount = (UserAccount) getIntent().getSerializableExtra(CONTACT);
        UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();


        // Binding contact data to UI
        final ImageView contactAvatar = findViewById(R.id.contact_avatar);
        contactAvatar.setColorFilter(ColourGenerator.generateFromName(contactAccount.getFirstName(),contactAccount.getLastName()));
        final TextView contactCharacter = findViewById(R.id.contact_avatar_character);
        contactCharacter.setText(contactAccount.getFirstName().substring(0, 1));
        final TextView contactNameTitle = findViewById(R.id.name);
        contactNameTitle.setText(contactAccount.getFirstName() + " " + contactAccount.getLastName());
        final TextView contactEmailTitle = findViewById(R.id.email);
        contactEmailTitle.setText(contactAccount.getEmail());
        final MaterialButton repayButton = findViewById(R.id.repay);
        transactionHistoryList = findViewById(R.id.transactionHistory);


        transactionHistoryList.setHasFixedSize(true);
        transactionHistoryList.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new ContactHistoryListAdapter();
        transactionHistoryList.setAdapter(historyAdapter);

        // Binding dynamic contact data to UI
        // Get all past transactions between the logged in user and the viewed contact
        thing();




    }

    private void thing(){
        UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();
        transactionHistoryList.onBeginLoading();
        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + TransactionManager.CONTACT_END_POINT + "/" + loggedInUser.getId() + "/" + contactAccount.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("VIEW CONTACT", response.toString());

                        try {
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("empty")) {
                                historyAdapter.setTransactionList(new ArrayList<>());
                            } else {
                                // On success
                                historyAdapter.setTransactionList(TransactionManager.createListArrayFromJsonArray(response));
                            }
                        } catch (Exception e) {
                            // On parse error, set contacts view to fail to finish loading mode
                            transactionHistoryList.onFailToFinishLoading();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, set contacts view to fail to finish loading mode
                        Log.d("CONTACTS", error.toString());
                        transactionHistoryList.onFailToFinishLoading();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }
}
