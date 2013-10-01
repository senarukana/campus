package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.adapter.ViewPagerScheduleAdapter;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.Schedules;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.fragment.ScheduleFragment;
import com.campusrecruit.fragment.ScheduleListFragment;
import com.krislq.sliding.R;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.MenuItem;

public class ScheduleActivity extends FragmentActivity {

	private ViewPager viewPager = null;
	private AppContext appContext;

	public static ScheduleFragment mScheduleFragment = null;
	public static ScheduleListFragment mScheduleListFragment = null;
	
	public static List<Schedules> scheduleList = new ArrayList<Schedules>();

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.schedule_main_layout);
		initFragment();
		initView();
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

	public void initFragment() {
		mScheduleFragment = new ScheduleFragment();
		mScheduleListFragment = new ScheduleListFragment();

	}

	public void initView() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("日程安排");

		appContext = (AppContext) getApplication();
		viewPager = (ViewPager) findViewById(R.id.schedule_viewPager);

		ViewPagerScheduleAdapter adapter = new ViewPagerScheduleAdapter(
				getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		viewPager.setOnPageChangeListener(onPageChangeListener);

		initTab();
	}

	private void initTab() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.removeAllTabs();
		actionBar.addTab(actionBar.newTab().setText("日程列表")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("日历")
				.setTabListener(tabListener));
	}

	private SimpleOnPageChangeListener onPageChangeListener = new SimpleOnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			getActionBar().setSelectedNavigationItem(position);
		}

	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == UIHelper.REQUEST_CODE_FOR_SCHEDULE
				&& resultCode == RESULT_CANCELED) {
			Log.i("test","result");
//			mScheduleFragment = new ScheduleFragment();
//			mScheduleListFragment.mAdapter.notifyDataSetChanged();
		}
	}

	private TabListener tabListener = new TabListener() {

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {

		}

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			if (viewPager.getCurrentItem() != tab.getPosition())
				viewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {

		}

	};

}
