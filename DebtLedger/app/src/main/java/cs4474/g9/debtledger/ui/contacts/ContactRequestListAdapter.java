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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.ViewContactActivity;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class ContactRequestListAdapter extends RecyclerView.Adapter<ContactRequestListAdapter.Item> {

    private List<UserAccount> contactRequests;
    private List<OnRequestReply> onRequestReplyListeners;

    public ContactRequestListAdapter(List<UserAccount> contactRequests) {
        super();
        this.contactRequests = contactRequests;
        this.onRequestReplyListeners = new ArrayList<>();
    }

    public void addOnContactAcceptedListener(OnRequestReply listener) {
        this.onRequestReplyListeners.add(listener);
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact_request, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        UserAccount contact = contactRequests.get(position);

        holder.contactAvatar.setColorFilter(ColourGenerator.generateFromName(contact.getFirstName(), contact.getLastName()));
        holder.contactAvatarCharacter.setText(contact.getFirstName().substring(0, 1));
        holder.contactName.setText(contact.getFirstName() + " " + contact.getLastName());
    }

    @Override
    public int getItemCount() {
        return contactRequests == null ? 0 : contactRequests.size();
    }

    public void addContactRequest(UserAccount contactRequest) {
        contactRequests.add(contactRequest);
//        notifyItemInserted(contactRequests.size() - 1);
        notifyDataSetChanged();
    }

    private void removeContactRequest(int position) {
        contactRequests.remove(position);
//        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    private void notifyListenersOfContactAccepted(UserAccount contact) {
        for (OnRequestReply onRequestReplyListener : onRequestReplyListeners) {
            onRequestReplyListener.onContactAccepted(contact);
        }
    }

    private void notifyListenersOfContactRejected(UserAccount contact) {
        for (OnRequestReply onRequestReplyListener : onRequestReplyListeners) {
            onRequestReplyListener.onContactAccepted(contact);
        }
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView contactAvatar;
        public TextView contactAvatarCharacter;
        public TextView contactName;
        public ImageView accept;
        public ImageView reject;

        public Item(View view) {
            super(view);
            this.contactAvatar = view.findViewById(R.id.contact_avatar);
            this.contactAvatarCharacter = view.findViewById(R.id.contact_avatar_character);
            this.contactName = view.findViewById(R.id.name);
            this.accept = view.findViewById(R.id.accept);
            this.reject = view.findViewById(R.id.reject);

            // On click confirm acceptance of contact request
            this.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final UserAccount contactRequest = contactRequests.get(getAdapterPosition());
                    String name = contactRequest.getFirstName() + " " + contactRequest.getLastName();
                    new MaterialAlertDialogBuilder(v.getContext())
                            .setTitle("Confirm Accept")
                            .setMessage("Are you sure you want to accept the contact request from " + name + "?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: Add contact in database
                                    removeContactRequest(getAdapterPosition());
                                    notifyListenersOfContactAccepted(contactRequest);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

            // On click confirm rejection of contact request
            this.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final UserAccount contactRequest = contactRequests.get(getAdapterPosition());
                    String name = contactRequest.getFirstName() + " " + contactRequest.getLastName();
                    new MaterialAlertDialogBuilder(v.getContext())
                            .setTitle("Confirm Reject")
                            .setMessage("Are you sure you want to reject the contact request from " + name + "?")
                            .setPositiveButton("No", null)
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: Delete contact request from database
                                    removeContactRequest(getAdapterPosition());
                                    notifyListenersOfContactRejected(contactRequest);
                                }
                            })
                            .show();
                }
            });

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("CONTACTS", "Contact Request clicked.");
            Intent toViewContact = new Intent(v.getContext(), ViewContactActivity.class);
            toViewContact.putExtra(ViewContactActivity.CONTACT, contactRequests.get(getAdapterPosition()));
            v.getContext().startActivity(toViewContact);
        }
    }
}
