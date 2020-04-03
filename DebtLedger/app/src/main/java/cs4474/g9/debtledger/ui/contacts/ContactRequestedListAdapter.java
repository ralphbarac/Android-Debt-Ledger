package cs4474.g9.debtledger.ui.contacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.ViewContactActivity;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.ContactRequestManager;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class ContactRequestedListAdapter extends RecyclerView.Adapter<ContactRequestedListAdapter.Item> {

    private List<UserAccount> contactsRequested;

    public ContactRequestedListAdapter() {
        super();
        this.contactsRequested = new ArrayList<>();
    }

    public void setContactsRequested(List<UserAccount> contactsRequested) {
        this.contactsRequested = contactsRequested;
        notifyDataSetChanged();
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
                                    makeRequestToDeleteContactRequest(
                                            LoginRepository.getInstance().getLoggedInUser(),
                                            contactRequest
                                    );
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

        private void makeRequestToDeleteContactRequest(UserAccount loggedInUser, UserAccount contactRequest) {
            RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                    ConnectionAdapter.BASE_URL + ContactRequestManager.DELETE_END_POINT + "/" + loggedInUser.getId() + "/" + contactRequest.getId() + "/",
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("CONTACTS", response.toString());

                            try {
                                if (response.getJSONObject(0).has("error") ||
                                        response.getJSONObject(0).has("failure")) {
                                    throw new Exception();
                                } else {
                                    // On success
                                    response.getJSONObject(0).get("success");
                                    removeRequestedContact(getAdapterPosition());
                                }
                            } catch (Exception e) {
                                // On parse error, display failed to cancel contact request message
                                Toast.makeText(cancel.getContext(), R.string.failure_cancel_contact_request, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // On error, display failed to cancel contact request message
                            Toast.makeText(cancel.getContext(), R.string.failure_cancel_contact_request, Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
        }
    }
}
