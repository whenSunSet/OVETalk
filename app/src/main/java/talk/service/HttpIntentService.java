package talk.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.activity.fragment.GroupAll;
import talk.activity.fragment.Groups;
import talk.http.SendMessage;
import talk.model.Group;
import talk.model.GroupChatMessage;
import talk.model.TaskBean;
import talk.model.WorkBean;

public class HttpIntentService extends Service implements SendMessage.SendMessageListener{
    private int groupId;
    private int messageStatu;
    private String userName;
    private String url;
    private String fileName;
    private String message;
    private String groupNcik;
    private GroupChatMessage chatMessage;
    private TaskBean mTaskBean;
    private WorkBean mWorkBean;
    private TalkApplication mApplication;
    private Boolean isMessage;



    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication=(TalkApplication)getApplication();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init(intent);
        RequestParams requestParams=new RequestParams();

        try {
            if (messageStatu==GlobleData.CREATE_GROUP){
                requestParams.put(GlobleData.GROUP_NICK,groupNcik);
                requestParams.put(GlobleData.USER_NAME,userName);
                requestParams.put(GlobleData.GROUP_ICON,new File(fileName));
                SendMessage.getSendMessage().post(mApplication,messageStatu,url,requestParams,this);
                return super.onStartCommand(intent, flags, startId);
            }

            requestParams.put(GlobleData.GROUP_ID,groupId);
            if (isMessage){
                requestParams.put(GlobleData.MESSAGE_STATU,messageStatu);
                requestParams.put(GlobleData.USER_NAME,userName);
                    if (messageStatu==GlobleData.COMMOM_MESSAGE||messageStatu==GlobleData.EMOJI_MESSAGE){
                        requestParams.put(GlobleData.MESSAGE,message);
                    }else if (messageStatu==GlobleData.PHOTO_MESSAGE){
                        requestParams.put(GlobleData.MESSAGE_IMAGE,new File(fileName));
                    }else if (messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE){
                        requestParams.put(GlobleData.MESSAGE_IMAGE,new File(fileName));
                        requestParams.put(GlobleData.ID_IN_GROUP,mTaskBean.getIdInGroup());
                    }else if (messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
                        requestParams.put(GlobleData.MESSAGE_IMAGE,new File(fileName));
                        requestParams.put(GlobleData.TASK_ID,mWorkBean.getTaskId());
                        requestParams.put(GlobleData.ID_IN_TASK,mWorkBean.getIdInTask());
                    }
            }else {
                switch (messageStatu){
                    case GlobleData.SEND_TASK:
                        if (mTaskBean.getType() == 2) {
                            requestParams.put(GlobleData.PATH, mTaskBean.getPath());
                        }else{
                            requestParams.put(GlobleData.FILE, new File(mTaskBean.getPath()));
                        }
                        requestParams.put(GlobleData.ID_IN_GROUP, mTaskBean.getIdInGroup());
                        requestParams.put(GlobleData.TYPE, mTaskBean.getType());
                        requestParams.put(GlobleData.TARGET, mTaskBean.getTarget());
                        requestParams.put(GlobleData.CLICK_NUMBER, mTaskBean.getClickNum());
                        requestParams.put(GlobleData.DATE, mTaskBean.getDate());
                        break;
                    case GlobleData.SEND_HOMEWORK:
                        requestParams.put(GlobleData.FILE, new File(mWorkBean.getPath()));
                        requestParams.put(GlobleData.ID_IN_TASK, mWorkBean.getIdInTask());
                        requestParams.put(GlobleData.TASK_ID, mWorkBean.getTaskId());
                        requestParams.put(GlobleData.TYPE, mWorkBean.getType());
                        requestParams.put(GlobleData.MASTER, mWorkBean.getMaster());
                        requestParams.put(GlobleData.CLICK_NUMBER, mWorkBean.getClickNum());
                        requestParams.put(GlobleData.DATE, mWorkBean.getDate());

                        break;
                    case GlobleData.GET_TASK_FILE:
                        int type1= intent.getIntExtra("type",GlobleData.DEFAULT);
                        requestParams.put(GlobleData.GROUP_ID,intent.getIntExtra(GlobleData.GROUP_ID,GlobleData.DEFAULT));
                        requestParams.put(GlobleData.ID_IN_GROUP,intent.getIntExtra(GlobleData.ID_IN_GROUP,GlobleData.DEFAULT));
                        requestParams.put(GlobleData.USER_NAME,intent.getStringExtra(GlobleData.USER_NAME));

                        break;
                    case GlobleData.GET_HOMEWORK_FILE:
                        int type2= intent.getIntExtra("type",GlobleData.DEFAULT);
                        requestParams.put(GlobleData.GROUP_ID,intent.getIntExtra(GlobleData.GROUP_ID,GlobleData.DEFAULT));
                        requestParams.put(GlobleData.ID_IN_TASK,intent.getIntExtra(GlobleData.ID_IN_TASK,GlobleData.DEFAULT));
                        requestParams.put(GlobleData.TASK_ID,intent.getStringExtra(GlobleData.TASK_ID));
                        requestParams.put(GlobleData.USER_NAME,intent.getStringExtra(GlobleData.USER_NAME));

                        break;
                    case GlobleData.GET_TASK_INFO:
                        requestParams.put(GlobleData.ID_IN_GROUP, mTaskBean.getIdInGroup());
                        break;
                    case GlobleData.GET_HOMEWORK_INFO:
                        requestParams.put(GlobleData.TASK_ID, mWorkBean.getTaskId());
                        requestParams.put(GlobleData.ID_IN_TASK, mWorkBean.getIdInTask());
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.d("HttpIntentService", url);
        SendMessage.getSendMessage().post(mApplication,messageStatu,url,requestParams,this);
        return super.onStartCommand(intent, flags, startId);
    }

    private void init(Intent intent){
        messageStatu=intent.getIntExtra(GlobleData.MESSAGE_STATU, GlobleData.DEFAULT);
        groupId=intent.getIntExtra(GlobleData.GROUP_ID,GlobleData.DEFAULT);
        userName=intent.getStringExtra(GlobleData.USER_NAME);
        url=intent.getStringExtra(GlobleData.URL);
        fileName=intent.getStringExtra(GlobleData.MESSAGE_IMAGE);
        groupNcik=intent.getStringExtra(GlobleData.GROUP_NICK);
        message=intent.getStringExtra(GlobleData.MESSAGE);
        chatMessage=intent.getParcelableExtra(GlobleData.CHAT_MESSAGE);
        isMessage=intent.getBooleanExtra(GlobleData.IS_MESSAGE,true);
        mTaskBean=intent.getParcelableExtra(GlobleData.TASK);
        mWorkBean=intent.getParcelableExtra(GlobleData.HOMEWORK);

    }

    @Override
    public void success(HashMap<String, Object> result) {
        if (result==null){
            return;
        }
        if ((int)result.get("res")==GlobleData.SEND_MESSAGE_SUCCESS){
            switch (messageStatu){
                case GlobleData.USER_OUT_GROUP:
                    GlobleMethod.deleteGroup(mApplication,groupId,mApplication.getSpUtil().getUserId());
                    break;
                case GlobleData.USER_CANCEL_GROUP:
                    GlobleMethod.deleteGroup(mApplication,groupId,mApplication.getSpUtil().getUserId());
                    break;
                case GlobleData.SEND_TASK:
                        mApplication.getTaskDB().add(mTaskBean);
                        GroupAll.mIsFlash =true;

                        Intent msgIntent1 = new Intent(GlobleData.MESSAGE_RECEIVED_ACTION);
                        msgIntent1.putExtra("type", GlobleData.BROADCAST_TASK_MESSAGE);
                        msgIntent1.putExtra("taskBean", mTaskBean);
                        mApplication.sendBroadcast(msgIntent1);
                    break;
                case GlobleData.SEND_HOMEWORK:
                        mApplication.getWorkDB().add(mWorkBean);
                        GroupAll.mIsFlash =true;

                        Intent msgIntent2 = new Intent(GlobleData.MESSAGE_RECEIVED_ACTION);
                        msgIntent2.putExtra("type",GlobleData.BROADCAST_HOMEWORK_MESSAGE);
                        msgIntent2.putExtra("workBean",mWorkBean);
                        mApplication.sendBroadcast(msgIntent2);
                    break;
                case GlobleData.GET_TASK_FILE:
                    break;
                case GlobleData.GET_HOMEWORK_FILE:
                    break;
                case GlobleData.CREATE_GROUP:
                    int returnGroupId= (int) result.get("groupId");
                    Groups.mIsFlash =true;
                    Group group=new Group(
                            returnGroupId
                            ,groupNcik
                            , GlobleMethod.changeFileName(mApplication,String.valueOf(returnGroupId))
                            ,mApplication.getSpUtil().getUserId(),0,0);

                    mApplication.getGroupDB().addGroup(group);
                    GlobleMethod.setTag(mApplication);
                    GlobleMethod.addUserToGroup(mApplication
                            , new talk.model.Message(
                                    mApplication.getSpUtil().getUserId()
                                    , returnGroupId
                                    , GlobleMethod.getNowDate()
                                    , mApplication.getSpUtil().getUserIcon()
                                    , mApplication.getSpUtil().getUsreNickName()),
                            GlobleData.ADD_MASTER);
                    break;
                case GlobleData.GET_GROUP_INFO:
                    GlobleMethod.addMeToGroup(mApplication,result);
                    GlobleMethod.setTag(mApplication);
                    Intent msgIntent = new Intent(GlobleData.MESSAGE_RECEIVED_ACTION);
                    msgIntent.putExtra("type",GlobleData.BROADCAST_JOIN_GROUP);
                    mApplication.sendBroadcast(msgIntent);
                    break;
                case GlobleData.AGREE_USER_TO_GROUP:
                    Log.d("GroupChatting", chatMessage.toString());
                    mApplication.getGroupMessageDB().update(GlobleData.MESSAGE_STATU, String.valueOf(GlobleData.YOU_AGREE_TO_JOIN_GROUP), chatMessage.getDateStr(), GlobleData.SYSTEM);
                    mApplication.getGroupMessageDB().update(GlobleData.MESSAGE, "\"您已经同意" + chatMessage.getUserNickName() + "加入了 " + mApplication.getGroupDB().getGroup(chatMessage.getGroupId()).getGroupNick()+"("+chatMessage.getGroupId()+")\"", chatMessage.getDateStr(), GlobleData.SYSTEM);
                    break;
                case GlobleData.DISAGREE_USER_TO_GROUP:
                    mApplication.getGroupMessageDB().update(GlobleData.MESSAGE_STATU, String.valueOf(GlobleData.YOU_DISAGREE_TO_JOIN_GROUP), chatMessage.getDateStr(), GlobleData.SYSTEM);
                    mApplication.getGroupMessageDB().update(GlobleData.MESSAGE, "\"您已经拒绝了" + chatMessage.getUserNickName() + "加入" + mApplication.getGroupDB().getGroup(chatMessage.getGroupId()).getGroupNick()+"("+chatMessage.getGroupId()+")\"", chatMessage.getDateStr(), GlobleData.SYSTEM);
                    break;
                default:
                    if (messageStatu==GlobleData.USER_JOIN_GROUP){
                        mApplication.getGroupMessageDB().add(GlobleData.SYSTEM, chatMessage);
                    }else {
                        mApplication.getGroupMessageDB().add(groupId, chatMessage);
                    }
                    break;
            }
            if (messageStatu==GlobleData.COMMOM_MESSAGE
                    ||messageStatu==GlobleData.EMOJI_MESSAGE
                    ||messageStatu==GlobleData.PHOTO_MESSAGE
                    ||messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE
                    ||messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
                Intent msgIntent = new Intent(GlobleData.MESSAGE_RECEIVED_ACTION);
                msgIntent.putExtra("type",GlobleData.BROADCAST_FLASH);
                mApplication.sendBroadcast(msgIntent);
            }
        }
    }

}
