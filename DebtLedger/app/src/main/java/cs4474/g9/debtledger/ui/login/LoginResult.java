package cs4474.g9.debtledger.ui.login;

import androidx.annotation.Nullable;
import cs4474.g9.debtledger.data.model.LoggedInUser;

/**
 * Result of login, to be used with LiveData and Observer
 */
class LoginResult {

    @Nullable
    private LoggedInUser success;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable LoggedInUser success) {
        this.success = success;
    }

    @Nullable
    LoggedInUser getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
