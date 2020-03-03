package cs4474.g9.debtledger.ui;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ContactRequestManager;
import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_nav_bar);

        // Settings up bottom navigation
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_contacts, R.id.navigation_groups, R.id.navigation_settings
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Adding notification badge (if necessary) to contacts icon
        // TODO: Also call this method when new request added, or request responded to
        updateContactsNotificationBadge();
    }

    public void updateContactsNotificationBadge() {
        ContactRequestManager contactRequestManager = new ContactRequestManager();
        UserAccount loggedInUser = LoginRepository.getInstance(this).getLoggedInUser();
        Result result = contactRequestManager.getCountOfContactRequestsFor(loggedInUser);
        int numContactRequests = result instanceof Result.Success
                ? (int) ((Result.Success) result).getData()
                : 0;
        if (numContactRequests > 0) {
            bottomNavigationView.getOrCreateBadge(R.id.navigation_contacts).setNumber(numContactRequests);
        } else {
            bottomNavigationView.removeBadge(R.id.navigation_contacts);
        }
    }

}
