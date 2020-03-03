package cs4474.g9.debtledger.data.login;

import android.content.Context;
import android.content.SharedPreferences;

import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.model.UserAccount;

/**
 * Class to handle and persist authentication status related to current loggedInUser logged in
 */
public class LoginRepository {

    private final String TOKEN_STORE = "token_store";
    private final String TOKEN_KEY = "token";

    private static volatile LoginRepository instance;

    private LoginAuthenticator authenticator;
    private UserAccount loggedInUser = null;
    private String token = "";

    // Singleton, so private constructor
    private LoginRepository(LoginAuthenticator authenticator, Context context) {
        this.authenticator = authenticator;

        readToken(context);
        if (!this.token.isEmpty()) {
            // Attempt to authenticate token, discard result
            authenticateToken();
        }
    }

    public static LoginRepository getInstance(Context context) {
        if (instance == null) {
            instance = new LoginRepository(new LoginAuthenticator(), context);
        }
        return instance;
    }

    public UserAccount getLoggedInUser() {
        return loggedInUser;
    }

    public String getToken() {
        return token;
    }

    public boolean isUserLoggedInAndAuthenticated() {
        return token != null && !token.isEmpty() && loggedInUser != null;
    }

    private void readToken(Context context) {
        SharedPreferences tokenStore = context.getSharedPreferences(TOKEN_STORE, Context.MODE_PRIVATE);
        token = tokenStore.getString(TOKEN_KEY, "");
    }

    public void storeToken(Context context) {
        SharedPreferences tokenStore = context.getSharedPreferences(TOKEN_STORE, Context.MODE_PRIVATE);
        tokenStore.edit().putString(TOKEN_KEY, token).apply();
    }

    private void deleteToken(Context context) {
        SharedPreferences tokenStore = context.getSharedPreferences(TOKEN_STORE, Context.MODE_PRIVATE);
        tokenStore.edit().putString(TOKEN_KEY, "").apply();
        token = "";
    }

    public Result<UserAccount> login(String email, String password) {
        Result<LoggedInUserHolder> result = authenticator.loginAndAuthenticate(email, password);
        if (result instanceof Result.Success) {
            LoggedInUserHolder loggedInUserHolder = ((Result.Success<LoggedInUserHolder>) result).getData();
            this.loggedInUser = loggedInUserHolder.getAccount();
            this.token = loggedInUserHolder.getToken();

            return new Result.Success(loggedInUser);
        } else {
            return new Result.Error(((Result.Error) result).getError());
        }
    }

    public void logout(Context context) {
        // Revoke authentication token
        authenticator.revokeAuthentication(token);
        loggedInUser = null;
        deleteToken(context);
    }

    public Result<UserAccount> authenticateToken() {
        // Authenticate given token
        Result<LoggedInUserHolder> result = authenticator.authenticate(token);
        if (result instanceof Result.Success) {
            LoggedInUserHolder loggedInUserHolder = ((Result.Success<LoggedInUserHolder>) result).getData();
            this.loggedInUser = loggedInUserHolder.getAccount();

            return new Result.Success(loggedInUser);
        } else {
            return new Result.Error(((Result.Error) result).getError());
        }
    }
}
