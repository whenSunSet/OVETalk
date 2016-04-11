package talk.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heshixiyang.ovetalk.R;

import java.util.List;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.activity.fragment.GroupAll;
import talk.model.Group;
import talk.model.GroupChatMessage;


/**
 * Created by asus on 2015/11/5.
 */
public class GroupListAdapter extends ArrayAdapter<Group> {
    private int mResource;

    private Group mGroup;

    private TalkApplication mApplication;

    public GroupListAdapter(Context context, int textViewResourceId, List<Group> objects) {
        super(context, textViewResourceId, objects);
        mResource =textViewResourceId;
        mApplication =(TalkApplication)context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mGroup=getItem(position);
        View view;
        ViewHolder holder;
        if (convertView==null){
            view= LayoutInflater.from(getContext()).inflate(mResource,null);
            holder=new ViewHolder();
            holder.name=(TextView)view.findViewById(R.id.name);
            holder.chat=(TextView)view.findViewById(R.id.chat);
            holder.data=(TextView)view.findViewById(R.id.data);
            holder.unReadNum=(TextView)view.findViewById(R.id.unReadNum);
            holder.icon=(ImageView)view.findViewById(R.id.icon);
            holder.unReadIcon=(ImageView)view.findViewById(R.id.unReadIcon);
            holder.chatAll=(LinearLayout)view.findViewById(R.id.chat_all);
            view.setTag(holder);
        }else {
            view=convertView;
            holder=(ViewHolder)convertView.getTag();
        }

        setResource(holder,mGroup);
        return view;
    }
    //-----------------------------布置每个item里面的布局
    private void setResource(ViewHolder holder,Group group){
        //获取该group的最后一条信息
        Log.d("GroupListAdapter", "-------------------------");
        GroupChatMessage chatMessage=mApplication.getGroupMessageDB().getLastChatMessage(group.getGroupId());
        //放置该group的NickName和icon
        holder.name.setText(group.getGroupNick());
        holder.icon.setImageBitmap(BitmapFactory.decodeFile(mGroup.getGroupIcon()));
        //判断该群的groupName是否和其userName相同，若相同则该群为系统群
        boolean isSystemGroup=(group.getGroupId()==GlobleData.SYSTEM);
        Log.d("GroupListAdapter", group.toString());

        //判断当前group的消息是否为空
        if (chatMessage==null){
            //若为空，将未阅读的标志隐藏，将data和chat都设置为空
            holder.unReadNum.setVisibility(View.GONE);
            holder.unReadIcon.setVisibility(View.GONE);
            holder.data.setText("");
            holder.chat.setText("");

        }else {
            //若不为空，则显示未阅读的信息条数，将data和chat都设置好

            //获取该群没有阅读的信息的数量
            int unReadedMsgNum=mApplication.getGroupMessageDB().getUnreadedMsgsCountByGroupId(group.getGroupId());
            //若为0则隐藏未阅读标志
            if (unReadedMsgNum==0){
                holder.unReadNum.setVisibility(View.GONE);
                holder.unReadIcon.setVisibility(View.GONE);
            }else {
                //若不为0则显示未阅读数目
                holder.unReadIcon.setVisibility(View.VISIBLE);
                holder.unReadNum.setVisibility(View.VISIBLE);
                holder.unReadNum.setText(String.valueOf(unReadedMsgNum));

            }
            holder.data.setText(chatMessage.getDateStr());
            if (isSystemGroup){
                //如果是系统Group的话就把姓名去掉
                holder.chat.setText(chatMessage.getMessage());
            }else {
                //将发送过来user的姓名和聊天内容都写入
                holder.chat.setText(mApplication.getUserDB().getMember(chatMessage.getUserId()).getUserNick()+ ":" + chatMessage.getMessage());
            }
        }

        //判断当前是不是系统群
        if (isSystemGroup){
            //若是，则把向下一个Activity传递的groupName设置为-1
            setClick(holder.chatAll,GlobleData.SYSTEM,group);
        }else {
            //若不是，则直接传递groupName
            setClick(holder.chatAll,group.getGroupId(),group);
        }

    }

    private void setClick(LinearLayout chatAll, final int groupId, final Group group){
        final Group group1=mApplication.getGroupDB().getGroup(group.getGroupId());
        if (group1==null) {
            Toast.makeText(mApplication, "该群组已经解散,请刷新列表", Toast.LENGTH_SHORT).show();
            return;
        }
        chatAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (mApplication).map.put("nowGroup",group);

                Intent intent = new Intent(mApplication, GroupAll.class);
                intent.putExtra(GlobleData.GROUP_ID, groupId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mApplication.startActivity(intent);
            }
        });
    }

    class ViewHolder {
        TextView name;
        ImageView unReadIcon;
        TextView  unReadNum;
        TextView chat;
        ImageView icon;
        TextView data;
        LinearLayout chatAll;
    }
}
