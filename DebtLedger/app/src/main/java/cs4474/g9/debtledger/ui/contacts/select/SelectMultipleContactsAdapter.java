package cs4474.g9.debtledger.ui.contacts.select;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class SelectMultipleContactsAdapter extends RecyclerView.Adapter<SelectMultipleContactsAdapter.Item> {

    private List<UserAccount> contacts;
    private BitSet selectedContacts;

    private List<OnContactChecked> onContactCheckedListeners;

    public SelectMultipleContactsAdapter(List<UserAccount> contacts, List<UserAccount> currentlySelectedContacts) {
        super();
        this.contacts = contacts;
        this.selectedContacts = new BitSet(contacts.size());
        this.onContactCheckedListeners = new ArrayList<>();

        for (UserAccount currentlySelectedContact : currentlySelectedContacts) {
            if (contacts.contains(currentlySelectedContact)) {
                selectedContacts.set(contacts.indexOf(currentlySelectedContact));
            } else {
                Log.e("CONTACTS", "The selected contact is not found in the list of contacts!");
            }
        }
    }

    public SelectMultipleContactsAdapter() {
        super();
        this.contacts = new ArrayList<>();
        this.selectedContacts = new BitSet(contacts.size());
        this.onContactCheckedListeners = new ArrayList<>();
    }

    public void addOnContactCheckedListener(OnContactChecked onContactChecked) {
        onContactCheckedListeners.add(onContactChecked);
    }

    public void notifyListenersOfContactChecked(UserAccount contact) {
        for (OnContactChecked onContactCheckedListener : onContactCheckedListeners) {
            onContactCheckedListener.onContactChecked(contact);
        }
    }

    public void notifyListenersOfContactUnchecked(UserAccount contact) {
        for (OnContactChecked onContactCheckedListener : onContactCheckedListeners) {
            onContactCheckedListener.onContactUnchecked(contact);
        }
    }

    public void setContacts(List<UserAccount> contacts, List<UserAccount> currentlySelectedContacts) {
        this.contacts = contacts;
        this.selectedContacts = new BitSet(contacts.size());

        for (UserAccount currentlySelectedContact : currentlySelectedContacts) {
            selectedContacts.set(contacts.indexOf(currentlySelectedContact));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_select_contact_multiple, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        UserAccount contact = contacts.get(position);

        holder.contactAvatar.setColorFilter(ColourGenerator.generateFromName(contact.getFirstName(), contact.getLastName()));
        holder.contactAvatarCharacter.setText(contact.getFirstName().substring(0, 1));
        holder.contactName.setText(contact.getFirstName() + " " + contact.getLastName());

        holder.checkBox.setChecked(selectedContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    public List<UserAccount> getSelectedContacts() {
        List<UserAccount> selections = new ArrayList<>();
        for (int i = 0; i < selectedContacts.size(); i++) {
            if (selectedContacts.get(i)) {
                selections.add(contacts.get(i));
            }
        }
        return selections;
    }

    public void selectContactsFromGroup(Group group) {
        for (UserAccount groupMember : group.getGroupMembers()) {
            int position = contacts.indexOf(groupMember);
            selectedContacts.set(position);
            notifyItemChanged(position);
        }
    }

    public void unselectContactsFromGroup(Group group) {
        for (UserAccount groupMember : group.getGroupMembers()) {
            int position = contacts.indexOf(groupMember);
            selectedContacts.clear(position);
            notifyItemChanged(position);
        }
    }

    private void setSelected(int position) {
        selectedContacts.set(position);
    }

    private void clearSelected(int position) {
        selectedContacts.clear(position);
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener, CheckBox.OnCheckedChangeListener {
        public ImageView contactAvatar;
        public TextView contactAvatarCharacter;
        public TextView contactName;
        public CheckBox checkBox;

        public Item(View view) {
            super(view);
            view.setOnClickListener(this);

            this.contactAvatar = view.findViewById(R.id.contact_avatar);
            this.contactAvatarCharacter = view.findViewById(R.id.contact_avatar_character);
            this.contactName = view.findViewById(R.id.name);
            this.checkBox = view.findViewById(R.id.check_box);
            this.checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            checkBox.setChecked(!checkBox.isChecked());
            if (checkBox.isChecked()) {
                setSelected(getAdapterPosition());
                notifyListenersOfContactChecked(contacts.get(getAdapterPosition()));
            } else {
                clearSelected(getAdapterPosition());
                notifyListenersOfContactUnchecked(contacts.get(getAdapterPosition()));
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // On checked changed is called even through programmatic changes, in order to avoid
            // notifying listeners when not necessary, only executing method if state is changing
            if (selectedContacts.get(getAdapterPosition()) != isChecked) {
                if (isChecked) {
                    setSelected(getAdapterPosition());
                    notifyListenersOfContactChecked(contacts.get(getAdapterPosition()));
                } else {
                    clearSelected(getAdapterPosition());
                    notifyListenersOfContactUnchecked(contacts.get(getAdapterPosition()));
                }
            }
        }
    }
}
