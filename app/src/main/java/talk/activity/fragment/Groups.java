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
import talk.adapter.ChatMessageAdapter;
import talk.fragment.GroupsFind;
import talk.fragment.GroupsList;
import talk.model.TabInfo;


public class Groups extends IndicatorFragmentActivity implements ChatMessageAdapter.OnCallBackMore{
    private static final String TAG="Groups";

    private TextView makeGroup;
    private Boolean isResume=false;


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
        makeGroup=new TextView(this);
        makeGroup.setText("创建群组");
        makeGroup.setTextSize(15);
        makeGroup.setId(R.id.titlebar+1);
        makeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Groups.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin=width-200;
        layoutParams.topMargin=20;
        layout.addView(makeGroup, layoutParams);
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

        if (mCurrentTab==0&& Groups.isFlash) {
            flashFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground=true;

        if (isResume&&mCurrentTab==0&&Groups.isFlash) {
            flashFragment();
        }

        isResume=true;
    }


    @Override
    public void callBackMore() {}
}
