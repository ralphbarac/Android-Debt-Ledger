package cs4474.g9.debtledger.data.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TransactionManager {
    public static final String BALANCES_END_POINT = "/transaction/balances";
    public static final String ADD_MULTIPLE_END_POINT = "/transaction/addmultiple";

    public static JSONObject createJsonFromTransaction(Transaction transaction) throws JSONException {
        JSONObject jsonTransaction = new JSONObject();
        jsonTransaction.put("debtor", transaction.getDebtor());
        jsonTransaction.put("creditor", transaction.getCreditor());
        jsonTransaction.put("description", transaction.getDescription());
        jsonTransaction.put("amount", transaction.getAmount());
        return jsonTransaction;
    }

    public static JSONArray createJsonArrayFromTransactions(List<Transaction> transactions) throws JSONException {
        JSONArray jsonTransactionList = new JSONArray();
        for (Transaction transaction : transactions) {
            jsonTransactionList.put(createJsonFromTransaction(transaction));
        }
        return jsonTransactionList;
    }

}
