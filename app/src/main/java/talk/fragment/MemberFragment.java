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

/**
 * Created by heshixiyang on 2016/3/3.
 */
public class MemberFragment extends BasicFragment {
    private Group mGroup;
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
        mGroup=((ListViewActivity)getActivity()).mGroup;
        mData= GlobleMethod.findUserFromGroup(mTalkApplication.getJoinGroupDB(),mTalkApplication.getUserDB(),mGroup.getGroupName());
        mAdapter=new MemberAdapter(mTalkApplication, R.layout.member_item_layout,mData);
        mListView.setAdapter(mAdapter);
    }

}
