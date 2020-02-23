package cs4474.g9.debtledger.ui.contacts;

import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.Item> {

    private List<Pair<UserAccount, Integer>> balances;

    public ContactListAdapter(List<Pair<UserAccount, Integer>> balances) {
        super();
        this.balances = balances;
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
        UserAccount contact = balances.get(position).first;
        Integer amount = balances.get(position).second;

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
        return balances == null ? 0 : balances.size();
    }

    public static class Item extends RecyclerView.ViewHolder {
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
        }
    }
}
