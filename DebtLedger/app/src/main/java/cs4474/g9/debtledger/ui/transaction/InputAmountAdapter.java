package cs4474.g9.debtledger.ui.transaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class InputAmountAdapter extends RecyclerView.Adapter<InputAmountAdapter.Item> {

    private List<UserAccount> contacts;
    private List<String> amounts;
    private UserAccount loggedInUser;

    private List<OnInputAmountsChanged> onInputChangedListeners = new ArrayList<>();

    public InputAmountAdapter(List<UserAccount> contacts, UserAccount loggedInUser) {
        super();
        this.contacts = contacts;

        int numContacts = contacts == null ? 0 : contacts.size();
        this.amounts = new ArrayList<>(numContacts);
        for (int i = 0; i < numContacts; i++) {
            amounts.add("");
        }

        this.loggedInUser = loggedInUser;
    }

    public void addOnInputChangedListener(OnInputAmountsChanged onInputAmountsChanged) {
        onInputChangedListeners.add(onInputAmountsChanged);
    }

    public void notifyListenersOfInputAmountChanged() {
        for (OnInputAmountsChanged onInputChangedListener : onInputChangedListeners) {
            onInputChangedListener.onAmountChanged(amounts);
        }
    }

    public void updateContacts(List<UserAccount> contacts) {
        this.contacts = contacts;

        // TODO: Maintain previous input
        int numContacts = contacts == null ? 0 : contacts.size();
        this.amounts = new ArrayList<>(numContacts);
        for (int i = 0; i < numContacts; i++) {
            amounts.add("");
        }
        notifyDataSetChanged();
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
        if (contact.equals(loggedInUser)) {
            holder.contactName.setText("Me");
        } else {
            holder.contactName.setText(contact.getFirstName() + " " + contact.getLastName());
        }

        // Only change the amount text if it is different - otherwise we may create an infinite loop with the text watcher
        if (!holder.amount.getText().toString().equals(amounts.get(position))) {
            holder.amount.setText(amounts.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    public class Item extends RecyclerView.ViewHolder {
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

            this.amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    amounts.set(getAdapterPosition(), s.toString());
                    if (!TransactionViewModel.isAmountValid(s.toString())) {
                        amount.setError(amount.getContext().getString(R.string.invalid_amount));
                    }
                    notifyListenersOfInputAmountChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }
}
