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
    public static final String ID_IN_GROUP ="idInGroup";
    public static final String DATE="date";

    private SQLiteDatabase mDb;

    public ClickTaskDB(Context context) {
        mDb = context.openOrCreateDatabase(GlobleData.GROUP_DB_NAME, Context.MODE_PRIVATE, null);
        createTable();
    }


    public void createTable() {
        mDb.execSQL("CREATE table IF NOT EXISTS " + CLICK_TASK_TABLE_NAME
                + " (" + USER_ID + " TEXT, "
                + GROUP_ID + " INTEGER, "
                + ID_IN_GROUP + " INTEGER "
                + DATE + " TEXT,"
                + "foreign key (" + GROUP_ID + "," + ID_IN_GROUP + ") references " + "task(groupId,idInGroup) "
                + "PRIMARY KEY (" + USER_ID + "," + GROUP_ID + "," + ID_IN_GROUP + "))");

    }

    public void adds(ArrayList<ClickTask> list){
        for (ClickTask clickTask:list){
            add(clickTask);
        }
    }

    public void add(ClickTask clickTask) {
        if (getClick(clickTask.getGroupId(), clickTask.getUserId(), clickTask.getIdInGroup())!=null){
            update(clickTask);
            return;
        }
        mDb.execSQL(
                "insert into "
                        + CLICK_TASK_TABLE_NAME
                        + " (" + GROUP_ID + ","
                        + ID_IN_GROUP + ","
                        + DATE + ","
                        + USER_ID + ") values(?,?,?,?)",
                new Object[]{
                        clickTask.getGroupId(),
                        clickTask.getIdInGroup(),
                        clickTask.getDate(),
                        clickTask.getUserId()});

    }

    public void deleteClick(int groupId,String userId,int taskId) {
        mDb.execSQL("delete from " + CLICK_TASK_TABLE_NAME + " where " + USER_ID + "=? AND " + GROUP_ID + "=? AND" + ID_IN_GROUP + "=?",
                new Object[]{userId, groupId, taskId});
    }


    public void deleteClick(int groupId,String userId) {
        mDb.execSQL("delete from " + CLICK_TASK_TABLE_NAME + " where " + USER_ID + "=? AND " + GROUP_ID + "=?" ,
                new Object[]{userId, groupId});
    }


    public ClickTask getClick(int groupId,String userId,int taskId){
        Cursor c=mDb.rawQuery("select * from " + CLICK_TASK_TABLE_NAME + " where " + USER_ID + "=? AND " + GROUP_ID + "=? AND"+ ID_IN_GROUP + "=?",
                new String []{userId,String.valueOf(groupId),String.valueOf(taskId)});
        ClickTask clickTask=null;
        if (c.moveToFirst()){
            clickTask=makeClickTask(c);
        }

        c.close();
        return clickTask;
    }

    public ArrayList<ClickTask> getClicks(int groupId,int taskId){
        ArrayList<ClickTask> list=new ArrayList<>();
        Cursor c=mDb.rawQuery("select * from " + CLICK_TASK_TABLE_NAME+ " where " + GROUP_ID + "=? AND "+ ID_IN_GROUP + "=?",
                new String []{String .valueOf(groupId),String.valueOf(taskId)});
        while (c.moveToNext()){
            list.add(makeClickTask(c));
        }
        c.close();
        return list;
    }

    public ArrayList<String> getClickMemberName(int groupId,int taskId){
        ArrayList<String> list=new ArrayList<>();
        ArrayList<ClickTask> clickTasks=getClicks(groupId,taskId);

        for (ClickTask clickTask:clickTasks){
            list.add(clickTask.getUserId());
        }

        return list;
    }

    public ClickTask makeClickTask(Cursor c){
        ClickTask clickTask=new ClickTask();

        clickTask.setGroupId(c.getInt(c.getColumnIndex(GROUP_ID)));
        clickTask.setUserId(c.getString(c.getColumnIndex(USER_ID)));
        clickTask.setDate(c.getString(c.getColumnIndex(DATE)));
        clickTask.setIdInGroup(c.getInt(c.getColumnIndex(ID_IN_GROUP)));

        return clickTask;
    }



    public void update(ClickTask clickTask){
        deleteClick(clickTask.getGroupId(), clickTask.getUserId(), clickTask.getIdInGroup());
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
