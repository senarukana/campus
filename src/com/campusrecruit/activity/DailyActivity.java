package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.campusrecruit.adapter.ListViewScheduleAdapter;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.Schedule;
import com.campusrecruit.bean.Schedules;
import com.campusrecruit.widget.PullToRefreshListView;
import com.pcncad.campusRecruit.R;

import android.R.anim;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DailyActivity extends BaseActivity {

	private PullToRefreshListView pvScheudle = null;
	private ArrayList<Schedules> scheduleList = new ArrayList<Schedules>();
	ListViewScheduleAdapter scheduleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("schedule", "1");
		setContentView(R.layout.schedule_day_list);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("安排");
		Log.i("schedule", "2");
		Intent intent = getIntent();
		Log.i("schedule", "3");
		scheduleList = (ArrayList<Schedules>) intent
				.getSerializableExtra("schedule_list");
		Log.i("schedule", "4");
		if (scheduleList.isEmpty())
			setContentView(R.layout.schedule_daily_main_empty);
		else
			initListView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;

		default:
			return false;
		}
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < scheduleList.size(); i++) {
			String name = scheduleList.get(i).getCompanyName();
			String place = scheduleList.get(i).getPlace();
			String time = scheduleList.get(i).getDate();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("sequence", i + 1);
			map.put("date", time);
			map.put("place", place);
			map.put("name", name);
			map.put("time", time);
			list.add(map);
		}
		pvScheudle.onRefreshComplete();
		return list;
	}

	/*
	 * @Override public void onBackPressed() { super.onBackPressed(); Intent
	 * intent = getIntent(); if (scheduleAdapter.dataChanged) {
	 * setResult(RESULT_CANCELED, intent); } finish(); }
	 */

	public void initListView() {
		Log.i("schedule","5");
		pvScheudle = (PullToRefreshListView) findViewById(R.id.schedule_day_listview);
		scheduleAdapter = new ListViewScheduleAdapter(this,
				(AppContext) getApplication(),
				R.layout.schedule_day_item);
		scheduleAdapter.setData(scheduleList);
		
		pvScheudle.setOnRefreshListener(new refreshListener());
		/*
		 * SimpleAdapter adapter = new SimpleAdapter(this, getData(),
		 * R.layout.schedule_daily_main_item, new String[]{"sequence", "date",
		 * "name", "time", "place"}, new
		 * int[]{R.id.schedule_sequence,R.id.schedule_date,
		 * R.id.schedule_company_name, R.id.schedule_time,
		 * R.id.schedule_place}); pvScheudle.setAdapter(adapter);
		 */
		pvScheudle.setAdapter(scheduleAdapter);
	}

	private class refreshListener implements
			PullToRefreshListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			getData();
		}

	}

}
