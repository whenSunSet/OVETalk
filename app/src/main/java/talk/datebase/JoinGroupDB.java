package talk.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import talk.Globle.GlobleData;
import talk.model.JoinGroup;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class JoinGroupDB {
    public static final String JOIN_TABLE_NAME="joinGroup";
    
    public static final String GROUP_NAME="groupName";
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
                + GROUP_NAME + " TEXT, "//
                + DATE + " TEXT "
                + "foreign key (" + GROUP_NAME + ") references " + "group(groupName) "
                + "PRIMARY KEY (" + USER_ID + "," + GROUP_NAME + "))");
        
    }



    public void add(JoinGroup joinGroup) {
        if (getMember(joinGroup.getGroupName(),joinGroup.getMemberId())!=null){
            update(joinGroup);
            return;
        }
        mDb.execSQL(
                "insert into "
                        + JOIN_TABLE_NAME
                        + " (" + GROUP_NAME + ","
                        + DATE + ","
                        + USER_ID + ") values(?,?,?,?)",
                new Object[]{
                        joinGroup.getGroupName(),
                        joinGroup.getDate(),
                        joinGroup.getMemberId()});

    }

    public void deleteMember(String groupName,String userId) {
        mDb.execSQL("delete from " + JOIN_TABLE_NAME + " where " + USER_ID + "=? AND " + GROUP_NAME + "=?",
                new Object[]{userId, groupName});
    }


    public JoinGroup getMember(String groupName,String userId){
        Cursor c=mDb.rawQuery("select from " + JOIN_TABLE_NAME + " where "+ USER_ID + "=? AND " + GROUP_NAME +"=?",
                new String []{userId,groupName});
        JoinGroup joinGroup=new JoinGroup();
        if (c.moveToFirst()){
            joinGroup.setGroupName(c.getString(c.getColumnIndex(GROUP_NAME)));
            joinGroup.setMemberId(c.getString(c.getColumnIndex(USER_ID)));
            joinGroup.setDate(c.getString(c.getColumnIndex(DATE)));

        }

        c.close();
        return joinGroup;
    }




    public void update(JoinGroup joinGroup){
        deleteMember(joinGroup.getGroupName(),joinGroup.getMemberId());
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
