package talk.activity.create;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.heshixiyang.ovetalk.R;
import com.loopj.android.http.RequestParams;

import org.apache.http.NameValuePair;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.activity.fragment.Groups;
import talk.activity.supers.BasicActivity;
import talk.model.Group;
import talk.util.DialogUtil;
import talk.util.SendMessage;

public class CreateGroupActivity extends BasicActivity implements View.OnClickListener{
    private EditText mGroupNickNameText;
    private Button mCreate;
    private ImageView mIcon;
    private Button mGetIcon;
    private TalkApplication mApplication;
    private String mGroupId=null;
    private String mGroupNickName =null;
    private String mIconPath=null;
    private List<NameValuePair> formparams ;
    private Bitmap icon;

    public static final int START_ALBUM_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        init();
    }

    private void init(){
        mApplication=(TalkApplication)getApplication();
        mGroupNickNameText =(EditText)findViewById(R.id.groupId);
        mIcon =(ImageView)findViewById(R.id.groupIcon);
        mGetIcon=(Button)findViewById(R.id.getIcon);
        mCreate =(Button)findViewById(R.id.create);

        mGetIcon.setOnClickListener(this);
        mCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create:
                mGroupNickName = mGroupNickNameText.getText().toString();
                if (TextUtils.isEmpty(mGroupNickName)) {
                    DialogUtil.showToast(CreateGroupActivity.this, "请输入群组的名字");
                } else {
                    makeGroup(icon, GlobleData.create);
                }
                break;
            case R.id.getIcon:
                getIcon();
                break;
            default:
                break;
        }
    }

    private void makeGroup(Bitmap icon,String url) {
        HashMap<String,Object> result=null;
        RequestParams requestParams=new RequestParams();
        requestParams.put(GlobleData.USER_NAME,mApplication.getSpUtil().getUserId());
        requestParams.put(GlobleData.GROUP_NICK_NAME,mGroupNickName);
        try {
            requestParams.put(GlobleData.GROUP_ICON,GlobleMethod.saveIamge(icon,GlobleMethod.getFileDir(mApplication)+"/"+mGroupId+".jpg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        result=SendMessage.getSendMessage().post(mApplication,GlobleData.CREATE_GROUP,url,null,requestParams);
        if (result.get("res")==1){
            mGroupId= String.valueOf(result.get("groupId"));
            Groups.mIsFlash =true;
            addGroup();
        }
        return ;
    }

    public void addGroup(){
        Group group=new Group(mGroupId, mGroupNickName
                ,GlobleMethod.getFileDir(mApplication)+"/"+mGroupId+".jpg"
                ,mApplication.getSpUtil().getUserId(),0,0);
        mApplication.getGroupDB().addGroup(group);
        GlobleMethod.setTag(mApplication);
        GlobleMethod.addUserToGroup(mApplication
                , new talk.model.Message(
                        mApplication.getSpUtil().getUserId()
                        , mGroupId
                        , GlobleMethod.getNowDate()
                        , mApplication.getSpUtil().getUserIcon()
                        , mApplication.getSpUtil().getUsreNickName()),
                GlobleData.ADD_MASTER);
    }

    private void getIcon(){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, START_ALBUM_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==START_ALBUM_CODE){
            Uri uri = data.getData();
            icon=GlobleMethod.getImage(uri, this);
            mIcon.setImageBitmap(icon);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //从当前Activity回到GroupAll的时候将其设置为需要刷新
        Groups.mIsFlash =true;
    }
}
