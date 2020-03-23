package cs4474.g9.debtledger.data.login;

import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import cs4474.g9.debtledger.data.ConnectionAdapter;
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
            // TODO: Remove sleep, here to simulate delay needed for call to backend
            Thread.sleep(ThreadLocalRandom.current().nextInt(250, 1500));
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

//    Result<LoggedInUserHolder> loginAndAuthenticate(String email, String password) {
//        try {
//            MobileServiceClient client = ConnectionAdapter.getInstance().getClient();
//            MobileServiceTable<UserAccount> userTable = client.getTable("users", UserAccount.class);
//            List<UserAccount> results = userTable.where()
//                    .field("Email").eq(email)
//                    .and()
//                    .field("Password").eq(password)
//                    .select("id", "FirstName", "LastName", "Email", "Password")
//                    .execute().get();
//            Log.d("LOGIN", results.toString());
//
//            if (results.size() >= 1) {
//                // For now, token is simply user id
//                LoggedInUserHolder user = new LoggedInUserHolder(
//                        results.get(0).getId(),
//                        results.get(0)
//                );
//                return new Result.Success<>(user);
//            } else {
//                throw new Exception("Invalid Email and/or Password!");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Result.Error(new IOException("Error logging in!", e));
//        }
//    }
//
//    Result<LoggedInUserHolder> authenticate(String token) {
//        try {
//            MobileServiceClient client = ConnectionAdapter.getInstance().getClient();
//            MobileServiceTable<UserAccount> userTable = client.getTable("users", UserAccount.class);
//            List<UserAccount> results = userTable.where()
//                    .field("id").eq(token)
//                    .select("id", "FirstName", "LastName", "Email", "Password")
//                    .execute().get();
//            Log.d("LOGIN", results.toString());
//
//            if (results.size() >= 1) {
//                LoggedInUserHolder user = new LoggedInUserHolder(
//                        token,
//                        results.get(0)
//                );
//                return new Result.Success<>(user);
//            } else {
//                throw new Exception("Invalid token!");
//            }
//        } catch (Exception e) {
//            return new Result.Error(new IOException("Error authenticating!", e));
//        }
//    }
//
//    void revokeAuthentication(String token) {
//        // Given that actual tokens are not being used right now, no action needs to be taken here
//    }
}
