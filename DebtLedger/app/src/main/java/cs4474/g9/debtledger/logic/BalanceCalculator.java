package cs4474.g9.debtledger.logic;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cs4474.g9.debtledger.data.UserAccountManager;
import cs4474.g9.debtledger.data.model.UserAccount;

public class BalanceCalculator {

    public static List<Pair<UserAccount, Integer>> parseOutstandingBalancesFromJson(JSONArray balancesJson) throws JSONException {
        List<Pair<UserAccount, Integer>> contactsWithBalances = new ArrayList<>();
        for (int i = 0; i < balancesJson.length(); i++) {
            int balance = balancesJson.getJSONObject(i).isNull("balance") ? 0
                    : (int) (balancesJson.getJSONObject(i).getDouble("balance") * 100);

            if (balance != 0) {
                contactsWithBalances.add(
                        Pair.create(
                                UserAccountManager.parseUserAccountFromJson(balancesJson.getJSONObject(i)),
                                balance
                        )
                );
            }
        }
        return contactsWithBalances;
    }

    public static List<Pair<UserAccount, Integer>> parseBalancesFromJson(JSONArray balancesJson) throws JSONException {
        List<Pair<UserAccount, Integer>> contactsWithBalances = new ArrayList<>();
        for (int i = 0; i < balancesJson.length(); i++) {
            contactsWithBalances.add(
                    Pair.create(
                            UserAccountManager.parseUserAccountFromJson(balancesJson.getJSONObject(i)),
                            balancesJson.getJSONObject(i).isNull("balance")
                                    ? 0
                                    : (int) (balancesJson.getJSONObject(i).getDouble("balance") * 100)
                    )
            );
        }
        return contactsWithBalances;
    }

    public List<Pair<UserAccount, Integer>> calculateBalances(/* TODO: Pass Information */) {
        // TODO: Implement, currently using dummy data
        List<Pair<UserAccount, Integer>> list = new ArrayList<>();


        list.add(
                Pair.create(
                        new UserAccount("John", "Doe", ""),
                        1100
                )
        );

        list.add(
                Pair.create(
                        new UserAccount("Thomas", "Morphew", ""),
                        -856
                )
        );

        list.add(
                Pair.create(
                        new UserAccount("Timothy", "Young", ""),
                        0
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

    public List<Pair<UserAccount, Integer>> calculateBalances(List<UserAccount> contacts) {
        // TODO: Implement, currently using dummy data
        List<Pair<UserAccount, Integer>> all = calculateBalances();

        List<Pair<UserAccount, Integer>> list = new ArrayList<>();
        for (Pair<UserAccount, Integer> pair : all) {
            if (contacts.contains(pair.first)) {
                list.add(pair);
            }
        }

        return list;
    }

}
