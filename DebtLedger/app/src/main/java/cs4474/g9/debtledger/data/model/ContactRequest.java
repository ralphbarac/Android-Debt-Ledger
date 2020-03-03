package cs4474.g9.debtledger.data.model;

public class ContactRequest {

    private long id;

    private UserAccount requestFor;
    private UserAccount requestBy;

    public ContactRequest(UserAccount requestFor, UserAccount requestBy) {
        this.requestFor = requestFor;
        this.requestBy = requestBy;
    }

    public ContactRequest(long id, UserAccount requestFor, UserAccount requestBy) {
        this.id = id;
        this.requestFor = requestFor;
        this.requestBy = requestBy;
    }

    public long getId() {
        return id;
    }

    public UserAccount getRequestFor() {
        return requestFor;
    }

    public UserAccount getRequestBy() {
        return requestBy;
    }
}
