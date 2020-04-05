package cs4474.g9.debtledger.ui.groups;

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
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.settings.AccessibleColours;

public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.Item> {

    private List<UserAccount> groupMembers;
    private List<OnMemberRemoved> onMemberRemovedListeners;

    private boolean isInAccessibleMode;

    public GroupMemberListAdapter(List<UserAccount> groupMembers, boolean isInAccessibleMode) {
        super();
        this.groupMembers = groupMembers;
        this.onMemberRemovedListeners = new ArrayList<>();

        this.isInAccessibleMode = isInAccessibleMode;
    }

    public void addOnMemberRemovedListener(OnMemberRemoved listener) {
        onMemberRemovedListeners.add(listener);
    }

    public void notifyListenersOfMemberRemoved() {
        for (OnMemberRemoved onMemberRemovedListener : onMemberRemovedListeners) {
            onMemberRemovedListener.onGroupMemberRemoved(groupMembers);
        }
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_group_member, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        UserAccount contact = groupMembers.get(position);

        holder.contactAvatar.setColorFilter(ColourGenerator.generateFromName(contact.getFirstName(), contact.getLastName()));
        holder.contactAvatarCharacter.setText(contact.getFirstName().substring(0, 1));
        holder.contactName.setText(contact.getFirstName() + " " + contact.getLastName());
    }

    @Override
    public int getItemCount() {
        return groupMembers == null ? 0 : groupMembers.size();
    }

    public void setGroupMembers(List<UserAccount> groupMembers) {
        this.groupMembers = groupMembers;
        notifyDataSetChanged();
    }

    public class Item extends RecyclerView.ViewHolder {
        public ImageView contactAvatar;
        public TextView contactAvatarCharacter;
        public TextView contactName;

        public Item(View view) {
            super(view);
            this.contactAvatar = view.findViewById(R.id.contact_avatar);
            this.contactAvatarCharacter = view.findViewById(R.id.contact_avatar_character);
            this.contactName = view.findViewById(R.id.name);

            ImageView remove = view.findViewById(R.id.remove);
            remove.setColorFilter(AccessibleColours.getNegativeColour(isInAccessibleMode));
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupMembers.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyListenersOfMemberRemoved();
                }
            });
        }
    }
}
