package talk.activity.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.heshixiyang.ovetalk.R;

import java.util.List;

import talk.Globle.Test;
import talk.activity.create.CreateGroupActivity;
import talk.activity.supers.IndicatorFragmentActivity;
import talk.fragment.GroupsFind;
import talk.fragment.GroupsList;
import talk.model.TabInfo;


public class Groups extends IndicatorFragmentActivity {
    private TextView mMakeGroup;
    private Boolean mIsResume =false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Test.init(mApplication);
        addView();
        registerMessageReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                flashFragment();
            }
        });
    }

    public void addView() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）

        RelativeLayout layout= (RelativeLayout)findViewById(R.id.titlebar);
        mMakeGroup =new TextView(this);
        mMakeGroup.setText("创建群组");
        mMakeGroup.setTextSize(15);
        mMakeGroup.setId(R.id.titlebar + 1);
        mMakeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Groups.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin=width-200;
        layoutParams.topMargin=20;
        layout.addView(mMakeGroup, layoutParams);
    }



    protected int supplyTabs(List<TabInfo> tabs) {
        tabs.add(new TabInfo(FRAGMENT_ONE, "我的小组",
                GroupsList.class));
        tabs.add(new TabInfo(FRAGMENT_TWO, "查找小组",
                GroupsFind.class));
        return FRAGMENT_ONE;
    }


    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        if (mCurrentTab==0&& Groups.mIsFlash) {
            flashFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsForeground =true;

        if (mIsResume &&mCurrentTab==0&&Groups.mIsFlash) {
            flashFragment();
        }

        mIsResume =true;
    }
}
