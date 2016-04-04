package talk.model;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class ClickTask {
    private int groupId;
    private int idInGroup;
    private String userId;
    private String date;


    public ClickTask(int groupId, int idInGroup, String userId, String date) {
        this.groupId = groupId;
        this.idInGroup = idInGroup;
        this.userId = userId;
        this.date = date;
    }

    public ClickTask() {
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getIdInGroup() {
        return idInGroup;
    }

    public void setIdInGroup(int idInGroup) {
        this.idInGroup = idInGroup;
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
