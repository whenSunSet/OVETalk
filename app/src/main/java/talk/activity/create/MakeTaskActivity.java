package talk.activity.create;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.heshixiyang.ovetalk.R;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.activity.fragment.GroupAll;
import talk.activity.supers.BasicActivity;
import talk.activity.util.GugleFileActivity;
import talk.model.Task;
import talk.util.DialogUtil;

public class MakeTaskActivity extends BasicActivity {
    private Task mTask;
    private TalkApplication mApplication;
    private String[] mDate;
    private LinearLayout mAll;
    private Button mLastStep;
    private Button mNextStep;
    private int nowStep=1;
    private LinearLayout.LayoutParams mLayoutParams;
    private String mGroupId;
    private Button mChoose;
    private ListView mItem;
    private ArrayAdapter<String > mAdapter;
    private Animation mAnimationExpand;//显示listView
    private Animation mAnimationPullBack;//收缩listView
    private EditText mName;
    private EditText mConent;
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
        mTask=new Task();
        mGroupId =getIntent().getStringExtra(GlobleData.GROUP_ID);
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
                switch (nowStep) {
                    case GlobleData.STEP_ONE:
                        finish();
                        break;
                    case GlobleData.STEP_TWO:
                        stepOne();
                        mAll.removeView(mName);
                        break;
                    case GlobleData.STEP_THREE:
                        stepTwo();
                        mAll.removeView(mConent);
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
                    case GlobleData.STEP_ONE:
                        stepTwo();
                        break;
                    case GlobleData.STEP_TWO:
                        if (TextUtils.isEmpty(mName.getText().toString())) {
                            DialogUtil.showToast(mApplication, "还没有输入任务的名字");
                        } else {
                            mTask.setGroupId(mName.getText().toString());
                            stepThree();
                        }
                        break;
                    case GlobleData.STEP_THREE:
                        if (TextUtils.isEmpty(mConent.getText().toString())) {
                            DialogUtil.showToast(mApplication, "还没有输入任务的目标");
                        } else {
                            mTask.setTarget(mName.getText().toString());
                            mTask.setDate(new Date().toString());
                            mTask.setClickNum(0);
                            mTask.setGroupId(mGroupId);
                            mTask.setIdInGroup(mApplication.getTaskDB().getGroupTaskNum(mGroupId) + 1);

                            mApplication.getTaskDB().add(mTask);
                            GroupAll.mIsFlash =true;

//                            try {
//                                GlobleMethod.upLoadFile(mTask,"task","",mApplication);
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }

                            Intent intent=new Intent();
                            intent.putExtra("idInGroup",mTask.getIdInGroup());
                            intent.putExtra("path",mTask.getPath());
                            setResult(GlobleData.START_MAKE_TASK_ACTIVITY,intent);
                            finish();
                        }
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
                Intent intent=new Intent(MakeTaskActivity.this,GugleFileActivity.class);
                if (date.equals("文档")) {
                    mChoose.setText("选择发布文件类型：文档");
                    intent.putExtra("fileType", GlobleData.IS_TEXT);
                    startActivityForResult(intent, 2);
                    mTask.setType(GlobleData.IS_TEXT);
                } else if (date.equals("音频")) {
                    mChoose.setText("选择发布文件类型：音频");
                    intent.putExtra("fileType", GlobleData.IS_MUSIC);
                    startActivityForResult(intent,2);
                    mTask.setType(GlobleData.IS_MUSIC);
                } else {
                    mChoose.setText("选择发布文件类型：视频");
                    mTask.setType(GlobleData.IS_VIDEO);
                }
                mItem.startAnimation(mAnimationPullBack);
                mItem.setVisibility(View.GONE);
                mIsListViewVisible = false;
            }
        });

        mChoose=new Button(mApplication);
        mChoose.setId(R.id.all + 1);
        mChoose.setText("选择发布文件类型：文档");
        mChoose.setOnClickListener(new View.OnClickListener() {
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
        mName=new EditText(mApplication);
        mName.setBackgroundColor(getResources().getColor(R.color.titlebar2));
        mName.setMinLines(1);
        mName.setHint("请输入任务名字");
        mName.setId(R.id.all+3);

        //stepThree
        mConent=new EditText(mApplication);
        mConent.setBackgroundColor(getResources().getColor(R.color.black));
        mName.setMinLines(1);
        mConent.setHint("请输入任务的要求和目标");
        mConent.setId(R.id.all + 4);
    }

    private void stepOne(){
        nowStep=GlobleData.STEP_ONE;

        mAll.addView(mChoose, mLayoutParams);
        mAll.addView(mItem, mLayoutParams);
    }

    private void stepTwo(){
        nowStep=GlobleData.STEP_TWO;

        mAll.removeView(mChoose);
        mAll.removeView(mItem);
        mAll.addView(mName, mLayoutParams);

    }

    private void stepThree(){
        nowStep=GlobleData.STEP_THREE;

        mAll.removeView(mName);
        mAll.addView(mConent, mLayoutParams);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case 2:
                mTask.setPath(data.getStringExtra("filePath"));
                break;
            default:
                break;
        }
    }
}
