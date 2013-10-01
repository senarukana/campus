package com.campusrecruit.adapter;

import java.util.List;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.common.BitmapManager;
import com.campusrecruit.common.SmileyParser;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.LinkView;
import com.krislq.sliding.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewReplyAdapter extends BaseAdapter {
	private AppContext appContext;
	private Context context;// 运行上下文
	private List<BBSReply> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private BitmapManager bmpManager;
	private BBSReply _reply;

	static class ListItemView { // 自定义控件集合
		public ImageView face;
		public TextView name;
		public TextView date;
		public LinkView content;
		private TextView floor;
		// public LinearLayout replies;
		public LinearLayout refers;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewReplyAdapter(Context context, AppContext appContext,
			List<BBSReply> data, int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		this.appContext = appContext;
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

	public void setData(List<BBSReply> data) {
		this.listItems = data;
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
			listItemView.face = (ImageView) convertView
					.findViewById(R.id.comment_listitem_userface);
			listItemView.name = (TextView) convertView
					.findViewById(R.id.comment_listitem_username);
			listItemView.date = (TextView) convertView
					.findViewById(R.id.comment_listitem_date);
			listItemView.content = (LinkView) convertView
					.findViewById(R.id.comment_listitem_content);
			// listItemView.content =
			// (TextView)convertView.findViewById(R.id.comment_listitem_content);
			listItemView.floor = (TextView) convertView
					.findViewById(R.id.comment_listitem_floor);
			// listItemView.replies =
			// (LinearLayout)convertView.findViewById(R.id.comment_listitem_replies);
			listItemView.refers = (LinearLayout) convertView
					.findViewById(R.id.comment_listitem_refers);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		// 设置文字和图片
		BBSReply bbsReply = listItems.get(position);
		Log.i("face", "begin load face");
		if (appContext == null) {
			Log.i("face", "fuck!!!!");
		}
		if (appContext != null && appContext.isLogin()
				&& !appContext.getLoginUser().isShowPicture()) {
			listItemView.face.setVisibility(View.GONE);
		} else {
			if (bbsReply.getFace() == 0) {
				listItemView.face.setImageResource(R.drawable.user_face);
			} else {
				bmpManager.loadBitmap(bbsReply.getUserID(), listItemView.face);
			}

		}
		Log.i("face", "load face complete");
		listItemView.face.setTag(bbsReply);// 设置隐藏参数(实体类)
		listItemView.face.setOnClickListener(faceClickListener);
		listItemView.name.setText(bbsReply.getUserName());
		listItemView.date.setText(StringUtils.friendly_time(bbsReply
				.getCreatedTime()));
		listItemView.content.setLinkText(bbsReply.getContent());
		
		SmileyParser.init(this.context);
		SmileyParser parser = SmileyParser.getInstance();
		Log.i("test", bbsReply.getContent());
		listItemView.content.setText(parser.addSmileySpans(listItemView.content
				.getText()));
		listItemView.content.setTag(bbsReply);// 设置隐藏参数(实体类)
		if (bbsReply.getStatus() == 0) {
			listItemView.floor.setText((position + 1) + "楼");
		} else {
			listItemView.floor.setText("New");
		}

		listItemView.refers.setVisibility(View.GONE);
		listItemView.refers.removeAllViews();// 先清空
		
		
		
		if (bbsReply.getReplies().size() != 0) {
			// 引用内容
			for (BBSReply reply : bbsReply.getReplies()) {
				View view = listContainer.inflate(R.layout.comment_refer, null);
				TextView title = (TextView) view
						.findViewById(R.id.comment_refer_title);
				LinkView body = (LinkView) view
						.findViewById(R.id.comment_refer_body);
				title.setText("回复 " + reply.getUserName());
				body.setText(reply.getContent());
				body.setText(parser.addSmileySpans(body.getText()));
				listItemView.refers.addView(view);
			}
			listItemView.refers.setVisibility(View.VISIBLE);
		}

		return convertView;
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

	private OnClickListener onselect = new OnClickListener() {
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
						_reply.getUserID(), _reply.getUserName(),
						_reply.getCreatedTime());
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