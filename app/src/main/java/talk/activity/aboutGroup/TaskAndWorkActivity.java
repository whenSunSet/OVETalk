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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        which=getIntent().getIntExtra("which",-999);
        init();
    }

    public void init(){
        mApplication=(TalkApplication)getApplication();
        mTask=((Task)(mApplication.map.get("nowTask")));

        mType = (ImageView) findViewById(R.id.type);
        mName=(TextView)findViewById(R.id.name);
        mTime=(TextView)findViewById(R.id.time);
        mTarget=(TextView)findViewById(R.id.target);
        mButton=(Button)findViewById(R.id.button);
        mCliclMember=(Button)findViewById(R.id.clickMember);
        mTime.setText(mTask.getDate());

        String p = null;
        int type = 0;
        int w = 0;
        switch (which){
            case GlobleData.IS_TASK:
                mTarget.setText(mTask.getTarget());
                mName.setText(mTask.getGroupName());
                p=mTask.getPath();
                type=mTask.getType();
                w=GlobleData.TASK_CLICK_MEMBER_LIST;
                break;
            case GlobleData.IS_WORK:
                mTarget.setVisibility(View.GONE);
                mName.setText(mWork.getMaster());
                p=mWork.getPath();
                type=mWork.getType();
                w=GlobleData.WORK_CLICK_MEMBER_LIST;
                break;
        }
        final String path=p;
        switch (type){
            case GlobleData.IS_TEXT:
                mType.setImageResource(R.drawable.text);
                mButton.setText("观看文档");
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(TaskAndWorkActivity.this,NotePadActivity.class);
                        intent.putExtra("FILE_NAME", path);
                        startActivity(intent);
                    }
                });
                break;
            case GlobleData.IS_MUSIC:
                mType.setImageResource(R.drawable.music);
                mButton.setText("试听音频");
                break;
            case GlobleData.IS_VIDIO:
                mType.setImageResource(R.drawable.video);
                mButton.setText("观看视频");
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
                startActivity(intent);
            }
        });
    }
}
