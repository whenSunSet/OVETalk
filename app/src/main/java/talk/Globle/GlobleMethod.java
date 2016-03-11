package talk.Globle;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import talk.TalkApplication;
import talk.datebase.ClickTaskDB;
import talk.datebase.ClickWorkDB;
import talk.datebase.JoinGroupDB;
import talk.datebase.UserDB;
import talk.model.JoinGroup;
import talk.model.Message;
import talk.model.User;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class GlobleMethod {

    public static final void addUserToGroup(TalkApplication talkApplication,Message message){
        UserDB userDB=talkApplication.getUserDB();
        JoinGroupDB joinGroupDB=talkApplication.getJoinGroupDB();
        JoinGroup joinGroup=new JoinGroup();
        User user=new User();

        joinGroup.setGroupName(message.getGroupName());
        joinGroup.setMemberId(message.getUserName());
        joinGroup.setDate(message.getDate());

        user.setUserID(message.getUserName());
        user.setUserIcon(message.getUserIcon());
        user.setUserNickName(message.getUserNickName());

        userDB.add(user);
        joinGroupDB.add(joinGroup);

    }

    public static final ArrayList<User> findUserFromGroup(JoinGroupDB joinGroupDB,UserDB userDB,String groupName){
        ArrayList<User> list=new ArrayList<>();
        ArrayList<String> nameList;
        nameList=joinGroupDB.getMembersName(groupName);
        for (String name:nameList) {
            list.add(userDB.getMember(name));
        }
        return list;
    }

    public static final void quitFromGroup(TalkApplication talkApplication,Message message){
        JoinGroupDB joinGroupDB=talkApplication.getJoinGroupDB();
        ClickTaskDB clickTaskDB=talkApplication.getClickTaskDB();
        ClickWorkDB clickWorkDB=talkApplication.getClickWorkDB();

        joinGroupDB.deleteMember(message.getGroupName(), message.getUserName());
        clickTaskDB.deleteClick(message.getGroupName(), message.getUserName());
        clickWorkDB.deleteClick(message.getGroupName(), message.getUserName());

    }


    public static boolean setTag(TalkApplication application){
        List<String > groupNames=application.getGroupDB().getGroupNames();
        Set<String> set= new HashSet<String>();
        final int[] j = new int[1];
        set.addAll(groupNames);
        set= JPushInterface.filterValidTags(set);

        //这个每次改变都会覆盖前面的
        JPushInterface.setTags(application, set, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

                j[0] = i;
            }
        });

        if (j[0]==0){
            return true;
        }else {
            return false;
        }
    }

    public static ArrayList<User> findClickWorkMembers(String groupName,
                                                         int taskId,
                                                         int workId,
                                                         ClickWorkDB clickWorkDB,
                                                         UserDB userDB){
        ArrayList<User> list=new ArrayList<>();
        for (String userId:clickWorkDB.getClickMemberName(groupName,taskId,workId)){
            list.add(userDB.getMember(userId));
        }
        return list;
    }

    public static ArrayList<User> findClickTaskMembers(String groupName,
                                                       int taskId,
                                                       ClickTaskDB clickTaskDB,
                                                       UserDB userDB){
        ArrayList<User> list=new ArrayList<>();
        for (String userId:clickTaskDB.getClickMemberName(groupName, taskId)){
            list.add(userDB.getMember(userId));
        }
        return list;
    }
    public static String GetResult(String url, List<org.apache.http.NameValuePair> formparams) {

        HttpResponse response2;// 创建一个可关闭的response对象
        HttpClient client = new DefaultHttpClient();// 创建一个http客户端，用于发送http请求
        HttpPost post = new HttpPost(url);// 创建一个post请求对象
        String result = "";
        try {
            // 将参数放入post
            post.setEntity(new UrlEncodedFormEntity(formparams,"utf-8"));
            response2 = client.execute(post);// 执行请求

            if (response2.getStatusLine().getStatusCode() != 200) {
                result = Const.methodString;
                return result;
            }
            HttpEntity entity2 = response2.getEntity();
            result = EntityUtils.toString(entity2);// 打印出entity2的内容
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
