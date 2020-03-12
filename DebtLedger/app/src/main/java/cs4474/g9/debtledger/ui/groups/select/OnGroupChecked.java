package cs4474.g9.debtledger.ui.groups.select;

import cs4474.g9.debtledger.data.model.Group;

public interface OnGroupChecked {

    void onGroupChecked(Group group);

    void onGroupUnchecked(Group group);

}
