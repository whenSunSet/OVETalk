package talk.activity.util;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
    public Boolean mIsTaskClick =false;
    public Boolean mIsWorkClick =false;
    public Task mTask;
    public Work mWork;
    private Fragment mFragment;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        mFragmentTransaction =getSupportFragmentManager().beginTransaction();
        mApplication=(TalkApplication)getApplication();
        mGroup=mApplication.getGroupDB().getGroup(getIntent().getStringExtra(GlobleData.GROUP_ID));
        initWhich(getIntent().getIntExtra("which",GlobleData.DEFAULT));
    }
    private void initWhich(int which){
        switch (which){
            case GlobleData.GROUP_TASK_LIST:
                mFragment=new GroupTask();
                break;
            case GlobleData.GROUP_MEMBER_LIST:
                mIsTaskClick =false;
                mIsWorkClick =false;
                mFragment=new MemberFragment();
                break;
            case GlobleData.TASK_CLICK_MEMBER_LIST:
                mIsTaskClick =true;
                mTask=((Task)(mApplication.map.get("nowTask")));
                mFragment=new MemberFragment();
                break;
            case GlobleData.WORK_CLICK_MEMBER_LIST:
                mIsWorkClick =true;
                mWork=((Work)(mApplication.map.get("nowWork")));
                mFragment=new MemberFragment();
                break;
            default:
                break;
        }
        mFragmentTransaction.replace(R.id.fragment_container, mFragment);
        mFragmentTransaction.commit();
    }


    public void finish(String grouName,int idInGroup) {
        Intent intent=new Intent();
        intent.putExtra(GlobleData.GROUP_ID,grouName);
        intent.putExtra(GlobleData.ID_IN_GROUP,idInGroup);
        setResult(1, intent);
        super.finish();
    }
}
