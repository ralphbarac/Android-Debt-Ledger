package com.example.thomasmorphew.roomies;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Vector;

public class MainActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //private LinearLayout scrollTransactions;
    private SQLiteDatabase database = null;
    private int REQUEST_CODE_ADD_FRIEND = 0, REQUEST_CODE_CREATE_TRANSACTION = 1;
    private String signInToken;
    private Vector<Integer> friendIDMap;
    private Vector<String> friends;


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_home:
                    selectedFragment = new FragmentHome();
                    break;
                case R.id.nav_recent:
                    selectedFragment = new FragmentRecentTransactions();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new FragmentProfile();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Start up
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNav = findViewById(R.id.navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentHome()).commit();


        /*
         * Start of Database section
         */


        database = this.openOrCreateDatabase("Roomies",MODE_PRIVATE, null);

        //DELETE THIS LINE
        database.execSQL("DROP TABLE IF EXISTS accounts;");

        //Create roommate table
        database.execSQL("CREATE TABLE IF NOT EXISTS roommates " +
                "(id integer primary key, name VARCHAR, email VARCHAR);");

        database.execSQL("CREATE TABLE IF NOT EXISTS accounts " +
                "(googleID VARCHAR primary key, appID integer, FOREIGN KEY (appID) REFERENCES roommates(id));");

        //Create transaction table
        database.execSQL("CREATE TABLE IF NOT EXISTS transactions " +
                "(transID integer primary key, name text, sender integer, receiver integer, amount real, date text, FOREIGN KEY (sender) REFERENCES roommates(id), FOREIGN KEY (receiver) REFERENCES roommates(id));");


        /*
         * End of Database section
         *
         * Beginning of Spinner section
         */


        //Populating the spinner at the top of the screen.
        Cursor cursor = database.rawQuery("SELECT * FROM roommates", null);
        friends = new Vector<String>(cursor.getCount());
        friendIDMap = new Vector<Integer>(cursor.getCount());

        int nameColumn = cursor.getColumnIndex("name");
        int idColumn = cursor.getColumnIndex("id");
        cursor.moveToFirst();
        int index = 0;
        if (cursor != null && (cursor.getCount() > 0)) {

            //Might need to collect the other information for pretty display, that is we need the appIDs, or change the database key to the email for a friend.
            do {
                friendIDMap.add(index, cursor.getInt(idColumn));
                friends.add(index++, cursor.getString(nameColumn));
            } while (cursor.moveToNext());
        }
        cursor.close();

        Spinner spinner = (Spinner) findViewById(R.id.spinnerPOV);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                friends);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        /*
         * End of Spinner Section
         *
         * Beginning of cleanup private routine functions
         */


        //addGoogleAccount();
        /*
        Problem: displayFriends() displays yourself, when it shouldn't.
         */
        displayFriends();

    }

    /*
     * This code may be useful later when implementing a more permanent solution to the google madness.
     *
     *
    private void addGoogleAccount() {
        String googleInfo = getIntent().getStringExtra("GoogleInfoKey");
        String googleID = googleInfo.split(":", 3)[0];
        String googleFirstName = googleInfo.split(":", 3)[1];
        String googleEmail = googleInfo.split(":", 3)[2];

        Cursor cursor = database.rawQuery("SELECT * FROM accounts WHERE googleID = '" + googleID + "'",null);
        int appIdColumn = cursor.getColumnIndex("appID");
        cursor.moveToFirst();

        //If no google account is associated with the app...
        if(cursor != null && cursor.getCount() == 0){
            Cursor cursor1 = database.rawQuery("SELECT * FROM roommates WHERE name = '" + googleFirstName + "' AND email = '" + googleEmail + "'",null);
            int idColumn = cursor1.getColumnIndex("id");
            cursor1.moveToFirst();
            //If the person is not in the app...
            if(cursor1 != null && cursor1.getCount() == 0){
                database.execSQL("INSERT INTO roommates (name,email) VALUES ('" +
                        googleFirstName + "', '" + googleEmail + "')");
                cursor1 = database.rawQuery("SELECT * FROM roommates WHERE name = '" + googleFirstName + "' AND email = '" + googleEmail + "'",null);
                cursor1.moveToFirst();
            }

            String appID = cursor1.getString(idColumn);

            database.execSQL("INSERT INTO accounts (googleID,appID) VALUES ('" +
                    googleID + "', '" + appID + "')");

            cursor = database.rawQuery("SELECT * FROM accounts WHERE googleID = '" + googleID + "'",null);

            cursor.moveToFirst();
        }

        signInToken = cursor.getString(appIdColumn);
    }
*/

    //List1 refers to the scrollview with the roommates in it
    public void AddRoommateToList1(View view) {

        //Needs to start a new activity for result

        Intent addNewFriend = new Intent(this, AddFriendActivity.class);
        //Start activity
        startActivityForResult(addNewFriend, REQUEST_CODE_ADD_FRIEND);
        //End of activity, user has entered the data.


        /*
        TextView temp = new TextView(this);
        String textToAdd = "Roommate" + counter++;
        temp.setText(textToAdd);
        scrollRoommate.addView(temp);
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Returning from AddFriendButton
        if (requestCode == REQUEST_CODE_ADD_FRIEND) {
            if (resultCode == RESULT_OK) {
                //Get details
                String fEmail = data.getStringExtra("FriendsEmailKey");
                String fName = data.getStringExtra("FriendsNameKey");

                //Insert into database
                database.execSQL("INSERT INTO roommates (name,email) VALUES ('" +
                        fName + "', '" + fEmail + "');");


                //Reset local variables
                Cursor cursor = database.rawQuery("SELECT * FROM roommates", null);
                int nameColumn = cursor.getColumnIndex("name");
                int idColumn = cursor.getColumnIndex("id");
                cursor.moveToFirst();
                int index = 0;

                if (cursor != null && (cursor.getCount() > 0)) {

                    //Might need to collect the other information for pretty display, that is we need the appIDs, or change the database key to the email for a friend.
                    do {
                        friendIDMap.add(index, cursor.getInt(idColumn));
                        friends.add(index++, cursor.getString(nameColumn));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                //Reset spinner elements?
                /*
                Spinner spinner = (Spinner) findViewById(R.id.spinnerPOV);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        friends);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(this);
                */
                //Reset friend display
                displayFriends();
            }
        } else if(requestCode == REQUEST_CODE_CREATE_TRANSACTION){
            if(resultCode == RESULT_OK){
                //Pull from database and display results
                //displayMyTransactions();
                //Update display with the new transaction to show most current balance
                displayFriends();
            }
        }
    }

    public void goToNewTransaction(View view) {
        Intent goToNewTransactionScreen = new Intent(this, TransactionActivity.class);
        startActivityForResult(goToNewTransactionScreen, REQUEST_CODE_CREATE_TRANSACTION);
    }

    public void goToRecentTransactions(View view){
        Intent goToRecentTransactionsScreen = new Intent(this, ShowRecentTransactionsActivity.class);
        startActivity(goToRecentTransactionsScreen);
    }

    public void goToTestArea(View view){
        Intent goToTestAreaScreen = new Intent(this, TestAreaActivity.class);
        startActivity(goToTestAreaScreen);
    }

    /*
    public void goToSignIn(View view) {
        Intent goToSignInScreen = new Intent(this, SignInActivity.class);
        startActivity(goToSignInScreen);
    }
    */

    public void deleteDatabase(View view) {
        this.deleteDatabase("Roomies");
    }

    //Displays the Friends to the Scroll view after an update is added.
    private void displayFriends(){
        //Retrieve sign in token // EDIT: Sign in token refute


        //Query for all transactions you are associated with
        Cursor cursor1 = database.rawQuery("SELECT * FROM transactions WHERE sender == " + signInToken + " OR receiver == " + signInToken,null);

        int senderColumn = cursor1.getColumnIndex("sender");
        int receiverColumn = cursor1.getColumnIndex("receiver");
        int amountColumn = cursor1.getColumnIndex("amount");

        HashMap<String,Double> hashMap = new HashMap<>();

        cursor1.moveToFirst();

        //Determine resulting total of all transactions between you and i'th person (positive = you are receiving from i'th person, negative you are sending)

        //Loop starts
        if(cursor1 != null && (cursor1.getCount() > 0)){
            do {
                String key = cursor1.getString(receiverColumn);
                double amount = cursor1.getDouble(amountColumn);
                Double total;

                // If the signed in user is receiving money then
                if (key.equals(signInToken)) {
                    key = cursor1.getString(senderColumn);
                }
                // Else the signed in user is sending the money so amount is considered negative
                else {
                    amount = amount * -1;
                }

                total = hashMap.get(key);
                // If it wasn't found then it is the first entry, therefore set it to 0
                if (total == null) {
                    total = (double) 0;
                }
                total = total + amount;
                hashMap.put(key, total);
            }while(cursor1.moveToNext());
        }
        //Loop ends

        Cursor cursor = database.rawQuery("SELECT * FROM roommates", null);

        String[] friends = new String[cursor.getCount()];


        int idColumn = cursor.getColumnIndex("id");
        int nameColumn = cursor.getColumnIndex("name");
        int emailColumn = cursor.getColumnIndex("email");

        cursor.moveToFirst();
        String textToAdd;
        int index = 0;
        if (cursor != null && (cursor.getCount() > 0)) {

            do {
                String id = cursor.getString(idColumn);
                String name = cursor.getString(nameColumn);
                String email = cursor.getString(emailColumn);
                Double amount = hashMap.get(id);

                textToAdd = " " + id + " " + name + " " + email;
                if(amount != null){
                    textToAdd = amount.toString() + textToAdd;
                }
                else{
                    textToAdd =  "0" + textToAdd;
                }

                friends[index++] = textToAdd;
            } while (cursor.moveToNext());
        }
        cursor.close();

        ListAdapter listAdapter = new FriendAndTransactionChainAdapter(this, friends);
        ListView listView = findViewById(R.id.listViewFriends);
        listView.setAdapter(listAdapter);


    }

    /*
    private void displayMyTransactions() {
        scrollTransactions.removeAllViews();

        //Pull all info
        Cursor cursor = database.rawQuery("SELECT * FROM transactions WHERE sender == " + signInToken + " OR receiver == " + signInToken,null);


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
                if(cursorSender != null){
                    idRoommateColumn = cursorSender.getColumnIndex("name");
                    cursorSender.moveToFirst();
                    sender = cursorSender.getString(idRoommateColumn);
                    cursorSender.close();
                }

                //Retrieve receiver
                Cursor cursorReceiver = database.rawQuery("SELECT name FROM roommates WHERE id == "+ receiver, null);
                if(cursorReceiver != null){
                    idRoommateColumn = cursorReceiver.getColumnIndex("name");
                    cursorReceiver.moveToFirst();
                    receiver = cursorReceiver.getString(idRoommateColumn);
                    cursorReceiver.close();
                }

                roommateList = id + " : " + name + " : " + sender + " : " + receiver + " : $" + amount + "\n";
                TextView temp = new TextView(this);
                temp.setText(roommateList);
                scrollTransactions.addView(temp);

            }while(cursor.moveToNext());

        }

        cursor.close();
    }
    */

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        signInToken = Integer.toString(friendIDMap.elementAt(position));
        displayFriends();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*
    public void displayAllTransactions(View view) {
        scrollTransactions.removeAllViews();

        //Pull all info
        Cursor cursor = database.rawQuery("SELECT * FROM transactions",null);


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
                if(cursorSender != null){
                    idRoommateColumn = cursorSender.getColumnIndex("name");
                    cursorSender.moveToFirst();
                    sender = cursorSender.getString(idRoommateColumn);
                    cursorSender.close();
                }

                //Retrieve receiver
                Cursor cursorReceiver = database.rawQuery("SELECT name FROM roommates WHERE id == "+ receiver, null);
                if(cursorReceiver != null){
                    idRoommateColumn = cursorReceiver.getColumnIndex("name");
                    cursorReceiver.moveToFirst();
                    receiver = cursorReceiver.getString(idRoommateColumn);
                    cursorReceiver.close();
                }

                roommateList = id + " : " + name + " : " + sender + " : " + receiver + " : $" + amount + "\n";
                TextView temp = new TextView(this);
                temp.setText(roommateList);
                scrollTransactions.addView(temp);

            }while(cursor.moveToNext());

        }

        cursor.close();
    }
    */

    /*
    public void createTransactionChains(View view){
        //Retrieve sign in token
        //Query for all transactions you are associated with
        Cursor cursor = database.rawQuery("SELECT * FROM transactions WHERE sender == " + signInToken + " OR receiver == " + signInToken,null);

        int senderColumn = cursor.getColumnIndex("sender");
        int receiverColumn = cursor.getColumnIndex("receiver");
        int amountColumn = cursor.getColumnIndex("amount");

        HashMap<String,Double> hashMap = new HashMap<>();

        cursor.moveToFirst();

        //Determine resulting total of all transactions between you and i'th person (positive = you are receiving from i'th person, negative you are sending)

        //Loop starts
        if(cursor != null && (cursor.getCount() > 0)){
            do {
                String key = cursor.getString(receiverColumn);
                double amount = cursor.getDouble(amountColumn);
                Double total;

                // If the signed in user is receiving money then
                if (key.equals(signInToken)) {
                    key = cursor.getString(senderColumn);
                }
                // Else the signed in user is sending the money so amount is considered negative
                else {
                    amount = amount * -1;
                }

                total = hashMap.get(key);
                // If it wasn't found then it is the first entry, therefore set it to 0
                if (total == null) {
                    total = (double) 0;
                }
                total = total + amount;
                hashMap.put(key, total);
            }while(cursor.moveToNext());
        }
        //Loop ends

        //Display results to UI

        scrollTransactions.removeAllViews();
        String transactionChain;

        //Loop through hashMap
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            transactionChain = pair.getKey() + "  " + pair.getValue();
            it.remove(); // avoids a ConcurrentModificationException

            TextView temp = new TextView(this);
            temp.setText(transactionChain);
            //Set text to green or red if positive or negative respectively
            if(((Double) pair.getValue()) < 0) {
                temp.setTextColor(Color.RED);
            }
            else{
                temp.setTextColor(Color.GREEN);
            }

            scrollTransactions.addView(temp);
        }
    }
    */
}
