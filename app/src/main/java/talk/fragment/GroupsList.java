package talk.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heshixiyang.ovetalk.R;

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

    @Override
    protected void upData() {
        super.upData();
        flash();
    }

    public void flash(){
        makeListView();
        super.flash();
    }

}