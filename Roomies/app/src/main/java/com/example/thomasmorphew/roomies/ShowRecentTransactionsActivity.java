package com.example.thomasmorphew.roomies;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowRecentTransactionsActivity extends AppCompatActivity {

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recent_transactions);
        LinearLayout transactionList = (LinearLayout) findViewById(R.id.linearLayoutRecentTransactions);

        transactionList.removeAllViews();
        database = openOrCreateDatabase("Roomies", MODE_PRIVATE, null);


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
                TextView temp = new TextView(this);
                temp.setText(roommateList);
                transactionList.addView(temp);

            }while(cursor.moveToNext());

        }
        else{
            TextView temp = new TextView(this);
            temp.setText("Sorry, no transactions have been made yet.");
            transactionList.addView(temp);
        }

        cursor.close();





        //Sort from most recent to oldest (can be combined in query?)
        //Put the items in the linearLayoutRecentTransactions as textViews as per the original code.
    }
}
