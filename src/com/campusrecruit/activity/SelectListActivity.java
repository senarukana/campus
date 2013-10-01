package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.adapter.ListViewSelectListAdapter;
import com.campusrecruit.adapter.ListViewSelectListAdapter.ListItemView;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.common.UIHelper;
import com.krislq.sliding.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public abstract class SelectListActivity extends BaseActivity {
	ListView listView;
	ListViewSelectListAdapter selectAdapter;
	boolean isAll = false;

	ArrayList<Integer> selectedList = new ArrayList<Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("test","init select list");
		setContentView(R.layout.list_base);

		Intent intent = getIntent();
		ArrayList<Integer> list = intent
				.getIntegerArrayListExtra("selectedList");
		selectedList.clear();
		if (list != null) {
			for (Integer i : list) {
				selectedList.add(i);
			}
		}
		Log.i("selection", "initview");
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_finish, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_finish_id:
			String text = null;
			if (selectedList.size() == 1 && !selectedList.contains(0)) {
				text = getData().get(0);
			} 
			UIHelper.closeSelection(SelectListActivity.this, selectedList, text);
			break;
		default:
			break;
		}
		return true;
	}

	protected abstract List<String> getData();

	private void initView() {
		listView = (ListView) findViewById(R.id.list_base);
		Log.i("selection", "new adapter");
		selectAdapter = new ListViewSelectListAdapter(this, getData(),
				selectedList, R.layout.select_item);
		listView.setAdapter(selectAdapter);
		Log.i("selection", "init view set adapter ends");
		if (selectedList.contains(0)) {
			for (int i = 0; i < listView.getCount(); i++) {
				ListViewSelectListAdapter.getIsSelected().put(i, true);
			}
			selectAdapter.notifyDataSetChanged();
			selectedList.clear();
			selectedList.add(0);
		}
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Log.i("selection","onitem click");
				if (pos == 0) {
					Log.i("java", listView.getCount() + " count");
					if (!ListViewSelectListAdapter.getIsSelected().get(0)) {
						for (int i = 0; i < listView.getCount(); i++) {
							ListViewSelectListAdapter.getIsSelected().put(i, true);
						}
						selectAdapter.notifyDataSetChanged();
						selectedList.clear();
						selectedList.add(0);
					} else {
						for (int i = 0; i < listView.getCount(); i++) {
							ListViewSelectListAdapter.getIsSelected().put(i, false);
						}
						selectAdapter.notifyDataSetChanged();
						selectedList.clear();
					}
				} else {
					Log.i("selection","onitem click event");
					if (selectedList.contains(0)) {
						selectedList.remove(0);
						for (int i = 1; i < listView.getCount(); i++) {
							selectedList.add(i);
						}
						ListViewSelectListAdapter.getIsSelected().put(0, false);
						selectAdapter.notifyDataSetChanged();
					}
					Log.i("selection","onitem click set status");
					ListItemView holder = (ListItemView) view.getTag();
					Log.i("selection","onitem click set status 2");
					// 改变checkbox的状态
					holder.checkBox.toggle();
					Log.i("selection","onitem click set status 3");
					ListViewSelectListAdapter.getIsSelected().put(pos,
							holder.checkBox.isChecked());
					Log.i("selection","onitem click set status 4");
					if (holder.checkBox.isChecked()) {
						selectedList.add(pos);
					} else {
						selectedList.remove((Integer) pos);
					}
					Log.i("selection","onitem click set status 5");
				}

			}

		});

		Log.i("selection", "init view complete");
	}
}
