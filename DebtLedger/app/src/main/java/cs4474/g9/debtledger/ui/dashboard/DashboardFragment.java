package cs4474.g9.debtledger.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.ui.transaction.CreateTransactionActivity;

public class DashboardFragment extends Fragment {

    private final int SEMI_TRANSPARENT = 130;
    private final int SOLID = 255;

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

        // Creating custom views for tab icons
        ImageView owedTabView = new ImageView(getContext());
        owedTabView.setImageResource(R.drawable.ic_dashboard_owed_black);
        owedTabView.setColorFilter(Color.WHITE);
        owedTabView.setImageAlpha(SEMI_TRANSPARENT);
        tabLayout.getTabAt(0).setCustomView(owedTabView);

        ImageView allTabView = new ImageView(getContext());
        allTabView.setImageResource(R.drawable.ic_dashboard_all_black);
        allTabView.setColorFilter(Color.WHITE);
        allTabView.setImageAlpha(SEMI_TRANSPARENT);
        tabLayout.getTabAt(1).setCustomView(allTabView);

        ImageView oweTabView = new ImageView(getContext());
        oweTabView.setImageResource(R.drawable.ic_dashboard_owe_black);
        oweTabView.setColorFilter(Color.WHITE);
        oweTabView.setImageAlpha(SEMI_TRANSPARENT);
        tabLayout.getTabAt(2).setCustomView(oweTabView);

        // Listener to change state between solid and semi-transparent of icons on select/un-select
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView();
                if (tabView instanceof ImageView) {
                    ImageView imageView = (ImageView) tabView;
                    imageView.setImageAlpha(SOLID);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView();
                if (tabView instanceof ImageView) {
                    ImageView imageView = (ImageView) tabView;
                    imageView.setImageAlpha(SEMI_TRANSPARENT);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Initial selected tab should be with "All" Outstanding balances, which appears in the middle, or at position 1
        viewPager.setCurrentItem(1);
    }
}