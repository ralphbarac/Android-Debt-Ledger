package cs4474.g9.debtledger.data.model;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class UserAccount implements Serializable {

    private long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // TODO: Add relationships on UserAccount

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

    public long getId() {
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
