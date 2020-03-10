package cs4474.g9.debtledger.ui.groups;

import java.util.List;

import cs4474.g9.debtledger.data.model.UserAccount;

public interface OnMemberRemoved {

    void onGroupMemberRemoved(List<UserAccount> newListOfMembers);

}
