package talk.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heshixiyang.ovetalk.R;

import java.util.List;

import talk.Globle.GlobleData;
import talk.model.GroupChatMessage;


public class ChatMessageAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<GroupChatMessage> mDatas;
	private Context mContext;


	public ChatMessageAdapter(Context context, List<GroupChatMessage> datas) {
		mInflater = LayoutInflater.from(context);
		mDatas = datas;
		mContext=context;

    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final GroupChatMessage chatMessage = mDatas.get(position);
		ViewHolder viewHolder ;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (chatMessage.isComing()) {
				convertView = mInflater.inflate(R.layout.main_chat_from_msg,
						parent, false);
				viewHolder.createDate = (TextView) convertView
						.findViewById(R.id.chat_from_createDate);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.chat_from_content);
				viewHolder.nickname = (TextView) convertView
						.findViewById(R.id.chat_from_name);
				viewHolder.img=(ImageView)convertView
						.findViewById(R.id.img);
				viewHolder.agree=(Button)convertView
						.findViewById(R.id.agree);
				viewHolder.disagree=(Button)convertView
						.findViewById(R.id.disagree);

				convertView.setTag(viewHolder);

				viewHolder.isFrom=true;
			} else {
				convertView = mInflater.inflate(R.layout.main_chat_send_msg,
						null);
				viewHolder.createDate = (TextView) convertView
						.findViewById(R.id.chat_send_createDate);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.chat_send_content);
				viewHolder.nickname = (TextView) convertView
						.findViewById(R.id.chat_send_name);
				viewHolder.img=(ImageView)convertView
						.findViewById(R.id.img);
				convertView.setTag(viewHolder);

				viewHolder.isFrom=false;
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		setView(viewHolder,chatMessage);
		return convertView;
	}

	public void setView(ViewHolder viewHolder,GroupChatMessage chatMessage){
		int messageStatu=chatMessage.getMessageStatu();
		// 初始化View状态
		if (viewHolder.isFrom){
			viewHolder.disagree.setVisibility(View.GONE);
			viewHolder.agree.setVisibility(View.GONE);
		}
		viewHolder.img.setVisibility(View.GONE);
		viewHolder.createDate.setText(chatMessage.getDateStr());

		//根据消息的不同 放置nickname 消息1-3和MASTER_PUT_TASK USER_PUT_HOMEWORK 只能在普通群组里使用
		if (messageStatu<=3||messageStatu==GlobleData.MASTER_PUT_TASK||messageStatu==GlobleData.USER_PUT_HOMEWORK){
			viewHolder.nickname.setText(chatMessage.getUserNickName());
		}else {
			//其他的消息只能在system里使用
			viewHolder.nickname.setText("OVEsystem");
		}


		if (messageStatu==GlobleData.PHOTO_MESSAGE){
			//发送图片
			viewHolder.img.setVisibility(View.VISIBLE);
			viewHolder.img.setImageBitmap(BitmapFactory.decodeFile(chatMessage.getMessageImage()));
		}else if (messageStatu==GlobleData.EMOJI_MESSAGE){
			//发送表情

		}else {
			//将消息内容显示
			viewHolder.content.setText(chatMessage.getMessage());
			if (messageStatu==GlobleData.MASTER_PUT_TASK||messageStatu==GlobleData.USER_PUT_HOMEWORK){
				//如果是 任务或者作品 将附上一张图
				viewHolder.img.setVisibility(View.VISIBLE);
				viewHolder.img.setImageBitmap(BitmapFactory.decodeFile(chatMessage.getMessageImage()));

			}else if (messageStatu==GlobleData.USER_REQUEST_JOIN_GROUP){
				//如果是请求加入群里 则显示两个按钮
				viewHolder.agree.setVisibility(View.VISIBLE);
				viewHolder.disagree.setVisibility(View.VISIBLE);
			}
		}

	}
	@Override
	public int getCount()
	{
		return mDatas.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		GroupChatMessage msg = mDatas.get(position);
		return msg.isComing() ? 1 : 0;
	}

	private class ViewHolder {
		public TextView createDate;
		public TextView nickname;
		public TextView content;
		public ImageView img;
		public Button agree;
		public Button disagree;
		public boolean isFrom;
	}


}
