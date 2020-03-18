package cs4474.g9.debtledger.data;

public class User
{
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public User(String first, String last, String email, String pass)
    {
        firstName = first;
        lastName = last;
        this.email = email;
        password = pass;
    }

    public String getID()
    {
        return id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String name)
    {
        firstName = name;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String name)
    {
        lastName = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPassword(String pass)
    {
        password = pass;
    }
}
