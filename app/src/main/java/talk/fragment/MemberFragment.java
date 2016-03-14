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
import talk.model.Task;
import talk.model.Work;

/**
 * Created by heshixiyang on 2016/3/3.
 */
public class MemberFragment extends BasicFragment {
    private Group mGroup;
    private ListViewActivity activity;
    private Work mWork;
    private Task mTask;
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
        mTask=activity.mTask;
        mWork=activity.mWork;
        if (activity.isWorkClick){
            mData=GlobleMethod.findClickWorkMembers(
                    mGroup.getGroupName(),
                    mWork.getTaskId(),
                    mWork.getIdInTask(),
                    activity.mApplication.getClickWorkDB(),
                    activity.mApplication.getUserDB());

        }else if (activity.isTaskClick){
            mData=GlobleMethod.findClickTaskMembers(
                    mGroup.getGroupName(),
                    mTask.getIdInGroup(),
                    activity.mApplication.getClickTaskDB(),
                    activity.mApplication.getUserDB());
        }else {
            mData= GlobleMethod.findUserFromGroup(mApplication.getJoinGroupDB(), mApplication.getUserDB(),mGroup.getGroupName());
        }
        mAdapter=new MemberAdapter(mApplication, R.layout.member_item_layout,mData);
        mListView.setAdapter(mAdapter);
    }

}
