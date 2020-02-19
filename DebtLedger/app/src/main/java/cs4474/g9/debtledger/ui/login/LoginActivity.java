package cs4474.g9.debtledger.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import cs4474.g9.debtledger.data.LoginAuthenticator;
import cs4474.g9.debtledger.data.LoginRepository;
import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.model.LoggedInUser;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private LoginRepository loginRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user has already logged in, if so, proceed to dashboard
        loginRepository = LoginRepository.getInstance(new LoginAuthenticator());
        if (loginRepository.isUserLoggedIn()) {
            // Authenticate login
            Result<LoggedInUser> authenticationResult = loginRepository.authenticateLoggedInUser();
            if (authenticationResult instanceof Result.Success) {
                LoggedInUser user = (LoggedInUser) ((Result.Success) authenticationResult).getData();
                proceedToDashboard(user);
            }
        }

        setContentView(R.layout.activity_login);

        // View model is used to manage and operate on data inputted through interface
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        final TextInputEditText emailInput = findViewById(R.id.email);
        final TextInputEditText passwordInput = findViewById(R.id.password);
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
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    informUserOfFailedLogin(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    proceedToDashboard(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
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
                    loginViewModel.login(emailInput.getText().toString(),
                            passwordInput.getText().toString());
                }
                return false;
            }
        });

        // On clicking login button, attempts login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(emailInput.getText().toString(),
                        passwordInput.getText().toString());
            }
        });
    }

    private void proceedToDashboard(LoggedInUser user) {
        String welcome = getString(R.string.welcome) + user.getFirstName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        // TODO: Re-direct to Dashboard
    }

    private void informUserOfFailedLogin(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
