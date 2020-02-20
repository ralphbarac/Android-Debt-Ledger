package cs4474.g9.debtledger.ui.dashboard;

import android.util.Pair;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import cs4474.g9.debtledger.data.model.UserAccount;

public class OutstandingBalancesWrapper implements Serializable {

    // TODO: Pair is not serializable
    private List<Pair<UserAccount, Integer>> outstandingBalances;

    public OutstandingBalancesWrapper(@NonNull List<Pair<UserAccount, Integer>> outstandingBalances) {
        this.outstandingBalances = outstandingBalances;
    }

    public List<Pair<UserAccount, Integer>> getOutstandingBalances() {
        return outstandingBalances;
    }
}
