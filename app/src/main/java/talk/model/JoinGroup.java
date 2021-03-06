package talk.model;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class JoinGroup {
    private int groupId;
    private String userId;
    private String date;

    public JoinGroup(int groupId, String userId, String date) {
        this.groupId = groupId;
        this.userId = userId;
        this.date = date;
    }

    public JoinGroup() {
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

