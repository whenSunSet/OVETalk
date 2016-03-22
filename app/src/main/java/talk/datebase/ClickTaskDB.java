package talk.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import talk.Globle.GlobleData;
import talk.model.ClickTask;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class ClickTaskDB {
    public static final String CLICK_TASK_TABLE_NAME="clickTask";

    public static final String GROUP_NAME="groupName";
    public static final String USER_ID="userId";
    public static final String TASK_ID="idInGroup";
    public static final String DATE="date";

    private SQLiteDatabase mDb;

    public ClickTaskDB(Context context) {
        mDb = context.openOrCreateDatabase(GlobleData.GROUP_DB_NAME, Context.MODE_PRIVATE, null);
        createTable();
    }


    public void createTable() {
        mDb.execSQL("CREATE table IF NOT EXISTS " + CLICK_TASK_TABLE_NAME
                + " (" + USER_ID + " TEXT, "
                + GROUP_NAME + " TEXT, "
                + TASK_ID + " INTEGER "
                + DATE + " TEXT,"
                + "foreign key (" + GROUP_NAME + "," + TASK_ID + ") references " + "task(groupName,idInGroup) "
                + "PRIMARY KEY (" + USER_ID + "," + GROUP_NAME + "," + TASK_ID + "))");

    }



    public void add(ClickTask clickTask) {
        if (getClick(clickTask.getGroupName(), clickTask.getUserId(), clickTask.getTaskId())!=null){
            update(clickTask);
            return;
        }
        mDb.execSQL(
                "insert into "
                        + CLICK_TASK_TABLE_NAME
                        + " (" + GROUP_NAME + ","
                        + TASK_ID + ","
                        + DATE + ","
                        + USER_ID + ") values(?,?,?,?)",
                new Object[]{
                        clickTask.getGroupName(),
                        clickTask.getTaskId(),
                        clickTask.getDate(),
                        clickTask.getUserId()});

    }

    public void deleteClick(String groupName,String userId,int taskId) {
        mDb.execSQL("delete from " + CLICK_TASK_TABLE_NAME + " where " + USER_ID + "=? AND " + GROUP_NAME + "=? AND" + TASK_ID + "=?",
                new Object[]{userId, groupName, taskId});
    }


    public void deleteClick(String groupName,String userId) {
        mDb.execSQL("delete from " + CLICK_TASK_TABLE_NAME + " where " + USER_ID + "=? AND " + GROUP_NAME + "=?" ,
                new Object[]{userId, groupName});
    }


    public ClickTask getClick(String groupName,String userId,int taskId){
        Cursor c=mDb.rawQuery("select * from " + CLICK_TASK_TABLE_NAME + " where " + USER_ID + "=? AND " + GROUP_NAME + "=? AND"+TASK_ID + "=?",
                new String []{userId,groupName,String.valueOf(taskId)});
        ClickTask clickTask=null;
        if (c.moveToFirst()){
            clickTask=makeClickTask(c);
        }

        c.close();
        return clickTask;
    }

    public ArrayList<ClickTask> getClicks(String groupName,int taskId){
        ArrayList<ClickTask> list=new ArrayList<>();
        Cursor c=mDb.rawQuery("select * from " + CLICK_TASK_TABLE_NAME+ " where " + GROUP_NAME + "=? AND "+TASK_ID + "=?",
                new String []{groupName,String.valueOf(taskId)});
        while (c.moveToFirst()){
            list.add(makeClickTask(c));
        }
        c.close();
        return list;
    }

    public ArrayList<String> getClickMemberName(String groupName,int taskId){
        ArrayList<String> list=new ArrayList<>();
        ArrayList<ClickTask> clickTasks=getClicks(groupName,taskId);

        for (ClickTask clickTask:clickTasks){
            list.add(clickTask.getUserId());
        }

        return list;
    }

    public ClickTask makeClickTask(Cursor c){
        ClickTask clickTask=new ClickTask();

        clickTask.setGroupName(c.getString(c.getColumnIndex(GROUP_NAME)));
        clickTask.setUserId(c.getString(c.getColumnIndex(USER_ID)));
        clickTask.setDate(c.getString(c.getColumnIndex(DATE)));
        clickTask.setTaskId(c.getInt(c.getColumnIndex(TASK_ID)));

        return clickTask;
    }



    public void update(ClickTask clickTask){
        deleteClick(clickTask.getGroupName(), clickTask.getUserId(), clickTask.getTaskId());
        add(clickTask);

    }



    public void delTable() {
        mDb.execSQL("DROP TABLE " + CLICK_TASK_TABLE_NAME);
    }
    public void close() {
        if (mDb != null && mDb.isOpen())
            mDb.close();
    }

}
