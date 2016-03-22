package talk.activity.aboutGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heshixiyang.ovetalk.R;

import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.activity.util.ListViewActivity;
import talk.activity.util.NotePadActivity;
import talk.model.Task;
import talk.model.Work;

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

    private String groupName;
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
        int type = 0;
        int w = 0;
        switch (which){
            case GlobleData.IS_TASK:
                mTarget.setText(mTask.getTarget());
                mName.setText(mTask.getGroupName());
                mTime.setText(mTask.getDate());
                p=mTask.getPath();
                type=mTask.getType();
                w=GlobleData.TASK_CLICK_MEMBER_LIST;
                groupName=mTask.getGroupName();
                break;
            case GlobleData.IS_WORK:
                mTarget.setVisibility(View.GONE);
                mName.setText(mWork.getMaster());
                mTime.setText(mWork.getDate());
                p=mWork.getPath();
                type=mWork.getType();
                w=GlobleData.WORK_CLICK_MEMBER_LIST;
                groupName=mWork.getGroupName();
                break;
        }
        final String[] path = {p};
        switch (type){
            case GlobleData.IS_TEXT:
                mType.setImageResource(R.drawable.text);
                mButton.setText("观看文档");
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeButton(path[0], NotePadActivity.class);
                    }
                });
                break;
            case GlobleData.IS_MUSIC:
                mType.setImageResource(R.drawable.music);
                mButton.setText("试听音频");
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeButton(path[0],null);
                    }
                });
                break;
            case GlobleData.IS_VIDEO:
                mType.setImageResource(R.drawable.video);
                mButton.setText("观看视频");
                makeButton(path[0],null);
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
                intent.putExtra("groupName",groupName);
                startActivity(intent);
            }
        });
    }
    private void makeButton(String path,Class c){
        Intent intent = new Intent(TaskAndWorkActivity.this,c);
        if (which==GlobleData.IS_TASK){
            intent.putExtra("vedioId",Integer.parseInt(path));
        }else if (which==GlobleData.IS_WORK){
            if (!GlobleMethod.isDownLoad(path)) {
                path= GlobleMethod.downLoadFile();
//                mApplication.getTaskDB().update(path, mTask.getIdInGroup(), mTask.getGroupName());
            }
            intent.putExtra("FILE_NAME", path);
        }
        startActivity(intent);
    }
}
