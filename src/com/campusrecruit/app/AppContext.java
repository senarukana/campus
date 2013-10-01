package com.campusrecruit.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.campusrecruit.adapter.ListViewBBSSectionAdapter;
import com.campusrecruit.adapter.ListViewCareerTalkAdapter;
import com.campusrecruit.adapter.ListViewCareerTalkFavorateAdapter;
import com.campusrecruit.adapter.ListViewRecruitAdapter;
import com.campusrecruit.adapter.ListViewRecruitFavorateAdapter;
import com.campusrecruit.adapter.ListViewReplyAdapter;
import com.campusrecruit.adapter.ListViewTopicsAdapter;
import com.campusrecruit.adapter.ListViewUserTopicsAdapter;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSSection;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Company;
import com.campusrecruit.bean.Notice;
import com.campusrecruit.bean.Province;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.Result;
import com.campusrecruit.bean.Schedules;
import com.campusrecruit.bean.User;
import com.campusrecruit.bean.UserMessage;
import com.campusrecruit.bean.UserPreference;
import com.campusrecruit.common.CryptoUtils;
import com.campusrecruit.common.FileUtils;
import com.campusrecruit.common.MethodsCompat;
import com.campusrecruit.common.ScheduleAlarmBroadcastReceiver;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.db.BBSTopicManager;
import com.campusrecruit.db.CareerTalkManager;
import com.campusrecruit.db.MessageListManager;
import com.campusrecruit.db.RecruitManager;
import com.campusrecruit.db.ScheduleManager;
import com.campusrecruit.net.NetApiClient;
import com.krislq.sliding.R;

import android.R.bool;
import android.R.integer;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
//import android.webkit.CacheManager;
import android.util.Log;
import android.util.Pair;

public class AppContext extends Application {

	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	private static final String app_key = "campusRecruit";

	private User loginUser = null;
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	private String saveImagePath;// 保存图片路径
	private static RecruitManager recruitManager;
	private static CareerTalkManager careerManager;
	private static BBSTopicManager topicManager;
	private static ScheduleManager scheduleManager;
	private static MessageListManager messageListManager;

	// init status false未初始化 true 初始化完成
	public boolean recruitIsInit = false;
	public boolean careerTalkIsInit = false;

	// loadfrom
	public boolean sectionLoadFromDisk = false;
	public boolean topicsLoadFromDisk = false;
	public boolean commentsLoadFromDisk = false;
	public boolean privateMessageLoadFromDisk = false;
	public boolean recruitLoadFromDisk = false;
	public boolean companyLoadFromDisk = false;
	public boolean userInfoLoadFromDisk = false;
	public boolean replyByLoadFromDisk = false;
	public boolean replyToLoadFromDisk = false;

	// list
	private List<Recruit> lvRecruitFavorateList = new ArrayList<Recruit>();
	private List<CareerTalk> lvCareerTalkFavorateList = new ArrayList<CareerTalk>();
	private List<BBSTopic> lvUserTopicList = new ArrayList<BBSTopic>();
	private List<BBSSection> lvBBSSectionList = new ArrayList<BBSSection>();
	private List<Recruit> lvRecruitList = new ArrayList<Recruit>();
	private List<CareerTalk> lvCareerTalkList = new ArrayList<CareerTalk>();

	// use for schedule
	private List<Schedules> scheduleList = new ArrayList<Schedules>();
	private int messageCount = 0;

