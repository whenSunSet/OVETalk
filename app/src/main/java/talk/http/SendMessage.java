package talk.http;

import android.util.Log;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.model.ClickTask;
import talk.model.ClickWork;
import talk.model.Group;
import talk.model.JoinGroup;
import talk.model.Task;
import talk.model.User;
import talk.model.Work;

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

    public void post(TalkApplication mApplication, final int messageStatu, String url, HashMap<String ,String> paramter, final RequestParams requestParams,SendMessageListener listener){
        sendMessageListener=listener;
        JSONObject jsonObject = new JSONObject();
        if ((messageStatu<=11&&messageStatu!= GlobleData.PHOTO_MESSAGE)
                ||messageStatu==GlobleData.GET_GROUP_INFO
                ||messageStatu==GlobleData.GET_TASK_CLICK
                ||messageStatu==GlobleData.GET_HOMEWORK_CLICK
                ||messageStatu==GlobleData.GET_TASK_INFO
                ||messageStatu==GlobleData.GET_HOMEWORK_INFO){
            MyResponseErrorListenerAndListener myResponseErrorListenerAndListener=new MyResponseErrorListenerAndListener(mApplication,messageStatu){
                @Override
                public void onResponse(JSONObject jsonObject) {
                    super.onResponse(jsonObject);
                    if (messageStatu==GlobleData.USER_JOIN_GROUP){
                        return;
                    }
                    result=makeReturnValue(messageStatu, jsonObject, null, this.isSuccess());
                    sendMessageListener.success(result);
                }
            };
            MyJsonObjectRequest jsonObjectRequest = new MyJsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    paramter,
                    myResponseErrorListenerAndListener
            );
            mApplication.getRequestQueue().add(jsonObjectRequest);
        }else if (messageStatu==GlobleData.PHOTO_MESSAGE
                ||messageStatu==GlobleData.CREATE_GROUP
                ||messageStatu==GlobleData.SEND_TASK
                ||messageStatu==GlobleData.SEND_HOMEWORK){
            JsonAsyncHttpResponseHandler jsonAsyncHttpResponseHandler =new JsonAsyncHttpResponseHandler(mApplication,messageStatu){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    result=makeReturnValue(messageStatu, response, null, this.isSuccess());
                    sendMessageListener.success(result);
                }
            };
            Log.d("SendMessage", url);
            AsyncHttpClientUtil.post(url, requestParams, jsonAsyncHttpResponseHandler);
        } else if (messageStatu==GlobleData.GET_TASK_FILE ||messageStatu==GlobleData.GET_HOMEWORK_FILE){
            ByteAsyncHttpResponseHandler byteAsyncHttpResponseHandler=new ByteAsyncHttpResponseHandler(mApplication,messageStatu){
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    super.onSuccess(i, headers, bytes);
                    result=makeReturnValue(messageStatu, null, bytes, this.isSuccess());
                    sendMessageListener.success(result);
                }
            };
            AsyncHttpClientUtil.post(url,requestParams,byteAsyncHttpResponseHandler);
        }
    }

    private HashMap<String,Object> makeReturnValue(int messageStatu,JSONObject jsonObject,byte[] bytes,boolean isSuccess){
        if (!isSuccess){
            return null;
        }
        HashMap<String,Object> returnValue=new HashMap();
        Gson gson=new Gson();
        try {
            if (messageStatu<=11||messageStatu==GlobleData.SEND_TASK||messageStatu==GlobleData.SEND_HOMEWORK){
                returnValue.put("res",jsonObject.getInt("res"));
            }else if (messageStatu==GlobleData.CREATE_GROUP){
                returnValue.put("groupId",jsonObject.getInt("groupId"));
                returnValue.put("res",jsonObject.getInt("res"));
            }else if (messageStatu==GlobleData.GET_GROUP_INFO){
                Type workListType = new TypeToken<ArrayList<Work>>(){}.getType();
                Type taskListType = new TypeToken<ArrayList<Task>>(){}.getType();
                Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
                Type clickWorkListType = new TypeToken<ArrayList<ClickWork>>(){}.getType();
                Type clickTaskListType = new TypeToken<ArrayList<ClickTask>>(){}.getType();
                Type joinGroupListType = new TypeToken<ArrayList<JoinGroup>>(){}.getType();

                returnValue.put("group",gson.fromJson(jsonObject.toString(), Group.class));
                returnValue.put("tasks",gson.fromJson(jsonObject.toString(), taskListType));
                returnValue.put("works",gson.fromJson(jsonObject.toString(),workListType));
                returnValue.put("users",gson.fromJson(jsonObject.toString(),userListType));
                returnValue.put("clickWorks",gson.fromJson(jsonObject.toString(), clickWorkListType));
                returnValue.put("clickTask",gson.fromJson(jsonObject.toString(), clickTaskListType));
                returnValue.put("joinGroup",gson.fromJson(jsonObject.toString(), joinGroupListType));
            }else if (messageStatu==GlobleData.GET_TASK_CLICK){
                Type clickTaskListType = new TypeToken<ArrayList<ClickTask>>(){}.getType();
                returnValue.put("clickTasks",gson.fromJson(jsonObject.toString(), clickTaskListType));
            }else if (messageStatu==GlobleData.GET_HOMEWORK_CLICK){
                Type clickWorkListType = new TypeToken<ArrayList<ClickWork>>(){}.getType();
                returnValue.put("clickWorks",gson.fromJson(jsonObject.toString(), clickWorkListType));
            }else if (messageStatu==GlobleData.GET_TASK_INFO){
                Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
                returnValue.put("task",gson.fromJson(jsonObject.toString(), taskType));
            }else if (messageStatu==GlobleData.GET_HOMEWORK_INFO){
                Type workType = new TypeToken<ArrayList<Work>>(){}.getType();
                returnValue.put("Work",gson.fromJson(jsonObject.toString(),workType));
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
