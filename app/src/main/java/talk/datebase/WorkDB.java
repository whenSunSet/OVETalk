package talk.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import talk.Globle.GlobleData;
import talk.model.Work;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class WorkDB {
    public static final String WORK_TABLE_NAME="work";
    public  static final String GROUP_ID ="groupId";
    private static final String TASK_ID="idInGroup";
    private static final String ID="idInTask";
    private static final String TYPE = "type";
    private static final String PATH= "path";
    private static final String MASTER= "master";
    private static final String CLICK_NUMBER = "clickNumber";
    private static final String DATE= "date";

    private SQLiteDatabase mDb;
    public WorkDB(Context context) {
        mDb = context.openOrCreateDatabase(GlobleData.GROUP_DB_NAME, Context.MODE_PRIVATE, null);
        createTable();
    }

    public void createTable() {
        mDb.execSQL("CREATE table IF NOT EXISTS " + WORK_TABLE_NAME + " ( "
                + TASK_ID + " INTEGER, "
                + GROUP_ID + " INTEGER, "
                + ID + " INTEGER, "
                + TYPE + " INTEGER ,"
                + DATE + " TEXT , "
                + PATH + " TEXT , "
                + MASTER + " TEXT , "
                + CLICK_NUMBER + " INTEGER,"
                + "foreign key (" + GROUP_ID + "," + TASK_ID + ") references " + "task(groupId,idInGroup) "
                + "PRIMARY KEY (" + ID + "," + GROUP_ID + "," + TASK_ID + "))");
    }

    public void adds(ArrayList<Work> list){
        for (Work work:list) {
            add(work);
        }

    }

    public void add(Work work) {
        if (getTaskWork(work.getGroupId(),work.getTaskId(),work.getIdInTask())==null){
            update(work);
        }
        mDb.execSQL("insert into " + WORK_TABLE_NAME + " ("
                        + TASK_ID + ","
                        + GROUP_ID + ","
                        + ID + ","
                        + TYPE + ","
                        + DATE + ","
                        + PATH + ","
                        + MASTER + ","
                        + CLICK_NUMBER + ") values(?,?,?,?,?,?,?,?)",
                new Object[]{
                        work.getTaskId(),
                        work.getGroupId(),
                        work.getIdInTask(),
                        work.getType(),
                        work.getDate(),
                        work.getPath(),
                        work.getMaster(),
                        work.getClickNum()}
        );
    }

    public void delWork(int groupId,int taskId,int idInTask ){
        mDb.execSQL("delete from "
                        + WORK_TABLE_NAME
                        + " where "
                        + GROUP_ID + "=? AND "
                        + TASK_ID + "=? AND "
                        + ID +"=?",
                new Object[]{
                        groupId,
                        taskId,
                        idInTask});
    }

    public void update(Work work){
        delWork(work.getGroupId(),work.getTaskId(),work.getIdInTask());
        add(work);
    }

    public void update(int clickNum,int taskId,int idInTask,int groupId){
        mDb.execSQL("update "
                        + WORK_TABLE_NAME
                        + " set "
                        + CLICK_NUMBER + " =?"
                        + " where "
                        + ID + "=? AND "
                        + TASK_ID + "=? AND "
                        + GROUP_ID + "=?",
                new Object[]{
                        clickNum,
                        idInTask,
                        taskId,
                        groupId});
    }

    public Work getWork(int groupId,int taskId,int idInTask){
        Work work=new Work();
        Cursor c = mDb.rawQuery("select * from "+WORK_TABLE_NAME
                        + " where "
                        + GROUP_ID + "=? AND "
                        + TASK_ID + "=? AND "
                        + ID +"=?",
                new String []{
                        String .valueOf(groupId),
                        String.valueOf(taskId),
                        String.valueOf(idInTask)});

        if (c.moveToFirst()) {
            work=makeWork(c);
        }
        c.close();
        return work;

    }



    public  List<Work> getTaskWorks(int groupId,int taskId){
        List<Work> list=new ArrayList<>();
        Cursor c = mDb.rawQuery("select * from "+WORK_TABLE_NAME
                        + " where "
                        + GROUP_ID + "=? AND "
                        + TASK_ID +"=?",
                new String []{
                        String .valueOf(groupId),
                        String.valueOf(taskId)});

        while (c.moveToNext()) {list.add(makeWork(c));}
        Collections.reverse(list);
        c.close();
        return list;
    }
    public  Work getTaskWork(int groupId,int taskId,int idInTask){
        Work work=null;
        Cursor c = mDb.rawQuery("select * from "+WORK_TABLE_NAME
                        + " where "
                        + GROUP_ID + "=? AND "
                        + ID + "=? AND "
                        + TASK_ID +"=?",
                new String []{
                        String .valueOf(groupId),
                        String.valueOf(idInTask),
                        String.valueOf(taskId)});

        if (c.moveToNext()){
           work = makeWork(c);
        }
        c.close();
        return work;
    }


    public  int getTaskWorkNum(int groupId,int taskId){
        Cursor c = mDb.rawQuery("select * from "+WORK_TABLE_NAME
                        + " where "
                        + GROUP_ID + "=? AND "
                        + TASK_ID +"=?",
                new String []{
                        String .valueOf(groupId),
                        String.valueOf(taskId)});
        int a=0;
        while (c.moveToNext()) {a++;}
        c.close();
        return a;
    }
    public ArrayList<Work> getGroupWork(int groupId) {
        ArrayList<Work> list=new ArrayList<>();
        Cursor c = mDb.rawQuery("select * from "+WORK_TABLE_NAME+" where "+ GROUP_ID +"=?",new String []{String .valueOf(groupId)});

        while (c.moveToNext()) {list.add(makeWork(c));}
        Collections.reverse(list);
        c.close();
        return list;
    }

    public Work makeWork(Cursor c){
        Work work=new Work();
        work.setGroupId(c.getInt(c.getColumnIndex(GROUP_ID)));
        work.setIdInTask(c.getInt(c.getColumnIndex(ID)));
        work.setTaskId(c.getInt(c.getColumnIndex(TASK_ID)));
        work.setDate(c.getString(c.getColumnIndex(DATE)));
        work.setClickNum(c.getInt(c.getColumnIndex(CLICK_NUMBER)));
        work.setType(c.getInt(c.getColumnIndex(TYPE)));
        work.setPath(c.getString(c.getColumnIndex(PATH)));
        work.setMaster(c.getString(c.getColumnIndex(MASTER)));
        return work;
    }

    public void delTable(int groupId) {
        mDb.execSQL("DROP TABLE " + WORK_TABLE_NAME);
    }


}
