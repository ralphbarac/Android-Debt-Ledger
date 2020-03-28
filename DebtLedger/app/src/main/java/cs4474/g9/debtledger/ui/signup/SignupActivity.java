package cs4474.g9.debtledger.ui.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.UserAccountManager;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.ui.MainActivity;
import cs4474.g9.debtledger.ui.login.LoginActivity;

public class SignupActivity extends AppCompatActivity {

    private SignupViewModel signupViewModel;
    private LoginRepository loginRepository;

    private MaterialButton signupButton;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        // View model is used to manage and operate on data inputted through interface
        loginRepository = LoginRepository.getInstance();
        SignupViewModelFactory factory = new SignupViewModelFactory();
        signupViewModel = ViewModelProviders.of(this, factory).get(SignupViewModel.class);

        final TextInputEditText firstNameInput = findViewById(R.id.first_name);
        final TextInputEditText lastNameInput = findViewById(R.id.last_name);
        final TextInputEditText emailInput = findViewById(R.id.email);
        final TextInputEditText passwordInput = findViewById(R.id.password);
        final MaterialButton switchToLoginButton = findViewById(R.id.switch_to_login);
        signupButton = findViewById(R.id.signup);
        loadingProgressBar = findViewById(R.id.loading);

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
                if (signupFormState.getEmailError() != null) {
                    emailInput.setError(getString(signupFormState.getEmailError()));
                }
                if (signupFormState.getPasswordError() != null) {
                    passwordInput.setError(getString(signupFormState.getPasswordError()));
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
                    // Hide the keyboard
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(v.getWindowToken(), 0);

                    makeSignupRequest(
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
                makeSignupRequest(
                        firstNameInput.getText().toString(),
                        lastNameInput.getText().toString(),
                        emailInput.getText().toString(),
                        passwordInput.getText().toString()
                );
            }
        });
    }

    private void makeSignupRequest(String firstName, String lastName, String email, String password) {
        signupButton.setEnabled(false);
        loadingProgressBar.setVisibility(View.VISIBLE);

        JSONObject requestBody;
        try {
            requestBody = UserAccountManager.createJsonFromUserAccount(
                    new UserAccount(firstName, lastName, email, password)
            );
        } catch (JSONException e) {
            throw new RuntimeException();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                ConnectionAdapter.BASE_URL + UserAccountManager.SIGNUP_END_POINT,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // On success, parse UserAccount, store in repository, and process to dashboard
                            UserAccount loggedInUser = UserAccountManager.parseUserAccountFromJson(response);
                            loginRepository.loginUser(loggedInUser, loggedInUser.getId());
                            proceedToDashboard(loggedInUser);
                        } catch (JSONException e) {
                            // On parse error, display signup failed to user
                            Toast.makeText(SignupActivity.this, R.string.signup_failed, Toast.LENGTH_SHORT).show();
                            signupButton.setEnabled(true);
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display signup failed to user
                        Toast.makeText(SignupActivity.this, R.string.signup_failed, Toast.LENGTH_SHORT).show();
                        signupButton.setEnabled(true);
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );

        // TODO: Remove, temporary to allow login
        if (email.equals("zsirohey@uwo.ca") && password.equals("zain1234")) {
            UserAccount loggedInUser = new UserAccount(
                    "1000",
                    "Zain",
                    "Sirohey",
                    "zsirohey@uwo.ca",
                    "zain1234"
            );
            loginRepository.loginUser(loggedInUser, loggedInUser.getId());
            proceedToDashboard(loggedInUser);
        } else {
            Toast.makeText(SignupActivity.this, R.string.signup_failed, Toast.LENGTH_SHORT).show();
            signupButton.setEnabled(true);
            loadingProgressBar.setVisibility(View.INVISIBLE);
        }

//        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

    private void proceedToDashboard(UserAccount loggedInUser) {
        String welcome = getString(R.string.welcome) + loggedInUser.getFirstName();
        Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();

        Intent toDashboard = new Intent(this, MainActivity.class);
        startActivity(toDashboard);
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionAdapter.getInstance().cancelAllRequests(hashCode());
    }

}
