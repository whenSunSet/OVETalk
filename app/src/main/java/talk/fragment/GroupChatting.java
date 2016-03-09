package talk.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.example.heshixiyang.ovetalk.R;

import org.apache.commons.httpclient.NameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import talk.Globle.GlobleData;
import talk.activity.aboutGroup.TaskAndWorkActivity;
import talk.activity.create.MakeHomeWorkActivity;
import talk.activity.fragment.GroupAll;
import talk.adapter.ChatMessageAdapter;
import talk.datebase.GroupMessageDB;
import talk.model.Group;
import talk.model.GroupChatMessage;
import talk.model.Task;
import talk.model.Work;
import talk.util.DialogUtil;
import talk.util.MyPreferenceManager;
import talk.util.MyRunnable;

public class GroupChatting extends BasicFragment {
    //-------------------------handler的几种状态
    public static final int SEND_JOIN_MESSAGE_ERROR=1;
    public static final int SEND_JOIN_MESSAGE_INTERNET_ERROR=2;
    public static final int SEND_JOIN_MESSAGE_SUCCESS=3;
    public static final int CHANGE_MESSAGE_NUM=4;
    public static final int I_WANT_TO_CALL_110=110;

    public ImageView mMore;
    private Button mMsgSend;
    private EditText mMsgInput;

    private ImageView mHomeWork;
    private ImageView mPicture;
    private ImageView mEmoji;
    private LinearLayout mContainer;

    private String mGroupName;
    private GroupAll mActivity;
    private GroupChatMessage mChatMessage ;

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
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEND_JOIN_MESSAGE_ERROR:
                    DialogUtil.showToast(getActivity(), "返回值错误");
                    break;

                case SEND_JOIN_MESSAGE_INTERNET_ERROR:
                    DialogUtil.showToast(getActivity(),"网络错误");
                    break;

                case SEND_JOIN_MESSAGE_SUCCESS:

                    break;

                case CHANGE_MESSAGE_NUM:
                    if (mMessageMax<=mMessageNum){
                        DialogUtil.showToast(mTalkApplication,"没有更多的消息了");

                        break;
                    }

                    mMessageNum=mMessageNum+10;
                    mData = mGroupMessageDB.find(mGroup.getGroupName(), 1, mMessageNum);

