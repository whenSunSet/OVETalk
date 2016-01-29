package talk.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2015/11/7.
 */
public class Message implements Parcelable {

    private String message;
    private String groupName;
    private String userIcon;
    private String date;
    private String userNickName;
    private String userName;
    private String messageImage;

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
        dest.writeString(message);
        dest.writeString(groupName);
        dest.writeString(userIcon);
        dest.writeString(date);
        dest.writeString(userNickName);
        dest.writeString(userName);
        dest.writeString(messageImage);
    }

    public Message() {
    }

    public Message(String message, String groupName, String userIcon, String date, String userNickName, String userName,String messageImage) {
        this.message = message;
        this.groupName = groupName;
        this.userIcon= userIcon;
        this.date = date;
        this.userNickName= userNickName;
        this.userName = userName;
        this.messageImage=messageImage;
    }

    @Override
    public String toString() {
        return "getGroupName():"+getGroupName()+"getUserName():"+getUserName()+"getDate():"+getDate()+
                "getMessage():"+getMessage()+"getMessageImage():"+getMessageImage()+"getUserNickName():"+getUserNickName()+"getUserIcon():"+getUserIcon();
    }

    public Message(Parcel in) {
        this.message = in.readString();
        this.groupName = in.readString();
        this.userIcon= in.readString();
        this.date = in.readString();
        this.userNickName = in.readString();
        this.userName= in.readString();
        this.messageImage=in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>()
    {
        public Message createFromParcel(Parcel in)
        {
            return new Message(in);
        }

        public Message[] newArray(int size)
        {
            return new Message[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static Creator<Message> getCREATOR() {
        return CREATOR;
    }
}
