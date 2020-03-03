package cs4474.g9.debtledger.ui.login;

import androidx.annotation.Nullable;
import cs4474.g9.debtledger.data.model.UserAccount;

/**
 * Result of login, to be used with LiveData and Observer
 */
class LoginResult {

    @Nullable
    private UserAccount success;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable UserAccount success) {
        this.success = success;
    }

    @Nullable
    UserAccount getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
