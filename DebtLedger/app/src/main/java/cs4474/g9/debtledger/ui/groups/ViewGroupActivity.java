package cs4474.g9.debtledger.ui.groups;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.ViewContactActivity;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.MainActivity;
import cs4474.g9.debtledger.ui.contacts.ContactListAdapter;
import cs4474.g9.debtledger.ui.settings.AccessibleColours;
import cs4474.g9.debtledger.ui.settings.Preference;

public class ViewGroupActivity extends AppCompatActivity {

    private final static int EDIT_REQUEST = 0;

    public static final String GROUP = "group";
    public static final String DELETED = "deleted";
    public static final String MODIFIED = "modified";

    private Group group;
    // Variables to check current status of group
    private boolean modified = false;
    private boolean deleted = false;

    private boolean isInAccessibleMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isInAccessibleMode = preferences.getBoolean(Preference.ACCESSIBILITY, false);

        setContentView(R.layout.activity_view_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        group = (Group) getIntent().getSerializableExtra(GROUP);
        setGroupDetails();
        makeRequestForBalances(group.getGroupMembers()); // Get balance with all members of group and update relevant UI
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_group:
                Intent toEditGroup = new Intent(this, CreateEditGroupActivity.class);
                toEditGroup.putExtra(CreateEditGroupActivity.MODE, CreateEditGroupActivity.EDIT_MODE);
                toEditGroup.putExtra(CreateEditGroupActivity.GROUP, group);
                startActivityForResult(toEditGroup, EDIT_REQUEST);
                return true;
            case R.id.delete_group:
                confirmDeletion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.hasExtra(GROUP)) {
                    modified = true;
                    if (data.getSerializableExtra(GROUP) instanceof Group) {
                        group = ((Group) data.getSerializableExtra(GROUP));
                        setGroupDetails();
                        makeRequestForBalances(group.getGroupMembers());
                    }
                }
            }
        } else if (requestCode == MainActivity.VIEW_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null && (data.hasExtra(ViewContactActivity.MODIFIED))) {
                    makeRequestForBalances(group.getGroupMembers());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel/terminate any requests that are still running or queued
        ConnectionAdapter.getInstance().cancelAllRequests(hashCode());
    }

    private void setGroupDetails() {
        // Binding group data to UI
        final ImageView groupAvatar = findViewById(R.id.group_avatar);
        groupAvatar.setColorFilter(ColourGenerator.generateFromGroupName(group.getGroupName()));
        final TextView groupAvatarCharacter = findViewById(R.id.group_avatar_character);
        groupAvatarCharacter.setText(group.getGroupName().substring(0, 1));
        final TextView groupNameTitle = findViewById(R.id.group_name_title);
        groupNameTitle.setText(group.getGroupName());

        final TextView memberCount = findViewById(R.id.member_count);
        List<UserAccount> members = group.getGroupMembers();
        memberCount.setText(
                members.size() == 0
                        ? "No members"
                        : String.format(Locale.CANADA, "%d member%s", members.size(), members.size() > 1 ? "s" : "")
        );
    }

    private void updateAmountsOwedAndMembers(List<Pair<UserAccount, Long>> memberBalances) {
        // Computing amount that user owes and amount user is owed
        long youOwe = 0;
        long youreOwed = 0;

        for (int i = 0; i < memberBalances.size(); i++) {
            Pair<UserAccount, Long> memberBalance = memberBalances.get(i);
            if (memberBalance.second < 0) {
                youOwe = youOwe + memberBalance.second;
            } else {
                youreOwed = youreOwed + memberBalance.second;
            }
        }

        // Setting amount that user owes
        final TextView youOweText = findViewById(R.id.group_owe);
        Spannable youOweFormatted = new SpannableString(
                String.format(Locale.CANADA, "You Owe: $%.2f", Math.abs(youOwe) / 100.0)
        );
        // Set number portion to red
        youOweFormatted.setSpan(
                new ForegroundColorSpan(AccessibleColours.getNegativeColour(isInAccessibleMode)),
                9,
                youOweFormatted.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        );
        youOweText.setText(youOweFormatted);

        // Setting amount user is owed
        final TextView youreOwedText = findViewById(R.id.group_owed);
        Spannable youreOweFormatted = new SpannableString(
                String.format(Locale.CANADA, "You're Owed: $%.2f", Math.abs(youreOwed) / 100.0)
        );
        // Set number portion to green
        youreOweFormatted.setSpan(
                new ForegroundColorSpan(AccessibleColours.getPositiveColour(isInAccessibleMode)),
                13,
                youreOweFormatted.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        );
        youreOwedText.setText(youreOweFormatted);

        // Setting up list (recycler) view and adapater
        final RecyclerView groupMembersView = findViewById(R.id.group_members_list);
        groupMembersView.setHasFixedSize(true);
        groupMembersView.setLayoutManager(new LinearLayoutManager(this));

        final ContactListAdapter groupMembersAdapter = new ContactListAdapter(memberBalances, isInAccessibleMode);
        groupMembersView.setAdapter(groupMembersAdapter);
    }


    private void makeRequestForBalances(List<UserAccount> contacts) {
        // List of pairs associating a user with their balance with the logged in user
        List<Pair<UserAccount, Long>> balances = new ArrayList<>();
        UserAccount user = LoginRepository.getInstance().getLoggedInUser();

        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + "/transaction/balances/" + user.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GROUP", response.toString());
                        try {
                            if (response.getJSONObject(0).has("error") ||
                                    response.getJSONObject(0).has("failure")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("empty")) {
                                updateAmountsOwedAndMembers(balances);
                            } else {
                                // Got list of transactions, total them to find balance with logged in user
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject transaction = response.getJSONObject(i);
                                    for (UserAccount contact : contacts) {
                                        if (contact.getEmail().equals(transaction.get("email").toString())) {
                                            // Get the balance as a long
                                            Long balance = transaction.isNull("balance")
                                                    ? 0
                                                    : (long) (transaction.getDouble("balance") * 100);
                                            // Add a pair with the contact and their balance with the user
                                            balances.add(Pair.create(contact, balance));
                                        }
                                    }
                                }
                                updateAmountsOwedAndMembers(balances);
                            }
                        } catch (Exception e) {
                            // On error, display failed to get user balances
                            Toast.makeText(ViewGroupActivity.this, R.string.failure_balances, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display failed to get user balances
                        Log.d("GROUP", error.toString());
                        Toast.makeText(ViewGroupActivity.this, R.string.failure_balances, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

    private void confirmDeletion() {
        // Create confirmation dialogue box
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setCancelable(true);
        builder.setTitle("Delete Group");
        builder.setMessage("Are you sure you want to delete this group?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGroup();
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void deleteGroup() {
        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + "/contact_group/delete/" + group.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GROUP", response.toString());

                        try {
                            if (response.getJSONObject(0).has("error") ||
                                    response.getJSONObject(0).has("failure")) {
                                throw new Exception();
                            } else {
                                deleted = true;
                                finish();
                            }
                        } catch (Exception e) {
                            // On error, display failed to delete group from database
                            Toast.makeText(ViewGroupActivity.this, R.string.failure_delete_group, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, display failed to delete group from database
                        Log.d("GROUP", error.toString());
                        Toast.makeText(ViewGroupActivity.this, R.string.failure_delete_group, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }


    @Override
    public boolean onSupportNavigateUp() {
        // When clicking back arrow in top left, mimic behaviour of back
        onBackPressed();
        return true;
    }

    @Override
    public void finish() {
        // If the group has either been deleted/modified, pass that information back
        if (deleted) {
            Intent data = new Intent();
            data.putExtra(DELETED, true);
            setResult(RESULT_OK, data);
        } else if (modified) {
            Intent data = new Intent();
            data.putExtra(MODIFIED, true);
            data.putExtra(GROUP, group);
            setResult(RESULT_OK, data);
        }

        super.finish();
    }
}
