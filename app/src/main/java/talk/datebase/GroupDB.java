package talk.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.model.Group;

public class GroupDB {

    public static final String GROUP_TABLE_NAME="groups";

    public static final String GROUP_NAME="groupName";
    public static final String GROUP_NICK_NAME="groupNickName";
    public static final String GROUP_ICON="groupIcon";
    public static final String GROUP_MASTER="groupMaster";
    public static final String GROUP_TASK_NUM="taskNum";
    public static final String GROUP_MEMBER_NUM="memberNum";

    private TalkApplication mApplication;
    private SQLiteDatabase mDb;

    private GroupMessageDB messageDB;


    public GroupDB(Context context) {
        mApplication=(TalkApplication)context;
        messageDB=mApplication.getGroupMessageDB();
        mDb = context.openOrCreateDatabase(GlobleData.GROUP_DB_NAME, Context.MODE_PRIVATE, null);
        createTable(mDb);
    }

    public void createTable(SQLiteDatabase mDb) {
        mDb.execSQL("CREATE table IF NOT EXISTS "+GROUP_TABLE_NAME
                +" ( "+GROUP_NAME +" TEXT PRIMARY KEY,"
                +GROUP_NICK_NAME +" TEXT,"
                +GROUP_ICON +" TEXT,"
                +GROUP_TASK_NUM +" INTEGER,"
                +GROUP_MEMBER_NUM +" INTEGER,"
                +GROUP_MASTER +" TEXT)");
    }

    public void addGroup(Group u) {
        if (getGroup(u.getGroupName()) != null) {
            update(u);
            return;
        }
        mDb.execSQL(
                "insert into " + GROUP_TABLE_NAME + "("
                        + GROUP_NAME + ","
                        + GROUP_NICK_NAME + ","
                        + GROUP_MEMBER_NUM + ","
                        + GROUP_TASK_NUM+ ","
                        + GROUP_ICON + ","
                        + GROUP_MASTER + ") values(?,?,?,?,?,?)",
                new Object[]{
                        u.getGroupName(),
                        u.getGroupNick(),
                        u.getMemberNum(),
                        u.getTaskNum(),
                        u.getGroupIcon(),
                        u.getGroupMaster()});

        messageDB.createTable(u.getGroupName());
    }


    public void addGroups(List<Group> list) {
        for (Group u : list) {
            addGroup(u);
        }
    }

    public void delGroup(String groupName) {
        mDb.execSQL("delete from " + GROUP_TABLE_NAME + " where " + GROUP_NAME + "=?",
                new Object[]{groupName});

        messageDB.createTable(groupName);
    }

    public void upDateGroup(List<Group> list) {
        if (list.size() > 0) {
            delete();
            addGroups(list);
        }
    }

    public void update(Group u) {
        mDb.execSQL(
                "update groups set "
                        + GROUP_NICK_NAME + "=?,"
                        + GROUP_ICON + "=?,"
                        + GROUP_MASTER + "=?,"
                        + GROUP_TASK_NUM+ "=?,"
                        + GROUP_MEMBER_NUM+ "=? "
                        + "where "
                        + GROUP_NAME + "=?",
                new Object[]{
                        u.getGroupNick(),
                        u.getGroupIcon(),
                        u.getGroupMaster(),
                        u.getTaskNum(),
                        u.getMemberNum(),
                        u.getGroupName()});
    }

    public Group getGroup(String groupId) {
        Group u = new Group();
        Cursor c = mDb.rawQuery("select * from " + GROUP_TABLE_NAME + " where " + GROUP_NAME + "=?",
                new String[]{groupId});
        if (c.moveToFirst()) {
            u.setGroupNick(c.getString(c.getColumnIndex(GROUP_NICK_NAME)));
            u.setGroupIcon(c.getString(c.getColumnIndex(GROUP_ICON)));
            u.setGroupMaster(c.getString(c.getColumnIndex(GROUP_MASTER)));
            u.setTaskNum(c.getInt(c.getColumnIndex(GROUP_TASK_NUM)));
            u.setMemberNum(c.getInt(c.getColumnIndex(GROUP_MEMBER_NUM)));
            u.setGroupName(groupId);
        } else {
            c.close();
            return null;
        }
        c.close();
        return u;
    }



    public ArrayList<Group> getGroups() {
        ArrayList<Group> list = new ArrayList<>();
        Cursor c = mDb.rawQuery("select * from "+GROUP_TABLE_NAME, null);
        while (c.moveToNext())
        {
            Group u = new Group();
            u.setGroupNick(c.getString(c.getColumnIndex(GROUP_NICK_NAME)));
            u.setGroupIcon(c.getString(c.getColumnIndex(GROUP_ICON)));
            u.setGroupMaster(c.getString(c.getColumnIndex(GROUP_MASTER)));
            u.setTaskNum(c.getInt(c.getColumnIndex(GROUP_TASK_NUM)));
            u.setMemberNum(c.getInt(c.getColumnIndex(GROUP_MEMBER_NUM)));
            u.setGroupName(c.getString(c.getColumnIndex(GROUP_NAME)));
            list.add(u);
        }
        c.close();
        return list;
    }

    public int getGroupNum(){
        int num=0;
        Cursor c = mDb.rawQuery("select "+GROUP_NAME+" from " + GROUP_TABLE_NAME, null);
        while(c.moveToNext()){
            num++;
        }
        c.close();
        return num;
    }

    public List<String> getGroupNames() {
        List<String> list = new ArrayList<>();
        Cursor c = mDb.rawQuery("select " + GROUP_NAME + " from " + GROUP_TABLE_NAME, null);
        while (c.moveToNext()) {
            list.add(c.getString(c.getColumnIndex(GROUP_NAME)));
        }
        c.close();
        return list;
    }



    public void delete() {
        mDb.execSQL("delete from "+GROUP_TABLE_NAME);
        mDb.close();
    }

}
