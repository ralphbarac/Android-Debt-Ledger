package cs4474.g9.debtledger.ui.groups.select;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;

public class SelectMultipleGroupsAdapter extends RecyclerView.Adapter<SelectMultipleGroupsAdapter.Item> {

    private List<Group> groups;
    private BitSet selectedGroups;

    private List<OnGroupChecked> onGroupCheckedListeners;

    public SelectMultipleGroupsAdapter() {
        super();
        this.groups = new ArrayList<>();
        this.selectedGroups = new BitSet(groups.size());
        this.onGroupCheckedListeners = new ArrayList<>();
    }

    public SelectMultipleGroupsAdapter(List<Group> groups, List<Group> currentlySelectedGroups) {
        super();
        this.groups = groups;
        this.selectedGroups = new BitSet(groups.size());
        this.onGroupCheckedListeners = new ArrayList<>();

        for (Group currentlySelectedContact : currentlySelectedGroups) {
            selectedGroups.set(groups.indexOf(currentlySelectedContact));
        }
    }

    public void addOnGroupCheckedListener(OnGroupChecked onGroupChecked) {
        onGroupCheckedListeners.add(onGroupChecked);
    }

    public void notifyListenersOfGroupChecked(Group group) {
        for (OnGroupChecked onGroupCheckedListener : onGroupCheckedListeners) {
            onGroupCheckedListener.onGroupChecked(group);
        }
    }

    public void notifyListenersOfGroupUnchecked(Group group) {
        for (OnGroupChecked onGroupCheckedListener : onGroupCheckedListeners) {
            onGroupCheckedListener.onGroupUnchecked(group);
        }
    }

    public void setGroups(List<Group> groups, List<Group> currentlySelectedGroups) {
        this.groups = groups;
        this.selectedGroups = new BitSet(groups.size());

        for (Group currentlySelectedContact : currentlySelectedGroups) {
            selectedGroups.set(groups.indexOf(currentlySelectedContact));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_select_group_multiple, parent, false);
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

        holder.checkBox.setChecked(selectedGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups == null ? 0 : groups.size();
    }

    public List<Group> getSelectedGroups() {
        List<Group> selections = new ArrayList<>();
        for (int i = 0; i < selectedGroups.size(); i++) {
            if (selectedGroups.get(i)) {
                selections.add(groups.get(i));
            }
        }
        return selections;
    }

    public void unselectGroupsIfNecessary(UserAccount uncheckedContact) {
        for (int i = 0; i < groups.size(); i++) {
            if (selectedGroups.get(i)) {
                if (groups.get(i).getGroupMembers().contains(uncheckedContact)) {
                    selectedGroups.clear(i);
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void unselectGroupsIfNecessary(Group uncheckedGroup) {
        for (UserAccount uncheckedGroupMember : uncheckedGroup.getGroupMembers()) {
            unselectGroupsIfNecessary(uncheckedGroupMember);
        }
    }

    private void setSelected(int position) {
        selectedGroups.set(position);
    }

    private void clearSelected(int position) {
        selectedGroups.clear(position);
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener, CheckBox.OnCheckedChangeListener {
        public ImageView groupAvatar;
        public TextView groupAvatarCharacter;
        public TextView groupName;
        public TextView groupMemberNames;
        public CheckBox checkBox;

        public Item(View view) {
            super(view);
            view.setOnClickListener(this);

            this.groupAvatar = view.findViewById(R.id.group_avatar);
            this.groupAvatarCharacter = view.findViewById(R.id.group_avatar_character);
            this.groupName = view.findViewById(R.id.group_name);
            this.groupMemberNames = view.findViewById(R.id.group_members);
            this.checkBox = view.findViewById(R.id.check_box);
            this.checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            checkBox.setChecked(!checkBox.isChecked());
            if (checkBox.isChecked()) {
                setSelected(getAdapterPosition());
                notifyListenersOfGroupChecked(groups.get(getAdapterPosition()));
            } else {
                clearSelected(getAdapterPosition());
                notifyListenersOfGroupUnchecked(groups.get(getAdapterPosition()));
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // On checked changed is called even through programmatic changes, in order to avoid
            // notifying listeners when not necessary, only executing method if state is changing
            if (selectedGroups.get(getAdapterPosition()) != isChecked) {
                if (isChecked) {
                    setSelected(getAdapterPosition());
                    notifyListenersOfGroupChecked(groups.get(getAdapterPosition()));
                } else {
                    clearSelected(getAdapterPosition());
                    notifyListenersOfGroupUnchecked(groups.get(getAdapterPosition()));
                }
            }
        }
    }
}
