package talk.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import talk.Globle.GlobleData;
import talk.model.User;

/**
 * Created by asus on 2015/11/18.
 */
public class UserDB {
    public static final String USER_TABLE_NAME ="user";

    private static final String USER_ID="id";
    private static final String USER_ICON = "userIcon";
    private static final String USER_NICK_NAME = "userNickName";

    private SQLiteDatabase mDb;

    public UserDB(Context context)  {
        mDb = context.openOrCreateDatabase(GlobleData.GROUP_DB_NAME, Context.MODE_PRIVATE, null);
        createTable();
    }

    public void createTable() {
        mDb.execSQL("CREATE table IF NOT EXISTS " + USER_TABLE_NAME
                + " (" + USER_ID + " TEXT PRIMARY KEY, " //
                + USER_ICON + " TEXT, "//
                + USER_NICK_NAME + " TEXT)"); //
    }

    public void delTable() {
        mDb.execSQL("DROP TABLE " + USER_TABLE_NAME);
    }


    public void add(User user) {
        if (getMember(user.getUserID())!=null){
            update(user);
            return;
        }
        mDb.execSQL(
                "insert into "
                        + USER_TABLE_NAME
                        + " (" + USER_ICON + ","
                        + USER_NICK_NAME + ","
                        + USER_ID + ") values(?,?,?,?)",
                new Object[]{
                        user.getUserIcon(),
                        user.getUserNickName(),
                        user.getUserID()});

    }

    public void deleteMember(String userId) {
        mDb.execSQL("delete from " + USER_TABLE_NAME + " where " + USER_ID + "=?",
                new Object[]{userId});
    }

    public User getMember(String userId){
        Cursor c=mDb.rawQuery("select from " +USER_TABLE_NAME+ " where " + USER_ID+ "=?",
                new String[]{userId});
        User user =null;
        if (c.moveToFirst()){
            user =new User();
            user.setUserIcon(c.getString(c.getColumnIndex(USER_ICON)));
            user.setUserNickName(c.getString(c.getColumnIndex(USER_NICK_NAME)));
            user.setUserID(c.getString(c.getColumnIndex(USER_ID)));
        }

        c.close();
        return user;
    }

    /**
     *
     获得所有的member
     */
    public ArrayList<User> getMembers() {
        ArrayList<User> list = new ArrayList<>();
        Cursor c = mDb.rawQuery("select * from "+USER_TABLE_NAME, null);
        while (c.moveToNext())
        {
            User u = new User();
            u.setUserNickName(c.getString(c.getColumnIndex(USER_NICK_NAME)));
            u.setUserIcon(c.getString(c.getColumnIndex(USER_ICON)));
            u.setUserID(c.getString(c.getColumnIndex(USER_ID)));
            list.add(u);
        }
        c.close();
        return list;
    }


    public void update(User user){
        deleteMember(user.getUserID());
        add(user);

    }

    /**
     *
     获得所有member的NickName
     */
    public List<String> getGroupMenmberNickNames() {
        List<String> list = new LinkedList<String>();
        Cursor c = mDb.rawQuery("select "+USER_NICK_NAME+" from "+ USER_TABLE_NAME, null);
        while (c.moveToNext())
        {
            String userNickName = c.getString(c.getColumnIndex(USER_NICK_NAME));
            list.add(userNickName);
        }
        c.close();
        return list;
    }

    public void close() {
        if (mDb != null && mDb.isOpen())
            mDb.close();
    }

}
