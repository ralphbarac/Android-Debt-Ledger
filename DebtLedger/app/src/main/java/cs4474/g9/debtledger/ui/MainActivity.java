package cs4474.g9.debtledger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.ViewContactActivity;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.ContactRequestManager;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.ui.groups.ViewGroupActivity;

public class MainActivity extends AppCompatActivity {

    public static final int VIEW_GROUP_REQUEST = 0;
    public static final int VIEW_CONTACT_REQUEST = 1;

    public static final String CHANGE_TAB = "change_tab";
    public static final String TAB = "tab";
    public static final int DASHBOARD_TAB = 0;
    public static final int CONTACTS_TAB = 1;
    public static final int GROUPS_TAB = 2;
    public static final int SETTINGS_TAB = 3;

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

        // Set initial view as designated (or dashboard by default)
        switch (getIntent().getIntExtra(TAB, DASHBOARD_TAB)) {
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

        // Adding notification badge (if necessary) to contacts icon
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

        // A workaround, in order to update contacts/groups after modifying/deleting viewed contact/group
        if (requestCode == MainActivity.VIEW_GROUP_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null && (data.hasExtra(ViewGroupActivity.MODIFIED) || data.hasExtra(ViewGroupActivity.DELETED))) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_groups);
                }
            }
        } else if (requestCode == MainActivity.VIEW_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null && (data.hasExtra(ViewContactActivity.MODIFIED))) {
                    bottomNavigationView.setSelectedItemId(bottomNavigationView.getSelectedItemId());
                }
            }
        }
    }

    public void updateContactsNotificationBadge() {
        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + ContactRequestManager.PENDING_END_POINT + "/" + LoginRepository.getInstance().getLoggedInUser().getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("CONTACTS", response.toString());

                        try {
                            int numContactRequests;
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("empty")) {
                                numContactRequests = 0;
                            } else {
                                numContactRequests = response.length();
                            }

                            if (numContactRequests > 0) {
                                bottomNavigationView.getOrCreateBadge(R.id.navigation_contacts).setNumber(numContactRequests);
                            } else {
                                bottomNavigationView.removeBadge(R.id.navigation_contacts);
                            }
                        } catch (Exception e) {
                            // On parse error, display failed to load contact requests message
                            Toast.makeText(MainActivity.this, R.string.failure_contact_requests, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display failed to load contact requests message
                        Log.d("CONTACTS", error.toString());
                        Toast.makeText(MainActivity.this, R.string.failure_contact_requests, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel/terminate any requests that are still running or queued
        ConnectionAdapter.getInstance().cancelAllRequests(hashCode());
    }
}
