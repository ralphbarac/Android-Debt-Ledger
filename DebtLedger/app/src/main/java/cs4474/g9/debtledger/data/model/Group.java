package cs4474.g9.debtledger.data.model;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;


public class Group implements Serializable {

    private long id;
    private String owner;
    private String description;
    private List<UserAccount> groupMembers;

    public Group(String name, List<UserAccount> groupMembers) {
        this.description = name;
        this.groupMembers = groupMembers;
    }

    public long getId() {
        return id;
    }

    public String getGroupName() {
        return description;
    }

    public List<UserAccount> getGroupMembers() {
        return groupMembers;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Group) {
            Group group = (Group) obj;
            // TODO: Use ids eventually...
            return this.description.equals(group.getGroupName());
        } else {
            return false;
        }
    }

}
