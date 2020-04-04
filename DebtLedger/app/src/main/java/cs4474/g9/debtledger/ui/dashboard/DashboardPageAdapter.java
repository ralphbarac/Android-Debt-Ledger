package cs4474.g9.debtledger.ui.dashboard;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.ui.shared.OnActionButtonClickedListener;

public class DashboardPageAdapter extends FragmentStatePagerAdapter {

    private List<DashboardPageFragment> dashboardPages = new ArrayList<>();

    private List<Pair<UserAccount, Long>> filteredPositiveBalances = null;
    private List<Pair<UserAccount, Long>> allBalances = null;
    private List<Pair<UserAccount, Long>> filteredNegativeBalances = null;

    private boolean isInFailedToLoadState = false;

    private OnActionButtonClickedListener actionButtonClickedListener;

    public DashboardPageAdapter(FragmentManager fm, OnActionButtonClickedListener listener) {
        super(fm);
        this.actionButtonClickedListener = listener;

        // Add placeholders for each page
        dashboardPages.add(null);
        dashboardPages.add(null);
        dashboardPages.add(null);
    }

    @Override
    public Fragment getItem(int position) {
        String label;
        if (position == 0) {
            label = "You're Owed";
        } else if (position == 2) {
            label = "You Owe";
        } else {
            label = "Balance";
        }
        DashboardPageFragment fragment = new DashboardPageFragment(label, actionButtonClickedListener);
        dashboardPages.set(position, fragment);

        // Depending on  position, if data is available, set corresponding data
        if (position == 0) {
            if (filteredPositiveBalances != null) {
                fragment.setData(filteredPositiveBalances);
            }
        } else if (position == 1) {
            if (allBalances != null) {
                fragment.setData(allBalances);
            }
        } else if (position == 2) {
            if (filteredNegativeBalances != null) {
                fragment.setData(filteredNegativeBalances);
            }
        }

        // If current state is failed to load, set corresponding state
        if (isInFailedToLoadState) {
            fragment.onFailToFinishLoading();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    public void onBeginLoading() {
        isInFailedToLoadState = false;

        // Only if fragments are loaded (non-null), set on being loading
        for (DashboardPageFragment dashboardPage : dashboardPages) {
            if (dashboardPage != null) {
                dashboardPage.onBeginLoading();
            }
        }
    }

    public void setData(List<Pair<UserAccount, Long>> outstandingBalances) {
        isInFailedToLoadState = false;

        allBalances = outstandingBalances;
        filteredPositiveBalances = new ArrayList<>();
        filteredNegativeBalances = new ArrayList<>();

        // Filter positive and negative balances respectively
        for (int i = 0; i < outstandingBalances.size(); i++) {
            if (outstandingBalances.get(i).second > 0) {
                filteredPositiveBalances.add(outstandingBalances.get(i));
            }
            if (outstandingBalances.get(i).second < 0) {
                filteredNegativeBalances.add(outstandingBalances.get(i));
            }
        }

        // Only if fragments are loaded (non-null), setData to corresponding data
        if (dashboardPages.get(0) != null) {
            dashboardPages.get(0).setData(filteredPositiveBalances);
        }
        if (dashboardPages.get(1) != null) {
            dashboardPages.get(1).setData(allBalances);
        }
        if (dashboardPages.get(2) != null) {
            dashboardPages.get(2).setData(filteredNegativeBalances);
        }
    }

    public void onFailToFinishLoading() {
        isInFailedToLoadState = true;

        // Only if fragments are loaded (non-null), set on fail to load
        for (DashboardPageFragment dashboardPage : dashboardPages) {
            if (dashboardPage != null) {
                dashboardPage.onFailToFinishLoading();
            }
        }
    }

}