package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.campusrecruit.adapter.ListViewUserTopicsAdapter.ListItemView;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.UserMessage;
import com.campusrecruit.common.BitmapManager;
import com.campusrecruit.common.SmileyParser;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.LinkView;
import com.pcncad.campusRecruit.R;

public class ListViewUserMessageAdapter extends BaseAdapter {
	private Context context;
	private AppContext appContext;
	
	private List<UserMessage> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private BitmapManager bmpManager;

	static class ListItemView { // 自定义控件集合
		public ImageView userImage;
		public TextView userName;
		public TextView createdTime;
		public LinkView content;
	}

	public void setData(List<UserMessage> data) {
		this.listItems = data;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewUserMessageAdapter(Context context, AppContext appContext,
			int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.appContext = appContext;
		this.listItems = new ArrayList<UserMessage>();
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
		// 自定义视图
		ListItemView listItemView = null;
		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.userImage = (ImageView) convertView
					.findViewById(R.id.private_message_user_face);
			listItemView.userName = (TextView) convertView
					.findViewById(R.id.private_message_user_name);
			listItemView.content = (LinkView) convertView
					.findViewById(R.id.private_message_content);
			listItemView.createdTime = (TextView) convertView
					.findViewById(R.id.private_message_time);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		Log.i("test", "user message adapter");
		// 设置文字和图片
		UserMessage message = listItems.get(position);
		listItemView.userName.setText(message.getUserName());
		listItemView.userName.setTag(message);
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

		listItemView.content.setText(message.getContent());
		listItemView.content.setText(parser
				.addSmileySpans(listItemView.content.getText()));
		if (appContext != null && appContext.isLogin()
				&& !appContext.getLoginUser().isShowPicture()) {
			listItemView.userImage.setVisibility(View.GONE);
		} else {
			if (message.getFace() == 0) {
				Log.i("test","show picture1");
				listItemView.userImage.setImageResource(R.drawable.user_face);
			} else {
				Log.i("test","show picture2");
				bmpManager
						.loadBitmap(message.getUserID(), listItemView.userImage);
			}
		}
		return convertView;
	}
}
