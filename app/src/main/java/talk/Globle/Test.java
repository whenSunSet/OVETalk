package talk.Globle;

import talk.TalkApplication;
import talk.model.Group;
import talk.model.User;
import talk.util.MyPreferenceManager;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class Test {

    public static void init(TalkApplication talkApplication){
        MyPreferenceManager myPreferenceManager= talkApplication.getSpUtil();

//        if (!myPreferenceManager.getIsCreatSystemGroup()){
//            myPreferenceManager.setUserId("13588299364");
//            myPreferenceManager.setUserNickName("薛克林顿");
//            createSystemGroup(talkApplication);
//
//            myPreferenceManager.setIsCreatSystemGroup(true);
//            talkApplication.getUserDB().add(new User("13588299364", "薛克林顿", "http"));
//            GlobleMethod.setAlias(talkApplication,"13588299364");
//        }

        if (!myPreferenceManager.getIsCreatSystemGroup()){
            myPreferenceManager.setUserId("13588197967");
            myPreferenceManager.setUserNickName("徐佳慧");
            createSystemGroup(talkApplication);

            myPreferenceManager.setIsCreatSystemGroup(true);
            talkApplication.getUserDB().add(new User("13588197967", "徐佳慧", "http"));
            GlobleMethod.setAlias(talkApplication,"13588197967");
        }
    }
    public static void createSystemGroup(TalkApplication talkApplication) {
        MyPreferenceManager myPreferenceManager= talkApplication.getSpUtil();
        Group group = new Group(GlobleData.SYSTEM, "SystemMessage", " ", myPreferenceManager.getUserId(),0,0);
        talkApplication.getGroupDB().addGroup(group);

    }
}
