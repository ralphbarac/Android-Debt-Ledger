package cs4474.g9.debtledger.ui.contacts.select;

import java.io.Serializable;
import java.util.List;

import cs4474.g9.debtledger.data.model.UserAccount;

public class SelectMultipleContactsWrapper implements Serializable {

    private boolean selectedSelf;
    private List<Object> selectedGroups;
    private List<UserAccount> selectedContacts;

    public SelectMultipleContactsWrapper(boolean selectedSelf, List<Object> selectedGroups, List<UserAccount> selectedContacts) {
        this.selectedSelf = selectedSelf;
        this.selectedGroups = selectedGroups;
        this.selectedContacts = selectedContacts;
    }


    public boolean isSelectedSelf() {
        return selectedSelf;
    }

    public List<Object> getSelectedGroups() {
        return selectedGroups;
    }

    public List<UserAccount> getSelectedContacts() {
        return selectedContacts;
    }

}
