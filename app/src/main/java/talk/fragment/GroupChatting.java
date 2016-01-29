package talk.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.BuildConfig;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.example.heshixiyang.ovetalk.R;

import org.apache.commons.httpclient.NameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.activity.fragment.GroupAll;
import talk.activity.fragment.Groups;
import talk.adapter.ChatMessageAdapter;
import talk.datebase.GroupMessageDB;
import talk.model.Group;
import talk.model.GroupChatMessage;
import talk.util.DialogUtil;
import talk.util.MyPreferenceManager;
import talk.util.MyRunnable;

public class GroupChatting extends Fragment {
    //-------------------------handler的几种状态
    public static final int SEND_JOIN_MESSAGE_ERROR=1;
    public static final int SEND_JOIN_MESSAGE_INTERNET_ERROR=2;
    public static final int SEND_JOIN_MESSAGE_SUCCESS=3;
    public static final int CHANGE_MESSAGE_NUM=4;
    public static final int I_WANT_TO_CALL_110=110;
    //---------------------listView
    private ListView mGroupChatMessagesListView;
    private ChatMessageAdapter mAdapter;
    private List<GroupChatMessage> mDatas = new ArrayList<GroupChatMessage>();
    private Button mMsgSend;
    private EditText mMsgInput;
    private ImageView mMore;
    private ImageView mVedio;
    private LinearLayout mContainer;
    private String mGroupName;
    private GroupAll mActivity;
    private GroupChatMessage mChatMessage ;

    //发送消息的数据
    private List<NameValuePair> formparams;
    private View view;
    private TalkApplication mApplication;
    private MyPreferenceManager mPreferenceManager;
    private GroupMessageDB mGroupMessageDB;

    private Group mGroup;
    private int mClickNum=0;
    //---------------------mContan是否可见
    private Boolean isVisble=false;

    //---------------------每次加载消息10个为阶梯
    private int mMessageNum=10;

    private int mMessageMax;

    public void setIsVisble(Boolean isVisble) {
        this.isVisble = isVisble;
    }
    public LinearLayout getmContan() {
        return mContainer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.main_chatting,null);
        initView();
        initEvent();
        return view;
    }

    private void initView() {
        mActivity=(GroupAll)getActivity();
        mApplication = mActivity.mApplication;
        mGroupMessageDB=mActivity.mGroupMessageDB;
        mPreferenceManager = mApplication.getSpUtil();
        mGroup = mActivity.mGroup;
        mGroupName= mGroup.getGroupName();

        if (mActivity.isSystemGroup){
            //如果是SystemGroup的话就把输入框去掉
            mGroupName=mApplication.getSpUtil().getUserName();
            view.findViewById(R.id.contant).setVisibility(View.GONE);
            if (BuildConfig.DEBUG) Log.d("GroupChatting", "isSystemGroup");
        }else {
            mMsgSend = (Button) view.findViewById(R.id.id_chat_send);
            mMsgInput = (EditText) view.findViewById(R.id.id_chat_msg);
            mMore=(ImageView)view.findViewById(R.id.more);
            mVedio=(ImageView) view.findViewById(R.id.btn_video);
            mContainer=(LinearLayout)view.findViewById(R.id.ll_btn_container);
            formparams = new ArrayList<NameValuePair>();
        }

        mGroupChatMessagesListView = (ListView) view.findViewById(R.id.id_chat_listView);
        //将本group的所有消息设置为已读
        mGroupMessageDB.updateReaded(mGroupName);

        mMessageMax=mGroupMessageDB.getMessageNum(mGroup.getGroupName());

        // 获取10条聊天记录
        mDatas = mGroupMessageDB.find(mGroup.getGroupName(), 1, mMessageNum);
        mAdapter = new ChatMessageAdapter(getActivity(), mDatas);
        mGroupChatMessagesListView.setAdapter(mAdapter);
        mGroupChatMessagesListView.setSelection(mDatas.size() - 1);

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
                    mClickNum++;
                    mContainer.setVisibility(View.VISIBLE);
                    //当让mMore出现的时候，让软键盘关闭
                    InputMethodManager im = (InputMethodManager) mApplication.getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(mMsgInput.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    Message message = new Message();
                    message.what = I_WANT_TO_CALL_110;
                    handler.sendMessage(message);

                    isVisble = true;
                } else {
                    mContainer.setVisibility(View.GONE);
                    isVisble = false;
                }

            }
        });

        //当要输入文字的时候，让mMore消失
        mMsgInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickNum++;

                if (BuildConfig.DEBUG) Log.d("GroupChatting", "input");

                //当软键盘出现的时候，让listView 不被下面的信息不被遮挡
                Message message = new Message();
                message.what = I_WANT_TO_CALL_110;
                handler.sendMessage(message);

            }
        });

