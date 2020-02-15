package com.example.thomasmorphew.roomies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SelectFriendsArrayAdapter extends ArrayAdapter<String> {

    //Constructor
    public SelectFriendsArrayAdapter(Context context, String[] values){
        super(context, R.layout.select_friend_row_layout, values);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Some stupid class
        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.select_friend_row_layout, parent, false);

        String friendsName = (String) getItem(position);

        TextView textView = theView.findViewById(R.id.textViewFriendName);

        textView.setText(friendsName);

        CheckBox checkBox = theView.findViewById(R.id.checkBoxForFriendSelect);
        checkBox.setChecked(false);

        return theView;
    }
}
