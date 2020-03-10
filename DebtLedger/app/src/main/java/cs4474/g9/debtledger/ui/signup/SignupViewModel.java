package cs4474.g9.debtledger.ui.signup;

import android.util.Patterns;

import java.util.regex.Pattern;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.UserAccountManager;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;

public class SignupViewModel extends ViewModel {

    public static final Pattern NAME = Pattern.compile("^[a-zA-Z]+$");

    private MutableLiveData<SignupFormState> signupFormState = new MutableLiveData<>();
    private MutableLiveData<SignupResult> signupResult = new MutableLiveData<>();
    private UserAccountManager userAccountManager;
    private LoginRepository loginRepository;

    public SignupViewModel(LoginRepository loginRepository) {
        this.userAccountManager = new UserAccountManager();
        this.loginRepository = loginRepository;
    }

    LiveData<SignupFormState> getSignupFormState() {
        return signupFormState;
    }

    LiveData<SignupResult> getSignupResult() {
        return signupResult;
    }

    public Result<UserAccount> signup(String firstName, String lastName, String email, String password) {
        // Attempt to signup with given data
        Result<UserAccount> signupResult = userAccountManager.createAccount(
                new UserAccount(firstName, lastName, email, password)
        );

        if (signupResult instanceof Result.Success) {
            // Login to newly created account
            Result<UserAccount> loginResult = loginRepository.login(email, password);
            return loginResult;
        } else {
            return signupResult;
        }
    }

    public void signupResultChanged(Result<UserAccount> result) {
        // Update value of signup result, which is propagated to the view
        if (result instanceof Result.Success) {
            UserAccount loggedInUser = ((Result.Success<UserAccount>) result).getData();
            signupResult.setValue(new SignupResult(loggedInUser));
        } else {
            signupResult.setValue(new SignupResult(R.string.signup_failed));
        }
    }

    public void signupFormDataChanged(String firstName, String lastName, String email, String password) {
        // Update value of login form state, depending on what applies, which is propagated to the view
        Integer firstNameError, lastNameError, emailError, passwordError;
        firstNameError = isNameValid(firstName) ? null : R.string.invalid_first_name;
        lastNameError = isNameValid(lastName) ? null : R.string.invalid_last_name;
        emailError = isEmailValid(email) ? null : R.string.invalid_email;
        passwordError = isPasswordValid(password) ? null : R.string.invalid_password;

        if (firstNameError == null && lastNameError == null && emailError == null && passwordError == null) {
            signupFormState.setValue(new SignupFormState(true));
        } else {
            signupFormState.setValue(new SignupFormState(firstNameError, lastNameError, emailError, passwordError));
        }
    }

    private boolean isNameValid(String name) {
        return !name.trim().isEmpty() && NAME.matcher(name).matches();
    }

    private boolean isEmailValid(String username) {
        return username != null && Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() > 5;
    }
}