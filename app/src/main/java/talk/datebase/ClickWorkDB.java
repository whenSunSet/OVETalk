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
    public static final String TASK_ID="taskId";
    public static final String ID_IN_TASK ="idInTask";
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
                + GROUP_ID + " INTEGER, "
                + TASK_ID + " INTEGER,"
                + ID_IN_TASK + " INTEGER,"
                + DATE + " TEXT,"
                + "foreign key (" + GROUP_ID + "," + TASK_ID + "," + ID_IN_TASK + ") references " + "work(groupId,idInGroup,idInTask) "
                + "PRIMARY KEY (" + USER_ID + "," + GROUP_ID + "," + TASK_ID + "," + ID_IN_TASK + "))");

    }

    public void adds(ArrayList<ClickWork> list){
        for (ClickWork clickWork:list){
            add(clickWork);
        }
    }

    public void add(ClickWork clickWork) {
        if (getClick(clickWork.getGroupId(),clickWork.getUserId(),clickWork.getTaskId(),clickWork.getIdInTask())!=null){
            update(clickWork);
            return;
        }
        mDb.execSQL(
                "insert into "
                        + CLICK_WORK_TABLE_NAME
                        + " (" + GROUP_ID + ","
                        + TASK_ID + ","
                        + DATE + ","
                        + ID_IN_TASK + ","
                        + USER_ID + ") values(?,?,?,?,?)",
                new Object[]{
                        clickWork.getGroupId(),
                        clickWork.getTaskId(),
                        clickWork.getDate(),
                        clickWork.getIdInTask(),
                        clickWork.getUserId()});
    }

    public void deleteClick(int groupId,String userId,int taskId,int workId) {
        mDb.execSQL("delete from " + CLICK_WORK_TABLE_NAME + " where " + USER_ID + "=? AND "+ ID_IN_TASK + "=? AND" + GROUP_ID + "=? AND"+TASK_ID + "=?",
                new Object[]{userId, workId,groupId,taskId});
    }


    public void deleteClick(int groupId,String userId) {
        mDb.execSQL("delete from " + CLICK_WORK_TABLE_NAME + " where " + USER_ID + "=? AND "+ GROUP_ID + "=?",
                new Object[]{userId, groupId});
    }


    public ClickWork getClick(int groupId,String userId,int taskId,int workId){
        Cursor c=mDb.rawQuery("select * from " + CLICK_WORK_TABLE_NAME + " where " + USER_ID + "=? AND "+ ID_IN_TASK + "=? AND " + GROUP_ID + "=? AND "+TASK_ID + "=?",
                new String []{userId,String.valueOf(workId),String .valueOf(groupId),String.valueOf(taskId)});
        ClickWork clickWork=null;
        if (c.moveToFirst()){
            clickWork=makeClickWork(c);
        }

        c.close();
        return clickWork;
    }

    public ArrayList<ClickWork> getClicks(int groupId,int taskId,int workId){
        ArrayList<ClickWork> list=new ArrayList<>();
        Cursor c=mDb.rawQuery("select * from " + CLICK_WORK_TABLE_NAME + " where " + ID_IN_TASK + "=? AND " + GROUP_ID + "=? AND "+TASK_ID + "=?",
                new String []{String.valueOf(workId),String .valueOf(groupId),String.valueOf(taskId)});
        while (c.moveToNext()){
            list.add(makeClickWork(c));
        }

        c.close();
        return list;
    }

    public ArrayList<String> getClickMemberName(int groupId,int taskId,int workId){
        ArrayList<String> list=new ArrayList<>();
        ArrayList<ClickWork> clickWorks=getClicks(groupId,taskId,workId);

        for (ClickWork clickWork:clickWorks){
            list.add(clickWork.getUserId());
        }

        return list;
    }

    public ClickWork makeClickWork(Cursor c){
        ClickWork clickWork=new ClickWork();

        clickWork.setGroupId(c.getInt(c.getColumnIndex(GROUP_ID)));
        clickWork.setUserId(c.getString(c.getColumnIndex(USER_ID)));
        clickWork.setDate(c.getString(c.getColumnIndex(DATE)));
        clickWork.setTaskId(c.getInt(c.getColumnIndex(TASK_ID)));
        clickWork.setIdInTask(c.getInt(c.getColumnIndex(ID_IN_TASK)));

        return clickWork;
    }


    public void update(ClickWork clickWork){
        deleteClick(clickWork.getGroupId(), clickWork.getUserId(), clickWork.getTaskId(),clickWork.getIdInTask());
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
