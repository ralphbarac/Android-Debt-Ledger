package cs4474.g9.debtledger.data;

import cs4474.g9.debtledger.data.model.UserAccount;

public class UserAccountManager {

    public Result<UserAccount> createAccount(UserAccount account) {
        // TODO: Implement, currently using dummy data
        return new Result.Success<>(account);
    }

    public Result<UserAccount> updateAccount(UserAccount account) {
        // TODO: Implement
        return null;
    }

}
