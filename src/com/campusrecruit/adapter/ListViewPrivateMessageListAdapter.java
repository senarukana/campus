package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.adapter.ListViewUserMessageAdapter.ListItemView;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.UserMessage;
import com.campusrecruit.common.BitmapManager;
import com.campusrecruit.common.SmileyParser;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.LinkView;
import com.krislq.sliding.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ListViewPrivateMessageListAdapter extends BaseAdapter {

	private Context context;
	private List<UserMessage> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private BitmapManager bmpManager;
	private AppContext appContext;

	static class ListItemView { // 自定义控件集合
		public LinearLayout layout;
		public ImageView newFlag;
		public ImageView userImage;
		public ImageView userImageAnother;
		public TextView userName;
		public TextView createdTime;
		public LinkView content;
	}

	public void setData(List<UserMessage> data) {
		this.listItems = data;
	}

	public ListViewPrivateMessageListAdapter(Context context,
			AppContext appContext, int resource) {
		this.context = context;
		this.appContext = appContext;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = new ArrayList<UserMessage>();
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.user_face));
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("test", "private list adapter11");
		ListItemView listItemView = null;
		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.layout = (LinearLayout) convertView
					.findViewById(R.id.private_message_list_item_layout);
			listItemView.newFlag = (ImageView) convertView
					.findViewById(R.id.private_message_list_item_new_flag);
			listItemView.userImage = (ImageView) convertView
					.findViewById(R.id.private_message_list_item_userface);
			listItemView.userName = (TextView) convertView
					.findViewById(R.id.private_message_list_item_username);
			listItemView.content = (LinkView) convertView
					.findViewById(R.id.comment_listitem_content);
			listItemView.createdTime = (TextView) convertView
					.findViewById(R.id.private_message_list_item_date);
			listItemView.userImageAnother = (ImageView) convertView
					.findViewById(R.id.private_message_list_item_userface_another);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		Log.i("test", "private list adapter");
		UserMessage message = listItems.get(position);
		if (message.getStatus() == 1) {
			listItemView.newFlag.setVisibility(View.VISIBLE);
		} else {
			listItemView.newFlag.setVisibility(View.INVISIBLE);
		}
		Log.i("test", "private list adapter middle");
		if (message.getUserID().equals(appContext.getLoginUid())) {
			int marginRight = 150 -  message.getContent().length() * 10;
			if (marginRight < 0)
				marginRight = 0;
			LinearLayout.LayoutParams lpp = (LinearLayout.LayoutParams) listItemView.layout
					.getLayoutParams();
			lpp.setMargins(10, 0, marginRight, 0);
			listItemView.layout.setLayoutParams(lpp);
			listItemView.layout
					.setBackgroundResource(R.drawable.review_bg_blue);
			listItemView.userImageAnother.setVisibility(View.GONE);
			listItemView.userName.setText("我");
			if (appContext != null && appContext.isLogin()
					&& !appContext.getLoginUser().isShowPicture()) {
				listItemView.userImage.setVisibility(View.GONE);
			} else {
				if (appContext.getLoginUser().getHasFace() == 0) {
					listItemView.userImage
							.setImageResource(R.drawable.user_face);
				} else {
					bmpManager.loadBitmap(appContext.getLoginUid(),
							listItemView.userImage);
				}
			}
		} else {
			int marginLeft = 150 - message.getContent().length() * 10;
			if (marginLeft < 0) {
				marginLeft = 0;
			}
			LinearLayout.LayoutParams lpp = (LinearLayout.LayoutParams) listItemView.layout
					.getLayoutParams();
			lpp.setMargins(marginLeft, 0, 10, 0);
			listItemView.layout.setLayoutParams(lpp);
			listItemView.layout
					.setBackgroundResource(R.drawable.review_bg_gray);
			listItemView.userImage.setVisibility(View.GONE);
			listItemView.userName.setText(message.getUserName());
			if (appContext != null && appContext.isLogin()
					&& !appContext.getLoginUser().isShowPicture()) {
				listItemView.userImageAnother.setVisibility(View.GONE);
			} else {
				if (appContext.getLoginUser().getHasFace() == 0) {
					listItemView.userImageAnother
							.setImageResource(R.drawable.user_face);
				} else {
					bmpManager.loadBitmap(message.getUserID(),
							listItemView.userImageAnother);
				}
			}
		}
		Log.i("test", "private list adapter 123");
		String friendlyTime;
		try {
			friendlyTime = StringUtils.friendly_time(message.getCreatedTime());
		} catch (Exception e) {
			friendlyTime = message.getCreatedTime();
		}
		listItemView.createdTime.setText(friendlyTime);
		// face
		SmileyParser.init(context);
		SmileyParser parser = SmileyParser.getInstance();
		listItemView.content.setLinkText(message.getContent());
		listItemView.content.setText(parser.addSmileySpans(listItemView.content
				.getText()));
		Log.i("test", "okokoko complete");
		return convertView;
	}

}
