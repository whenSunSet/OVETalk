package talk.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;

import talk.Globle.GlobleData;
import talk.model.Task;

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
                + GROUP_Id + " TEXT, "
                + TARGET + " TEXT, "
                + TYPE + " INTEGER,"
                + DATE + " TEXT, "
                + PATH + " TEXT, "
                + CLICK_NUMBER + " INTEGER,"
                + "foreign key (" + GROUP_Id + ") references " + "groups(groupId) "
                + "PRIMARY KEY (" + ID + "," + GROUP_Id + "))");
    }


    public void add(Task task) {

        mDb.execSQL("insert into " + TASK_TABLE_NAME + " ("
                        + ID + ","
                        + GROUP_Id + ","
                        + TARGET + ","
                        + TYPE + ","
                        + DATE + ","
                        + PATH + ","
                        + CLICK_NUMBER + ") values(?,?,?,?,?,?,?)",
                new Object[]{
                        task.getIdInGroup(),
                        task.getGroupId(),
                        task.getTarget(),
                        task.getType(),
                        task.getDate(),
                        task.getPath(),
                        task.getClickNumber()}
        );
    }

    public void deltask(String groupId,int idInGroup){
            mDb.execSQL("delete from "
                    + TASK_TABLE_NAME
                    + " where "
                    + ID + "=? AND "
                    + GROUP_Id +"=?",
                    new Object[]{
                            idInGroup,
                            groupId});
    }

    public void update(Task task){
        deltask(task.getGroupId(),task.getIdInGroup());
        add(task);
    }

    public void update(int clickNum,int idInGroup ,String groupId){
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
    public void update(String path,int idInGroup ,String groupId){
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
    public Task getTask(String groupId,int idInGroup){
        Task task=new Task();
        Cursor c = mDb.rawQuery("select * from "+TASK_TABLE_NAME+" where "+ ID + "=? AND " + GROUP_Id +"=?",
                new String []{String.valueOf(idInGroup),groupId});

        if (c.moveToFirst()) {
            task.setGroupId(c.getString(c.getColumnIndex(GROUP_Id)));
            task.setIdInGroup(c.getInt(c.getColumnIndex(ID)));
            task.setDate(c.getString(c.getColumnIndex(DATE)));
            task.setClickNumber(c.getInt(c.getColumnIndex(CLICK_NUMBER)));
            task.setType(c.getInt(c.getColumnIndex(TYPE)));
            task.setPath(c.getString(c.getColumnIndex(PATH)));
            task.setTarget(c.getString(c.getColumnIndex(TARGET)));
        }
        c.close();
        return task;

    }

    public ArrayList<Task> getGroupTask(String groupId) {
        ArrayList<Task> tasks= new ArrayList<>();
        Cursor c = mDb.rawQuery("select * from "+TASK_TABLE_NAME+" where "+ GROUP_Id +"=?",new String []{groupId});

        while (c.moveToNext()) {
            Task task=new Task();
            task.setGroupId(c.getString(c.getColumnIndex(GROUP_Id)));
            task.setIdInGroup(c.getInt(c.getColumnIndex(ID)));
            task.setDate(c.getString(c.getColumnIndex(DATE)));
            task.setClickNumber(c.getInt(c.getColumnIndex(CLICK_NUMBER)));
            task.setType(c.getInt(c.getColumnIndex(TYPE)));
            task.setPath(c.getString(c.getColumnIndex(PATH)));
            task.setTarget(c.getString(c.getColumnIndex(TARGET)));
            tasks.add(task);
        }
        Collections.reverse(tasks);
        c.close();
        return tasks;
    }
    public int getGroupTaskNum(String groupName) {
        Cursor c = mDb.rawQuery("select * from "+TASK_TABLE_NAME+" where "+ GROUP_Id +"=?",new String []{groupName});
        int a=0;
        while (c.moveToNext()) {
            a++;
        }
        c.close();
        return a;
    }
    public void delTable(String groupName) {
        mDb.execSQL("DROP TABLE " + TASK_TABLE_NAME);
    }
}
