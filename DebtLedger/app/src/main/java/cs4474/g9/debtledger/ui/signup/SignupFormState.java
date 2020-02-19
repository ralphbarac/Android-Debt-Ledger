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
    private Integer usernameError;
    @Nullable
    private Integer passwordError;

    private boolean isDataValid;

    SignupFormState(@Nullable Integer firstNameError, @Nullable Integer lastNameError, @Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.firstNameError = firstNameError;
        this.lastNameError = lastNameError;
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    SignupFormState(boolean isDataValid) {
        this.firstNameError = null;
        this.lastNameError = null;
        this.usernameError = null;
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
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }

}
