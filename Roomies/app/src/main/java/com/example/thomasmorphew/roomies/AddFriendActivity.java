package com.example.thomasmorphew.roomies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

public class AddFriendActivity extends Activity {

    EditText editTextName, editTextEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
    }

    public void passFriendInfoBack(View view) {

        Intent toReturn = new Intent();

        toReturn.putExtra("FriendsNameKey", editTextName.getText().toString());
        toReturn.putExtra("FriendsEmailKey", editTextEmail.getText().toString());

        setResult(RESULT_OK, toReturn);

        finish();
    }
}
