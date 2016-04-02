package talk.activity.create;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.heshixiyang.ovetalk.R;
import com.loopj.android.http.RequestParams;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.activity.fragment.Groups;
import talk.activity.supers.BasicActivity;
import talk.model.Group;
import talk.util.AsyncHttpClientUtil;
import talk.util.DialogUtil;
import talk.util.MyAsyncHttpResponseHandler;
import talk.util.MyHandler;
import talk.util.MyRunnable;

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
        mCreate =(Button)findViewById(R.id.create);
        mIcon =(ImageView)findViewById(R.id.groupIcon);
        mGetIcon=(Button)findViewById(R.id.getIcon);
        mApplication=(TalkApplication)getApplication();
        mGroupNickNameText =(EditText)findViewById(R.id.groupId);

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

    private void getIcon(){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent, START_ALBUM_CODE);
    }

    private boolean makeGroup(Bitmap icon,String url) {
        AsyncHttpClientUtil asyncHttpClientUtil=new AsyncHttpClientUtil();
        RequestParams requestParams=new RequestParams();
        MyAsyncHttpResponseHandler myAsyncHttpResponseHandler=new MyAsyncHttpResponseHandler(mApplication,GlobleData.MAKE_GROUP){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    mGroupId= String.valueOf(response.getInt("groupId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Groups.mIsFlash =true;
                addGroup();
            }
        };

        requestParams.put(GlobleData.USER_NAME,mApplication.getSpUtil().getUserId());
        requestParams.put(GlobleData.GROUP_NICK_NAME, mGroupNickName);
        try {
            requestParams.put(GlobleData.GROUP_ICON,GlobleMethod.saveIamge(icon,GlobleMethod.getFileDir(mApplication)+"/"+mGroupId+".jpg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        asyncHttpClientUtil.post(url, requestParams, myAsyncHttpResponseHandler);

        return myAsyncHttpResponseHandler.isSuccess();
    }

    public void addGroup(){
        GlobleMethod.saveIamge(icon,GlobleMethod.getFileDir(mApplication)+"/"+mGroupId+".jpg");
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
    private void makeF(){
        formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair(GlobleData.USER_NAME, mApplication.getSpUtil().getUserId()));
        formparams.add(new BasicNameValuePair(GlobleData.GROUP_NICK_NAME, mGroupNickName));
        new Thread(new MyRunnable(formparams, GlobleData.create, handler, GlobleData.DEFAULT));

    }

    private MyHandler handler=new MyHandler(CreateGroupActivity.this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GlobleData.SEND_MESSAGE_SUCCESS:
                    mGroupId=(String)msg.obj;
                    Groups.mIsFlash =true;
                    addGroup();

                    break;
            }
        }
    };
}
