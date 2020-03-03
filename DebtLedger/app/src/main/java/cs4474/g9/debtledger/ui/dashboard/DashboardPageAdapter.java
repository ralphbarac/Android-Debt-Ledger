package cs4474.g9.debtledger.ui.dashboard;

import android.os.Bundle;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.BalanceCalculator;

public class DashboardPageAdapter extends FragmentStatePagerAdapter {

    public DashboardPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new DashboardPageFragment();
        Bundle args = new Bundle();
        BalanceCalculator calculator = new BalanceCalculator();
        List<Pair<UserAccount, Integer>> outstandingBalances = calculator.calculateOutstandingBalances();

        List<Pair<UserAccount, Integer>> filteredBalances = new ArrayList<>();
        for (int i = 0; i < outstandingBalances.size(); i++) {
            if (position == 0) {
                if (outstandingBalances.get(i).second > 0) {
                    filteredBalances.add(outstandingBalances.get(i));
                }
            } else if (position == 2) {
                if (outstandingBalances.get(i).second < 0) {
                    filteredBalances.add(outstandingBalances.get(i));
                }
            } else {
                filteredBalances.add(outstandingBalances.get(i));
            }
        }

        args.putSerializable(DashboardPageFragment.OUTSTANDING_BALANCES, new OutstandingBalancesWrapper(filteredBalances));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
//        if (position == 0) {
//            return "OWED";
//        } else if (position == 2) {
//            return "OWE";
//        } else {
//            return "ALL";
//        }
    }

}