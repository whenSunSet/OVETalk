package talk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import org.apache.commons.httpclient.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.activity.fragment.GroupAll;
import talk.activity.fragment.Groups;
import talk.datebase.UserDB;
import talk.model.GroupChatMessage;
import talk.model.Message;
import talk.util.MyPreferenceManager;
import talk.util.MyRunnable;

/**
 * Created by asus on 2015/11/5.o
 */
public class JpushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

        }
    };
    private TalkApplication mApplication;
    private UserDB mUserDB;
    private MyPreferenceManager myPreferenceManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        mApplication=(TalkApplication)context.getApplicationContext();
        mUserDB =mApplication.getUserDB();
        myPreferenceManager=mApplication.getSpUtil();
        Bundle bundle = intent.getExtras();
        Message message;
        String groupName;
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
                groupName=message.getGroupName();
                makeAndSaveMessage(message, groupName);

            }else {
                //以下都是把信息发在SystemGroup里面的
                groupName=myPreferenceManager.getUserName();
                if (messageStatu==GlobleData.AGREE_USER_TO_GROUP){
                    groupNickName=message.getMessage();
                }else {
                    groupNickName=mApplication.getGroupDB().getGroup(message.getGroupName()).getGroupNick();
                }

                switch (messageStatu) {
                    case GlobleData.USER_JOIN_GROUP:
                        msg="加入了";
                        GlobleMethod.addUserToGroup(mApplication, message);
                        //groupname:加入的群组，date：服务器发送的时间，nickname：加入人的昵称，username：加入人的id

                        break;
                    case GlobleData.USER_OUT_GROUP:
                        msg="退出了";
                        GlobleMethod.quitFromGroup(mApplication, message);
                        //groupname:要退出的群组，date：服务器发送的时间，nickname：退出人的昵称，username：退出人的id

                        break;
                    case GlobleData.USER_CANCEL_GROUP:
                        msg="注销了";
                        mApplication.getGroupDB().delGroup(message.getGroupName());
                        GlobleMethod.setTag(mApplication);
                        //groupname:注销的群组，date：服务器发送的时间，nickname：注销群主的昵称，username：注销群主的id
                        break;
                    case GlobleData.AGREE_USER_TO_GROUP:
                        msg="同意你加入";
//                        mApplication.getGroupDB().addGroup(new Group(message.getGroupName(),groupNickName,message.getUserIcon(),message.getUserName()));
                        GlobleMethod.setTag(mApplication);
                        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                        formparams.add(new NameValuePair("username",myPreferenceManager.getUserName()));
                        formparams.add(new NameValuePair("groupname",message.getGroupName()));

                        new Thread(new MyRunnable(formparams,GlobleData.GET_GROUP_INFO,handler)).start();

                        //groupname:被同意加入的群组，date：服务器发送的时间，nickname：同意加入的群主的昵称，username：同意加入的群主的id，userIcon：群组的icon，message：群的nickname

                        break;
                    case GlobleData.DISAGREE_USER_TO_GROUP:
                        msg="不同意你加入";
                        //groupname:不同意加入的群组，date：服务器发送的时间，nickname：不同意加入的群主的昵称，username：不同意加入的群主的id

                        break;
                    case GlobleData.USER_REQUEST_JOIN_GROUP:
                        msg="请求加入";
                        //groupname:请求加入的群组，date：服务器发送的时间，nickname：想加入该群人的昵称，username：想加入该群人的id

                        break;
                }
                makeAndSaveMessage(msg, message, groupName, groupNickName);
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
                    object.getString("groupname"),
                    object.getString("icon"),
                    object.getString("date"),
                    object.getString("nickname"),
                    object.getString("username"),
                    object.getInt("messagestatu"),
                    object.getString("messageimage"));

            //如果是自己发出的信息，则不接受
            if (object.getString("username").toString().equals(myPreferenceManager.getUserName())){
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    private void sendMessageToActivity(Context context, Message message) {
        if (GroupAll.isForeground|| Groups.isForeground) {
            Bundle bundle=new Bundle();
            bundle.putParcelable(GlobleData.KEY_MESSAGE,message);
            Intent msgIntent = new Intent(GlobleData.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtras(bundle);
            context.sendBroadcast(msgIntent);
        }
    }

    private void makeAndSaveMessage(Message message, String groupName){
        makeAndSaveMessage("", message, groupName, "");
    }

    private  void makeAndSaveMessage(String s, Message message, String groupName, String groupNickName){
        String msg;

        if (TextUtils.isEmpty(s)){
            msg=message.getMessage();
        }else {
            msg=message.getUserNickName()+"("+message.getUserName()+")"+ s+"："+groupNickName+"("+message.getGroupName()+")群";
        }

        message.setMessage(msg);
        GroupChatMessage groupChatMessage=new GroupChatMessage(
                msg,
                true,
                message.getGroupName(),
                message.getUserIcon(),
                false,
                message.getDate(),
                message.getUserNickName(),
                message.getUserName(),
                message.getMessageImage(),
                message.getMessageStatu());
        mApplication.getGroupMessageDB().add(groupName, groupChatMessage);

    }
}
