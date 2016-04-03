package talk.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heshixiyang.ovetalk.R;

import talk.Globle.GlobleData;
import talk.activity.fragment.Groups;
import talk.adapter.GroupListAdapter;

public class GroupsList extends BasicFragment{
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        init(inflater);
        return view;
    }

    public void init(LayoutInflater inflater) {
        super.init(inflater);
        makeListView();
    }

    private void makeListView(){
        mData = mApplication.getGroupDB().getGroups();
        mAdapter = new GroupListAdapter(mApplication, R.layout.group_list_adapter, mData);
        mListView.setAdapter(mAdapter);
    }

    public void flash(int which){
        makeListView();
        super.flash(which);
        Groups.mIsFlash =false;
    }

    @Override
    protected void upData() {
        super.upData();
        flash(GlobleData.SELECT_FRIST);
    }
}