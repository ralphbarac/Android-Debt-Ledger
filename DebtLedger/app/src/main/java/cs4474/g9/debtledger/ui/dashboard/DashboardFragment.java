package cs4474.g9.debtledger.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.ui.transaction.CreateTransactionActivity;

public class DashboardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ViewPager viewPager = view.findViewById(R.id.view_pager);
        final TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        view.findViewById(R.id.create_transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCreateTransaction = new Intent(getActivity(), CreateTransactionActivity.class);
                startActivity(toCreateTransaction);
            }
        });

        DashboardPageAdapter pageAdapter = new DashboardPageAdapter(getChildFragmentManager());
        viewPager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_dashboard_owed_black);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_dashboard_all_black);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_dashboard_owe_black);

        // Initial selected tab should be with "All" Outstanding balances, which appears in the middle, or at position 1
        viewPager.setCurrentItem(1);
    }
}