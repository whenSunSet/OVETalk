package talk.model;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class ClickWork {
    private String groupId;
    private int taskId;
    private int workId;
    private String date;
    private String userId;

    public ClickWork(String groupId, int taskId, int workId, String date, String userId) {
        this.groupId = groupId;
        this.taskId = taskId;
        this.workId = workId;
        this.date = date;
        this.userId = userId;
    }

    public ClickWork() {
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

    public int getWorkId() {
        return workId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
