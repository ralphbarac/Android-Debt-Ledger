package cs4474.g9.debtledger.ui.groups;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.Item> {

    private List<Group> groups;

    public GroupListAdapter() {
        super();
        this.groups = new ArrayList<>();
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_group, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        Group group = groups.get(position);

        // Build string containing summary of group member names
        List<UserAccount> groupMembers = group.getGroupMembers();
        String groupMemberNames = groupMembers.size() > 0 ? groupMembers.get(0).getFirstName() : "No members";
        if (groupMembers.size() >= 2) {
            groupMemberNames = groupMemberNames + ", " + groupMembers.get(1).getFirstName();
        }
        if (groupMembers.size() >= 3) {
            groupMemberNames = groupMemberNames + ", +" + (groupMembers.size() - 2) + " others";
        }

        holder.groupAvatar.setColorFilter(ColourGenerator.generateFromGroupName(group.getGroupName()));
        holder.groupAvatarCharacter.setText(group.getGroupName().substring(0, 1));
        holder.groupName.setText(group.getGroupName());
        holder.groupMemberNames.setText(groupMemberNames);
    }

    @Override
    public int getItemCount() {
        return groups == null ? 0 : groups.size();
    }

    public void addNewGroup(Group group) {
        //TODO: Currently just adds to top of list
        groups.add(group);
        notifyDataSetChanged();
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView groupAvatar;
        public TextView groupAvatarCharacter;
        public TextView groupName;
        public TextView groupMemberNames;

        public Item(View view) {
            super(view);
            this.groupAvatar = view.findViewById(R.id.group_avatar);
            this.groupAvatarCharacter = view.findViewById(R.id.group_avatar_character);
            this.groupName = view.findViewById(R.id.group_name);
            this.groupMemberNames = view.findViewById(R.id.group_members);

            view.setOnClickListener(this);
        }

        public void onClick(View v) {
            Log.d("GROUP", "Group clicked.");
            Intent toViewGroup = new Intent(v.getContext(), ViewGroupActivity.class);
            toViewGroup.putExtra(ViewGroupActivity.GROUP, groups.get(getAdapterPosition()));
            v.getContext().startActivity(toViewGroup);
        }
    }
}
