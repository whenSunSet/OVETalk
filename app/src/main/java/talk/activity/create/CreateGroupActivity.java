package talk.activity.create;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.heshixiyang.ovetalk.R;

import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.TalkApplication;
import talk.activity.fragment.Groups;
import talk.activity.supers.BasicActivity;
import talk.service.HttpIntentService;
import talk.util.DialogUtil;

public class CreateGroupActivity extends BasicActivity implements View.OnClickListener{
    private Button mCreate;
    private Button mGetIcon;
    private ImageView mIcon;
    private EditText mGroupNickNameText;
    private TalkApplication mApplication;
    private String mGroupNickName =null;
    private Bitmap icon;

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

                    startHttpService(icon, GlobleData.create);
                }
                break;
            case R.id.getIcon:
                GlobleMethod.openPhotoAlbum(this,GlobleData.OPEN_PHOTO_ALBUM);
                break;
            default:
                break;
        }
    }

    private void startHttpService(Bitmap icon, String url) {
        Intent intent=new Intent(this, HttpIntentService.class);
        intent.putExtra(GlobleData.URL,url);
        intent.putExtra(GlobleData.MESSAGE_IMAGE,GlobleMethod.saveImage(icon,GlobleMethod.getFileDir(mApplication)+"/0.jpg").getAbsolutePath());
        intent.putExtra(GlobleData.GROUP_NICK,mGroupNickName);
        intent.putExtra(GlobleData.USER_NAME,mApplication.getSpUtil().getUserId());
        intent.putExtra(GlobleData.MESSAGE_STATU,GlobleData.CREATE_GROUP);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==GlobleData.OPEN_PHOTO_ALBUM){
            Uri uri = data.getData();
            icon=GlobleMethod.getImageFromUri(uri, this);
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
