package talk.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.heshixiyang.ovetalk.R;

import java.util.HashMap;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.util.DialogUtil;
import talk.http.SendMessage;

public class GroupsFind extends Fragment {
    private TalkApplication mApplication;
    private View mView;
    private EditText mGroupIdEdit;
    private Button mAddGroup;
    private String  mGroupId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init(inflater);
        return mView;
    }

    public View init(LayoutInflater inflater){
        mApplication=(TalkApplication)getActivity().getApplication();
        mView =inflater.inflate(R.layout.find_into_group_layout,null);
        mGroupIdEdit =(EditText) mView.findViewById(R.id.groupId);
        mAddGroup=(Button) mView.findViewById(R.id.send_button);

        mAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGroupId = mGroupIdEdit.getText().toString();
                if (TextUtils.isEmpty(mGroupId)) {
                    DialogUtil.showToast(mApplication, "你还没输入小组号呢");
                    return;
                }
                sendMessage(GlobleData.joinOrExitGroup);
            }
        });
        return mView;
    }
    private void sendMessage(String url){
        HashMap<String,String> paramter=new HashMap<>();
        paramter.put(GlobleData.GROUP_ID, mGroupId);
        paramter.put(GlobleData.USER_NAME,mApplication.getSpUtil().getUserId());
        paramter.put(GlobleData.MESSAGE_STATU, String.valueOf(GlobleData.USER_REQUEST_JOIN_GROUP));

        SendMessage.getSendMessage().post(mApplication,GlobleData.USER_REQUEST_JOIN_GROUP,url,paramter,null);
    }
}
