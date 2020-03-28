package cs4474.g9.debtledger.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cs4474.g9.debtledger.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public void loginDataChanged(String email, String password) {
        // Update value of login form state, depending on what applies, which is propagated to the view
        if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() > 5;
    }
}
