package talk.util;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import talk.Globle.GlobleData;
import talk.activity.aboutGroup.TaskAndWorkActivity;
import talk.fragment.GroupChatting;
import talk.model.GroupChatMessage;

/**
 * Created by heshixiyang on 2016/3/24.
 */
public class MyChatListView extends ListView {
    private GroupChatting groupChatting;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyChatListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyChatListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyChatListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGroupChatting(GroupChatting groupChatting) {
        this.groupChatting = groupChatting;
    }

    public void onClick(){
        final GroupChatMessage chatMessage=(GroupChatMessage)this.getSelectedItem();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (chatMessage.getMessageStatu() == GlobleData.USER_REQUEST_JOIN_GROUP){
            builder.setMessage("加入请求");
            builder.setPositiveButton("同意加入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    groupChatting.getmGroupMessageDB().update(GlobleData.MESSAGE_STATU, String.valueOf(GlobleData.YOU_AGREE_TO_JOIN_GROUP), chatMessage.getDateStr(), groupChatting.getmGroupName());
                    groupChatting.getmGroupMessageDB().update(GlobleData.MESSAGE, "您已经同意" + chatMessage.getUserNickName() + "加入了" + chatMessage.getGroupName(), chatMessage.getDateStr(), groupChatting.getmGroupName());
                    groupChatting.sendMessage("", null, null, GlobleData.AGREE_USER_TO_GROUP, null, null);
                }
            });
            builder.setNegativeButton("不同意加入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    groupChatting.getmGroupMessageDB().update(GlobleData.MESSAGE_STATU, String.valueOf(GlobleData.YOU_DISAGREE_TO_JOIN_GROUP), chatMessage.getDateStr(), groupChatting.getmGroupName());
                    groupChatting.getmGroupMessageDB().update(GlobleData.MESSAGE, "您已经拒绝了" + chatMessage.getUserNickName() + "加入" + chatMessage.getGroupName(), chatMessage.getDateStr(), groupChatting.getmGroupName());
                    groupChatting.sendMessage("", null, null, GlobleData.DISAGREE_USER_TO_GROUP, null, null);
                }
            });
            builder.create().show();
        }else if (chatMessage.getMessageStatu()==GlobleData.MASTER_PUT_TASK){
            groupChatting.setmTask(groupChatting.getmApplication().getTaskDB().getTask(chatMessage.getGroupName(),Integer.parseInt(chatMessage.getUserIcon())));
            groupChatting.getmApplication().map.put("nowTask",groupChatting.getmTask());
            Intent intent=new Intent(groupChatting.getActivity(), TaskAndWorkActivity.class);
            intent.putExtra("which", GlobleData.IS_TASK);
            groupChatting.startActivity(intent);
        }else if (chatMessage.getMessageStatu()==GlobleData.USER_PUT_HOMEWORK){
            groupChatting.setmWork(groupChatting.getmApplication().getWorkDB().getWork(chatMessage.getGroupName(),
                    Integer.parseInt(chatMessage.getUserIcon()),
                    Integer.parseInt(chatMessage.getUserNickName())));
            groupChatting.getmApplication().map.put("nowWork",groupChatting.getmTask());
            Intent intent=new Intent(groupChatting.getActivity(),TaskAndWorkActivity.class);
            intent.putExtra("which", GlobleData.IS_WORK);
            groupChatting.startActivity(intent);
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction()==MotionEvent.ACTION_UP){
            onClick();
        }
        return super.dispatchTouchEvent(ev);
    }
}
