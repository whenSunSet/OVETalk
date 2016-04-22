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
import talk.model.Group;
import talk.service.HttpIntentService;


public class GroupActivity extends Activity {
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

        ImageView mGroupIcon = (ImageView) findViewById(R.id.icon);
        TextView mGroupId = (TextView) findViewById(R.id.groupId);
        TextView mGroupNickName = (TextView) findViewById(R.id.groupNick);
        Button mExit = (Button) findViewById(R.id.exit);
        Button mDestroy = (Button) findViewById(R.id.destroy);
        Button mMember = (Button) findViewById(R.id.member);

//      mGroupIcon.setImageResource(Integer.parseInt(mGroup.getGroupIcon()));
        mGroupId.setText(String.valueOf(mGroup.getGroupId()));
        mGroupNickName.setText(mGroup.getGroupNick());

        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHttpService(GlobleData.joinOrExitGroup,GlobleData.USER_OUT_GROUP);
            }
        });

        mDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHttpService(GlobleData.logoutGroup,GlobleData.USER_CANCEL_GROUP);
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

    private void startHttpService(String url, final int messageStatu){
        Intent intent=new Intent(GroupActivity.this, HttpIntentService.class);
        intent.putExtra(GlobleData.URL,url);
        intent.putExtra(GlobleData.GROUP_ID,mGroup.getGroupId());
        intent.putExtra(GlobleData.USER_NAME,mApplication.getSpUtil().getUserId());
        intent.putExtra(GlobleData.MESSAGE_STATU, messageStatu);
        intent.putExtra(GlobleData.IS_MESSAGE, true);
        startService(intent);
    }
}
