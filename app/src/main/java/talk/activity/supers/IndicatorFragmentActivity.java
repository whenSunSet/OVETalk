package talk.activity.supers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.heshixiyang.ovetalk.R;

import java.util.ArrayList;
import java.util.List;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.adapter.ViewPagerAdapter;
import talk.fragment.BasicFragment;
import talk.fragment.GroupChatting;
import talk.model.TabInfo;
import talk.util.ClickContarctionImageView;
import talk.util.TitleIndicator;


public abstract class IndicatorFragmentActivity extends FragmentActivity implements OnPageChangeListener {
    protected static final int FRAGMENT_ONE = 0;
    protected static final int FRAGMENT_TWO = 1;
    protected static final int FRAGMENT_THREE = 2;
    //-------------当前fragement是否需要刷新
    public static boolean isFlash=false;
    //--------------当前Activity是否处于显示界面
    public static boolean isForeground=false;
    public TalkApplication mApplication;
    public ViewPagerAdapter myAdapter = null;
    protected int mCurrentTab = 0;
    protected int mLastTab = -1;
    protected BroadcastReceiver mMessageReceiver;
    protected ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    protected ViewPager mPager;
    protected TitleIndicator mIndicator;
    protected RelativeLayout mTitle;

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


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View isEdit = getCurrentFocus();
            View isImage = ((GroupChatting) (mTabs.get(0).fragment)).mMore;

            hideSoftInput(isEdit.getWindowToken(),isShouldHideInput(isEdit,isImage,ev));

        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param isEdit
     * @param event
     * @return
     */
    protected int isShouldHideInput(View isEdit,View isImage, MotionEvent event) {
        if (isEdit != null&&isEdit instanceof EditText){
            int[] e = { 0, 0 };
            int[] i = { 0, 0 };
            isEdit.getLocationInWindow(e);
            isImage.getLocationInWindow(i);
            int eLeft = e[0], eTop = e[1], eBottom = eTop + isEdit.getHeight(), eRight = eLeft + isEdit.getWidth();
            int iLeft = i[0], iTop = i[1], iBottom = iTop + isImage.getHeight(), iRight = iLeft + isImage.getWidth();
            if((event.getX() > eLeft && event.getX() < eRight
                    && event.getY() > eTop && event.getY() < eBottom)||
                    (event.getX() > iLeft && event.getX() < iRight
                            && event.getY() > iTop && event.getY() < iBottom)) {
                return 0;
            }
        }else{

        }
                if {
                    // 点击Edit或者ClickContarctionImageView的事件，忽略它。
                return 0;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    protected void hideSoftInput(IBinder token,int statu) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }else if (view instanceof ClickContarctionImageView){
            ((GroupChatting) (mTabs.get(0).fragment)).mMore.setVisibility();
        }
    }

    protected abstract int supplyTabs(List<TabInfo> tabs);

    protected void flashFragment(){
        ((BasicFragment)mTabs.get(mCurrentTab).fragment).flash();
    }

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
