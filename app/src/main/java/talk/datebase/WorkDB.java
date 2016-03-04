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
    public  static final String GROUP_NAME="groupName";
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
                + GROUP_NAME + " TEXT, "
                + ID + " INTEGER, "
                + TYPE + " INTEGER ,"
                + DATE + " TEXT , "
                + PATH + " TEXT , "
                + MASTER + " TEXT , "
                + CLICK_NUMBER + " INTEGER,"
                + "foreign key (" + GROUP_NAME + "," + TASK_ID + ") references " + "task(groupName,idInGroup) "
                + "PRIMARY KEY (" + ID + "," + GROUP_NAME + "," + TASK_ID + "))");
    }


    public void add(Work work) {

        mDb.execSQL("insert into " + WORK_TABLE_NAME + " ("
                        + TASK_ID + ","
                        + GROUP_NAME + ","
                        + ID + ","
                        + TYPE + ","
                        + DATE + ","
                        + PATH + ","
                        + MASTER + ","
                        + CLICK_NUMBER + ") values(?,?,?,?,?,?)",
                new Object[]{
                        work.getTaskId(),
                        work.getGroupName(),
                        work.getIdInTask(),
                        work.getType(),
                        work.getDate(),
                        work.getPath(),
                        work.getMaster(),
                        work.getClickNumber()}
        );
    }

    public void delWork(String groupName,int taskId,int idInTask ){
        mDb.execSQL("delete from "
                        + WORK_TABLE_NAME
                        + " where "
                        + GROUP_NAME + "=? AND "
                        + TASK_ID + "=? AND "
                        + ID +"=?",
                new Object[]{
                        groupName,
                        taskId,
                        idInTask});
    }

    public void update(Work work){
        delWork(work.getGroupName(),work.getTaskId(),work.getIdInTask());
        add(work);
    }

    public void update(int clickNum,int taskId,int idInTask,String groupName){
        mDb.execSQL("update "
                        + WORK_TABLE_NAME
                        + " set "
                        + CLICK_NUMBER + " =?"
                        + " where "
                        + ID + "=? AND "
                        + TASK_ID + "=? AND "
                        + GROUP_NAME + "=?",
                new Object[]{
                        clickNum,
                        idInTask,
                        taskId,
                        groupName});
    }

    public Work getWork(String groupName,int taskId,int idInTask){
        Work work=new Work();
        Cursor c = mDb.rawQuery("select * from "+WORK_TABLE_NAME
                        + " where "
                        + GROUP_NAME + "=? AND "
                        + TASK_ID + "=? AND "
                        + ID +"=?",
                new String []{
                        groupName,
                        String.valueOf(taskId),
                        String.valueOf(idInTask)});

        if (c.moveToFirst()) {
            work=makeWork(c);
        }
        c.close();
        return work;

    }



    public  List<Work> getTaskWorks(String groupName,int taskId){
        List<Work> list=new ArrayList<Work>();
        Cursor c = mDb.rawQuery("select * from "+WORK_TABLE_NAME
                        + " where "
                        + GROUP_NAME + "=? AND "
                        + TASK_ID +"=?",
                new String []{
                        groupName,
                        String.valueOf(taskId)});

        while (c.moveToNext()) {list.add(makeWork(c));}
        Collections.reverse(list);
        c.close();
        return list;
    }
    public  Work getTaskWork(String groupName,int taskId,int idInTask){
        Work work=null;
        Cursor c = mDb.rawQuery("select * from "+WORK_TABLE_NAME
                        + " where "
                        + GROUP_NAME + "=? AND "
                        + ID + "=? AND "
                        + TASK_ID +"=?",
                new String []{
                        groupName,
                        String.valueOf(idInTask),
                        String.valueOf(taskId)});

        if (c.moveToNext()){
           work = makeWork(c);
        }
        c.close();
        return work;
    }


    public  int getTaskWorkNum(String groupName,int taskId){
        List<Work> list=new ArrayList<Work>();
        Cursor c = mDb.rawQuery("select * from "+WORK_TABLE_NAME
                        + " where "
                        + GROUP_NAME + "=? AND "
                        + TASK_ID +"=?",
                new String []{
                        groupName,
                        String.valueOf(taskId)});
        int a=0;
        while (c.moveToNext()) {a++;}
        c.close();
        return a;
    }
    public ArrayList<Work> getGroupWork(String groupName) {
        ArrayList<Work> list=new ArrayList<Work>();
        Cursor c = mDb.rawQuery("select * from "+WORK_TABLE_NAME+"where "+GROUP_NAME+"=?",new String []{groupName});

        while (c.moveToNext()) {list.add(makeWork(c));}
        Collections.reverse(list);
        c.close();
        return list;
    }

    public Work makeWork(Cursor c){
        Work work=new Work();
        work.setGroupName(c.getString(c.getColumnIndex(GROUP_NAME)));
        work.setIdInTask(c.getInt(c.getColumnIndex(ID)));
        work.setTaskId(c.getInt(c.getColumnIndex(TASK_ID)));
        work.setDate(c.getString(c.getColumnIndex(DATE)));
        work.setClickNumber(c.getInt(c.getColumnIndex(CLICK_NUMBER)));
        work.setType(c.getInt(c.getColumnIndex(TYPE)));
        work.setPath(c.getString(c.getColumnIndex(PATH)));
        work.setMaster(c.getString(c.getColumnIndex(MASTER)));
        return work;
    }

    public void delTable(String groupName) {
        mDb.execSQL("DROP TABLE " + WORK_TABLE_NAME);
    }


}
