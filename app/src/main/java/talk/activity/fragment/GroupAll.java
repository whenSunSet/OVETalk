package talk.activity.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import talk.activity.create.MakeHomeWorkActivity;
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
import talk.model.Task;
import talk.model.Work;

/**
 * Created by asus on 2015/11/14.
 */
public class  GroupAll extends IndicatorFragmentActivity {
    private static final String TAG="GroupAll";

    public Group mGroup;

    public boolean mIsSystemGroup;

    public GroupMessageDB mGroupMessageDB;

    private TextView textView;

    private ImageView myGroup;

    private ImageView mMakeTask;

    private ImageView mMakeWork;

    private Boolean mIsResume =false;

    private Boolean mIsMaster =false;

    public static boolean mIsChattingFlash =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Groups.mIsFlash =true;
        GroupAll.mIsFlash =false;
        GroupAll.mIsChattingFlash =false;

        initView();
    }
    public void initView() {
        registerMessageReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receive(context, intent);
            }
        });
        mIsSystemGroup =getIntent().getStringExtra(GlobleData.GROUP_ID).equals("-1");
        mGroupMessageDB=mApplication.getGroupMessageDB();
        mGroup =(Group)((TalkApplication) getApplication()).map.get("nowGroup");
        //判断当前的用户是不是当前群的master
        if (mGroup.getGroupMaster().equals(mApplication.getSpUtil().getUserId())){
            mIsMaster =true;
        }else {
            mIsMaster =false;
        }

        if (mTitle.getVisibility()!=View.GONE){
            textView = (TextView) findViewById(R.id.textView1);
            textView.setText(mGroup.getGroupNick());
            addView();
        }

    }
    public void receive(Context context, Intent intent) {
        Message message=intent.getParcelableExtra(GlobleData.KEY_MESSAGE);
        GroupChatMessage chatMessage=new GroupChatMessage(message.getMessage(),true,message.getGroupId()
                ,message.getUserIcon(),true,message.getDate(),message.getUserNickName(),message.getUserId()
                ,message.getMessageImage(),message.getMessageStatu());

        int messageStatu=message.getMessageStatu();

        if (!mIsSystemGroup &&message.getGroupId().equals(mGroup.getGroupId())){
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
                GroupAll.mIsFlash =true;
                return;
            }
        }
        //其他情况 添加一个消息item 然后刷新
        ((GroupChatting) (mTabs.get(0).fragment)).flash(GlobleData.SELECT_LAST,chatMessage);
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
        mMakeTask.setId(R.drawable.ic_launcher + 2);

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
                intent.putExtra(GlobleData.GROUP_ID, mGroup.getGroupId());
                startActivityForResult(intent,GlobleData.START_MAKE_TASK_ACTIVITY);
            }
        });

        mMakeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAll.this, MakeHomeWorkActivity.class);
                intent.putExtra(GlobleData.GROUP_ID, mGroup.getGroupId());
                startActivityForResult(intent, GlobleData.START_MAKE_HOMEWORK_ACTIVITY);
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
        if (GroupAll.mIsFlash ||GroupAll.mIsChattingFlash){
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
        }else if (position==1&& mIsMaster){
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
        if (getIntent().getStringExtra(GlobleData.GROUP_ID).equals("-1")){
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==GlobleData.START_MAKE_TASK_ACTIVITY){
            ((GroupChatting) (mTabs.get(0).fragment)).addAndSendMessage(
                    "我发布了一个任务，快来看看吧",
                    null,
                    10,
                    null,
                    new Task(data.getStringExtra("path"), data.getIntExtra("idInGroup", GlobleData.DEFAULT)));
        }else if (resultCode==GlobleData.START_MAKE_HOMEWORK_ACTIVITY){
            ((GroupChatting) (mTabs.get(0).fragment)).addAndSendMessage(
                    "我发布了一个作业，快来看看吧",
                    null,
                    11,
                    new Work(data.getIntExtra("taskId", GlobleData.DEFAULT),
                            data.getIntExtra("idInTask", GlobleData.DEFAULT),
                            data.getStringExtra("path"),
                            resultCode),
                    null);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mIsForeground =true;
        if (mApplication.getGroupDB().getGroup(mGroup.getGroupId())==null){
            finish();
            return;
        }
        if (mIsResume && GroupAll.mIsFlash) {
            flashFragment();
        }else if(mIsResume && GroupAll.mIsChattingFlash){
            ((GroupChatting) (mTabs.get(0).fragment)).flash(GlobleData.SELECT_LAST,null);
        }
        mIsResume =true;
    }
}
