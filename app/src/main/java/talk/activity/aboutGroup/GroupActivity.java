package talk.activity.aboutGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heshixiyang.ovetalk.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.activity.fragment.Groups;
import talk.activity.util.ListViewActivity;
import talk.model.Group;
import talk.util.DialogUtil;
import talk.util.MyHandler;
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
        mDestroy=(Button)findViewById(R.id.destroy);
        mMember=(Button)findViewById(R.id.member);
        myPreferenceManager=mApplication.getSpUtil();

//      mGroupIcon.setImageResource(Integer.parseInt(mGroup.getGroupIcon()));
        mGroupName.setText(mGroup.getGroupName());
        mGroupNickName.setText(mGroup.getGroupNick());
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequest(GlobleData.GROUP_EXIT,GlobleData.USER_OUT_GROUP);
            }
        });

        mDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequest(GlobleData.GROUP_CANCEL,GlobleData.USER_CANCEL_GROUP);
            }
        });

        mMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupActivity.this,ListViewActivity.class);
                intent.putExtra("groupName",mGroup.getGroupName());
                intent.putExtra("which",GlobleData.GROUP_MEMBER_LIST);
                startActivity(intent);
            }
        });

        if (mGroup.getGroupMaster().equals(myPreferenceManager.getUserName())){
            mExit.setVisibility(View.GONE);
            mDestroy.setVisibility(View.VISIBLE);
        }else {
            mExit.setVisibility(View.VISIBLE);
            mDestroy.setVisibility(View.GONE);
        }

    }

    private void setRequest(String url,int messageStatu){
        formparams.add(new BasicNameValuePair(GlobleData.GROUP_NAME,mGroup.getGroupName()));
        formparams.add(new BasicNameValuePair(GlobleData.USER_NAME,mApplication.getSpUtil().getUserName()));
        formparams.add(new BasicNameValuePair(GlobleData.MESSAGE_STATU,String.valueOf(messageStatu)));
        new Thread(new MyRunnable(formparams,url,handler,messageStatu)).start();
    }
    MyHandler handler= new MyHandler(GroupActivity.this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GlobleData.SEND_MESSAGE_SUCCESS:
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
