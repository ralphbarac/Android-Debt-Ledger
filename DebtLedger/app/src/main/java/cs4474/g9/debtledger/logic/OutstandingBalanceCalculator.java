package cs4474.g9.debtledger.logic;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import cs4474.g9.debtledger.data.model.UserAccount;

public class OutstandingBalanceCalculator {

    public List<Pair<UserAccount, Integer>> calculateOutstandingBalances(/* TODO: Pass Information */) {
        List<Pair<UserAccount, Integer>> list = new ArrayList<>();

        list.add(
                Pair.create(
                        new UserAccount("Thomas", "Morphew", ""),
                        -856
                )
        );

        list.add(
                Pair.create(
                        new UserAccount("John", "Doe", ""),
                        1100
                )
        );

        list.add(
                Pair.create(
                        new UserAccount("Will", "Smith", ""),
                        -350
                )
        );


        return list;
    }


}
