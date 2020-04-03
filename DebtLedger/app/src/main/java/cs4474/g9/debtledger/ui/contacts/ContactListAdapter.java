package cs4474.g9.debtledger.ui.contacts;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.ViewContactActivity;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.Item> {

    private List<Pair<UserAccount, Long>> contactsWithBalances;

    public ContactListAdapter(List<Pair<UserAccount, Long>> contactsWithBalances) {
        super();
        this.contactsWithBalances = contactsWithBalances;
    }

    public ContactListAdapter() {
        super();
        this.contactsWithBalances = new ArrayList<>();
    }

    public void setContactsWithBalances(List<Pair<UserAccount, Long>> contactsWithBalances) {
        this.contactsWithBalances = contactsWithBalances;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        UserAccount contact = contactsWithBalances.get(position).first;
        Long amount = contactsWithBalances.get(position).second;

        holder.contactAvatar.setColorFilter(ColourGenerator.generateFromName(contact.getFirstName(), contact.getLastName()));
        holder.contactAvatarCharacter.setText(contact.getFirstName().substring(0, 1));
        holder.contactName.setText(contact.getFirstName() + " " + contact.getLastName());
        if (amount == 0) {
            holder.amount.setText("");
        } else {
            holder.amount.setText(String.format(Locale.CANADA, "%s$%.2f", amount < 0 ? "-" : "+", Math.abs(amount) / 100.0));
            if (amount < 0) {
                holder.amount.setTextColor(Color.RED);
            } else {
                holder.amount.setTextColor(Color.GREEN);
            }
        }
    }

    @Override
    public int getItemCount() {
        return contactsWithBalances == null ? 0 : contactsWithBalances.size();
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView contactAvatar;
        public TextView contactAvatarCharacter;
        public TextView contactName;
        public TextView amount;

        public Item(View view) {
            super(view);
            this.contactAvatar = view.findViewById(R.id.contact_avatar);
            this.contactAvatarCharacter = view.findViewById(R.id.contact_avatar_character);
            this.contactName = view.findViewById(R.id.name);
            this.amount = view.findViewById(R.id.amount);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("CONTACTS", "Contact clicked.");
            Intent toViewContact = new Intent(v.getContext(), ViewContactActivity.class);
            toViewContact.putExtra(ViewContactActivity.CONTACT, contactsWithBalances.get(getAdapterPosition()).first);
            v.getContext().startActivity(toViewContact);
        }
    }
}
