package talk.Globle;

import talk.TalkApplication;
import talk.model.Group;
import talk.util.MyPreferenceManager;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class Test {

    public static void init(TalkApplication talkApplication){
        MyPreferenceManager myPreferenceManager= talkApplication.getSpUtil();

        if (!myPreferenceManager.getIsCreatSystemGroup()){
            myPreferenceManager.setUserName("13588197967");
            myPreferenceManager.setUserNickName("薛克林顿");
            createSystemGroup(talkApplication);

            myPreferenceManager.setIsCreatSystemGroup(true);
        }

    }


    public static void createSystemGroup(TalkApplication talkApplication) {
        MyPreferenceManager myPreferenceManager= talkApplication.getSpUtil();
        Group group = new Group(myPreferenceManager.getUserName(), "SystemMessage", " ", myPreferenceManager.getUserName(),0,0);
        talkApplication.getGroupDB().addGroup(group);

    }

}
