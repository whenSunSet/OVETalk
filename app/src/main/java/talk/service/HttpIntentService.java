package talk.service;

import android.app.IntentService;
import android.content.Intent;

import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.activity.fragment.GroupAll;
import talk.model.Task;
import talk.model.Work;
import talk.util.SendMessage;

public class HttpIntentService extends IntentService {
    public HttpIntentService() {
        super("HttpIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TalkApplication mApplication=(TalkApplication)getApplication();
        HashMap<String,Object> result;
        HashMap<String,Object> pa=intent.getParcelableExtra("pa");
        String url=intent.getStringExtra("url");
        int messageStatu=intent.getIntExtra("messageStatu", GlobleData.DEFAULT);
        RequestParams requestParams=null;

         if (messageStatu==GlobleData.SEND_TASK_SERVICE) {
             Task task=(Task)pa.get("task");
             if (task.getType() == 2) {
                 requestParams.put(GlobleData.PATH, task.getPath());
             }else{
                 try {
                     requestParams.put(GlobleData.FILE, new File(task.getPath()));
                 } catch (FileNotFoundException e) {
                     e.printStackTrace();
                 }
             }
             requestParams.put(GlobleData.ID_IN_GROUP,task.getIdInGroup());
             requestParams.put(GlobleData.GROUP_ID,task.getGroupId());
             requestParams.put(GlobleData.TYPE,task.getType());
             requestParams.put(GlobleData.TARGET, task.getTarget());
             requestParams.put(GlobleData.CLICK_NUMBER,task.getClickNum());
             requestParams.put(GlobleData.DATE,task.getDate());
             result= SendMessage.getSendMessage().post(mApplication,messageStatu,url,null,requestParams);

             mApplication.getTaskDB().add(task);
             GroupAll.mIsFlash =true;
         } else if (messageStatu == GlobleData.SEND_HOMEWORK_SERVICE) {
             Work work=(Work)pa.get("work");
             try {
                 requestParams.put(GlobleData.FILE, new File(work.getPath()));
             } catch (FileNotFoundException e) {
                 e.printStackTrace();
             }
             requestParams.put(GlobleData.ID_IN_TASK, work.getIdInTask());
             requestParams.put(GlobleData.TASK_ID, work.getTaskId());
             requestParams.put(GlobleData.GROUP_ID, work.getGroupId());
             requestParams.put(GlobleData.TYPE, work.getType());
             requestParams.put(GlobleData.MASTER, work.getMaster());
             requestParams.put(GlobleData.CLICK_NUMBER, work.getClickNum());
             requestParams.put(GlobleData.DATE, work.getDate());
             result=SendMessage.getSendMessage().post(mApplication,messageStatu,url,null,requestParams);

         }else if (messageStatu==GlobleData.GET_HOMEWORK_FILE_SERVICE){
             requestParams.put(GlobleData.GROUP_ID,(int)pa.get(GlobleData.GROUP_ID));
             requestParams.put(GlobleData.ID_IN_GROUP,(int)pa.get(GlobleData.ID_IN_GROUP));
             requestParams.put(GlobleData.USER_NAME,(String)pa.get(GlobleData.USER_NAME));
             result=SendMessage.getSendMessage().post(mApplication,messageStatu,url,null,requestParams);

         }else if (messageStatu==GlobleData.GET_TASK_FILE_SERVICE){
             requestParams.put(GlobleData.GROUP_ID,(int)pa.get(GlobleData.GROUP_ID));
             requestParams.put(GlobleData.ID_IN_TASK,(int)pa.get(GlobleData.ID_IN_TASK));
             requestParams.put(GlobleData.TASK_ID,(String)pa.get(GlobleData.TASK_ID));
             requestParams.put(GlobleData.USER_NAME,(String)pa.get(GlobleData.USER_NAME));
             result=SendMessage.getSendMessage().post(mApplication,messageStatu,url,null,requestParams);

         }
    }
}
