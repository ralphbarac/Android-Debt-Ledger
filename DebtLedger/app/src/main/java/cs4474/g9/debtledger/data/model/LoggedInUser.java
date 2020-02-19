package cs4474.g9.debtledger.data.model;

/**
 * Data class to store information about currently logged in user
 */
public class LoggedInUser {

    private String token;
    private String firstName;
    private String lastName;

    public LoggedInUser(String token, String firstName, String lastName) {
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getToken() {
        return token;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
