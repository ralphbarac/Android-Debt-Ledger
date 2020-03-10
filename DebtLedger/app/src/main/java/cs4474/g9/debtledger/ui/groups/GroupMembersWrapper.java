package cs4474.g9.debtledger.ui.groups;

import java.io.Serializable;
import java.util.List;

import cs4474.g9.debtledger.data.model.UserAccount;

/*
    A wrapper class that implements Serializable in order to pass list of group members using intent
 */
public class GroupMembersWrapper implements Serializable {

    private List<UserAccount> groupMembers;

    public GroupMembersWrapper(List<UserAccount> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public List<UserAccount> getGroupMembers() {
        return groupMembers;
    }
}
