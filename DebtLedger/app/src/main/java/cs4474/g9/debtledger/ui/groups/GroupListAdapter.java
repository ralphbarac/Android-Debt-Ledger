package cs4474.g9.debtledger.ui.groups;

import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.Item> {

    private List<Group> group_list;

    public GroupListAdapter(List<Group> group_list) {
        super();
        this.group_list = group_list;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_group, parent,false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        Group group = group_list.get(position);
        String group_name = group.getGroupName();
        List<UserAccount> group_members = group.getGroupMembers();
        List<String> group_first_names = new ArrayList<>();
        int memberCount = group_members.size();
        String member_first_name;
        // Add all the first names of the group members to a list
        for(int i = 0; i < memberCount; ++i){
            member_first_name = group_members.get(i).getFirstName();
            group_first_names.add(member_first_name);
        }

        String firstNameString = " ";
        int overflowCount = 0;
        // If there are more than 2 members we need to summarize
        if(memberCount > 2){
          overflowCount = memberCount - 2;
          firstNameString.concat(group_first_names.get(0) + ", " + group_first_names.get(1) + ", +" + overflowCount + " others");
        }
        else {
            firstNameString.concat(group_first_names.get(0) + ", " + group_first_names.get(1));
        }

        holder.groupAvatar.setColorFilter(ColourGenerator.generateFromGroupName(group_name));
        holder.groupAvatarCharacter.setText(group_name.substring(0, 1));
        holder.groupName.setText(group_name);
        holder.groupMemberNames.setText(firstNameString);
    }

    @Override
    public int getItemCount() { return group_list == null ? 0 : group_list.size(); }

    public void addNewGroup(Group group) {
        //TODO: Currently just adds to top of list
        group_list.add(group);
        notifyDataSetChanged();
    }

    public static class Item extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            // TODO: Intent to view group
        }
    }
}
