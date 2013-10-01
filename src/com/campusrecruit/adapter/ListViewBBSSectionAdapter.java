package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.bean.BBSSection;
import com.krislq.sliding.R;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ListViewBBSSectionAdapter extends BaseAdapter {
	private List<BBSSection> 			listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 
	static class ListItemView{				//自定义控件集合  
			public ImageView todayImage;
			public ImageView isJoinedImage;
			public ImageView famousImage;
	        public TextView sectionName;  
	        public TextView companyType;
	        public TextView companyIndustry;
		    public TextView topics;
		    public ImageView topicsImage;
		    public TextView joins;  
	 }  

	public void setData(List<BBSSection> data) {
		this.listItems = data;
	}
	
	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewBBSSectionAdapter(Context context, int resource) {
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.listItems = new ArrayList<BBSSection>();
		this.itemViewResource = resource;
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
			listItemView.todayImage = (ImageView)convertView.findViewById(R.id.bbssection_item_flag);
			listItemView.isJoinedImage = (ImageView)convertView.findViewById(R.id.bbssection_isjoined_image);
			listItemView.famousImage = (ImageView)convertView.findViewById(R.id.bbssection_famous_flag);
			listItemView.sectionName = (TextView)convertView.findViewById(R.id.bbssection_company_name);
			listItemView.topics = (TextView)convertView.findViewById(R.id.bbssection_item_topic_count);
			listItemView.companyIndustry = (TextView)convertView.findViewById(R.id.bbssection_company_industry);
			listItemView.companyType = (TextView)convertView.findViewById(R.id.bbssection_company_type);
			listItemView.topicsImage = (ImageView)convertView.findViewById(R.id.bbssection_item_topic_count_image);
			listItemView.joins = (TextView)convertView.findViewById(R.id.bbssection_item_joins);
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		//设置文字和图片
		BBSSection section = listItems.get(position);
		if (section.getStatus() != 1) {
			listItemView.topics.setText(section.getTopics() + "");
			listItemView.joins.setText(section.getJoins() + "");
			listItemView.todayImage.setVisibility(View.INVISIBLE);
		} else{
			listItemView.todayImage.setVisibility(View.VISIBLE);
			listItemView.joins.setText("?");
			listItemView.topics.setText("?");
		}
		
		if (section.getFamous() == 1) {
			listItemView.famousImage.setVisibility(View.VISIBLE);
		} else {
			listItemView.famousImage.setVisibility(View.INVISIBLE);
		}
		if (section.getIsJoined() == 1) {
			listItemView.isJoinedImage.setImageResource(R.drawable.user_favorate1);
		} else {
			listItemView.isJoinedImage.setImageResource(R.drawable.user_favorate);
		}
		listItemView.sectionName.setText(section.getSectionName());
		listItemView.sectionName.setTag(section);//设置隐藏参数(实体类)
		listItemView.companyIndustry.setText(section.getCompanyIndustry());
		listItemView.companyType.setText(section.getCompanyType());
		return convertView;
	}
}