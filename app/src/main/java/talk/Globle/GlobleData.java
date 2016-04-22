package talk.Globle;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class GlobleData {
    public static final String GROUP_DB_NAME="group.db";
    public static final String CHAT_DB_NAME="chat.db";
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String base="http://192.168.199.198:8088";

    public static final String baseUrl1 = "http://120.27.111.167:8088/OVE/server/group";

    public static final String baseUrl11 = "http://192.168.199.198:8088/ove/server/group";

    public static final String jpush_sendMessage= baseUrl11+"/jpush_sendMessage.do";

    public static final String jpush_sendImage= baseUrl11+"/jpush_sendImage.do";

    public static final String sendTaskMessage= baseUrl11+"/sendTaskMessage.do";

    public static final String sendWorkMessage= baseUrl11+"/sendWorkMessage.do";

    public static final String joinOrExitGroup = baseUrl11+"/joinOrExitGroup.do";

    public static final String logoutGroup= baseUrl11+"/logoutGroup.do";

    public static final String agreeOrDisAgree= "http://192.168.199.198:8088/ove/group/master.do";

    public static final String pushWork= baseUrl11+"/pushWork.do";

    public static final String sendTask= baseUrl11+"/sendTask.do";

    public static final String getWorkFile= baseUrl11+"/getWorkFile.do";

    public static final String getTaskFile= baseUrl11+"/getTaskFile.do";

    public static final String getGroupInfo= baseUrl11+"/getGroupInfo.do";

    public static final String updateAllTaskClick= baseUrl11+"/updateAllTaskClick.do";

    public static final String updateAllWorkClick= baseUrl11+"/updateAllWorkClick.do";

    public static final String create= baseUrl11+"/create.do";//已经测通

    public static final String join= baseUrl11+"/join.do";//已经测通

    public static String SD_CACHE="";

    public static String SD_FILE="";

    public static final int COMMOM_MESSAGE=1;

    public static final int EMOJI_MESSAGE=2;

    public static final int PHOTO_MESSAGE=3;

    public static final int USER_JOIN_GROUP=4;

    public static final int USER_OUT_GROUP=5;

    public static final int USER_CANCEL_GROUP=6;

    public static final int AGREE_USER_TO_GROUP=7;

    public static final int DISAGREE_USER_TO_GROUP=8;

    public static final int USER_REQUEST_JOIN_GROUP=9;

    public static final int MASTER_SEND_TASK_MESSAGE =10;

    public static final int USER_SEND_HOMEWORK_MESSAGE =11;

    public static final int YOU_AGREE_TO_JOIN_GROUP=12;

    public static final int YOU_DISAGREE_TO_JOIN_GROUP=13;

    public static final int CREATE_GROUP =14;

    public static final int SEND_TASK=15;

    public static final int SEND_HOMEWORK=16;

    public static final int GET_TASK_FILE=17;

    public static final int GET_HOMEWORK_FILE=18;

    public static final int GET_GROUP_INFO=19;

    public static final int GET_TASK_CLICK=20;

    public static final int GET_HOMEWORK_CLICK=21;

    public static final int GET_HOMEWORK_INFO=22;

    public static final int GET_TASK_INFO=23;

    public static final String MESSAGE="message";
    public static final String GROUP_ICON="groupIcon";
    public static final String DATE="date";
    public static final String GROUP_ID="groupId";
    public static final String GROUP_NICK ="groupNick";
    public static final String USER_NAME ="userName";
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
    public static final String FILE="file";
    public static final String FILE_NAME="fileName";
    public static final String TASK="task";
    public static final String HOMEWORK="homeWork";
    public static final String URL="url";
    public static final String CHAT_MESSAGE="chatMessage";
    public static final String IS_MESSAGE="isMessage";

    public static final int GROUP_TASK_LIST=1;
    public static final int GROUP_MEMBER_LIST=2;
    public static final int TASK_CLICK_MEMBER_LIST=3;
    public static final int WORK_CLICK_MEMBER_LIST=4;

    public static final int IS_TASK=1;
    public static final int IS_WORK=2;

    public static final int IS_TEXT=0;
    public static final int IS_MUSIC=1;
    public static final int IS_VIDEO =2;

    public static final int SEND_MESSAGE_FAIL =0;
    public static final int SEND_MESSAGE_SUCCESS=1;
    public static final int NET_ERROR=2;
    public static final int CAN_NOT_CONNECT_SERVER=3;
    public static final int NO_SUCH_GROUP=4;
    public static final int YOU_IN_GROUP=5;

    public static final int SEND_FILE_FAIL=0;
    public static final int SEND_FILE_SUCCESS=1;

    public static final int CHOOSE_TASK =1;
    public static final int CHOOSE_FILE =2;

    public static final int STEP_ONE=1;
    public static final int STEP_TWO=2;
    public static final int STEP_THREE=3;

    public static final int START_MAKE_TASK_ACTIVITY=1;
    public static final int START_MAKE_HOMEWORK_ACTIVITY=2;

    public static final int DEFAULT=-999;

    public static int res=-999;

    public static final int CHAT_ADAPTER_AGREE=0;
    public static final int CHAT_ADAPTER_DISAGREE=1;
    public static final int CHAT_ADAPTER_TASK_IAMGE=2;
    public static final int CHAT_ADAPTER_WORK_IAMGE=3;

    public static final int HIDE_SOFT_INPUT=0;
    public static final int HIDE_MORE=1;

    public static final int SELECT_FRIST=0;
    public static final int SELECT_LAST=1;

    public static final int ADD_MASTER=0;
    public static final int ADD_MEMBER=1;

    public static final int SEND_FILE=1;
    public static final int MAKE_GROUP=2;

    public static final int SYSTEM=999999;

    public static final int BROADCAST_MESSAGE=1;
    public static final int BROADCAST_TASK_MESSAGE=2;
    public static final int BROADCAST_HOMEWORK_MESSAGE=3;
    public static final int BROADCAST_JOIN_GROUP=4;
    public static final int BROADCAST_FLASH=5;

    public static final int SAVE_GROUP_ICON=1;
    public static final int SAVE_USER_ICON=2;

    public static final int OPEN_PHOTO_ALBUM=0;

    public static final int SELECT_STRING=0;
    public static final int SELECT_JSON=1;
    public static final int SELECT_BYTE=2;
}

