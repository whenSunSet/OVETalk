package talk.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2015/11/7.
 */
public class Message implements Parcelable {

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
    private String message;
    private String date;
    private int groupId;
    private String userId;
    private int messageStatu;
    private String messageImage;

    private String userIcon;
    private String userNickName;

    public Message() {}

    public Message(String userId, int groupId, String date, String userIcon, String userNickName) {
        this.userId = userId;
        this.groupId = groupId;
        this.date = date;
        this.userIcon = userIcon;
        this.userNickName = userNickName;
    }

    public Message(String message, int groupId, String userIcon, String date, String userNickName, String userId, int messageStatu, String messageImage) {
        this.message = message;
        this.groupId = groupId;
        this.userIcon = userIcon;
        this.date = date;
        this.userNickName = userNickName;
        this.userId = userId;
        this.messageStatu = messageStatu;
        this.messageImage = messageImage;
    }

    protected Message(Parcel in) {
        this.message = in.readString();
        this.groupId = in.readInt();
        this.userIcon = in.readString();
        this.date = in.readString();
        this.userNickName = in.readString();
        this.userId = in.readString();
        this.messageStatu = in.readInt();
        this.messageImage = in.readString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public int getMessageStatu() {
        return messageStatu;
    }

    public void setMessageStatu(int messageStatu) {
        this.messageStatu = messageStatu;
    }

    public String getMessageImage() {
        return messageImage;
    }

    public void setMessageImage(String messageImage) {
        this.messageImage = messageImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeInt(this.groupId);
        dest.writeString(this.userIcon);
        dest.writeString(this.date);
        dest.writeString(this.userNickName);
        dest.writeString(this.userId);
        dest.writeInt(this.messageStatu);
        dest.writeString(this.messageImage);
    }
}
