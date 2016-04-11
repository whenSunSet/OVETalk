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
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;

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
import talk.util.MyPreferenceManager;
import talk.http.SendMessage;

public class GroupChatting extends BasicFragment implements ChatMessageAdapter.AdapterClickListener,SendMessage.SendMessageListener{
    private ImageView mMore;
    private Button mMsgSend;
    private EditText mMsgInput;
    private ImageView mHomeWork;
    private ImageView mPicture;
    private ImageView mEmoji;
    public LinearLayout mContainer;

    private int mGroupId;
    private GroupAll mActivity;

    //发送消息的数据
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

    private GroupChatMessage mChatMessage;
    private int mMessageStatu;
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
        mGroupId = mGroup.getGroupId();

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
        mMessageMax=mGroupMessageDB.getMessageNum(mGroup.getGroupId());

        // 获取10条聊天记录
        mData = mGroupMessageDB.find(mGroup.getGroupId(), 1, mMessageNum);
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

                addAndSendMessage(msg, "0", 1, null, null);
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

    @Override
    public void onClick(final GroupChatMessage chatMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        mMessageStatu=chatMessage.getMessageStatu();
        mChatMessage=chatMessage;
        if (chatMessage.getMessageStatu() == GlobleData.USER_REQUEST_JOIN_GROUP) {
            mGroup.setGroupId(GlobleData.SYSTEM);
            builder.setMessage("加入请求");
            builder.setPositiveButton("同意加入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendMessage(GlobleData.agreeOrDisAgree, null, null, GlobleData.AGREE_USER_TO_GROUP, null, null);
                }
            });
            builder.setNegativeButton("不同意加入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendMessage(GlobleData.agreeOrDisAgree, null, null, GlobleData.DISAGREE_USER_TO_GROUP, null, null);
                }
            });
            builder.create().show();
        } else if (chatMessage.getMessageStatu() == GlobleData.MASTER_SEND_TASK_MESSAGE) {
            mTask = mApplication.getTaskDB().getTask(chatMessage.getGroupId(), Integer.parseInt(chatMessage.getUserIcon()));
            mApplication.map.put("nowTask", mTask);
            Intent intent = new Intent(getActivity(), TaskAndWorkActivity.class);
            intent.putExtra("which", GlobleData.IS_TASK);
            startActivity(intent);
        } else if (chatMessage.getMessageStatu() == GlobleData.USER_SEND_HOMEWORK_MESSAGE) {
            mWork = mApplication.getWorkDB().getWork(chatMessage.getGroupId(),
                    Integer.parseInt(chatMessage.getUserIcon()),
                    Integer.parseInt(chatMessage.getUserNickName()));
            mApplication.map.put("nowWork", mWork);
            Intent intent = new Intent(getActivity(), TaskAndWorkActivity.class);
            intent.putExtra("which", GlobleData.IS_WORK);
            startActivity(intent);
        }
    }

    public void addAndSendMessage(String message, String messageImage, int messageStatu, Work work, Task task) {
        mMessageStatu=messageStatu;
        mChatMessage = makeMessage(mGroupId,message, messageImage,mPreferenceManager.getUserId(),mMessageStatu, task, work);
        HashMap<String,Object> result=null;
        if (mChatMessage.getMessageStatu()==GlobleData.COMMOM_MESSAGE||mChatMessage.getMessageStatu()==GlobleData.EMOJI_MESSAGE){
            sendMessage(GlobleData.jpush_sendMessage, mChatMessage.getMessage(), null,mMessageStatu, null, null);
        }else if (mChatMessage.getMessageStatu()==GlobleData.PHOTO_MESSAGE){
            sendMessage(GlobleData.jpush_sendImage, null, mChatMessage.getMessageImage(),mMessageStatu, null, null);
        }else if (mChatMessage.getMessageStatu()==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
            sendMessage(GlobleData.sendWorkMessage,mChatMessage.getMessage(), mChatMessage.getMessageImage(),mMessageStatu, work, null);
        }else if (mChatMessage.getMessageStatu()==GlobleData.MASTER_SEND_TASK_MESSAGE){
            sendMessage(GlobleData.sendTaskMessage,mChatMessage.getMessage(), null, mMessageStatu, null, task);
        }
        makeResult(result, mMessageStatu, mChatMessage);
        flash(GlobleData.SELECT_LAST, mChatMessage);
    }
    //组装chatMessage
    public GroupChatMessage makeMessage(int groupId,String message, String messageImage,String you, int messageStatu, Task task, Work work){
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
            chatMessage.setUserIcon(String.valueOf(work.getTaskId()));
            chatMessage.setUserNickName(String.valueOf(work.getIdInTask()));
        }else if (messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE){
            chatMessage.setUserIcon(String.valueOf(task.getIdInGroup()));
        }
        return chatMessage;
    }
    /**
     *  如果是普通消息 message=消息 isIamge=空 type=空
     *  如果是 Emoji消息 !
     *  如果是 photo消息 message=空 isIamge=图片路径 type=空!
     *  如果是 homeWork消息 message=消息 isImage=无 work
     *  如果是 Task消息 message=消息 isImage=无 task
     *  如果是 同意某人加入 无
     *  如果是 不同意某人加入 无
     */
    private void sendMessage(String url, String message, String isImage, int messageStatu, Work work, Task task){
        RequestParams requestParams=new RequestParams();
        requestParams.put(GlobleData.GROUP_ID, mGroup.getGroupId());
        requestParams.put(GlobleData.USER_NAME, mApplication.getSpUtil().getUserId());
        requestParams.put(GlobleData.MESSAGE_STATU, messageStatu);

        if (messageStatu==GlobleData.PHOTO_MESSAGE){
            try {
                requestParams.put(GlobleData.FILE,new File(isImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            SendMessage.getSendMessage().post(mApplication,messageStatu,url,requestParams,this);
        }else{
            if (messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
                requestParams.put(GlobleData.TASK_ID, work.getTaskId());
                requestParams.put(GlobleData.ID_IN_TASK, work.getIdInTask());

            }else if (messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE){
                requestParams.put(GlobleData.ID_IN_GROUP, task.getIdInGroup());
            }
            requestParams.put(GlobleData.MESSAGE, message);
            SendMessage.getSendMessage().post(mApplication,messageStatu,url,requestParams,this);
        }
    }

    @Override
    protected void upData() {
        super.upData();
        if (mMessageMax==0){
            mMessageMax=mGroupMessageDB.getMessageNum(mGroup.getGroupId());
        }
        mMessageNum+=10;
        if (mMessageNum>=mMessageMax){
            mMessageNum=mMessageMax;
            DialogUtil.showToast(getActivity(), "消息已经显示完毕");
        }
        flash(GlobleData.SELECT_FRIST,null);
    }

    public void flash(int which,GroupChatMessage chatMessage) {
        if (chatMessage==null){
            mData=mGroupMessageDB.find(mGroup.getGroupId(), 1,mMessageNum);
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
    }


    @Override
    public void success(HashMap<String, Object> result) {
        makeResult(result, mMessageStatu, mChatMessage);
    }

    private void makeResult(HashMap<String,Object> result,int messageStatu,GroupChatMessage chatMessage){
        if (result==null){
            return;
        }
        if ((int)result.get("res")==GlobleData.SEND_MESSAGE_SUCCESS){
            switch (messageStatu){
                case GlobleData.AGREE_USER_TO_GROUP:
                    mGroupMessageDB.update(GlobleData.MESSAGE_STATU, String.valueOf(GlobleData.YOU_AGREE_TO_JOIN_GROUP), chatMessage.getDateStr(), mGroupId);
                    mGroupMessageDB.update(GlobleData.MESSAGE, "您已经同意" + chatMessage.getUserNickName() + "加入了" + chatMessage.getGroupId(), chatMessage.getDateStr(), mGroupId);
                    break;
                case GlobleData.DISAGREE_USER_TO_GROUP:
                    mGroupMessageDB.update(GlobleData.MESSAGE_STATU, String.valueOf(GlobleData.YOU_DISAGREE_TO_JOIN_GROUP), chatMessage.getDateStr(), mGroupId);
                    mGroupMessageDB.update(GlobleData.MESSAGE, "您已经拒绝了" + chatMessage.getUserNickName() + "加入" + chatMessage.getGroupId(), chatMessage.getDateStr(), mGroupId);
                    break;
                default:
                    mGroupMessageDB.add(mGroup.getGroupId(), chatMessage);
                    break;
            }
        }
    }
    public ImageView getmMore() {
        return mMore;
    }

    public Button getmMsgSend() {
        return mMsgSend;
    }
   }
