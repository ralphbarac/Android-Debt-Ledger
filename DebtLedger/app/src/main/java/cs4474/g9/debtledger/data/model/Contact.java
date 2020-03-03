package cs4474.g9.debtledger.data.model;

public class Contact {

    private long id;

    private UserAccount userA;
    private UserAccount userB;

    public Contact(UserAccount userA, UserAccount userB) {
        this.userA = userA;
        this.userB = userB;
    }

    public Contact(long id, UserAccount userA, UserAccount userB) {
        this.id = id;
        this.userA = userA;
        this.userB = userB;
    }

    public long getId() {
        return id;
    }

    public UserAccount getUserA() {
        return userA;
    }

    public UserAccount getUserB() {
        return userB;
    }
}
