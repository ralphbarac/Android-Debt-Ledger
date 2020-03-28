package cs4474.g9.debtledger.data.login;

import android.content.Context;
import android.content.SharedPreferences;

import cs4474.g9.debtledger.data.model.UserAccount;

/**
 * Class to handle and persist authentication status related to current loggedInUser logged in
 */
public class LoginRepository {

    private final String TOKEN_STORE = "token_store";
    private final String TOKEN_KEY = "token";

    private static volatile LoginRepository instance;
    private Context context;

    private UserAccount loggedInUser = null;
    private String token = "";

    // Singleton, so private constructor
    private LoginRepository(Context context) {
        this.context = context.getApplicationContext();

        readToken();
    }

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new LoginRepository(context);
        }
    }
    
    public static LoginRepository getInstance() {
        if (instance != null) {
            return instance;
        } else {
            throw new IllegalStateException("LoginRepository is not initialized");
        }
    }

    public UserAccount getLoggedInUser() {
        return loggedInUser;
    }

    public String getToken() {
        return token;
    }

    public boolean hasToken() {
        return token != null && !token.isEmpty();
    }
    
    public boolean isUserLoggedInAndAuthenticated() {
        return token != null && !token.isEmpty() && loggedInUser != null;
    }

    private void readToken() {
        SharedPreferences tokenStore = context.getSharedPreferences(TOKEN_STORE, Context.MODE_PRIVATE);
        token = tokenStore.getString(TOKEN_KEY, "");
    }

    private void storeToken() {
        SharedPreferences tokenStore = context.getSharedPreferences(TOKEN_STORE, Context.MODE_PRIVATE);
        tokenStore.edit().putString(TOKEN_KEY, token).apply();
    }

    private void deleteToken() {
        SharedPreferences tokenStore = context.getSharedPreferences(TOKEN_STORE, Context.MODE_PRIVATE);
        tokenStore.edit().putString(TOKEN_KEY, "").apply();
        token = "";
    }

    public void loginUser(UserAccount loggedInUser, String token) {
        this.loggedInUser = loggedInUser;
        this.token = token;
        storeToken();
    }

    public void logoutUser() {
        loggedInUser = null;
        token = "";
        deleteToken();
    }
}
