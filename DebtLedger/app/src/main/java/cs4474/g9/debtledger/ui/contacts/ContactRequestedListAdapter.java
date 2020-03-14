package cs4474.g9.debtledger.ui.contacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.ViewContactActivity;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class ContactRequestedListAdapter extends RecyclerView.Adapter<ContactRequestedListAdapter.Item> {

    private List<UserAccount> contactsRequested;

    public ContactRequestedListAdapter(List<UserAccount> contactsRequested) {
        super();
        this.contactsRequested = contactsRequested;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact_requested, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        UserAccount contact = contactsRequested.get(position);

        holder.contactAvatar.setColorFilter(ColourGenerator.generateFromName(contact.getFirstName(), contact.getLastName()));
        holder.contactAvatarCharacter.setText(contact.getFirstName().substring(0, 1));
        holder.contactName.setText(contact.getFirstName() + " " + contact.getLastName());
    }

    @Override
    public int getItemCount() {
        return contactsRequested == null ? 0 : contactsRequested.size();
    }

    public void addRequestedContact(UserAccount requestedContact) {
        contactsRequested.add(requestedContact);
//        notifyItemInserted(contactsRequested.size() - 1);
        notifyDataSetChanged();
    }

    private void removeRequestedContact(int position) {
        contactsRequested.remove(position);
//        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView contactAvatar;
        public TextView contactAvatarCharacter;
        public TextView contactName;
        public ImageView cancel;

        public Item(View view) {
            super(view);
            this.contactAvatar = view.findViewById(R.id.contact_avatar);
            this.contactAvatarCharacter = view.findViewById(R.id.contact_avatar_character);
            this.contactName = view.findViewById(R.id.name);
            this.cancel = view.findViewById(R.id.cancel);

            // On click confirm cancellation of contact request
            this.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserAccount contactRequest = contactsRequested.get(getAdapterPosition());
                    String name = contactRequest.getFirstName() + " " + contactRequest.getLastName();
                    new MaterialAlertDialogBuilder(v.getContext())
                            .setTitle("Confirm Cancel")
                            .setMessage("Are you sure you want to cancel your contact request to " + name + "?")
                            .setPositiveButton("No", null)
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: Delete contact request from database
                                    removeRequestedContact(getAdapterPosition());
                                }
                            })
                            .show();
                }
            });

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("CONTACTS", "Requested Contact clicked.");
            Intent toViewContact = new Intent(v.getContext(), ViewContactActivity.class);
            toViewContact.putExtra(ViewContactActivity.CONTACT, contactsRequested.get(getAdapterPosition()));
            v.getContext().startActivity(toViewContact);
        }
    }
}
