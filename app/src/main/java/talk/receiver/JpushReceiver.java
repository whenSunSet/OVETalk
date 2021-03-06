package talk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.model.GroupChatMessage;
import talk.model.Message;
import talk.model.TaskBean;
import talk.model.WorkBean;
import talk.service.HttpIntentService;
import talk.util.MyPreferenceManager;

/**
 * Created by asus on 2015/11/5.o
 */
public class JpushReceiver extends BroadcastReceiver {
    private TalkApplication mApplication;
    private MyPreferenceManager myPreferenceManager;
    private int messageStatu;

    @Override
    public void onReceive(Context context, Intent intent) {
        mApplication=(TalkApplication)context.getApplicationContext();
        myPreferenceManager=mApplication.getSpUtil();
        Bundle bundle = intent.getExtras();
        Message message;
        int groupId;
        String msg=null;
        String groupNickName;

        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            if ((message=getMessage(bundle))==null){
                return;
            }
            messageStatu=message.getMessageStatu();

            //根据MessageImage 的状态得知信息的状态
            if (messageStatu<=3||messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE ||messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE){
                //1-3 10 11都是group接受的消息
                groupId=message.getGroupId();
                makeAndSaveMessage(message, groupId);

                if (messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE){
                    message.setMessage("我发布了一个任务，快来看看吧");
                    startHttpService("",GlobleData.GET_TASK_INFO,message.getGroupId(),new TaskBean(Integer.parseInt(message.getUserIcon())),null);
                }else if (messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
                    message.setMessage("我发布了一个作业，快来看看吧");
                    startHttpService("",GlobleData.GET_HOMEWORK_INFO,message.getGroupId(),null,new WorkBean(Integer.parseInt(message.getUserIcon()),Integer.parseInt(message.getUserNick())));
                }
            }else {
                //以下都是把信息发在SystemGroup里面的
                groupId=GlobleData.SYSTEM;
                if (messageStatu==GlobleData.AGREE_USER_TO_GROUP){
                    groupNickName=message.getMessage();
                }else {
                    groupNickName=mApplication.getGroupDB().getGroup(message.getGroupId()).getGroupNick();
                }

                switch (messageStatu) {
                    case GlobleData.USER_JOIN_GROUP:
                        msg="加入了";
                        GlobleMethod.addUserToGroup(mApplication, message,GlobleData.ADD_MEMBER);
                        //groupId 加入的群组，date：服务器发送的时间，nickname：加入人的昵称，username：加入人的id userIcon: 加入人的头像

                        break;
                    case GlobleData.USER_OUT_GROUP:
                        msg="退出了";
                        GlobleMethod.userOutGroup(mApplication, message);
                        //groupId:要退出的群组，date：服务器发送的时间，nickname：退出人的昵称，username：退出人的id

                        break;
                    case GlobleData.USER_CANCEL_GROUP:
                        msg="注销了";
                        GlobleMethod.deleteGroup(mApplication,groupId,message.getUserId());
                        GlobleMethod.setTag(mApplication);
                        //groupId:注销的群组，date：服务器发送的时间，nickname：注销群主的昵称，username：注销群主的id
                        break;
                    case GlobleData.AGREE_USER_TO_GROUP:
                        msg="同意你加入";
                        startHttpService(GlobleData.getGroupInfo,GlobleData.GET_GROUP_INFO,message.getGroupId(),null,null);
                        //groupID:被同意加入的群组，date：服务器发送的时间，nickname：同意加入的群主的昵称，username：同意加入的群主的id，userIcon：群组的icon，message：群的nickname
                        break;
                    case GlobleData.DISAGREE_USER_TO_GROUP:
                        msg="不同意你加入";
                        //groupID:不同意加入的群组，date：服务器发送的时间，nickname：不同意加入的群主的昵称，username：不同意加入的群主的id
                        break;
                    case GlobleData.USER_REQUEST_JOIN_GROUP:
                        msg="请求加入";
                        //groupId:请求加入的群组，date：服务器发送的时间，nickname：想加入该群人的昵称，username：想加入该群人的id
                        break;
                }
                makeAndSaveMessage(msg, message, groupId, groupNickName);
            }
             sendMessageToActivity(context, message);
        }
    }
    private Message getMessage(Bundle bundle){
        JSONObject object;
        Message message= null;

        //把收到的信息获取到message中
        try {
            object=new JSONObject(bundle.getString("cn.jpush.android.EXTRA"));
            message = new Message(
                    bundle.getString("cn.jpush.android.MESSAGE"),
                    object.getInt("groupId"),
                    object.getString("groupIcon"),
                    object.getString("date"),
                    object.getString("userNickName"),
                    object.getString("userName"),
                    object.getInt("messageStatu"),
                    object.getString("messageImage"));

            //如果是自己发出的信息，则不接受
            if (object.getString("userName").equals(myPreferenceManager.getUserId())){
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    private void startHttpService(String url,int messageStatu,int groupId,TaskBean taskBean,WorkBean workBean){
        Intent startHttpService=new Intent(mApplication, HttpIntentService.class);
        startHttpService.putExtra(GlobleData.IS_MESSAGE,false);
        startHttpService.putExtra(GlobleData.GROUP_ID,groupId);
        startHttpService.putExtra(GlobleData.URL,url);
        startHttpService.putExtra(GlobleData.MESSAGE_STATU,messageStatu);
        startHttpService.putExtra(GlobleData.TASK,taskBean);
        startHttpService.putExtra(GlobleData.HOMEWORK,workBean);
        mApplication.startService(startHttpService);
    }

    private void makeAndSaveMessage(Message message, int groupId){
        makeAndSaveMessage("", message, groupId, "");
    }
    private  void makeAndSaveMessage(String s, Message message, int groupId, String groupNickName){
        String msg;

        if (TextUtils.isEmpty(s)){
            msg=message.getMessage();
        }else {
            msg=message.getUserNick()+"("+message.getUserId()+")"+ s+"："+groupNickName+"("+message.getGroupId()+")群";
        }
        message.setMessage(msg);
        GroupChatMessage groupChatMessage=new GroupChatMessage(
                msg,
                true,
                message.getGroupId(),
                message.getUserIcon(),
                false,
                message.getDate(),
                message.getUserNick(),
                message.getUserId(),
                message.getMessageImage(),
                message.getMessageStatu());
        mApplication.getGroupMessageDB().add(groupId, groupChatMessage);
    }

    private void sendMessageToActivity(Context context, Message message) {
        Bundle bundle=new Bundle();
        bundle.putParcelable(GlobleData.KEY_MESSAGE,message);
        Intent msgIntent = new Intent(GlobleData.MESSAGE_RECEIVED_ACTION);
        msgIntent.putExtra("type",GlobleData.BROADCAST_MESSAGE);
        msgIntent.putExtras(bundle);
        context.sendBroadcast(msgIntent);
    }
}
