package cs4474.g9.debtledger.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.ui.contacts.select.SelectContactActivity;
import cs4474.g9.debtledger.ui.contacts.select.SelectMultipleContactsActivity;

public class CreateTransactionActivity extends AppCompatActivity {

    private UserAccount whoIsPaying;
    private List<UserAccount> whoOwes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        final RecyclerView whoOwesInputView = findViewById(R.id.who_owes_input_list);
        whoOwesInputView.setHasFixedSize(true);
        whoOwesInputView.setLayoutManager(new LinearLayoutManager(this));
        final RecyclerView.Adapter whoOwesInputAdapter = new InputAmountAdapter(whoOwes, LoginRepository.getInstance().getLoggedInUser());
        whoOwesInputView.setAdapter(whoOwesInputAdapter);
    }

    public void selectWhoIsPaying(View view) {
        Intent toSelectWhoIsPaying = new Intent(this, SelectContactActivity.class);
        startActivity(toSelectWhoIsPaying);
    }

    public void selectWhoOwes(View view) {
        Intent toSelectWhoOwes = new Intent(this, SelectMultipleContactsActivity.class);
        startActivity(toSelectWhoOwes);
    }
}