                    mAdapter.notifyDataSetChanged();
                    ((GroupAll)getActivity()).myAdapter.notifyDataSetChanged();
                    //将焦点放在上一次消息的最前面一个
                    mListView.setSelection(10);
                    break;
                default:
                    break;

            }
        }

    };

    public LinearLayout getmContan() {
        return mContainer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isChattingFragment=true;
        init(inflater);

        initEvent();
        return view;
    }

    @Override
    protected void init(final LayoutInflater inflater) {
        super.init(inflater);
        mActivity=(GroupAll)getActivity();
        mPreferenceManager = mTalkApplication.getSpUtil();
        mGroupMessageDB=mActivity.mGroupMessageDB;

        mGroup = mActivity.mGroup;
        mGroupName= mGroup.getGroupName();

        if (mActivity.isSystemGroup){
            //如果是SystemGroup的话就把输入框去掉
            mGroupName=mTalkApplication.getSpUtil().getUserName();
            view.findViewById(R.id.contant).setVisibility(View.GONE);
        }else {
            mMsgSend = (Button) view.findViewById(R.id.id_chat_send);
            mMsgInput = (EditText) view.findViewById(R.id.id_chat_msg);
            mMore=(ImageView)view.findViewById(R.id.more);

            mHomeWork=(ImageView) view.findViewById(R.id.btn_homework);
            mPicture=(ImageView) view.findViewById(R.id.btn_picture);
            mEmoji=(ImageView) view.findViewById(R.id.btn_emoji);

            mContainer=(LinearLayout)view.findViewById(R.id.ll_btn_container);
            formparams = new ArrayList<NameValuePair>();
        }

        //将本group的所有消息设置为已读
        mGroupMessageDB.updateReaded(mGroupName);

        mMessageMax=mGroupMessageDB.getMessageNum(mGroup.getGroupName());

        // 获取10条聊天记录
        mData = mGroupMessageDB.find(mGroup.getGroupName(), 1, mMessageNum);
        mAdapter = new ChatMessageAdapter(getActivity(), mData);
        mListView.setAdapter((ListAdapter) mAdapter);
        mListView.setSelection(mData.size() - 1);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final GroupChatMessage chatMessage = (GroupChatMessage) mData.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                if (chatMessage.getMessageStatu() == GlobleData.USER_REQUEST_JOIN_GROUP){
                    builder.setMessage("加入请求");
                    builder.setPositiveButton("同意加入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mGroupMessageDB.update("messageStatu", "12", chatMessage.getDateStr(), mGroupName);
                            mGroupMessageDB.update("message", "您已经同意" + chatMessage.getUserNickName() + "加入了" + chatMessage.getGroupName(), chatMessage.getDateStr(), mGroupName);
                            openThread(null,null,GlobleData.AGREE_USER_TO_GROUP,null,null);
                        }
                    });
                    builder.setNegativeButton("不同意加入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mGroupMessageDB.update("messageStatu", "13", chatMessage.getDateStr(), mGroupName);
                            mGroupMessageDB.update("message", "您已经拒绝了" + chatMessage.getUserNickName() + "加入" + chatMessage.getGroupName(), chatMessage.getDateStr(), mGroupName);
                            openThread(null, null, GlobleData.DISAGREE_USER_TO_GROUP, null, null);
                        }
                    });
                    builder.create().show();
                }else if (chatMessage.getMessageStatu()==GlobleData.MASTER_PUT_TASK){
                    mTask=mTalkApplication.getTaskDB().getTask(chatMessage.getGroupName(),Integer.parseInt(chatMessage.getUserIcon()));
                    mTalkApplication.map.put("task",mTask);
                    Intent intent=new Intent(getActivity(), TaskAndWorkActivity.class);
                    intent.putExtra("which", GlobleData.IS_TASK);
                    startActivity(intent);
                }else if (chatMessage.getMessageStatu()==GlobleData.USER_PUT_HOMEWORK){
                    mWork=mTalkApplication.getWorkDB().getWork(chatMessage.getGroupName(),
                            Integer.parseInt(chatMessage.getUserIcon()),
                            Integer.parseInt(chatMessage.getUserNickName()));
                    mTalkApplication.map.put("work",mWork);
                    Intent intent=new Intent(getActivity(),TaskAndWorkActivity.class);
                    intent.putExtra("which", GlobleData.IS_WORK);
                    startActivity(intent);
                }
            }
        });
    }

    private void initEvent(){
        if (!mActivity.isSystemGroup){
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
                intent.putExtra("group", mGroup.getGroupName());
                intent.putExtra("type",3);
                startActivityForResult(intent, 3);
            }
        });

    }

    private void initPrintEvent() {
        //通过点击mMore这个按钮让mContan显示或者消失
        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    DialogUtil.showToast(mTalkApplication, "你还没输入文字呢");
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

    //-----------------------添加自己的信息 并开启线程发送信息---------------------------
    public void addMessage(String message,String messageImage,int statu,Work work,Task task) {
        GroupChatMessage chatMessage =makeChatMessage(message,messageImage,statu,null);
        if (chatMessage.getMessageStatu()==GlobleData.MASTER_PUT_TASK){
            openThread(chatMessage.getMessage(),null,statu,null,task);
            return;
        }else if (chatMessage.getMessageStatu()==GlobleData.USER_PUT_HOMEWORK&&work.getType()==2){
            openThread(chatMessage.getMessage(),chatMessage.getMessageImage(),chatMessage.getMessageStatu(),work,null);
            return;
        }
        flash(chatMessage);
        if (chatMessage.getMessageStatu()==GlobleData.COMMOM_MESSAGE&&chatMessage.getMessageStatu()==GlobleData.EMOJI_MESSAGE){
            openThread(chatMessage.getMessage(),null,chatMessage.getMessageStatu(),null,null);
        }else if (chatMessage.getMessageStatu()==GlobleData.PHOTO_MESSAGE){
            openThread(null,chatMessage.getMessageImage(),chatMessage.getMessageStatu(),null,null);
        }else if (chatMessage.getMessageStatu()==GlobleData.USER_PUT_HOMEWORK){
            openThread(chatMessage.getMessage(),chatMessage.getMessageImage(),chatMessage.getMessageStatu(),work,null);
        }
    }

    //组装并储存chatMessage
    private GroupChatMessage makeChatMessage(String message,String messageImage,int statu,Task task){
        GroupChatMessage chatMessage = new GroupChatMessage();
        chatMessage.setIsComing(false);
        chatMessage.setDate(new Date());
        chatMessage.setMessage(message);
        chatMessage.setReaded(true);
        chatMessage.setUserIcon(mPreferenceManager.getUserIcon());
        chatMessage.setUserNickName(mPreferenceManager.getUsreNickName());
        chatMessage.setGroupName(mGroupName);
        chatMessage.setUserName(mPreferenceManager.getUserName());
        chatMessage.setMessageImage(messageImage);
        chatMessage.setMessageStatu(statu);
        mGroupMessageDB.add(mGroup.getGroupName(), chatMessage);
        return chatMessage;
    }

    //刷新chat界面
    public void flash(GroupChatMessage chatMessage) {
        if (chatMessage==null){
            mData=mGroupMessageDB.find(mGroup.getGroupName(), 1, mGroupMessageDB.getUnreadedMsgsCountByGroupId(mGroup.getGroupName()));
        }else {
            mData.add(chatMessage);
        }
        mGroupMessageDB.updateReaded(mGroup.getGroupName());
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(mData.size() - 1);

        GroupAll.isFlash=false;
    }

    /**
     *  如果是普通消息 message=消息 isIamge=空 type=空
     *  如果是 Emoji消息 同上
     *  如果是 photo消息 message=空 isIamge=图片路径 type=空
     *  如果是 homeWork消息 message=消息 isImage=无 work
     *  如果是 Task消息 message=消息 isImage=无 task
     *  如果是 同意某人加入 无
     *  如果是 不同意某人加入 无
     */
    private void openThread(String message,String isImage,int statu,Work work,Task task){
        formparams.clear();
        formparams.add(new NameValuePair(GlobleData.GROUP_NAME, mGroup.getGroupName()));
        formparams.add(new NameValuePair(GlobleData.USER_NAME, "13588197966"));
        formparams.add(new NameValuePair(GlobleData.MESSAGE_STATU, String.valueOf(statu)));
        if (statu==GlobleData.COMMOM_MESSAGE){
            formparams.add(new NameValuePair(GlobleData.MESSAGE, message));

        } else if (statu==GlobleData.EMOJI_MESSAGE){
            formparams.add(new NameValuePair(GlobleData.MESSAGE, message));

        }else if (statu==GlobleData.PHOTO_MESSAGE){
            formparams.add(new NameValuePair(GlobleData.MESSAGE, message));

        }else if (statu==GlobleData.USER_PUT_HOMEWORK){
            formparams.add(new NameValuePair(GlobleData.MESSAGE, message));
            formparams.add(new NameValuePair(GlobleData.TASK_ID, String.valueOf(work.getTaskId())));
            formparams.add(new NameValuePair(GlobleData.ID_IN_TASK, String.valueOf(work.getIdInTask())));
        }else if (statu==GlobleData.MASTER_PUT_TASK){
            formparams.add(new NameValuePair(GlobleData.MESSAGE, message));
            formparams.add(new NameValuePair(GlobleData.ID_IN_GROUP, String.valueOf(task.getIdInGroup())));
        }
        new Thread(new MyRunnable(formparams, GlobleData.GROUP_SEND_MESSAGE, handler)).start();
    }

}
