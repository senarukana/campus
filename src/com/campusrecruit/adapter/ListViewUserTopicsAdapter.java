package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.common.StringUtils;
import com.krislq.sliding.R;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ListViewUserTopicsAdapter extends BaseAdapter {
	private List<BBSTopic> 				listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 
	static class ListItemView{				//自定义控件集合  
	        public TextView title; 
		    public TextView date;  
		    public ImageView flag;
	 }  
	
	public void setData(List<BBSTopic> data) {
		this.listItems = data;
	}

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewUserTopicsAdapter(Context context, int resource) {
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = new ArrayList<BBSTopic>();
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
			listItemView.title = (TextView)convertView.findViewById(R.id.user_topics_item_title);
			listItemView.date= (TextView)convertView.findViewById(R.id.user_topics_item_date);
			listItemView.flag= (ImageView)convertView.findViewById(R.id.user_topics_item_flag);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		//设置文字和图片
		BBSTopic topic = listItems.get(position);
		if (topic.getStatus() == 1) {
			listItemView.flag.setVisibility(View.VISIBLE);
		}
		listItemView.title.setText(topic.getTitle());
		listItemView.title.setTag(topic.getTopicID());//设置隐藏参数(实体类)
		String friendlyTime = null;
		try {
			friendlyTime = StringUtils.friendly_time(topic.getCreatedTime());
		} catch (Exception e) {
			friendlyTime = topic.getCreatedTime();
		}
		listItemView.date.setText(friendlyTime);
		return convertView;
	}
}