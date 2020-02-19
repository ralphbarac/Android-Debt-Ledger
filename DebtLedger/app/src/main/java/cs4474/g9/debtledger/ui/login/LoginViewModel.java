package cs4474.g9.debtledger.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.LoginAuthenticator;
import cs4474.g9.debtledger.data.LoginRepository;
import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.model.LoggedInUser;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    public LoginViewModel() {
        this.loginRepository = LoginRepository.getInstance(new LoginAuthenticator());
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        // Attempt to login with given credentials
        Result<LoggedInUser> result = loginRepository.login(email, password);

        // Update value of login result, which is propagated to the view
        if (result instanceof Result.Success) {
            LoggedInUser user = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(user));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        // Update value of login form state, depending on what applies, which is propagated to the view
        if (!isEmailValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isEmailValid(String username) {
        return username != null && Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() > 5;
    }
}
