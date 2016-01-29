package talk.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2015/11/3.
 */
public class Group implements Parcelable {
    private String groupName;
    private String groupNick;
    private String groupIcon;
    private String groupMaster;
    private int taskNum;
    private int memberNum;

    public Group() {
    }

    public Group(String groupName, String groupNick, String groupIcon, String groupMaster, int taskNum, int memberNum) {
        this.groupName = groupName;
        this.groupNick = groupNick;
        this.groupIcon = groupIcon;
        this.groupMaster = groupMaster;
        this.taskNum = taskNum;
        this.memberNum = memberNum;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupNick() {
        return groupNick;
    }

    public void setGroupNick(String groupNick) {
        this.groupNick = groupNick;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getGroupMaster() {
        return groupMaster;
    }

    public void setGroupMaster(String groupMaster) {
        this.groupMaster = groupMaster;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public int getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(int memberNum) {
        this.memberNum = memberNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.groupName);
        dest.writeString(this.groupNick);
        dest.writeString(this.groupIcon);
        dest.writeString(this.groupMaster);
        dest.writeInt(this.taskNum);
        dest.writeInt(this.memberNum);
    }

    protected Group(Parcel in) {
        this.groupName = in.readString();
        this.groupNick = in.readString();
        this.groupIcon = in.readString();
        this.groupMaster = in.readString();
        this.taskNum = in.readInt();
        this.memberNum = in.readInt();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
