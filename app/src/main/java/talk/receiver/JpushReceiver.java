package talk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.activity.fragment.GroupAll;
import talk.activity.fragment.Groups;
import talk.model.GroupChatMessage;
import talk.model.Message;
import talk.model.Task;
import talk.model.Work;
import talk.util.MyPreferenceManager;
import talk.util.SendMessage;

/**
 * Created by asus on 2015/11/5.o
 */
public class JpushReceiver extends BroadcastReceiver {
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

        }
    };
    private TalkApplication mApplication;
    private MyPreferenceManager myPreferenceManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        mApplication=(TalkApplication)context.getApplicationContext();
        myPreferenceManager=mApplication.getSpUtil();
        Bundle bundle = intent.getExtras();
        Message message;
        String groupId;
        String msg=null;
        String groupNickName;

        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            if ((message=getMessage(bundle))==null){
                return;
            }
            int messageStatu=message.getMessageStatu();

            //根据MessageImage 的状态得知信息的状态
            if (messageStatu<=3&&messageStatu==GlobleData.USER_PUT_HOMEWORK&&messageStatu==GlobleData.MASTER_PUT_TASK){
                //1-3 10 11都是group接受的消息
                groupId=message.getGroupId();
                makeAndSaveMessage(message, groupId);
                HashMap<String,String> paramter=new HashMap<>();
                HashMap<String,Object> result=null;
                paramter.put(GlobleData.GROUP_ID, message.getGroupId());
                if (messageStatu==GlobleData.MASTER_PUT_TASK){
                    message.setMessage("我发布了一个任务，快来看看吧");
                    paramter.put(GlobleData.ID_IN_GROUP, message.getUserIcon());
                    result=SendMessage.getSendMessage().post(mApplication,messageStatu,"",paramter,null);
                    mApplication.getTaskDB().add((Task)result.get("task"));
                }else if (messageStatu==GlobleData.USER_PUT_HOMEWORK){
                    message.setMessage("我发布了一个作业，快来看看吧");
                    paramter.put(GlobleData.TASK_ID, message.getUserIcon());
                    paramter.put(GlobleData.ID_IN_TASK, message.getUserNickName());
                    result=SendMessage.getSendMessage().post(mApplication,messageStatu,"",paramter,null);
                    mApplication.getWorkDB().add((Work)result.get("work"));
                }
            }else {
                //以下都是把信息发在SystemGroup里面的
                groupId=myPreferenceManager.getUserId();
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
                        HashMap<String,String> paramter=new HashMap();
                        HashMap<String,Object> result=SendMessage.getSendMessage().post(mApplication, GlobleData.AGREE_USER_TO_GROUP, GlobleData.getGroupInfo, paramter, null);

                        GlobleMethod.addMeToGroup(mApplication,result);
                        GlobleMethod.setTag(mApplication);

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
                    object.getString("groupId"),
                    object.getString("icon"),
                    object.getString("date"),
                    object.getString("nickname"),
                    object.getString("userName"),
                    object.getInt("messagestatu"),
                    object.getString("messageimage"));

            //如果是自己发出的信息，则不接受
            if (object.getString("userName").toString().equals(myPreferenceManager.getUserId())){
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }
    private void sendMessageToActivity(Context context, Message message) {
        if (GroupAll.mIsForeground || Groups.mIsForeground) {
            Bundle bundle=new Bundle();
            bundle.putParcelable(GlobleData.KEY_MESSAGE,message);
            Intent msgIntent = new Intent(GlobleData.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtras(bundle);
            context.sendBroadcast(msgIntent);
        }
    }
    private void makeAndSaveMessage(Message message, String groupId){
        makeAndSaveMessage("", message, groupId, "");
    }
    private  void makeAndSaveMessage(String s, Message message, String groupId, String groupNickName){
        String msg;

        if (TextUtils.isEmpty(s)){
            msg=message.getMessage();
        }else {
            msg=message.getUserNickName()+"("+message.getUserId()+")"+ s+"："+groupNickName+"("+message.getGroupId()+")群";
        }
        message.setMessage(msg);
        GroupChatMessage groupChatMessage=new GroupChatMessage(
                msg,
                true,
                message.getGroupId(),
                message.getUserIcon(),
                false,
                message.getDate(),
                message.getUserNickName(),
                message.getUserId(),
                message.getMessageImage(),
                message.getMessageStatu());
        mApplication.getGroupMessageDB().add(groupId, groupChatMessage);
    }
}
