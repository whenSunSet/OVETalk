package talk.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.model.ChangeBean;
import talk.model.ClickTask;
import talk.model.ClickWork;
import talk.model.TaskBean;
import talk.model.WorkBean;

/**
 * Created by heshixiyang on 2016/4/3.
 */
public class SendMessage {
    public static SendMessage sendMessage=new SendMessage();

    public HashMap<String,Object> result;

    private SendMessage() {}

    private SendMessageListener sendMessageListener;

    public static SendMessage getSendMessage(){
        return sendMessage;
    }

    public void post(
            final TalkApplication mApplication
            , final int messageStatu
            , String url
            , final RequestParams requestParams
            , SendMessageListener listener) {
        sendMessageListener = listener;
        int type;

        if (messageStatu==GlobleData.GET_GROUP_INFO){
            type=GlobleData.SELECT_STRING;
        }else if (messageStatu==GlobleData.GET_TASK_FILE||messageStatu==GlobleData.GET_HOMEWORK_FILE){
            type=GlobleData.SELECT_BYTE;
        }else {
            type=GlobleData.SELECT_JSON;
        }

        SelectHttpResponseHandler selectHttpResponseHandler = new SelectHttpResponseHandler(mApplication, messageStatu,type) {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                if (messageStatu == GlobleData.USER_REQUEST_JOIN_GROUP) {
                    return;
                }
                if (getType()== GlobleData.SELECT_JSON) {
                    result = makeReturnValue(messageStatu,getJsonObject(), null, null, isSuccess());
                } else if (getType() == GlobleData.SELECT_BYTE) {
                    result = makeReturnValue(messageStatu, null, getBytes(), null, this.isSuccess());
                } else if (getType() == GlobleData.SELECT_STRING) {
                    result = makeReturnValue(messageStatu, null, null, getS(), this.isSuccess());
                }
                sendMessageListener.success(result);
            }
        };
        AsyncHttpClientUtil.post(url, requestParams, selectHttpResponseHandler.getResponseHandler());

//        if (messageStatu <= 11
//                || messageStatu == GlobleData.GET_TASK_CLICK
//                || messageStatu == GlobleData.GET_HOMEWORK_CLICK
//                || messageStatu == GlobleData.GET_TASK_INFO
//                || messageStatu == GlobleData.GET_HOMEWORK_INFO
//                || messageStatu == GlobleData.PHOTO_MESSAGE
//                || messageStatu == GlobleData.CREATE_GROUP
//                || messageStatu == GlobleData.SEND_TASK
//                || messageStatu == GlobleData.SEND_HOMEWORK) {
//
//            JsonAsyncHttpResponseHandler jsonAsyncHttpResponseHandler = new JsonAsyncHttpResponseHandler(mApplication, messageStatu) {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    super.onSuccess(statusCode, headers, response);
//                    if (messageStatu == GlobleData.USER_REQUEST_JOIN_GROUP) {
//                        return;
//                    }
//                    result = makeReturnValue(messageStatu, response, null, null, this.isSuccess());
//                    sendMessageListener.success(result);
//                }
//            };
//            Log.d("SendMessage", url);
//            AsyncHttpClientUtil.post(url, requestParams, jsonAsyncHttpResponseHandler);
//        } else if (messageStatu == GlobleData.GET_TASK_FILE || messageStatu == GlobleData.GET_HOMEWORK_FILE) {
//            ByteAsyncHttpResponseHandler byteAsyncHttpResponseHandler = new ByteAsyncHttpResponseHandler(mApplication, messageStatu) {
//                @Override
//                public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                    super.onSuccess(i, headers, bytes);
//                    result = makeReturnValue(messageStatu, null, bytes, null, this.isSuccess());
//                    sendMessageListener.success(result);
//                }
//            };
//            AsyncHttpClientUtil.post(url, requestParams, byteAsyncHttpResponseHandler);
//        } else if (messageStatu == GlobleData.GET_GROUP_INFO) {
//            StringAsyncHttpResponseHandler stringAsyncHttpResponseHandler = new StringAsyncHttpResponseHandler(mApplication, messageStatu) {
//                @Override
//                public void onSuccess(int i, Header[] headers, String s) {
//                    super.onSuccess(i, headers, s);
//                    result = makeReturnValue(messageStatu, null, null, s, this.isSuccess());
//                    sendMessageListener.success(result);
//                }
//            };
//            AsyncHttpClientUtil.post(url, requestParams, stringAsyncHttpResponseHandler);
//        }
    }

    private HashMap<String,Object> makeReturnValue(int messageStatu,JSONObject jsonObject,Byte[] bytes,String s,boolean isSuccess){
        if (!isSuccess){
            return null;
        }
        Log.d("SendMessage", "map");
        HashMap<String,Object> returnValue=new HashMap();
        Gson gson=new Gson();
        try {
            if (messageStatu<=11||messageStatu==GlobleData.SEND_TASK||messageStatu==GlobleData.SEND_HOMEWORK){
                returnValue.put("res",jsonObject.getInt("res"));
            }else if (messageStatu==GlobleData.CREATE_GROUP){
                returnValue.put("groupId",jsonObject.getInt("groupId"));
                returnValue.put("res",jsonObject.getInt("res"));
            }else if (messageStatu==GlobleData.GET_GROUP_INFO){
                Type workListType = new TypeToken<ChangeBean>(){}.getType();
                ChangeBean changeBean=gson.fromJson(s,workListType);
//                Type taskListType = new TypeToken<ArrayList<TaskBean>>(){}.getType();
//                Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
//                Type clickWorkListType = new TypeToken<ArrayList<ClickWork>>(){}.getType();
//                Type clickTaskListType = new TypeToken<ArrayList<ClickTask>>(){}.getType();
//                Type joinGroupListType = new TypeToken<ArrayList<JoinGroup>>(){}.getType();

//                returnValue.put("group",gson.fromJson(jsonObject.toString(), Group.class));
//                returnValue.put("tasks",gson.fromJson(jsonObject.toString(), taskListType));
//                returnValue.put("works",gson.fromJson(jsonObject.toString(),workListType));
//                returnValue.put("users",gson.fromJson(jsonObject.toString(),userListType));
//                returnValue.put("clickWorks",gson.fromJson(jsonObject.toString(), clickWorkListType));
//                returnValue.put("clickTasks",gson.fromJson(jsonObject.toString(), clickTaskListType));
//                returnValue.put("joinGroups",gson.fromJson(jsonObject.toString(), joinGroupListType));
                returnValue.put("changeBean",changeBean);
                returnValue.put("res",1);
            }else if (messageStatu==GlobleData.GET_TASK_CLICK){
                Type clickTaskListType = new TypeToken<ArrayList<ClickTask>>(){}.getType();
                returnValue.put("clickTasks",gson.fromJson(jsonObject.toString(), clickTaskListType));
            }else if (messageStatu==GlobleData.GET_HOMEWORK_CLICK){
                Type clickWorkListType = new TypeToken<ArrayList<ClickWork>>(){}.getType();
                returnValue.put("clickWorks",gson.fromJson(jsonObject.toString(), clickWorkListType));
            }else if (messageStatu==GlobleData.GET_TASK_INFO){
                Type taskType = new TypeToken<ArrayList<TaskBean>>(){}.getType();
                returnValue.put("task",gson.fromJson(jsonObject.toString(), taskType));
            }else if (messageStatu==GlobleData.GET_HOMEWORK_INFO){
                Type workType = new TypeToken<ArrayList<WorkBean>>(){}.getType();
                returnValue.put("WorkBean",gson.fromJson(jsonObject.toString(),workType));
            }else if (messageStatu==GlobleData.GET_TASK_FILE||messageStatu==GlobleData.GET_HOMEWORK_FILE) {
                returnValue.put("bytes",bytes);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public interface SendMessageListener{
        public void success(HashMap<String,Object> result);
    }
}
