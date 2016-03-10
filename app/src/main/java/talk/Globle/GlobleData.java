package talk.Globle;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class GlobleData {
    public static final String GROUP_DB_NAME="group.db";
    public static final String CHAT_DB_NAME="chat.db";
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";

    public static final String requestUrl1 = "http://139.129.23.72/OVE/";


    public static final String groupRequestUrl="http://39.181.120.215:8080/OVE/";

    //创建小组的URL
    public static final String GROUP_CREATE=requestUrl1+"group/create.do";

    //退出小组的URL
    public static final String GROUP_EXIT=groupRequestUrl +"group/exit.do";

    //注销小组的URL
    public static final String GROUP_CANCEL=groupRequestUrl +"group/cancel.do";

    //发送消息的URL
    public static final String GROUP_SEND_MESSAGE=groupRequestUrl +"group/sendMessage.do";
//	public static final String GROUP_SEND_MESSAGE="http://39.191.127.100:8080/OVE/group/sendMessage.do";

    //加入小组的URL
    public static final String GROUP_JOIN=requestUrl1 +"group/join.do";

    //群主同意他人加入的URL
    public static final String GROUP_MSATER_AGREE=groupRequestUrl +"group/master.do";

    //群主不同意他人加入的URL
    public static final String GROUP_MSATER_DISAGREE=groupRequestUrl +"group/master.do";

    //获得小组信息 的URL
    public static final String GET_GROUP_INFO=groupRequestUrl +"group/getGroupInfo.do";


    //视频数据加载显示每页个数
    public static final int pageCount = 10;

    public static final int COMMOM_MESSAGE=1;

    public static final int EMOJI_MESSAGE=2;

    public static final int PHOTO_MESSAGE=3;

    public static final int USER_JOIN_GROUP=4;

    public static final int USER_OUT_GROUP=5;

    public static final int USER_CANCEL_GROUP=6;

    public static final int AGREE_USER_TO_GROUP=7;

    public static final int DISAGREE_USER_TO_GROUP=8;

    public static final int USER_REQUEST_JOIN_GROUP=9;

    public static final int MASTER_PUT_TASK=10;

    public static final int USER_PUT_HOMEWORK=11;

    public static final int YOU_AGREE_TO_JOIN_GROUP=12;

    public static final int YOU_DISAGREE_TO_JOIN_GROUP=13;

    public static final String MESSAGE="message";
    public static final String DATE="date";
    public static final String GROUP_NAME="groupName";
    public static final String USER_NAME="userName";
    public static final String MESSAGE_STATU="messageStatu";
    public static final String MESSAGE_IMAGE="messageImage";


    public static final String PATH="path";
    public static final String ID_IN_GROUP="idInGroup";
    public static final String ID_IN_TASK="idInTask";
    public static final String TASK_ID="taskId";
    public static final String MASTER="master";
    public static final String TYPE="type";
    public static final String CLICK_NUMBER="clickNumber";
    public static final String TARGET="target";

    public static final int GROUP_TASK_LIST=1;
    public static final int GROUP_MEMBER_LIST=2;
    public static final int TASK_CLICK_MEMBER_LIST=3;
    public static final int WORK_CLICK_MEMBER_LIST=4;

    public static final int IS_TASK=1;
    public static final int IS_WORK=2;

    public static final int IS_TEXT=0;
    public static final int IS_MUSIC=1;
    public static final int IS_VIDIO=2;

    public static final int SEND_MESSAGE_FAILD=0;
    public static final int SEND_MESSAGE_SUCCESS=1;
    public static final int NET_ERROR=2;
    public static final int CAN_NOT_CONNECT_SERVER=3;
    public static final int NO_SUCH_GROUP=4;

    public static final int SEND_FILE_FAILD=0;
    public static final int SEND_FILE_SUCCESS=1;

}

