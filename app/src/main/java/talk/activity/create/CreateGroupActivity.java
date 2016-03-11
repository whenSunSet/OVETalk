package talk.activity.create;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.heshixiyang.ovetalk.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.activity.fragment.Groups;
import talk.activity.supers.BasicActivity;
import talk.model.Group;
import talk.util.DialogUtil;
import talk.util.MyHandler;
import talk.util.MyRunnable;

public class CreateGroupActivity extends BasicActivity{
    private EditText groupNickNameText;
    private Button create;
    private TalkApplication mApplication;
    private String groupName=null;
    private String groupNickName=null;
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

        formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("userName", mApplication.getSpUtil().getUserName()));
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupNickName = groupNickNameText.getText().toString();
                if (TextUtils.isEmpty(groupNickName)) {
                    DialogUtil.showToast(CreateGroupActivity.this, "请输入群组的名字");
                } else {
                    formparams.add(new BasicNameValuePair("groupNickName", groupNickName));
                    new Thread(new MyRunnable(formparams,"",handler,GlobleData.DEFAULT));
                }
            }
        });
    }

    private MyHandler handler=new MyHandler(CreateGroupActivity.this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GlobleData.SEND_MESSAGE_SUCCESS:
                    groupName=(String)msg.obj;
                    Groups.isFlash=true;
                    addGroup();

                    break;
            }
        }
    };
    public void addGroup(){
        Group group=new Group(groupName,groupNickName,"",mApplication.getSpUtil().getUserName(),0,0);
        mApplication.getGroupDB().addGroup(group);
        GlobleMethod.setTag(mApplication);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //从当前Activity回到GroupAll的时候将其设置为需要刷新
        Groups.isFlash=true;
    }
}
