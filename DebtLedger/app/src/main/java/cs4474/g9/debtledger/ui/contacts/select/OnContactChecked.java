package cs4474.g9.debtledger.ui.contacts.select;

import cs4474.g9.debtledger.data.model.UserAccount;

public interface OnContactChecked {

    void onContactChecked(UserAccount contact);

    void onContactUnchecked(UserAccount contact);

}
