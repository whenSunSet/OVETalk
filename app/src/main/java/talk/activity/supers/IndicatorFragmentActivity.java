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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.heshixiyang.ovetalk.R;

import java.util.ArrayList;
import java.util.List;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.activity.fragment.GroupAll;
import talk.activity.fragment.Groups;
import talk.adapter.ViewPagerAdapter;
import talk.fragment.BasicFragment;
import talk.fragment.GroupChatting;
import talk.model.TabInfo;
import talk.util.TitleIndicator;


public abstract class IndicatorFragmentActivity extends FragmentActivity implements OnPageChangeListener {
    protected static final int FRAGMENT_ONE = 0;
    protected static final int FRAGMENT_TWO = 1;
    protected static final int FRAGMENT_THREE = 2;
    //-------------当前fragement是否需要刷新
    public static boolean mIsFlash =false;
    //--------------当前Activity是否处于显示界面
    public static boolean mIsForeground =false;
    public TalkApplication mApplication;
    public ViewPagerAdapter myAdapter = null;
    protected int mCurrentTab = 0;
    protected int mLastTab = -1;
    protected BroadcastReceiver mMessageReceiver;
    protected ArrayList<TabInfo> mTabs = new ArrayList<>();
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View isEdit = getCurrentFocus();
        ImageView isImage;
        Button button;

        if (isEdit==null){
            return super.dispatchTouchEvent(event);
        }

        if (this instanceof Groups){
            if (!isInView(getViewLocation(isEdit),event)) {
                hideSoftInput(isEdit.getWindowToken(), 3);
            }
            return super.dispatchTouchEvent(event);
        }


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            button=((GroupChatting) (mTabs.get(0).fragment)).getmMsgSend();
            if (button==null){
                return super.dispatchTouchEvent(event);
            }
            hideSoftInput(isEdit.getWindowToken(),isShouldHideInput(isEdit,null,button,event));
        }else if (event.getAction()==MotionEvent.ACTION_UP){
            isImage= ((GroupChatting) (mTabs.get(0).fragment)).getmMore();
            if (isImage==null){
                return super.dispatchTouchEvent(event);
            }
            hideSoftInput(isEdit.getWindowToken(),isShouldHideInput(isEdit,isImage,null,event));
        }
        return super.dispatchTouchEvent(event);
    }
    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     */
    protected int isShouldHideInput(View isEdit,ImageView isImage,Button button,MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            int[] e=getViewLocation(isEdit);
            int[] b=getViewLocation(button);
            if (!(isInView(e,event)||isInView(b,event))){
                return GlobleData.HIDE_SOFT_INPUT;
            }
        }else if (event.getAction()==MotionEvent.ACTION_UP){
            int[] i=getViewLocation(isImage);
            if(!isInView(i,event)){
                return GlobleData.HIDE_MORE;
            }
        }
        return GlobleData.DEFAULT;
    }

    protected int[] getViewLocation(View view){
        //上下左右
        int[] location={0,0,0,0};
        int[] a={0,0};

        if (view!=null){
            view.getLocationInWindow(a);
            location[0]=a[1];
            location[1]=location[0]+view.getHeight();
            location[2]=a[0];
            location[3]=location[2]+view.getWidth();
            return location;
        }else {
            return null;
        }
    }

    protected boolean isInView(int[] i,MotionEvent event){
        boolean isInView=false;
        if (i[0]<event.getY()&&i[1]>event.getY()
                &&i[2]<event.getX()&&i[3]>event.getX()){
            isInView=true;
        }
        return isInView;
    }
    /**
     * 多种隐藏软件盘方法的其中一种
     *
     */
    protected void hideSoftInput(IBinder token,int statu) {
        switch (statu){
            case GlobleData.HIDE_MORE:
                ((GroupChatting) (mTabs.get(0).fragment)).mContainer.setVisibility(View.GONE);
                break;
            case GlobleData.HIDE_SOFT_INPUT:
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(token,InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            default:
                break;
        }
    }

    protected abstract int supplyTabs(List<TabInfo> tabs);

    protected void flashFragment(){
        if (mCurrentTab==0&&(this instanceof GroupAll)){
            ((GroupChatting)mTabs.get(mCurrentTab).fragment).flash(GlobleData.SELECT_LAST, null);
        }else {
            ((BasicFragment)mTabs.get(mCurrentTab).fragment).flash(GlobleData.SELECT_FRIST);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsForeground =false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsForeground =true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsForeground =false;
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
