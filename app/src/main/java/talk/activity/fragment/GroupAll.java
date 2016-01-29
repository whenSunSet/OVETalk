package talk.activity.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.heshixiyang.ovetalk.R;

import java.util.List;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.activity.aboutGroup.GroupActivity;
import talk.activity.supers.IndicatorFragmentActivity;
import talk.adapter.ChatMessageAdapter;
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
public class  GroupAll extends IndicatorFragmentActivity implements ChatMessageAdapter.OnCallBackMore,ChatMessageAdapter.OnCallBackDialog{
    private static final String TAG="GroupAll";

    //-------------view控件
    private TextView textView;
    private ImageView myGroup;

    //-------------当前的Group
    public Group mGroup;


    //--------------Activity是否重新Resume过
    private Boolean isResume=false;

    private MyRunnable myRunnable;

    private Thread mTread;

    public boolean isSystemGroup;

    public GroupMessageDB mGroupMessageDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Groups.isFlash=true;
        GroupAll.isFlash=false;
        registerMessageReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceive(context, intent);
            }
        });
        initViews();

    }

    @Override
    public void callBackMore() {
        if (isSystemGroup){
            return;
        }

        ((GroupChatting)(mTabs.get(0).fragment)).getmContan().setVisibility(View.GONE);
        ((GroupChatting)(mTabs.get(0).fragment)).setIsVisble(false);

    }

    @Override
    public void callBackDialog(final boolean isAgree, final String time) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(GroupAll.this);
        builder.setTitle("提示");
        if (isAgree){
            builder.setMessage("是否确定其加入该群");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    android.os.Message message=new android.os.Message();
                    message.what=1;
                    message.obj=time;
                    handler.sendMessage(message);
                }
            });
        }else {
            builder.setMessage("是否拒绝其加入该群");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    android.os.Message message=new android.os.Message();
                    message.what=0;
                    message.obj=time;
                    handler.sendMessage(message);
                }
            });
        }

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.create().show();

    }

    public void initViews() {
        super.initViews();
        String groupName=null;
        isSystemGroup=getIntent().getStringExtra("groupName").equals("-1");
        mGroupMessageDB=mApplication.getGroupMessageDB();

        if (isSystemGroup){
            groupName=mApplication.getSpUtil().getUserName();
        }else {
            groupName=getIntent().getStringExtra("groupName");
        }

        mGroup =(Group)((TalkApplication) getApplication()).map.get("nowGroup");

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
        myGroup.setId(R.drawable.ic_launcher+ 1);

        myGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAll.this, GroupActivity.class);
                Bundle bundle=new Bundle();
                bundle.putParcelable("group",mGroup);
                intent.putExtra("group",mGroup);
                startActivity(intent);
            }
        });
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin=width-150;
        layoutParams.topMargin=0;
        mTitle.addView(myGroup, layoutParams);
    }

    //----------------------覆盖父类的函数，当滑动页到了本页的时候，刷新本页。
    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        if (GroupAll.isFlash) {
            ((GroupChatting) (mTabs.get(0).fragment)).flashFragment();
        }
    }

    //------------------覆盖父类的函数 为当前Activity添加滑动页
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
        tabs.add(new TabInfo(FRAGMENT_THREE,"作品"
                , GroupWork.class));
        return 0;
    }



//        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);

    public void onReceive(Context context, Intent intent) {
            Message message=intent.getParcelableExtra(GlobleData.KEY_MESSAGE);
            GroupChatMessage chatMessage=new GroupChatMessage(message.getMessage(),true,message.getGroupName()
                    ,message.getUserIcon(),true,message.getDate(),message.getUserNickName(),message.getUserName(),message.getMessageImage());

            int messageStatu=Integer.parseInt(message.getMessageImage());

            if (messageStatu<=1){
                //当是普通Group的信息时
                if (!message.getGroupName().equals(mGroup.getGroupName())){
                    //如果当前的group不是被发送信息的group，就不操作。
                    Groups.isFlash=true;
                    return;
                }

                //如果当前的fragment是聊天的界面，则调用聊天fragment的函数刷新界面
                if (mCurrentTab==0) {
                    //如果是在第一个界面
                    ((GroupChatting) (mTabs.get(0).fragment)).addNewMessage(chatMessage);
                }else {
                    //如果不是在第一个界面，则把是否要刷新，设置为是
                    GroupAll.isFlash=true;
                }

            }else if (messageStatu==5){
                //当是注销该Group的信息时

                if (message.getGroupName().equals(mGroup.getGroupName())){
                    //如果当前的group是要注销的group，则关闭当前的group,把是否刷新groupList设置为是
                    Groups.isFlash=true;
                    finish();
                    return;
                }else if (!(message.getGroupName().equals(mGroup.getGroupName()))&&!(mApplication.getSpUtil().getUserName().equals(mGroup.getGroupName()))){
                    //如果当前的group不是SystemGroup，也不是要注销的群，把是否刷新groupList设置为是
                    Groups.isFlash=true;
                    return;
                }else {
                    //当前的群是SystemGroup群
                        ((GroupChatting) (mTabs.get(0).fragment)).addNewMessage(chatMessage);
                }


            }else {
                //其他的显示在SsytemGroup的时

                //如果不是SystemGroup群
                if (!(mApplication.getSpUtil().getUserName().equals(mGroup.getGroupName()))){
                    return;
                }else {
                    //如果是，则刷新界面
                    ((GroupChatting) (mTabs.get(0).fragment)).addNewMessage(chatMessage);
                }

            }

            Groups.isFlash=true;
        }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
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
                ((GroupChatting) (mTabs.get(0).fragment)).flashFragment();
        }
        isResume=true;
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    mGroupMessageDB.update("messageImage",String.valueOf(GlobleData.MASTER_DISAGREE),(String)msg.obj,mApplication.getSpUtil().getUserName());
                    ((GroupChatting) (mTabs.get(0).fragment)).flashFragment();

                    break;
                case 1:
                    mGroupMessageDB.update("messageImage",String.valueOf(GlobleData.MASTER_AGREE),(String)msg.obj,mApplication.getSpUtil().getUserName());
                    ((GroupChatting) (mTabs.get(0).fragment)).flashFragment();

                    break;
                default:
                    break;

            }
        }
    };

}
