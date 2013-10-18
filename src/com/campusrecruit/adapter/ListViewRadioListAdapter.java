package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pcncad.campusRecruit.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

public class ListViewRadioListAdapter extends BaseAdapter {
	private Context context;// 运行上下文
	private List<String> listItems;// 数据集合
	private String select;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源

	public static class ListItemView { // 自定义控件集合
		public TextView name;
		public RadioButton radioBtn;
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
	
	public void setSelectStr(String select) {
		this.select = select;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewRadioListAdapter(Context context, List<String> data,
			String select, int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		this.select = select;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Log.d("method", "getView");

		// 自定义视图
		ListItemView listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.name = (TextView) convertView
					.findViewById(R.id.radio_name);
			listItemView.radioBtn = (RadioButton) convertView
					.findViewById(R.id.radio_select);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		listItemView.name.setText(listItems.get(position));
		if (select != null && listItems.get(position).equals(select)) {
			listItemView.radioBtn.setChecked(true);
		} else {
			listItemView.radioBtn.setChecked(false);
		}

		return convertView;
	}

}
