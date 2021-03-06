package talk.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heshixiyang.ovetalk.R;

import talk.Globle.GlobleMethod;
import talk.activity.util.ListViewActivity;
import talk.adapter.MemberAdapter;
import talk.model.Group;
import talk.model.TaskBean;
import talk.model.WorkBean;

/**
 * Created by heshixiyang on 2016/3/3.
 */
public class MemberFragment extends BasicFragment {
    private Group mGroup;
    private ListViewActivity activity;
    private WorkBean mWorkBean;
    private TaskBean mTaskBean;
    public MemberFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init(inflater);
        return view;
    }
    protected void init(LayoutInflater inflater) {
        super.init(inflater);
        activity=(ListViewActivity)getActivity();
        mGroup=activity.mGroup;
        mTaskBean =activity.mTaskBean;
        mWorkBean =activity.mWorkBean;
        if (activity.mIsWorkClick){
            mData=GlobleMethod.findClickWorkMembers(
                    mGroup.getGroupId(),
                    mWorkBean.getTaskId(),
                    mWorkBean.getIdInTask(),
                    activity.mApplication.getClickWorkDB(),
                    activity.mApplication.getUserDB());

        }else if (activity.mIsTaskClick){
            mData=GlobleMethod.findClickTaskMembers(
                    mGroup.getGroupId(),
                    mTaskBean.getIdInGroup(),
                    activity.mApplication.getClickTaskDB(),
                    activity.mApplication.getUserDB());
        }else {
            mData= GlobleMethod.findUserFromGroup(mApplication.getJoinGroupDB(), mApplication.getUserDB(),mGroup.getGroupId());
        }
        mAdapter=new MemberAdapter(mApplication, R.layout.member_item_layout,mData);
        mListView.setAdapter(mAdapter);
    }

}
