package talk.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2015/11/3.
 */
public class Group implements Parcelable {
    private String groupId;
    private String groupNickName;
    private String groupIcon;
    private String groupMaster;
    private int taskNum;
    private int memberNum;

    public Group() {
    }

    public Group(String groupId, String groupNickName, String groupIcon, String groupMaster, int taskNum, int memberNum) {
        this.groupId = groupId;
        this.groupNickName = groupNickName;
        this.groupIcon = groupIcon;
        this.groupMaster = groupMaster;
        this.taskNum = taskNum;
        this.memberNum = memberNum;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupNickName() {
        return groupNickName;
    }

    public void setGroupNickName(String groupNickName) {
        this.groupNickName = groupNickName;
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
        dest.writeString(this.groupId);
        dest.writeString(this.groupNickName);
        dest.writeString(this.groupIcon);
        dest.writeString(this.groupMaster);
        dest.writeInt(this.taskNum);
        dest.writeInt(this.memberNum);
    }

    protected Group(Parcel in) {
        this.groupId = in.readString();
        this.groupNickName = in.readString();
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
