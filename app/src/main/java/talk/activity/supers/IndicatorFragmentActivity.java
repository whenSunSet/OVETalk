package talk.activity.supers;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;


import com.example.heshixiyang.ovetalk.R;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.adapter.ViewPagerAdapter;
import talk.model.TabInfo;
import talk.util.TitleIndicator;


public abstract class IndicatorFragmentActivity extends FragmentActivity implements OnPageChangeListener {
    protected static final int FRAGMENT_ONE = 0;
    protected static final int FRAGMENT_TWO = 1;
    protected static final int FRAGMENT_THREE = 2;

    protected int mCurrentTab = 0;
    protected int mLastTab = -1;
    public TalkApplication mApplication;

    protected BroadcastReceiver mMessageReceiver;

    protected ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    public ViewPagerAdapter myAdapter = null;

    protected ViewPager mPager;

    protected TitleIndicator mIndicator;

    protected RelativeLayout mTitle;

    protected PtrClassicFrameLayout mPtrFrame;


    //-------------当前fragement是否需要刷新
    public static boolean isFlash=false;

    //--------------当前Activity是否处于显示界面
    public static boolean isForeground=false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.titled_fragment_tab_activity);

        initViews();
    }
    public void initViews() {
        mTitle=(RelativeLayout)findViewById(R.id.titlebar);
        mApplication=(TalkApplication)getApplication();
        mCurrentTab = supplyTabs(mTabs);

        myAdapter = new ViewPagerAdapter(this, getSupportFragmentManager(), mTabs);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(myAdapter);
        mPager.setOnPageChangeListener(this);
        mPager.setOffscreenPageLimit(mTabs.size());

        mIndicator = (TitleIndicator) findViewById(R.id.pagerindicator);
        mIndicator.init(mCurrentTab, mTabs, mPager);

        mPager.setCurrentItem(mCurrentTab);
        mLastTab = mCurrentTab;

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.view_pager_ptr_frame);
        mPtrFrame.disableWhenHorizontalMove(true);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                upData();
            }
        });
    }

    protected void upData(){
        mPtrFrame.refreshComplete();
    }

    public void registerMessageReceiver(BroadcastReceiver broadcastReceiver) {
        mMessageReceiver = broadcastReceiver;
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobleData.MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    //监听viewpage滚动的方法--------------------------
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mIndicator.onScrolled((mPager.getWidth() + mPager.getPageMargin()) * position + positionOffsetPixels);
    }
    @Override
    public void onPageSelected(int position) {
        mIndicator.onSwitched(position);
        mCurrentTab = position;
    }
    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mLastTab = mCurrentTab;
        }
    }
    //---------------------------------------------------

    protected abstract int supplyTabs(List<TabInfo> tabs);

    @Override
    protected void onStart() {
        super.onStart();
        isForeground=false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isForeground=false;
    }

    @Override
    protected void onDestroy() {

        mTabs.clear();
        mTabs = null;
        myAdapter.notifyDataSetChanged();
        myAdapter = null;
        mPager.setAdapter(null);
        mPager = null;
        mIndicator = null;
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
