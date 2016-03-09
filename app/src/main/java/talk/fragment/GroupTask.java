package talk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.heshixiyang.ovetalk.R;

import talk.activity.aboutGroup.TaskAndWorkActivity;
import talk.activity.fragment.GroupAll;
import talk.activity.util.ListViewActivity;
import talk.adapter.TaskAdapter;
import talk.model.Group;
import talk.model.Task;

/**
 * Created by asus on 2015/11/15.
 */
public class GroupTask extends BasicFragment{
    private Group mGroup;
    private int whichActivity;
    public GroupTask() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() instanceof GroupAll){
            whichActivity=1;
            mGroup=((GroupAll)getActivity()).mGroup;
        }else if (getActivity() instanceof ListViewActivity){
            whichActivity=2;
            mGroup=((ListViewActivity)getActivity()).mGroup;
        }
        init(inflater);
        return view;
    }
    protected void init(LayoutInflater inflater) {
        super.init(inflater);
        mData=mTalkApplication.getTaskDB().getGroupTask(mGroup.getGroupName());
        mAdapter=new TaskAdapter(mTalkApplication,R.layout.task_item,mData);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task) (mListView.getItemAtPosition(position));
                if (whichActivity==1){
                    mTalkApplication.map.put("nowTask", task);
                    Intent intent = new Intent(getActivity(), TaskAndWorkActivity.class);
                    getActivity().startActivity(intent);
                }else if (whichActivity==2){
                    ((ListViewActivity)getActivity()).finish(task.getGroupName(),task.getIdInGroup());
                }
            }
        });
    }

    @Override
    public void flash() {
        mData=mTalkApplication.getTaskDB().getGroupTask(mGroup.getGroupName());
        super.flash();
    }
}
