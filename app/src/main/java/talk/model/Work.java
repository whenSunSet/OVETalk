package talk.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class Work implements Parcelable {
    public static final Parcelable.Creator<Work> CREATOR = new Parcelable.Creator<Work>() {
        public Work createFromParcel(Parcel source) {
            return new Work(source);
        }

        public Work[] newArray(int size) {
            return new Work[size];
        }
    };
    private int taskId;
    private String groupName;
    private int idInTask;
    private int type;
    private String path;
    private String master;
    private int clickNumber;
    private String date;

    public Work(int taskId, String groupName, int idInTask, int type, String path, String master, int clickNumber, String date) {
        this.taskId = taskId;
        this.groupName = groupName;
        this.idInTask = idInTask;
        this.type = type;
        this.path = path;
        this.master = master;
        this.clickNumber = clickNumber;
        this.date = date;
    }

    public Work(int idInTask, int taskId,String path) {
        this.idInTask = idInTask;
        this.taskId = taskId;
        this.path=path;
    }

    public Work() {}


    protected Work(Parcel in) {
        this.taskId = in.readInt();
        this.groupName = in.readString();
        this.idInTask = in.readInt();
        this.type = in.readInt();
        this.path = in.readString();
        this.master = in.readString();
        this.clickNumber = in.readInt();
        this.date = in.readString();
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getIdInTask() {
        return idInTask;
    }

    public void setIdInTask(int idInTask) {
        this.idInTask = idInTask;
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

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
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
        dest.writeInt(this.taskId);
        dest.writeString(this.groupName);
        dest.writeInt(this.idInTask);
        dest.writeInt(this.type);
        dest.writeString(this.path);
        dest.writeString(this.master);
        dest.writeInt(this.clickNumber);
        dest.writeString(this.date);
    }
}
