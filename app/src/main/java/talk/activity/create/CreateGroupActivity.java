package talk.activity.create;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.heshixiyang.ovetalk.R;

import org.apache.commons.httpclient.NameValuePair;

import java.util.ArrayList;
import java.util.List;

import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.activity.fragment.Groups;
import talk.model.Group;
import talk.util.DialogUtil;
import talk.util.MyRunnable;

public class CreateGroupActivity extends Activity {
    private EditText groupNickNameText;
    private Button create;
    private TalkApplication mApplication;
    private String groupName=null;
    private String groupNickName=null;
    private Thread mThread;
    private List<NameValuePair> formparams ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        init();
    }

    private void init(){
        create=(Button)findViewById(R.id.create);
        mApplication=(TalkApplication)getApplication();
        groupNickNameText=(EditText)findViewById(R.id.groupName);

        formparams = new ArrayList<NameValuePair>();
        formparams.add(new NameValuePair("username", mApplication.getSpUtil().getUserName()));
        formparams.add(new NameValuePair("groupicon", "groupIcon"));
        formparams.add(new NameValuePair("groupnickname", ""));


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupNickName = groupNickNameText.getText().toString();
                groupName = mApplication.getSpUtil().getUserName().substring(6)+System.currentTimeMillis();

                if (TextUtils.isEmpty(groupNickName)) {
                    DialogUtil.showToast(CreateGroupActivity.this, "请输入群组的名字");
                } else {
                    formparams.add(new NameValuePair("groupname",groupName));
                    mThread = new Thread(new MyRunnable(formparams, GlobleData.GROUP_CREATE, handler));
                    mThread.start();
                }
            }
        });
    }

    public void addGroup(){
        Group group=new Group(groupName,groupNickName,"",mApplication.getSpUtil().getUserName(),0,0);
        mApplication.getGroupDB().addGroup(group);
        GlobleMethod.setTag(mApplication);
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    DialogUtil.showToast(CreateGroupActivity.this, "返回值错误");
                    groupName=(String)msg.obj;
                    Groups.isFlash=true;
                    addGroup();

                    break;
                case 2:
                    DialogUtil.showToast(CreateGroupActivity.this,"网络错误");
                    groupName=(String)msg.obj;
                    Groups.isFlash=true;
                    addGroup();

                    break;
                case 3:
                    DialogUtil.showToast(mApplication, "创建成功");
                    Groups.isFlash=true;
                    addGroup();
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //从当前Activity回到GroupAll的时候将其设置为需要刷新
        Groups.isFlash=true;
    }
}
