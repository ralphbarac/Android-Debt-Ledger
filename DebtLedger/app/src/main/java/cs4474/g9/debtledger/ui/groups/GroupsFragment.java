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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.GroupManager;
import cs4474.g9.debtledger.data.Result;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.UserAccount;

public class GroupsFragment extends Fragment {

    private RecyclerView groupView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_groups, container, false);
        setHasOptionsMenu(true);

        Result result;
        UserAccount loggedInUser = LoginRepository.getInstance(getContext()).getLoggedInUser();

        List<Group> userGroups = new ArrayList<>();
        GroupManager groupManager = new GroupManager();
        result = groupManager.getGroupsOf(loggedInUser);
        if(result instanceof Result.Success) {
            userGroups = (List<Group>) ((Result.Success) result).getData();
        }
        else {
            Toast.makeText(getContext(), "Unable to get groups", Toast.LENGTH_SHORT).show();
        }

        groupView = root.findViewById(R.id.group_list);
        groupView.setHasFixedSize(true);
        groupView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_groups, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_group:
                // TODO: Open Add Group Activity
                Intent toAddGroup = new Intent(getActivity(), CreateEditGroupActivity.class);
                toAddGroup.putExtra(CreateEditGroupActivity.MODE, CreateEditGroupActivity.CREATE_MODE);
                startActivity(toAddGroup);
                Log.d("GROUPS", "Add Group icon clicked.");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}