	public void messageCountAdd() {
		this.messageCount++;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	public List<Schedules> getScheduleList() {
		return scheduleList;
	}

	public void setScheduleList(List<Schedules> scheduleList) {
		this.scheduleList = scheduleList;
	}

	public List<Recruit> getLvRecruitFavorateList() {
		return lvRecruitFavorateList;
	}

	public void setLvRecruitFavorateList(List<Recruit> lvRecruitFavorateList) {
		this.lvRecruitFavorateList = lvRecruitFavorateList;
	}

	public List<BBSTopic> getLvUserTopicList() {
		return lvUserTopicList;
	}

	public void setLvUserTopicList(List<BBSTopic> lvUserTopicList) {
		this.lvUserTopicList = lvUserTopicList;
	}

	public List<BBSSection> getLvBBSSectionList() {
		return lvBBSSectionList;
	}

	public void setLvBBSSectionList(List<BBSSection> lvBBSSectionList) {
		this.lvBBSSectionList = lvBBSSectionList;
	}

	public List<Recruit> getLvRecruitList() {
		return lvRecruitList;
	}

	public void setLvRecruitList(List<Recruit> lvRecruitList) {
		this.lvRecruitList = lvRecruitList;
	}

	public List<CareerTalk> getLvCareerTalkList() {
		return lvCareerTalkList;
	}

	public void setLvCareerTalkList(List<CareerTalk> lvCareerTalkList) {
		this.lvCareerTalkList = lvCareerTalkList;
	}

	public List<CareerTalk> getLvCareerTalkFavorateList() {
		return lvCareerTalkFavorateList;
	}

	public void setLvCareerTalkFavorateList(
			List<CareerTalk> lvCareerTalkFavorateList) {
		this.lvCareerTalkFavorateList = lvCareerTalkFavorateList;
	}

	// adapter
	private ListViewRecruitFavorateAdapter lvRecruitFavoratesAdapter;
	private ListViewCareerTalkFavorateAdapter lvCareerTalkFavoratesAdapter;
	private ListViewUserTopicsAdapter lvUserTopicsAdapter;

	private ListViewRecruitAdapter lvRecruitListAdapter;
	private ListViewCareerTalkAdapter lvCareerTalkListAdapter;
	private ListViewBBSSectionAdapter lvSectionAdapter;

	public ListViewRecruitFavorateAdapter getLvRecruitFavoratesAdapter() {
		return lvRecruitFavoratesAdapter;
	}

	public ListViewUserTopicsAdapter getLvUserTopicsAdapter() {
		return lvUserTopicsAdapter;
	}

	public ListViewRecruitAdapter getLvRecruitListAdapter() {
		return lvRecruitListAdapter;
	}

	public ListViewCareerTalkAdapter getLvCareerTalkListAdapter() {
		return lvCareerTalkListAdapter;
	}

	public ListViewBBSSectionAdapter getLvSectionAdapter() {
		return lvSectionAdapter;
	}

	public void setLvRecruitFavoratesAdapter(
			ListViewRecruitFavorateAdapter lvRecruitFavoratesAdapter) {
		this.lvRecruitFavoratesAdapter = lvRecruitFavoratesAdapter;
	}

	public void setLvUserTopicsAdapter(
			ListViewUserTopicsAdapter lvUserTopicsAdapter) {
		this.lvUserTopicsAdapter = lvUserTopicsAdapter;
	}

	public void setLvRecruitListAdapter(
			ListViewRecruitAdapter lvRecruitListAdapter) {
		this.lvRecruitListAdapter = lvRecruitListAdapter;
	}

	public void setLvCareerTalkListAdapter(
			ListViewCareerTalkAdapter lvCareerTalkListAdapter) {
		this.lvCareerTalkListAdapter = lvCareerTalkListAdapter;
	}

	public void setLvSectionAdapter(ListViewBBSSectionAdapter lvSectionAdapter) {
		this.lvSectionAdapter = lvSectionAdapter;
	}

	public ListViewCareerTalkFavorateAdapter getLvCareerTalkFavoratesAdapter() {
		return lvCareerTalkFavoratesAdapter;
	}

	public void setLvCareerTalkFavoratesAdapter(
			ListViewCareerTalkFavorateAdapter lvCareerTalkFavoratesAdapter) {
		this.lvCareerTalkFavoratesAdapter = lvCareerTalkFavoratesAdapter;
	}

	private BBSTopicManager getBBSTopicManager() {
		if (topicManager == null) {
			topicManager = new BBSTopicManager(this);
		}
		return topicManager;
	}

	private CareerTalkManager getCareerTalkManager() {
		if (careerManager == null) {
			careerManager = new CareerTalkManager(this);
		}
		return careerManager;
	}

	private RecruitManager getRecruitManager() {
		if (recruitManager == null) {
			recruitManager = new RecruitManager(this);
		}
		return recruitManager;
	}

	private ScheduleManager getScheduleManager() {
		if (scheduleManager == null) {
			scheduleManager = new ScheduleManager(this);
		}
		return scheduleManager;
	}

	public void scheduleAdd(Schedules s) {
		getScheduleManager().scheduleAddOne(s);
	}

	public void scheduleDelete(CareerTalk c) {
		getScheduleManager().schedule_delete_by_id(c.getCareerTalkID());
	}

	public void scheduleDelete(Schedules s) {
		getScheduleManager().schedule_delete_by_id(s.getScheduleID());
	}

	public List<Schedules> scheduleGetAll() {
		return getScheduleManager().scheduleGetAll();
	}

	/*
	 * 收藏宣讲会 修改用户收藏讨论组列表
	 */
	public void caeerTalkJoin(CareerTalk careerTalk, boolean flag) {
		if (flag) {
			// 检查该公司是否已经在收藏讨论组中
			boolean check = false;
			Log.i("bug",
					"career talk company name " + careerTalk.getCompanyName());
			for (BBSSection section : lvBBSSectionList) {
				Log.i("bug", "section name" + section.getSectionName());
				if (section.getSectionName()
						.equals(careerTalk.getCompanyName())) {
					Log.i("bug", "equal" + section.getSectionName());
					check = true;
					break;
				}
			}
			if (!check) {
				BBSSection bbsSection = new BBSSection();
				bbsSection.setSectionName(careerTalk.getCompanyName());
				bbsSection.setStatus(1);
				bbsSection.setIsJoined(1);
				bbsSection.setCompanyID(careerTalk.getCompanyID());
				lvBBSSectionList.add(bbsSection);
				if (lvSectionAdapter != null) {
					lvSectionAdapter.setData(lvBBSSectionList);
					lvSectionAdapter.notifyDataSetChanged();
				}
			}
			if (lvCareerTalkFavorateList == null)
				lvCareerTalkFavorateList = new ArrayList<CareerTalk>();
			lvCareerTalkFavorateList.add(careerTalk);
			if (lvCareerTalkFavoratesAdapter != null) {
				lvCareerTalkFavoratesAdapter.notifyDataSetChanged();
			}
			/*
			 * lvCareerTalkFavoratesAdapter.setData(lvCareerTalkFavorateList);
			 * Log.i("test", "recruit set set data!!");
			 * lvCareerTalkFavoratesAdapter.notifyDataSetChanged();
			 * Log.i("test", "recruit set notify data!!");
			 */
		} else {
			for (BBSSection section : lvBBSSectionList) {
				if (section.getSectionName()
						.equals(careerTalk.getCompanyName())) {
					lvBBSSectionList.remove(section);
					if (lvSectionAdapter != null) {
						lvSectionAdapter.setData(lvBBSSectionList);
						lvSectionAdapter.notifyDataSetChanged();
					}
					break;
				}
			}
			for (CareerTalk c : lvCareerTalkFavorateList) {
				if (c.getCareerTalkID() == careerTalk.getCareerTalkID()) {
					lvCareerTalkFavorateList.remove(c);
					if (lvCareerTalkFavoratesAdapter != null) {
						lvCareerTalkFavoratesAdapter.notifyDataSetChanged();
					}
					break;
				}
			}
		}
	}

	/*
	 * 收藏宣讲会 1. 修改用户收藏列表
	 */
	public void caeerTalkDetailJoin(CareerTalk careerTalk, boolean flag) {
		caeerTalkJoin(careerTalk, flag);

		for (CareerTalk c : lvCareerTalkList) {
			if (c.getCareerTalkID() == careerTalk.getCareerTalkID()) {
				if (flag)
					c.setIsJoined(1);
				else
					c.setIsJoined(0);
				lvCareerTalkListAdapter.setData(lvCareerTalkList);
				lvCareerTalkListAdapter.notifyDataSetChanged();
				break;
			}
		}
		if (!flag) {
			scheduleDelete(careerTalk);
		}
	}

	/*
	 * 收藏校园招聘 1. 修改用户收藏列表 2. 修改用户讨论组列表
	 */
	public void recruitJoin(Recruit recruit, boolean flag) {
		if (flag) {
			boolean check = false;
			for (BBSSection section : lvBBSSectionList) {
				if (section.getSectionName().equals(
						recruit.getCompany().getCompanyName())) {
					check = true;
					break;
				}
			}
			if (!check) {
				BBSSection bbsSection = new BBSSection();
				bbsSection
						.setSectionName(recruit.getCompany().getCompanyName());
				bbsSection.setCompanyIndustry(recruit.getCompany()
						.getIndustry());
				bbsSection.setCompanyType(recruit.getCompany().getType());
				bbsSection.setCompanyID(recruit.getCompany().getCompanyID());
				bbsSection.setStatus(1);
				bbsSection.setIsJoined(1);
				lvBBSSectionList.add(bbsSection);
				if (lvSectionAdapter != null) {
					lvSectionAdapter.setData(lvBBSSectionList);
					lvSectionAdapter.notifyDataSetChanged();
				}
			}
			if (lvRecruitFavorateList == null)
				lvRecruitFavorateList = new ArrayList<Recruit>();
			lvRecruitFavorateList.add(recruit);
			if (lvRecruitFavoratesAdapter != null) {
				lvRecruitFavoratesAdapter.notifyDataSetChanged();
			}
			Log.i("test", "recruit set notify data!!");

		} else {
			for (BBSSection section : lvBBSSectionList) {
				if (section.getSectionName().equals(recruit.getCompanyName())) {
					lvBBSSectionList.remove(section);
					lvSectionAdapter.setData(lvBBSSectionList);
					lvSectionAdapter.notifyDataSetChanged();
					break;
				}
			}

			for (Recruit r : lvRecruitFavorateList) {
				if (recruit.getRecruitID() == r.getRecruitID()) {
					lvRecruitFavorateList.remove(r);
					lvRecruitFavoratesAdapter.notifyDataSetChanged();
					break;
				}
			}

		}
		Log.i("test", "recruit join complete");
	}

	/*
	 * 在校园招聘详情界面收藏 1. 修改用户讨论组列表 2. 修改用户收藏列表 3. 修改校园招聘列表
	 */
	public void recruitDetailJoin(Recruit recruit, boolean flag) {
		Log.i("test", "recruit join");
		recruitJoin(recruit, flag);
		Log.i("test", "recruit join complete");
		for (Recruit r : lvRecruitList) {
			if (r.getRecruitID() == recruit.getRecruitID()) {
				if (flag)
					r.setIsJoined(1);
				else
					r.setIsJoined(0);
				lvRecruitListAdapter.setData(lvRecruitList);
				lvRecruitListAdapter.notifyDataSetChanged();
				break;
			}
		}
		Log.i("test", "rrr complete");
	}

	/*
	 * 用户发帖之后添加用户帖子列表
	 */
	public void commentPubAfter(BBSTopic topic) {
		Log.i("test", "comment pub afer begin");
		if (lvUserTopicList == null)
			lvUserTopicList = new ArrayList<BBSTopic>();
		// 去重
		boolean flag = false;
		for (BBSTopic t : lvUserTopicList) {
			if (t.getTopicID() == topic.getTopicID()) {
				t.setCreatedTime(topic.getCreatedTime());
				flag = true;
				break;
			}

		}
		if (!flag) {
			lvUserTopicList.add(topic);
		}
		/*
		 * lvUserTopicsAdapter.setData(lvUserTopicList); Log.i("test",
		 * "comment after notify"); lvUserTopicsAdapter.notifyDataSetChanged();
		 */
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册App异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(AppException
				.getAppExceptionHandler());

		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 设置保存图片的路径
		saveImagePath = getProperty(AppConfig.SAVE_IMAGE_PATH);
		if (StringUtils.isEmpty(saveImagePath)) {
			setProperty(AppConfig.SAVE_IMAGE_PATH,
					AppConfig.DEFAULT_SAVE_IMAGE_PATH);
			saveImagePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH;
		}
	}

	/**
	 * 检测当前系统声音是否为正常模式
	 * 
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}

	/**
	 * 应用程序是否发出提示音
	 * 
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 获取App唯一标识
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * 用户是否登录
	 * 
	 * @return
	 */
	public boolean isLogin() {
		if (loginUser != null && loginUser.getName() != null)
			return true;
		return false;
	}

	/**
	 * 用户是否登录
	 * 
	 * @return
	 */
	public boolean isInit() {
		if (loginUser != null && loginUser.getUid() != null)
			return true;
		return false;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public boolean isSetUserPreference() {
		return loginUser.getPreference() == null ? false : true;
	}

	/**
	 * 获取登录用户id
	 * 
	 * @return
	 */
	public String getLoginUid() {
		if (loginUser != null)
			return loginUser.getUid();
		return null;
	}

	/**
	 * 用户注销
	 */
	public void Logout() {
		NetApiClient.cleanCookie();
		this.cleanCookie();
		this.loginUser = null;
	}

	public void createDefaultUser(String userName) {
		Log.i("user", "create complete");
		if (loginUser != null) {
			Log.i("user", "create set name" + userName);
			if (this.loginUser.getUid() == null) {
				this.loginUser.setUid(UUID.randomUUID().toString());
			}
			this.loginUser.setName(userName);
			Log.i("user", "set complete");
		} else {
			User user = new User();
			user.setUid(UUID.randomUUID().toString());
			user.setName(userName);
			this.loginUser = user;
			Log.i("user", "create complete");
		}
	}

	public void createDefaultUser() {
		User user = new User();
		user.setUid(UUID.randomUUID().toString());
		this.loginUser = user;
	}

	public void startAlarm(Context context, Schedules vSchedule) {
		Log.i("alarm", "begin");
		Log.i("alaram", "add alarm");
		scheduleAdd(vSchedule);
		Log.i("alaram", "start alarm");
		AlarmManager alarms = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Log.i("alarm --", alarms.toString());
		Intent intent = new Intent(context,
				ScheduleAlarmBroadcastReceiver.class);
		intent.setAction("com.campusRecruit.action.SCHEDULE");
		Bundle bundle = new Bundle();
		bundle.putSerializable("schedule", vSchedule);
		intent.putExtras(bundle);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				vSchedule.getScheduleID(), intent, PendingIntent.FLAG_ONE_SHOT);
		// String dateString = vSchedule.getDate() + " ";
		String dateString = vSchedule.getDate().substring(0,
				vSchedule.getDate().indexOf(" "))
				+ " ";
		int index = vSchedule.getTime().indexOf("-");
		String startTime = null;
		// expect the time is : 13:00-15:00
		if (index != -1) {
			startTime = vSchedule.getTime().substring(0, index);
			if (startTime == null) {
				// oops date format is wrong
				Log.i("bug", "alarm error,data format is wrong");
				return;
			}
		} else {
			// may be the time is : 13:00~15:00
			index = vSchedule.getTime().indexOf("~");
			if (index != -1) {
				startTime = vSchedule.getTime().substring(0, index);
				if (startTime == null) {
					// oops date format is wrong
					Log.i("bug", "alarm error,data format is wrong");
					return;
				}
			}
		}
		dateString += startTime;
		Log.i("time", dateString);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			calendar.setTime(sdf.parse(dateString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Log.i("time", calendar.YEAR + ":" + calendar.MONTH + ":"
				+ calendar.DAY_OF_MONTH + " " + calendar.HOUR + ":"
				+ calendar.MINUTE + ":" + calendar.SECOND);

		Log.i("time", calendar.getTimeInMillis() + "");

		alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				pendingIntent);
		/*
		 * alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
		 * pendingIntent);
		 */
		Log.i("alaram", "complete");
	}

	public void startAlarm(Context context, CareerTalk careerTalk) {
		Log.i("alaram", "start");
		Schedules vSchedule = new Schedules(careerTalk.getCareerTalkID(),
				careerTalk.getCompanyName(), careerTalk.getPlace(),
				careerTalk.getDate(), careerTalk.getTime());
		startAlarm(context, vSchedule);
	}

	public void cancelAlarm(Context context, Schedules schedule) {
		scheduleDelete(schedule);
		Log.i("alarm", "cancel alarm");
		AlarmManager alarms = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context,
				ScheduleAlarmBroadcastReceiver.class);
		intent.setAction("com.campusRecruit.action.SCHEDULE");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				schedule.getScheduleID(), intent, PendingIntent.FLAG_ONE_SHOT);
		alarms.cancel(pendingIntent);
	}

