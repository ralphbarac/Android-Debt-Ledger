package cs4474.g9.debtledger.data.login;

import java.io.IOException;

import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.model.UserAccount;

/**
 * A class to handle logic related to authentication, and returning of appropriate user information
 * and token - this class emulates what should be an external authentication API
 */
class LoginAuthenticator {

    Result<LoggedInUserHolder> loginAndAuthenticate(String email, String password) {

        try {
            // TODO: Authenticate
            if (email.equals("zsirohey@uwo.ca") && password.equals("zain1234")) {
                LoggedInUserHolder testUser = new LoggedInUserHolder(
                        "XSJKJSKDJKJS93JSKJK88",
                        new UserAccount(
                                "Zain",
                                "Sirohey",
                                "zsirohey@uwo.ca"
                        )
                );
                return new Result.Success<>(testUser);
            } else {
                throw new Exception("Invalid Email and/or Password!");
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in!", e));
        }
    }

    Result<LoggedInUserHolder> authenticate(String token) {
        try {
            // TODO: Authenticate
            if (token.equals("XSJKJSKDJKJS93JSKJK88")) {
                LoggedInUserHolder testUser = new LoggedInUserHolder(
                        token,
                        new UserAccount(
                                "Zain",
                                "Sirohey",
                                "zsirohey@uwo.ca"
                        )
                );
                return new Result.Success<>(testUser);
            } else {
                throw new Exception("Invalid token!");
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error authenticating!", e));
        }
    }

    void revokeAuthentication(String token) {
        // TODO: Remove Authentication
    }
}
