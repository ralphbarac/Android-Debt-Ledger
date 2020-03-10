package cs4474.g9.debtledger.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.ui.MainActivity;
import cs4474.g9.debtledger.ui.signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private LoginRepository loginRepository;

    private AsyncTask<String, Void, Result> loginProcess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user has already logged in, if so, proceed to dashboard
        loginRepository = LoginRepository.getInstance(this);
        if (loginRepository.isUserLoggedInAndAuthenticated()) {
            proceedToDashboard(loginRepository.getLoggedInUser());
        }

        setContentView(R.layout.activity_login);

        // View model is used to manage and operate on data inputted through interface
        LoginViewModelFactory factory = new LoginViewModelFactory(loginRepository);
        loginViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel.class);

        final TextInputEditText emailInput = findViewById(R.id.email);
        final TextInputEditText passwordInput = findViewById(R.id.password);
        final MaterialButton switchToSignupButton = findViewById(R.id.switch_to_signup);
        final MaterialButton loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        // Listen for any changes to the login form state, which will display errors and/or enable/disable button
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());

                // If applicable, indicate appropriate error next to input field
                if (loginFormState.getUsernameError() != null) {
                    emailInput.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordInput.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        // Listen for any changes to login result, at which point, user is informed of outcome
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.INVISIBLE);
                if (loginResult.getError() != null) {
                    informUserOfFailedLogin(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    proceedToDashboard(loginResult.getSuccess());
                    // TODO: If stay logged in...
                    loginRepository.storeToken(LoginActivity.this);
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

                    loadingProgressBar.setVisibility(View.VISIBLE);
                    if (loginProcess == null || loginProcess.getStatus() == AsyncTask.Status.FINISHED) {
                        loginProcess = new LoginProcess();
                        loginProcess.execute(emailInput.getText().toString(), passwordInput.getText().toString());
                    }
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
                loadingProgressBar.setVisibility(View.VISIBLE);
                if (loginProcess == null || loginProcess.getStatus() == AsyncTask.Status.FINISHED) {
                    loginProcess = new LoginProcess();
                    loginProcess.execute(emailInput.getText().toString(), passwordInput.getText().toString());
                }
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

    private void informUserOfFailedLogin(@StringRes Integer errorString) {
        Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loginProcess != null) {
            loginProcess.cancel(true);
        }
    }

    private final class LoginProcess extends AsyncTask<String, Void, Result> {

        @Override
        protected Result doInBackground(String... params) {
            Result<UserAccount> result;
            if (params.length == 2) {
                String email = params[0];
                String password = params[1];
                result = loginViewModel.login(email, password);
            } else {
                result = new Result.Error(new IllegalArgumentException());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            loginViewModel.loginResultChanged(result);
        }

    }
}
