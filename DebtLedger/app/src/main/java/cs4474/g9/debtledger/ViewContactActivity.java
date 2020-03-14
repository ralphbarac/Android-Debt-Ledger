package cs4474.g9.debtledger;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ViewContactActivity extends AppCompatActivity {

    public static final String CONTACT = "contact";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);
    }
}
