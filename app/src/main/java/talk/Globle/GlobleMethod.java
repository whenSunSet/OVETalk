package talk.Globle;


import org.apache.commons.httpclient.NameValuePair;

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
        ArrayList<User> list=new ArrayList<User>();
        ArrayList<String > nameList=new ArrayList<String >();
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


    public static String GetResult(String url, List<NameValuePair> formparams) {

      return null;
    }

}
