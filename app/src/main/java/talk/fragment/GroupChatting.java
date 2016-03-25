package talk.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.example.heshixiyang.ovetalk.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import talk.Globle.GlobleData;
import talk.activity.aboutGroup.TaskAndWorkActivity;
import talk.activity.create.MakeHomeWorkActivity;
import talk.activity.fragment.GroupAll;
import talk.activity.fragment.Groups;
import talk.adapter.ChatMessageAdapter;
import talk.datebase.GroupMessageDB;
import talk.model.Group;
import talk.model.GroupChatMessage;
import talk.model.Task;
import talk.model.Work;
import talk.util.DialogUtil;
import talk.util.MyHandler;
import talk.util.MyJsonObjectRequest;
import talk.util.MyPreferenceManager;
import talk.util.MyResponseErrorListenerAndListener;
import talk.util.MyRunnable;

public class GroupChatting extends BasicFragment implements ChatMessageAdapter.AdapterClickLisener{
    private ImageView mMore;
    private Button mMsgSend;
    private EditText mMsgInput;

    private ImageView mHomeWork;
    private ImageView mPicture;
    private ImageView mEmoji;
    public LinearLayout mContainer;

    private String mGroupName;
    private GroupAll mActivity;

    //发送消息的数据
    private List<NameValuePair> formparams;
    private MyPreferenceManager mPreferenceManager;
    private GroupMessageDB mGroupMessageDB;

    private Group mGroup;
    private Work mWork;
    private Task mTask;
    //---------------------mContan是否可见
    private Boolean isVisble=false;

