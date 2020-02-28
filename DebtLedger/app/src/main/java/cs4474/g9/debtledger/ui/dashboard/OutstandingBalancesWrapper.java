package cs4474.g9.debtledger.ui.dashboard;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import cs4474.g9.debtledger.data.model.UserAccount;

public class OutstandingBalancesWrapper implements Serializable {

    private List<UserAccount> outstandingBalanceUserAccounts;
    private List<Integer> outstandingBalanceIntegers;

    public OutstandingBalancesWrapper(@NonNull List<Pair<UserAccount, Integer>> outstandingBalances) {
        this.outstandingBalanceUserAccounts = new ArrayList<>(outstandingBalances.size());
        this.outstandingBalanceIntegers = new ArrayList<>(outstandingBalances.size());

        for (int i = 0; i < outstandingBalances.size(); i++) {
            Pair<UserAccount, Integer> outstandingBalance = outstandingBalances.get(i);
            this.outstandingBalanceUserAccounts.add(outstandingBalance.first);
            this.outstandingBalanceIntegers.add(outstandingBalance.second);
        }
    }

    public List<Pair<UserAccount, Integer>> getOutstandingBalances() {
        List<Pair<UserAccount, Integer>> outstandingBalances = new ArrayList<>(outstandingBalanceUserAccounts.size());

        for (int i = 0; i < outstandingBalanceUserAccounts.size(); i++) {
            outstandingBalances.add(Pair.create(outstandingBalanceUserAccounts.get(i), outstandingBalanceIntegers.get(i)));
        }

        return outstandingBalances;
    }
}
