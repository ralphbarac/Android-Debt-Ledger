package cs4474.g9.debtledger.ui.contacts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ContactRequestManager;
import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.BalanceCalculator;
import cs4474.g9.debtledger.ui.MainActivity;
import cs4474.g9.debtledger.ui.shared.LoadableRecyclerView;

public class ContactsFragment extends Fragment implements OnRequestReply {

    private TextView contactRequestsTitle;
    private RecyclerView contactRequestsView;
    private ContactRequestListAdapter contactRequestsAdapter;

    private TextView contactsRequestedTitle;
    private RecyclerView contactsRequestedView;
    private ContactRequestedListAdapter contactsRequestedAdapter;

    private LoadableRecyclerView contactsView;
    private ContactListAdapter contactsAdapter;

    private AsyncTask<UserAccount, Void, Result> getContactsWithBalancesProcess;
    private AsyncTask<UserAccount, Void, Result> getContactRequestsProcess;
    private AsyncTask<UserAccount, Void, Result> getContactsRequestedProcess;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);
        setHasOptionsMenu(true);

        UserAccount loggedInUser = LoginRepository.getInstance(getContext()).getLoggedInUser();

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
        contactsAdapter = new ContactListAdapter();
        contactsView.setAdapter(contactsAdapter);

        // Make call to backend to get contacts
        getContactsWithBalancesProcess = new GetContactsWithBalancesProcess();
        getContactsWithBalancesProcess.execute(loggedInUser);

        // Make call to backend to get contacts requested by user
        getContactsRequestedProcess = new GetContactsRequested();
        getContactsRequestedProcess.execute(loggedInUser);

        // Make call to backend to get contact requests
        getContactRequestsProcess = new GetContactRequests();
        getContactRequestsProcess.execute(loggedInUser);

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
                // TODO: Open Add Contact Dialog
                Log.d("CONTACTS", "Add Contact icon clicked.");
                AddContactDialog addContact = new AddContactDialog();
                addContact.show(getActivity().getSupportFragmentManager(),"");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onContactAccepted(UserAccount contact) {
        contactsAdapter.addNewContact(contact);

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
    public void onDestroy() {
        super.onDestroy();

        // Cancel/terminate any processes that are still running
        if (getContactsWithBalancesProcess != null) {
            getContactsWithBalancesProcess.cancel(true);
        }
        if (getContactRequestsProcess != null) {
            getContactsRequestedProcess.cancel(true);
        }
        if (getContactsRequestedProcess != null) {
            getContactsRequestedProcess.cancel(true);
        }
    }

    private final class GetContactsWithBalancesProcess extends AsyncTask<UserAccount, Void, Result> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            contactsView.onBeginLoading();
        }

        @Override
        protected Result doInBackground(UserAccount... params) {
            UserAccount loggedInUser = params[0];
            BalanceCalculator calculator = new BalanceCalculator();
            // TODO: Remove sleep
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(250, 1500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new Result.Success(calculator.calculateBalances());
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            if (result instanceof Result.Success) {
                List<Pair<UserAccount, Integer>> contactsWithBalances;
                contactsWithBalances = (List<Pair<UserAccount, Integer>>) ((Result.Success) result).getData();
                contactsAdapter.setContactsWithBalances(contactsWithBalances);
            } else {
                contactsView.onFailToFinishLoading();
            }
        }

    }

    private final class GetContactRequests extends AsyncTask<UserAccount, Void, Result> {

        @Override
        protected Result doInBackground(UserAccount... params) {
            UserAccount loggedInUser = params[0];
            ContactRequestManager requestManager = new ContactRequestManager();
            // TODO: Remove sleep
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(250, 1500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return requestManager.getAllContactRequestsFor(loggedInUser);
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            if (result instanceof Result.Success) {
                List<UserAccount> requestsForUser;
                requestsForUser = (List<UserAccount>) ((Result.Success) result).getData();
                contactRequestsAdapter.setContactRequests(requestsForUser);
            } else {
                Toast.makeText(getContext(), R.string.failure_contact_requests, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private final class GetContactsRequested extends AsyncTask<UserAccount, Void, Result> {

        @Override
        protected Result doInBackground(UserAccount... params) {
            UserAccount loggedInUser = params[0];
            ContactRequestManager requestManager = new ContactRequestManager();
            // TODO: Remove sleep
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(250, 1500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return requestManager.getAllContectRequestsBy(loggedInUser);
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            if (result instanceof Result.Success) {
                List<UserAccount> requestsByUser;
                requestsByUser = (List<UserAccount>) ((Result.Success) result).getData();
                contactsRequestedAdapter.setContactsRequested(requestsByUser);
            } else {
                Toast.makeText(getContext(), R.string.failure_contact_requests, Toast.LENGTH_SHORT).show();
            }
        }

    }
}