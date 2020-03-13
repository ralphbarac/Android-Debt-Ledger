package cs4474.g9.debtledger.ui.transaction;

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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.Group;
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
        whoOwesInputAdapter = new InputAmountAdapter(whoOwes, LoginRepository.getInstance(this).getLoggedInUser());
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
                int amountPaid = TransactionViewModel.isAmountValid(CreateTransactionActivity.this.amountPaid)
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
        if (requestCode == SELECT_WHO_IS_PAYING) {
            if (resultCode == RESULT_OK) {
                Serializable value = data.getSerializableExtra(SelectWhoIsPayingActivity.SELECTED_CONTACT);
                if (value instanceof UserAccount) {
                    whoIsPaying = (UserAccount) value;
                    updateWhoIsPayingSection();
                }
            }
        } else if (requestCode == SELECT_WHO_OWES) {
            if (resultCode == RESULT_OK) {
                Serializable value = data.getSerializableExtra(SelectWhoOwesActivity.SELECTED_CONTACTS);
                if (value instanceof WhoOwesWrapper) {
                    WhoOwesWrapper wrapper = (WhoOwesWrapper) value;
                    selectedSelf = wrapper.isSelectedSelf();
                    selectedGroups = wrapper.getSelectedGroups();
                    selectedContacts = wrapper.getSelectedContacts();

                    whoOwes = new ArrayList<>();
                    if (selectedSelf) {
                        whoOwes.add(LoginRepository.getInstance(this).getLoggedInUser());
                    }
                    whoOwes.addAll(selectedContacts);
                    updateWhoOwesSection();
                }
            }
        }
    }

    private void updateWhoIsPayingSection() {
        if (whoIsPaying != null) {
            whoIsPayingInputContainer.setVisibility(View.VISIBLE);
            whoIsPayingAvatar.setColorFilter(ColourGenerator.generateFromName(whoIsPaying.getFirstName(), whoIsPaying.getLastName()));
            whoIsPayingAvatarCharacter.setText(whoIsPaying.getFirstName().substring(0, 1));
            if (whoIsPaying.equals(LoginRepository.getInstance(this).getLoggedInUser())) {
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
        UserAccount loggedInUser = LoginRepository.getInstance(this).getLoggedInUser();
        if (!whoIsPaying.equals(loggedInUser) && !whoOwes.contains(loggedInUser)) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("You Aren't Involved!")
                    .setMessage("In order to add debt, you must either be paying or owe!")
                    .setPositiveButton("Ok", null)
                    .show();
        } else {
            // TODO: Submit Transaction
        }
    }

}
