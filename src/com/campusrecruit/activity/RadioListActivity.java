package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.adapter.ListViewRadioListAdapter;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.common.UIHelper;
import com.pcncad.campusRecruit.R;

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

public abstract class RadioListActivity extends BaseActivity {
	ListView listView;
	ListViewRadioListAdapter selectAdapter;
	boolean isAll = false;

	String selectStr = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_base);

		Intent intent = getIntent();
		selectStr = intent
				.getStringExtra("selectStr");
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
			if (selectStr == null) {
				UIHelper.ToastMessage(this, "您还没选择！");
				return true;
			}
			UIHelper.closeRadio(RadioListActivity.this, selectStr);
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
		selectAdapter = new ListViewRadioListAdapter(this, getData(),
				selectStr, R.layout.radio_item);
		listView.setAdapter(selectAdapter);
		Log.i("selection", "init view set adapter ends");
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
					selectStr = getData().get(pos);
					selectAdapter.setSelectStr(selectStr);
					selectAdapter.notifyDataSetChanged();
			}

		});

		Log.i("selection", "init view complete");
	}
}
