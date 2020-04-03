package cs4474.g9.debtledger.ui.groups;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.UserAccountManager;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.BalanceCalculator;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.contacts.ContactListAdapter;

public class ViewGroupActivity extends AppCompatActivity {

    public static final String GROUP = "group";

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        group = (Group) getIntent().getSerializableExtra(GROUP);
        List<UserAccount> members = group.getGroupMembers();

        BalanceCalculator calculator = new BalanceCalculator();
        List<Pair<UserAccount, Long>> memberBalances = calculator.calculateBalances(members);

        // Computing amount that user owes and amount user is owed
        long youOwe = 0;
        long youreOwed = 0;
        for (Pair<UserAccount, Long> memberBalance : memberBalances) {
            if (memberBalance.second < 0) {
                youOwe = youOwe + memberBalance.second;
            } else {
                youreOwed = youreOwed + memberBalance.second;
            }
        }

        // Binding group data to UI
        final ImageView groupAvatar = findViewById(R.id.group_avatar);
        groupAvatar.setColorFilter(ColourGenerator.generateFromGroupName(group.getGroupName()));
        final TextView groupAvatarCharacter = findViewById(R.id.group_avatar_character);
        groupAvatarCharacter.setText(group.getGroupName().substring(0, 1));
        final TextView groupNameTitle = findViewById(R.id.group_name_title);
        groupNameTitle.setText(group.getGroupName());
        final TextView memberCount = findViewById(R.id.member_count);
        memberCount.setText(
                members.size() == 0
                        ? "No members"
                        : String.format(Locale.CANADA, "%d member%s", members.size(), members.size() > 1 ? "s" : "")
        );

        // Setting amount that user owes
        final TextView youOweText = findViewById(R.id.group_owe);
        Spannable youOweFormatted = new SpannableString(
                String.format(Locale.CANADA, "You Owe: $%.2f", Math.abs(youOwe) / 100.0)
        );
        // Set number portion to red
        youOweFormatted.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
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
                new ForegroundColorSpan(ContextCompat.getColor(this, R.color.green)),
                13,
                youreOweFormatted.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        );
        youreOwedText.setText(youreOweFormatted);

        // Setting up list (recycler) view and adapater
        final RecyclerView groupMembersView = findViewById(R.id.group_members_list);
        groupMembersView.setHasFixedSize(true);
        groupMembersView.setLayoutManager(new LinearLayoutManager(this));

        final ContactListAdapter groupMembersAdapter = new ContactListAdapter(memberBalances);
        groupMembersView.setAdapter(groupMembersAdapter);
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
                startActivity(toEditGroup);
            case R.id.delete_group:
                confirmDeletion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void confirmDeletion()
    {
        // Create confirmation dialogue box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void deleteGroup()
    {
        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + "/contact_group/delete/" + group.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            if (response.getJSONObject(0).has("error")) {
                                Log.d("DELETE GROUP ERROR", response.toString());
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("failure")) {
                                Log.d("DELETE GROUP FAILURE", response.toString());
                                throw new Exception();
                            }
                        } catch (Exception e) {
                            // failed to delete group from database
                            Log.d("DELETE GROUP", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // failed to update user name
                        Log.d("DELETE GROUP", error.toString());
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
        finish();
    }


    @Override
    public boolean onSupportNavigateUp() {
        // When clicking back arrow in top left, mimic behaviour of back
        onBackPressed();
        return true;
    }
}
