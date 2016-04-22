package talk.model;

import java.util.ArrayList;

/**
 * Created by heshixiyang on 2016/4/17.
 */
public class ChangeBean {
    private Group group=new Group();
    private ArrayList<User> users=new ArrayList<User>();
    private ArrayList<TaskBean> tasks=new ArrayList<TaskBean>();
    private ArrayList<WorkBean> works=new ArrayList<WorkBean>();
    private ArrayList<ClickTask> clickTasks=new ArrayList<ClickTask>();
    private ArrayList<ClickWork> clickWorks=new ArrayList<ClickWork>();
    private ArrayList<JoinGroup> joinGroups=new ArrayList<JoinGroup>();

    public ChangeBean(Group group) {

    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<TaskBean> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskBean> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<WorkBean> getWorks() {
        return works;
    }

    public void setWorks(ArrayList<WorkBean> works) {
        this.works = works;
    }

    public ArrayList<ClickTask> getClickTasks() {
        return clickTasks;
    }

    public void setClickTasks(ArrayList<ClickTask> clickTasks) {
        this.clickTasks = clickTasks;
    }

    public ArrayList<ClickWork> getClickWorks() {
        return clickWorks;
    }

    public void setClickWorks(ArrayList<ClickWork> clickWorks) {
        this.clickWorks = clickWorks;
    }

    public ArrayList<JoinGroup> getJoinGroups() {
        return joinGroups;
    }

    public void setJoinGroups(ArrayList<JoinGroup> joinGroups) {
        this.joinGroups = joinGroups;
    }
}