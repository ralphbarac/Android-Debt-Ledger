package com.example.thomasmorphew.roomies;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FriendAndTransactionChainAdapter extends ArrayAdapter<String>{


    public FriendAndTransactionChainAdapter(@NonNull Context context, String[] values) {
        super(context, R.layout.friend_and_transaction_chain_layout, values);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Some stupid class
        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.friend_and_transaction_chain_layout, parent, false);

        String string = ((String) getItem(position));
        String friendDetails = string.split(" ", 2)[1];
        String friendAmount = string.split(" ", 2)[0];

        TextView textView1 = theView.findViewById(R.id.textViewFriendName);
        TextView textView2 = theView.findViewById(R.id.textViewTransactionChain);

        double amount = Double.valueOf(friendAmount);

        if(amount < 0){
            textView2.setTextColor(Color.RED);
            amount = amount * -1;
            friendAmount = "$" + Double.toString(amount);
        }
        else{
            textView2.setTextColor(Color.GREEN);
        }

        textView1.setText(friendDetails);
        textView2.setText(friendAmount);
        return theView;
    }
}
