package talk.activity.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import com.example.heshixiyang.ovetalk.R;
import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.fragment.GroupTask;
import talk.fragment.MemberFragment;
import talk.model.Group;
import talk.model.Task;
import talk.model.Work;

public class ListViewActivity extends AppCompatActivity {
    public TalkApplication mApplication;
    public Group mGroup;
    public Boolean isTaskClick=false;
    public Boolean isWorkClick=false;
    public Task mTask;
    public Work mWork;
    private Fragment mFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        mFragmentManager=getSupportFragmentManager();
        mApplication=(TalkApplication)getApplication();
        mGroup=mApplication.getGroupDB().getGroup(getIntent().getStringExtra("groupName"));
        initWhich(getIntent().getIntExtra("which",GlobleData.DEFAULT));
    }


    private void initWhich(int which){
        switch (which){
            case GlobleData.GROUP_TASK_LIST:
                mFragment=new GroupTask();
                break;
            case GlobleData.GROUP_MEMBER_LIST:
                isTaskClick=false;
                isWorkClick=false;
                mFragment=new MemberFragment();
                break;
            case GlobleData.TASK_CLICK_MEMBER_LIST:
                isTaskClick=true;
                mTask=getIntent().getParcelableExtra("task");
                mFragment=new MemberFragment();
                break;
            case GlobleData.WORK_CLICK_MEMBER_LIST:
                isWorkClick=true;
                mTask=getIntent().getParcelableExtra("work");
                mFragment=new MemberFragment();
                break;
            default:
                break;
        }
        mFragmentManager.beginTransaction().add(R.id.fragment_container,mFragment);
    }


    public void finish(String grouName,int idInGroup) {
        Intent intent=new Intent();
        intent.putExtra("groupName",grouName);
        intent.putExtra("idInGroup",idInGroup);
        setResult(1, intent);
        super.finish();
    }
}
