package cs4474.g9.debtledger.ui.login;

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
import cs4474.g9.debtledger.ui.signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private LoginRepository loginRepository;

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private ProgressBar loadingProgressBar;

    private View invalidEmailPasswordError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize connection adapter and login repository
        ConnectionAdapter.initialize(this);
        LoginRepository.initialize(this);

        setContentView(R.layout.activity_login);

        // View model is used to manage and operate on data inputted through interface
        LoginViewModelFactory factory = new LoginViewModelFactory();
        loginViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel.class);

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        final MaterialButton switchToSignupButton = findViewById(R.id.switch_to_signup);
        invalidEmailPasswordError = findViewById(R.id.invalid_email_password_error_container);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        // Listen for any changes to the login form state, which will display errors and/or enable/disable button
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());

                // If applicable, indicate appropriate error next to input field
                if (loginFormState.getEmailError() != null) {
                    emailInput.setError(getString(loginFormState.getEmailError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordInput.setError(getString(loginFormState.getPasswordError()));
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
                // Remove invalid email or password error (if visible)
                invalidEmailPasswordError.setVisibility(View.GONE);
                loginViewModel.loginDataChanged(emailInput.getText().toString(),
                        passwordInput.getText().toString());
            }
        };
        emailInput.addTextChangedListener(afterTextChangedListener);
        passwordInput.addTextChangedListener(afterTextChangedListener);

        // On clicking enter in password field, attempts login
        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(v.getWindowToken(), 0);

                    makeLoginRequest(emailInput.getText().toString(), passwordInput.getText().toString());
                }
                return false;
            }
        });

        // On clicking switch to signup button, redirect to signup page
        switchToSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSignupPage = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(toSignupPage);
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        // On clicking login button, attempts login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeLoginRequest(emailInput.getText().toString(), passwordInput.getText().toString());
            }
        });

        // Check if user has already logged in, if so, proceed to dashboard
        loginRepository = LoginRepository.getInstance();
        if (loginRepository.hasToken()) {
            makeLoginRequestWithToken(loginRepository.getToken());
        }
    }

    private void makeLoginRequestWithToken(long token) {
        // Disable email and password input, display loading bar
        emailInput.setEnabled(false);
        passwordInput.setEnabled(false);
        loadingProgressBar.setVisibility(View.VISIBLE);

        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + UserAccountManager.LOGIN_WITH_TOKEN_END_POINT + "/" + token + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("LOGIN", response.toString());

                        try {
                            // If error or user missing, reset form and do nothing...
                            if (response.getJSONObject(0).has("error") ||
                                    response.getJSONObject(0).has("missing")) {
                                throw new Exception();
                            } else {
                                // On success, parse UserAccount, store in repository, and process to dashboard
                                UserAccount loggedInUser = UserAccountManager.parseUserAccountFromJson(response.getJSONObject(0));
                                loginRepository.loginUser(loggedInUser, loggedInUser.getId());
                                proceedToDashboard(loggedInUser);
                            }
                        } catch (Exception e) {
                            // Reset form and do nothing...
                            emailInput.setEnabled(true);
                            passwordInput.setEnabled(true);
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Reset form and do nothing...
                        Log.d("LOGIN", error.toString());
                        emailInput.setEnabled(true);
                        passwordInput.setEnabled(true);
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

    private void makeLoginRequest(String email, String password) {
        // Remove invalid email/password error, disable login button, display loading bar
        invalidEmailPasswordError.setVisibility(View.GONE);
        loginButton.setEnabled(false);
        loadingProgressBar.setVisibility(View.VISIBLE);

        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + UserAccountManager.LOGIN_END_POINT + "/" + email + "/" + password + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("LOGIN", response.toString());

                        try {
                            // If user not found, display login failed to user...
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("missing")) {
                                invalidEmailPasswordError.setVisibility(View.VISIBLE);
                                loginButton.setEnabled(true);
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                            } else {
                                // On success, parse UserAccount, store in repository, and process to dashboard
                                UserAccount loggedInUser = UserAccountManager.parseUserAccountFromJson(response.getJSONObject(0));
                                loginRepository.loginUser(loggedInUser, loggedInUser.getId());
                                proceedToDashboard(loggedInUser);
                            }
                        } catch (Exception e) {
                            // On parse error or user not found, display login failed to user
                            Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                            loginButton.setEnabled(true);
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display login failed to user
                        Log.d("LOGIN", error.toString());
                        Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        loginButton.setEnabled(true);
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
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

        // Cancel/terminate any requests that are still running or queued
        ConnectionAdapter.getInstance().cancelAllRequests(hashCode());
    }
}
