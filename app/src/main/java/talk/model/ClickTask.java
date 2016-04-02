package talk.model;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class ClickTask {
    private String groupId;
    private int taskId;
    private String userId;
    private String date;


    public ClickTask(String groupId, int taskId, String userId, String date) {
        this.groupId = groupId;
        this.taskId = taskId;
        this.userId = userId;
        this.date = date;
    }

    public ClickTask() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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
