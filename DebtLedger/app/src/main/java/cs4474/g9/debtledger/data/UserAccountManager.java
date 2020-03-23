package cs4474.g9.debtledger.data;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import cs4474.g9.debtledger.data.model.UserAccount;

public class UserAccountManager {

    public Result<UserAccount> createAccount(UserAccount user) {
        try {
            MobileServiceClient client = ConnectionAdapter.getInstance().getClient();
            MobileServiceTable<UserAccount> userTable = client.getTable("users", UserAccount.class);
            UserAccount result = userTable.insert(user).get();
            return new Result.Success<>(result);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result.Error(e);
        }
    }

    public Result<UserAccount> updateAccount(UserAccount account) {
        // TODO: Implement
        return null;
    }

}
