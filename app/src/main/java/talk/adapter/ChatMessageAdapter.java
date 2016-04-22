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
import android.widget.LinearLayout;
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
				convertView = mInflater.inflate(R.layout.main_chat_msg,
						parent, false);

				viewHolder.fromCreateDate = (TextView) convertView
						.findViewById(R.id.chat_from_createDate);
				viewHolder.fromContent = (TextView) convertView
						.findViewById(R.id.chat_from_content);
				viewHolder.fromUserNickname = (TextView) convertView
						.findViewById(R.id.chat_from_name);
				viewHolder.fromImg=(NetworkImageView)convertView
						.findViewById(R.id.from_img);
				viewHolder.fromUserIcon=(ImageView)convertView
						.findViewById(R.id.chat_from_icon);

				viewHolder.sendCreateDate = (TextView) convertView
						.findViewById(R.id.chat_send_createDate);
				viewHolder.sendContent= (TextView) convertView
						.findViewById(R.id.chat_send_content);
				viewHolder.sendUserNickname = (TextView) convertView
						.findViewById(R.id.chat_send_name);
				viewHolder.sendImg=(NetworkImageView)convertView
						.findViewById(R.id.send_img);
				viewHolder.sendUserIcon=(ImageView)convertView
						.findViewById(R.id.chat_send_icon);

				viewHolder.agree=(Button) convertView.findViewById(R.id.agree);
				viewHolder.disagree=(Button) convertView.findViewById(R.id.disagree);

				viewHolder.send=(LinearLayout)convertView.findViewById(R.id.send);
				viewHolder.from=(LinearLayout)convertView.findViewById(R.id.from);
				convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		mUser = mApplication.getUserDB().getMember(chatMessage.getUserId());
		setView(viewHolder,chatMessage,position);
		return convertView;
	}
	public void setView(ViewHolder viewHolder,GroupChatMessage chatMessage, final int position){
		int messageStatu=chatMessage.getMessageStatu();
		boolean isComing=chatMessage.isComing();

		viewHolder.disagree.setVisibility(View.GONE);
		viewHolder.agree.setVisibility(View.GONE);
		viewHolder.fromImg.setVisibility(View.GONE);
		viewHolder.sendImg.setVisibility(View.GONE);
		viewHolder.fromCreateDate.setText(chatMessage.getDateStr());

		if (isComing){
			viewHolder.send.setVisibility(View.GONE);
			viewHolder.from.setVisibility(View.VISIBLE);

			if (messageStatu==GlobleData.COMMOM_MESSAGE
					||messageStatu==GlobleData.EMOJI_MESSAGE
					||messageStatu==GlobleData.PHOTO_MESSAGE
					||messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE
					||messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
				if (mUser.getUserIcon()==null){
					mUser.setUserIcon("htt");
				}
				if (mUser.getUserIcon().contains("http")){
					//没有获取成功的userIcon
					viewHolder.fromUserIcon.setImageResource(R.drawable.icon);
				}else {
					viewHolder.fromUserIcon.setImageBitmap(BitmapFactory.decodeFile(mUser.getUserIcon()));
				}
				viewHolder.fromUserNickname.setText(mUser.getUserNick());
				if (messageStatu==GlobleData.PHOTO_MESSAGE){
					setImage(viewHolder,chatMessage);
				}else if (messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE
						||messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
					setImage(viewHolder,chatMessage);
					viewHolder.fromContent.setText(chatMessage.getMessage());
					viewHolder.fromImg.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mAdapterClickListener.onClick((GroupChatMessage)getItem(position));
						}
					});
				}else {
					viewHolder.fromContent.setText(chatMessage.getMessage());
				}
			}else {
				//其他的消息只能在system里使用
				viewHolder.fromUserNickname.setText("OVEsystem");
				viewHolder.fromUserIcon.setImageResource(R.drawable.icon);
				viewHolder.fromContent.setText(chatMessage.getMessage());

				if (messageStatu==GlobleData.USER_REQUEST_JOIN_GROUP){
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
		}else {
			viewHolder.send.setVisibility(View.VISIBLE);
			viewHolder.from.setVisibility(View.GONE);

			if (mUser.getUserIcon()==null){
				mUser.setUserIcon("htt");
			}
			if (mUser.getUserIcon().contains("http")){
				//没有获取成功的userIcon
				viewHolder.sendUserIcon.setImageResource(R.drawable.icon);
			}else {
				viewHolder.sendUserIcon.setImageBitmap(BitmapFactory.decodeFile(mUser.getUserIcon()));
			}
			viewHolder.sendUserNickname.setText(mUser.getUserNick());

			if (messageStatu==GlobleData.PHOTO_MESSAGE){
				viewHolder.sendImg.setVisibility(View.VISIBLE);
				viewHolder.sendImg.setImageBitmap(BitmapFactory.decodeFile(chatMessage.getMessageImage()));
			}else if (messageStatu==GlobleData.MASTER_SEND_TASK_MESSAGE
					||messageStatu==GlobleData.USER_SEND_HOMEWORK_MESSAGE){
				setImage(viewHolder,chatMessage);
				viewHolder.sendContent.setText(chatMessage.getMessage());
				viewHolder.sendImg.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mAdapterClickListener.onClick((GroupChatMessage)getItem(position));
					}
				});
			}else {
				viewHolder.sendContent.setText(chatMessage.getMessage());
			}
		}
	}
	private void setImage(ViewHolder viewHolder,GroupChatMessage chatMessage){
		NetworkImageView networkImageView=null;
		if (chatMessage.isComing()){
			viewHolder.fromImg.setVisibility(View.VISIBLE);
			networkImageView=viewHolder.fromImg;
		}else {
			viewHolder.sendImg.setVisibility(View.VISIBLE);
			networkImageView=viewHolder.sendImg;
		}
		if (mUser.getUserId().equals(mApplication.getGroupDB().getGroup(chatMessage.getGroupId()).getGroupMaster())){
			//是群主
			networkImageView.setImageBitmap(BitmapFactory.decodeFile(chatMessage.getMessageImage()));
			return;
		}
		networkImageView.setDefaultImageResId(R.drawable.icon);
		networkImageView.setErrorImageResId(R.drawable.error);
		networkImageView.setImageUrl(chatMessage.getMessageImage(), new ImageLoader(Volley.newRequestQueue(mApplication), new ImageLoader.ImageCache() {
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
		public TextView fromCreateDate;
		public TextView fromUserNickname;
		public TextView fromContent;
		public NetworkImageView fromImg;
		public ImageView fromUserIcon;

		public TextView sendCreateDate;
		public TextView sendUserNickname;
		public TextView sendContent;
		public NetworkImageView sendImg;
		public ImageView sendUserIcon;

		public Button agree;
		public Button disagree;

		public LinearLayout send;
		public LinearLayout from;
	}

	public interface AdapterClickListener {
		void onClick(GroupChatMessage groupChatMessage);
	}
}
