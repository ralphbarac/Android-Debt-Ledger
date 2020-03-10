package cs4474.g9.debtledger.data.model;

import java.util.ArrayList;
import java.util.List;


public class Group {

    private long id; // if necessary

    private UserAccount user;
    private String name;
    private List<UserAccount> group_members = new ArrayList<>();

    public Group(UserAccount user, String name, List<UserAccount> group_members) {
        this.user = user;
        this.name = name;
        this.group_members = group_members;
    }

    public String getGroupName() {return name;}
    public List<UserAccount> getGroupMembers() {
        return group_members;
    }



}
