package talk.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;

import talk.Globle.GlobleData;
import talk.model.TaskBean;

public class TaskDB {
    public static final String TASK_TABLE_NAME="task";
    public  static final String GROUP_Id ="groupId";
    private static final String ID="idInGroup";
    private static final String TYPE = "type";
    private static final String PATH= "path";
    private static final String TARGET = "target";
    private static final String CLICK_NUMBER = "clickNumber";
    private static final String DATE= "date";

    private SQLiteDatabase mDb;
    public TaskDB(Context context) {
        mDb = context.openOrCreateDatabase(GlobleData.GROUP_DB_NAME, Context.MODE_PRIVATE, null);
        createTable();
    }

    public void createTable() {
        mDb.execSQL("CREATE table IF NOT EXISTS " + TASK_TABLE_NAME + " ( "
                + ID + " INTEGER, "
                + GROUP_Id + " INTEGER, "
                + TARGET + " TEXT, "
                + TYPE + " INTEGER,"
                + DATE + " TEXT, "
                + PATH + " TEXT, "
                + CLICK_NUMBER + " INTEGER,"
                + "foreign key (" + GROUP_Id + ") references " + "groups(groupId) "
                + "PRIMARY KEY (" + ID + "," + GROUP_Id + "))");
    }

    public void adds(ArrayList<TaskBean> list){
        for (TaskBean taskBean :list){
            add(taskBean);
        }
    }

    public void add(TaskBean taskBean) {
        if (getTask(taskBean.getGroupId(), taskBean.getIdInGroup())!=null){
            return;
        }
        mDb.execSQL("insert into " + TASK_TABLE_NAME + " ("
                        + ID + ","
                        + GROUP_Id + ","
                        + TARGET + ","
                        + TYPE + ","
                        + DATE + ","
                        + PATH + ","
                        + CLICK_NUMBER + ") values(?,?,?,?,?,?,?)",
                new Object[]{
                        taskBean.getIdInGroup(),
                        taskBean.getGroupId(),
                        taskBean.getTarget(),
                        taskBean.getType(),
                        taskBean.getDate(),
                        taskBean.getPath(),
                        taskBean.getClickNum()}
        );
    }

    public void deltask(int groupId,int idInGroup){
            mDb.execSQL("delete from "
                    + TASK_TABLE_NAME
                    + " where "
                    + ID + "=? AND "
                    + GROUP_Id +"=?",
                    new Object[]{
                            idInGroup,
                            groupId});
    }

    public void update(TaskBean taskBean){
        deltask(taskBean.getGroupId(), taskBean.getIdInGroup());
        add(taskBean);
    }

    public void update(int clickNum,int idInGroup ,int groupId){
        mDb.execSQL("update "
                        + TASK_TABLE_NAME
                        + " set "
                        + CLICK_NUMBER + " =?"
                        + " where "
                        + ID + "=? AND "
                        + GROUP_Id + "=?",
                new Object[]{
                        clickNum,
                        idInGroup,
                        groupId});
    }
    public void update(String path,int idInGroup ,int groupId){
        mDb.execSQL("update "
                        + TASK_TABLE_NAME
                        + " set "
                        + PATH + " =?"
                        + " where "
                        + ID + "=? AND "
                        + GROUP_Id + "=?",
                new Object[]{
                        path,
                        idInGroup,
                        groupId});
    }
    public TaskBean getTask(int groupId, int idInGroup){
        TaskBean taskBean =new TaskBean();
        Cursor c = mDb.rawQuery("select * from "+TASK_TABLE_NAME+" where "+ ID + "=? AND " + GROUP_Id +"=?",
                new String []{String.valueOf(idInGroup),String.valueOf(groupId)});

        if (c.moveToFirst()) {
            taskBean.setGroupId(c.getInt(c.getColumnIndex(GROUP_Id)));
            taskBean.setIdInGroup(c.getInt(c.getColumnIndex(ID)));
            taskBean.setDate(c.getString(c.getColumnIndex(DATE)));
            taskBean.setClickNum(c.getInt(c.getColumnIndex(CLICK_NUMBER)));
            taskBean.setType(c.getInt(c.getColumnIndex(TYPE)));
            taskBean.setPath(c.getString(c.getColumnIndex(PATH)));
            taskBean.setTarget(c.getString(c.getColumnIndex(TARGET)));
        }
        c.close();
        return taskBean;

    }

    public ArrayList<TaskBean> getGroupTask(int groupId) {
        ArrayList<TaskBean> taskBeen = new ArrayList<>();
        Cursor c = mDb.rawQuery("select * from "+TASK_TABLE_NAME+" where "+ GROUP_Id +"=?",new String []{String .valueOf(groupId)});

        while (c.moveToNext()) {
            TaskBean taskBean =new TaskBean();
            taskBean.setGroupId(c.getInt(c.getColumnIndex(GROUP_Id)));
            taskBean.setIdInGroup(c.getInt(c.getColumnIndex(ID)));
            taskBean.setDate(c.getString(c.getColumnIndex(DATE)));
            taskBean.setClickNum(c.getInt(c.getColumnIndex(CLICK_NUMBER)));
            taskBean.setType(c.getInt(c.getColumnIndex(TYPE)));
            taskBean.setPath(c.getString(c.getColumnIndex(PATH)));
            taskBean.setTarget(c.getString(c.getColumnIndex(TARGET)));
            taskBeen.add(taskBean);
        }
        Collections.reverse(taskBeen);
        c.close();
        return taskBeen;
    }
    public int getGroupTaskNum(int groupId) {
        Cursor c = mDb.rawQuery("select * from "+TASK_TABLE_NAME+" where "+ GROUP_Id +"=?",new String []{String .valueOf(groupId)});
        int a=0;
        while (c.moveToNext()) {
            a++;
        }
        c.close();
        return a;
    }
    public void delTable(int groupId) {
        mDb.execSQL("DROP TABLE " + TASK_TABLE_NAME);
    }
}
