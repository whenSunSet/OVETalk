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
                "message='" + message + '\'' +
                ", isComing=" + isComing +
                ", date=" + date +
                ", groupId='" + groupId + '\'' +
                ", userIcon='" + userIcon + '\'' +
                ", readed=" + readed +
                ", dateStr='" + dateStr + '\'' +
                ", userNickName='" + userNickName + '\'' +
                ", userId='" + userId + '\'' +
                ", messageImage='" + messageImage + '\'' +
                ", messageStatu='" + messageStatu + '\'' +
                '}';
    }
}
