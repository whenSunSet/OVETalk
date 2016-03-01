package talk.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import talk.Globle.GlobleData;
import talk.model.GroupChatMessage;

/**
 * Created by asus on 2015/11/3.
 */
public class GroupMessageDB {

    private static final String COL_MESSAGE = "message";
    // 1：from ; 0:to
    private static final String COL_IS_COMING = "is_coming";
    private static final String COL_GROUP_NAME = "groupName";
    private static final String COL_ICON = "userIcon";
    private static final String COL_USER_NICK_NAME = "userNickName";
    // 1:readed ; 0 unreaded ;
    private static final String COL_READED = "readed";
    private static final String COL_DATE = "date";
    private static final String COL_USER_NAME = "userName";
    private static final String COL_MESSAGE_IMAGE="messageImage";
    private static final String COL_MESSAGE_STATU="messageStatu";

    private SQLiteDatabase mDb;

    public GroupMessageDB(Context context) {
        mDb = context.openOrCreateDatabase(GlobleData.CHAT_DB_NAME, Context.MODE_PRIVATE, null);
    }

    public void createTable(String groupName) {
        mDb.execSQL("CREATE table IF NOT EXISTS _" + groupName
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_GROUP_NAME +" TEXT, "
                + COL_ICON +" TEXT, "
                + COL_IS_COMING +" INTEGER ,"
                + COL_MESSAGE_STATU +" INTEGER ,"
                + COL_MESSAGE +" TEXT , "
                + COL_USER_NICK_NAME +" TEXT , "
                + COL_DATE +" TEXT , "
                + COL_USER_NAME+" TEXT ,"
                + COL_MESSAGE_IMAGE+" TEXT ,"
                + COL_READED +" INTEGER)");
    }
    public void delTable(String groupName) {
        mDb.execSQL("DROP TABLE _"+groupName);
    }

    /**
     * 为每个用户根据其groupName创建一张消息表
     *
     */
    public void add(String groupName, GroupChatMessage groupChatMessage) {
        createTable(groupName);
        int isComing = groupChatMessage.isComing() ? 1 : 0;
        int readed = groupChatMessage.isReaded() ? 1 : 0;

        mDb.execSQL(
                "insert into _" + groupName + " ("
                        + COL_GROUP_NAME + ","
                        + COL_ICON + ","
                        + COL_IS_COMING + ","
                        + COL_MESSAGE + ","
                        + COL_USER_NICK_NAME + ","
                        + COL_DATE + ","
                        + COL_USER_NAME + ","
                        + COL_READED + ","
                        + COL_MESSAGE_STATU+ ","
                        + COL_MESSAGE_IMAGE + ") values(?,?,?,?,?,?,?,?,?,?)",
                new Object[]{groupChatMessage.getGroupName(),
                        groupChatMessage.getUserIcon(),
                        isComing,
                        groupChatMessage.getMessage(),
                        groupChatMessage.getUserNickName(),
                        groupChatMessage.getDateStr(),
                        groupChatMessage.getUserName(),
                        readed,
                        groupChatMessage.getMessageStatu(),
                        groupChatMessage.getMessageImage()});
    }

    /**
     *
     查找指定的最后n条消息记录
     */
    public ArrayList<GroupChatMessage> find(String groupName, int currentPage, int pageSize) {
        ArrayList<GroupChatMessage> groupChatMessages = new ArrayList<GroupChatMessage>();
        createTable(groupName);
        int start = (currentPage - 1) * pageSize;
        int end = start + pageSize;
        // 取最后的10条
        String sql = "select * from _" + groupName + " order by _id  desc limit  "
                + start + " , " + end;
        Cursor c = mDb.rawQuery(sql, null);
        GroupChatMessage groupChatMessage;
        while (c.moveToNext()) {
            groupChatMessage = new GroupChatMessage(
                    c.getString(c.getColumnIndex(COL_MESSAGE)),
                    c.getInt(c.getColumnIndex(COL_IS_COMING))==1,
                    c.getString(c.getColumnIndex(COL_GROUP_NAME)),
                    c.getString(c.getColumnIndex(COL_ICON)),
                    c.getInt(c.getColumnIndex(COL_READED))== 1,
                    c.getString(c.getColumnIndex(COL_DATE)),
                    c.getString(c.getColumnIndex(COL_USER_NICK_NAME)),
                    c.getString(c.getColumnIndex(COL_USER_NAME)),
                    c.getString(c.getColumnIndex(COL_MESSAGE_IMAGE)),
                    c.getInt(c.getColumnIndex(COL_MESSAGE_STATU))
            );

            if (!TextUtils.isEmpty(groupChatMessage.getMessage())){
                groupChatMessages.add(groupChatMessage);
            }
        }
        //将顺序反一下
        Collections.reverse(groupChatMessages);
        c.close();
        return groupChatMessages;
    }

