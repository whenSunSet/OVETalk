package talk.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import talk.activity.fragment.GroupAll;
import talk.adapter.ChatMessageAdapter;
import talk.datebase.GroupMessageDB;
import talk.model.Group;
import talk.model.GroupChatMessage;
import talk.util.ClickContarctionImageView;
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
    public ClickContarctionImageView mMore;
    //---------------------listView
    private Button mMsgSend;
    private EditText mMsgInput;
    private ImageView mVedio;
    private LinearLayout mContainer;

    private String mGroupName;
    private GroupAll mActivity;
    private GroupChatMessage mChatMessage ;

    //发送消息的数据
    private List<NameValuePair> formparams;
    private MyPreferenceManager mPreferenceManager;
    private GroupMessageDB mGroupMessageDB;

    private Group mGroup;
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
    protected void init(LayoutInflater inflater) {
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
            mMore=(ClickContarctionImageView)view.findViewById(R.id.more);
            mVedio=(ImageView) view.findViewById(R.id.btn_video);
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
                            mGroupMessageDB.update("messageStatu","12",chatMessage.getDateStr(),mGroupName);
                            mGroupMessageDB.update("message","您已经同意"+chatMessage.getUserNickName()+"加入了"+chatMessage.getGroupName(),chatMessage.getDateStr(),mGroupName);
                        }
                    });
                    builder.setNegativeButton("不同意加入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mGroupMessageDB.update("messageStatu","13",chatMessage.getDateStr(),mGroupName);
                            mGroupMessageDB.update("message","您已经拒绝了"+chatMessage.getUserNickName()+"加入"+chatMessage.getGroupName(),chatMessage.getDateStr(),mGroupName);
                        }
                    });
                }
                builder.create().show();
            }
        });
    }

    private void initEvent(){
        if (!mActivity.isSystemGroup){
            initPrintEvent();
        }
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
                addMessage(msg, "0");
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

    //-------------------------刷新本fragment
    public void flash(GroupChatMessage chatMessage) {
        if (chatMessage==null){
            mData=mGroupMessageDB.find(mGroup.getGroupName(), 1, mGroupMessageDB.getUnreadedMsgsCountByGroupId(mGroup.getGroupName()));
        }else {
            mData.add(chatMessage);
        }
        mGroupMessageDB.updateReaded(mGroup.getGroupName());
        mListView.setSelection(mData.size() - 1);

        GroupAll.isFlash=false;
    }

    //-----------------------添加自己的信息 并开启线程发送信息---------------------------
    public void addMessage(String message,String messageImage,int statu,String path) {
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
        mData.add(chatMessage);
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(mData.size() - 1);

        //如果是视屏的话在这里开启一个线程，发送视频信息
        openThread(message, messageImage,statu,path);
    }

    private void openThread(String message,String isImage,int statu,String path){
        formparams.clear();
        formparams.add(new NameValuePair("groupname", mGroup.getGroupName()));
        formparams.add(new NameValuePair("username", "13588197966"));
        formparams.add(new NameValuePair("message", message));
        formparams.add(new NameValuePair("messageStatu", String.valueOf(statu)));

        if (statu==GlobleData.COMMOM_MESSAGE){
        }else if (statu==GlobleData.EMOJI_MESSAGE){

        }else if (statu==GlobleData.PHOTO_MESSAGE){
            formparams.remove("message");

        }else if (statu==GlobleData.USER_PUT_HOMEWORK){
            formparams.add(new NameValuePair("",path));
        }
        new Thread(new MyRunnable(formparams, GlobleData.GROUP_SEND_MESSAGE, handler)).start();
    }

}
