package talk.activity.aboutGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heshixiyang.ovetalk.R;

import java.util.HashMap;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.activity.util.ListViewActivity;
import talk.activity.util.NotePadActivity;
import talk.model.Task;
import talk.model.Work;
import talk.service.HttpIntentService;

public class TaskAndWorkActivity extends Activity {
    private TalkApplication mApplication;
    private ImageView mType;
    private TextView mName;
    private TextView mTime;
    private TextView mTarget;
    private Button mButton;
    private Button mCliclMember;
    private Task mTask;
    private Work mWork;
    private int which;

    private int groupId;
    private boolean disDownLoad =true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        which=getIntent().getIntExtra("which",GlobleData.DEFAULT);
        init();
    }

    public void init(){
        mApplication=(TalkApplication)getApplication();
        mTask=((Task)(mApplication.map.get("nowTask")));
        mWork=((Work)(mApplication.map.get("nowWork")));

        mType = (ImageView) findViewById(R.id.type);
        mName=(TextView)findViewById(R.id.name);
        mTime=(TextView)findViewById(R.id.time);
        mTarget=(TextView)findViewById(R.id.target);
        mButton=(Button)findViewById(R.id.button);
        mCliclMember=(Button)findViewById(R.id.clickMember);

        String p = null;
        int t = 0;
        int w = 0;
        switch (which){
            case GlobleData.IS_TASK:
                mTarget.setText(mTask.getTarget());
                mName.setText(mTask.getGroupId());
                mTime.setText(mTask.getDate());
                p=mTask.getPath();
                t=mTask.getType();
                w=GlobleData.TASK_CLICK_MEMBER_LIST;
                groupId =mTask.getGroupId();
                break;
            case GlobleData.IS_WORK:
                mTarget.setVisibility(View.GONE);
                mName.setText(mWork.getMaster());
                mTime.setText(mWork.getDate());
                p=mWork.getPath();
                t=mWork.getType();
                w=GlobleData.WORK_CLICK_MEMBER_LIST;
                groupId =mWork.getGroupId();
                break;
        }
        final String[] path = {p};
        final int type=t;
        disDownLoad =p.contains("http");
        switch (type){
            case GlobleData.IS_TEXT:
                mType.setImageResource(R.drawable.text);
                mButton.setText("观看文档");
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeButton(path[0], type,NotePadActivity.class);
                    }
                });
                break;
            case GlobleData.IS_MUSIC:
                mType.setImageResource(R.drawable.music);
                mButton.setText("试听音频");
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeButton(path[0],type,null);
                    }
                });
                break;
            case GlobleData.IS_VIDEO:
                mType.setImageResource(R.drawable.video);
                mButton.setText("观看视频");
                makeButton(path[0],type,null);
                break;
            default:
                break;
        }
        final int whichMember=w;
        mCliclMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TaskAndWorkActivity.this, ListViewActivity.class);
                intent.putExtra("which", whichMember);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });
    }
    private void makeButton(String path,int type,Class c){
        Intent intent = new Intent(TaskAndWorkActivity.this,c);
        switch (type){
            case GlobleData.IS_TEXT:
                if (!disDownLoad){
                    intent.putExtra("FILE_NAME", path);
                }else {
                    mButton.setText("正在下载文档");
                    downLoadFile();
                    return;
                }
                break;
            case GlobleData.IS_MUSIC:
                if (!disDownLoad){
                    intent.putExtra("FILE_NAME", path);
                }else{
                    mButton.setText("正在下载音频");
                    downLoadFile();
                    return;
                }
                break;
            case GlobleData.IS_VIDEO:
                if (which==GlobleData.IS_TASK||!disDownLoad){
                    intent.putExtra("vedioId", path);
                }else {
                    mButton.setText("正在下载视频");
                    downLoadFile();
                    return;
                }
                break;
        }
        startActivity(intent);
    }

    private void downLoadFile(){
        Intent startHttpService=new Intent(TaskAndWorkActivity.this, HttpIntentService.class);
        HashMap<String,Object> pa=new HashMap<>();
        int messageStatu;
        String url;

        if (which==GlobleData.IS_TASK){
            messageStatu=GlobleData.GET_TASK_FILE;
            url=GlobleData.getTaskFile;
            pa.put(GlobleData.GROUP_ID,mTask.getGroupId());
            pa.put(GlobleData.ID_IN_GROUP,mTask.getIdInGroup());
            pa.put(GlobleData.USER_NAME,mApplication.getGroupDB().getGroup(mTask.getGroupId()).getGroupId());
            pa.put("type",mTask.getType());
        }else {
            messageStatu=GlobleData.GET_HOMEWORK_FILE;
            url=GlobleData.getWorkFile;
            pa.put(GlobleData.GROUP_ID,mWork.getGroupId());
            pa.put(GlobleData.ID_IN_TASK,mWork.getIdInTask());
            pa.put(GlobleData.USER_NAME,mWork.getMaster());
            pa.put(GlobleData.TASK_ID,mWork.getTaskId());
            pa.put("type",mWork.getType());
        }

        startHttpService.putExtra("pa", pa);
        startHttpService.putExtra("url",url);
        startHttpService.putExtra("messageStatu", messageStatu);
        startService(startHttpService);
    }
}
