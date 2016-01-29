package talk.model;

import com.android.volley.toolbox.Volley;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class ClickTask {
    private String groupName;
    private int taskId;
    private String userId;
    private String date;


    public ClickTask(String groupName, int taskId, String userId, String date) {
        this.groupName = groupName;
        this.taskId = taskId;
        this.userId = userId;
        this.date = date;
    }

    public ClickTask() {


    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
