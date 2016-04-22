package talk.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.heshixiyang.ovetalk.R;

import java.util.Date;

import talk.Globle.GlobleData;
import talk.Globle.GlobleMethod;
import talk.activity.aboutGroup.TaskAndWorkActivity;
import talk.activity.create.MakeHomeWorkActivity;
import talk.activity.fragment.GroupAll;
import talk.activity.fragment.Groups;
import talk.adapter.ChatMessageAdapter;
import talk.datebase.GroupMessageDB;
import talk.model.Group;
import talk.model.GroupChatMessage;
import talk.model.TaskBean;
import talk.model.WorkBean;
import talk.service.HttpIntentService;
import talk.util.DialogUtil;

public class GroupChatting extends BasicFragment implements ChatMessageAdapter.AdapterClickListener{
    private Button mMsgSend;
    private EditText mMsgInput;
    private ImageView mMore;
    private ImageView mHomeWork;
    private ImageView mPicture;
    private ImageView mEmoji;
    public LinearLayout mContainer;

    private int mGroupId;
    private Group mGroup;
    private String myId;
    private String messageImage;

    private GroupAll mActivity;
    private GroupMessageDB mGroupMessageDB;

    //---------------------mContan是否可见
    private Boolean isVisable =false;

    private int mMessageNum=10;

