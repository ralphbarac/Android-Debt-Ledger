package cs4474.g9.debtledger.data;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cs4474.g9.debtledger.data.model.ContactRequest;
import cs4474.g9.debtledger.data.model.UserAccount;

public class ContactRequestManager {

    public static final String PENDING_END_POINT = "/contact_request/pending";
    public static final String PENDING_FROM_USER_END_POINT = "/contact_request/pendingfrom";

    public static List<UserAccount> parseContactRequestsFromJson(JSONArray contactsJson) throws JSONException {
        List<UserAccount> contacts = new ArrayList<>();
        for (int i = 0; i < contactsJson.length(); i++) {
            contacts.add(UserAccountManager.parseUserAccountFromJson(contactsJson.getJSONObject(i)));
        }
        return contacts;
    }

    public Result create(ContactRequest contactRequest) {
        // TODO: Implement
        return null;
    }

    public Result getAllContactRequestsFor(UserAccount user) {
        // TODO: Implement, currently using dummy data
        List<UserAccount> contactRequestsFor = new ArrayList<>();
        contactRequestsFor.add(new UserAccount("Randal", "Smith", "rmsith@uwo.ca"));

        return new Result.Success<>(contactRequestsFor);
    }

    public Result getCountOfContactRequestsFor(UserAccount user) {
        // TODO: Implement, currently using dummy data
        return new Result.Success<>(1);
    }

    public Result getAllContectRequestsBy(UserAccount user) {
        // TODO: Implement, currently using dummy data
        List<UserAccount> contactRequestsBy = new ArrayList<>();
        contactRequestsBy.add(new UserAccount("Mike", "Lee", "mlee@uwo.ca"));

        return new Result.Success<>(contactRequestsBy);
    }

}
