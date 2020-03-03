package cs4474.g9.debtledger.ui.contacts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

public class ContactsFragment extends Fragment {

    private RecyclerView contactRequestsView;
    private RecyclerView.Adapter contactRequestsAdapter;

    private RecyclerView contactsRequestedView;
    private RecyclerView.Adapter contactsRequestedAdapter;

    private RecyclerView contactsView;
    private RecyclerView.Adapter contactsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);
        setHasOptionsMenu(true);

        Result result;
        ContactRequestManager requestManager = new ContactRequestManager();
        UserAccount loggedInUser = LoginRepository.getInstance(getContext()).getLoggedInUser();

        List<UserAccount> requestsForUser = new ArrayList<>();
        result = requestManager.getAllContactRequestsFor(loggedInUser);
        if (result instanceof Result.Success) {
            requestsForUser = (List<UserAccount>) ((Result.Success) result).getData();
        } else {
            Toast.makeText(getContext(), R.string.failure_contact_requests, Toast.LENGTH_SHORT).show();
        }
        contactRequestsView = root.findViewById(R.id.requests_to_me_list);
        contactRequestsView.setHasFixedSize(true);
        contactRequestsView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactRequestsAdapter = new ContactRequestListAdapter(requestsForUser);
        contactRequestsView.setAdapter(contactRequestsAdapter);

        List<UserAccount> requestsByUser = new ArrayList<>();
        result = requestManager.getAllContactRequestsFor(loggedInUser);
        if (result instanceof Result.Success) {
            requestsByUser = (List<UserAccount>) ((Result.Success) result).getData();
        } else {
            Toast.makeText(getContext(), R.string.failure_contact_requests, Toast.LENGTH_SHORT).show();
        }
        contactsRequestedView = root.findViewById(R.id.my_requests_list);
        contactsRequestedView.setHasFixedSize(true);
        contactsRequestedView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsRequestedAdapter = new ContactRequestedListAdapter(requestsByUser);
        contactsRequestedView.setAdapter(contactsRequestedAdapter);

        BalanceCalculator calculator = new BalanceCalculator();
        contactsView = root.findViewById(R.id.my_contacts_list);
        contactsView.setHasFixedSize(true);
        contactsView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsAdapter = new ContactListAdapter(calculator.calculateBalances());
        contactsView.setAdapter(contactsAdapter);

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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}