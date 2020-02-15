package com.example.thomasmorphew.roomies;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class FragmentRecentTransactions extends Fragment {

    private SQLiteDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_recent_transactions, container, false);


        LinearLayout transactionList = (LinearLayout) inflated.findViewById(R.id.linearLayoutRecentTransactions);

        transactionList.removeAllViews();
        database = this.getActivity().openOrCreateDatabase("Roomies", MODE_PRIVATE, null);


        //Query for all of the transactions

        /*
         *
         *
         * TRY A NEW QUERY THAT CAN SPEED UP THIS PROCESS
         *
         *
         */
        Cursor cursor = database.rawQuery("SELECT * FROM transactions ORDER BY date()", null);

        //Pull all info

        int idColumn = cursor.getColumnIndex("transID");
        int nameColumn = cursor.getColumnIndex("name");
        int senderColumn = cursor.getColumnIndex("sender");
        int receiverColumn = cursor.getColumnIndex("receiver");
        int amountColumn = cursor.getColumnIndex("amount");

        cursor.moveToFirst();

        String roommateList;

        if(cursor != null && (cursor.getCount() > 0)){

            do{
                String id = cursor.getString(idColumn);
                String name = cursor.getString(nameColumn);
                String sender = cursor.getString(senderColumn);
                String receiver = cursor.getString(receiverColumn);
                String amount = String.valueOf(cursor.getDouble(amountColumn));

                int idRoommateColumn;

                //Retrieve sender
                Cursor cursorSender = database.rawQuery("SELECT name FROM roommates WHERE id == "+ sender, null);
                if(cursorSender != null && (cursorSender.getCount() > 0)){
                    idRoommateColumn = cursorSender.getColumnIndex("name");
                    cursorSender.moveToFirst();
                    sender = cursorSender.getString(idRoommateColumn);
                    cursorSender.close();
                }

                //Retrieve receiver
                Cursor cursorReceiver = database.rawQuery("SELECT name FROM roommates WHERE id == "+ receiver, null);
                if(cursorReceiver != null && (cursorReceiver.getCount() > 0)){
                    idRoommateColumn = cursorReceiver.getColumnIndex("name");
                    cursorReceiver.moveToFirst();
                    receiver = cursorReceiver.getString(idRoommateColumn);
                    cursorReceiver.close();
                }

                roommateList = "Transaction #" + id + " : " + sender + " sent $" + amount + " to " + receiver + " for " + name + ".\n";
                TextView temp = new TextView(this.getContext());
                temp.setText(roommateList);
                transactionList.addView(temp);

            }while(cursor.moveToNext());

        }
        else{
            TextView temp = new TextView(this.getContext());
            temp.setText("Sorry, no transactions have been made yet.");
            transactionList.addView(temp);
        }

        cursor.close();






        return inflated;
        
    }




    /*
     *
     *
     *
     */
}
