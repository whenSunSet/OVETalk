package talk.model;

/**
 * Created by asus on 2015/11/18.
 */
public class User {
    private String userID;
    private String userNickName;
    private String userIcon;


    public User() {
    }

    public User(String userID, String userNickName, String userIcon) {
        this.userID = userID;
        this.userNickName = userNickName;
        this.userIcon = userIcon;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }
}
