package talk;

import android.app.Application;
import android.app.NotificationManager;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import talk.datebase.ClickTaskDB;
import talk.datebase.ClickWorkDB;
import talk.datebase.GroupDB;
import talk.datebase.GroupMessageDB;
import talk.datebase.JoinGroupDB;
import talk.datebase.TaskDB;
import talk.datebase.UserDB;
import talk.datebase.WorkDB;
import talk.model.User;
import talk.util.MyPreferenceManager;

public class TalkApplication extends Application {
    private static TalkApplication mApplication;
    public Map map=new HashMap();
    private MyPreferenceManager mSpUtil;
    private NotificationManager mNotificationManager;
    private GroupMessageDB groupMessageDB;
    private GroupDB groupDB;
    private UserDB userDB;
    private TaskDB taskDB;
    private WorkDB workDB;
    private JoinGroupDB joinGroupDB;
    private ClickTaskDB clickTaskDB;
    private ClickWorkDB clickWorkDB;

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush

        mApplication=this;
        initData();

    }

    private void initData() {
        mSpUtil = new MyPreferenceManager();
        MyPreferenceManager.init(this);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        groupMessageDB = new GroupMessageDB(this);
        groupDB = new GroupDB(this);
        userDB =new UserDB(this);
        taskDB=new TaskDB(this);
        workDB=new WorkDB(this);

        joinGroupDB=new JoinGroupDB(this);
        clickTaskDB=new ClickTaskDB(this);
        clickWorkDB=new ClickWorkDB(this);


        map.put("my", new User(getSpUtil().getUserName(),getSpUtil().getUsreNickName(),getSpUtil().getUserIcon()));

    }



    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    public synchronized MyPreferenceManager getSpUtil() {
        if (mSpUtil == null)
            mSpUtil = new MyPreferenceManager();
        return mSpUtil;
    }

    public GroupMessageDB getGroupMessageDB() {
        return groupMessageDB;
    }

    public GroupDB getGroupDB() {
        return groupDB;
    }

    public UserDB getUserDB() {
        return userDB;
    }

    public TaskDB getTaskDB() {
        return taskDB;
    }

    public WorkDB getWorkDB() {
        return workDB;
    }

    public JoinGroupDB getJoinGroupDB() {
        return joinGroupDB;
    }

    public ClickTaskDB getClickTaskDB() {
        return clickTaskDB;
    }

    public ClickWorkDB getClickWorkDB() {
        return clickWorkDB;
    }
}
