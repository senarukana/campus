package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.campusrecruit.adapter.ListViewUserTopicsAdapter.ListItemView;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.krislq.sliding.R;

public class CopyOfListViewReplyToOthersAdapter extends BaseAdapter{
	private List<BBSReply> 				listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 
	static class ListItemView{				//自定义控件集合  
			public ImageView userImage;
			public TextView userName;
			public TextView replyTime;
	        public TextView myContent; 
		    public TextView othersContent;  
		    public ImageView flag;
	 }  
	
	public void setData(List<BBSReply> data) {
		this.listItems = data;
	}

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public CopyOfListViewReplyToOthersAdapter(Context context, int resource) {
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = new ArrayList<BBSReply>();
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
		//自定义视图
		ListItemView  listItemView = null;
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.userImage = (ImageView)convertView.findViewById(R.id.reply_by_other_user_face);
			listItemView.userName = (TextView)convertView.findViewById(R.id.reply_by_other_user_name);
			listItemView.replyTime = (TextView)convertView.findViewById(R.id.reply_by_other_time);
			listItemView.myContent= (TextView)convertView.findViewById(R.id.reply_by_other_my_content);
			listItemView.othersContent= (TextView)convertView.findViewById(R.id.reply_by_other_content);
			listItemView.flag= (ImageView)convertView.findViewById(R.id.reply_by_other_today_flag);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		//设置文字和图片
		BBSReply reply = listItems.get(position);
/*		if (topic.getStatus() == 1) {
			listItemView.flag.setVisibility(View.VISIBLE);
		}*/
	
		listItemView.userName.setText(reply.getUserName());
		listItemView.userName.setTag(reply);
		listItemView.replyTime.setText(reply.getCreatedTime());
		listItemView.othersContent.setText(reply.getContent());
		listItemView.myContent.setText(reply.getMyContent());
		listItemView.replyTime.setText(reply.getCreatedTime());
		return convertView;
	}
}