package talk.activity.aboutGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.heshixiyang.ovetalk.R;
import talk.TalkApplication;
import talk.activity.util.NotePadActivity;
import talk.model.Group;
import talk.model.Task;


public class TaskActivity extends Activity {
    private TalkApplication mApplication;
    private Group mGroup;
    private Task mTask;
    private ImageView mType;
    private TextView mName;
    private TextView mTime;
    private TextView mTarget;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        init();
    }

    public void init(){
        mApplication=(TalkApplication)getApplication();
        mGroup=(Group)mApplication.map.get("nowGroup");
        mTask=((Task)(mApplication.map.get("nowTask")));

        mType = (ImageView) findViewById(R.id.type);
        mName=(TextView)findViewById(R.id.name);
        mTime=(TextView)findViewById(R.id.time);
        mTarget=(TextView)findViewById(R.id.target);
        mButton=(Button)findViewById(R.id.button);

        mName.setText(mTask.getGroupName());
        mTime.setText(mTask.getDate());
        mTarget.setText(mTask.getTarget());
        switch (mTask.getType()){
            case 0:
                mType.setImageResource(R.drawable.text);
                mButton.setText("观看文档");
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(TaskActivity.this,NotePadActivity.class);
                        intent.putExtra("FILE_NAME",mTask.getPath());
                        startActivity(intent);
                    }
                });
                break;
            case 1:
                mType.setImageResource(R.drawable.music);
                mButton.setText("试听音频");
                break;
            case 2:
                mType.setImageResource(R.drawable.video);
                mButton.setText("观看视频");
                break;
            default:
                break;
        }

    }

}
