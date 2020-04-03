package cs4474.g9.debtledger.ui.contacts;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.ContactRequestManager;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.UserAccountManager;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class AddContactDialog {

    private AlertDialog dialog;

    private TextInputEditText emailInput;
    private ImageButton searchButton;
    private ProgressBar searchLoading;

    private View miscError;
    private View contactContainer;
    private ImageView contactAvatar;
    private TextView contactAvatarCharacter;
    private TextView contactName;

    private Button sendRequestButton;

    private UserAccount contact;

    private AddContactDialog(Activity activity) {
        // Use the Builder class for convenient dialog construction
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        // Pass null as the parent view because its going in the dialog layout
        View dialogContents = activity.getLayoutInflater().inflate(R.layout.dialog_add_contact, null);


        // Inflate and set the layout for the dialog
        builder.setTitle("Add Contact")
                .setView(dialogContents)
                // Add action buttons
                .setPositiveButton("Send Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Send a request to the database and update the contacts page
                        makeRequestToAddContactRequest(
                                LoginRepository.getInstance().getLoggedInUser(),
                                contact
                        );
                    }
                })
                .setNegativeButton("Cancel", null);

        dialog = builder.create();

        emailInput = dialogContents.findViewById(R.id.email);
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check for valid input
                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    searchButton.setEnabled(true);
                    searchButton.setColorFilter(ContextCompat.getColor(dialog.getContext(), R.color.colorPrimary));
                } else {
                    emailInput.setError(dialog.getContext().getString(R.string.invalid_email));
                    searchButton.setEnabled(false);
                    searchButton.setColorFilter(ContextCompat.getColor(dialog.getContext(), R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchButton = dialogContents.findViewById(R.id.search);
        searchButton.setEnabled(false);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequestForUserByEmail(emailInput.getText().toString());
            }
        });
        searchLoading = dialogContents.findViewById(R.id.search_loading);

        miscError = dialogContents.findViewById(R.id.misc_error_container);
        contactContainer = dialogContents.findViewById(R.id.contact_container);
        contactAvatar = dialogContents.findViewById(R.id.contact_avatar);
        contactAvatarCharacter = dialogContents.findViewById(R.id.contact_avatar_character);
        contactName = dialogContents.findViewById(R.id.name);

        dialog.show();

        sendRequestButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        sendRequestButton.setEnabled(false);
    }

    public static Dialog createAddContactDialog(Activity activity) {
        return new AddContactDialog(activity).dialog;
    }

    private void makeRequestForUserByEmail(String email) {
        // Start loading animation
        searchLoading.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.INVISIBLE);

        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + UserAccountManager.GET_USER_BY_EMAIL_END_POINT + "/" + email + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("CONTACTS", response.toString());

                        try {
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("missing")) {
                                // If contact not found, display error and disable button
                                contactContainer.setVisibility(View.GONE);
                                miscError.setVisibility(View.VISIBLE);
                                sendRequestButton.setEnabled(false);
                            } else {
                                // On success, set contact properties, make it visible, and enable button
                                contact = UserAccountManager.parseUserAccountFromJson(response.getJSONObject(0));
                                contactAvatar.setColorFilter(ColourGenerator.generateFromName(contact.getFirstName(), contact.getLastName()));
                                contactAvatarCharacter.setText(contact.getFirstName().substring(0, 1));
                                contactName.setText(contact.getFirstName() + " " + contact.getLastName());

                                contactContainer.setVisibility(View.VISIBLE);
                                miscError.setVisibility(View.GONE);

                                sendRequestButton.setEnabled(true);
                            }
                        } catch (Exception e) {
                            // On parse error, display failure to get contact
                            Toast.makeText(dialog.getContext(), R.string.failure_contacts, Toast.LENGTH_SHORT).show();
                        } finally {
                            searchLoading.setVisibility(View.GONE);
                            searchButton.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display failure to get contact
                        Log.d("CONTACTS", error.toString());
                        Toast.makeText(dialog.getContext(), R.string.failure_contacts, Toast.LENGTH_SHORT).show();
                        searchLoading.setVisibility(View.GONE);
                        searchButton.setVisibility(View.VISIBLE);
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, dialog.getContext().hashCode());
    }

    private void makeRequestToAddContactRequest(UserAccount loggedInUser, UserAccount contact) {
        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + ContactRequestManager.ADD_END_POINT + "/" + loggedInUser.getId() + "/" + contact.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("DASHBOARD", response.toString());

                        try {
                            if (response.getJSONObject(0).has("error") ||
                                    response.getJSONObject(0).has("failure")) {
                                throw new Exception();
                            }

                            // On success
                            dialog.dismiss();
                        } catch (Exception e) {
                            // On parse error, display failure to add contact
                            Toast.makeText(dialog.getContext(), R.string.failure_add_contact, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display failure to add contact
                        Log.d("CONTACTS", error.toString());
                        Toast.makeText(dialog.getContext(), R.string.failure_add_contact, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, dialog.getContext().hashCode());
    }
}
