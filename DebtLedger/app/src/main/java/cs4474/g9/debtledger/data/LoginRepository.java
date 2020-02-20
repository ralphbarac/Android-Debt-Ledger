package cs4474.g9.debtledger.data;

import cs4474.g9.debtledger.data.model.LoggedInUser;

/**
 * Class to handle and persist authentication status related to current user logged in
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginAuthenticator authenticator;
    private LoggedInUser user = null;

    // Singleton, so private constructor
    private LoginRepository(LoginAuthenticator authenticator) {
        // TODO: Load user data (token) which may have been persisted
        this.authenticator = authenticator;
    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository(new LoginAuthenticator());
        }
        return instance;
    }

    public static LoginRepository getInstance(LoginAuthenticator source) {
        if (instance == null) {
            instance = new LoginRepository(source);
        }
        return instance;
    }

    public LoggedInUser getLoggedInUser() {
        return user;
    }

    public boolean isUserLoggedIn() {
        return user != null;
    }

    public void logout() {
        // TODO: Delete token (locally) and revoke authentication
        authenticator.revokeAuthentication(user.getToken());
        user = null;
    }

    private void setUserAsLoggedIn(LoggedInUser user) {
        // TODO: Persist user data (token) locally, Encryption, see https://developer.android.com/training/articles/keystore
        this.user = user;
    }

    public Result<LoggedInUser> login(String email, String password) {
        // TODO: Login and persist token
        Result<LoggedInUser> result = authenticator.loginAndAuthenticate(email, password);
        if (result instanceof Result.Success) {
            setUserAsLoggedIn(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public Result<LoggedInUser> authenticateLoggedInUser() {
        // Authenticate given token
        return authenticator.authenticate(user.getToken());
    }
}
