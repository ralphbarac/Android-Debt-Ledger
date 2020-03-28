package cs4474.g9.debtledger.data;

import org.json.JSONException;
import org.json.JSONObject;

import cs4474.g9.debtledger.data.model.UserAccount;

public class UserAccountManager {

    public static final String LOGIN_END_POINT = "/login";
    public static final String LOGIN_WITH_TOKEN_END_POINT = "/login-token";
    public static final String SIGNUP_END_POINT = "/create";

    public static JSONObject createJsonFromUserAccount(UserAccount userAccount) throws JSONException {
        JSONObject jsonUserAccount = new JSONObject();
        jsonUserAccount.put("firstName", userAccount.getId());
        jsonUserAccount.put("lastName", userAccount.getId());
        jsonUserAccount.put("email", userAccount.getId());
        jsonUserAccount.put("password", userAccount.getId());
        return jsonUserAccount;
    }

    public static UserAccount parseUserAccountFromJson(JSONObject jsonUserAccount) throws JSONException {
        return new UserAccount(
                jsonUserAccount.getString("id"),
                jsonUserAccount.getString("firstName"),
                jsonUserAccount.getString("lastName"),
                jsonUserAccount.getString("email"),
                jsonUserAccount.getString("password")
        );
    }

}
