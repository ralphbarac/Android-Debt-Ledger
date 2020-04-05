package cs4474.g9.debtledger;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.Transaction;
import cs4474.g9.debtledger.data.model.TransactionManager;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.contacts.ContactHistoryListAdapter;
import cs4474.g9.debtledger.ui.settings.AccessibleColours;
import cs4474.g9.debtledger.ui.settings.Preference;
import cs4474.g9.debtledger.ui.shared.LoadableRecyclerView;
import cs4474.g9.debtledger.ui.shared.OnActionButtonClickedListener;

public class ViewContactActivity extends AppCompatActivity implements OnActionButtonClickedListener {

    public static final String CONTACT = "contact";

    private UserAccount contactAccount;
    private ContactHistoryListAdapter historyAdapter;
    private LoadableRecyclerView transactionHistoryList;
    private List<Transaction> listOfTransactions;
    private BigDecimal result;
    private MaterialButton repayButton;
    private TextView youowe;
    private TextView total;

    private boolean isInAccessibleMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isInAccessibleMode = preferences.getBoolean(Preference.ACCESSIBILITY, false);

        setContentView(R.layout.activity_view_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactAccount = (UserAccount) getIntent().getSerializableExtra(CONTACT);
        UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();


        // Binding contact data to UI
        final ImageView contactAvatar = findViewById(R.id.contact_avatar);
        contactAvatar.setColorFilter(ColourGenerator.generateFromName(contactAccount.getFirstName(), contactAccount.getLastName()));
        final TextView contactCharacter = findViewById(R.id.contact_avatar_character);
        contactCharacter.setText(contactAccount.getFirstName().substring(0, 1));
        final TextView contactNameTitle = findViewById(R.id.name);
        contactNameTitle.setText(contactAccount.getFirstName() + " " + contactAccount.getLastName());
        final TextView contactEmailTitle = findViewById(R.id.email);
        contactEmailTitle.setText(contactAccount.getEmail());
        repayButton = findViewById(R.id.repay);
        transactionHistoryList = findViewById(R.id.transactionHistory);
        youowe = findViewById(R.id.youowe);
        total = findViewById(R.id.total);

        transactionHistoryList.setHasFixedSize(true);
        transactionHistoryList.setLayoutManager(new LinearLayoutManager(this));
        transactionHistoryList.addOnActionButtonClickedClickListener(this);
        transactionHistoryList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        historyAdapter = new ContactHistoryListAdapter(isInAccessibleMode);
        transactionHistoryList.setAdapter(historyAdapter);

        // Binding dynamic contact data to UI
        // Get all past transactions between the logged in user and the viewed contact and display them in the recycler view
        makeRequestForTransactionHistory();

        // Creditor is the guy who is owed money
        // Debtor is the guy who owes money

        // Calculate the balance between the logged in user and the viewed contact


        // Define Click action for button
        repayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String forgiveRepay = "";
                if (result.compareTo(BigDecimal.ZERO) > 0) {
                    forgiveRepay = "forgive";
                } else {
                    forgiveRepay = "repay";
                }
                // Start a dialogue confirmation
                new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("Confirm Balance")
                        .setMessage("Are you sure you want to " + forgiveRepay + " " + contactAccount.getFirstName() + " $" + result.abs().toString() + "?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Create transaction which balances the excess
                                UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();
                                List<Transaction> tempList = new ArrayList<>();

                                if (result.compareTo(BigDecimal.ZERO) > 0) {
                                    tempList.add(new Transaction(loggedInUser.getId(), contactAccount.getId(), "Forgiven", result));
                                } else {
                                    tempList.add(new Transaction(contactAccount.getId(), loggedInUser.getId(), "Repayment", result));
                                }
                                makeAddTransactionsRequest(tempList);

                            }
                        })
                        .show();


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel/terminate any requests that are still running or queued
        ConnectionAdapter.getInstance().cancelAllRequests(hashCode());
    }

    @Override
    public void onFailedToLoadActionButtonClicked() {
        makeRequestForTransactionHistory();
    }

    @Override
    public void onEmptyActionButtonClicked() {
        // No button, so this should be impossible :)
    }

    private void makeAddTransactionsRequest(List<Transaction> transactions) {
        JSONArray input;
        try {
            input = TransactionManager.createJsonArrayFromTransactions(transactions);
        } catch (JSONException e) {
            throw new RuntimeException();
        }

        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + TransactionManager.ADD_MULTIPLE_END_POINT + "/" + input.toString() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("TRANSACTIONS", response.toString());

                        try {
                            // If error, display add transactions failed...
                            if (response.getJSONObject(0).has("error")
                                    || response.getJSONObject(0).has("failure")) {
                                throw new Exception();
                            } else {
                                // On success, return to dashboard
                                response.getJSONObject(0).get("success");

                                // Update the list
                                makeRequestForTransactionHistory();
                                result = calculateBalance();
                                setupButton();
                            }
                        } catch (Exception e) {
                            // On parse error, display add transactions failed to user
                            Toast.makeText(ViewContactActivity.this, R.string.failure_add_transactions, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display add transactions failed to user
                        Log.d("TRANSACTIONS", error.toString());
                        Toast.makeText(ViewContactActivity.this, R.string.failure_add_transactions, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }


    private void setupButton() {
        // There are 3 cases, 1. The balance between 2 users is 0, 2. The balance is negative, 3. The balance is positive

        if (result.compareTo(BigDecimal.ZERO) == 0) {
            // No money is owed
            repayButton.setVisibility(View.INVISIBLE);
            if (historyAdapter.getItemCount() > 0) {
                youowe.setText("Balance: ");
                total.setText("$0.00");
            } else {
                youowe.setText("");
                total.setText("");
            }
            total.setTextColor(Color.BLACK);
        } else if (result.compareTo(BigDecimal.ZERO) > 0) {
            // You are owed money
            repayButton.setVisibility(View.VISIBLE);
            repayButton.setEnabled(true);
            repayButton.setText("Forgive");
            youowe.setText("You're owed: ");
            total.setText("$" + result.toString());
            total.setTextColor(AccessibleColours.getPositiveColour(isInAccessibleMode));
        } else {
            // You owe money
            repayButton.setVisibility(View.VISIBLE);
            repayButton.setEnabled(true);
            repayButton.setText("Repay");
            youowe.setText("You owe: ");
            total.setText("$" + result.abs().toString());
            total.setTextColor(AccessibleColours.getNegativeColour(isInAccessibleMode));
        }
    }

    private BigDecimal calculateBalance() {
        // Calculate the balance between the logged in user and the viewed contact
        UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();
        BigDecimal sum = BigDecimal.ZERO;
        if (listOfTransactions != null) {
            for (int i = 0; i < listOfTransactions.size(); i++) {
                if (listOfTransactions.get(i).getCreditor() == loggedInUser.getId()) {
                    // Then amount is positive
                    sum = sum.add(listOfTransactions.get(i).getAmount());
                } else {
                    sum = sum.subtract(listOfTransactions.get(i).getAmount());
                }
            }
        }
        return sum;
    }

    private void makeRequestForTransactionHistory() {
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

                                listOfTransactions = TransactionManager.createListArrayFromJsonArray(response);
                                historyAdapter.setTransactionList(listOfTransactions);
                                result = calculateBalance();
                                setupButton();
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

    @Override
    public boolean onSupportNavigateUp() {
        // When clicking back arrow in top left, mimic behaviour of back
        onBackPressed();
        return true;
    }
}
