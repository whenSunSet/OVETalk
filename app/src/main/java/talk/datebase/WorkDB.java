package talk.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import talk.Globle.GlobleData;
import talk.model.WorkBean;

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

    public void adds(ArrayList<WorkBean> list){
        for (WorkBean workBean :list) {
            add(workBean);
        }

    }

    public void add(WorkBean workBean) {
        if (getTaskWork(workBean.getGroupId(), workBean.getTaskId(), workBean.getIdInTask())==null){
            update(workBean);
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
                        workBean.getTaskId(),
                        workBean.getGroupId(),
                        workBean.getIdInTask(),
                        workBean.getType(),
                        workBean.getDate(),
                        workBean.getPath(),
                        workBean.getMaster(),
                        workBean.getClickNum()}
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

    public void update(WorkBean workBean){
        delWork(workBean.getGroupId(), workBean.getTaskId(), workBean.getIdInTask());
        add(workBean);
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

    public WorkBean getWork(int groupId, int taskId, int idInTask){
        WorkBean workBean =new WorkBean();
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
            workBean =makeWork(c);
        }
        c.close();
        return workBean;

    }



    public  List<WorkBean> getTaskWorks(int groupId, int taskId){
        List<WorkBean> list=new ArrayList<>();
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
    public WorkBean getTaskWork(int groupId, int taskId, int idInTask){
        WorkBean workBean =null;
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
           workBean = makeWork(c);
        }
        c.close();
        return workBean;
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
    public ArrayList<WorkBean> getGroupWork(int groupId) {
        ArrayList<WorkBean> list=new ArrayList<>();
        Cursor c = mDb.rawQuery("select * from "+WORK_TABLE_NAME+" where "+ GROUP_ID +"=?",new String []{String .valueOf(groupId)});

        while (c.moveToNext()) {list.add(makeWork(c));}
        Collections.reverse(list);
        c.close();
        return list;
    }

    public WorkBean makeWork(Cursor c){
        WorkBean workBean =new WorkBean();
        workBean.setGroupId(c.getInt(c.getColumnIndex(GROUP_ID)));
        workBean.setIdInTask(c.getInt(c.getColumnIndex(ID)));
        workBean.setTaskId(c.getInt(c.getColumnIndex(TASK_ID)));
        workBean.setDate(c.getString(c.getColumnIndex(DATE)));
        workBean.setClickNum(c.getInt(c.getColumnIndex(CLICK_NUMBER)));
        workBean.setType(c.getInt(c.getColumnIndex(TYPE)));
        workBean.setPath(c.getString(c.getColumnIndex(PATH)));
        workBean.setMaster(c.getString(c.getColumnIndex(MASTER)));
        return workBean;
    }

    public void delTable(int groupId) {
        mDb.execSQL("DROP TABLE " + WORK_TABLE_NAME);
    }


}
