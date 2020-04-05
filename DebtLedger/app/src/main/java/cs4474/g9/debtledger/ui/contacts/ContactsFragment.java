package cs4474.g9.debtledger.ui.contacts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.ContactRequestManager;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.TransactionManager;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.BalanceCalculator;
import cs4474.g9.debtledger.ui.MainActivity;
import cs4474.g9.debtledger.ui.shared.LoadableRecyclerView;
import cs4474.g9.debtledger.ui.shared.OnActionButtonClickedListener;

public class ContactsFragment extends Fragment implements OnRequestReply, OnActionButtonClickedListener {

    private TextView contactRequestsTitle;
    private RecyclerView contactRequestsView;
    private ContactRequestListAdapter contactRequestsAdapter;

    private TextView contactsRequestedTitle;
    private RecyclerView contactsRequestedView;
    private ContactRequestedListAdapter contactsRequestedAdapter;

    private LoadableRecyclerView contactsView;
    private ContactListAdapter contactsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);
        setHasOptionsMenu(true);

        UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();

        // Get view references for requests to user list, and initialize list and adapter
        contactRequestsTitle = root.findViewById(R.id.request_to_me_title);
        contactRequestsView = root.findViewById(R.id.requests_to_me_list);
        contactRequestsView.setHasFixedSize(true);
        contactRequestsView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactRequestsAdapter = new ContactRequestListAdapter();
        contactRequestsAdapter.addOnContactAcceptedListener(this);
        contactRequestsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                // If there are any requests, make the title visible, otherwise gone (removed)
                if (contactRequestsAdapter.getItemCount() > 0) {
                    contactRequestsTitle.setVisibility(View.VISIBLE);
                } else {
                    contactRequestsTitle.setVisibility(View.GONE);
                }
            }
        });
        contactRequestsView.setAdapter(contactRequestsAdapter);

        // Get view references for requests by user list, and initialize list and adapter
        contactsRequestedTitle = root.findViewById(R.id.my_requests_title);
        contactsRequestedView = root.findViewById(R.id.my_requests_list);
        contactsRequestedView.setHasFixedSize(true);
        contactsRequestedView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsRequestedAdapter = new ContactRequestedListAdapter();
        contactsRequestedAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                // If there are any requests, make the title visible, otherwise gone (removed)
                if (contactsRequestedAdapter.getItemCount() > 0) {
                    contactsRequestedTitle.setVisibility(View.VISIBLE);
                } else {
                    contactsRequestedTitle.setVisibility(View.GONE);
                }
            }
        });
        contactsRequestedView.setAdapter(contactsRequestedAdapter);

        // Get view references contacts with balances list, and initialize list and adapter
        contactsView = root.findViewById(R.id.my_contacts_list);
        contactsView.setHasFixedSize(true);
        contactsView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsView.addOnActionButtonClickedClickListener(this);
        contactsAdapter = new ContactListAdapter();
        contactsView.setAdapter(contactsAdapter);

        // Make call to backend to get contacts
        makeRequestForContactsWithBalances(loggedInUser);

        // Make call to backend to get contacts requested by user
        makeRequestForPendingContactsRequested(loggedInUser);

        // Make call to backend to get contact requests
        makeRequestForPendingContactRequests(loggedInUser);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contacts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_contact:
                Log.d("CONTACTS", "Add Contact icon clicked.");
                AddContactDialog.createAddContactDialog(getActivity()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        makeRequestForPendingContactsRequested(LoginRepository.getInstance().getLoggedInUser());
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onContactAccepted(UserAccount contact) {
        makeRequestForContactsWithBalances(LoginRepository.getInstance().getLoggedInUser());

        // Update notification badge
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateContactsNotificationBadge();
        }
    }

    @Override
    public void onContactRejected(UserAccount contact) {
        // Update notification badge
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateContactsNotificationBadge();
        }
    }

    @Override
    public void onFailedToLoadActionButtonClicked() {
        makeRequestForContactsWithBalances(LoginRepository.getInstance().getLoggedInUser());
    }

    @Override
    public void onEmptyActionButtonClicked() {
        AddContactDialog.createAddContactDialog(getActivity()).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                makeRequestForPendingContactsRequested(LoginRepository.getInstance().getLoggedInUser());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel/terminate any requests that are still running or queued
        ConnectionAdapter.getInstance().getRequestQueue().cancelAll(hashCode());
    }

    private void makeRequestForContactsWithBalances(UserAccount loggedInUser) {
        contactsView.onBeginLoading();

        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + TransactionManager.BALANCES_END_POINT + "/" + loggedInUser.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("CONTACTS", response.toString());

                        try {
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("empty")) {
                                contactsAdapter.setContactsWithBalances(new ArrayList<>());
                            } else {
                                // On success
                                contactsAdapter.setContactsWithBalances(BalanceCalculator.parseBalancesFromJson(response));
                            }
                        } catch (Exception e) {
                            // On parse error, set contacts view to fail to finish loading mode
                            contactsView.onFailToFinishLoading();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, set contacts view to fail to finish loading mode
                        Log.d("CONTACTS", error.toString());
                        contactsView.onFailToFinishLoading();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

    private void makeRequestForPendingContactRequests(UserAccount loggedInUser) {
        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + ContactRequestManager.PENDING_END_POINT + "/" + loggedInUser.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("CONTACTS", response.toString());

                        try {
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("empty")) {
                                // Do nothing if there are no requests
                            } else {
                                // On success
                                contactRequestsAdapter.setContactRequests(ContactRequestManager.parseContactRequestsFromJson(response));
                            }
                        } catch (Exception e) {
                            // On parse error, display failed to load contact requests message
                            Toast.makeText(getContext(), R.string.failure_contact_requests, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display failed to load contact requests message
                        Log.d("CONTACTS", error.toString());
                        Toast.makeText(getContext(), R.string.failure_contact_requests, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

    private void makeRequestForPendingContactsRequested(UserAccount loggedInUser) {
        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + ContactRequestManager.PENDING_FROM_USER_END_POINT + "/" + loggedInUser.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("CONTACTS", response.toString());

                        try {
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("empty")) {
                                // Do nothing if there are no requests
                            } else {
                                // On success
                                contactsRequestedAdapter.setContactsRequested(ContactRequestManager.parseContactRequestsFromJson(response));
                            }
                        } catch (Exception e) {
                            // On parse error, display failed to load contact requests message
                            Toast.makeText(getContext(), R.string.failure_contact_requests, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display failed to load contact requests message
                        Log.d("CONTACTS", error.toString());
                        Toast.makeText(getContext(), R.string.failure_contact_requests, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }
}