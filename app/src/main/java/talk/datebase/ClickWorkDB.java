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

    public static final String GROUP_ID ="groupId";
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
                + GROUP_ID + " TEXT, "
                + TASK_ID + " INTEGER,"
                + WORK_ID + " INTEGER,"
                + DATE + " TEXT,"
                + "foreign key (" + GROUP_ID + "," + TASK_ID + "," + WORK_ID + ") references " + "work(groupId,idInGroup,idInTask) "
                + "PRIMARY KEY (" + USER_ID + "," + GROUP_ID + "," + TASK_ID + "," + WORK_ID + "))");

    }



    public void add(ClickWork clickWork) {
        if (getClick(clickWork.getGroupId(),clickWork.getUserId(),clickWork.getTaskId(),clickWork.getWorkId())!=null){
            update(clickWork);
            return;
        }
        mDb.execSQL(
                "insert into "
                        + CLICK_WORK_TABLE_NAME
                        + " (" + GROUP_ID + ","
                        + TASK_ID + ","
                        + DATE + ","
                        + WORK_ID + ","
                        + USER_ID + ") values(?,?,?,?,?)",
                new Object[]{
                        clickWork.getGroupId(),
                        clickWork.getTaskId(),
                        clickWork.getDate(),
                        clickWork.getWorkId(),
                        clickWork.getUserId()});

    }

    public void deleteClick(String groupId,String userId,int taskId,int workId) {
        mDb.execSQL("delete from " + CLICK_WORK_TABLE_NAME + " where " + USER_ID + "=? AND "+ WORK_ID + "=? AND" + GROUP_ID + "=? AND"+TASK_ID + "=?",
                new Object[]{userId, workId,groupId,taskId});
    }


    public void deleteClick(String groupId,String userId) {
        mDb.execSQL("delete from " + CLICK_WORK_TABLE_NAME + " where " + USER_ID + "=? AND "+ GROUP_ID + "=?",
                new Object[]{userId, groupId});
    }


    public ClickWork getClick(String groupId,String userId,int taskId,int workId){
        Cursor c=mDb.rawQuery("select * from " + CLICK_WORK_TABLE_NAME + " where " + USER_ID + "=? AND "+ WORK_ID + "=? AND " + GROUP_ID + "=? AND "+TASK_ID + "=?",
                new String []{userId,String.valueOf(workId),groupId,String.valueOf(taskId)});
        ClickWork clickWork=null;
        if (c.moveToFirst()){
            clickWork=makeClickWork(c);
        }

        c.close();
        return clickWork;
    }

    public ArrayList<ClickWork> getClicks(String groupId,int taskId,int workId){
        ArrayList<ClickWork> list=new ArrayList<>();
        Cursor c=mDb.rawQuery("select * from " + CLICK_WORK_TABLE_NAME + " where " + WORK_ID + "=? AND " + GROUP_ID + "=? AND "+TASK_ID + "=?",
                new String []{String.valueOf(workId),groupId,String.valueOf(taskId)});
        while (c.moveToFirst()){
            list.add(makeClickWork(c));
        }

        c.close();
        return list;
    }

    public ArrayList<String> getClickMemberName(String groupId,int taskId,int workId){
        ArrayList<String> list=new ArrayList<>();
        ArrayList<ClickWork> clickWorks=getClicks(groupId,taskId,workId);

        for (ClickWork clickWork:clickWorks){
            list.add(clickWork.getUserId());
        }

        return list;
    }

    public ClickWork makeClickWork(Cursor c){
        ClickWork clickWork=new ClickWork();

        clickWork.setGroupId(c.getString(c.getColumnIndex(GROUP_ID)));
        clickWork.setUserId(c.getString(c.getColumnIndex(USER_ID)));
        clickWork.setDate(c.getString(c.getColumnIndex(DATE)));
        clickWork.setTaskId(c.getInt(c.getColumnIndex(TASK_ID)));
        clickWork.setWorkId(c.getInt(c.getColumnIndex(WORK_ID)));

        return clickWork;
    }


    public void update(ClickWork clickWork){
        deleteClick(clickWork.getGroupId(), clickWork.getUserId(), clickWork.getTaskId(),clickWork.getWorkId());
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
