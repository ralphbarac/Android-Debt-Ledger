package cs4474.g9.debtledger.ui.contacts;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.Transaction;

public class ContactHistoryListAdapter extends RecyclerView.Adapter<ContactHistoryListAdapter.Item> {

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

        holder.date.setText(
                temp.getDatetime().length() > 10
                        ? temp.getDatetime().substring(0, 10)
                        : temp.getDatetime()
        );


        if (temp.getCreditor() == LoginRepository.getInstance().getLoggedInUser().getId()) {
            holder.amount.setText("+$" + temp.getAmount().toString());
            holder.amount.setTextColor(Color.GREEN);
        } else {
            holder.amount.setText("-$" + temp.getAmount().toString());
            holder.amount.setTextColor(Color.RED);
        }
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
