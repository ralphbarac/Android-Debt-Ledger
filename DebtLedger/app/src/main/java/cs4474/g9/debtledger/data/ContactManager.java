package cs4474.g9.debtledger.data;

import java.util.ArrayList;
import java.util.List;

import cs4474.g9.debtledger.data.model.Contact;
import cs4474.g9.debtledger.data.model.UserAccount;

public class ContactManager {

    public Result createContact(Contact contact) {
        // TODO: Implement
        return null;
    }

    public Result<List<UserAccount>> getAllContactsOf(UserAccount user) {
        // TODO: Implement, currently using dummy data
        List<UserAccount> contacts = new ArrayList<>();
        contacts.add(new UserAccount("John", "Doe", ""));
        contacts.add(new UserAccount("Thomas", "Morphew", ""));
        contacts.add(new UserAccount("Timothy", "Young", ""));
        contacts.add(new UserAccount("Will", "Smith", ""));

        return new Result.Success<>(contacts);
    }

}
