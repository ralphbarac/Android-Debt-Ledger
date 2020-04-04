package cs4474.g9.debtledger.data.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    public static final String BALANCES_END_POINT = "/transaction/balances";
    public static final String CONTACT_END_POINT = "/transaction/contact";
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

    public static List<Transaction> createListArrayFromJsonArray(JSONArray jsonArray) throws JSONException{

        List<Transaction> list = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject transactionJSON = jsonArray.getJSONObject(i);

            Transaction temp = new Transaction(transactionJSON.getLong("id"),
                    transactionJSON.getLong("debtor"),
                    transactionJSON.getLong("creditor"),
                    transactionJSON.getString("description"),
                    new BigDecimal(transactionJSON.getString("amount")),
                    transactionJSON.getString("date"));
            list.add(temp);
        }
        Log.d("VIEW CONTACT", list.toString());
        return list;
    }
}