    private GroupChatMessage mChatMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mIsChattingFragment =true;
        init(inflater);
        if (!mActivity.mIsSystemGroup){
            initPrintEvent();
            initMoreEvent();
        }
        return view;
    }

    @Override
    protected void init(final LayoutInflater inflater) {
        super.init(inflater);
        mActivity=(GroupAll)getActivity();
        mGroupMessageDB=mActivity.mGroupMessageDB;

        mGroup = mActivity.mGroup;
        mGroupId = mGroup.getGroupId();
        myId=mApplication.getSpUtil().getUserId();

        if (mActivity.mIsSystemGroup){
            //如果是SystemGroup的话就把输入框去掉
            mGroupId = GlobleData.SYSTEM;
            view.findViewById(R.id.contant).setVisibility(View.GONE);
        }else {
            mMsgSend = (Button) view.findViewById(R.id.id_chat_send);
            mMsgInput = (EditText) view.findViewById(R.id.id_chat_msg);
            mMore=(ImageView)view.findViewById(R.id.more);

            mHomeWork=(ImageView) view.findViewById(R.id.btn_homework);
            mPicture=(ImageView) view.findViewById(R.id.btn_picture);
            mEmoji=(ImageView) view.findViewById(R.id.btn_emoji);

            mContainer=(LinearLayout)view.findViewById(R.id.ll_btn_container);
        }

        //将本group的所有消息设置为已读
        mGroupMessageDB.updateReaded(mGroupId);

        // 获取10条聊天记录
        mData = mGroupMessageDB.find(mGroup.getGroupId(), 1, mMessageNum,null);
        mAdapter = new ChatMessageAdapter(mApplication, mData,this);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mData.size() - 1);
        mListView.setClickable(true);
        mListView.getSelectedItem();

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
                GlobleMethod.openPhotoAlbum(getActivity(),GlobleData.OPEN_PHOTO_ALBUM);
                startHttpService(GlobleData.jpush_sendImage,GlobleData.PHOTO_MESSAGE,mGroupId,myId,messageImage,null,null,null);
            }
        });

        mHomeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MakeHomeWorkActivity.class);
                intent.putExtra(GlobleData.GROUP_ID, mGroup.getGroupId());
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

                if (!isVisable) {
                    mContainer.setVisibility(View.VISIBLE);
                    isVisable = true;
                } else {
                    mContainer.setVisibility(View.GONE);
                    isVisable = false;
                }

            }
        });

        mMsgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMsgInput.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    DialogUtil.showToast(mApplication, "你还没输入文字呢");
                }else {
                    startHttpService(GlobleData.jpush_sendMessage,GlobleData.COMMOM_MESSAGE,mGroupId,myId,null,msg,null,null);
                    mMsgInput.setText("");
                }
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

    @Override
    public void onClick(final GroupChatMessage chatMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        mChatMessage=chatMessage;
        if (chatMessage.getMessageStatu() == GlobleData.USER_REQUEST_JOIN_GROUP) {
            mGroup.setGroupId(mChatMessage.getGroupId());
            builder.setMessage("加入请求");
            builder.setPositiveButton("同意加入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startHttpService(GlobleData.agreeOrDisAgree,GlobleData.AGREE_USER_TO_GROUP,chatMessage.getGroupId(),chatMessage.getUserId(),null,null,null,null);
                }
            });
            builder.setNegativeButton("不同意加入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startHttpService(GlobleData.agreeOrDisAgree,GlobleData.DISAGREE_USER_TO_GROUP,chatMessage.getGroupId(),chatMessage.getUserId(),null,null,null,null);
                }
            });
            builder.create().show();
        } else if (chatMessage.getMessageStatu() == GlobleData.MASTER_SEND_TASK_MESSAGE) {
            TaskBean mTaskBean = mApplication.getTaskDB().getTask(chatMessage.getGroupId(), Integer.parseInt(chatMessage.getUserIcon()));
            mApplication.map.put("nowTask", mTaskBean);
            Intent intent = new Intent(getActivity(), TaskAndWorkActivity.class);
            intent.putExtra("which", GlobleData.IS_TASK);
            startActivity(intent);
        } else if (chatMessage.getMessageStatu() == GlobleData.USER_SEND_HOMEWORK_MESSAGE) {
            WorkBean mWorkBean = mApplication.getWorkDB().getWork(chatMessage.getGroupId(),
                    Integer.parseInt(chatMessage.getUserIcon()),
                    Integer.parseInt(chatMessage.getUserNickName()));
            mApplication.map.put("nowWork", mWorkBean);
            Intent intent = new Intent(getActivity(), TaskAndWorkActivity.class);
            intent.putExtra("which", GlobleData.IS_WORK);
            startActivity(intent);
        }
    }

    public void startHttpService(String url,int messageStatu,int groupId,String userName,String messageImage,String message,WorkBean workBean,TaskBean taskBean){
        GroupChatMessage chatMessage=makeMessage(groupId,message,messageImage,userName,messageStatu,taskBean,workBean);
        Intent intent=new Intent(getActivity(), HttpIntentService.class);
        intent.putExtra(GlobleData.MESSAGE_STATU,messageStatu);
        intent.putExtra(GlobleData.GROUP_ID,groupId);
        intent.putExtra(GlobleData.USER_NAME,userName);
        intent.putExtra(GlobleData.URL,url);
        intent.putExtra(GlobleData.IS_MESSAGE,true);
        intent.putExtra(GlobleData.CHAT_MESSAGE,chatMessage);

        if (messageStatu==GlobleData.COMMOM_MESSAGE||messageStatu==GlobleData.EMOJI_MESSAGE){
            intent.putExtra(GlobleData.MESSAGE,message);
        }else {
            intent.putExtra(GlobleData.MESSAGE_IMAGE,messageImage);
            if (messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE){
                intent.putExtra(GlobleData.TASK,taskBean);
            }else if (messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
                intent.putExtra(GlobleData.HOMEWORK,workBean);
            }
        }

        getActivity().startService(intent);
        flash(GlobleData.SELECT_LAST, mChatMessage);
    }

    //组装chatMessage
    public GroupChatMessage makeMessage(int groupId, String message, String messageImage, String you, int messageStatu, TaskBean taskBean, WorkBean workBean){
        GroupChatMessage chatMessage = new GroupChatMessage();
        chatMessage.setIsComing(false);
        chatMessage.setDate(new Date());
        chatMessage.setMessage(message);
        chatMessage.setReaded(true);
        chatMessage.setGroupId(groupId);
        chatMessage.setUserId(you);
        chatMessage.setMessageImage(messageImage);
        chatMessage.setMessageStatu(messageStatu);
        if (messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
            chatMessage.setUserIcon(String.valueOf(workBean.getTaskId()));
            chatMessage.setUserNickName(String.valueOf(workBean.getIdInTask()));
            chatMessage.setMessageImage(mGroup.getGroupIcon());
        }else if (messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE){
            chatMessage.setUserIcon(String.valueOf(taskBean.getIdInGroup()));
            chatMessage.setMessageImage(mGroup.getGroupIcon());
        }
        return chatMessage;
    }

    @Override
    protected void upData() {
        super.upData();
        mMessageNum+=10;
        flash(GlobleData.SELECT_FRIST,null);
    }

    public void flash(int which,GroupChatMessage chatMessage) {
        Boolean isMax=false;
        if (chatMessage==null){
            mData=mGroupMessageDB.find(mGroup.getGroupId(), 1,mMessageNum,isMax);
            mAdapter=new ChatMessageAdapter(mApplication,mData,this);
            mListView.setAdapter(mAdapter);
        }else {
            mData.add(chatMessage);
            mAdapter.notifyDataSetChanged();
        }
        mGroupMessageDB.updateReaded(mGroup.getGroupId());
        GroupAll.mIsChattingFlash =false;
        super.flash(which);
        Groups.mIsFlash =true;

        if (isMax){
            DialogUtil.showToast(mActivity,"消息已经显示完毕");
        }
    }

    public ImageView getmMore() {
        return mMore;
    }

    public Button getmMsgSend() {
        return mMsgSend;
    }

    public void setMessageImage(String messageImage) {
        this.messageImage = messageImage;
    }
}
