package cs4474.g9.debtledger.data;

import java.io.IOException;

import cs4474.g9.debtledger.data.model.LoggedInUser;

/**
 * A class to handle logic related to authentication, and returning of appropriate user information
 */
public class LoginAuthenticator {

    public Result<LoggedInUser> loginAndAuthenticate(String email, String password) {

        try {
            // TODO: Authenticate
            if (email.equals("zsirohey@uwo.ca") && password.equals("zain1234")) {
                LoggedInUser testUser = new LoggedInUser(
                        "XSJKJSKDJKJS93JSKJK88",
                        "Zain",
                        "Sirohey"
                );
                return new Result.Success<>(testUser);
            } else {
                throw new Exception("Invalid Email and/or Password!");
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in!", e));
        }
    }

    public Result<LoggedInUser> authenticate(String token) {
        try {
            // TODO: Authenticate
            if (token.equals("XSJKJSKDJKJS93JSKJK88")) {
                LoggedInUser testUser = new LoggedInUser(
                        token,
                        "Zain",
                        "Sirohey"
                );
                return new Result.Success<>(testUser);
            } else {
                throw new Exception("Invalid token!");
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error authenticating!", e));
        }
    }

    public void revokeAuthentication(String token) {
        // TODO: Remove Authentication
    }
}
