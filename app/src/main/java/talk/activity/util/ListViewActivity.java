package talk.activity.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.heshixiyang.ovetalk.R;

import talk.TalkApplication;
import talk.fragment.GroupTask;
import talk.fragment.MemberFragment;
import talk.model.Group;

public class ListViewActivity extends AppCompatActivity {
    public Group mGroup;
    private Fragment mFragment;
    private FragmentManager mFragmentManager;
    private TalkApplication mApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        mFragmentManager=getSupportFragmentManager();
        mApplication=(TalkApplication)getApplication();
        mGroup=mApplication.getGroupDB().getGroup(getIntent().getStringExtra("groupName"));
        initWhich(getIntent().getIntExtra("which",-999));
    }


    private void initWhich(int which){
        switch (which){
            case 1:
                mFragment=new GroupTask();
                break;
            case 2:
                mFragment=new MemberFragment();
                break;
            case 3:
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
