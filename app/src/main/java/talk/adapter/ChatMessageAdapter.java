package talk.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
	private OnCallBackDialog mCallBackDialog;
	private OnCallBackMore mCallBackMore;


	public ChatMessageAdapter(Context context, List<GroupChatMessage> datas) {
		mInflater = LayoutInflater.from(context);
		mDatas = datas;
		mContext=context;
		mCallBackDialog=(OnCallBackDialog)mContext;
		mCallBackMore=(OnCallBackMore)mContext;

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
				viewHolder.isChoose=false;
				viewHolder.chooseWhich=-1;
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

		//当点击converView的时候，让软键盘和More收缩
		final View view=convertView;
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					im.hideSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

					mCallBackMore.callBackMore();
				}
				return false;
			}
		});

		int messageStatu=Integer.parseInt(chatMessage.getMessageImage());
        viewHolder.img.setVisibility(View.GONE);
		if (viewHolder.isFrom){
			//一开始就把Button隐藏起来
			viewHolder.agree.setVisibility(View.GONE);
			viewHolder.disagree.setVisibility(View.GONE);
		}

		if (messageStatu<=1){
			switch (messageStatu){
				case 0:
					//如果是0则是普通的信息，那么隐藏img和Button，将message的信息显示在content上
					viewHolder.content.setText(chatMessage.getMessage());

					break;
				case 1:
					//如果messageIamge等于1则该信息是视频，那么让img显示，然后message中是视频的name，然后播放视频。

					viewHolder.content.setText("我分享了,视屏快来看看吧!");
					viewHolder.img.setImageResource(R.drawable.icon);
					viewHolder.img.setVisibility(View.VISIBLE);

					viewHolder.img.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
//							Intent intent = new Intent(mContext, VideoDetailsActivity.class);
//							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							GlobalVariable.currentId = chatMessage.getMessage();
//							GlobalVariable.currentType = "1";
//							mContext.startActivity(intent);
						}
					});
					break;
				default:
					break;
			}
			viewHolder.nickname.setText(chatMessage.getUserNickName());
		}else {
			//如果是申请加入群组的信息，就显示Button
			if (messageStatu== GlobleData.USER_REQUEST_JOIN_GROUP){
				//如果请求的按钮没有被点击过
				viewHolder.agree.setVisibility(View.VISIBLE);
				viewHolder.disagree.setVisibility(View.VISIBLE);
				viewHolder.agree.setText("同意");
				viewHolder.disagree.setText("拒绝");
				viewHolder.agree.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mCallBackDialog.callBackDialog(true, chatMessage.getDateStr());
					}
				});

                viewHolder.disagree.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mCallBackDialog.callBackDialog(false, chatMessage.getDateStr());

					}
				});

            }else if (messageStatu==GlobleData.MASTER_AGREE){
				viewHolder.agree.setVisibility(View.VISIBLE);
                viewHolder.agree.setText("已允许其加入");

			}else if (messageStatu==GlobleData.MASTER_DISAGREE){
                viewHolder.disagree.setVisibility(View.VISIBLE);
                viewHolder.disagree.setText("已拒绝其加入");
			}

			//否则就直接放message
			viewHolder.content.setText(chatMessage.getMessage());
			viewHolder.img.setVisibility(View.GONE);
			viewHolder.nickname.setText("OVESystem");
		}

		//在item中布局date
		viewHolder.createDate.setText(chatMessage.getDateStr());
		viewHolder.createDate.setGravity(Gravity.CENTER);
		return convertView;
	}

	private class ViewHolder {
		public TextView createDate;
		public TextView nickname;
		public TextView content;
		public ImageView img;
		public Button agree;
		public Button disagree;
		public boolean isFrom;
		public boolean isChoose;
		public int chooseWhich;
	}

	public interface OnCallBackMore{
		public void callBackMore();
	}

	public interface OnCallBackDialog{
		public void callBackDialog(boolean isAgree, String time);
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
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public int getItemViewType(int position)
	{
		GroupChatMessage msg = mDatas.get(position);
		return msg.isComing() ? 1 : 0;
	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	}

}
