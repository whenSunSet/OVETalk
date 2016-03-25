package talk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.heshixiyang.ovetalk.R;

import org.apache.http.NameValuePair;

import java.util.List;

import talk.Globle.GlobleData;
import talk.activity.aboutGroup.TaskAndWorkActivity;
import talk.activity.fragment.GroupAll;
import talk.activity.fragment.Groups;
import talk.activity.util.ListViewActivity;
import talk.adapter.TaskAdapter;
import talk.model.Group;
import talk.model.Task;
import talk.util.MyHandler;

/**
 * Created by asus on 2015/11/15.
 */
public class GroupTask extends BasicFragment{
    private Group mGroup;
    private int mWhichActivity;
    private List<NameValuePair>  formparams;
    public GroupTask() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() instanceof GroupAll){
            mWhichActivity =1;
            mGroup=((GroupAll)getActivity()).mGroup;
        }else if (getActivity() instanceof ListViewActivity){
            mWhichActivity =2;
            mGroup=((ListViewActivity)getActivity()).mGroup;
        }
        init(inflater);
        return view;
    }
    protected void init(final LayoutInflater inflater) {
        super.init(inflater);
        makeListView();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task) (mListView.getItemAtPosition(position));
                if (mWhichActivity == 1) {
                    mApplication.map.put("nowTask", task);
                    Intent intent = new Intent(getActivity(), TaskAndWorkActivity.class);
                    intent.putExtra("which", GlobleData.IS_TASK);
                    getActivity().startActivity(intent);
                } else if (mWhichActivity == 2) {
                    ((ListViewActivity) getActivity()).finish(task.getGroupName(), task.getIdInGroup());
                }
            }
        });
    }

    protected void makeListView(){
        mData= mApplication.getTaskDB().getGroupTask(mGroup.getGroupName());
        mAdapter=new TaskAdapter(mApplication,R.layout.task_item,mData);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void flash(int which) {
        makeListView();
        super.flash(which);
        Groups.mIsFlash =true;
    }
    @Override

    protected void upData() {
        super.upData();

//        formparams.add(new BasicNameValuePair("groupName", mGroup.getGroupName()));
//        new Thread(new MyRunnable(formparams,"",handler, GlobleData.DEFAULT));
    }

    MyHandler handler=new MyHandler(getActivity()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
