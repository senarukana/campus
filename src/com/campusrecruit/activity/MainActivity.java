package com.campusrecruit.activity;

import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TabHost;

import com.campusrecruit.adapter.MainFragmentAdapter;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.app.AppManager;
import com.campusrecruit.bean.BBSSection;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Notice;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.common.UpdateManager;
import com.campusrecruit.db.DBManager;
import com.campusrecruit.fragment.BBSSectionFragment;
import com.campusrecruit.fragment.CareerTalkFragment;
import com.campusrecruit.fragment.MenuFragment;
import com.campusrecruit.fragment.RecruitFragment;
import com.campusrecruit.widget.BadgeView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.pcncad.campusRecruit.R;

public class MainActivity extends SlidingFragmentActivity {
	public static DBManager mgr = null;
	private MenuFragment menuFragment = null;
	private MainFragmentAdapter mainFragmentAdapter = null;

	public static CareerTalkFragment careerTalkFragment;
	public static RecruitFragment recruitFragment;
	public static BBSSectionFragment bbsSectionFragment;
	public static int alarmScheduleID;
	public static boolean isLogout = false;
	public static int currentViewPosition = 0;
	

	private boolean mainFragmentIsInit = true;

	private boolean recruitDatIsInit = true;
	private boolean careerDatIsInit = true;
	private boolean bbsSectionDatIsInit = true;

	private final int CATALOG_RECRUIT = 1;
	private final int CATALOG_CAREERTALK = 2;
	private final int CATALOG_DISCUSS = 3;

	public static int NotifyCount = 0;
	public static int CarrerTalkCount = 0;
	public static int RecruitCount = 0;
	public static int ReplyCount = 0;
	public static int MessageCount = 0;

	private boolean isAboutQuit = false;

	private CanvasTransformer mTransformer;

	public int index = -1;
	private boolean isSendNotice = false;

	private ViewPager mViewPager = null;
	private LinearLayout mLinearLayout = null;
	private TabHost tabHost = null;

	private RadioButton rdRecruit = null;
	private RadioButton rdCareerTalk = null;
	private RadioButton rdBBSection = null;

	public static ListView menuListView = null;

	public static BadgeView bvRecruit;
	public static BadgeView bvCareerTalk;
	public static BadgeView bvMessage = null;
	public static BadgeView bvReply = null;

	private AppContext appContext;

	List<Recruit> iRecruits;
	List<CareerTalk> iCareerTalks;
	List<BBSSection> iSections;

	public static int toPager = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("main", "begin");
		super.onCreate(savedInstanceState);
		appContext = (AppContext) getApplication();
		setContentView(R.layout.frame_context_v);
		setTitle("校园招聘");

		if (!appContext.isNetworkConnected())
			UIHelper.ToastMessage(this, R.string.network_not_connected);
		if (appContext.isCheckUp()) {
			UpdateManager.getUpdateManager().checkAppUpdate(this, false);
		}
		appContext.isInnerAddress();
		
		initFragment();
		initView();
		// initAdapter();

		// set the Behind View
		setBehindContentView(R.layout.frame_menu);

		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		Log.i("main", "new menu fragment");
		menuFragment = new MenuFragment();
		fragmentTransaction.replace(R.id.menu, menuFragment);
		fragmentTransaction.commit();
		initSlidingMenu();

		// // 初始化DBManager
		Log.i("main", "main create complete");

		if (appContext.isLogin()
				&& appContext.getLoginUser().isBackgroundNotice()) {
			foreachUserNotice();
		}

		if (!appContext.getTutorialMainPageCompleted()) {
			UIHelper.showHomeTutorial1(this);
		}

