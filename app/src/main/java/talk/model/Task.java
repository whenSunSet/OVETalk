package talk.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2015/12/16.
 */
public class Task implements Parcelable {

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
    //0 ：文档，1：音频，2：视频
    private String groupId;
    private int idInGroup;
    private int type;
    private String path;
    private String target;
    private int clickNumber;
    private String date;

    public Task() {
    }

    public Task(String path, int idInGroup) {
        this.path = path;
        this.idInGroup = idInGroup;
    }

    public Task(String groupId, int idInGroup, int type, String path, String target, int clickNumber, String date) {
        this.groupId = groupId;
        this.idInGroup = idInGroup;
        this.type = type;
        this.path = path;
        this.target = target;
        this.clickNumber = clickNumber;
        this.date = date;
    }

    protected Task(Parcel in) {
        this.groupId = in.readString();
        this.idInGroup = in.readInt();
        this.type = in.readInt();
        this.path = in.readString();
        this.target = in.readString();
        this.clickNumber = in.readInt();
        this.date = in.readString();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.groupId);
        dest.writeInt(this.idInGroup);
        dest.writeInt(this.type);
        dest.writeString(this.path);
        dest.writeString(this.target);
        dest.writeInt(this.clickNumber);
        dest.writeString(this.date);
    }
}
