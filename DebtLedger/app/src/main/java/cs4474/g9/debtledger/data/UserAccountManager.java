package cs4474.g9.debtledger.data;

import java.util.List;

import cs4474.g9.debtledger.data.model.UserAccount;

public class UserAccountManager implements DatabaseManager<UserAccount> {

    @Override
    public Result<UserAccount> create(UserAccount object) {
        // TODO: Create User Account in database
        return new Result.Success<>(object);
    }

    @Override
    public Result<UserAccount> delete(UserAccount object) {
        // TODO: Delete User Account in databasse
        return new Result.Success<>(object);
    }

    @Override
    public Result<UserAccount> update(UserAccount object) {
        // TODO: Update User Account in database
        return new Result.Success<>(object);
    }

    @Override
    public List<UserAccount> getAll() {
        // TODO: Get all User Accounts in database
        return null;
    }

}
