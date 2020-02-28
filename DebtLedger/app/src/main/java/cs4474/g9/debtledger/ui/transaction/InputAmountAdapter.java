package cs4474.g9.debtledger.ui.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.model.LoggedInUser;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class InputAmountAdapter extends RecyclerView.Adapter<InputAmountAdapter.Item> {

    private List<UserAccount> contacts;
    private List<Integer> amounts;
    private LoggedInUser user;

    public InputAmountAdapter(List<UserAccount> contacts, LoggedInUser user) {
        super();
        this.contacts = contacts;
        this.amounts = new ArrayList<>(contacts == null ? 0 : contacts.size());
        this.user = user;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_input_amount, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        UserAccount contact = contacts.get(position);

        holder.contactAvatar.setColorFilter(ColourGenerator.generateFromName(contact.getFirstName(), contact.getLastName()));
        holder.contactAvatarCharacter.setText(contact.getFirstName().substring(0, 1));
        // TODO: If user is the same
        holder.contactName.setText(contact.getFirstName() + " " + contact.getLastName());
        holder.amount.setText(String.format(Locale.CANADA, "%.2f", amounts.get(position)/100.0));
    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    public static class Item extends RecyclerView.ViewHolder {
        public ImageView contactAvatar;
        public TextView contactAvatarCharacter;
        public TextView contactName;
        public TextInputEditText amount;

        public Item(View view) {
            super(view);
            this.contactAvatar = view.findViewById(R.id.contact_avatar);
            this.contactAvatarCharacter = view.findViewById(R.id.contact_avatar_character);
            this.contactName = view.findViewById(R.id.contact_name);
            this.amount = view.findViewById(R.id.amount_input);
        }
    }
}
