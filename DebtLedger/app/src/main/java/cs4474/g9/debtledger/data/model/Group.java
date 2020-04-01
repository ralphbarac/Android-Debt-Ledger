package cs4474.g9.debtledger.data.model;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;


public class Group implements Serializable {

    private long id;
    private String name;
    private List<UserAccount> groupMembers;

    public Group(String name, List<UserAccount> groupMembers) {
        this.name = name;
        this.groupMembers = groupMembers;
    }

    public Group(long id, String name, List<UserAccount> groupMembers) {
        this.id = id;
        this.name = name;
        this.groupMembers = groupMembers;
    }

    public long getId() {
        return id;
    }

    public String getGroupName() {
        return name;
    }

    public List<UserAccount> getGroupMembers() {
        return groupMembers;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Group) {
            Group group = (Group) obj;
            // TODO: Use ids eventually...
            return this.name.equals(group.getGroupName());
        } else {
            return false;
        }
    }

}
