package cs4474.g9.debtledger.ui.groups;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.GroupManager;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.ui.shared.LoadableRecyclerView;
import cs4474.g9.debtledger.ui.shared.OnActionButtonClickedListener;

import static android.app.Activity.RESULT_OK;

public class GroupsFragment extends Fragment implements OnActionButtonClickedListener {

    public static final int CREATE_REQUEST = 0;

    private LoadableRecyclerView groupsView;
    private GroupListAdapter groupsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_groups, container, false);
        setHasOptionsMenu(true);

        groupsView = root.findViewById(R.id.groups_list);
        groupsView.setHasFixedSize(true);
        groupsView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupsView.addOnActionButtonClickedClickListener(this);

        groupsAdapter = new GroupListAdapter();
        groupsView.setAdapter(groupsAdapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserAccount loggedInUser = LoginRepository.getInstance().getLoggedInUser();
        makeRequestForGroups(loggedInUser);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_groups, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_group:
                Intent toAddGroup = new Intent(getActivity(), CreateEditGroupActivity.class);
                toAddGroup.putExtra(CreateEditGroupActivity.MODE, CreateEditGroupActivity.CREATE_MODE);
                startActivityForResult(toAddGroup, CREATE_REQUEST);
                Log.d("GROUPS", "Add Group icon clicked.");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("GROUPS", "onActivityResponse");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_REQUEST) {
            if (resultCode == RESULT_OK) {
                makeRequestForGroups(LoginRepository.getInstance().getLoggedInUser());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel/terminate any requests that are still running or queued
        ConnectionAdapter.getInstance().cancelAllRequests(hashCode());
    }

    @Override
    public void onFailedToLoadActionButtonClicked() {
        makeRequestForGroups(LoginRepository.getInstance().getLoggedInUser());
    }

    @Override
    public void onEmptyActionButtonClicked() {
        Intent toAddGroup = new Intent(getActivity(), CreateEditGroupActivity.class);
        toAddGroup.putExtra(CreateEditGroupActivity.MODE, CreateEditGroupActivity.CREATE_MODE);
        startActivity(toAddGroup);
    }

    private void makeRequestForGroups(UserAccount loggedInUser) {
        groupsView.onBeginLoading();

        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + GroupManager.LIST_END_POINT + "/" + loggedInUser.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GROUPS", response.toString());

                        try {
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("empty")) {
                                groupsAdapter.setGroups(new ArrayList<>());
                            } else {
                                // On success
                                groupsAdapter.setGroups(GroupManager.parseGroupsFromJson(response));
                            }
                        } catch (Exception e) {
                            // On parse error, set groups view to fail to finish loading mode
                            groupsView.onFailToFinishLoading();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, set groups view to fail to finish loading mode
                        Log.d("GROUPS", error.toString());
                        groupsView.onFailToFinishLoading();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }
}