package cs4474.g9.debtledger.data;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cs4474.g9.debtledger.data.model.UserAccount;

public class ContactManager {

    public static final String LIST_END_POINT = "/contact/id";

    public static List<UserAccount> parseContactsFromJson(JSONArray contactsJson) throws JSONException {
        List<UserAccount> contacts = new ArrayList<>();
        for (int i = 0; i < contactsJson.length(); i++) {
            contacts.add(UserAccountManager.parseUserAccountFromJson(contactsJson.getJSONObject(i)));
        }
        return contacts;
    }

}
