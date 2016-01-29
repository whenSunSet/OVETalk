package talk.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.heshixiyang.ovetalk.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import talk.TalkApplication;
import talk.util.DialogUtil;
import talk.util.MyPreferenceManager;
import talk.util.MyRunnable;

public class GroupsFind extends Fragment {
    public static final int SEND_JOIN_MESSAGE_ERROR=1;
    public static final int SEND_JOIN_MESSAGE_INTERNET_ERROR=2;
    public static final int SEND_JOIN_MESSAGE_SUCCESS=3;

    private MyPreferenceManager myPreferenceManager;
    private TalkApplication mApplication;
    private MyRunnable mRunnable;
    private Thread mThread;
    private View view;

    private EditText mGroupNameEdit;
    private Button mAddGroup;
    private List<NameValuePair> formparams;
    private String mGroupName;

    public GroupsFind() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init(inflater);

        return view;
    }

    public View init(LayoutInflater inflater){
        mApplication=(TalkApplication)getActivity().getApplication();
        view=inflater.inflate(R.layout.find_into_group_layout,null);
        mGroupNameEdit=(EditText)view.findViewById(R.id.groupName);
        mAddGroup=(Button)view.findViewById(R.id.send_button);
        myPreferenceManager=mApplication.getSpUtil();
        formparams = new ArrayList<NameValuePair>();

        mAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGroupName = mGroupNameEdit.getText().toString();
                if (TextUtils.isEmpty(mGroupName)) {
                    DialogUtil.showToast(mApplication, "你还没输入文字呢");
                    return;
                }

                formparams.add(new BasicNameValuePair("groupname", mGroupName));
                formparams.add(new BasicNameValuePair("username", myPreferenceManager.getUserName()));
//                mRunnable=new MyRunnable(formparams,);
                mThread=new Thread(mRunnable);
                mThread.start();


            }
        });

        return view;
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEND_JOIN_MESSAGE_ERROR:
                    DialogUtil.showToast(mApplication, "返回值错误");

                    break;
                case SEND_JOIN_MESSAGE_INTERNET_ERROR:
                    DialogUtil.showToast(mApplication, "网络错误");
                    break;
                case SEND_JOIN_MESSAGE_SUCCESS:
                    JSONObject jsonObject=null;

                    try {
                        jsonObject=new JSONObject(msg.obj.toString());
                        if (String.valueOf(jsonObject.get("res")).equals("0")){
                            DialogUtil.showToast(mApplication, "群组不存在");
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    DialogUtil.showToast(mApplication, "发送加入请求成功");
                    break;
                default:
                    break;
            }
        }
    };

}
