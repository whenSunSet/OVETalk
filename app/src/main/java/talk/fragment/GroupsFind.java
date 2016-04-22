package talk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.heshixiyang.ovetalk.R;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.service.HttpIntentService;
import talk.util.DialogUtil;

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
                startHttpService();
            }
        });
        return mView;
    }
    private void startHttpService(){
        Intent intent=new Intent(getActivity(), HttpIntentService.class);
        intent.putExtra(GlobleData.URL,GlobleData.join);
        intent.putExtra(GlobleData.GROUP_ID,Integer.parseInt(mGroupId));
        intent.putExtra(GlobleData.USER_NAME,mApplication.getSpUtil().getUserId());
        intent.putExtra(GlobleData.MESSAGE_STATU, GlobleData.USER_REQUEST_JOIN_GROUP);
        intent.putExtra(GlobleData.IS_MESSAGE, true);
        getActivity().startService(intent);
    }
}
