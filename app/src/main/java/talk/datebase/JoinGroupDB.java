package talk.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import talk.Globle.GlobleData;
import talk.model.JoinGroup;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class JoinGroupDB {
    public static final String JOIN_TABLE_NAME="joinOrExitGroup";
    
    public static final String GROUP_Id ="groupId";
    public static final String USER_ID="memberId";
    public static final String DATE="date";

    private SQLiteDatabase mDb;

    public JoinGroupDB(Context context) {
        mDb = context.openOrCreateDatabase(GlobleData.GROUP_DB_NAME, Context.MODE_PRIVATE, null);
        createTable();
    }


    public void createTable() {
        mDb.execSQL("CREATE table IF NOT EXISTS " + JOIN_TABLE_NAME
                + " (" + USER_ID + " TEXT, " //
                + GROUP_Id + " INTEGER, "//
                + DATE + " TEXT,"
                + "foreign key (" + GROUP_Id + ") references " + "groups(groupId) "
                + "PRIMARY KEY (" + USER_ID + "," + GROUP_Id + "))");
        
    }

    public void adds(ArrayList<JoinGroup> list){
        for (JoinGroup joinGroup:list){
            add(joinGroup);
        }
    }

    public void add(JoinGroup joinGroup) {
        if (getMember(joinGroup.getGroupId(),joinGroup.getUserId())!=null){
            update(joinGroup);
            return;
        }
        mDb.execSQL(
                "insert into "
                        + JOIN_TABLE_NAME
                        + " (" + GROUP_Id + ","
                        + DATE + ","
                        + USER_ID + ") values(?,?,?)",
                new Object[]{
                        joinGroup.getGroupId(),
                        joinGroup.getDate(),
                        joinGroup.getUserId()});
    }

    public void deleteMember(int groupId,String userId) {
        mDb.execSQL("delete from " + JOIN_TABLE_NAME + " where " + USER_ID + "=? AND " + GROUP_Id + "=?",
                new Object[]{userId, groupId});
    }


    public JoinGroup getMember(int groupId,String userId){
        Cursor c=mDb.rawQuery("select * from " + JOIN_TABLE_NAME + " where "+ USER_ID + "=? AND " + GROUP_Id +"=?",
                new String []{userId,String .valueOf(groupId)});
        JoinGroup joinGroup=new JoinGroup();
        if (c.moveToFirst()){
            joinGroup.setGroupId(c.getInt(c.getColumnIndex(GROUP_Id)));
            joinGroup.setUserId(c.getString(c.getColumnIndex(USER_ID)));
            joinGroup.setDate(c.getString(c.getColumnIndex(DATE)));

            c.close();
            return joinGroup;
        }else{
            c.close();
            return null;
        }

    }

    public ArrayList<String> getMembersName(int groupId){
        Cursor c=mDb.rawQuery("select "+USER_ID+" from " + JOIN_TABLE_NAME + " where "+ GROUP_Id +"=?",
                new String []{String .valueOf(groupId)});
        ArrayList<String> list=new ArrayList<>();
        while (c.moveToNext()){
            list.add(c.getString(c.getColumnIndex(USER_ID)));
        }

        c.close();
        return list;
    }



    public void update(JoinGroup joinGroup){
        deleteMember(joinGroup.getGroupId(),joinGroup.getUserId());
        add(joinGroup);

    }



    public void delTable() {
        mDb.execSQL("DROP TABLE " + JOIN_TABLE_NAME);
    }
    public void close() {
        if (mDb != null && mDb.isOpen())
            mDb.close();
    }

}
