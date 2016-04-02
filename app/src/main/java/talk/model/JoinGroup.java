package talk.model;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class JoinGroup {
    private String groupId;
    private String memberId;
    private String date;

    public JoinGroup(String groupId, String memberId, String date) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.date = date;
    }

    public JoinGroup() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

