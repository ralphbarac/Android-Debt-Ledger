package cs4474.g9.debtledger.ui.groups;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import cs4474.g9.debtledger.R;

public class GroupsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_groups, container, false);
        setHasOptionsMenu(true);

        final TextView textView = root.findViewById(R.id.text_groups);
        textView.setText("This is groups fragment");
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
                Log.d("GROUPS", "Add Group icon clicked.");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}