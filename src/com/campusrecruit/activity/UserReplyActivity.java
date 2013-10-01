package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.adapter.ViewPagerFavorateAdapter;
import com.campusrecruit.adapter.ViewPagerScheduleAdapter;
import com.campusrecruit.adapter.ViewPagerUserMessageAdapter;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.fragment.BaseFragment;
import com.campusrecruit.fragment.CareerTalkFavoratesFragment;
import com.campusrecruit.fragment.RecruitFavoratesFragment;
import com.campusrecruit.fragment.ReplyByOtherUserFragment;
import com.campusrecruit.fragment.ReplyToOtherUserFragment;
import com.campusrecruit.fragment.UserTopicFragment;
import com.krislq.sliding.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class UserReplyActivity extends FragmentActivity {
	private ViewPager viewPager = null;
	private AppContext appContext;
	private ViewPagerUserMessageAdapter adapter;

	private ReplyByOtherUserFragment replyByFragment;
	private ReplyToOtherUserFragment replyToFragment;
	private UserTopicFragment userTopicFragment;
	private List<BaseFragment> fragments = new ArrayList<BaseFragment>();

	// private final int CATALOG_REPLY_MESSAGE = 0;

	public static final int CATALOG_REPLY_BY_OTHER = 0;
	public static final int CATALOG_REPLY_TO_OTHER = 1;
	public static final int CATALOG_MY_TOPICS = 2;

	public static boolean messageDatIsInit = false;
	public static boolean replyByDatIsInit = false;
	public static boolean replyToDatIsInit = false;
	public static boolean userTopicDatIsInit = false;

	private void initFragment() {
		replyByFragment = new ReplyByOtherUserFragment();
		replyToFragment = new ReplyToOtherUserFragment();
		userTopicFragment = new UserTopicFragment();
		fragments.add(replyByFragment);
		fragments.add(replyToFragment);
		fragments.add(userTopicFragment);
		Log.i("ac", "init user message");
	}

	private void initFragmentData(int type) {
		Log.i("ac", "init fragment data");
		switch (type) {
		case CATALOG_REPLY_BY_OTHER:
			if (!replyByDatIsInit) {
				replyByFragment.initData(appContext);
				replyByDatIsInit = true;
			}
			break;
		case CATALOG_REPLY_TO_OTHER:
			Log.i("ac", "process");
			if (!replyToDatIsInit) {
				Log.i("ac", "process init data");
				replyToFragment.initData(appContext);
				Log.i("ac", "begin laod data");
				replyToDatIsInit = true;
			}
			break;
		case CATALOG_MY_TOPICS:
			Log.i("ac", "process");
			if (!userTopicDatIsInit) {
				Log.i("ac", "process init data");
				userTopicFragment.initData(appContext);
				userTopicDatIsInit = true;
			}
			break;
		default:
			break;
		}
		Log.i("ac", "load fragment data complete");
	}

	@Override
	protected void onStop() {
		super.onStop();
		replyByDatIsInit = false;
		replyToDatIsInit = false;
		userTopicDatIsInit = false;
	}

	private void initTab() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.removeAllTabs();
		actionBar.addTab(actionBar.newTab().setText("回复我的")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("我回复的")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("我的帖子")
				.setTabListener(tabListener));
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Log.i("test", "init user meesage");
		setContentView(R.layout.user_message);
		appContext = (AppContext) getApplication();
		initFragment();
		initView();
		Log.i("test", "init message complete");
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_refresh, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.menu_refresh_id:
			refreshData();
			return true;
		default:
			return false;
		}
	}
	
	private void refreshData() {
		Log.i("refresh", "touched");
		// refresh event
		switch (viewPager.getCurrentItem()) {
		case CATALOG_REPLY_BY_OTHER:
			replyByDatIsInit = false;
			break;
		case CATALOG_REPLY_TO_OTHER:
			replyToDatIsInit = false;
			break;
		case CATALOG_MY_TOPICS:
			userTopicDatIsInit = false;
			break;
		default:
			break;
		}
		Log.i("bug","refresh fragment");
		initFragmentData(viewPager.getCurrentItem());
	}

	private void initView() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("消息");

		viewPager = (ViewPager) findViewById(R.id.user_message_viewPager);

		ViewPagerUserMessageAdapter adapter = new ViewPagerUserMessageAdapter(
				getSupportFragmentManager(), fragments);
		viewPager.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		viewPager.setOnPageChangeListener(onPageChangeListener);

		initTab();
		initFragmentData(CATALOG_REPLY_BY_OTHER);
		viewPager.setCurrentItem(0);
		Log.i("test", "init view complete!!!");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == UIHelper.REQUEST_CODE_FOR_RESULT) {
			viewPager.setCurrentItem(CATALOG_REPLY_TO_OTHER);
		}
	}

	private SimpleOnPageChangeListener onPageChangeListener = new SimpleOnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			initFragmentData(position);
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
