package talk.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.heshixiyang.ovetalk.R;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import talk.TalkApplication;
import talk.activity.fragment.GroupAll;
import talk.activity.fragment.Groups;
import talk.adapter.ViewPagerAdapter;
import talk.model.GroupChatMessage;

/**
 * Created by heshixiyang on 2016/2/3.
 */
public  class BasicFragment extends Fragment {
    protected TalkApplication mApplication;
    protected ViewGroup view;
    protected ArrayList mData;
    protected BaseAdapter mAdapter;
    protected ListView mListView;
    protected PtrClassicFrameLayout mPtrFrame;
    protected ViewPagerAdapter myAdapter;
    protected boolean isChattingFragment=false;

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    protected void init(LayoutInflater inflater){
        if (isChattingFragment){
            view= (ViewGroup) inflater.inflate(R.layout.main_chatting,null);
        }else {
            view= (ViewGroup) inflater.inflate(R.layout.basic_fragment_layout,null);
        }
        mApplication =(TalkApplication)(getActivity().getApplication());
        mListView=(ListView)view.findViewById(R.id.listView);
        if (getActivity() instanceof Groups){
            myAdapter=((Groups)getActivity()).myAdapter;
        }else if (getActivity() instanceof GroupAll){
            myAdapter=((GroupAll)getActivity()).myAdapter;
        }

        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.view_pager_ptr_frame);
        mPtrFrame.disableWhenHorizontalMove(true);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                upData();
            }
        });
        return ;
    }

    protected void upData(){
        mPtrFrame.refreshComplete();
    }

    public void flash(){
        mListView.setSelection(0);
        Groups.isFlash=false;
    }
    public void flash(GroupChatMessage chatMessage){}
}
