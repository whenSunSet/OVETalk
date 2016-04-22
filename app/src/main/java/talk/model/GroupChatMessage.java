package talk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by asus on 2015/11/3.
 */
public class GroupChatMessage implements Parcelable {
    private String message;
    private boolean isComing;
    private Date date;
    private int groupId;
    private String userIcon;
    private boolean readed;
    private String dateStr;
    private String userNickName;
    private String userId;
    private String messageImage;
    private int messageStatu;

    public GroupChatMessage() {}

    public GroupChatMessage(String message, boolean isComing, int groupId, String userIcon,
                            boolean readed, String dateStr, String userNickName, String userId,
                            String messageImage,int messageStatu) {
        this.message = message;
        this.isComing = isComing;
        this.groupId = groupId;
        this.userIcon = userIcon;
        this.readed = readed;
        this.dateStr = dateStr;
        this.userNickName = userNickName;
        this.userId = userId;
        this.messageImage = messageImage;
        this.messageStatu=messageStatu;
    }

    public int getMessageStatu() {
        return messageStatu;
    }

    public void setMessageStatu(int messageStatu) {
        this.messageStatu = messageStatu;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isComing() {
        return isComing;
    }

    public void setIsComing(boolean isComing) {
        this.isComing = isComing;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dateStr = df.format(date);
        this.date = date;
    }

    public String getMessageImage() {
        return messageImage;
    }

    public void setMessageImage(String messageImage) {
        this.messageImage = messageImage;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }

    @Override
    public String toString() {
        return "GroupChatMessage{" +
                "message='" + message + '\n' +
                "isComing=" + isComing+ '\n' +
                "date=" + date + '\n'+
                "groupId='" + groupId + '\n' +
                "userIcon='" + userIcon + '\n' +
                "readed=" + readed + '\n'+
                "dateStr='" + dateStr + '\n' +
                "userNickName='" + userNickName + '\n'+
                "userId='" + userId + '\n' +
                "messageImage='" + messageImage + '\n' +
                "messageStatu='" + messageStatu + '\n' ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeByte(isComing ? (byte) 1 : (byte) 0);
        dest.writeLong(date != null ? date.getTime() : -1);
        dest.writeInt(this.groupId);
        dest.writeString(this.userIcon);
        dest.writeByte(readed ? (byte) 1 : (byte) 0);
        dest.writeString(this.dateStr);
        dest.writeString(this.userNickName);
        dest.writeString(this.userId);
        dest.writeString(this.messageImage);
        dest.writeInt(this.messageStatu);
    }

    protected GroupChatMessage(Parcel in) {
        this.message = in.readString();
        this.isComing = in.readByte() != 0;
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.groupId = in.readInt();
        this.userIcon = in.readString();
        this.readed = in.readByte() != 0;
        this.dateStr = in.readString();
        this.userNickName = in.readString();
        this.userId = in.readString();
        this.messageImage = in.readString();
        this.messageStatu = in.readInt();
    }

    public static final Parcelable.Creator<GroupChatMessage> CREATOR = new Parcelable.Creator<GroupChatMessage>() {
        @Override
        public GroupChatMessage createFromParcel(Parcel source) {
            return new GroupChatMessage(source);
        }

        @Override
        public GroupChatMessage[] newArray(int size) {
            return new GroupChatMessage[size];
        }
    };
}
