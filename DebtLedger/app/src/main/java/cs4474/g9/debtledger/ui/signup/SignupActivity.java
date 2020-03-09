package cs4474.g9.debtledger.ui.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.ui.MainActivity;
import cs4474.g9.debtledger.ui.login.LoginActivity;

public class SignupActivity extends AppCompatActivity {

    private SignupViewModel signupViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        // View model is used to manage and operate on data inputted through interface
        final LoginRepository loginRepository = LoginRepository.getInstance(this);
        SignupViewModelFactory factory = new SignupViewModelFactory(loginRepository);
        signupViewModel = ViewModelProviders.of(this, factory).get(SignupViewModel.class);

        final TextInputEditText firstNameInput = findViewById(R.id.first_name);
        final TextInputEditText lastNameInput = findViewById(R.id.last_name);
        final TextInputEditText emailInput = findViewById(R.id.email);
        final TextInputEditText passwordInput = findViewById(R.id.password);
        final MaterialButton switchToLoginButton = findViewById(R.id.switch_to_login);
        final MaterialButton signupButton = findViewById(R.id.signup);

        // Listen for any changes to the signup form state, which will display errors and/or enable/disable button
        signupViewModel.getSignupFormState().observe(this, new Observer<SignupFormState>() {
            @Override
            public void onChanged(@Nullable SignupFormState signupFormState) {
                if (signupFormState == null) {
                    return;
                }
                signupButton.setEnabled(signupFormState.isDataValid());

                // If applicable, indicate appropriate error next to input field
                if (signupFormState.getFirstNameError() != null) {
                    firstNameInput.setError(getString(signupFormState.getFirstNameError()));
                }
                if (signupFormState.getLastNameError() != null) {
                    lastNameInput.setError(getString(signupFormState.getLastNameError()));
                }
                if (signupFormState.getUsernameError() != null) {
                    emailInput.setError(getString(signupFormState.getUsernameError()));
                }
                if (signupFormState.getPasswordError() != null) {
                    passwordInput.setError(getString(signupFormState.getPasswordError()));
                }
            }
        });

        // Listen for any changes to signup result, at which point, user is informed of outcome
        signupViewModel.getSignupResult().observe(this, new Observer<SignupResult>() {
            @Override
            public void onChanged(@Nullable SignupResult signupResult) {
                if (signupResult == null) {
                    return;
                }
                if (signupResult.getError() != null) {
                    informUserOfFailedSignup(signupResult.getError());
                }
                if (signupResult.getSuccess() != null) {
                    proceedToDashboard(signupResult.getSuccess());
                    // TODO: If stay logged in...
                    loginRepository.storeToken(SignupActivity.this);
                }
            }
        });

        // "Watch" for changes in text (in assigned text boxes below), and update view model appropriately
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                signupViewModel.signupFormDataChanged(
                        firstNameInput.getText().toString(),
                        lastNameInput.getText().toString(),
                        emailInput.getText().toString(),
                        passwordInput.getText().toString()
                );
            }
        };
        firstNameInput.addTextChangedListener(afterTextChangedListener);
        lastNameInput.addTextChangedListener(afterTextChangedListener);
        emailInput.addTextChangedListener(afterTextChangedListener);
        passwordInput.addTextChangedListener(afterTextChangedListener);

        // On clicking enter in password field, attempts signup
        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signupViewModel.signup(
                            firstNameInput.getText().toString(),
                            lastNameInput.getText().toString(),
                            emailInput.getText().toString(),
                            passwordInput.getText().toString()
                    );
                }
                return false;
            }
        });

        // On clicking switch to login button, redirect to login page
        switchToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLoginPage = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(toLoginPage);
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        // On clicking signup button, attempts signup
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupViewModel.signup(
                        firstNameInput.getText().toString(),
                        lastNameInput.getText().toString(),
                        emailInput.getText().toString(),
                        passwordInput.getText().toString()
                );
            }
        });
    }

    private void proceedToDashboard(UserAccount loggedInUser) {
        String welcome = getString(R.string.welcome) + loggedInUser.getFirstName();
        Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();

        Intent toDashboard = new Intent(this, MainActivity.class);
        startActivity(toDashboard);
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void informUserOfFailedSignup(@StringRes Integer errorString) {
        Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
    }
}
