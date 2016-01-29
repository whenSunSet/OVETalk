package talk.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.heshixiyang.ovetalk.R;

import java.util.List;

import talk.TalkApplication;
import talk.activity.fragment.Groups;
import talk.adapter.GroupListAdapter;
import talk.adapter.ViewPagerAdapter;
import talk.model.Group;


public class GroupsList extends Fragment {
    public static final String TAG="GroupsList";

    private TalkApplication mApplication;
    private View view;

    private ListView mGroupList;
    private GroupListAdapter mAdapter;
    private List<Group> mData;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        init(inflater);
        return view;
    }

    public void init(LayoutInflater inflater) {
        mApplication=((Groups)getActivity()).mApplication;
        view = inflater.inflate(R.layout.activity_group_list2,null);
        mGroupList = (ListView)view.findViewById(R.id.listView);

        mData = mApplication.getGroupDB().getGroups();
        mAdapter = new GroupListAdapter(mApplication, R.layout.group_list_adapter, mData);
        mGroupList.setAdapter(mAdapter);

    }

    public void flashFragment(ViewPagerAdapter myAdapter){
        mData = mApplication.getGroupDB().getGroups();
        myAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();
        mGroupList.setSelection(0);

        Groups.isFlash=false;
    }


}