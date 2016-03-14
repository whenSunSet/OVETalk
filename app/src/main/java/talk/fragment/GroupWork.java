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
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

import talk.Globle.GlobleData;
import talk.activity.aboutGroup.TaskAndWorkActivity;
import talk.activity.fragment.GroupAll;
import talk.adapter.WorkAdapter;
import talk.model.Group;
import talk.model.Task;
import talk.util.MyHandler;
import talk.util.MyRunnable;

/**
 * Created by asus on 2015/11/15.
 */
public class GroupWork extends BasicFragment{
    private Group mGroup;
    private List<NameValuePair> formparams;
    public GroupWork() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init(inflater);

        return view;
    }
    protected void init(LayoutInflater inflater) {
        super.init(inflater);
        mGroup=((GroupAll)getActivity()).mGroup;

        mData= mApplication.getWorkDB().getGroupWork(mGroup.getGroupName());
        mAdapter=new WorkAdapter(mApplication, R.layout.work_item,mData);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task) (mListView.getItemAtPosition(position));
                mApplication.map.put("nowTask", task);
                Intent intent = new Intent(getActivity(), TaskAndWorkActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    protected void upData() {
        super.upData();
        formparams.add(new BasicNameValuePair("groupName", mGroup.getGroupName()));
        new Thread(new MyRunnable(formparams,"",handler, GlobleData.DEFAULT));
    }

    @Override
    public void flash() {
        mData= mApplication.getTaskDB().getGroupTask(mGroup.getGroupName());
        super.flash();
    }
    MyHandler handler=new MyHandler(getActivity()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