    /**
     *
     获取指定group的消息条数
     */
    public int getMessageNum(String groupName){
        Cursor c = mDb.rawQuery("select * from _"+groupName,null);
        int count=c.getCount();
        c.close();
        return  count;
    }

    /**
     *
     获取指定group的最后一条消息的
     */
    public GroupChatMessage getLastChatMessage(String groupName){
        GroupChatMessage groupChatMessage = null;
        Cursor c = mDb.rawQuery("select * from _" + groupName, null);
        String count=String.valueOf(c.getCount());
        c=mDb.rawQuery("select * from _" + groupName + " where _id= " +count, null);
        if (c.getCount()==0){
            return null;
        }
        if (c.moveToFirst()){
            groupChatMessage = new GroupChatMessage(
                    c.getString(c.getColumnIndex(COL_MESSAGE)),
                    c.getInt(c.getColumnIndex(COL_IS_COMING))==1,
                    c.getString(c.getColumnIndex(COL_GROUP_NAME)),
                    c.getString(c.getColumnIndex(COL_ICON)),
                    c.getInt(c.getColumnIndex(COL_READED))== 1,
                    c.getString(c.getColumnIndex(COL_DATE)),
                    c.getString(c.getColumnIndex(COL_USER_NICK_NAME)),
                    c.getString(c.getColumnIndex(COL_USER_NAME)),
                    c.getString(c.getColumnIndex(COL_MESSAGE_IMAGE)),
                    c.getInt(c.getColumnIndex(COL_MESSAGE_STATU))
            );
        }
        c.close();
        return groupChatMessage;
    }

    /**
     *
     获取每个group未读消息的条数
     */
    public Map<String, Integer> getGroupUnReadMsgs(List<String> groupNames) {
        Map<String, Integer> groupUnReadMsgs = new HashMap<String, Integer>();
        for (String groupName : groupNames) {
            int count = getUnreadedMsgsCountByGroupId(groupName);
            groupUnReadMsgs.put(groupName, count);
        }
        return groupUnReadMsgs;
    }

    /**
     *
     获得所有未读消息的条数
     */
    public int getUnreadMsgsCount(List<String> groupNames){
        Map<String,Integer> stringIntegerMap=getGroupUnReadMsgs(groupNames);
        int count=0;
        for (Integer valus:stringIntegerMap.values()){
            count=count+valus.intValue();
        }
        return count;
    }

    /**
     *
     获取指定group的未读消息的条数
     */
    public int getUnreadedMsgsCountByGroupId(String groupName) {
        createTable(groupName);
        String sql = "select count(*) as count from _" + groupName + " where "
                + COL_IS_COMING + " = 1 and " + COL_READED + " = 0";
        Cursor c = mDb.rawQuery(sql, null);
        int count = 0;
        if (c.moveToNext()){count = c.getInt(c.getColumnIndex("count"));}
        c.close();
        return count;
    }

    /**
     *
     让指定group未读消息条数变为0
     */
    public void updateReaded(String groupName) {
        createTable(groupName);
        mDb.execSQL("update  _" + groupName + " set " + COL_READED + " = 1 where "
                + COL_READED + " = 0 ", new Object[]{});
    }

    /**
     *
     通过日期和group找到指定的信息，改变其中的值
     */
    public void update(String which,String what,String time,String groupName){
        createTable(groupName);
        mDb.execSQL("update  _" + groupName + " set "
                + which+ " = "+what +" where "
                + COL_DATE + " ='"+time+"'", new Object[]{});

    }

    public void close() {
        if (mDb != null && mDb.isOpen())
            mDb.close();
    }

}
