package talk.model;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class ClickWork {
    private int groupId;
    private int taskId;
    private int idInTask;
    private String date;
    private String userId;

    public ClickWork(int groupId, int taskId, int idInTask, String date, String userId) {
        this.groupId = groupId;
        this.taskId = taskId;
        this.idInTask = idInTask;
        this.date = date;
        this.userId = userId;
    }

    public ClickWork() {
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getIdInTask() {
        return idInTask;
    }

    public void setIdInTask(int idInTask) {
        this.idInTask = idInTask;
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
