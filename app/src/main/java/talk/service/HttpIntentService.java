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
import talk.util.DialogUtil;
import talk.http.SendMessage;

public class HttpIntentService extends IntentService implements SendMessage.SendMessageListener{
    private int messageStatu;
    private TalkApplication mApplication;
    private HashMap<String,Object> pa;
    public HttpIntentService() {
        super("HttpIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        messageStatu=intent.getIntExtra("messageStatu", GlobleData.DEFAULT);
        mApplication=(TalkApplication)getApplication();
        pa=intent.getParcelableExtra("pa");
        String url=intent.getStringExtra("url");
        RequestParams requestParams=null;

         if (messageStatu==GlobleData.SEND_TASK) {
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
             requestParams.put(GlobleData.DATE, task.getDate());
             SendMessage.getSendMessage().post(mApplication,messageStatu,url,requestParams,this);

         } else if (messageStatu == GlobleData.SEND_HOMEWORK) {
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
             SendMessage.getSendMessage().post(mApplication,messageStatu,url,requestParams,this);

         }else if (messageStatu==GlobleData.GET_TASK_FILE){
             int type= (int) pa.get("type");
             requestParams.put(GlobleData.GROUP_ID,(int)pa.get(GlobleData.GROUP_ID));
             requestParams.put(GlobleData.ID_IN_GROUP,(int)pa.get(GlobleData.ID_IN_GROUP));
             requestParams.put(GlobleData.USER_NAME,(String)pa.get(GlobleData.USER_NAME));
             SendMessage.getSendMessage().post(mApplication,messageStatu,url,requestParams,this);

         }else if (messageStatu==GlobleData.GET_HOMEWORK_FILE){
             int type= (int) pa.get("type");
             requestParams.put(GlobleData.GROUP_ID,(int)pa.get(GlobleData.GROUP_ID));
             requestParams.put(GlobleData.ID_IN_TASK,(int)pa.get(GlobleData.ID_IN_TASK));
             requestParams.put(GlobleData.TASK_ID,(String)pa.get(GlobleData.TASK_ID));
             requestParams.put(GlobleData.USER_NAME,(String)pa.get(GlobleData.USER_NAME));
             SendMessage.getSendMessage().post(mApplication,messageStatu,url,requestParams,this);

         }
    }

    @Override
    public void success(HashMap<String, Object> result) {
        if (result==null){
            return;
        }
        switch (messageStatu){
            case GlobleData.SEND_TASK:
                Task task=(Task)pa.get("task");
                if ((int)result.get("res")==1){
                    mApplication.getTaskDB().add(task);
                    GroupAll.mIsFlash =true;

                    Intent msgIntent = new Intent(GlobleData.MESSAGE_RECEIVED_ACTION);
                    msgIntent.putExtra("type", GlobleData.BROADCAST_TASK_MESSAGE);
                    msgIntent.putExtra("task", pa);
                    mApplication.sendBroadcast(msgIntent);
                }else {
                    DialogUtil.showToast(mApplication,"任务上传失败");
                }
                break;
            case GlobleData.SEND_HOMEWORK:
                Work work=(Work)pa.get("work");
                if ((int)result.get("res")==1){
                    mApplication.getWorkDB().add(work);
                    GroupAll.mIsFlash =true;

                    Intent msgIntent = new Intent(GlobleData.MESSAGE_RECEIVED_ACTION);
                    msgIntent.putExtra("type",GlobleData.BROADCAST_HOMEWORK_MESSAGE);
                    msgIntent.putExtra("work",pa);
                    mApplication.sendBroadcast(msgIntent);
                }else {
                    DialogUtil.showToast(mApplication, "作业上传失败");
                }
                break;
            case GlobleData.GET_TASK_FILE:
                break;
            case GlobleData.GET_HOMEWORK_FILE:
                break;
        }
    }
}
