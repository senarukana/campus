package com.campusrecruit.activity;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.fragment.InflatedLayoutFragment;
import com.campusrecruit.widget.UnderlinePageIndicator;
import com.pcncad.campusRecruit.R;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

public class TutorialActivity extends BaseActivity {
	private ViewPager mViewPager;
	private TutorialLaneAdapter mLaneAdapter;
	private boolean mDoFollow = true;

	private static final int PAGE_WELCOME = 0;
	private static final int PAGE_PREFERENCE = 1;
	private static final int PAGE_CALENDAR = 2;
	private static final int PAGE_COLLECTS = 3;
	private static final int PAGE_MESSAGE = 4;
	private static final int PAGE_THANKS = 5;

	/*
     *
	 */
	public AppContext getApp() {
		return (AppContext) getApplication();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Key the screen from dimming -
		// http://stackoverflow.com/a/4197370/328679
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		if (mLaneAdapter == null) {
			mLaneAdapter = new TutorialLaneAdapter(getSupportFragmentManager());
		}

		if (mLaneAdapter != null) {
			setContentView(R.layout.tutorial);

			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mLaneAdapter);

			UnderlinePageIndicator indicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
			indicator.setViewPager(mViewPager);
			indicator.setFades(false);
			indicator.setOnPageChangeListener(mOnPageChangeListener);
		}
	}

	/*
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		int currentItem = mViewPager.getCurrentItem();
		if (currentItem != PAGE_WELCOME) {
			menu.add(getString(R.string.action_previous))
					.setOnMenuItemClickListener(
							new MenuItem.OnMenuItemClickListener() {

								@Override
								public boolean onMenuItemClick(MenuItem item) {
									mViewPager.setCurrentItem(mViewPager
											.getCurrentItem() - 1);
									return true;
								}

							})
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_ALWAYS
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}

		if (currentItem != PAGE_THANKS) {
			menu.add(getString(R.string.action_next))
					.setOnMenuItemClickListener(
							new MenuItem.OnMenuItemClickListener() {

								@Override
								public boolean onMenuItemClick(MenuItem item) {
									mViewPager.setCurrentItem(mViewPager
											.getCurrentItem() + 1);
									return true;
								}

							})
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_ALWAYS
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}

		return true;
	}

	/*
	 * 
	 */
	OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int laneIndex) {

			Resources res = getResources();
			String[] titles = res.getStringArray(R.array.tutorial_titles);
			String title = titles[laneIndex];
			getActionBar().setTitle(title);

			invalidateOptionsMenu();
		}
	};

	/*
	 * 
	 */
	public void onFinishTutorialClicked(View view) {
		finish();
		Intent intent = new Intent(getApplicationContext(), StartActivity.class);
		startActivity(intent);
		getApp().setTutorialCompleted();
	}
	
	@Override
	public void onBackPressed() {
	}

	/*
	 * 
	 */
	public void onFollowCheckboxClicked(View view) {
		mDoFollow = ((CheckBox) (view)).isChecked();
	}

	/*
     * 
     */
	class TutorialLaneAdapter extends FragmentPagerAdapter {

		public TutorialLaneAdapter(FragmentManager supportFragmentManager) {
			super(supportFragmentManager);
		}

		@Override
		public Fragment getItem(int position) {

			switch (position) {

			case PAGE_WELCOME:
				return InflatedLayoutFragment
						.newInstance(R.layout.tutorial_welcome);

				// case PAGE_IMAGE_PREVIEW:
				// return
				// InflatedLayoutFragment.newInstance(R.layout.tutorial_images);

			case PAGE_PREFERENCE:
				return InflatedLayoutFragment
						.newInstance(R.layout.tutorial_preference);

			case PAGE_COLLECTS:
				return InflatedLayoutFragment
						.newInstance(R.layout.tutorial_collection);

			case PAGE_CALENDAR:
				return InflatedLayoutFragment
						.newInstance(R.layout.tutorial_calendar);
				
			case PAGE_MESSAGE:
				return InflatedLayoutFragment
						.newInstance(R.layout.tutorial_messages);

			case PAGE_THANKS:
				return InflatedLayoutFragment
						.newInstance(R.layout.tutorial_thanks);

			default:
				return InflatedLayoutFragment
						.newInstance(R.layout.tutorial_welcome);
			}
		}

		@Override
		public int getCount() {
			return 6;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}
}