		/*
		 * boolean needRefresh = getIntent().getBooleanExtra("needRefresh",
		 * false);
		 * 
		 * if (needRefresh) { Log.i("refresh","need refresh!!!"); if
		 * (recruitFragment != null) { recruitFragment.initSelectedList();
		 * Log.i("refresh","need refresh!!!"); } if (careerTalkFragment != null)
		 * { careerTalkFragment.initSelectedList(); } } else {
		 * Log.i("refresh","NOONONONONOneed refresh!!!"); }
		 */
	}

	private void initView() {
		mLinearLayout = (LinearLayout) findViewById(R.id.content);
		tabHost = (TabHost) findViewById(R.id.tab_host);
		rdRecruit = (RadioButton) findViewById(R.id.radio_campus_recruit);
		rdCareerTalk = (RadioButton) findViewById(R.id.radio_career_talk);
		rdBBSection = (RadioButton) findViewById(R.id.radio_discuss);
		bvRecruit = new BadgeView(this, rdRecruit);
		bvCareerTalk = new BadgeView(this, rdCareerTalk);
		bvMessage = new BadgeView(this, rdBBSection);
		initBadgeView(bvRecruit);
		initBadgeView(bvCareerTalk);
		initBadgeView(bvMessage);
		initRadioButtonListener();
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mainFragmentAdapter = new MainFragmentAdapter(
				getSupportFragmentManager());
		/*
		 * mViewPager.setAdapter(mainFragmentAdapter);
		 * mViewPager.setOnPageChangeListener(onPageChangeListener);
		 */
		// mViewPager.setCurrentItem(0);

	}

	private void initFragment() {
		careerTalkFragment = new CareerTalkFragment();
		recruitFragment = new RecruitFragment();
		bbsSectionFragment = new BBSSectionFragment();
	}

	private void initBadgeView(BadgeView badgeView) {
		badgeView.setBackgroundResource(R.drawable.widget_count_bg2);
		badgeView.setIncludeFontPadding(false);
		badgeView.setGravity(Gravity.CENTER);
		badgeView.setTextSize(8f);
		badgeView.setTextColor(Color.WHITE);
	}

	private void initSlidingMenu() {

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidth(50);
		// sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffset(150);
		sm.setFadeDegree(0.35f);
		sm.setBehindScrollScale(0.0f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mTransformer = new CanvasTransformer() {

			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, canvas.getWidth() / 2,
						canvas.getHeight() / 2);
			}
		};
		sm.setBehindCanvasTransformer(mTransformer);

		// 使用左上方icon可点，这样在onOptionsItemSelected里面才可以监听到R.id.home
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onStop() {
		super.onStop();
		isSendNotice = true;
		// foreachUserNotice();
	}

	@Override
	protected void onResume() {
		Log.i("test", "resume");
		isAboutQuit = false;
		super.onResume();
		isSendNotice = false;
		NotifyCount = 0;
		if (RecruitCount != 0) {
			Log.i("life", "recruit!!!");
			getSlidingMenu().showContent();
			recruitDatIsInit = true;
			mViewPager.setCurrentItem(0);
			return;
		}
		if (CarrerTalkCount != 0) {
			Log.i("life", "career!!!");
			getSlidingMenu().showContent();
			careerDatIsInit = true;
			mViewPager.setCurrentItem(1);
			return;
		}

		if (ReplyCount != 0) {
			Log.i("life", "reply!!!");
			ReplyCount = 0;
			UIHelper.showUserReply(this);
			return;
		}

		if (MessageCount != 0) {
			Log.i("life", "message");
			MessageCount = 0;
			UIHelper.showPrivateMessage(this);
			return;
		}

		if (alarmScheduleID != 0) {
			UIHelper.showCareerTalkDetailByCalendar(this, alarmScheduleID);
			alarmScheduleID = 0;
			return;
		}

		if (mViewPager.getCurrentItem() == 0) {
			Log.i("main", "0");
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
		Log.i("test", "resume complete");

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		isSendNotice = false;
		/*
		 * Log.i("life", "on resume!!test" );
		 * Log.i("life",String.format("recruit %d, career %d, reply %d, message %d"
		 * , RecruitCount,CarrerTalkCount,ReplyCount,MessageCount)); if
		 * (RecruitCount != 0) { Log.i("life", "recruit!!!");
		 * getSlidingMenu().showContent(); mViewPager.setCurrentItem(0); return;
		 * } if (CarrerTalkCount != 0) { Log.i("life", "career!!!");
		 * getSlidingMenu().showContent(); mViewPager.setCurrentItem(1); return;
		 * }
		 */
		/*
		 * if (ReplyCount != 0) { Log.i("life", "reply!!!");
		 * UIHelper.showUserReply(this); return; }
		 * 
		 * if (MessageCount != 0) { Log.i("life","message");
		 * UIHelper.showPrivateMessage(this); }
		 */
		/*
		 * if (DiscussCount != 0) { getSlidingMenu().showContent();
		 * mViewPager.setCurrentItem(2); return; }
		 */
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isSendNotice = true;
		if (mgr != null) {
			mgr.close();
		}
	}

	@Override
	public void onBackPressed() {

		if (!isAboutQuit) {
			UIHelper.ToastMessage(MainActivity.this, "再按一次退出程序");
			isAboutQuit = true;
		} else {
			AppManager.getAppManager().AppExitQuickly(MainActivity.this);
		}

		/*
		 * AlertDialog.Builder adb = new AlertDialog.Builder(this);
		 * adb.setTitle("确定退出一职有你吗?"); adb.setMessage("你将接收不到后台消息?");
		 * adb.setNegativeButton("取消", null); adb.setPositiveButton("确定", new
		 * AlertDialog.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * finish(); //
		 * AppManager.getAppManager().AppExitQuickly(MainActivity.this); }
		 * }).setNegativeButton("取消", null).show(); ;
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
			/*
			 * case R.id.menu_settings: getSlidingMenu().showMenu(); return
			 * true;
			 */
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Log.i("test", "onPostCreate");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				showViewPager();
				getSlidingMenu().showMenu();
			}
		}, 500);
	}

	private void initRadioButtonListener() {
		Log.i("main", "initRadioButton");
		rdRecruit.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Log.i("main", "recruit check");
					mViewPager.setCurrentItem(0);
					if (recruitDatIsInit || RecruitCount != 0) {
						refreshFragmentData(CATALOG_RECRUIT, recruitDatIsInit);
						Log.i("refresh", bvRecruit.getText().toString());
						// bvRecruit.hide();
					}
					/*
					 * clearNotice(CATALOG_RECRUIT); bvRecruit.setText("");
					 */
					rdCareerTalk.setChecked(false);
					rdBBSection.setChecked(false);
				}
			}
		});

		rdCareerTalk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Log.i("main", "carer check");
					mViewPager.setCurrentItem(1);
					rdBBSection.setChecked(false);
					rdRecruit.setChecked(false);
					if (CarrerTalkCount != 0) {
						// 如果通知新数据到来，标识新数据标示
						appContext.careerTalkIsInit = false;
					}
					if (careerDatIsInit || CarrerTalkCount != 0) {
						refreshFragmentData(CATALOG_CAREERTALK, careerDatIsInit);
					}
				}
			}
		});

		rdBBSection.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					mViewPager.setCurrentItem(2);
					rdCareerTalk.setChecked(false);
					rdRecruit.setChecked(false);
					if (bbsSectionDatIsInit) {
						refreshFragmentData(CATALOG_DISCUSS,
								bbsSectionDatIsInit);
					}
				}
			}
		});
	}

	public void showViewPager() {
		Log.i("test", "show viewpager");
		if (mainFragmentIsInit) {
			mainFragmentIsInit = false;
			index = 0;
			mLinearLayout.setVisibility(View.GONE);
			tabHost.setVisibility(View.VISIBLE);

			mViewPager.setOffscreenPageLimit(5);
			mViewPager.setAdapter(mainFragmentAdapter);
			mViewPager.setOnPageChangeListener(onPageChangeListener);
			getActionBar()
					.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			refreshFragmentData(CATALOG_RECRUIT, recruitDatIsInit);
		}
		mViewPager.setCurrentItem(currentViewPosition);
		// radioGroup.check(R.id.radio_campus_recruit);
	}

	/*
	 * 刷新fragment数据 type：fragment类型 isInit：是否是初始化数据
	 */
	private void refreshFragmentData(int type, boolean isInit) {
		switch (type) {
		case CATALOG_RECRUIT:
			if (isInit) {
				recruitFragment.loadRecruitDataFromDisk();
				recruitDatIsInit = false;
			} else {
				recruitFragment.ThreadSearch(0,
						UIHelper.LISTVIEW_ACTION_REFRESH);
				RecruitCount = 0;
			}
			break;
		case CATALOG_CAREERTALK:
			if (isInit) {
				careerTalkFragment.loadCareerTalkDataFromDisk();
				careerDatIsInit = false;
			} else {
				careerTalkFragment.ThreadSearch(0,
						UIHelper.LISTVIEW_ACTION_REFRESH);
				CarrerTalkCount = 0;
			}
			break;
		case CATALOG_DISCUSS:
			if (isInit) {
				bbsSectionFragment.loadData(0, UIHelper.LISTVIEW_ACTION_INIT);
				bbsSectionDatIsInit = false;
			} else {
				bbsSectionFragment
						.loadData(0, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
			break;
		default:
			break;
		}
	}

	ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			// getActivity().getActionBar().setSelectedNavigationItem(position);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			switch (position) {
			case 0:
				Log.i("notice", "recruit selected");
				getSlidingMenu().setTouchModeAbove(
						SlidingMenu.TOUCHMODE_FULLSCREEN);
				rdRecruit.setChecked(true);
				getActionBar().setTitle("校园招聘");
				break;
			case 1:
				rdCareerTalk.setChecked(true);
				getActionBar().setTitle("宣讲会");
				break;
			case 2:
				rdBBSection.setChecked(true);
				getActionBar().setTitle("讨论组");
				break;
			default:
				getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				break;
			}

		}

	};

	/*
	 * private void initAdapter() { Log.i("main", "init Adapter");
	 * mainFragmentAdapter = new MainFragmentAdapter(
	 * getSupportFragmentManager());
	 * 
	 * appContext.setLvUserTopicsAdapter(new ListViewUserTopicsAdapter(this,
	 * R.layout.user_topic_item));
	 * 
	 * 
	 * appContext.setLvRecruitFavoratesAdapter(new
	 * ListViewRecruitFavorateAdapter( this, appContext,
	 * R.layout.recruit_favorate_item));
	 * 
	 * 
	 * appContext.setLvCareerTalkFavoratesAdapter(new
	 * ListViewCareerTalkFavorateAdapter( this, appContext,
	 * R.layout.career_favorate_item));
	 * 
	 * }
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("life", "result!!!!!!!!!!!!!!" + resultCode);

		if (requestCode == UIHelper.REQUEST_CODE_FOR_TUTORIAL_MAIN_1) {
			UIHelper.showHomeTutorial2(this);
			return;
		} else if (requestCode == UIHelper.REQUEST_CODE_FOR_TUTORIAL_MAIN_2) {
			appContext.setTutorialMainPageCompleted();
			return;
		} else if (requestCode == UIHelper.REQUEST_CODE_FOR_RECOMMEND
				&& resultCode == RESULT_OK) {
			appContext.clearObsoleteSqlite();
			recruitDatIsInit = true;
			careerDatIsInit = true;
			Log.i("life", "refresh fragment");
			refreshFragmentData(CATALOG_RECRUIT, recruitDatIsInit);
			if (recruitFragment != null) {
				recruitFragment.initSelectedList();
				Log.i("refresh", "need refresh!!!");
			}
			if (careerTalkFragment != null) {
				careerTalkFragment.initSelectedList();
			}
			return;
		} else if ((requestCode == UIHelper.REQUEST_CODE_FOR_FAVORATE || requestCode == UIHelper.REQUEST_CODE_FOR_SCHEDULE) 
				&& resultCode == RESULT_OK) {
			mViewPager.setCurrentItem(currentViewPosition);
			getSlidingMenu().toggle();
			currentViewPosition = 0;
		}
		if (requestCode == UIHelper.REQUEST_CODE_FOR_SETTINGS && isLogout) {
			isLogout = false;
			finish();
		}

	}

	private void foreachUserNotice() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					if (((Notice)msg.obj).getSum() != 0) {
						UIHelper.sendBroadCast(MainActivity.this,
								(Notice) msg.obj);
					}
				}
				if (appContext.isLogin()
						&& appContext.getLoginUser().isBackgroundNotice()
						&& isSendNotice) {
					foreachUserNotice();
				}
			}
		};
		new Thread() {
			@Override
			public void run() {
				Message msg = new Message();
				try {
					sleep(60 * 1000);
					Notice notice = appContext.getUserNotice();
					msg.what = 1;
					msg.obj = notice;
				} catch (AppException e) {

					msg.what = -1;
				} catch (Exception e) {

					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

}
