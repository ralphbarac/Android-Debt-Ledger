package cs4474.g9.debtledger.data.model;

/**
 * Data class to store information about currently logged in user
 */
public class LoggedInUser {

    private String token;
    private UserAccount account;

    public LoggedInUser(String token, UserAccount account) {
        this.token = token;
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public UserAccount getAccount() {
        return account;
    }

    public String getFirstName() {
        return account.getFirstName();
    }

    public String getLastName() {
        return account.getLastName();
    }
}
