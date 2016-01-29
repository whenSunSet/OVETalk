package talk.activity.aboutGroup;

import android.app.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.heshixiyang.ovetalk.R;

import java.util.ArrayList;
import talk.TalkApplication;
import talk.adapter.MemberAdapter;
import talk.datebase.UserDB;
import talk.model.Group;
import talk.model.User;
import talk.util.SharedPreferencesUtils;


public class MemberActivity extends Activity {
    private TalkApplication mApplication;
    private Group mGroup;
    private ListView mListView;
    private ArrayList<Integer> mMembersNum;
    private ArrayList mMembers;
    private ListAdapter mAdapter;
    private SharedPreferencesUtils mSharedPreferencesUtils;
    private UserDB userDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        init();
    }

    public void init(){
        mApplication=(TalkApplication)getApplication();
        mGroup=mApplication.getGroupDB().getGroup(getIntent().getStringExtra("groupName"));
        userDB =mApplication.getUserDB();
        mSharedPreferencesUtils=new SharedPreferencesUtils(mApplication,getIntent().getStringExtra("groupName"));
        mListView=(ListView)findViewById(R.id.member);
        mMembers=new ArrayList<User>();
        mMembersNum=(ArrayList<Integer>)(mSharedPreferencesUtils.getObject(SharedPreferencesUtils.MEMBER,ArrayList.class));

        for (Integer integer:mMembersNum){
            if (integer.intValue()==-1){
                mMembers.add((User)(mApplication.map.get("my")));
            }else {
//                mMembers.add(userDB.getMember());
            }
        }
        mAdapter=new MemberAdapter(mApplication,R.layout.member_item_layout,mMembers);
        mListView.setAdapter( mAdapter);
    }

}
