package talk.model;

/**
 * Created by asus on 2015/11/18.
 */
public class User {
    private String userId;
    private String userNick;
    private String userIcon;


    public User() {
    }

    public User(String userId, String userNick, String userIcon) {
        this.userId = userId;
        this.userNick = userNick;
        this.userIcon = userIcon;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }
}
