package cs4474.g9.debtledger.data;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cs4474.g9.debtledger.data.model.UserAccount;

public class ContactRequestManager {

    public static final String PENDING_END_POINT = "/contact_request/pending";
    public static final String PENDING_FROM_USER_END_POINT = "/contact_request/pendingfrom";
    public static final String ACCEPT_END_POINT = "/contact_request/accept";
    public static final String DENY_END_POINT = "/contact_request/deny";
    public static final String ADD_END_POINT = "/contact_request/add";
    public static final String DELETE_END_POINT = "/contact_request/delete";

    public static List<UserAccount> parseContactRequestsFromJson(JSONArray contactsJson) throws JSONException {
        List<UserAccount> contacts = new ArrayList<>();
        for (int i = 0; i < contactsJson.length(); i++) {
            contacts.add(UserAccountManager.parseUserAccountFromJson(contactsJson.getJSONObject(i)));
        }
        return contacts;
    }

}
