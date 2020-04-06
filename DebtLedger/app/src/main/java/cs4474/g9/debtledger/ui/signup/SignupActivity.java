package cs4474.g9.debtledger.ui.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.UserAccountManager;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.ui.MainActivity;
import cs4474.g9.debtledger.ui.login.LoginActivity;

public class SignupActivity extends AppCompatActivity {

    private SignupViewModel signupViewModel;
    private LoginRepository loginRepository;

    private View duplicateEmailError;

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
        final TextInputEditText confirmPasswordInput = findViewById(R.id.confirm_password);
        final MaterialButton switchToLoginButton = findViewById(R.id.switch_to_login);
        duplicateEmailError = findViewById(R.id.duplicate_email_error_container);
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
                if (signupFormState.getConfirmPasswordError() != null) {
                    confirmPasswordInput.setError(getString(signupFormState.getConfirmPasswordError()));
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
                        passwordInput.getText().toString(),
                        confirmPasswordInput.getText().toString()
                );
            }
        };
        firstNameInput.addTextChangedListener(afterTextChangedListener);
        lastNameInput.addTextChangedListener(afterTextChangedListener);
        emailInput.addTextChangedListener(afterTextChangedListener);
        passwordInput.addTextChangedListener(afterTextChangedListener);
        confirmPasswordInput.addTextChangedListener(afterTextChangedListener);

        // Add additional listener to remove duplicate email error when email changes
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove duplicate email error (if visible)
                duplicateEmailError.setVisibility(View.GONE);
            }
        });

        // On clicking enter in password field, attempts signup
        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(v.getWindowToken(), 0);

                    if (signupButton.isEnabled()) {
                        makeSignupRequest(
                                firstNameInput.getText().toString(),
                                lastNameInput.getText().toString(),
                                emailInput.getText().toString(),
                                passwordInput.getText().toString()
                        );
                    }
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
        // Remove duplicate email error, disable signup button, display loading bar
        duplicateEmailError.setVisibility(View.GONE);
        signupButton.setEnabled(false);
        loadingProgressBar.setVisibility(View.VISIBLE);

        JSONObject input;
        try {
            input = UserAccountManager.createJsonFromUserAccount(
                    new UserAccount(firstName, lastName, email, password)
            );
        } catch (JSONException e) {
            throw new RuntimeException();
        }

        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + UserAccountManager.SIGNUP_END_POINT + "/" + input.toString() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("SIGN-UP", response.toString());

                        try {
                            // If error, display signup failed to user...
                            if (response.getJSONObject(0).has("error")
                                    || response.getJSONObject(0).has("failure")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("duplicate")) {
                                // On duplicate email found
                                duplicateEmailError.setVisibility(View.VISIBLE);
                                signupButton.setEnabled(true);
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                            } else {
                                // On success, parse UserAccount, store in repository, and proceed to contacts
                                UserAccount loggedInUser = UserAccountManager.parseUserAccountFromJson(response.getJSONObject(0));
                                loginRepository.loginUser(loggedInUser, loggedInUser.getId());
                                proceedToContacts(loggedInUser);
                            }
                        } catch (Exception e) {
                            // On parse error or user not found, display signup failed to user
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
                        Log.d("SIGN-UP", error.toString());
                        Toast.makeText(SignupActivity.this, R.string.signup_failed, Toast.LENGTH_SHORT).show();
                        signupButton.setEnabled(true);
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

    private void proceedToContacts(UserAccount loggedInUser) {
        String welcome = getString(R.string.welcome) + loggedInUser.getFirstName();
        Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();

        Intent toContacts = new Intent(this, MainActivity.class);
        toContacts.putExtra(MainActivity.TAB, MainActivity.CONTACTS_TAB);
        startActivity(toContacts);
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel/terminate any requests that are still running or queued
        ConnectionAdapter.getInstance().cancelAllRequests(hashCode());
    }

}
