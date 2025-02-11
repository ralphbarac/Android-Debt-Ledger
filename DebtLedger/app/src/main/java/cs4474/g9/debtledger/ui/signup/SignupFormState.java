package cs4474.g9.debtledger.ui.signup;

import androidx.annotation.Nullable;

/**
 * Data validation of the signup form, to be used with LiveData and Observer
 */
public class SignupFormState {

    @Nullable
    private Integer firstNameError;
    @Nullable
    private Integer lastNameError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer confirmPasswordError;

    private boolean isDataValid;

    SignupFormState(@Nullable Integer firstNameError, @Nullable Integer lastNameError, @Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer confirmPasswordError) {
        this.firstNameError = firstNameError;
        this.lastNameError = lastNameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.confirmPasswordError = confirmPasswordError;
        this.isDataValid = false;
    }

    SignupFormState(boolean isDataValid) {
        this.firstNameError = null;
        this.lastNameError = null;
        this.emailError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getFirstNameError() {
        return firstNameError;
    }

    @Nullable
    Integer getLastNameError() {
        return lastNameError;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getConfirmPasswordError() {
        return confirmPasswordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }

}
