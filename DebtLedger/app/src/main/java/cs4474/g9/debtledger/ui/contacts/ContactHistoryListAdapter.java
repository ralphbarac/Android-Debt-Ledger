package cs4474.g9.debtledger.ui.contacts;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.ViewContactActivity;
import cs4474.g9.debtledger.data.model.Transaction;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class ContactHistoryListAdapter  extends RecyclerView.Adapter<ContactHistoryListAdapter.Item> {

    private List<Transaction> transactionList;

    // Create empty list
    public ContactHistoryListAdapter() {
        transactionList = new ArrayList<>();
    }

    // Set new list to
    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHistoryListAdapter.Item holder, int position) {
        Transaction temp = transactionList.get(position);
        holder.description.setText(temp.getDescription());
        holder.amount.setText(temp.getAmount().toString());
        holder.date.setText(temp.getDatetime());
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_transaction_history, parent, false);
        return new Item(view);
    }

    @Override
    public int getItemCount() {
        return transactionList == null ? 0 : transactionList.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView date;
        public TextView amount;

        public Item(View view) {
            super(view);
            this.description = view.findViewById(R.id.description);
            this.date = view.findViewById(R.id.date);
            this.amount = view.findViewById(R.id.amount);

        }

    }
}
