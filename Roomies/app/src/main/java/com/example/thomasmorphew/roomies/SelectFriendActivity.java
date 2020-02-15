package com.example.thomasmorphew.roomies;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectFriendActivity extends Activity {

    private SQLiteDatabase database;
    private ListView listView;
    private String[] friends;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);

        database = openOrCreateDatabase("Roomies", MODE_PRIVATE, null);

        Cursor cursor = database.rawQuery("SELECT * FROM roommates", null);

        friends = new String[cursor.getCount()];


        int idColumn = cursor.getColumnIndex("id");
        int nameColumn = cursor.getColumnIndex("name");

        cursor.moveToFirst();
        String textToAdd;
        int index = 0;
        if (cursor != null && (cursor.getCount() > 0)) {

            do {
                String id = cursor.getString(idColumn);
                String name = cursor.getString(nameColumn);

                textToAdd = id + " " + name;
                friends[index++] = textToAdd;
            } while (cursor.moveToNext());
        }


        //Set up ListView with newly found strings from database
        ListAdapter theAdapter = new SelectFriendsArrayAdapter(this, friends);
        listView = findViewById(R.id.listViewForSelectingFriends);

        /*Problem caused when setting adapter, probably need to create custom adapter
        *   ERROR FOUND: Wrong id was used above, referencing listView that was on previous activity screen not current one.
        */
        listView.setAdapter(theAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConstraintLayout constraintLayout = (ConstraintLayout) view;
                CheckBox tgl = (CheckBox) constraintLayout.getViewById(R.id.checkBoxForFriendSelect);

                if(tgl.isChecked()){
                    tgl.setChecked(false);
                }else{
                    tgl.setChecked(true);
                }
            }
        });
        cursor.close();

        //Adapter crap
        /*
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lView = (ListView) parent;

                ListAdapter adapter = (ListAdapter) lView.getAdapter();

                HashMap<String,Object> hm = (HashMap) adapter.getItem(position);

                // The clicked Item in the ListView
                ConstraintLayout constraintLayout = (ConstraintLayout) view;
                // Getting the toggle button corresponding to the clicked item
                CheckBox tgl = (CheckBox) constraintLayout.getViewById(R.id.checkBoxForFriendSelect);


                if(tgl.isChecked()){
                    tgl.setChecked(false);
                }else{
                    tgl.setChecked(true);
                }
            }
        };
        */
        //End of adapter crap
    }

    public void selectFriends(View view) {
        Intent toReturn = new Intent();

        View v;
        ArrayList<String> friendIDs = new ArrayList<String>();
        CheckBox toggle;
        for (int i = 0; i < listView.getCount(); i++) {
            v = listView.getChildAt(i);
            TextView textView = v.findViewById(R.id.textViewFriendName);
            toggle = (CheckBox) v.findViewById(R.id.checkBoxForFriendSelect);
            if (toggle.isChecked()) {
                /*
                CONSIDER CHANGING THIS LATER SO THAT IT IS MORE SECURE/PROPER SINCE THIS IS A TERRIBLE WAY OF ACHIEVING IT
                 */
                //adds the id, not the name, to the list by splitting the string
                friendIDs.add(textView.getText().toString());
            }
        }

        //All clear, load data and end activity
        toReturn.putExtra("FriendsListKey", friendIDs);
        setResult(RESULT_OK, toReturn);
        finish();
    }
}
