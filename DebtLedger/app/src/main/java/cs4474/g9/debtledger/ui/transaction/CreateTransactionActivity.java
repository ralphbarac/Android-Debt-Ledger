package cs4474.g9.debtledger.ui.transaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.ContactManager;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.Transaction;
import cs4474.g9.debtledger.data.model.TransactionManager;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.contacts.select.SelectWhoIsPayingActivity;
import cs4474.g9.debtledger.ui.contacts.select.SelectWhoOwesActivity;

public class CreateTransactionActivity extends AppCompatActivity implements OnInputAmountsChanged {

    final int SELECT_WHO_IS_PAYING = 0;
    final int SELECT_WHO_OWES = 1;

    private TransactionViewModel viewModel;
    private boolean isFormSubmittable = false;

    private UserAccount whoIsPaying;
    private String amountPaid = "";

    private List<UserAccount> whoOwes;
    private boolean selectedSelf = false;
    private List<Group> selectedGroups = new ArrayList<>();
    private List<UserAccount> selectedContacts = new ArrayList<>();
    private List<String> amountsOwed = new ArrayList<>();

    private TextInputEditText descriptionInput;
    private TextView amountTitle;

    private View whoIsPayingInputContainer;
    private ImageView whoIsPayingAvatar;
    private TextView whoIsPayingAvatarCharacter;
    private TextView whoIsPayingName;
    private TextInputEditText whoIsPayingAmountInput;
    private ImageView whoIsPayingAddEditIcon;
    private TextView whoIsPayingActionTitle;

    private RecyclerView whoOwesInputView;
    private InputAmountAdapter whoOwesInputAdapter;
    private ImageView whoOwesAddEditIcon;
    private TextView whoOwesActionTitle;
    private MaterialButton splitButton;

    private MaterialButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        descriptionInput = findViewById(R.id.description);
        amountTitle = findViewById(R.id.amount_title);

        // Who is paying view references
        whoIsPayingInputContainer = findViewById(R.id.who_is_paying_input_container);
        whoIsPayingAvatar = findViewById(R.id.who_is_paying_avatar);
        whoIsPayingAvatarCharacter = findViewById(R.id.who_is_paying_avatar_character);
        whoIsPayingName = findViewById(R.id.who_is_paying_name);
        whoIsPayingAddEditIcon = findViewById(R.id.who_is_paying_add_edit_icon);
        whoIsPayingActionTitle = findViewById(R.id.who_is_paying_action_title);

        // Initializing who owes input recycler view
        whoOwesInputView = findViewById(R.id.who_owes_input_list);
        whoOwesInputView.setHasFixedSize(true);
        whoOwesInputView.setLayoutManager(new LinearLayoutManager(this));

        // Initializing who owes input adapter
        whoOwesInputAdapter = new InputAmountAdapter(whoOwes, LoginRepository.getInstance().getLoggedInUser());
        whoOwesInputAdapter.addOnInputChangedListener(this);
        whoOwesInputView.setAdapter(whoOwesInputAdapter);

