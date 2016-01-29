package talk.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by asus on 2015/11/3.
 */
public class GroupChatMessage {
    private String message;
    private boolean isComing;
    private Date date;
    private String groupName;
    private String userIcon;
    private boolean readed;
    private String dateStr;
    private String userNickName;
    private String userName;
    private String messageImage;

    public GroupChatMessage()
    {
    }

    public GroupChatMessage(String message, boolean isComing, String groupName, String userIcon, boolean readed, String dateStr, String userNickName, String userName, String messageImage) {
        this.message = message;
        this.isComing = isComing;
        this.groupName = groupName;
        this.userIcon = userIcon;
        this.readed = readed;
        this.dateStr = dateStr;
        this.userNickName = userNickName;
        this.userName = userName;
        this.messageImage = messageImage;
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

    public String getMessageImage() {
        return messageImage;
    }

    public void setMessageImage(String messageImage) {
        this.messageImage = messageImage;
    }

    public void setDate(Date date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dateStr = df.format(date);
        this.date = date;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    @Override
    public String toString() {
        return "GroupChatMessage{" +
                "message='" + message + '\'' +
                ", isComing=" + isComing +
                ", date=" + date +
                ", groupName='" + groupName + '\'' +
                ", userIcon='" + userIcon + '\'' +
                ", readed=" + readed +
                ", dateStr='" + dateStr + '\'' +
                ", userNickName='" + userNickName + '\'' +
                ", userName='" + userName + '\'' +
                ", messageImage='" + messageImage + '\'' +
                '}';
    }
}
