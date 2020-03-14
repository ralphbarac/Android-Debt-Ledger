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
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.ui.shared.LoadableRecyclerView;
import cs4474.g9.debtledger.ui.shared.OnActionButtonClickedListener;

public class DashboardPageFragment extends Fragment {

    private List<Pair<UserAccount, Integer>> outstandingBalances;

    private TextView balanceTextView;
    private LoadableRecyclerView outstandingBalancesView;
    private OutstandingBalanceListAdapter outstandingBalancesAdapter;

    private boolean isCreated = false;
    private boolean isInFailedToLoadState = false;

    private OnActionButtonClickedListener actionButtonClickedListener;

    public DashboardPageFragment(OnActionButtonClickedListener listener) {
        this.actionButtonClickedListener = listener;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard_page, container, false);
        balanceTextView = root.findViewById(R.id.balance);
        balanceTextView.setText("...");

        outstandingBalancesView = root.findViewById(R.id.outstanding_balance_list);
        outstandingBalancesView.setHasFixedSize(true);
        outstandingBalancesView.setLayoutManager(new LinearLayoutManager(getContext()));
        outstandingBalancesView.addOnActionButtonClickedClickListener(actionButtonClickedListener);

        outstandingBalancesAdapter = new OutstandingBalanceListAdapter(LoginRepository.getInstance(getContext()).getLoggedInUser());
        outstandingBalancesView.setAdapter(outstandingBalancesAdapter);

        isCreated = true;

        if (isInFailedToLoadState) {
            outstandingBalancesView.onFailToFinishLoading();
        } else {
            // Since setData() may be called before onCreateView(), need to call setData or will load forever
            if (outstandingBalances != null) {
                setData(outstandingBalances);
            }
        }


        return root;
    }

    public void onBeginLoading() {
        isInFailedToLoadState = false;

        // onCreateView() may not have been called, and thus view not created yet, check if created
        if (isCreated) {
            outstandingBalancesView.onBeginLoading();
        }
    }

    public void setData(List<Pair<UserAccount, Integer>> outstandingBalances) {
        this.isInFailedToLoadState = false;
        this.outstandingBalances = outstandingBalances;

        // onCreateView() may not have been called, and thus view not created yet, check if created
        if (isCreated) {
            outstandingBalancesAdapter.setOutstandingBalances(outstandingBalances);

            Integer total = 0;
            for (int i = 0; i < outstandingBalances.size(); i++) {
                total += outstandingBalances.get(i).second;
            }
            balanceTextView.setText(String.format(Locale.CANADA, "Balance: %s$%.2f", total < 0 ? "-" : "+", Math.abs(total) / 100.0));
        }
    }

    public void onFailToFinishLoading() {
        isInFailedToLoadState = true;

        // onCreateView() may not have been called, and thus view not created yet, check if created
        if (isCreated) {
            outstandingBalancesView.onFailToFinishLoading();
        }
    }

}
