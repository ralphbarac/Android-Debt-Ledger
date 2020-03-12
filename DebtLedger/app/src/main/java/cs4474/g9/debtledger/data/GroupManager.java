package cs4474.g9.debtledger.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.UserAccount;

public class GroupManager {

    public Result createGroup(Group group) {
        // TODO: Implement
        return null;
    }

    public Result<List<Group>> getGroupsOf(UserAccount user) {
        // TODO: Implement, currently using dummy data
        List<UserAccount> group1list = new ArrayList<>();
        List<UserAccount> group2list = new ArrayList<>();
        group1list.add(new UserAccount("John", "Doe", ""));
        group1list.add(new UserAccount("Thomas", "Morphew", ""));
        group2list.add(new UserAccount("Timothy", "Young", ""));
        group2list.add(new UserAccount("Will", "Smith", ""));

        List<Group> groupList = new ArrayList<>();
        groupList.add(new Group(user, "Roomies", group1list));
        groupList.add(new Group(user, "Pizza Hut Boys", group2list));

        return new Result.Success<>(groupList);
    }

}
