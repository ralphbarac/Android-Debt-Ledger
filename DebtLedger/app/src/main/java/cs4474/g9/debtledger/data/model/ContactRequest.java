package cs4474.g9.debtledger.data.model;

public class ContactRequest {

    private long id;

    private UserAccount requester;
    private UserAccount requestee;

    public ContactRequest(UserAccount requester, UserAccount requestee) {
        this.requester = requester;
        this.requestee = requestee;
    }

    public ContactRequest(long id, UserAccount requester, UserAccount requestee) {
        this.id = id;
        this.requester = requester;
        this.requestee = requestee;
    }

    public long getId() {
        return id;
    }

    public UserAccount getRequester() {
        return requester;
    }

    public UserAccount getRequestee() {
        return requestee;
    }
}