        // Who owes view references
        whoOwesAddEditIcon = findViewById(R.id.who_owes_add_edit_icon);
        whoOwesActionTitle = findViewById(R.id.who_owes_action_title);
        splitButton = findViewById(R.id.split);
        splitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CREATE-TRANSACTION", "Split button clicked.");
                long amountPaid = TransactionViewModel.isAmountValid(CreateTransactionActivity.this.amountPaid)
                        ? TransactionViewModel.parseAmountValue(CreateTransactionActivity.this.amountPaid)
                        : 0;
                whoOwesInputAdapter.splitAmount(amountPaid);
            }
        });

        submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CREATE-TRANSACTION", "Submit button clicked.");
                submitTransaction();
            }
        });

        // Who is paying input listener
        whoIsPayingAmountInput = findViewById(R.id.who_is_paying_amount_input);
        whoIsPayingAmountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                amountPaid = s.toString();
                if (!TransactionViewModel.isAmountValid(amountPaid)) {
                    whoIsPayingAmountInput.setError(getString(R.string.invalid_amount));
                }
                viewModel.transactionDataChanged(whoIsPaying, amountPaid, whoOwes, amountsOwed);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        viewModel.getTransactionFormState().observe(this, new Observer<TransactionFormState>() {
            @Override
            public void onChanged(TransactionFormState transactionFormState) {
                if (transactionFormState == null) {
                    return;
                }
                boolean shouldFormBeSubmittable = transactionFormState.isDataValid();

                // Enable submit button and submit menu icon
                submitButton.setEnabled(shouldFormBeSubmittable);
                if (isFormSubmittable != shouldFormBeSubmittable) {
                    isFormSubmittable = shouldFormBeSubmittable;
                    invalidateOptionsMenu();
                }

                View miscErrorContainer = findViewById(R.id.misc_error_container);
                if (transactionFormState.getMiscError() != null) {
                    if (miscErrorContainer.getVisibility() == View.GONE) {
                        miscErrorContainer.setVisibility(View.VISIBLE);
                    }
                    TextView miscError = findViewById(R.id.misc_error);
                    miscError.setText(getString(transactionFormState.getMiscError()));
                } else {
                    if (miscErrorContainer.getVisibility() == View.VISIBLE) {
                        miscErrorContainer.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        // When clicking X in top left, mimic behaviour of back
        onBackPressed();
        return true;
    }

    public void selectWhoIsPaying(View view) {
        Intent toSelectWhoIsPaying = new Intent(this, SelectWhoIsPayingActivity.class);
        startActivityForResult(toSelectWhoIsPaying, SELECT_WHO_IS_PAYING);
    }

    public void selectWhoOwes(View view) {
        Intent toSelectWhoOwes = new Intent(this, SelectWhoOwesActivity.class);
        WhoOwesWrapper wrapper = new WhoOwesWrapper(selectedSelf, selectedGroups, selectedContacts);
        toSelectWhoOwes.putExtra(SelectWhoOwesActivity.SELECTED_CONTACTS, wrapper);
        startActivityForResult(toSelectWhoOwes, SELECT_WHO_OWES);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_transaction, menu);

        MenuItem item = menu.getItem(0);
        if (isFormSubmittable) {
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
                submitTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_WHO_IS_PAYING) {
            if (resultCode == RESULT_OK && data != null) {
                Serializable value = data.getSerializableExtra(SelectWhoIsPayingActivity.SELECTED_CONTACT);
                if (value instanceof UserAccount) {
                    whoIsPaying = (UserAccount) value;
                    updateWhoIsPayingSection();
                }
            } else if (resultCode == RESULT_CANCELED && data != null) {
                // If request from select who is paying to add new contact, direct back to add new contact
                if (data.getBooleanExtra(SelectWhoIsPayingActivity.ADD_NEW_CONTACT, false)) {
                    setResult(RESULT_CANCELED, data);
                    finish();
                }
            }
        } else if (requestCode == SELECT_WHO_OWES) {
            if (resultCode == RESULT_OK && data != null) {
                Serializable value = data.getSerializableExtra(SelectWhoOwesActivity.SELECTED_CONTACTS);
                if (value instanceof WhoOwesWrapper) {
                    WhoOwesWrapper wrapper = (WhoOwesWrapper) value;
                    selectedSelf = wrapper.isSelectedSelf();
                    selectedGroups = wrapper.getSelectedGroups();
                    selectedContacts = wrapper.getSelectedContacts();

                    whoOwes = new ArrayList<>();
                    if (selectedSelf) {
                        whoOwes.add(LoginRepository.getInstance().getLoggedInUser());
                    }
                    whoOwes.addAll(selectedContacts);
                    updateWhoOwesSection();
                }
            } else if (resultCode == RESULT_CANCELED && data != null) {
                if (data.getBooleanExtra(SelectWhoOwesActivity.ADD_NEW_GROUP, false) ||
                        data.getBooleanExtra(SelectWhoOwesActivity.ADD_NEW_CONTACT, false)) {
                    setResult(RESULT_CANCELED, data);
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel/terminate any requests that are still running or queued
        ConnectionAdapter.getInstance().cancelAllRequests(hashCode());
    }

    private void updateWhoIsPayingSection() {
        if (whoIsPaying != null) {
            whoIsPayingInputContainer.setVisibility(View.VISIBLE);
            whoIsPayingAvatar.setColorFilter(ColourGenerator.generateFromName(whoIsPaying.getFirstName(), whoIsPaying.getLastName()));
            whoIsPayingAvatarCharacter.setText(whoIsPaying.getFirstName().substring(0, 1));
            if (whoIsPaying.equals(LoginRepository.getInstance().getLoggedInUser())) {
                whoIsPayingName.setText(getString(R.string.self_identifier));
            } else {
                whoIsPayingName.setText(whoIsPaying.getFirstName() + " " + whoIsPaying.getLastName());
            }

            whoIsPayingAddEditIcon.setImageResource(R.drawable.ic_edit_black_24dp);
            whoIsPayingActionTitle.setVisibility(View.INVISIBLE);
        } else {
            whoIsPayingInputContainer.setVisibility(View.GONE);

            whoIsPayingAddEditIcon.setImageResource(R.drawable.ic_add_black_24dp);
            whoIsPayingActionTitle.setVisibility(View.VISIBLE);
        }

        if (whoIsPaying != null || (whoOwes != null && !whoOwes.isEmpty())) {
            amountTitle.setVisibility(View.VISIBLE);
        } else {
            amountTitle.setVisibility(View.INVISIBLE);
        }
    }

    private void updateWhoOwesSection() {
        if (whoOwes != null && !whoOwes.isEmpty()) {
            whoOwesInputView.setVisibility(View.VISIBLE);
            whoOwesInputAdapter.updateContacts(whoOwes);

            whoOwesAddEditIcon.setImageResource(R.drawable.ic_edit_black_24dp);
            whoOwesActionTitle.setVisibility(View.INVISIBLE);
            splitButton.setVisibility(View.VISIBLE);
        } else {
            whoOwesInputView.setVisibility(View.GONE);

            whoOwesAddEditIcon.setImageResource(R.drawable.ic_add_black_24dp);
            whoOwesActionTitle.setVisibility(View.VISIBLE);
            splitButton.setVisibility(View.INVISIBLE);
        }

        if (whoIsPaying != null || (whoOwes != null && !whoOwes.isEmpty())) {
            amountTitle.setVisibility(View.VISIBLE);
        } else {
            amountTitle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAmountChanged(List<String> amounts) {
        amountsOwed = new ArrayList<>(amounts);
        viewModel.transactionDataChanged(whoIsPaying, amountPaid, whoOwes, amountsOwed);
    }

    private void submitTransaction() {
        UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();
        if (!whoIsPaying.equals(loggedInUser) && !whoOwes.contains(loggedInUser)) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("You Aren't Involved!")
                    .setMessage("In order to add debt, you must either be paying or owe!")
                    .setPositiveButton("Ok", null)
                    .show();
        } else {
            // TODO: Ensure creditor has each debtor as contacts
            List<Transaction> transactions = new ArrayList<>();
            UserAccount creditor = whoIsPaying;
            for (int i = 0; i < whoOwes.size(); i++) {
                UserAccount debtor = whoOwes.get(i);
                if (!creditor.equals(debtor)) {
                    transactions.add(
                            new Transaction(
                                    debtor.getId(),
                                    creditor.getId(),
                                    descriptionInput.getText().toString(),
                                    new BigDecimal(amountsOwed.get(i))
                            )
                    );
                }
            }

            // If logged in user is paying, all people who owes must be their contacts, otherwise must validate
            if (whoIsPaying.equals(loggedInUser)) {
                confirmTransaction(transactions);
            } else {
                validateTransaction(transactions);
            }
        }
    }

    private void validateTransaction(List<Transaction> transactions) {
        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + ContactManager.LIST_END_POINT + "/" + whoIsPaying.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("TRANSACTIONS", response.toString());

                        List<UserAccount> contactsOfCreditor;
                        try {
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("empty")) {
                                contactsOfCreditor = new ArrayList<>();
                            } else {
                                // On success
                                contactsOfCreditor = ContactManager.parseContactsFromJson(response);
                            }

                            if (contactsOfCreditor.containsAll(whoOwes)) {
                                confirmTransaction(transactions);
                            } else {
                                new MaterialAlertDialogBuilder(CreateTransactionActivity.this)
                                        .setTitle("Missing Contacts!")
                                        .setMessage(whoIsPaying.getFirstName() + " " + whoIsPaying.getLastName() + " is not a contact with everyone who owes!")
                                        .setPositiveButton("Ok", null)
                                        .show();
                            }
                        } catch (Exception e) {
                            // On parse error, display add transactions failed to user
                            Toast.makeText(CreateTransactionActivity.this, R.string.failure_add_transactions, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display add transactions failed to user
                        Log.d("TRANSACTIONS", error.toString());
                        Toast.makeText(CreateTransactionActivity.this, R.string.failure_add_transactions, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

    private void confirmTransaction(List<Transaction> transactions) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Transaction")
                .setMessage("Are you sure you want to add the debt?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makeAddTransactionsRequest(transactions);
                    }
                })
                .setNegativeButton("No", null)
                .show();
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
                                setResult(RESULT_OK);
                                finish();
                            }
                        } catch (Exception e) {
                            // On parse error, display add transactions failed to user
                            Toast.makeText(CreateTransactionActivity.this, R.string.failure_add_transactions, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display add transactions failed to user
                        Log.d("TRANSACTIONS", error.toString());
                        Toast.makeText(CreateTransactionActivity.this, R.string.failure_add_transactions, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

}
