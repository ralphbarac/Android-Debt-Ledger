package cs4474.g9.debtledger.logic;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cs4474.g9.debtledger.data.UserAccountManager;
import cs4474.g9.debtledger.data.model.UserAccount;

public class BalanceCalculator {

    public static List<Pair<UserAccount, Long>> parseOutstandingBalancesFromJson(JSONArray balancesJson) throws JSONException {
        List<Pair<UserAccount, Long>> contactsWithBalances = new ArrayList<>();
        for (int i = 0; i < balancesJson.length(); i++) {
            long balance = balancesJson.getJSONObject(i).isNull("balance") ? 0
                    : (long) (balancesJson.getJSONObject(i).getDouble("balance") * 100);

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

    public static List<Pair<UserAccount, Long>> parseBalancesFromJson(JSONArray balancesJson) throws JSONException {
        List<Pair<UserAccount, Long>> contactsWithBalances = new ArrayList<>();
        for (int i = 0; i < balancesJson.length(); i++) {
            contactsWithBalances.add(
                    Pair.create(
                            UserAccountManager.parseUserAccountFromJson(balancesJson.getJSONObject(i)),
                            balancesJson.getJSONObject(i).isNull("balance")
                                    ? 0
                                    : (long) (balancesJson.getJSONObject(i).getDouble("balance") * 100)
                    )
            );
        }
        return contactsWithBalances;
    }
}
