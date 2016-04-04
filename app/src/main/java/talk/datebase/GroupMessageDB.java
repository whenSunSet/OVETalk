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
    private static final String COL_GROUP_ID = "groupId";
    private static final String COL_ICON = "userIcon";
    private static final String COL_USER_NICK_NAME = "userNickName";
    // 1:readed ; 0 unreaded ;
    private static final String COL_READED = "readed";
    private static final String COL_DATE = "date";
    private static final String COL_USER_ID = "userId";
    private static final String COL_MESSAGE_IMAGE="messageImage";
    private static final String COL_MESSAGE_STATU="messageStatu";

    private SQLiteDatabase mDb;

    public GroupMessageDB(Context context) {
        mDb = context.openOrCreateDatabase(GlobleData.CHAT_DB_NAME, Context.MODE_PRIVATE, null);
    }

    public void createTable(int groupId) {
        mDb.execSQL("CREATE table IF NOT EXISTS _" + groupId
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_GROUP_ID +" INTEGER, "
                + COL_ICON +" TEXT, "
                + COL_IS_COMING +" INTEGER ,"
                + COL_MESSAGE_STATU +" INTEGER ,"
                + COL_MESSAGE +" TEXT , "
                + COL_USER_NICK_NAME +" TEXT , "
                + COL_DATE +" TEXT , "
                + COL_USER_ID +" TEXT ,"
                + COL_MESSAGE_IMAGE+" TEXT ,"
                + COL_READED +" INTEGER)");
    }
    public void delTable(int groupId) {
        mDb.execSQL("DROP TABLE _"+groupId);
    }

    /**
     * 为每个用户根据其groupId创建一张消息表
     *
     */
    public void add(int groupId, GroupChatMessage groupChatMessage) {
        createTable(groupId);
        int isComing = groupChatMessage.isComing() ? 1 : 0;
        int readed = groupChatMessage.isReaded() ? 1 : 0;

        mDb.execSQL(
                "insert into _" + groupId + " ("
                        + COL_GROUP_ID + ","
                        + COL_ICON + ","
                        + COL_IS_COMING + ","
                        + COL_MESSAGE + ","
                        + COL_USER_NICK_NAME + ","
                        + COL_DATE + ","
                        + COL_USER_ID + ","
                        + COL_READED + ","
                        + COL_MESSAGE_STATU+ ","
                        + COL_MESSAGE_IMAGE + ") values(?,?,?,?,?,?,?,?,?,?)",
                new Object[]{groupChatMessage.getGroupId(),
                        groupChatMessage.getUserIcon(),
                        isComing,
                        groupChatMessage.getMessage(),
                        groupChatMessage.getUserNickName(),
                        groupChatMessage.getDateStr(),
                        groupChatMessage.getUserId(),
                        readed,
                        groupChatMessage.getMessageStatu(),
                        groupChatMessage.getMessageImage()});
    }

    /**
     *
     查找指定的最后n条消息记录
     */
    public ArrayList<GroupChatMessage> find(int groupId, int currentPage, int pageSize) {
        ArrayList<GroupChatMessage> groupChatMessages = new ArrayList<>();
        createTable(groupId);
        int start = (currentPage - 1) * pageSize;
        int end = start + pageSize;
        // 取最后的10条
        String sql = "select * from _" + groupId + " order by _id  desc limit  "
                + start + " , " + end;
        Cursor c = mDb.rawQuery(sql, null);
        GroupChatMessage groupChatMessage;
        while (c.moveToNext()) {
            groupChatMessage = new GroupChatMessage(
                    c.getString(c.getColumnIndex(COL_MESSAGE)),
                    c.getInt(c.getColumnIndex(COL_IS_COMING))==1,
                    c.getInt(c.getColumnIndex(COL_GROUP_ID)),
                    c.getString(c.getColumnIndex(COL_ICON)),
                    c.getInt(c.getColumnIndex(COL_READED))== 1,
                    c.getString(c.getColumnIndex(COL_DATE)),
                    c.getString(c.getColumnIndex(COL_USER_NICK_NAME)),
                    c.getString(c.getColumnIndex(COL_USER_ID)),
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
    public int getMessageNum(int groupId){
        Cursor c = mDb.rawQuery("select * from _"+groupId,null);
        int count=c.getCount();
        c.close();
        return  count;
    }

    /**
     *
     获取指定group的最后一条消息的
     */
    public GroupChatMessage getLastChatMessage(int groupId){
        GroupChatMessage groupChatMessage = null;
        Cursor c = mDb.rawQuery("select * from _" + groupId, null);
        String count=String.valueOf(c.getCount());
        c=mDb.rawQuery("select * from _" + groupId + " where _id= " +count, null);
        if (c.getCount()==0){
            return null;
        }
        if (c.moveToFirst()){
            groupChatMessage = new GroupChatMessage(
                    c.getString(c.getColumnIndex(COL_MESSAGE)),
                    c.getInt(c.getColumnIndex(COL_IS_COMING))==1,
                    c.getInt(c.getColumnIndex(COL_GROUP_ID)),
                    c.getString(c.getColumnIndex(COL_ICON)),
                    c.getInt(c.getColumnIndex(COL_READED))== 1,
                    c.getString(c.getColumnIndex(COL_DATE)),
                    c.getString(c.getColumnIndex(COL_USER_NICK_NAME)),
                    c.getString(c.getColumnIndex(COL_USER_ID)),
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
    public Map<Integer, Integer> getGroupUnReadMsgs(List<Integer> groupIds) {
        HashMap groupUnReadMsgs = new HashMap();
        for (Integer groupId : groupIds) {
            int count = getUnreadedMsgsCountByGroupId(groupId);
            groupUnReadMsgs.put(groupId, count);
        }
        return groupUnReadMsgs;
    }

    /**
     *
     获得所有未读消息的条数
     */
    public int getUnreadMsgsCount(List<Integer> groupIds){
        Map<Integer,Integer> stringIntegerMap=getGroupUnReadMsgs(groupIds);
        int count=0;
        for (Integer valus:stringIntegerMap.values()){
            count=count+ valus;
        }
        return count;
    }

    /**
     *
     获取指定group的未读消息的条数
     */
    public int getUnreadedMsgsCountByGroupId(int groupId) {
        createTable(groupId);
        String sql = "select count(*) as count from _" + groupId + " where "
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
    public void updateReaded(int groupId) {
        createTable(groupId);
        mDb.execSQL("update  _" + groupId + " set " + COL_READED + " = 1 where "
                + COL_READED + " = 0 ", new Object[]{});
    }

    /**
     *
     通过日期和group找到指定的信息，改变其中的值
     */
    public void update(String which,String what,String time,int groupId){
        createTable(groupId);
        mDb.execSQL("update  _" + groupId + " set "
                + which+ " = "+what +" where "
                + COL_DATE + " ='"+time+"'", new Object[]{});

    }

    public void close() {
        if (mDb != null && mDb.isOpen())
            mDb.close();
    }

}