    //---------------------每次加载消息10个为阶梯
    private int mMessageNum=10;
    private int mMessageMax;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mIsChattingFragment =true;
        init(inflater);
        initEvent();
        return view;
    }
    @Override
    protected void init(final LayoutInflater inflater) {
        super.init(inflater);
        mActivity=(GroupAll)getActivity();
        mPreferenceManager = mApplication.getSpUtil();
        mGroupMessageDB=mActivity.mGroupMessageDB;

        mGroup = mActivity.mGroup;
        mGroupName= mGroup.getGroupName();

        if (mActivity.mIsSystemGroup){
            //如果是SystemGroup的话就把输入框去掉
            mGroupName= mApplication.getSpUtil().getUserName();
            view.findViewById(R.id.contant).setVisibility(View.GONE);
        }else {
            mMsgSend = (Button) view.findViewById(R.id.id_chat_send);
            mMsgInput = (EditText) view.findViewById(R.id.id_chat_msg);
            mMore=(ImageView)view.findViewById(R.id.more);

            mHomeWork=(ImageView) view.findViewById(R.id.btn_homework);
            mPicture=(ImageView) view.findViewById(R.id.btn_picture);
            mEmoji=(ImageView) view.findViewById(R.id.btn_emoji);

            mContainer=(LinearLayout)view.findViewById(R.id.ll_btn_container);
            formparams = new ArrayList<>();
        }

        //将本group的所有消息设置为已读
        mGroupMessageDB.updateReaded(mGroupName);
        mMessageMax=mGroupMessageDB.getMessageNum(mGroup.getGroupName());

        // 获取10条聊天记录
        mData = mGroupMessageDB.find(mGroup.getGroupName(), 1, mMessageNum);
        mAdapter = new ChatMessageAdapter(mApplication, mData,this);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mData.size() - 1);
        mListView.setClickable(true);
        mListView.getSelectedItem();

    }

    private void initEvent(){
        if (!mActivity.mIsSystemGroup){
            initPrintEvent();
            initMoreEvent();
        }
    }

    private void initMoreEvent(){

        mEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mHomeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MakeHomeWorkActivity.class);
                intent.putExtra("groupName", mGroup.getGroupName());
                startActivityForResult(intent, GlobleData.START_MAKE_HOMEWORK_ACTIVITY);
            }
        });
    }

    private void initPrintEvent() {
        //通过点击mMore这个按钮让mContan显示或者消失
        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMsgInput.getWindowToken()!=null){
                    InputMethodManager im = (InputMethodManager) mApplication.getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(mMsgInput.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }

                if (!isVisble) {
                    mContainer.setVisibility(View.VISIBLE);
                    isVisble = true;
                } else {
                    mContainer.setVisibility(View.GONE);
                    isVisble = false;
                }

            }
        });

        mMsgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMsgInput.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    DialogUtil.showToast(mApplication, "你还没输入文字呢");
                    return;
                }
                //添加消息到数据库里并开启线程发送数据

                addMessage(msg, "0", 1, null,null);
                mMsgInput.setText("");
            }
        });
        //当输入框中有文字的时候，让send显示，否则则让mMore显示
        mMsgInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mMore.setVisibility(View.GONE);
                    mMsgSend.setVisibility(View.VISIBLE);
                    mContainer.setVisibility(View.GONE);
                } else {
                    mMore.setVisibility(View.VISIBLE);
                    mMsgSend.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mMsgInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContainer.setVisibility(View.GONE);
            }
        });
    }

    public void addMessage(String message,String messageImage,int statu,Work work,Task task) {
        GroupChatMessage chatMessage =makeChatMessage(message,messageImage,statu,task,work);
        if (chatMessage.getMessageStatu()==GlobleData.COMMOM_MESSAGE&&chatMessage.getMessageStatu()==GlobleData.EMOJI_MESSAGE){
            sendMessage("",chatMessage.getMessage(), null, chatMessage.getMessageStatu(), null, null);
        }else if (chatMessage.getMessageStatu()==GlobleData.PHOTO_MESSAGE){
            sendMessage("",null, chatMessage.getMessageImage(), chatMessage.getMessageStatu(), null, null);
        }else if (chatMessage.getMessageStatu()==GlobleData.USER_PUT_HOMEWORK){
            sendMessage("",chatMessage.getMessage(), chatMessage.getMessageImage(), chatMessage.getMessageStatu(), work, null);
        }else if (chatMessage.getMessageStatu()==GlobleData.MASTER_PUT_TASK){
            sendMessage("",chatMessage.getMessage(), null, statu, null, task);
        }
        flash(GlobleData.SELECT_LAST,chatMessage);
        return;
    }

    //组装并储存chatMessage
    private GroupChatMessage makeChatMessage(String message,String messageImage,int statu,Task task,Work work){
        GroupChatMessage chatMessage = new GroupChatMessage();
        chatMessage.setIsComing(false);
        chatMessage.setDate(new Date());
        chatMessage.setMessage(message);
        chatMessage.setReaded(true);
        chatMessage.setGroupName(mGroupName);
        chatMessage.setUserName(mPreferenceManager.getUserName());
        chatMessage.setMessageImage(messageImage);
        chatMessage.setMessageStatu(statu);
        if (statu==GlobleData.USER_PUT_HOMEWORK){
            chatMessage.setUserIcon(String.valueOf(work.getTaskId()));
            chatMessage.setUserNickName(String .valueOf(work.getIdInTask()));
        }else if (statu==GlobleData.MASTER_PUT_TASK){
            chatMessage.setUserIcon(String .valueOf(task.getIdInGroup()));
        }
        mGroupMessageDB.add(mGroup.getGroupName(), chatMessage);
        return chatMessage;
    }

    public void flash(int which,GroupChatMessage chatMessage) {
        if (chatMessage==null){
            mData=mGroupMessageDB.find(mGroup.getGroupName(), 1,mMessageNum);
            mAdapter=new ChatMessageAdapter(mApplication,mData,this);
            mListView.setAdapter(mAdapter);
        }else {
            mData.add(chatMessage);
            mAdapter.notifyDataSetChanged();
        }
        mGroupMessageDB.updateReaded(mGroup.getGroupName());
        GroupAll.mIsChattingFlash =false;
        super.flash(which);
        Groups.mIsFlash =true;
    }

    public void sendMessage(String url, String message, String isImage, int statu, Work work, Task task){
            JSONObject jsonObject = new JSONObject();
            MyJsonObjectRequest jsonObjectRequest = new MyJsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    makeMap(message,isImage,statu,work,task),
                    new MyResponseErrorListenerAndListener(getActivity(),statu)
            );
            mApplication.getRequestQueue().add(jsonObjectRequest);
    }
    /**
     *  如果是普通消息 message=消息 isIamge=空 type=空
     *  如果是 Emoji消息 !
     *  如果是 photo消息 message=空 isIamge=图片路径 type=空
     *  如果是 homeWork消息 message=消息 isImage=无 work
     *  如果是 Task消息 message=消息 isImage=无 task
     *  如果是 同意某人加入 无
     *  如果是 不同意某人加入 无
     */
    private HashMap<String ,String > makeMap(String message,String isImage,int statu,Work work,Task task){
        HashMap<String ,String > map=new HashMap();

        map.put(GlobleData.GROUP_NAME, mGroup.getGroupName());
        map.put(GlobleData.USER_NAME, mApplication.getSpUtil().getUserName());
        map.put(GlobleData.MESSAGE_STATU, String.valueOf(statu));
        if (statu==GlobleData.COMMOM_MESSAGE){
            formparams.add(new BasicNameValuePair(GlobleData.MESSAGE, message));
            map.put(GlobleData.MESSAGE, message);

        } else if (statu==GlobleData.EMOJI_MESSAGE){
            formparams.add(new BasicNameValuePair(GlobleData.MESSAGE, message));
            map.put(GlobleData.MESSAGE,message);

        }else if (statu==GlobleData.PHOTO_MESSAGE){
            formparams.add(new BasicNameValuePair(GlobleData.MESSAGE, message));
            map.put(GlobleData.MESSAGE, message);

        }else if (statu==GlobleData.USER_PUT_HOMEWORK){
            map.put(GlobleData.MESSAGE, message);
            map.put(GlobleData.TASK_ID, String.valueOf(work.getTaskId()));
            map.put(GlobleData.ID_IN_TASK, String.valueOf(work.getIdInTask()));
        }else if (statu==GlobleData.MASTER_PUT_TASK){
            map.put(GlobleData.MESSAGE, message);
            map.put(GlobleData.ID_IN_GROUP, String.valueOf(task.getIdInGroup()));
        }
        return map;
    }

    @Override
    protected void upData() {
        super.upData();
        if (mMessageMax==0){
            mMessageMax=mGroupMessageDB.getMessageNum(mGroup.getGroupName());
        }
        mMessageNum+=10;
        if (mMessageNum>=mMessageMax){
            mMessageNum=mMessageMax;
            DialogUtil.showToast(getActivity(), "消息已经显示完毕");
        }
        flash(GlobleData.SELECT_FRIST,null);
    }

    @Override
    public void onClick(final GroupChatMessage chatMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (chatMessage.getMessageStatu() == GlobleData.USER_REQUEST_JOIN_GROUP) {
            builder.setMessage("加入请求");
            builder.setPositiveButton("同意加入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mGroupMessageDB.update(GlobleData.MESSAGE_STATU, String.valueOf(GlobleData.YOU_AGREE_TO_JOIN_GROUP), chatMessage.getDateStr(), mGroupName);
                    mGroupMessageDB.update(GlobleData.MESSAGE, "您已经同意" + chatMessage.getUserNickName() + "加入了" + chatMessage.getGroupName(), chatMessage.getDateStr(), mGroupName);
                    sendMessage("", null, null, GlobleData.AGREE_USER_TO_GROUP, null, null);
                }
            });
            builder.setNegativeButton("不同意加入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mGroupMessageDB.update(GlobleData.MESSAGE_STATU, String.valueOf(GlobleData.YOU_DISAGREE_TO_JOIN_GROUP), chatMessage.getDateStr(), mGroupName);
                    mGroupMessageDB.update(GlobleData.MESSAGE, "您已经拒绝了" + chatMessage.getUserNickName() + "加入" + chatMessage.getGroupName(), chatMessage.getDateStr(), mGroupName);
                    sendMessage("", null, null, GlobleData.DISAGREE_USER_TO_GROUP, null, null);
                }
            });
            builder.create().show();
        } else if (chatMessage.getMessageStatu() == GlobleData.MASTER_PUT_TASK) {
            mTask = mApplication.getTaskDB().getTask(chatMessage.getGroupName(), Integer.parseInt(chatMessage.getUserIcon()));
            mApplication.map.put("nowTask", mTask);
            Intent intent = new Intent(getActivity(), TaskAndWorkActivity.class);
            intent.putExtra("which", GlobleData.IS_TASK);
            startActivity(intent);
        } else if (chatMessage.getMessageStatu() == GlobleData.USER_PUT_HOMEWORK) {
            mWork = mApplication.getWorkDB().getWork(chatMessage.getGroupName(),
                    Integer.parseInt(chatMessage.getUserIcon()),
                    Integer.parseInt(chatMessage.getUserNickName()));
            mApplication.map.put("nowWork", mWork);
            Intent intent = new Intent(getActivity(), TaskAndWorkActivity.class);
            intent.putExtra("which", GlobleData.IS_WORK);
            startActivity(intent);
        }
    }

    public GroupMessageDB getmGroupMessageDB() {
        return mGroupMessageDB;
    }

    public Work getmWork() {
        return mWork;
    }

    public Task getmTask() {
        return mTask;
    }

    public String getmGroupName() {
        return mGroupName;
    }

    public void setmWork(Work mWork) {
        this.mWork = mWork;
    }

    public void setmTask(Task mTask) {
        this.mTask = mTask;
    }

    public ImageView getmMore() {
        return mMore;
    }

    public Button getmMsgSend() {
        return mMsgSend;
    }

    private void openThread(String message, String isImage, int statu, Work work, Task task){
        formparams.clear();
        formparams.add(new BasicNameValuePair(GlobleData.GROUP_NAME, mGroup.getGroupName()));
        formparams.add(new BasicNameValuePair(GlobleData.USER_NAME, "13588197966"));
        formparams.add(new BasicNameValuePair(GlobleData.MESSAGE_STATU, String.valueOf(statu)));
        if (statu==GlobleData.COMMOM_MESSAGE){
            formparams.add(new BasicNameValuePair(GlobleData.MESSAGE, message));

        } else if (statu==GlobleData.EMOJI_MESSAGE){
            formparams.add(new BasicNameValuePair(GlobleData.MESSAGE, message));

        }else if (statu==GlobleData.PHOTO_MESSAGE){
            formparams.add(new BasicNameValuePair(GlobleData.MESSAGE, message));

        }else if (statu==GlobleData.USER_PUT_HOMEWORK){
            formparams.add(new BasicNameValuePair(GlobleData.MESSAGE, message));
            formparams.add(new BasicNameValuePair(GlobleData.TASK_ID, String.valueOf(work.getTaskId())));
            formparams.add(new BasicNameValuePair(GlobleData.ID_IN_TASK, String.valueOf(work.getIdInTask())));
        }else if (statu==GlobleData.MASTER_PUT_TASK){
            formparams.add(new BasicNameValuePair(GlobleData.MESSAGE, message));
            formparams.add(new BasicNameValuePair(GlobleData.ID_IN_GROUP, String.valueOf(task.getIdInGroup())));
        }
        new Thread(new MyRunnable(formparams, GlobleData.GROUP_SEND_MESSAGE, handler,statu)).start();
    }

    private MyHandler handler=new MyHandler(getActivity()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GlobleData.SEND_MESSAGE_SUCCESS:
                    break;
            }
        }
    };

}
