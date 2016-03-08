package talk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.heshixiyang.ovetalk.R;

import talk.activity.aboutGroup.TaskActivity;
import talk.activity.fragment.GroupAll;
import talk.adapter.WorkAdapter;
import talk.model.Group;
import talk.model.Task;

/**
 * Created by asus on 2015/11/15.
 */
public class GroupWork extends BasicFragment{
    private Group mGroup;

    public GroupWork() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init(inflater);

        return view;
    }
    protected void init(LayoutInflater inflater) {
        super.init(inflater);
        mGroup=((GroupAll)getActivity()).mGroup;

        mData=mTalkApplication.getWorkDB().getGroupWork(mGroup.getGroupName());
        mAdapter=new WorkAdapter(mTalkApplication, R.layout.work_item,mData);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task=(Task)(mListView.getItemAtPosition(position));
                mTalkApplication.map.put("nowTask", task);
                Intent intent=new Intent(getActivity(),TaskActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void flash() {
        mData=mTalkApplication.getTaskDB().getGroupTask(mGroup.getGroupName());
        super.flash();
    }
}
