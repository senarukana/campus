package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.krislq.sliding.R;

public class ListViewSelectListAdapter extends BaseAdapter{
	private Context 					context;//运行上下文
	private List<String> 				listItems;//数据集合
	private ArrayList<Integer> 			selectList;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 
	private static HashMap<Integer, Boolean> isSelected;
	
	
	public static class ListItemView{				//自定义控件集合  
	        public TextView name;  
		    public CheckBox checkBox;
	 }  
	
	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewSelectListAdapter(Context context, List<String> data, ArrayList<Integer> select,	int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		this.selectList = select;
		isSelected = new HashMap<Integer, Boolean>();
		initData();
	}
	
	private void initData(){
		Log.i("selection","init data" + listItems.size() + "");
		for(int i = 0; i < listItems.size(); i++){
			if(selectList.contains(i)){
				isSelected.put(i, true);
			}else{
				isSelected.put(i, false);
			}
		}
		Log.i("selection","init data complete");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("method", "getView");
		
		//自定义视图
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.name = (TextView)convertView.findViewById(R.id.select_name);
			listItemView.checkBox = (CheckBox)convertView.findViewById(R.id.select_cb);	
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		listItemView.name.setText(listItems.get(position));
		listItemView.checkBox.setChecked(isSelected.get(position));
		
		return convertView;
	}
	
	
	public static HashMap<Integer, Boolean> getIsSelected(){
		return isSelected;
	}
}
