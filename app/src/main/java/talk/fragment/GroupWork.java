package talk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.example.heshixiyang.ovetalk.R;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;

import talk.Globle.GlobleData;
import talk.activity.aboutGroup.TaskAndWorkActivity;
import talk.activity.fragment.GroupAll;
import talk.activity.fragment.Groups;
import talk.adapter.WorkAdapter;
import talk.model.ClickTask;
import talk.model.Group;
import talk.model.Work;
import talk.http.SendMessage;

/**
 * Created by asus on 2015/11/15.
 */
public class GroupWork extends BasicFragment implements SendMessage.SendMessageListener{
    private Group mGroup;
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
        mData= mApplication.getWorkDB().getGroupWork(mGroup.getGroupId());
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
        RequestParams requestParams=new RequestParams();
        requestParams.put(GlobleData.GROUP_ID,String.valueOf(mGroup.getGroupIcon()));
        SendMessage.sendMessage.post(mApplication, GlobleData.GET_TASK_CLICK, GlobleData.updateAllTaskClick, requestParams,this);
    }

    @Override
    public void success(HashMap<String, Object> result) {
        if (result==null){
            return;
        }
        mApplication.getClickTaskDB().adds((ArrayList<ClickTask>) result.get("clickTasks"));
        flash(GlobleData.SELECT_FRIST);
    }
}
