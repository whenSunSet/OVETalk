package talk.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.heshixiyang.ovetalk.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.util.DialogUtil;
import talk.util.MyHandler;
import talk.util.MyJsonObjectRequest;
import talk.util.MyPreferenceManager;
import talk.util.MyResponseErrorListener;
import talk.util.MyRunnable;

public class GroupsFind extends Fragment {
    private MyPreferenceManager myPreferenceManager;
    private TalkApplication mApplication;
    private View view;
    private EditText mGroupNameEdit;
    private Button mAddGroup;
    private List<NameValuePair> formparams;
    private String mGroupName;

    public GroupsFind() {}
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

        mAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGroupName = mGroupNameEdit.getText().toString();
                if (TextUtils.isEmpty(mGroupName)) {
                    DialogUtil.showToast(mApplication, "你还没输入文字呢");
                    return;
                }
//                makeF();
                sendMessage("");
            }
        });
        return view;
    }
    private void sendMessage(String url){
        JSONObject jsonObject = new JSONObject();
        MyJsonObjectRequest jsonObjectRequest = new MyJsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new MyResponseErrorListener(getActivity(),GlobleData.DEFAULT),
                makeMap(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                    }
                }
        );
        mApplication.getRequestQueue().add(jsonObjectRequest);
    }

    private HashMap makeMap(){
        Map map=new HashMap();
        map.put(GlobleData.GROUP_NAME, mGroupName);
        map.put(GlobleData.USER_NAME,mApplication.getSpUtil().getUserName());
        map.put(GlobleData.MESSAGE_STATU, String.valueOf(GlobleData.USER_REQUEST_JOIN_GROUP));
        return makeMap();
    }

    private void makeF(){
        formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair(GlobleData.GROUP_NAME, mGroupName));
        formparams.add(new BasicNameValuePair(GlobleData.USER_NAME, myPreferenceManager.getUserName()));
        formparams.add(new BasicNameValuePair(GlobleData.MESSAGE_STATU,String.valueOf(GlobleData.USER_REQUEST_JOIN_GROUP)));
        new Thread(new MyRunnable(formparams,"",handler,GlobleData.USER_REQUEST_JOIN_GROUP));
    }
    MyHandler handler= new MyHandler(getActivity()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GlobleData.SEND_MESSAGE_SUCCESS:
                    DialogUtil.showToast(mApplication, "发送加入请求成功");
                case GlobleData.NO_SUCH_GROUP:
                    DialogUtil.showToast(mApplication, "不好意思,没有这个群组");
                    break;
                default:
                    break;
            }
        }
    };

}
