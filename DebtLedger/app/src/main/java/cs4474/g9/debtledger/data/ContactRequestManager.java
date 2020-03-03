package cs4474.g9.debtledger.data;

import java.util.ArrayList;
import java.util.List;

import cs4474.g9.debtledger.data.model.ContactRequest;
import cs4474.g9.debtledger.data.model.UserAccount;

public class ContactRequestManager {

    public Result create(ContactRequest contactRequest) {
        // TODO: Implement
        return null;
    }

    public Result getAllContactRequestsFor(UserAccount user) {
        List<UserAccount> contactRequestsFor = new ArrayList<>();
        // TODO: Implement, currently using dummy data
        contactRequestsFor.add(new UserAccount("Randal", "Smith", "rmsith@uwo.ca"));

        return new Result.Success<>(contactRequestsFor);
    }

    public Result getAllContectRequestsBy(UserAccount user) {
        // TODO: Implement, currently using dummy data
        List<UserAccount> contactRequestsBy = new ArrayList<>();
        contactRequestsBy.add(new UserAccount("Mike", "Lee", "mlee@uwo.ca"));

        return new Result.Success<>(contactRequestsBy);
    }

}
