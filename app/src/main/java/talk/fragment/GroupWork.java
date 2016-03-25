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
import talk.adapter.WorkAdapter;
import talk.model.Group;
import talk.model.Work;
import talk.util.MyHandler;

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
    protected void init(final LayoutInflater inflater) {
        super.init(inflater);
        mGroup=((GroupAll)getActivity()).mGroup;
        makeListView();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Work work = (Work) (mListView.getItemAtPosition(position));
                mApplication.map.put("nowWork", work);
                Intent intent = new Intent(getActivity(), TaskAndWorkActivity.class);
                intent.putExtra("which", GlobleData.IS_WORK);
                getActivity().startActivity(intent);
            }
        });
    }
    protected void makeListView(){
        mData= mApplication.getWorkDB().getGroupWork(mGroup.getGroupName());
        mAdapter=new WorkAdapter(mApplication, R.layout.work_item,mData);
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
