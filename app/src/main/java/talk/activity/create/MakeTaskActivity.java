package talk.activity.create;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.Date;

import talk.TalkApplication;
import talk.activity.util.GugleFileActivity;
import talk.model.Task;
import talk.util.DialogUtil;

public class MakeTaskActivity extends Activity {
    private Task mTask;
    private TalkApplication mApplication;
    private String[] mDate;
    private LinearLayout mAll;
    private Button mLastStep;
    private Button mNextStep;
    private int nowStep=1;
    private LinearLayout.LayoutParams layoutParams;
    private String mGroupName;

    private Button mChoose;
    private ListView mItem;
    private ArrayAdapter<String > mAdapter;
    private Animation mAnimationExpand;//显示listView
    private Animation mAnimationPullBack;//收缩listView

    private EditText mName;

    private EditText mConent;

    private boolean isListViewVisible=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_task);
        init();

    }

    private void stepOne(){
        nowStep=1;

        mAll.addView(mChoose, layoutParams);
        mAll.addView(mItem, layoutParams);

    }

    private void stepTwo(){
        nowStep=2;

        mAll.removeView(mChoose);
        mAll.removeView(mItem);
        mAll.addView(mName,layoutParams);

    }

    private void stepThree(){
        nowStep=3;

        mAll.removeView(mName);
        mAll.addView(mConent,layoutParams);

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
                    intent.putExtra("fileType", 0);
                    startActivityForResult(intent, 1);
                    mTask.setType(0);
                } else if (date.equals("音频")) {
                    mChoose.setText("选择发布文件类型：音频");
                    intent.putExtra("fileType", 1);
                    startActivityForResult(intent,1);
                    mTask.setType(1);
                } else {
                    mChoose.setText("选择发布文件类型：视频");
                    mTask.setType(2);
                }
                mItem.startAnimation(mAnimationPullBack);
                mItem.setVisibility(View.GONE);
                isListViewVisible = false;
            }
        });


        mChoose=new Button(mApplication);
        mChoose.setId(R.id.all + 1);
        mChoose.setText("选择发布文件类型：文档");
        mChoose.setOnClickListener(new View.OnClickListener() {
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
        mName=new EditText(mApplication);
        mName.setBackgroundColor(R.color.titlebar2);
        mName.setMinLines(1);
        mName.setHint("请输入任务名字");
        mName.setId(R.id.all+3);

        //stepThree
        mConent=new EditText(mApplication);
        mConent.setBackgroundColor(R.color.black);
        mName.setMinLines(1);
        mConent.setHint("请输入任务的要求和目标");
        mConent.setId(R.id.all+4);
    }
    public void init(){
        mApplication=(TalkApplication)getApplication();
        mDate= new String[]{"文档", "音频", "视频"};
        mTask=new Task();
        mGroupName=getIntent().getStringExtra("groupName");
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
                        mAll.removeView(mName);
                        break;
                    case 3:
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
                    case 1:
                        stepTwo();
                        break;
                    case 2:
                        if (TextUtils.isEmpty(mName.getText().toString())) {
                            DialogUtil.showToast(mApplication, "还没有输入任务的名字");
                        } else {
                            mTask.setGroupName(mName.getText().toString());
                            stepThree();
                        }
                        break;
                    case 3:
                        if (TextUtils.isEmpty(mConent.getText().toString())) {
                            DialogUtil.showToast(mApplication, "还没有输入任务的目标");
                        } else {
                            mTask.setTarget(mName.getText().toString());
                            mTask.setDate(new Date().toString());
                            mTask.setClickNumber(0);
                            mApplication.getTaskDB().add( mTask);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                mTask.setPath(data.getStringExtra("filePath"));
                Log.d("MakeTaskActivity", data.getStringExtra("filePath"));
                break;
            default:
                break;
        }
    }

}
