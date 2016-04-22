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
    private String userNick;

    public Message() {}

    public Message(String userId, int groupId, String date, String userIcon, String userNick) {
        this.userId = userId;
        this.groupId = groupId;
        this.date = date;
        this.userIcon = userIcon;
        this.userNick = userNick;
    }

    public Message(String message, int groupId, String userIcon, String date, String userNick, String userId, int messageStatu, String messageImage) {
        this.message = message;
        this.groupId = groupId;
        this.userIcon = userIcon;
        this.date = date;
        this.userNick = userNick;
        this.userId = userId;
        this.messageStatu = messageStatu;
        this.messageImage = messageImage;
    }

    protected Message(Parcel in) {
        this.message = in.readString();
        this.groupId = in.readInt();
        this.userIcon = in.readString();
        this.date = in.readString();
        this.userNick = in.readString();
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

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
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
        dest.writeString(this.userNick);
        dest.writeString(this.userId);
        dest.writeInt(this.messageStatu);
        dest.writeString(this.messageImage);
    }
}
