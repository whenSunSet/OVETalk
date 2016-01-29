package talk.model;


/**
 * Created by asus on 2015/12/16.
 */
public class Task  {

    //0 ：文档，1：音频，2：视频
    private String groupName;
    private int idInGroup;
    private int type;
    private String path;
    private String target;
    private int clickNumber;
    private String date;

    public Task() {
    }

    public Task(String groupName, int idInGroup, int type, String path, String target, int clickNumber, String date) {
        this.groupName = groupName;
        this.idInGroup = idInGroup;
        this.type = type;
        this.path = path;
        this.target = target;
        this.clickNumber = clickNumber;
        this.date = date;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getIdInGroup() {
        return idInGroup;
    }

    public void setIdInGroup(int idInGroup) {
        this.idInGroup = idInGroup;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getClickNumber() {
        return clickNumber;
    }

    public void setClickNumber(int clickNumber) {
        this.clickNumber = clickNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
