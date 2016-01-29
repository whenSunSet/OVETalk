package talk.activity.aboutGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.heshixiyang.ovetalk.R;

import org.apache.commons.httpclient.NameValuePair;

import java.util.List;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.activity.create.MakeTaskActivity;
import talk.activity.fragment.Groups;
import talk.model.Group;
import talk.util.DialogUtil;
import talk.util.MyPreferenceManager;
import talk.util.MyRunnable;


public class GroupActivity extends Activity {
    private ImageView mGroupIcon;
    private TextView mGroupName;
    private TextView mGroupNickName;
    private Button mExit;
    private Button mDestroy;
    private Button mMember;
    private Group mGroup;
    private TalkApplication mApplication;
    private MyPreferenceManager myPreferenceManager;
    private Button mMakeTask;
    private List<NameValuePair> formparams ;
    private GroupActivity groupActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygroup);
        init();
    }

    private void init(){
        mApplication=(TalkApplication)getApplication();
        mGroup=getIntent().getParcelableExtra("group");
        groupActivity =this;

        mGroupIcon=(ImageView)findViewById(R.id.icon);
        mGroupName=(TextView)findViewById(R.id.groupName);
        mGroupNickName=(TextView)findViewById(R.id.name);
        mExit=(Button)findViewById(R.id.exit);
        mMakeTask=(Button)findViewById(R.id.makeTask);
        mDestroy=(Button)findViewById(R.id.destroy);
        mMember=(Button)findViewById(R.id.member);
        myPreferenceManager=mApplication.getSpUtil();

//      mGroupIcon.setImageResource(Integer.parseInt(mGroup.getGroupIcon()));
        mGroupName.setText(mGroup.getGroupName());
        mGroupNickName.setText(mGroup.getGroupNick());
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequest(GlobleData.GROUP_EXIT);
            }
        });

        mDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequest(GlobleData.GROUP_CANCEL);

            }
        });

        mMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupActivity.this,MemberActivity.class);
                intent.putExtra("groupName",mGroup.getGroupName());
                startActivity(intent);
            }
        });
        mMakeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupActivity.this,MakeTaskActivity.class);
                intent.putExtra("groupName",mGroup.getGroupName());
                startActivity(intent);
            }
        });

        if (mGroup.getGroupMaster().equals(myPreferenceManager.getUserName())){
            mExit.setVisibility(View.GONE);
            mDestroy.setVisibility(View.VISIBLE);
            mMakeTask.setVisibility(View.VISIBLE);
        }else {
            mExit.setVisibility(View.VISIBLE);
            mDestroy.setVisibility(View.GONE);
            mMakeTask.setVisibility(View.GONE);
        }

    }

    private void setRequest(String url){
        formparams.add(new NameValuePair("groupname",mGroup.getGroupName()));
        formparams.add(new NameValuePair("username",mApplication.getSpUtil().getUserName()));
        new Thread(new MyRunnable(formparams,url,handler)).start();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    DialogUtil.showToast(mApplication, "返回值错误");
                    break;
                case 2:
                    DialogUtil.showToast(mApplication, "网络错误");
                    break;
                case 3:
                    DialogUtil.showToast(mApplication, "操作成功");
                    mApplication.getGroupDB().delGroup(mGroup.getGroupName());
                    groupActivity.finish();
                    Groups.isFlash=true;

                    break;
                default:
                    break;

            }
        }
    };

}
