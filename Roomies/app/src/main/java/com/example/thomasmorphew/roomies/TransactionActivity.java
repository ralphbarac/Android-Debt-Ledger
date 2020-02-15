package com.example.thomasmorphew.roomies;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TransactionActivity extends Activity {

    private EditText amount;
    private int receiverCounter, senderCounter, request_Code;
    private final int RECEIVER_REQUEST_CODE = 0, SENDER_REQUEST_CODE = 1;
    private SQLiteDatabase database;
    private String[] senderValues;
    private String[] receiverValues;
    private HashMap<String, String> senderMap;
    private HashMap<String, String> receiverMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_creation);
        //Initialize variables
        amount = (EditText) findViewById(R.id.editTextAmount);

        receiverCounter = 0;
        senderCounter = 0;
        database = openOrCreateDatabase("Roomies", MODE_PRIVATE, null);


    }


    public void createNewTransaction(View view) {

        //Ending the TransactionActivity activity.
        Intent toReturn = new Intent(); //Possibly move inside of the confirmed transaction portion.

        //Initialize local vars
        View v;
        EditText editTextAmount = (EditText) findViewById(R.id.editTextAmount);
        EditText editTextName = (EditText) findViewById(R.id.editTextNameOfTransaction);
        ListView listViewReceivers = findViewById(R.id.listViewReceivers);
        ListView listViewSenders = findViewById(R.id.listViewSenders);
        EditTextListViewAdapter2 receiverAdapter = (EditTextListViewAdapter2) listViewReceivers.getAdapter();
        EditTextListViewAdapter2 senderAdapter = (EditTextListViewAdapter2) listViewSenders.getAdapter();



        //Check to make sure all fields are filled.
        boolean emptyFields = false;
        String missingFields = "";
        //Check Total amount
        if(editTextAmount.getText().toString().equals("")){
            emptyFields = true;
            missingFields += " total amount,";
        }
        if(editTextName.getText().toString().equals("")){
            emptyFields = true;
            missingFields += " transaction name,";
        }
        //Checking if there are receivers
        if(listViewReceivers.getCount() == 0){
            emptyFields = true;
            missingFields += " receivers,";
        }
        //Checking if receiver amounts are missing
        boolean missingReceiver = false;
        for(int i = 0; i < receiverAdapter.getCount(); i++){
            String temp = receiverAdapter.getSecondaryResource().get(i);
            if(temp.equals("")) {
                missingReceiver = true;
            }
        }
        if(missingReceiver){
            emptyFields = true;
            missingFields += " receiver amount(s),";
        }
        //Checking if sender amounts are missing
        if(listViewSenders.getCount() == 0){
            emptyFields = true;
            missingFields += " senders,";
        }
        boolean missingSender = false;
        for(int i = 0; i < senderAdapter.getCount(); i++){
            String temp = senderAdapter.getSecondaryResource().get(i);
            if(temp.equals("")) {
                missingSender = true;
            }
        }
        if(missingSender){
            emptyFields = true;
            missingFields += " sender amount(s),";
        }

        //If all clear
        if(!emptyFields) {
            //Gather the total amount
            double totalAmount =  Double.valueOf(editTextAmount.getText().toString());

            //Gather the total amount received by the receivers.
            double receiverTotal = 0;
            for (int i = 0; i < receiverAdapter.getCount(); i++) {
                receiverTotal += Double.valueOf(receiverAdapter.getSecondaryResource().get(i));
            }

            //Gather the total amount sent by the senders.
            double senderTotal = 0;
            for (int i = 0; i < listViewSenders.getCount(); i++) {
                senderTotal += Double.valueOf(senderAdapter.getSecondaryResource().get(i));
            }

            if (senderTotal == receiverTotal && senderTotal == totalAmount) {
                //Create the transaction and end activity
            /*
            for each sender{
                divide the amount they are paying across each receiver
                record the transaction{
                    transaction needs:
                    1. Datetime
                    2. Sender
                    3. Receiver
                    4. Name/Reason
                    5. Sender Amount
                }
            }
             */
                String tempName = editTextName.getText().toString();
                for (int i = 0; i < listViewSenders.getCount(); i++) {
                    String sender = senderAdapter.getPrimaryResource().get(i).split(" ")[0];
                    double senderAmount = Double.valueOf(senderAdapter.getSecondaryResource().get(i));
                    for (int j = 0; j < listViewReceivers.getCount(); j++) {
                        //This string access my cause errors if layout changes etc.
                        String receiver = receiverAdapter.getPrimaryResource().get(j).split(" ")[0];
                        double receiverAmount = Double.valueOf(receiverAdapter.getSecondaryResource().get(j));
                        double receiverRatio = receiverAmount / totalAmount;
                        double portionPayment = Math.round(senderAmount * receiverRatio * 100.0) / 100.0;

                        //Store the data in the database
                        database.execSQL("INSERT INTO transactions (name,sender,receiver,amount,date) VALUES ('" +
                                tempName + "', '" + sender + "', '" + receiver + "', '" + portionPayment + "', '" + "test" + "');");
                    }
                }
                setResult(RESULT_OK, toReturn);
                finish();
            } else {
                /*
                 *
                 *
                 *
                 * IMPORTANT
                 *
                 *
                 *
                 */
                /*
                    State how much is missing? Perhaps auto adjust so that the first person/ random person ends up getting the extra amount.
                    Also need to make sure that the sender total and receiver total match the ACTUAL total, that is, if they are different,
                    determine which sides (receiver/sender) need adjusting and adjust accordingly. Specifically, check receiver == actual_total,
                    if not equal -> then add difference to the first person/ rando in the list. Finally, do the same for senders.
                 */

                //make a toast say that it doesn't add up
                Toast.makeText(this, "Sender, Receiver and/or Total amounts are not equal", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "The" + missingFields + " fields are missing", Toast.LENGTH_SHORT).show();
        }
    }

    public void addReceiverToListView(View view) {
        //This should pop up a fragment to select MULTIPLE RECEIVERS to add
        Intent selectNewFriend = new Intent(this, SelectFriendActivity.class);
        request_Code = RECEIVER_REQUEST_CODE;
        //Start activity
        startActivityForResult(selectNewFriend, request_Code);
        //End of activity, user has entered the data.

        /*
        String textToAdd = "R" + receiverCounter++;
        TextView temp = new TextView(this);
        temp.setText(textToAdd);
        scrollReceivers.addView(temp, scrollReceivers.getChildCount()-1);
        */
    }

    public void addSenderToListView(View view) {
        //This should pop up an activity to select MULTIPLE SENDERS to add
        Intent selectNewFriend = new Intent(this, SelectFriendActivity.class);
        request_Code = SENDER_REQUEST_CODE;
        //Start activity
        startActivityForResult(selectNewFriend, request_Code);
        //End of activity, user has entered the data.


        /*
        String textToAdd = "S" + senderCounter++;
        TextView temp = new TextView(this);
        temp.setText(textToAdd);
        scrollSenders.addView(temp, scrollSenders.getChildCount()-1);
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RECEIVER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Get ids from selection
                ArrayList<String> temp = data.getStringArrayListExtra("FriendsListKey");

                ListView listViewReceivers = findViewById(R.id.listViewReceivers);

                //Adapt array to listView
                ListAdapter theAdapter = new EditTextListViewAdapter2(this, temp);

                listViewReceivers.setAdapter(theAdapter);
            }
        }else if(requestCode == SENDER_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                //Get ids from selection
                ArrayList<String> temp = data.getStringArrayListExtra("FriendsListKey");

                ListView listViewSenders = findViewById(R.id.listViewSenders);

                //Adapt array to listView
                ListAdapter theAdapter = new EditTextListViewAdapter2(this, temp);

                listViewSenders.setAdapter(theAdapter);
            }
        }
    }

    public void splitAmountReceivers(View view) {
        ListView listView = findViewById(R.id.listViewReceivers);
        splitAmount(listView);
    }

    public void splitAmountSenders(View view) {
        ListView listView = findViewById(R.id.listViewSenders);
        splitAmount(listView);

    }

    //Possibly turn into a double or void return value depending on situation
    private void splitAmount(ListView listView){

        /*
         * New method:
         * Write a secondary resource getter/setter method to access the values within the adapter
         * Problems that may occur:
         * It may not update the EditTexts with the correct value if they are already in view, hopefully it still carries the correct value?
         */

        double equalAmount = 0, totalAmount;
        if(!amount.getText().toString().equals("")){
            totalAmount = Double.valueOf(amount.getText().toString());
            if(listView.getChildCount() > 0){
                equalAmount = Math.round(totalAmount/listView.getChildCount() *100.0) / 100.0;
                ArrayList<String> secondaryResource = ((EditTextListViewAdapter2) listView.getAdapter()).getSecondaryResource(); //MAY NEED TO CHECK THAT THERE IS AN ADAPTER SET
                for(int i = 0; i<listView.getChildCount();i++){
                    secondaryResource.set(i,Double.toString(equalAmount));
                }

                //Auto adjust for bad divisors
                double totalAfterEqual = 0;
                for(int i = 0; i<listView.getChildCount();i++){
                    totalAfterEqual += Double.valueOf(secondaryResource.get(i));
                }
                double first = Double.valueOf(secondaryResource.get(0));
                double difference;
                //If division made it larger than normal after equal, subtract difference from the first
                if (totalAfterEqual > totalAmount){
                    difference = totalAfterEqual - totalAmount;
                    first = first - difference;
                    secondaryResource.set(0, Double.toString(first));
                    Toast.makeText(this, "Difference of " + difference + " subtracted from first entry", Toast.LENGTH_SHORT).show();
                }
                //Elseif division made it smaller than equal, add difference to the first
                else if(totalAfterEqual < totalAmount){
                    difference = totalAmount - totalAfterEqual;
                    first = first + difference;
                    secondaryResource.set(0, Double.toString(first));
                    Toast.makeText(this, "Difference of " + difference + " added to first entry", Toast.LENGTH_SHORT).show();
                }
                //Else they are the same, do nothing
                //Inevitably refresh
                ((EditTextListViewAdapter2) listView.getAdapter()).notifyDataSetChanged();
            }
        }




        /*
         * Old stuff, used to work but ever since I've changed the adapter it seems to return null when finding
         * the views within the layout of the list items.
         *
        double equalAmount = 0, totalAmount;
        if(!amount.getText().toString().equals("")){
            totalAmount = Double.valueOf(amount.getText().toString());
            if(listView.getChildCount() > 0){
                equalAmount = Math.round(totalAmount/listView.getChildCount() *100.0) / 100.0;
                for(int i = 0; i<listView.getChildCount();i++){
                    View v = listView.getChildAt(i);

                    System.out.println("*********************************************");
                    System.out.println(v);
                    System.out.println(v.getClass());
                    System.out.println(v.findViewById(R.id.editTextAmountOfMoneyForFriend));
                    System.out.println("*********************************************");

                    ((EditText) v.findViewById(R.id.editTextAmountOfMoneyForFriend)).setText(Double.toString(equalAmount));
                }
            }
        }
        return equalAmount;
        */
    }

}
