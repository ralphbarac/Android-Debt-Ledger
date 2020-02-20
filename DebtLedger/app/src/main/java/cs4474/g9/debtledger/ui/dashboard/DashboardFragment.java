package cs4474.g9.debtledger.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import cs4474.g9.debtledger.R;

public class DashboardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ViewPager viewPager = view.findViewById(R.id.view_pager);

        DashboardPageAdapter pageAdapter = new DashboardPageAdapter(getChildFragmentManager());
        viewPager.setAdapter(pageAdapter);

        // Initial selected tab should be with "All" Outstanding balances, which appears in the middle, or at position 1
        viewPager.setCurrentItem(1);
    }
}