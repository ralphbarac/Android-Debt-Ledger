package cs4474.g9.debtledger.data.model;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class UserAccount implements Serializable {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public UserAccount(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public UserAccount(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        // TODO: Improve Hash Code...
        return firstName.hashCode() + lastName.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof UserAccount) {
            UserAccount userAccount = (UserAccount) obj;
            // TODO: Use ids eventually...
            return this.firstName.equals(userAccount.getFirstName()) && this.lastName.equals(userAccount.getLastName());
        } else {
            return false;
        }
    }
}
