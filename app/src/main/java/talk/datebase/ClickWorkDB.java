package talk.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import talk.Globle.GlobleData;
import talk.model.ClickWork;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class ClickWorkDB {
    public static final String CLICK_WORK_TABLE_NAME="clickWork";

    public static final String GROUP_NAME="groupName";
    public static final String TASK_ID="idInGroup";
    public static final String WORK_ID="idInTask";
    public static final String USER_ID="userId";
    public static final String DATE="date";

    private SQLiteDatabase mDb;

    public ClickWorkDB(Context context) {
        mDb = context.openOrCreateDatabase(GlobleData.GROUP_DB_NAME, Context.MODE_PRIVATE, null);
        createTable();
    }


    public void createTable() {
        mDb.execSQL("CREATE table IF NOT EXISTS " + CLICK_WORK_TABLE_NAME
                + " (" + USER_ID + " TEXT,"
                + GROUP_NAME + " TEXT, "
                + TASK_ID + " INTEGER,"
                + WORK_ID + " INTEGER,"
                + DATE + " TEXT,"
                + "foreign key (" + GROUP_NAME + "," + TASK_ID + "," + WORK_ID + ") references " + "work(groupName,idInGroup,idInTask) "
                + "PRIMARY KEY (" + USER_ID + "," + GROUP_NAME + "," + TASK_ID + "," + WORK_ID + "))");

    }



    public void add(ClickWork clickWork) {
        if (getClick(clickWork.getGroupName(),clickWork.getUserId(),clickWork.getTaskId(),clickWork.getWorkId())!=null){
            update(clickWork);
            return;
        }
        mDb.execSQL(
                "insert into "
                        + CLICK_WORK_TABLE_NAME
                        + " (" + GROUP_NAME + ","
                        + TASK_ID + ","
                        + DATE + ","
                        + WORK_ID + ","
                        + USER_ID + ") values(?,?,?,?)",
                new Object[]{
                        clickWork.getGroupName(),
                        clickWork.getTaskId(),
                        clickWork.getDate(),
                        clickWork.getWorkId(),
                        clickWork.getUserId()});

    }

    public void deleteClick(String groupName,String userId,int taskId,int workId) {
        mDb.execSQL("delete from " + CLICK_WORK_TABLE_NAME + " where " + USER_ID + "=? AND "+ WORK_ID + "=? AND" + GROUP_NAME + "=? AND"+TASK_ID + "=?",
                new Object[]{userId, workId,groupName,taskId});
    }


    public void deleteClick(String groupName,String userId) {
        mDb.execSQL("delete from " + CLICK_WORK_TABLE_NAME + " where " + USER_ID + "=? AND "+ GROUP_NAME + "=?",
                new Object[]{userId, groupName});
    }


    public ClickWork getClick(String groupName,String userId,int taskId,int workId){
        Cursor c=mDb.rawQuery("select from " + CLICK_WORK_TABLE_NAME + " where " + USER_ID + "=? AND "+ WORK_ID + "=? AND" + GROUP_NAME + "=? AND"+TASK_ID + "=?",
                new String []{userId,String.valueOf(workId),groupName,String.valueOf(taskId)});
        ClickWork clickWork=null;
        if (c.moveToFirst()){
            clickWork=makeClickWork(c);
        }

        c.close();
        return clickWork;
    }

    public ArrayList<ClickWork> getClicks(String groupName,int taskId,int workId){
        ArrayList<ClickWork> list=new ArrayList<>();
        Cursor c=mDb.rawQuery("select from " + CLICK_WORK_TABLE_NAME + " where " + WORK_ID + "=? AND" + GROUP_NAME + "=? AND"+TASK_ID + "=?",
                new String []{String.valueOf(workId),groupName,String.valueOf(taskId)});
        while (c.moveToFirst()){
            list.add(makeClickWork(c));
        }

        c.close();
        return list;
    }

    public ArrayList<String> getClickMemberName(String groupName,int taskId,int workId){
        ArrayList<String> list=new ArrayList<>();
        ArrayList<ClickWork> clickWorks=getClicks(groupName,taskId,workId);

        for (ClickWork clickWork:clickWorks){
            list.add(clickWork.getUserId());
        }

        return list;
    }

    public ClickWork makeClickWork(Cursor c){
        ClickWork clickWork=new ClickWork();

        clickWork.setGroupName(c.getString(c.getColumnIndex(GROUP_NAME)));
        clickWork.setUserId(c.getString(c.getColumnIndex(USER_ID)));
        clickWork.setDate(c.getString(c.getColumnIndex(DATE)));
        clickWork.setTaskId(c.getInt(c.getColumnIndex(TASK_ID)));
        clickWork.setWorkId(c.getInt(c.getColumnIndex(WORK_ID)));

        return clickWork;
    }


    public void update(ClickWork clickWork){
        deleteClick(clickWork.getGroupName(), clickWork.getUserId(), clickWork.getTaskId(),clickWork.getWorkId());
        add(clickWork);

    }



    public void delTable() {
        mDb.execSQL("DROP TABLE " + CLICK_WORK_TABLE_NAME);
    }
    public void close() {
        if (mDb != null && mDb.isOpen())
            mDb.close();
    }
}
