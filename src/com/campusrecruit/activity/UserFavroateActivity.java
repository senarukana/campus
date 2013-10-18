package com.campusrecruit.activity;

import com.campusrecruit.adapter.ViewPagerFavorateAdapter;
import com.campusrecruit.adapter.ViewPagerScheduleAdapter;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.fragment.CareerTalkFavoratesFragment;
import com.campusrecruit.fragment.RecruitFavoratesFragment;
import com.pcncad.campusRecruit.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.MenuItem;


public class UserFavroateActivity extends BaseActivity{
	private ViewPager viewPager = null;
	
	private void initTab() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.removeAllTabs();
		actionBar.addTab(actionBar.newTab().setText("校园招聘")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("宣讲会")
				.setTabListener(tabListener));
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Log.i("test","init user favorate");
		setContentView(R.layout.user_favorate_main_layout);
		initView();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return false;
		}
	}
	
	public void initView(){
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("您的收藏");
		
		viewPager = (ViewPager) findViewById(R.id.user_favorate_viewPager);
		
		ViewPagerFavorateAdapter adapter = new ViewPagerFavorateAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		viewPager.setOnPageChangeListener(onPageChangeListener);
		
		initTab();
	}
	
	private SimpleOnPageChangeListener onPageChangeListener = new SimpleOnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			getActionBar().setSelectedNavigationItem(position);
			
		}

	};
	
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
