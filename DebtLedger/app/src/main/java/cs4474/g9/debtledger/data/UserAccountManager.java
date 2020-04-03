package cs4474.g9.debtledger.data;

import org.json.JSONException;
import org.json.JSONObject;

import cs4474.g9.debtledger.data.model.UserAccount;

public class UserAccountManager {

    public static final String LOGIN_END_POINT = "/user/login";
    public static final String LOGIN_WITH_TOKEN_END_POINT = "/user/id";
    public static final String SIGNUP_END_POINT = "/user/add";
    public static final String GET_USER_BY_EMAIL_END_POINT = "/user/email";

    public static JSONObject createJsonFromUserAccount(UserAccount userAccount) throws JSONException {
        JSONObject jsonUserAccount = new JSONObject();
        jsonUserAccount.put("first_name", userAccount.getFirstName());
        jsonUserAccount.put("last_name", userAccount.getLastName());
        jsonUserAccount.put("email", userAccount.getEmail());
        jsonUserAccount.put("password", userAccount.getPassword());
        return jsonUserAccount;
    }

    public static UserAccount parseUserAccountFromJson(JSONObject jsonUserAccount) throws JSONException {
        return new UserAccount(
                jsonUserAccount.getInt("id"),
                jsonUserAccount.getString("first_name"),
                jsonUserAccount.getString("last_name"),
                jsonUserAccount.getString("email")
        );
    }

}
