package cs4474.g9.debtledger.ui.contacts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
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

        // TODO: Contact Requests query system
        List<UserAccount> contactRequests = new ArrayList<>();
        contactRequests.add(new UserAccount("Randal", "Smith", "rmsith@uwo.ca"));
        contactRequestsView = root.findViewById(R.id.requests_to_me_list);
        contactRequestsView.setHasFixedSize(true);
        contactRequestsView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactRequestsAdapter = new ContactRequestListAdapter(contactRequests);
        contactRequestsView.setAdapter(contactRequestsAdapter);

        // TODO: Contacts Requested query system
        List<UserAccount> contactsRequested = new ArrayList<>();
        contactsRequested.add(new UserAccount("Mike", "Lee", "mlee@uwo.ca"));
        contactsRequestedView = root.findViewById(R.id.my_requests_list);
        contactsRequestedView.setHasFixedSize(true);
        contactsRequestedView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsRequestedAdapter = new ContactRequestedListAdapter(contactsRequested);
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