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

    public static final String GROUP_ID ="groupId";
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
                + GROUP_ID + " TEXT, "
                + TASK_ID + " INTEGER "
                + DATE + " TEXT,"
                + "foreign key (" + GROUP_ID + "," + TASK_ID + ") references " + "task(groupId,idInGroup) "
                + "PRIMARY KEY (" + USER_ID + "," + GROUP_ID + "," + TASK_ID + "))");

    }



    public void add(ClickTask clickTask) {
        if (getClick(clickTask.getGroupId(), clickTask.getUserId(), clickTask.getTaskId())!=null){
            update(clickTask);
            return;
        }
        mDb.execSQL(
                "insert into "
                        + CLICK_TASK_TABLE_NAME
                        + " (" + GROUP_ID + ","
                        + TASK_ID + ","
                        + DATE + ","
                        + USER_ID + ") values(?,?,?,?)",
                new Object[]{
                        clickTask.getGroupId(),
                        clickTask.getTaskId(),
                        clickTask.getDate(),
                        clickTask.getUserId()});

    }

    public void deleteClick(String groupId,String userId,int taskId) {
        mDb.execSQL("delete from " + CLICK_TASK_TABLE_NAME + " where " + USER_ID + "=? AND " + GROUP_ID + "=? AND" + TASK_ID + "=?",
                new Object[]{userId, groupId, taskId});
    }


    public void deleteClick(String groupId,String userId) {
        mDb.execSQL("delete from " + CLICK_TASK_TABLE_NAME + " where " + USER_ID + "=? AND " + GROUP_ID + "=?" ,
                new Object[]{userId, groupId});
    }


    public ClickTask getClick(String groupId,String userId,int taskId){
        Cursor c=mDb.rawQuery("select * from " + CLICK_TASK_TABLE_NAME + " where " + USER_ID + "=? AND " + GROUP_ID + "=? AND"+TASK_ID + "=?",
                new String []{userId,groupId,String.valueOf(taskId)});
        ClickTask clickTask=null;
        if (c.moveToFirst()){
            clickTask=makeClickTask(c);
        }

        c.close();
        return clickTask;
    }

    public ArrayList<ClickTask> getClicks(String groupId,int taskId){
        ArrayList<ClickTask> list=new ArrayList<>();
        Cursor c=mDb.rawQuery("select * from " + CLICK_TASK_TABLE_NAME+ " where " + GROUP_ID + "=? AND "+TASK_ID + "=?",
                new String []{groupId,String.valueOf(taskId)});
        while (c.moveToFirst()){
            list.add(makeClickTask(c));
        }
        c.close();
        return list;
    }

    public ArrayList<String> getClickMemberName(String groupId,int taskId){
        ArrayList<String> list=new ArrayList<>();
        ArrayList<ClickTask> clickTasks=getClicks(groupId,taskId);

        for (ClickTask clickTask:clickTasks){
            list.add(clickTask.getUserId());
        }

        return list;
    }

    public ClickTask makeClickTask(Cursor c){
        ClickTask clickTask=new ClickTask();

        clickTask.setGroupId(c.getString(c.getColumnIndex(GROUP_ID)));
        clickTask.setUserId(c.getString(c.getColumnIndex(USER_ID)));
        clickTask.setDate(c.getString(c.getColumnIndex(DATE)));
        clickTask.setTaskId(c.getInt(c.getColumnIndex(TASK_ID)));

        return clickTask;
    }



    public void update(ClickTask clickTask){
        deleteClick(clickTask.getGroupId(), clickTask.getUserId(), clickTask.getTaskId());
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
