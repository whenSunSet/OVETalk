package talk.activity.create;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import org.apache.commons.httpclient.NameValuePair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import talk.TalkApplication;
import talk.activity.fragment.GroupAll;
import talk.activity.supers.BasicActivity;
import talk.activity.util.GugleFileActivity;
import talk.activity.util.ListViewActivity;
import talk.model.Work;
import talk.util.MyRunnable;

public class MakeHomeWorkActivity extends BasicActivity {

    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private Button mLastStep;
    private Button mNextStep;
    private int nowStep=1;

    private Work mWork;
    private TalkApplication mApplication;
    private String[] mDate;
    private LinearLayout mAll;
    private LinearLayout.LayoutParams layoutParams;

    private Button mChooseType;
    private Button mChooseTask;
    private ListView mItem;
    private ArrayAdapter<String > mAdapter;
    private Animation mAnimationExpand;//显示listView
    private Animation mAnimationPullBack;//收缩listView
    private List<NameValuePair> formparams;
    private boolean isListViewVisible=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_home_work);
        init();
    }

    public void init(){
        mApplication=(TalkApplication)getApplication();
        mDate= new String[]{"文档", "音频", "视频"};
        mWork=new Work();
        formparams = new ArrayList<NameValuePair>();

        mAnimationExpand = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
        mAnimationPullBack= new ScaleAnimation(1.0f, 1.0f, 1.0f, 0.0f);
        mAnimationExpand.setDuration(500);
        mAnimationPullBack.setDuration(500);
        mAdapter=new ArrayAdapter<String>(mApplication,R.layout.text,mDate);
        mAll=(LinearLayout)findViewById(R.id.all);
        mLastStep=(Button)findViewById(R.id.lastStep);
        mNextStep=(Button)findViewById(R.id.nextStep);
        layoutParams=new LinearLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT,GridLayout.LayoutParams.WRAP_CONTENT);
        initStep();

        mLastStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (nowStep) {
                    case 1:
                        finish();
                        break;
                    case 2:
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
                switch (nowStep) {
                    case 1:
                        stepTwo();
                        break;
                    case 2:
                        mWork.setMaster(mApplication.getSpUtil().getUserName());
                        mWork.setIdInTask(mApplication.getWorkDB().getTaskWorkNum(mWork.getGroupName(), mWork.getTaskId()) + 1);
                        mWork.setClickNumber(0);
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
                        simpleDateFormat.applyPattern("yyyy年MM月dd日HH时mm分ss秒");
                        mWork.setDate(simpleDateFormat.format(new Date()));

                        mApplication.getWorkDB().add(mWork);
                        sendWork();
                        GroupAll.isFlash=true;
                        finish();
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
        mItem.setId(R.id.all + 2);
        mItem.setAdapter(mAdapter);
        mItem.setVisibility(View.GONE);
        mItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String date = (String) (mItem.getItemAtPosition(position));
                Intent intent=new Intent(MakeHomeWorkActivity.this,GugleFileActivity.class);
                if (date.equals("文档")) {
                    mChooseType.setText("选择发布文件类型：文档");
                    intent.putExtra("fileType", 0);
                    startActivityForResult(intent, 2);
                    mWork.setType(0);
                } else if (date.equals("音频")) {
                    mChooseType.setText("选择发布文件类型：音频");
                    intent.putExtra("fileType", 1);
                    startActivityForResult(intent,2);
                    mWork.setType(1);
                } else {
                    mChooseType.setText("选择发布文件类型：视频");
                    mWork.setType(2);
                }
                mItem.startAnimation(mAnimationPullBack);
                mItem.setVisibility(View.GONE);
                isListViewVisible = false;
            }
        });


        mChooseType =new Button(mApplication);
        mChooseType.setId(R.id.all + 1);
        mChooseType.setText("选择发布文件类型：文档");
        mChooseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation;
                if (isListViewVisible) {
                    animation = mAnimationPullBack;
                    mItem.setVisibility(View.GONE);
                } else {
                    animation = mAnimationExpand;
                    mItem.setVisibility(View.VISIBLE);
                }

                isListViewVisible = !isListViewVisible;
                mItem.startAnimation(animation);
            }
        });

        //stepTwo
        mChooseTask=new Button(mApplication);
        mChooseType.setId(R.id.all + 2);
        mChooseType.setText("请选择任务");
        mChooseTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MakeHomeWorkActivity.this, ListViewActivity.class);
                intent.putExtra("which",1);
                startActivityForResult(intent,1);
            }
        });
    }
    private void stepOne(){
        nowStep=1;

        mAll.addView(mChooseType, layoutParams);
        mAll.addView(mItem, layoutParams);

    }

    private void stepTwo(){
        nowStep=2;

        mAll.removeView(mChooseType);
        mAll.removeView(mItem);
        mAll.addView(mChooseTask,layoutParams);

    }

    private void sendWork(){
        formparams.clear();
        formparams.add(new NameValuePair("idInTask", String.valueOf(mWork.getIdInTask())));
        formparams.add(new NameValuePair("groupName", mWork.getGroupName()));
        formparams.add(new NameValuePair("master", mWork.getMaster()));
        formparams.add(new NameValuePair("type", String.valueOf(mWork.getType())));
        formparams.add(new NameValuePair("path", mWork.getPath()));
        formparams.add(new NameValuePair("taskId",String.valueOf(mWork.getTaskId()) ));
        formparams.add(new NameValuePair("clickNumber", String.valueOf(mWork.getClickNumber())));
        formparams.add(new NameValuePair("date", mWork.getDate()));
        new Thread(new MyRunnable(formparams,"",handler)).start();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                mWork.setTaskId(data.getIntExtra("idInGroup",-999));
                mWork.setGroupName(data.getStringExtra("groupName"));
                break;
            case 2:
                mWork.setPath(data.getStringExtra("filePath"));
                break;
            default:
                break;
        }
    }

}
