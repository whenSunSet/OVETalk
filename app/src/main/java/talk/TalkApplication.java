package talk;
import android.app.Application;
import android.app.NotificationManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import cn.jpush.android.api.JPushInterface;
import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
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
    private RequestQueue requestQueue;
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush

        initData();
    }

    private void initData() {
        mSpUtil = new MyPreferenceManager();
        MyPreferenceManager.init(this);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        requestQueue= Volley.newRequestQueue(this);

        groupMessageDB = new GroupMessageDB(this);
        groupDB = new GroupDB(this);
        userDB =new UserDB(this);
        taskDB=new TaskDB(this);
        workDB=new WorkDB(this);

        joinGroupDB=new JoinGroupDB(this);
        clickTaskDB=new ClickTaskDB(this);
        clickWorkDB=new ClickWorkDB(this);


        map.put("my", new User(getSpUtil().getUserName(),getSpUtil().getUsreNickName(),getSpUtil().getUserIcon()));

        GlobleData.SD_CACHE= GlobleMethod.getCacheDir(this);
        GlobleData.SD_FILE= GlobleMethod.getFileDir(this);
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

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
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
