package talk.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.heshixiyang.ovetalk.R;

import java.util.List;

import talk.Globle.GlobleData;
import talk.TalkApplication;
import talk.model.GroupChatMessage;
import talk.model.User;

public class ChatMessageAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<GroupChatMessage> mDatas;
	private TalkApplication mApplication;
	private AdapterClickListener mAdapterClickListener;
	private User mUser;

	public ChatMessageAdapter(Context context, List<GroupChatMessage> datas,AdapterClickListener mAdapterClickListener) {
		mInflater = LayoutInflater.from(context);
		mDatas = datas;
		mApplication =(TalkApplication)context;
		this.mAdapterClickListener = mAdapterClickListener;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupChatMessage chatMessage = mDatas.get(position);
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
				viewHolder.img=(NetworkImageView)convertView
						.findViewById(R.id.img);
				viewHolder.agree=(Button)convertView
						.findViewById(R.id.agree);
				viewHolder.disagree=(Button)convertView
						.findViewById(R.id.disagree);
				viewHolder.userIcon=(ImageView)convertView
						.findViewById(R.id.chat_from_icon);

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
				viewHolder.img=(NetworkImageView)convertView
						.findViewById(R.id.img);
				viewHolder.userIcon=(ImageView)convertView
						.findViewById(R.id.chat_send_icon);

				convertView.setTag(viewHolder);

				viewHolder.isFrom=false;
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		mUser = mApplication.getUserDB().getMember(chatMessage.getUserId());
		setView(viewHolder,chatMessage,position);
		return convertView;
	}
	public void setView(ViewHolder viewHolder,GroupChatMessage chatMessage, final int position){
		int messageStatu=chatMessage.getMessageStatu();

		// 初始化View状态
		if (viewHolder.isFrom){
			viewHolder.disagree.setVisibility(View.GONE);
			viewHolder.agree.setVisibility(View.GONE);
		}
		viewHolder.img.setVisibility(View.GONE);
		viewHolder.createDate.setText(chatMessage.getDateStr());

		//根据消息的不同 放置nickname 消息1-3和MASTER_PUT_TASK USER_SEND_HOMEWORK_MESSAGE 只能在普通群组里使用
		if (messageStatu<=3||messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE ||messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
			if (mUser.getUserIcon().contains("http")){
				//没有获取成功的userIcon
				viewHolder.userIcon.setImageResource(R.drawable.icon);
			}else {
				viewHolder.userIcon.setImageBitmap(BitmapFactory.decodeFile(mUser.getUserIcon()));
			}
			viewHolder.nickname.setText(mUser.getUserNick());
		}else {
			//其他的消息只能在system里使用
			viewHolder.nickname.setText("OVEsystem");
			viewHolder.userIcon.setImageResource(R.drawable.icon);
		}

		if (messageStatu==GlobleData.PHOTO_MESSAGE){
			setImage(viewHolder,chatMessage);
		}else if (messageStatu==GlobleData.EMOJI_MESSAGE){

		}else {
			//将消息内容显示
			viewHolder.content.setText(chatMessage.getMessage());
			if (messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE ||messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
				//如果是 任务或者作品 将附上一张图
				setImage(viewHolder,chatMessage);
				viewHolder.img.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mAdapterClickListener.onClick((GroupChatMessage)getItem(position));
					}
				});
			}else if (messageStatu==GlobleData.USER_REQUEST_JOIN_GROUP){
				//如果是请求加入群里 则显示两个按钮
				viewHolder.agree.setVisibility(View.VISIBLE);
				viewHolder.disagree.setVisibility(View.VISIBLE);
				viewHolder.agree.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mAdapterClickListener.onClick((GroupChatMessage)getItem(position));
					}
				});
				viewHolder.disagree.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mAdapterClickListener.onClick((GroupChatMessage)getItem(position));
					}
				});
			}
		}
	}
	private void setImage(ViewHolder viewHolder,GroupChatMessage chatMessage){
		viewHolder.img.setVisibility(View.VISIBLE);
		viewHolder.img.setDefaultImageResId(R.drawable.icon);
		viewHolder.img.setErrorImageResId(R.drawable.error);
		viewHolder.img.setImageUrl(chatMessage.getMessageImage(), new ImageLoader(Volley.newRequestQueue(mApplication), new ImageLoader.ImageCache() {
			@Override
			public Bitmap getBitmap(String s) {
				return null;
			}

			@Override
			public void putBitmap(String s, Bitmap bitmap) {

			}
		}));
	}

	@Override
	public int getCount()
	{
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
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
		public NetworkImageView img;
		public Button agree;
		public Button disagree;
		public boolean isFrom;
		public ImageView userIcon;
	}

	public interface AdapterClickListener {
		void onClick(GroupChatMessage groupChatMessage);
	}
}
