package cs4474.g9.debtledger.ui.signup;

import android.util.Patterns;

import java.util.regex.Pattern;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cs4474.g9.debtledger.R;

public class SignupViewModel extends ViewModel {

    public static final Pattern NAME = Pattern.compile("^[a-zA-Z]+$");

    private MutableLiveData<SignupFormState> signupFormState = new MutableLiveData<>();

    LiveData<SignupFormState> getSignupFormState() {
        return signupFormState;
    }

    public void signupFormDataChanged(String firstName, String lastName, String email, String password, String confirmPassword) {
        // Update value of login form state, depending on what applies, which is propagated to the view
        Integer firstNameError, lastNameError, emailError, passwordError, confirmPasswordError;
        firstNameError = isNameValid(firstName) ? null : R.string.invalid_first_name;
        lastNameError = isNameValid(lastName) ? null : R.string.invalid_last_name;
        emailError = isEmailValid(email) ? null : R.string.invalid_email;
        passwordError = isPasswordValid(password) ? null : R.string.invalid_password;
        confirmPasswordError = password.equals(confirmPassword) ? null : R.string.invalid_confirm_password;

        if (firstNameError == null && lastNameError == null && emailError == null && passwordError == null && confirmPasswordError == null) {
            signupFormState.setValue(new SignupFormState(true));
        } else {
            signupFormState.setValue(new SignupFormState(firstNameError, lastNameError, emailError, passwordError, confirmPasswordError));
        }
    }

    private boolean isNameValid(String name) {
        return !name.trim().isEmpty() && NAME.matcher(name).matches();
    }

    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() > 5;
    }
}