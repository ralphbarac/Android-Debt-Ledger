package cs4474.g9.debtledger.ui.dashboard;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;

public class DashboardPageFragment extends Fragment {

    public static final String OUTSTANDING_BALANCES = "outstanding_balances";

    private RecyclerView outstandingBalancesView;
    private RecyclerView.Adapter outstandingBalancesAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard_page, container, false);
        final TextView textView = root.findViewById(R.id.balance);

        OutstandingBalancesWrapper wrapper = (OutstandingBalancesWrapper) getArguments().get(OUTSTANDING_BALANCES);
        List<Pair<UserAccount, Integer>> outstandingBalances = wrapper.getOutstandingBalances();

        Integer total = 0;
        for (int i = 0; i < outstandingBalances.size(); i++) {
            total += outstandingBalances.get(i).second;
        }
        textView.setText(String.format(Locale.CANADA, "Balance: %s$%.2f", total < 0 ? "-" : "+", Math.abs(total) / 100.0));

        outstandingBalancesView = root.findViewById(R.id.outstanding_balance_list);
        outstandingBalancesView.setHasFixedSize(true);
        outstandingBalancesView.setLayoutManager(new LinearLayoutManager(getContext()));

        // TODO: How to propagate logged in user information...
        outstandingBalancesAdapter = new OutstandingBalanceListAdapter(LoginRepository.getInstance().getLoggedInUser(), outstandingBalances);
        outstandingBalancesView.setAdapter(outstandingBalancesAdapter);

        return root;
    }

}
