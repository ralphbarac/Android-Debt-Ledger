package cs4474.g9.debtledger.ui.contacts.select;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class SelectContactAdapter extends RecyclerView.Adapter<SelectContactAdapter.Item> {

    private List<UserAccount> contacts;
    private List<OnContactSelected> onContactSelectedListeners;

    public SelectContactAdapter(List<UserAccount> contacts) {
        super();
        this.contacts = contacts;
        this.onContactSelectedListeners = new ArrayList<>();
    }

    public void addOnContactSelectedListener(OnContactSelected listener) {
        this.onContactSelectedListeners.add(listener);
    }

    public void notifyOnContactSelectedListeners(UserAccount contact) {
        for (OnContactSelected onContactSelectedListener : onContactSelectedListeners) {
            onContactSelectedListener.onContactSelected(contact);
        }
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_select_contact, parent, false);

        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        UserAccount contact = contacts.get(position);

        holder.contactAvatar.setColorFilter(ColourGenerator.generateFromName(contact.getFirstName(), contact.getLastName()));
        holder.contactAvatarCharacter.setText(contact.getFirstName().substring(0, 1));
        holder.contactName.setText(contact.getFirstName() + " " + contact.getLastName());
    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    // TODO: Is it good practice for ViewHolder to be static?
    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView contactAvatar;
        public TextView contactAvatarCharacter;
        public TextView contactName;

        public Item(View view) {
            super(view);
            view.setOnClickListener(this);
            this.contactAvatar = view.findViewById(R.id.contact_avatar);
            this.contactAvatarCharacter = view.findViewById(R.id.contact_avatar_character);
            this.contactName = view.findViewById(R.id.name);
        }

        @Override
        public void onClick(View v) {
            notifyOnContactSelectedListeners(contacts.get(getAdapterPosition()));
        }
    }
}
