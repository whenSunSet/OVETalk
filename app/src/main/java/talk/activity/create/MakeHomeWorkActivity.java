package talk.activity.create;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.heshixiyang.ovetalk.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.activity.fragment.GroupAll;
import talk.activity.supers.BasicActivity;
import talk.activity.util.GugleFileActivity;
import talk.activity.util.ListViewActivity;
import talk.model.Work;
import talk.util.DialogUtil;
import talk.util.MyHandler;
import talk.util.MyRunnable;

public class MakeHomeWorkActivity extends BasicActivity {
    private Button mLastStep;
    private Button mNextStep;
    private int mNowStep =GlobleData.STEP_ONE;

    private Work mWork;
    private TalkApplication mApplication;
    private String[] mDate;
    private LinearLayout mAll;
    private LinearLayout.LayoutParams mLayoutParams;

    private Button mChooseType;
    private Button mChooseTask;
    private ListView mItem;
    private ArrayAdapter<String > mAdapter;
    private Animation mAnimationExpand;//显示listView
    private Animation mAnimationPullBack;//收缩listView
    private List<NameValuePair> formparams;
    private boolean mIsListViewVisible =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_task_and_home_work);
        init();
    }

    public void init(){
        mApplication=(TalkApplication)getApplication();
        mDate= new String[]{"文档", "音频", "视频"};
        mWork=new Work();
        mWork.setGroupId(getIntent().getStringExtra("groupId"));
        formparams = new ArrayList<>();

        mAnimationExpand = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
        mAnimationPullBack= new ScaleAnimation(1.0f, 1.0f, 1.0f, 0.0f);
        mAnimationExpand.setDuration(500);
        mAnimationPullBack.setDuration(500);
        mAdapter=new ArrayAdapter<>(mApplication,R.layout.text,mDate);
        mAll=(LinearLayout)findViewById(R.id.all);
        mLastStep=(Button)findViewById(R.id.lastStep);
        mNextStep=(Button)findViewById(R.id.nextStep);
        mLayoutParams =new LinearLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT,GridLayout.LayoutParams.WRAP_CONTENT);
        initStep();
        mLastStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mNowStep) {
                    case GlobleData.STEP_ONE:
                        finish();
                        break;
                    case GlobleData.STEP_TWO:
                        stepOne();
                        mAll.removeView(mChooseType);
                        break;
                    default:
                        break;
                }
            }
        });
        mNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mNowStep) {
                    case GlobleData.STEP_ONE:
                        stepTwo();
                        break;
                    case GlobleData.STEP_TWO:
                        if (sendHomeWork(GlobleData.pushWork,mApplication)){
                            mApplication.getWorkDB().add(mWork);
                            GroupAll.mIsFlash = true;

                            Intent intent = new Intent();
                            intent.putExtra("taskId", mWork.getTaskId());
                            intent.putExtra("idInTask", mWork.getIdInTask());
                            intent.putExtra("path", mWork.getPath());
                            setResult(GlobleData.START_MAKE_HOMEWORK_ACTIVITY, intent);
                            finish();
                        }

//                        try {
//                            GlobleMethod.upLoadFile(mWork,"work","",mApplication);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
                        break;
                    default:
                        break;
                }
            }
        });
        stepOne();
    }
    private void initStep(){
        //stepOne
        mItem=new ListView(mApplication);
        mItem.setId(R.id.all + 12);
        mItem.setAdapter(mAdapter);
        mItem.setVisibility(View.GONE);
        mItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String date = (String) (mItem.getItemAtPosition(position));
                Intent intent=new Intent(MakeHomeWorkActivity.this,GugleFileActivity.class);
                if (date.equals("文档")) {
                    mChooseType.setText("选择发布文件类型：文档");
                    intent.putExtra("fileType",GlobleData.IS_TEXT);
                    startActivityForResult(intent, GlobleData.CHOOSE_FILE);
                    mWork.setType(GlobleData.IS_TEXT);
                } else if (date.equals("音频")) {
                    mChooseType.setText("选择发布文件类型：音频");
                    intent.putExtra("fileType", GlobleData.IS_MUSIC);
                    startActivityForResult(intent,GlobleData.CHOOSE_FILE);
                    mWork.setType(GlobleData.IS_MUSIC);
                } else {
                    mChooseType.setText("选择发布文件类型：视频");
                    mWork.setType(GlobleData.IS_VIDEO);
                }
                mItem.startAnimation(mAnimationPullBack);
                mItem.setVisibility(View.GONE);
                mIsListViewVisible = false;
            }
        });
        mChooseType =new Button(mApplication);
        mChooseType.setId(R.id.all + 11);
        mChooseType.setText("选择发布文件类型：文档");
        mChooseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation;
                if (mIsListViewVisible) {
                    animation = mAnimationPullBack;
                    mItem.setVisibility(View.GONE);
                } else {
                    animation = mAnimationExpand;
                    mItem.setVisibility(View.VISIBLE);
                }

                mIsListViewVisible = !mIsListViewVisible;
                mItem.startAnimation(animation);
            }
        });

        //stepTwo
        mChooseTask=new Button(mApplication);
        mChooseTask.setId(R.id.all + 13);
        mChooseTask.setText("请选择任务");
        mChooseTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakeHomeWorkActivity.this, ListViewActivity.class);
                intent.putExtra("which", GlobleData.GROUP_TASK_LIST);
                intent.putExtra(GlobleData.GROUP_ID, mWork.getGroupId());
                startActivityForResult(intent, GlobleData.CHOOSE_TASK);
            }
        });
    }
    private void stepOne(){
        mNowStep =GlobleData.STEP_ONE;

        mChooseType.setVisibility(View.VISIBLE);
        mAll.addView(mChooseType, mLayoutParams);
        mAll.addView(mItem, mLayoutParams);

    }
    private void stepTwo(){
        mNowStep =GlobleData.STEP_TWO;

        mAll.removeView(mChooseType);
        mAll.removeView(mItem);
        mAll.addView(mChooseTask, mLayoutParams);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case GlobleData.CHOOSE_TASK:
                mWork.setTaskId(data.getIntExtra("idInGroup", GlobleData.DEFAULT));
                break;
            case GlobleData.CHOOSE_FILE:
                mWork.setPath(data.getStringExtra("filePath"));
                break;
            default:
                break;
        }
    }

    private boolean sendHomeWork(String url,TalkApplication talkApplication){
        mWork.setMaster(mApplication.getSpUtil().getUserId());
        mWork.setIdInTask(mApplication.getWorkDB().getTaskWorkNum(mWork.getGroupId(), mWork.getTaskId()) + 1);
        mWork.setClickNumber(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
        simpleDateFormat.applyPattern("yyyy年MM月dd日HH时mm分ss秒");
        mWork.setDate(simpleDateFormat.format(new Date()));

        boolean isSuccess=false;
        try {
            isSuccess= GlobleMethod.upLoadFile(mWork,"work",url,talkApplication);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    MyHandler handler=new MyHandler(MakeHomeWorkActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GlobleData.SEND_MESSAGE_SUCCESS:
                    DialogUtil.showToast(MakeHomeWorkActivity.this,"发送成功");
                    break;
            }
        }
    };
    private void sendWork(){
        formparams.clear();
        formparams.add(new BasicNameValuePair(GlobleData.GROUP_ID, mWork.getGroupId()));
        formparams.add(new BasicNameValuePair(GlobleData.ID_IN_TASK, String.valueOf(mWork.getIdInTask())));
        formparams.add(new BasicNameValuePair(GlobleData.MASTER, mWork.getMaster()));
        formparams.add(new BasicNameValuePair(GlobleData.TYPE, String.valueOf(mWork.getType())));
        formparams.add(new BasicNameValuePair(GlobleData.TASK_ID,String.valueOf(mWork.getTaskId()) ));
        formparams.add(new BasicNameValuePair(GlobleData.CLICK_NUMBER, String.valueOf(mWork.getClickNumber())));
        formparams.add(new BasicNameValuePair(GlobleData.DATE, mWork.getDate()));
        new Thread(new MyRunnable(formparams,"",handler,GlobleData.DEFAULT)).start();
    }
}
