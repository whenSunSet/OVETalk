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
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.activity.util.ListViewActivity;
import talk.model.Group;
import talk.util.SendMessage;


public class GroupActivity extends Activity {
    private ImageView mGroupIcon;
    private TextView mGroupId;
    private TextView mGroupNickName;
    private Button mExit;
    private Button mDestroy;
    private Button mMember;
    private Group mGroup;
    private TalkApplication mApplication;

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygroup);
        init();
    }

    private void init(){
        mApplication=(TalkApplication)getApplication();
        mGroup=getIntent().getParcelableExtra("group");

        mGroupIcon=(ImageView)findViewById(R.id.icon);
        mGroupId =(TextView)findViewById(R.id.groupId);
        mGroupNickName=(TextView)findViewById(R.id.groupNick);
        mExit=(Button)findViewById(R.id.exit);
        mDestroy=(Button)findViewById(R.id.destroy);
        mMember=(Button)findViewById(R.id.member);

//      mGroupIcon.setImageResource(Integer.parseInt(mGroup.getGroupIcon()));
        mGroupId.setText(mGroup.getGroupId());
        mGroupNickName.setText(mGroup.getGroupNick());
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(GlobleData.joinOrExitGroup,GlobleData.USER_OUT_GROUP);
            }
        });

        mDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(GlobleData.logoutGroup,GlobleData.USER_CANCEL_GROUP);
            }
        });

        mMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupActivity.this,ListViewActivity.class);
                intent.putExtra(GlobleData.GROUP_ID,mGroup.getGroupId());
                intent.putExtra("which",GlobleData.GROUP_MEMBER_LIST);
                startActivity(intent);
            }
        });

        if (mGroup.getGroupMaster().equals(mApplication.getSpUtil().getUserId())){
            mExit.setVisibility(View.GONE);
            mDestroy.setVisibility(View.VISIBLE);
        }else {
            mExit.setVisibility(View.VISIBLE);
            mDestroy.setVisibility(View.GONE);
        }

    }

    private void sendMessage(String url, final int messageStatu){
        HashMap<String ,String > paramter=new HashMap();
        HashMap<String ,Object> result=new HashMap();
        paramter.put(GlobleData.GROUP_ID,String.valueOf(mGroup.getGroupId()));
        paramter.put(GlobleData.USER_NAME, mApplication.getSpUtil().getUserId());
        paramter.put(GlobleData.MESSAGE_STATU, String.valueOf(GlobleData.USER_OUT_GROUP));

        result=SendMessage.getSendMessage().post(mApplication,messageStatu,url,paramter,null);
        if ((int)result.get("res")==GlobleData.SEND_MESSAGE_SUCCESS){
            GlobleMethod.deleteGroup(mApplication,mGroup.getGroupId(),mApplication.getSpUtil().getUserId());
        }
    }
}
