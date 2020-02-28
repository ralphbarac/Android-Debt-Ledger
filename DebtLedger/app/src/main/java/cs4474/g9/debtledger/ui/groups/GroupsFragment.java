package cs4474.g9.debtledger.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import cs4474.g9.debtledger.R;

public class GroupsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_groups, container, false);
        final TextView textView = root.findViewById(R.id.text_groups);
        textView.setText("This is groups fragment");
        return root;
    }
}