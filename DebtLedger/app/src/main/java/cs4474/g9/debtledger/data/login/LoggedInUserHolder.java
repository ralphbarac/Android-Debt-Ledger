package cs4474.g9.debtledger.data.login;

import cs4474.g9.debtledger.data.model.UserAccount;

/**
 * Data class to store information about currently logged in user
 */
class LoggedInUserHolder {

    private String token;
    private UserAccount account;

    LoggedInUserHolder(String token, UserAccount account) {
        this.token = token;
        this.account = account;
    }

    String getToken() {
        return token;
    }

    UserAccount getAccount() {
        return account;
    }

}
