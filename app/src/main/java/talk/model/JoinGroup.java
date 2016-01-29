package talk.model;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class JoinGroup {
    private String groupName;
    private String memberId;
    private String date;

    public JoinGroup(String groupName, String memberId, String date) {
        this.groupName = groupName;
        this.memberId = memberId;
        this.date = date;
    }

    public JoinGroup() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

