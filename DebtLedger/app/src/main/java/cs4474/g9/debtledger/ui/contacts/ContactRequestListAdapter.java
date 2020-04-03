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

public class ContactRequestListAdapter extends RecyclerView.Adapter<ContactRequestListAdapter.Item> {

    private List<UserAccount> contactRequests;
    private List<OnRequestReply> onRequestReplyListeners;

    public ContactRequestListAdapter() {
        super();
        this.contactRequests = new ArrayList<>();
        this.onRequestReplyListeners = new ArrayList<>();
    }

    public void addOnContactAcceptedListener(OnRequestReply listener) {
        this.onRequestReplyListeners.add(listener);
    }

    public void setContactRequests(List<UserAccount> contactRequests) {
        this.contactRequests = contactRequests;
        notifyDataSetChanged();
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
            onRequestReplyListener.onContactRejected(contact);
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
                                    makeRequestToAcceptContact(
                                            LoginRepository.getInstance().getLoggedInUser(),
                                            contactRequest
                                    );
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
                                    makeRequestToDenyContact(
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
            Log.d("CONTACTS", "Contact Request clicked.");
            Intent toViewContact = new Intent(v.getContext(), ViewContactActivity.class);
            toViewContact.putExtra(ViewContactActivity.CONTACT, contactRequests.get(getAdapterPosition()));
            v.getContext().startActivity(toViewContact);
        }

        private void makeRequestToAcceptContact(UserAccount loggedInUser, UserAccount contactRequest) {
            RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                    ConnectionAdapter.BASE_URL + ContactRequestManager.ACCEPT_END_POINT + "/" + contactRequest.getId() + "/" + loggedInUser.getId() + "/",
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
                                    removeContactRequest(getAdapterPosition());
                                    notifyListenersOfContactAccepted(contactRequest);
                                }
                            } catch (Exception e) {
                                // On parse error, display failed to accept contact request message
                                Toast.makeText(accept.getContext(), R.string.failure_accept_contact_request, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // On error, display failed to accept contact request message
                            Log.d("CONTACTS", error.toString());
                            Toast.makeText(accept.getContext(), R.string.failure_accept_contact_request, Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
        }

        private void makeRequestToDenyContact(UserAccount loggedInUser, UserAccount contactRequest) {
            RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                    ConnectionAdapter.BASE_URL + ContactRequestManager.DENY_END_POINT + "/" + contactRequest.getId() + "/" + loggedInUser.getId() + "/",
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
                                    removeContactRequest(getAdapterPosition());
                                    notifyListenersOfContactRejected(contactRequest);
                                }
                            } catch (Exception e) {
                                // On parse error, display failed to deny contact request message
                                Toast.makeText(reject.getContext(), R.string.failure_deny_contact_request, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // On error, display failed to deny contact request message
                            Log.d("CONTACTS", error.toString());
                            Toast.makeText(reject.getContext(), R.string.failure_deny_contact_request, Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
        }
    }
}
