package cs4474.g9.debtledger.ui.contacts;

import cs4474.g9.debtledger.data.model.UserAccount;

public interface OnRequestReply {

    void onContactAccepted(UserAccount contact);

    void onContactRejected(UserAccount contact);

}
