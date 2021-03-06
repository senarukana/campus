package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.campusrecruit.adapter.ListViewUserTopicsAdapter.ListItemView;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.common.BitmapManager;
import com.campusrecruit.common.SmileyParser;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.LinkView;
import com.pcncad.campusRecruit.R;

public class ListViewReplyToOthersAdapter extends BaseAdapter {
	private Context context;
	private AppContext appContext;
	
	private List<BBSReply> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private BitmapManager bmpManager;
	private BBSReply _reply;

	static class ListItemView { // 自定义控件集合
		public View mainLayout;
		public View clickLayout;
		public ImageView userImage;
		public TextView userName;
		public TextView replyTime;
		public LinkView myContent;
		public LinkView othersContent;
		public ImageView flag;
	}

	public void setData(List<BBSReply> data) {
		this.listItems = data;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewReplyToOthersAdapter(Context context, AppContext appContext, int resource) {
		this.context = context;
		this.appContext = appContext;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = new ArrayList<BBSReply>();
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.user_face));
	}

	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("bug","list view reply to");
		// 自定义视图
		ListItemView listItemView = null;
		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.mainLayout = convertView.findViewById(R.id.reply_to_other_main_layout);
			listItemView.clickLayout = convertView.findViewById(R.id.reply_to_other_click_layout);
			listItemView.userImage = (ImageView) convertView
					.findViewById(R.id.reply_to_other_user_face);
			listItemView.userName = (TextView) convertView
					.findViewById(R.id.reply_to_other_user_name);
			listItemView.replyTime = (TextView) convertView
					.findViewById(R.id.reply_to_other_time);
			listItemView.myContent = (LinkView) convertView
					.findViewById(R.id.reply_to_other_my_content);
			listItemView.othersContent = (LinkView) convertView
					.findViewById(R.id.reply_to_other_content);
			listItemView.flag = (ImageView) convertView
					.findViewById(R.id.reply_to_other_today_flag);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		Log.i("test","oooo");
		// 设置文字和图片
		BBSReply reply = listItems.get(position);
		/*
		 * if (topic.getStatus() == 1) {
		 * listItemView.flag.setVisibility(View.VISIBLE); }
		 */
		listItemView.userName.setText(reply.getUserName());
		listItemView.userName.setTag(reply);
		String friendlyTime = null;
		try {
			friendlyTime = StringUtils.friendly_time(reply.getCreatedTime());
		} catch (Exception e) {
			friendlyTime = reply.getCreatedTime();
		}
		listItemView.replyTime.setText(friendlyTime);
		// face
		Log.i("test","test0000");
		SmileyParser.init(context);
		SmileyParser parser = SmileyParser.getInstance();
		Log.i("test","test");
		listItemView.othersContent.setLinkText("回复了["+reply.getUserName()+"]的评论:"+reply.getContent());
		Log.i("test","test1");
		listItemView.othersContent.setText(parser
				.addSmileySpans(listItemView.othersContent.getText()));
		Log.i("test","test2");

		listItemView.myContent.setLinkText("我说："+reply.getMyContent());
		listItemView.myContent.setText(parser
				.addSmileySpans(listItemView.myContent.getText()));
		Log.i("face", "replyto");
		if (appContext != null && appContext.isLogin()
				&& !appContext.getLoginUser().isShowPicture()) {
			listItemView.userImage.setVisibility(View.GONE);
		} else {
			if (reply.getFace() == 0) {
				listItemView.userImage.setImageResource(R.drawable.user_face);
			} else {
				bmpManager
						.loadBitmap(reply.getUserID(), listItemView.userImage);
			}
		}
		listItemView.userImage.setTag(reply);  
		listItemView.userImage.setOnClickListener(faceClickListener);
		listItemView.mainLayout.setOnClickListener(new ReplyListener(reply));
		listItemView.clickLayout.setOnClickListener(new ReplyListener(reply));
		return convertView;
	}
	
	private class ReplyListener implements OnClickListener {
		public ReplyListener(BBSReply reply) {
			this.reply = reply;
		}

		private BBSReply reply;

		@Override
		public void onClick(View v) {
			// 跳转--回复评论界面
			UIHelper.showTopicDetail(context,
					reply.getTopicID());
		}

	}
	
	protected void actionAlertDialog() {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;
		String[] choices = { "用户资料", "发私信" };
		builder = new AlertDialog.Builder(context);
		builder.setTitle("用户");
		builder.setItems(choices, onselect).create();

		alertDialog = builder.create();
		alertDialog.show();

	}

	private DialogInterface.OnClickListener onselect = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (_reply == null) {
				Log.i("bug", "reply by null click!!!!");
				return;
			}
			switch (which) {
			case 0:
				UIHelper.showUserInfo((Activity) context, _reply.getUserID());
				break;
			case 1:
				UIHelper.showPrivateMessageList((Activity) context,
						_reply.getUserID(), _reply.getUserName(), _reply.getCreatedTime());
				break;
			}
		}

	};

	private View.OnClickListener faceClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (!appContext.isLogin()) {
				UIHelper.showLoginDialog((Activity) context);
				return;
			}
			Log.i("bug", "face click");
			BBSReply reply = (BBSReply) v.getTag();
			_reply = reply;
			if (appContext.getLoginUid().equals(reply.getUserID())) {
				UIHelper.ToastMessage(context,
						context.getString(R.string.click_me),
						Toast.LENGTH_SHORT);
				return;
			}
			actionAlertDialog();
		}
	};
}