//
//        //跳转到视频收藏那分享视频
//        mVedio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), MyFavoritesActivity.class);
//                intent.putExtra("isMessage", "isMessage");
//                getActivity().startActivityForResult(intent, 1);
//            }
//        });


        mMsgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMsgInput.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    DialogUtil.showToast(mApplication, "你还没输入文字呢");
                    return;
                }

                //添加消息到数据库里并开启线程发送数据
                addMessage(msg,"0");

                //提醒界面改刷新了
                Groups.isFlash = true;
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
    }

    private void openThread(String message,String isImage){
        formparams.clear();
        formparams.add(new NameValuePair("groupname", mGroup.getGroupName()));
        formparams.add(new NameValuePair("username", "13588197966"));
        formparams.add(new NameValuePair("message", message));
        formparams.add(new NameValuePair("isImage", isImage));
        new Thread(new MyRunnable(formparams, GlobleData.GROUP_SEND_MESSAGE, handler)).start();
    }
    //-----------------------添加自己的信息，分为普通的信息和视频信息
    public void addMessage(String message,String img) {
        GroupChatMessage chatMessage = new GroupChatMessage();
        chatMessage.setIsComing(false);
        chatMessage.setDate(new Date());
        chatMessage.setMessage(message);
        chatMessage.setReaded(true);
        chatMessage.setUserIcon(mPreferenceManager.getUserIcon());
        chatMessage.setUserNickName(mPreferenceManager.getUsreNickName());
        chatMessage.setGroupName(mGroupName);
        chatMessage.setUserName(mPreferenceManager.getUserName());
        chatMessage.setMessageImage(img);
        mGroupMessageDB.add(mGroup.getGroupName(), chatMessage);
        mDatas.add(chatMessage);
        mAdapter.notifyDataSetChanged();
        mGroupChatMessagesListView.setSelection(mDatas.size() - 1);

        //如果是视屏的话在这里开启一个线程，发送视频信息
            openThread(message,img);

    }
    //-------------------------刷新本fragment
    public void flashFragment(){
        mDatas.addAll(mGroupMessageDB.find(mGroup.getGroupName(), 1, mGroupMessageDB.getUnreadedMsgsCountByGroupId(mGroup.getGroupName())));
        mAdapter.notifyDataSetChanged();
        mGroupChatMessagesListView.setSelection(mDatas.size() - 1);
        mGroupMessageDB.updateReaded(mGroup.getGroupName());
        GroupAll.isFlash=false;
    }


    //------------------------当他人发送信息，接受时添加信息并刷新
    public void addNewMessage(GroupChatMessage groupChatMessage){
        //只有在这个的当前group的聊天界面的时候才会调用这个函数，调用这个函数的时候把所有的message 设置为已经读
        mGroupMessageDB.updateReaded(mGroup.getGroupName());
        mDatas.add(groupChatMessage);
        mAdapter.notifyDataSetChanged();
        mGroupChatMessagesListView.setSelection(mDatas.size() - 1);
    }



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
                        DialogUtil.showToast(mApplication,"没有更多的消息了");

                        break;
                    }

                    mMessageNum=mMessageNum+10;
                    mDatas = mGroupMessageDB.find(mGroup.getGroupName(), 1, mMessageNum);

                    mAdapter.notifyDataSetChanged();
                    ((GroupAll)getActivity()).myAdapter.notifyDataSetChanged();
                    //将焦点放在上一次消息的最前面一个
                    mGroupChatMessagesListView.setSelection(10);
                    break;
                case 110:
                    mGroupChatMessagesListView.setAdapter(mAdapter);
                    mGroupChatMessagesListView.setSelection(mDatas.size() - 1);
                    if (mClickNum<2){
                        mMsgInput.performClick();
                    }else if (mClickNum==2){
                        mClickNum=0;
                    }
                    break;
                default:
                    break;

            }
        }

    };


    //    public void messageSend(GroupChatMessage chatMessage){
//        Map requestMap=new HashMap();
//        requestMap.put("groupname",chatMessage.getGroupName());
//        requestMap.put("username",chatMessage.getUserName());
//        requestMap.put("data",chatMessage.getDateStr());
//        requestMap.put("message", chatMessage.getMessage());
//        JSONObject jsonRequest=new JSONObject(requestMap);
//        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, GlobalVariable.GROUP_SEND_MESSAGE, jsonRequest, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                if (jsonObject.toString()=="1"){
//
//                }else {
//                    DialogUtil.showToast(mApplication,"信息发送失败");
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                if (BuildConfig.DEBUG)
//                    Log.d("GroupChatting", "volleyError.networkResponse.statusCode:" + volleyError.networkResponse.statusCode);
//                DialogUtil.showToast(mApplication,"网络错误，信息发送失败");
//            }
//        });
//        requestQueue.add(request);
//    }

}
