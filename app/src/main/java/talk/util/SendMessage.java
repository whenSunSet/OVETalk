package talk.util;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
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

    private SendMessage() {}

    public static SendMessage getSendMessage(){
        return sendMessage;
    }

    public HashMap<String,Object> post(TalkApplication mApplication,final int messageStatu,String url,HashMap<String ,String> paramter,RequestParams requestParams){
        AsyncHttpClientUtil asyncHttpClientUtil=new AsyncHttpClientUtil();
        JSONObject jsonObject = new JSONObject();

        if ((messageStatu<=11&&messageStatu!= GlobleData.PHOTO_MESSAGE)
                ||messageStatu==GlobleData.GET_GROUP_INFO
                ||messageStatu==GlobleData.GET_TASK_CLICK
                ||messageStatu==GlobleData.GET_HOMEWORK_CLICK
                ||messageStatu==GlobleData.GET_TASK_INFO
                ||messageStatu==GlobleData.GET_HOMEWORK_INFO){

            MyJsonObjectRequest jsonObjectRequest = new MyJsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    paramter,
                    new MyResponseErrorListenerAndListener(mApplication,messageStatu)
            );
            mApplication.getRequestQueue().add(jsonObjectRequest);
            return makeReturnValue(messageStatu,jsonObject,null);
        }else if (messageStatu==GlobleData.PHOTO_MESSAGE
                ||messageStatu==GlobleData.CREATE_GROUP
                ||messageStatu==GlobleData.SEND_TASK
                ||messageStatu==GlobleData.SEND_HOMEWORK){
            MyAsyncHttpResponseHandler myAsyncHttpResponseHandler=new MyAsyncHttpResponseHandler(mApplication,messageStatu);
            asyncHttpClientUtil.post(url, requestParams, myAsyncHttpResponseHandler);
            return makeReturnValue(messageStatu,myAsyncHttpResponseHandler.getJsonObject(),null);
        } else if (messageStatu==GlobleData.GET_TASK_FILE ||messageStatu==GlobleData.GET_HOMEWORK_FILE){

        }
        return null;
    }

    private HashMap<String,Object> makeReturnValue(int messageStatu,JSONObject jsonObject,File file){
        HashMap<String,Object> returnValue=new HashMap();
        Gson gson=new Gson();
        try {
            if (messageStatu<=11||messageStatu==GlobleData.SEND_TASK||messageStatu==GlobleData.SEND_HOMEWORK){
                returnValue.put("res",jsonObject.getInt("res"));
            }else if (messageStatu==GlobleData.CREATE_GROUP){
                returnValue.put("groupId",jsonObject.getInt("groupId"));
                returnValue.put("res",jsonObject.getInt("groupId"));
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    private void makeRequestParams(int messageStatu,final RequestParams requestParams,HashMap<String,Object> paramter){
        switch (messageStatu){
            case GlobleData.CREATE_GROUP:
                requestParams.put(GlobleData.USER_NAME,(String)paramter.get(GlobleData.USER_NAME));
                requestParams.put(GlobleData.GROUP_NICK_NAME,(String)paramter.get(GlobleData.GROUP_NICK_NAME));
                try {
                    requestParams.put(GlobleData.GROUP_ICON,(File)paramter.get(GlobleData.GROUP_ICON));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case GlobleData.PHOTO_MESSAGE:
                requestParams.put(GlobleData.USER_NAME,(String)paramter.get(GlobleData.USER_NAME));
                requestParams.put(GlobleData.GROUP_ID,(int)paramter.get(GlobleData.GROUP_ID));
                requestParams.put(GlobleData.MESSAGE_STATU,(int)paramter.get(GlobleData.MESSAGE_STATU));
                try {
                    requestParams.put(GlobleData.MESSAGE_IMAGE,(File)paramter.get(GlobleData.MESSAGE_IMAGE));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case GlobleData.SEND_TASK:
                if ((int)paramter.get(GlobleData.TYPE)==2){
                    requestParams.put(GlobleData.PATH, (String)paramter.get(GlobleData.PATH));
                }else{
                    try {
                        requestParams.put(GlobleData.FILE, (File)paramter.get(GlobleData.FILE));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                requestParams.put(GlobleData.ID_IN_GROUP,(int)paramter.get(GlobleData.ID_IN_GROUP));
                requestParams.put(GlobleData.GROUP_ID,(int)paramter.get(GlobleData.GROUP_ID));
                requestParams.put(GlobleData.TYPE, (int)paramter.get(GlobleData.TYPE));
                requestParams.put(GlobleData.TARGET, (String)paramter.get(GlobleData.TARGET));
                requestParams.put(GlobleData.CLICK_NUMBER, (int)paramter.get(GlobleData.CLICK_NUMBER));
                requestParams.put(GlobleData.DATE, (String)paramter.get(GlobleData.DATE));
                break;
            case GlobleData.SEND_HOMEWORK:
                requestParams.put(GlobleData.ID_IN_TASK,(int)paramter.get(GlobleData.ID_IN_TASK));
                requestParams.put(GlobleData.TASK_ID,(int)paramter.get(GlobleData.TASK_ID));
                requestParams.put(GlobleData.GROUP_ID,(int)paramter.get(GlobleData.GROUP_ID));
                requestParams.put(GlobleData.TYPE, (int)paramter.get(GlobleData.TYPE));
                requestParams.put(GlobleData.MASTER, (String)paramter.get(GlobleData.MASTER));
                requestParams.put(GlobleData.CLICK_NUMBER, (int)paramter.get(GlobleData.CLICK_NUMBER));
                requestParams.put(GlobleData.DATE, (String)paramter.get(GlobleData.DATE));
                try {
                    requestParams.put(GlobleData.FILE, (File)paramter.get(GlobleData.FILE));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
