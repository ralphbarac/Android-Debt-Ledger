package cs4474.g9.debtledger.ui.transaction;

import java.io.Serializable;
import java.util.List;

import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.UserAccount;

public class WhoOwesWrapper implements Serializable {

    private boolean selectedSelf;
    private List<Group> selectedGroups;
    private List<UserAccount> selectedContacts;

    public WhoOwesWrapper(boolean selectedSelf, List<Group> selectedGroups, List<UserAccount> selectedContacts) {
        this.selectedSelf = selectedSelf;
        this.selectedGroups = selectedGroups;
        this.selectedContacts = selectedContacts;
    }


    public boolean isSelectedSelf() {
        return selectedSelf;
    }

    public List<Group> getSelectedGroups() {
        return selectedGroups;
    }

    public List<UserAccount> getSelectedContacts() {
        return selectedContacts;
    }

}
