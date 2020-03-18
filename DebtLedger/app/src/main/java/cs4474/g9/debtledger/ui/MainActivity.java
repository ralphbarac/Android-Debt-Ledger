package cs4474.g9.debtledger.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.ContactRequestManager;
import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.User;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;

public class MainActivity extends AppCompatActivity {

    public static final String CHANGE_TAB = "change_tab";
    public static final int DASHBOARD_TAB = 0;
    public static final int CONTACTS_TAB = 1;
    public static final int GROUPS_TAB = 2;
    public static final int SETTINGS_TAB = 3;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize connection to database
        ConnectionAdapter.Initialize(this);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.hasExtra(CHANGE_TAB)) {
            switch (data.getIntExtra(CHANGE_TAB, DASHBOARD_TAB)) {
                case DASHBOARD_TAB:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
                    break;
                case CONTACTS_TAB:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_contacts);
                    break;
                case GROUPS_TAB:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_groups);
                    break;
                case SETTINGS_TAB:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_settings);
                    break;
            }
        }
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
