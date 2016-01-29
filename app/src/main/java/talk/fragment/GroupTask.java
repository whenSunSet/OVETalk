package talk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.heshixiyang.ovetalk.R;

import java.util.List;

import talk.TalkApplication;
import talk.activity.aboutGroup.TaskActivity;
import talk.activity.fragment.GroupAll;
import talk.adapter.TaskAdapter;
import talk.model.Group;
import talk.model.Task;

/**
 * Created by asus on 2015/11/15.
 */
public class GroupTask extends Fragment {
    private View view;
    private TalkApplication mApplication;
    private TaskAdapter mTaskAdapter;
    private ListView mListView;
    private List<Task> mTask;
    private GroupAll mGroupActivity;
    private Group mGroup;

    public GroupTask() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init(inflater,container);

        return view;
    }
    private View init(final LayoutInflater inflater,ViewGroup container) {
        view = inflater.inflate(R.layout.task, container, false);
        mGroupActivity=(GroupAll)getActivity();
        mApplication=mGroupActivity.mApplication;
        mGroup=mGroupActivity.mGroup;
        mListView=(ListView)view.findViewById(R.id.listView);
        mTask=mApplication.getTaskDB().getGroupTask(mGroup.getGroupName());
        mTaskAdapter=new TaskAdapter(mApplication,R.layout.task_item,mTask);
        mListView.setAdapter(mTaskAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task=(Task)(mListView.getItemAtPosition(position));
                mApplication.map.put("nowTask", task);
                Intent intent=new Intent(getActivity(),TaskActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }
}