	public void cancelAlarm(Context context, CareerTalk careerTalk) {
		Schedules schedule = new Schedules(careerTalk.getCareerTalkID(),
				careerTalk.getCompanyName(), careerTalk.getPlace(),
				careerTalk.getDate(), careerTalk.getTime());
		cancelAlarm(context, schedule);
		Log.i("alarm", "delete alarm");
	}

	/**
	 * 初始化用户登录信息
	 */
	public void initLoginInfo() {
		this.loginUser = getLoginInfo();
	}

	/**
	 * 用户登录验证
	 * 
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public boolean loginVerify(String name, String pwd) throws AppException {
		cleanLoginInfo();
		User user = NetApiClient.login(this, name, pwd);
		if (user == null) {
			Log.i("test", "12345");
			return false;
		} else {
			Log.i("test", "33444");
			Log.i("user", user.getName());
			Log.i("user", user.getUid());
			this.loginUser = user;
			saveLoginInfo();
			savePreference(this.loginUser.getPreference());
			return true;
		}
	}

	/**
	 * 用户注册
	 * 
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public int registerUser(String userName, String pwd) throws AppException {
		Log.i("test", "register user");
		createDefaultUser(userName);
		Log.i("test", this.loginUser.getUid());
		Log.i("test", "begin rg user");
		return NetApiClient.register(this, this.loginUser.getUid(), userName,
				pwd);

	}

	/**
	 * 我的个人资料
	 * 
	 * @param isRefresh
	 *            是否主动刷新
	 * @return
	 * @throws AppException
	 */
	public User getMyInformation(boolean isRefresh) throws AppException {
		User myinfo = null;
		String key = "myinfo_" + getLoginUid();
		if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			try {
				myinfo = NetApiClient.getUserInfo(this, getLoginUid(), false);
			} catch (AppException e) {
				myinfo = (User) readObject(key);
				if (myinfo == null)
					throw e;
			}
		} else {
			myinfo = (User) readObject(key);
			if (myinfo == null)
				myinfo = new User();
		}
		return myinfo;
	}

	/**
	 * 我的个人资料
	 * 
	 * @param isRefresh
	 *            是否主动刷新
	 * @return
	 * @throws AppException
	 */
	public User getUserInformation(String uid) throws AppException {
		User userInfo = null;
		String key = "userinfo" + uid;
		userInfoLoadFromDisk = false;
		if (isNetworkConnected() && (!isReadDataCache(key))) {
			try {
				userInfo = NetApiClient.getUserInfo(this, uid, false);
			} catch (AppException e) {
				userInfo = (User) readObject(key);
				userInfoLoadFromDisk = true;
				if (userInfo == null)
					throw e;
			}
		} else {
			userInfo = (User) readObject(key);
			userInfoLoadFromDisk = true;
			if (userInfo == null)
				userInfo = new User();
		}
		return userInfo;
	}

	// use for messageList
	private MessageListManager getMessageListManager() {
		if (messageListManager == null) {
			messageListManager = new MessageListManager(this);
		}
		return messageListManager;
	}

	public List<UserMessage> getMessageListFromInternet() throws AppException {
		List<UserMessage> list = NetApiClient.getUserContactList(this,
				getLoginUid());
		if (list == null)
			return new ArrayList<UserMessage>();
		return list;
	}

	public List<UserMessage> getMessageListLocal() {
		List<UserMessage> list = getMessageListManager().getUserMessages();
		for (UserMessage message : list) {
			Log.i("test", String.format(
					"message user_id %s, user_name %s, created_time %s",
					message.getUserID(), message.getUserName(),
					message.getCreatedTime()));
		}
		return list;
	}

	public ArrayList<UserMessage> getPrivateMessageList(String userId,
			String lastTime) throws AppException {
		ArrayList<UserMessage> list = new ArrayList<UserMessage>();
		String key = "private_message" + userId;
		privateMessageLoadFromDisk = false;
		if (isNetworkConnected()) {
			try {
				list = NetApiClient.getUserMessageList(this, getLoginUid(),
						userId, lastTime);
				if (list != null) {
					saveObject(list, key);
				}
				if (list == null)
					return new ArrayList<UserMessage>();
			} catch (AppException e) {
				list = (ArrayList<UserMessage>) readObject(key);
				privateMessageLoadFromDisk = true;
				if (list == null)
					throw e;
			}
		} else {
			list = (ArrayList<UserMessage>) readObject(key);
			privateMessageLoadFromDisk = true;
			if (list == null)
				list = new ArrayList<UserMessage>();
		}
		return list;
	}

	/*
	 * 新建用户私信 userID 发给用户ID content 内容
	 */
	public UserMessage addMessage(String userID, String userName, String content)
			throws AppException {
		NetApiClient.newPrivateMessage(this, getLoginUid(), userID, content);
		UserMessage message = new UserMessage();
		message.setUserID(userID);
		message.setUserName(userName);
		message.setContent(content);
		message.setCreatedTime(StringUtils.getCurrentTimeStamp());
		Log.i("test", message.getCreatedTime());
		Log.i("bug", "new message userId" + userID);
		// 更新最后回复内容
		getMessageListManager().messageListAddOne(message);
		// 因为是自己发的
		message.setUserID(getLoginUid());
		message.setFace(1);
		message.setStatus(1);
		return message;
	}

	/*
	 * 保存俩人通信最后内容
	 */
	public void saveMessage(UserMessage message) {
		getMessageListManager().messageListAddOne(message);
	}

	/*
	 * 新建用户私信 userID 发给用户ID content 内容
	 */
	public void saveMessages(List<UserMessage> messages) {
		getMessageListManager().saveList(messages);
	}

	/**
	 * 我的个人资料
	 * 
	 * @param isRefresh
	 *            是否主动刷新
	 * @return
	 * @throws AppException
	 */
	public void setUserInformation(String uid, String email, String major,
			String schoolName, int gender) throws AppException {
		saveLoginInfo();
		NetApiClient.setUserInfo(this, uid, email, major, schoolName, gender);
	}

	public List<String> getProvinceList() {
		List<String> provinces = new ArrayList<String>();
		provinces.add("全国");
		provinces.add(("北京")); // 1
		provinces.add(("天津")); // 2
		provinces.add(("上海")); // 3
		provinces.add(("广东")); // 4
		provinces.add(("浙江")); // 5
		provinces.add(("江苏")); // 6
		provinces.add(("四川")); // 7
		provinces.add(("山东")); // 8
		provinces.add(("湖南")); // 9
		provinces.add(("湖北")); // 10
		provinces.add(("河南")); // 11
		provinces.add(("河北")); // 12
		provinces.add(("重庆")); // 13
		provinces.add(("辽宁")); // 14
		provinces.add(("安徽")); // 15
		provinces.add(("广西")); // 16
		provinces.add(("山西")); // 17
		provinces.add(("吉林")); // 18
		provinces.add(("黑龙江")); // 19
		provinces.add(("云南")); // 20
		provinces.add(("贵州")); // 21
		provinces.add(("福建")); // 22
		provinces.add(("宁夏")); // 23
		provinces.add(("甘肃")); // 24
		provinces.add(("青海")); // 25
		provinces.add(("陕西")); // 26
		provinces.add(("内蒙古")); // 27
		provinces.add(("海南")); // 28
		provinces.add(("江西")); // 29
		provinces.add(("新疆")); // 30
		provinces.add(("西藏")); // 31
		return provinces;
	}
	
	public List<String> getCompanyTypeList() {
		List<String> types = new ArrayList<String>();
		types.add("全部");
		types.add("国企");
		types.add("私企");
		types.add("外企");
		return types;
	}

	public List<String> getCompanyIndustryList() {
		List<String> industries = new ArrayList<String>();
		industries.add("全部");
		industries.add("计算机");
		industries.add("通信");
		industries.add("电子");
		industries.add("金融");
		return industries;
	}


	/**
	 * 更新用户头像
	 * 
	 * @param portrait
	 *            新上传的头像
	 * @return
	 * @throws AppException
	 */
	public void updatePortrait(File portrait) throws AppException {
		NetApiClient.updatePortrait(this, getLoginUid(), portrait);
	}

	/**
	 * 清空通知消息
	 * 
	 * @param uid
	 * @param type
	 *            1:@我的信息 2:未读消息 3:评论个数 4:新粉丝个数
	 * @return
	 * @throws AppException
	 */
	public void noticeClear(int type) throws AppException {
		NetApiClient.clearUserNotice(this, getLoginUid(), type);
	}

	/**
	 * 获取用户通知信息
	 * 
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public Notice getUserNotice() throws AppException {
		return NetApiClient.getUserNotice(this, getLoginUid());
	}

	/**
	 * 收藏校园招聘
	 * 
	 * @param uid
	 * @param recruitID
	 * @return
	 * @throws AppException
	 */
	public void joinRecruit(int recruitID, boolean join) throws AppException {
		getRecruitManager().updateJoin(recruitID, join);

		if (isNetworkConnected()) {
			NetApiClient.joinRecruit(this, getLoginUid(), recruitID, join);
		}
	}

	public void setNotifyType(Map<String, ArrayList<Integer>> selected)
			throws AppException {
		StringBuilder notifyTypeBuilder = new StringBuilder();
		for (Integer type : selected.get("type")) {
			notifyTypeBuilder.append(type + ",");
		}
		String notifyType = notifyTypeBuilder.substring(0,
				notifyTypeBuilder.length() - 1);
		savePreferenceNotifyType(notifyType);
		this.loginUser.getPreference().setNotifyType(notifyType);
		NetApiClient.setPreference(this, getLoginUid(), notifyType);
	}

	public void setPreference(Map<String, ArrayList<Integer>> selected)
			throws AppException {
		StringBuilder industryBuilder = new StringBuilder();
		for (Integer industry : selected.get("industry")) {
			industryBuilder.append(industry + ",");
		}
		StringBuilder propertyBuilder = new StringBuilder();
		for (Integer property : selected.get("property")) {
			propertyBuilder.append(property + ",");
		}
		/*
		 * StringBuilder notifyTypeBuilder = new StringBuilder(); for (Integer
		 * type : selected.get("type")) { notifyTypeBuilder.append(type + ",");
		 * }
		 */
		StringBuilder provinceBuilder = new StringBuilder();
		for (Integer province : selected.get("province")) {
			provinceBuilder.append(province + ",");
		}
		String industry = industryBuilder.substring(0,
				industryBuilder.length() - 1);
		String property = propertyBuilder.substring(0,
				propertyBuilder.length() - 1);
		String province = provinceBuilder.substring(0,
				provinceBuilder.length() - 1);
		/*
		 * String notifyType = notifyTypeBuilder.substring(0,
		 * notifyTypeBuilder.length() - 1);
		 */

		UserPreference preference = new UserPreference();
		preference.setProvince(province);
		preference.setCompanyType(property);
		preference.setCompanyIndustry(industry);
		this.loginUser.setPreference(preference);
		savePreference(preference);
		NetApiClient.setPreference(this, getLoginUid(), industry, property,
				province);
	}

	/**
	 * 加入校园宣讲会
	 * 
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public void joinCareerTalk(int careerTalkID, boolean join)
			throws AppException {
		Log.i("test", "update_join");
		getCareerTalkManager().updateJoin(careerTalkID, join);
		if (isNetworkConnected()) {
			NetApiClient
					.joinCareerTalk(this, getLoginUid(), careerTalkID, join);
		}
		Log.i("test", "join complete");
	}

	/**
	 * 点击校园宣讲会
	 * 
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public void clickCareerTalk(int careerTalkID) throws AppException {
		if (isNetworkConnected()) {
			NetApiClient.clickCareerTalk(this, careerTalkID);
		}
	}

	public List<BBSTopic> getUserTopics() {
		Log.i("bug", "get user topic");
		List<BBSTopic> list = getBBSTopicManager().getAllData();
		if (list == null)
			return new ArrayList<BBSTopic>();
		return list;
	}

	public List<BBSTopic> getUserTopicsFromInternet() throws AppException {
		List<BBSTopic> list = NetApiClient
				.getUserTopicList(this, getLoginUid());
		if (list == null)
			return new ArrayList<BBSTopic>();
		return list;
	}

	public List<BBSReply> getUserReplyByOthers() throws AppException {
		String key = "reply_by_other" + "_" + getLoginUid();
		ArrayList<BBSReply> list = new ArrayList<BBSReply>();
		replyByLoadFromDisk = false;
		if (isNetworkConnected()) {
			try {
				list = NetApiClient.getReplyByOtherList(this, getLoginUid());
				if (list != null) {
					saveObject(list, key);
				}
				if (list == null)
					return new ArrayList<BBSReply>();
			} catch (AppException e) {
				list = (ArrayList<BBSReply>) readObject(key);
				replyByLoadFromDisk = true;
				if (list == null)
					throw e;
			}
		} else {
			list = (ArrayList<BBSReply>) readObject(key);
			replyByLoadFromDisk = true;
			if (list == null)
				list = new ArrayList<BBSReply>();
		}
		Log.i("bug", "reply complete");
		return list;
	}

	public List<BBSReply> getUserReplyToOthers() throws AppException {
		String key = "reply_to_other" + "_" + getLoginUid();
		replyToLoadFromDisk = false;
		ArrayList<BBSReply> list = new ArrayList<BBSReply>();
		if (isNetworkConnected()) {
			try {
				list = NetApiClient.getReplyToOtherList(this, getLoginUid());
				if (list != null) {
					saveObject(list, key);
				}
			} catch (AppException e) {
				list = (ArrayList<BBSReply>) readObject(key);
				replyToLoadFromDisk = true;
				if (list == null)
					throw e;
			}
		} else {
			list = (ArrayList<BBSReply>) readObject(key);
			replyToLoadFromDisk = true;
			if (list == null)
				list = new ArrayList<BBSReply>();
		}
		return list;
	}

	public void saveUserTopic(int topicID, String title, String createdTime) {
		getBBSTopicManager().add(topicID, title, createdTime);
	}

	/*	*//**
	 * 加入讨论组
	 * 
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	/*
	 * public Result joinBBSSection(String companyName, boolean join) throws
	 * AppException { return NetApiClient.joinBBSSection(this,
	 * getLoginUid(),companyName, join); }
	 */

	/*
	 * 获取公司详细
	 * 
	 * @param companyID 公司ID
	 * 
	 * @return
	 * 
	 * @throws AppException
	 */
	public Company getCompanyDetail(int companyID) throws AppException {
		String key = "company_" + companyID;
		Company company;
		companyLoadFromDisk = false;
		if (isNetworkConnected()) {
			try {
				company = NetApiClient.getCompanyInfo(this, companyID);
				if (company != null) {
					saveObject(company, key);
				}
			} catch (AppException e) {
				company = (Company) readObject(key);
				companyLoadFromDisk = true;
				if (company == null)
					throw e;
			}
		} else {
			company = (Company) readObject(key);
			companyLoadFromDisk = true;
			if (company == null)
				company = new Company();
		}
		return company;
	}

	public void saveCompanyInfo(int recruitID, Company company) {
		getRecruitManager().updateCompanyInfo(recruitID, company);
	}

	public void saveRecruitDescription(int recruitID, String description) {
		getRecruitManager().updateRecruitDescription(recruitID, description);
	}

	public void saveRecruitContact(int recruitID, String contact) {
		getRecruitManager().updateRecruitContact(recruitID, contact);
	}

	/**
	 * 获取主题回帖
	 * 
	 * @param topicID
	 * @param pageIndex
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	// TODO
	public ArrayList<BBSReply> getBBSReplyList(int topicID, int pageIndex,
			boolean isRefresh) throws AppException {
		String key = "bbsreply" + "_" + topicID + "_" + pageIndex;
		ArrayList<BBSReply> list = new ArrayList<BBSReply>();
		commentsLoadFromDisk = false;
		if (isNetworkConnected()) {
			try {
				list = NetApiClient.getBBSReplyList(this, topicID, pageIndex);
				if (list != null && pageIndex == 0) {
					saveObject(list, key);
				}
			} catch (AppException e) {
				list = (ArrayList<BBSReply>) readObject(key);
				commentsLoadFromDisk = true;
				if (list == null)
					throw e;
			}
		} else {
			list = (ArrayList<BBSReply>) readObject(key);
			commentsLoadFromDisk = true;
			if (list == null)
				list = new ArrayList<BBSReply>();
		}
		return list;
	}

	/**
	 * 获取讨论组
	 * 
	 * @param topicID
	 * @param pageIndex
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public List<BBSSection> getBBSSectionList(int pageIndex, int flag,
			int orderBy) throws AppException {
		String key = "bbssection" + "_" + flag + "_" + orderBy + "_"
				+ pageIndex;
		ArrayList<BBSSection> list = new ArrayList<BBSSection>();
		sectionLoadFromDisk = false;
		if (isNetworkConnected()) {
			try {
				list = NetApiClient.getBBSSectionList(this, getLoginUid(),
						pageIndex, orderBy, flag);
				if (list != null && pageIndex == 0) {
					saveObject(list, key);
				}
			} catch (AppException e) {
				sectionLoadFromDisk = true;
				list = (ArrayList<BBSSection>) readObject(key);
				if (list == null)
					throw e;
			}
		} else {
			sectionLoadFromDisk = true;
			list = (ArrayList<BBSSection>) readObject(key);
			if (list == null)
				list = new ArrayList<BBSSection>();
		}
		return list;
	}

	public List<BBSSection> getBBSSectionListByCompanyName(String companyName)
			throws AppException {
		return NetApiClient.searchBBSSectionList(this, companyName);
	}

	public BBSTopic getTopicDetail(int topicID) throws AppException {
		return NetApiClient.getBBSTopicDetail(this, topicID);
	}

	/**
	 * 获取主题回帖
	 * 
	 * @param topicID
	 * @param pageIndex
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	// TODO
	public List<BBSTopic> getBBSTopicList(int sectionID, int pageIndex,
			boolean isRefresh) throws AppException {
		Log.i("test", "getBBSTopicList");
		String key = "bbstopic" + "_" + sectionID + "_" + pageIndex + "_"
				+ AppConfig.PAGE_SIZE;
		ArrayList<BBSTopic> list = new ArrayList<BBSTopic>();
		topicsLoadFromDisk = false;
		if (isNetworkConnected()) {
			try {
				list = NetApiClient.getBBSTopicList(this, getLoginUid(),
						sectionID, pageIndex);
				if (list != null && pageIndex == 0) {
					saveObject(list, key);
				}
			} catch (AppException e) {
				topicsLoadFromDisk = true;
				list = (ArrayList<BBSTopic>) readObject(key);
				if (list == null)
					throw e;
			}
		} else {
			list = (ArrayList<BBSTopic>) readObject(key);
			topicsLoadFromDisk = true;
			if (list == null)
				list = new ArrayList<BBSTopic>();
		}
		return list;
	}

	public List<BBSTopic> getBBSTopicListByCompanyID(int companyID,
			int pageIndex) throws AppException {
		String key = "bbstopic" + "_" + companyID + "_" + pageIndex + "_"
				+ AppConfig.PAGE_SIZE;
		ArrayList<BBSTopic> list = new ArrayList<BBSTopic>();
		topicsLoadFromDisk = false;
		if (isNetworkConnected()) {
			try {
				list = NetApiClient.getBBSTopicListByCompanyID(this,
						getLoginUid(), companyID, pageIndex);
				if (list != null && pageIndex == 0) {
					saveObject(list, key);
				}
			} catch (AppException e) {
				topicsLoadFromDisk = true;
				list = (ArrayList<BBSTopic>) readObject(key);
				if (list == null)
					throw e;
			}
		} else {
			list = (ArrayList<BBSTopic>) readObject(key);
			topicsLoadFromDisk = true;
			if (list == null)
				list = new ArrayList<BBSTopic>();
		}
		return list;
	}

	/**
	 * 获取招聘详细信息
	 * 
	 * @param uid
	 * @param recruitID
	 * @return
	 * @throws AppException
	 */
	/*
	 * public void setRecruitDetail(Recruit recruit) throws AppException {
	 * if(isNetworkConnected()) { try{ NetApiClient.getRecruitDetail(this,
	 * recruit); if(recruit.getTopicID() != null){
	 * getRecruitManager().update_detail(recruit); } }catch(AppException e){
	 * throw e; } } }
	 */

	/**
	 * 获取招聘流程信息
	 * 
	 * @param recruitID
	 * @return
	 * @throws AppException
	 */
	public String getRecruitProcessInfo(int recruitID) throws AppException {
		return NetApiClient.getRecruitProcessInfo(this, recruitID);
	}

	/**
	 * 获取招聘职位描述信息
	 * 
	 * @param recruitID
	 * @return
	 * @throws AppException
	 */
	public Recruit getRecruitDetail(int recruitID) throws AppException {
		String key = "recruit_" + recruitID;
		Recruit recruit;
		recruitLoadFromDisk = false;
		if (isNetworkConnected()) {
			try {
				recruit = NetApiClient.getRecruitDetail(this, recruitID);
				if (recruit != null) {
					saveObject(recruit, key);
				}
			} catch (AppException e) {
				recruit = (Recruit) readObject(key);
				recruitLoadFromDisk = true;
				if (recruit == null)
					throw e;
			}
		} else {
			recruit = (Recruit) readObject(key);
			recruitLoadFromDisk = true;
			if (recruit == null)
				recruit = new Recruit();
		}
		return recruit;
	}

	/**
	 * 获取招聘列表信息从本地
	 * 
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public List<Recruit> getRecruitListFromDisk() {
		return getRecruitManager().getAllData();
	}

	/**
	 * 获取招聘列表信息从服务器
	 * 
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public List<Recruit> getRecruitListFromInternet(int pageIndex, int orderby,
			boolean famous) throws AppException {
		List<Recruit> list = NetApiClient.getRecruitList(this, getLoginUid(),
				pageIndex, orderby, famous);
		if (list == null)
			return new ArrayList<Recruit>();
		return list;
	}

	public List<Recruit> getRecruitListByCompanyName(String companyName)
			throws AppException {
		return NetApiClient.searchRecruitList(this, companyName);
	}

	public void saveRecruitList(List<Recruit> list) {
		getRecruitManager().saveList(list);
	}

	public List<Recruit> getRecruitFavorateList(boolean flag) {
		List<Recruit> recruits = getRecruitManager().getFavorates(flag);
		if (recruits == null)
			return new ArrayList<Recruit>();
		return recruits;
	}

	public List<CareerTalk> getCareerTalkFavorateList(boolean flag) {
		List<CareerTalk> careerTalks = getCareerTalkManager()
				.getFavorates(flag);
		if (careerTalks == null)
			return new ArrayList<CareerTalk>();
		return careerTalks;
	}

	public CareerTalk getCareerTalkDetail(int careerTalkID) throws AppException {
		return NetApiClient.getCareerTalkDetail(this, careerTalkID);
	}

	public List<CareerTalk> getCareerTalkListFromDisk() {
		return getCareerTalkManager().getAllData();
	}

	public List<CareerTalk> getCareerTalkListFromInternet(int pageIndex,
			int orderby, boolean famous) throws AppException {
		List<CareerTalk> list = NetApiClient.getCareerTalkList(this,
				getLoginUid(), pageIndex, orderby, famous);
		if (list == null)
			return new ArrayList<CareerTalk>();
		return list;
	}

	public List<CareerTalk> getCareerTalkListByCompanyName(String companyName)
			throws AppException {
		List<CareerTalk> list = NetApiClient.searchCareerTalkList(this,
				companyName);
		if (list == null)
			return new ArrayList<CareerTalk>();
		return list;
	}

	public void saveCareerTalkList(List<CareerTalk> list) {
		getCareerTalkManager().saveList(list);
	}

	/**
	 * 删除评论
	 * 
	 * @param uid
	 * @param recruitID
	 * @return
	 * @throws AppException
	 */
	public Result delReply(int bbsReplyID) throws AppException {
		// CareerTalk careerTalk = null;
		// String key = "";
		// //TODO
		// if(isNetworkConnected() &&
		// (!getCareerTalkManager().exists(bbsReplyID) || isRefresh)) {
		// try{
		// careerTalk = NetApiClient.getCareerTalkDetail(this, getLoginUid(),
		// careerTalkID);
		// if(careerTalk != null){
		// getCareerTalkManager().add(careerTalk);
		// }
		// }catch(AppException e){
		// careerTalk = getCareerTalkManager().get(careerTalkID);
		// if(careerTalk == null)
		// throw e;
		// }
		// } else {
		// careerTalk = getCareerTalkManager().get(careerTalkID);
		// if(careerTalk == null)
		// return null;
		// }
		// return careerTalk;
		return NetApiClient.delBBSReply(this, bbsReplyID);
	}

	/**
	 * 发表评论
	 * 
	 * @param topicID
	 * @param content
	 *            发表评论的内容
	 * @return
	 * @throws AppException
	 */
	public BBSReply addReply(int topicID, String content) throws AppException {
		BBSReply reply = new BBSReply();
		reply.setTopicID(topicID);
		reply.setUserID(getLoginUid());
		reply.setContent(content);
		reply.setUserName("test");
		return NetApiClient.newBBSReply(this, topicID, getLoginUid(), content,
				0);
	}

	/**
	 * 发私信
	 * 
	 * @param topicID
	 * @param content
	 *            发表评论的内容
	 * @return
	 * @throws AppException
	 */
	/*
	 * public UserMessage sendPrivateMessage(String userID, String title, String
	 * content) throws AppException { return NetApiClient.(this, topicID,
	 * getLoginUid(), content, 0); }
	 */

	/**
	 * 发表评论
	 * 
	 * @param topicID
	 * @param content
	 *            发表评论的内容
	 * @return
	 * @throws AppException
	 */
	public BBSTopic addTopic(int sectionID, String title, String body)
			throws AppException {
		BBSTopic topic = new BBSTopic();
		topic.setTitle(title);
		topic.setBody(body);
		topic.setUserID(getLoginUid());
		topic.setUserName("test");
		topic.setCreatedTime(StringUtils.getCurrentTimeStamp());
		topic.setLastReplyTime(StringUtils.getCurrentTimeStamp());
		topic.setTopicID(NetApiClient.newBBSTopic(this, sectionID, topic));
		return topic;
	}

	/**
	 * 发表评论
	 * 
	 * @param topicID
	 * @param content
	 *            发表评论的内容
	 * @return
	 * @throws AppException
	 */
	public BBSTopic addTopicByCompanyID(int companyID, String title, String body)
			throws AppException {
		BBSTopic topic = new BBSTopic();
		topic.setTitle(title);
		topic.setBody(body);
		topic.setUserID(getLoginUid());
		topic.setUserName("test");
		topic.setCreatedTime(StringUtils.getCurrentTimeStamp());
		topic.setTopicID(NetApiClient.newBBSTopicByCompanyID(this, companyID,
				topic));
		return topic;
	}

	/**
	 * 回复某条评论
	 * 
	 * @param topicID
	 * @param content
	 *            发表评论的内容
	 * @param replyid
	 *            表示被回复的单个评论id
	 * @param ouid
	 *            表示该评论的原始作者id
	 * @return
	 * @throws AppException
	 */
	public BBSReply replyComment(int topicID, String content, int replyid)
			throws AppException {
		return NetApiClient.newBBSReply(this, topicID, getLoginUid(), content,
				replyid);
	}

	/**
	 * 保存登录信息
	 * 
	 * @param username
	 * @param pwd
	 */
	public void saveLoginInfo() {
		Log.i("user", "save user info");
		Log.i("user", "schoolname is " + loginUser.getSchoolName());
		setProperties(new Properties() {
			{
				setProperty(
						"user.uid",
						CryptoUtils.encode(app_key,
								String.valueOf(loginUser.getUid())));
				if (loginUser.getName() != null)
					setProperty("user.name", loginUser.getName());
				Log.i("test", "set gender");
				if (loginUser.getGender() != 0)
					setProperty("user.gender", loginUser.getGender() + "");

				if (loginUser.getHasFace() != 0) {
					setProperty("user.hasface",
							FileUtils.getFileName(loginUser.getHasFace() + ""));// 用户头像-文件名
				}
				if (loginUser.getEmail() != null)
					setProperty("user.email", loginUser.getEmail());
				if (loginUser.getSchoolName() != null)
					setProperty("user.school", loginUser.getSchoolName());
				if (loginUser.getMajorName() != null)
					setProperty("user.major", loginUser.getMajorName());
			}
		});
	}

	public void savePreferenceNotifyType(final String preference) {
		setProperties(new Properties() {
			{
				if (preference != null)
					setProperty("preference.notifyType", preference);
			}
		});
	}

	public void savePreference(final UserPreference preference) {
		setProperties(new Properties() {
			{
				setProperty("preference.companyType",
						preference.getCompanyType());
				setProperty("preference.companyIndustry",
						preference.getCompanyIndustry());
				setProperty("preference.province", preference.getProvince());
				setProperty("preference.notifyType", preference.getNotifyType());
			}
		});
	}

	// if flag is true, truncate all , else not truncate my bbstopic
	public void truncateSqlite(boolean flag) {
		getRecruitManager().truncateObsolete();
		getCareerTalkManager().truncateObsolete();
		getMessageListManager().truncate();
		if (flag) {
			getScheduleManager().truncate();
			getBBSTopicManager().truncate();
		}
	}

	public void clearObsoleteSqlite() {
		setLvCareerTalkList(new ArrayList<CareerTalk>());
		setLvRecruitList(new ArrayList<Recruit>());
		getRecruitManager().truncateObsolete();
		getCareerTalkManager().truncateObsolete();
	}

	public void truncateRecruitAndCareer() {
		getRecruitManager().truncate();
		getCareerTalkManager().truncate();
	}

	/**
	 * 清除登录信息
	 */
	public void cleanLoginInfo() {
		this.loginUser = null;
		removeProperty("user.uid", "user.name", "user.hasface", "user.email",
				"user.school", "user.major", "user.gender",
				"preference.companyType", "preference.companyIndustry",
				"preference.province", "preference.position",
				"preference.notifyType");
		cleanCookie();
		truncateSqlite(true);
	}

	public String getIsLogin() {
		return getProperty("user.uid");
	}

	public void saveSettingsBackground(boolean backgroundNotice) {
		this.loginUser.setBackgroundNotice(backgroundNotice);
		setProperty("background", backgroundNotice + "");
	}

	public void saveSettingsPicture(boolean showPicture) {
		this.loginUser.setShowPicture(showPicture);
		setProperty("showpicture", showPicture + "");
	}

	/**
	 * 获取登录信息
	 * 
	 * @return
	 */
	public User getLoginInfo() {
		User lu = new User();
		lu.setUid(CryptoUtils.decode(app_key, getProperty("user.uid")));
		lu.setName(getProperty("user.name"));
		if (getProperty("user.hasface") != null) {
			lu.setHasFace(Integer.parseInt(getProperty("user.hasface")));
		}
		lu.setSchoolName(getProperty("user.school"));
		lu.setEmail(getProperty("user.email"));
		if (getProperty("user.background") != null) {
			try {
				lu.setBackgroundNotice(Boolean
						.parseBoolean(getProperty("user.background")));
			} catch (Exception e) {
				e.printStackTrace();
				lu.setBackgroundNotice(true);
			}
		} else {
			lu.setBackgroundNotice(true);
		}
		if (getProperty("user.showpicture") != null) {
			try {
				lu.setShowPicture(Boolean
						.parseBoolean(getProperty("user.showpicture")));
			} catch (Exception e) {
				e.printStackTrace();
				lu.setShowPicture(true);
			}
		} else {
			lu.setShowPicture(true);
		}
		if (getProperty("user.gender") != null)
			lu.setGender(Integer.parseInt(getProperty("user.gender")));
		lu.setMajorName(getProperty("user.major"));
		if (getProperty("preference.companyType") == null)
			return lu;
		UserPreference preference = new UserPreference();
		preference.setCompanyType(getProperty("preference.companyType"));
		preference
				.setCompanyIndustry(getProperty("preference.companyIndustry"));
		preference.setProvince(getProperty("preference.province"));
		// preference.setPosition(getProperty("user.preference.position"));
		preference.setNotifyType(getProperty("preference.notifyType"));
		lu.setPreference(preference);
		return lu;
	}

	/**
	 * 保存用户头像
	 * 
	 * @param fileName
	 * @param bitmap
	 */
	/*
	 * public void saveUserFace(String fileName,Bitmap bitmap) { try {
	 * ImageUtils.saveImage(this, fileName, bitmap); } catch (IOException e) {
	 * e.printStackTrace(); } }
	 */

	/**
	 * 获取用户头像
	 * 
	 * @param key
	 * @return
	 * @throws AppException
	 */
	public Bitmap getUserFace(String key) throws AppException {
		FileInputStream fis = null;
		try {
			fis = openFileInput(key);
			return BitmapFactory.decodeStream(fis);
		} catch (Exception e) {
			throw AppException.run(e);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 是否加载显示文章图片
	 * 
	 * @return
	 */
	public boolean isLoadImage() {
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
		// 默认是加载的
		if (StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}

	/**
	 * 设置是否加载文章图片
	 * 
	 * @param b
	 */
	public void setConfigLoadimage(boolean b) {
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}

	/**
	 * 是否发出提示音
	 * 
	 * @return
	 */
	public boolean isVoice() {
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		// 默认是开启提示声音
		if (StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}

	/**
	 * 设置是否发出提示音
	 * 
	 * @param b
	 */
	public void setConfigVoice(boolean b) {
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}

	/**
	 * 是否启动检查更新
	 * 
	 * @return
	 */
	public boolean isCheckUp() {
		String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
		// 默认是开启
		if (StringUtils.isEmpty(perf_checkup))
			return true;
		else
			return StringUtils.toBool(perf_checkup);
	}

	/**
	 * 设置启动检查更新
	 * 
	 * @param b
	 */
	public void setConfigCheckUp(boolean b) {
		setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
	}

	/**
	 * 是否左右滑动
	 * 
	 * @return
	 */
	public boolean isScroll() {
		String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
		// 默认是关闭左右滑动
		if (StringUtils.isEmpty(perf_scroll))
			return false;
		else
			return StringUtils.toBool(perf_scroll);
	}

	/**
	 * 设置是否左右滑动
	 * 
	 * @param b
	 */
	public void setConfigScroll(boolean b) {
		setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
	}

	/**
	 * 是否Https登录
	 * 
	 * @return
	 */
	public boolean isHttpsLogin() {
		String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
		// 默认是http
		if (StringUtils.isEmpty(perf_httpslogin))
			return false;
		else
			return StringUtils.toBool(perf_httpslogin);
	}

	/**
	 * 设置是是否Https登录
	 * 
	 * @param b
	 */
	public void setConfigHttpsLogin(boolean b) {
		setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
	}

	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie() {
		removeProperty(AppConfig.CONF_COOKIE);
	}

	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存是否失效
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > AppConfig.CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		// 清除数据缓存
		clearCacheFolder(getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(getCacheDir(), System.currentTimeMillis());
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),
					System.currentTimeMillis());
		}
		// 清除编辑器保存的临时内容
		Properties props = getProperties();
		for (Object key : props.keySet()) {
			String _key = key.toString();
			if (_key.startsWith("temp"))
				removeProperty(_key);
		}
	}

	/**
	 * 清除app 内部数据
	 */
	public void initAppList() {
		this.lvBBSSectionList = new ArrayList<BBSSection>();
		this.lvCareerTalkFavorateList = new ArrayList<CareerTalk>();
		this.lvRecruitFavorateList = new ArrayList<Recruit>();
		this.lvCareerTalkList = new ArrayList<CareerTalk>();
		this.lvRecruitList = new ArrayList<Recruit>();
		this.lvUserTopicList = new ArrayList<BBSTopic>();
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param numDays
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 将对象保存到内存缓存中
	 * 
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}

	/**
	 * 从内存缓存中获取对象
	 * 
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key) {
		return memCacheRegion.get(key);
	}

	/**
	 * 保存磁盘缓存
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput("cache_" + key + ".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取磁盘缓存数据
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try {
			fis = openFileInput("cache_" + key + ".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	/**
	 * 获取内存中保存图片的路径
	 * 
	 * @return
	 */
	public String getSaveImagePath() {
		return saveImagePath;
	}

	/**
	 * 设置内存中保存图片的路径
	 * 
	 * @return
	 */
	public void setSaveImagePath(String saveImagePath) {
		this.saveImagePath = saveImagePath;
	}

}
