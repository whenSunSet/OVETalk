package talk.activity.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heshixiyang.ovetalk.R;

import java.util.List;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.activity.aboutGroup.GroupActivity;
import talk.activity.create.MakeTaskActivity;
import talk.activity.supers.IndicatorFragmentActivity;
import talk.datebase.GroupMessageDB;
import talk.fragment.GroupChatting;
import talk.fragment.GroupTask;
import talk.fragment.GroupWork;
import talk.model.Group;
import talk.model.GroupChatMessage;
import talk.model.Message;
import talk.model.TabInfo;
import talk.util.MyRunnable;

/**
 * Created by asus on 2015/11/14.
 */
public class  GroupAll extends IndicatorFragmentActivity {
    private static final String TAG="GroupAll";
    //-------------当前的Group
    public Group mGroup;
    public boolean isSystemGroup;
    public GroupMessageDB mGroupMessageDB;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    mGroupMessageDB.update("messageImage",String.valueOf(GlobleData.DISAGREE_USER_TO_GROUP),(String)msg.obj,mApplication.getSpUtil().getUserName());
                    flashFragment();

                    break;
                case 1:
                    mGroupMessageDB.update("messageImage",String.valueOf(GlobleData.AGREE_USER_TO_GROUP),(String)msg.obj,mApplication.getSpUtil().getUserName());
                    flashFragment();

                    break;
                default:
                    break;

            }
        }
    };
        //-------------view控件
    private TextView textView;
    private ImageView myGroup;
    private ImageView mMakeTask;
    private ImageView mMakeWork;
    //--------------Activity是否重新Resume过
    private Boolean isResume=false;
    private MyRunnable myRunnable;
    private Thread mTread;
    private Boolean isMaster=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Groups.isFlash=true;
        GroupAll.isFlash=false;

        initView();

    }

    public void initView() {
        registerMessageReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receive(context, intent);
            }
        });

        isSystemGroup=getIntent().getStringExtra("groupName").equals("-1");
        mGroupMessageDB=mApplication.getGroupMessageDB();
        mGroup =(Group)((TalkApplication) getApplication()).map.get("nowGroup");
        //判断当前的用户是不是当前群的master
        if (mGroup.getGroupMaster().equals(mApplication.getSpUtil().getUserName())){
            isMaster=true;
        }else {
            isMaster=false;
        }

        if (mTitle.getVisibility()!=View.GONE){
            textView = (TextView) findViewById(R.id.textView1);
            textView.setText(mGroup.getGroupNick());
            addView();
        }

    }

    //----------------动态添加一个控件
    public void addView(){

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）

        myGroup=new ImageView(this);
        myGroup.setImageResource(R.drawable.ic_launcher);
        myGroup.setId(R.drawable.ic_launcher + 1);

        mMakeTask=new ImageView(this);
        mMakeTask.setImageResource(R.drawable.ic_launcher);
        mMakeWork.setId(R.drawable.ic_launcher + 2);

        mMakeWork=new ImageView(this);
        mMakeWork.setImageResource(R.drawable.ic_launcher);
        mMakeWork.setId(R.drawable.ic_launcher + 3);

        myGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAll.this, GroupActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("group", mGroup);
                intent.putExtra("group", mGroup);
                startActivity(intent);
            }
        });


        mMakeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAll.this, MakeTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("group", mGroup);
                intent.putExtra("group", mGroup);
                startActivityForResult(intent,RESULT_OK);
            }
        });

        mMakeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAll.this, GroupActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("group", mGroup);
                intent.putExtra("group", mGroup);
                startActivity(intent);
            }
        });
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin=width-150;
        layoutParams.topMargin=0;
        mTitle.addView(myGroup, layoutParams);
        mTitle.addView(mMakeTask, layoutParams);
        mTitle.addView(mMakeWork, layoutParams);

        myGroup.setVisibility(View.VISIBLE);
        mMakeTask.setVisibility(View.GONE);
        mMakeWork.setVisibility(View.GONE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
        if (GroupAll.isFlash){
            flashFragment();
        }
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        if (position==0){
            myGroup.setVisibility(View.VISIBLE);
            mMakeTask.setVisibility(View.GONE);
            mMakeWork.setVisibility(View.GONE);
        }else if (position==1&&isMaster){
                myGroup.setVisibility(View.GONE);
                mMakeTask.setVisibility(View.VISIBLE);
                mMakeWork.setVisibility(View.GONE);
        }else if (position==2){
            myGroup.setVisibility(View.GONE);
            mMakeTask.setVisibility(View.GONE);
            mMakeWork.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int supplyTabs(List<TabInfo> tabs) {
        //如果是SystemGroup群
        if (getIntent().getStringExtra("groupName").equals("-1")){
            tabs.add(new TabInfo(FRAGMENT_ONE, "SystemGroup",
                    GroupChatting.class));
            mTitle.setVisibility(View.GONE);
            return 0;
        }

        tabs.add(new TabInfo(FRAGMENT_ONE, "聊天",
                GroupChatting.class));
        tabs.add(new TabInfo(FRAGMENT_TWO,"任务",
                GroupTask.class));
        tabs.add(new TabInfo(FRAGMENT_THREE, "作业"
                , GroupWork.class));
        return 0;
    }

    public void receive(Context context, Intent intent) {
            Message message=intent.getParcelableExtra(GlobleData.KEY_MESSAGE);
            GroupChatMessage chatMessage=new GroupChatMessage(message.getMessage(),true,message.getGroupName()
                    ,message.getUserIcon(),true,message.getDate(),message.getUserNickName(),message.getUserName()
                    ,message.getMessageImage(),message.getMessageStatu());

            int messageStatu=message.getMessageStatu();

            if (!isSystemGroup&&message.getGroupName().equals(mGroup.getGroupName())){
                //不是System但是是该group 并且不在chat或者group被解散
                if (messageStatu==GlobleData.USER_CANCEL_GROUP){
                    //如果群被注销，则finish
                    Toast.makeText(mApplication, "该群已经解散", Toast.LENGTH_SHORT).show();
                    finish();
                }

                if ((mCurrentTab==1&&messageStatu==GlobleData.MASTER_PUT_TASK)||(mCurrentTab==2&&messageStatu==GlobleData.USER_PUT_HOMEWORK)){
                    //如果不在chat 且消息类型匹配
                    flashFragment();
                    return;
                }else if (mCurrentTab==1||mCurrentTab==2){
                    //如果不在chat 且消息类型不匹配
                    GroupAll.isFlash=true;
                    return;
                }
            }
        //其他情况 添加一个消息item 然后刷新
        ((GroupChatting) (mTabs.get(0).fragment)).flash(chatMessage);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case 1:
                if (mCurrentTab==0) {
                    ((GroupChatting) (mTabs.get(0).fragment)).addMessage(String.valueOf(data.getIntExtra("url",0)), data.getStringExtra("img"));
                }
                break;
            default:
                break;
        }
    }

    //-------------------生命周期函数，负责对 isForeground 的判断
    @Override
    protected void onResume() {
        super.onResume();
        isForeground=true;

        if (mApplication.getGroupDB().getGroup(mGroup.getGroupName())==null){
            finish();
            return;
        }

        if (isResume&& GroupAll.isFlash) {
            flashFragment();
        }

        isResume=true;
    }
}
