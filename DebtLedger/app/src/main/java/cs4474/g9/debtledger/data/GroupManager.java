package cs4474.g9.debtledger.data;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import cs4474.g9.debtledger.data.model.Group;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.ui.groups.CreateEditGroupActivity;

public class GroupManager {

    public static final String LIST_END_POINT = "/contact_group/grouplist";

    public static Group parseGroupFromJson(JSONObject groupJson) throws JSONException {
        JSONArray membersJson = new JSONArray(groupJson.getString("members"));
        List<UserAccount> members = new ArrayList<>();
        for (int i = 0; i < membersJson.length(); i++) {
            members.add(UserAccountManager.parseUserAccountFromJson(membersJson.getJSONObject(i)));
        }
        return new Group(
                groupJson.getInt("id"),
                groupJson.getString("name"),
                members
        );
    }

    public static List<Group> parseGroupsFromJson(JSONArray groupsJson) throws JSONException {
        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < groupsJson.length(); i++) {
            groups.add(parseGroupFromJson(groupsJson.getJSONObject(i)));
        }
        return groups;
    }

    public Result<List<Group>> getGroupsOf(UserAccount user) {
        // TODO: Implement, currently using dummy data
        List<UserAccount> group1list = new ArrayList<>();
        List<UserAccount> group2list = new ArrayList<>();
        group1list.add(new UserAccount("John", "Doe", ""));
        group1list.add(new UserAccount("Thomas", "Morphew", ""));
        group2list.add(new UserAccount("Thomas", "Morphew", ""));
        group2list.add(new UserAccount("Timothy", "Young", ""));
        group2list.add(new UserAccount("Will", "Smith", ""));

        List<Group> groupList = new ArrayList<>();
        groupList.add(new Group("Roomies", group1list));
        groupList.add(new Group("Pizza Hut Boys", group2list));

        return new Result.Success<>(groupList);
    }

    public static JSONObject createJsonForAddGroup(long id, String name) throws JSONException {
        JSONObject jsonAddGroup = new JSONObject();
        jsonAddGroup.put("owner", id);
        jsonAddGroup.put("name", name);
        return jsonAddGroup;
    }

    public static JSONObject createJsonForSetGroupMembers(long id, List<UserAccount> members) throws JSONException {
        JSONObject jsonSetGroupMembers = new JSONObject();
        jsonSetGroupMembers.put("group_id", id);
        JSONArray groupMembers = new JSONArray();
        int i = 0;
        while (i < members.size()) {
            groupMembers.put(members.get(i).getId());
            i++;
        }
        jsonSetGroupMembers.put("members", groupMembers);
        Log.d("GENERATED SET", jsonSetGroupMembers.toString());
        return jsonSetGroupMembers;
    }

}
