package com.campusrecruit.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.campusrecruit.activity.BBSTopicListActivity;
import com.campusrecruit.activity.CareerTalkDetailActivity;
import com.campusrecruit.activity.CommentPubActivity;
import com.campusrecruit.activity.CompanyIndustryActivity;
import com.campusrecruit.activity.CompanyTypeActivity;
import com.campusrecruit.activity.DataSourcesActivity;
import com.campusrecruit.activity.LoginActivity;
import com.campusrecruit.activity.LoginDialogActivity;
import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.activity.MajorActivity;
import com.campusrecruit.activity.PrivateMessageActivity;
import com.campusrecruit.activity.PrivateMessageListActivity;
import com.campusrecruit.activity.ProvinceActivity;
import com.campusrecruit.activity.RadioAlarmSelectActivity;
import com.campusrecruit.activity.RecommendActivity;
import com.campusrecruit.activity.RecruitDetailActivity;
import com.campusrecruit.activity.RecruitDetailTabActivity;
import com.campusrecruit.activity.RecruitSimpleDetailActivity;
import com.campusrecruit.activity.RegisterActivity;
import com.campusrecruit.activity.ScheduleActivity;
import com.campusrecruit.activity.SchoolActivity;
import com.campusrecruit.activity.SchoolMultipleActivity;
import com.campusrecruit.activity.StartActivity;
import com.campusrecruit.activity.TopicDetailActivity;
import com.campusrecruit.activity.TopicPubActivity;
import com.campusrecruit.activity.TutorialHintActivity;
import com.campusrecruit.activity.UserCenterActivity;
import com.campusrecruit.activity.UserFavroateActivity;
import com.campusrecruit.activity.UserInfoActivity;
import com.campusrecruit.activity.UserReplyActivity;
import com.campusrecruit.activity.UserSettingActivity;

import com.campusrecruit.adapter.GridViewFaceAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.app.AppManager;
import com.campusrecruit.bean.AccessInfo;
import com.campusrecruit.bean.BBSSection;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Notice;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.URLs;
import com.campusrecruit.net.NetApiClient;
import com.pcncad.campusRecruit.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UIHelper {

	public final static int LISTVIEW_DATATYPE_CAREER_TALK = 0x01;
	public final static int LISTVIEW_DATATYPE_RECRUIT = 0x02;

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_SORT = 0x04;
	public final static int LISTVIEW_ACTION_SEARCH = 0x05;

	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;

	public final static int LISTVIEW_DATATYPE_NEWS = 0x01;
	public final static int LISTVIEW_DATATYPE_BLOG = 0x02;
	public final static int LISTVIEW_DATATYPE_POST = 0x03;
	public final static int LISTVIEW_DATATYPE_TWEET = 0x04;
	public final static int LISTVIEW_DATATYPE_ACTIVE = 0x05;
	public final static int LISTVIEW_DATATYPE_MESSAGE = 0x06;
	public final static int LISTVIEW_DATATYPE_COMMENT = 0x07;
	
	public final static int REQUEST_CODE_FOR_RESULT = 0x01;
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;
	public final static int REQUEST_CODE_FOR_REPLYBY = 0x03;
	public final static int REQUEST_CODE_FOR_REPLYTO = 0x04;
	public final static int REQUEST_CODE_FOR_FAVORATE = 0x04;
	public final static int REQUEST_CODE_FOR_RECOMMEND = 0x05;
	public final static int REQUEST_CODE_FOR_SCHEDULE = 0x06;
	public final static int REQUEST_CODE_FOR_SETTINGS = 0x07;

	public final static int REQUEST_PROVINCE_FOR_RESULT = 0x08;
	public final static int REQUEST_COMPANY_TYPE_FOR_RESULT = 0x09;
	public final static int REQUEST_COMPANY_INDUSTRY_RESULT = 0x10;
	public final static int REQUEST_DATA_SOURCE_RESULT = 0x11;
	
	public final static int REQUEST_SCHOOL_RESULT = 0x12;
	public final static int REQUEST_MAJOR_RESULT = 0x13;
	public final static int REQUEST_SET_ALARM_TIME_RESULT = 0x13;
	
	public final static int REQUEST_CODE_FOR_TUTORIAL_MAIN_1 = 0x14;
	public final static int REQUEST_CODE_FOR_TUTORIAL_MAIN_2 = 0x15;
	public final static int REQUEST_CODE_FOR_TUTORIAL_RECRUIT_DETAIL = 0x16;
	public final static int REQUEST_CODE_FOR_TUTORIAL_CAREER_FAVORATE = 0x17;
	public final static int REQUEST_CODE_FOR_TUTORIAL_RECRUIT_FAVORATE = 0x18;
	
	public final static int RESPONCE_SELECTION = 0x01;
	
	public final static int RESULT_CODE_FOR_LOGOUT = 0x01;

	/** 表情图片匹配 */
	private static Pattern facePattern = Pattern
			.compile("\\[{1}([0-9]\\d*)\\]{1}");

	/** 全局web样式 */
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	/**
	 * 显示首页
	 * 
	 * @param activity
	 */
	public static void showHome(Activity activity) {
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}
	
	public static void showHomeFromRecommend(Activity activity) {
		Intent intent = new Intent(activity, MainActivity.class);
		intent.putExtra("needRefresh", true);
		activity.startActivity(intent);
		activity.finish();
	}
	
	public static void showHomeTutorial1(Activity activity) {
		Intent intent = new Intent(activity, TutorialHintActivity.class);
		intent.putExtra("image", R.drawable.tutorial_main_1);
		activity.startActivityForResult(intent, REQUEST_CODE_FOR_TUTORIAL_MAIN_1);
	}
	
	public static void showHomeTutorial2(Activity activity) {
		Intent intent = new Intent(activity, TutorialHintActivity.class);
		intent.putExtra("image", R.drawable.tutorial_main_2);
		activity.startActivityForResult(intent, REQUEST_CODE_FOR_TUTORIAL_MAIN_2);
	}
	
	public static void showRecruitDetailTutorial(Activity activity) {
		Intent intent = new Intent(activity, TutorialHintActivity.class);
		intent.putExtra("image", R.drawable.tutorial_recruit_detail);
		activity.startActivityForResult(intent, REQUEST_CODE_FOR_TUTORIAL_MAIN_1);
	}
	
	public static void showCareerFavorateTutorial(Activity activity) {
		Intent intent = new Intent(activity, TutorialHintActivity.class);
		intent.putExtra("image", R.drawable.tutorial_3);
		activity.startActivityForResult(intent, REQUEST_CODE_FOR_TUTORIAL_CAREER_FAVORATE);
		
	}
	
	public static void showRecruitFavorateTutorial(Activity activity) {
		Intent intent = new Intent(activity, TutorialHintActivity.class);
		intent.putExtra("image", R.drawable.tutorial_4);
		activity.startActivityForResult(intent, REQUEST_CODE_FOR_TUTORIAL_RECRUIT_FAVORATE);
		
	}
	
	/**
	 * 显示注册界面
	 * 
	 * @param activity
	 */
	public static void showRegiste(Activity activity){
		Intent intent = new Intent(activity, RegisterActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 显示欢迎页
	 * 
	 * @param activity
	 */
	public static void showStart(Activity activity) {
		Intent intent = new Intent(activity, StartActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}
	
	public static void showStart(Activity activity, Boolean isDialog) {
		Intent intent = new Intent(activity, StartActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 显示欢迎页
	 * 
	 * @param activity
	 */
	public static void showSettings(Activity activity) {
		Intent intent = new Intent(activity, UserSettingActivity.class);
		activity.startActivity(intent);
	}
	
	/**
	 * 显示欢迎页
	 * 
	 * @param activity
	 */
	public static void showSettingsForResult(Activity activity) {
		Intent intent = new Intent(activity, UserSettingActivity.class);
		activity.startActivityForResult(intent, UIHelper.REQUEST_CODE_FOR_SETTINGS);
	}

	/**
	 * 显示登陆界面
	 * 
	 * @param activity
	 */
	public static void showLogin(Activity activity) {
		Intent intent = new Intent(activity, LoginActivity.class);
		activity.startActivity(intent);
		activity.finish();
		/*
		 * Intent intentMain = new Intent(activity, MainActivity.class);
		 * activity.startActivity(intentMain); activity.finish();
		 */
	}

	/**
	 * 显示登陆界面
	 * 
	 * @param activity
	 */
	public static void showWeiboLogin(Activity activity) {
		SinaWeiboHelper.authorizeLogin(activity);
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 显示登陆界面
	 * 
	 * @param activity
	 */
	public static void showRegister(Activity activity, boolean flag) {
		Intent intent = new Intent(activity, RegisterActivity.class);
		intent.putExtra("isInit", flag);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 显示登陆界面
	 * 
	 * @param activity
	 */
	public static void showRecommends(Activity activity) {
		Intent intent = new Intent(activity, RecommendActivity.class);
		intent.putExtra("init", true);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 选择省份
	 * 
	 * @param activity
	 */
	public static void showProvince(Activity activity,
			ArrayList<Integer> selectProvinceList) {
		Intent intent = new Intent(activity, ProvinceActivity.class);
		intent.putIntegerArrayListExtra("selectedList", selectProvinceList);
		activity.startActivityForResult(intent, REQUEST_PROVINCE_FOR_RESULT);
	}
	
	/**
	 * 选择省份
	 * 
	 * @param activity
	 */
	public static void showProvince(Fragment fragment,
			ArrayList<Integer> selectProvinceList) {
		Intent intent = new Intent(fragment.getActivity(), ProvinceActivity.class);
		intent.putIntegerArrayListExtra("selectedList", selectProvinceList);
		fragment.startActivityForResult(intent, REQUEST_PROVINCE_FOR_RESULT);
	}
	
	/**
	 * 选择公司行业
	 * 
	 * @param activity
	 */
	public static void showCompanyIndustry(Fragment fragment,
			ArrayList<Integer> selectedList) {
		Intent intent = new Intent(fragment.getActivity(), CompanyIndustryActivity.class);
		intent.putIntegerArrayListExtra("selectedList", selectedList);
		fragment.startActivityForResult(intent, REQUEST_COMPANY_INDUSTRY_RESULT);
	}
	
	
	/**
	 * 选择公司性质
	 * 
	 * @param activity
	 */
	public static void showCompanyType(Fragment fragment,
			ArrayList<Integer> selectedList) {
		Intent intent = new Intent(fragment.getActivity(), CompanyTypeActivity.class);
		intent.putIntegerArrayListExtra("selectedList", selectedList);
		fragment.startActivityForResult(intent, REQUEST_COMPANY_TYPE_FOR_RESULT);
	}
	
	/**
	 * 选择公司性质
	 * 
	 * @param activity
	 */
	public static void showDataSources(Fragment fragment,
			ArrayList<Integer> selectedList) {
		Intent intent = new Intent(fragment.getActivity(), DataSourcesActivity.class);
		intent.putIntegerArrayListExtra("selectedList", selectedList);
		fragment.startActivityForResult(intent, REQUEST_DATA_SOURCE_RESULT);
	}
	
	/**
	 * 选择学校
	 * 
	 * @param activity
	 */
	public static void showSchoolMultiple(Fragment fragment,
			ArrayList<Integer> selectedList) {
		Intent intent = new Intent(fragment.getActivity(), SchoolMultipleActivity.class);
		intent.putExtra("selectedList", selectedList);
		fragment.startActivityForResult(intent, REQUEST_SCHOOL_RESULT);
	}
	
	/**
	 * 选择学校
	 * 
	 * @param activity
	 */
	public static void showSchool(Activity activity,
			String selectStr) {
		Intent intent = new Intent(activity, SchoolActivity.class);
		intent.putExtra("selectStr", selectStr);
		activity.startActivityForResult(intent, REQUEST_SCHOOL_RESULT);
	}
	
	/**
	 * 选择专业
	 * 
	 * @param activity
	 */
	public static void showMajor(Activity activity,
			String selectStr) {
		Intent intent = new Intent(activity, MajorActivity.class);
		intent.putExtra("selectStr", selectStr);
		activity.startActivityForResult(intent, REQUEST_MAJOR_RESULT);
	}
	
	public static void showAlarmTime(Activity activity,
			String selectStr) {
		Intent intent = new Intent(activity, RadioAlarmSelectActivity.class);
		intent.putExtra("selectStr", selectStr);
		activity.startActivityForResult(intent, REQUEST_SET_ALARM_TIME_RESULT);
	}
/*	*//**
	 * 选择公司性质
	 * 
	 * @param activity
	 *//*
	public static void showCompanyType(Activity activity,
			ArrayList<Integer> selectedList) {
		Intent intent = new Intent(activity, CompanyIndustryActivity.class);
		intent.putIntegerArrayListExtra("selectedList", selectedList);
		activity.startActivityForResult(intent, REQUEST_COMPANY_TYPE_FOR_RESULT);
	}*/

	/**
	 * Checkbox选择完毕
	 * 
	 * @param activity
	 * @param selection 选择列表
	 * @param text 当仅仅选择一个时，其值
	 */
	public static void closeSelection(Activity activity,
			ArrayList<Integer> selection, String text) {
		Log.i("selection","closeselection");
		Intent intent = new Intent();
		intent.putIntegerArrayListExtra("selection", selection);
		if (text != null)
			intent.putExtra("text", text);
		activity.setResult(Activity.RESULT_OK, intent);
		activity.finish();
	}
	
	/**
	 * RadioButton选择完毕
	 * 
	 * @param activity
	 * @param selection 选择列表
	 * @param text 当仅仅选择一个时，其值
	 */
	public static void closeRadio(Activity activity,
			String selection) {
		Intent intent = new Intent();
		intent.putExtra("selectStr", selection);
		activity.setResult(Activity.RESULT_OK, intent);
		activity.finish();
	}

	/**
	 * 跳转到登录页面
	 * 
	 * @param activity
	 */
	public static void redirectToLogin(Activity activity) {
		Intent intent = new Intent(activity, LoginActivity.class);
		activity.startActivity(intent);
	}

	/**
	 * 跳转到登录页面
	 * 
	 * @param activity
	 */
	public static void showLoginDialog(Activity activity) {
		Intent intent = new Intent(activity, LoginDialogActivity.class);
		activity.startActivity(intent);
	}

	/**
	 * 跳转到登录页面
	 * 
	 * @param activity
	 */
	public static void showUserInfo(Activity activity, String uid) {
		Intent intent = new Intent(activity, UserInfoActivity.class);
		intent.putExtra("userID", uid);
		Log.i("user", "start activity" + uid);
		activity.startActivity(intent);
	}

	/**
	 * 跳转到登录页面
	 * 
	 * @param activity
	 */
	public static void showPrivateMessage(Activity activity, String uid) {
		Intent intent = new Intent(activity, PrivateMessageListActivity.class);
		intent.putExtra("userid", uid);
		activity.startActivity(intent);
	}

	/**
	 * 跳转到登录页面
	 * 
	 * @param activity
	 */
	public static void showUserFavroate(Activity activity) {
		Intent intent = new Intent(activity, UserFavroateActivity.class);
		activity.startActivityForResult(intent, UIHelper.REQUEST_CODE_FOR_FAVORATE);
	}

	/**
	 * 跳转到登录页面
	 * 
	 * @param activity
	 */
	public static void showSchedule(Activity activity) {
		Intent intent = new Intent(activity, ScheduleActivity.class);
		activity.startActivityForResult(intent, UIHelper.REQUEST_CODE_FOR_SCHEDULE);
	}

	/**
	 * 显示评论回复页面
	 * 
	 * @param context
	 * @param topicID
	 * @param replyID
	 * @param replyUserID
	 * @param replyUserName
	 * @param replyContent
	 */
	public static void showCommentPub(Activity context, int topicID,
			int replyID, String replyUserName, String replyContent) {
		Intent intent = new Intent(context, CommentPubActivity.class);
		intent.putExtra("topicID", topicID);
		intent.putExtra("replyID", replyID);
		intent.putExtra("replyUserName", replyUserName);
		intent.putExtra("replyContent", replyContent);
		context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT);
	}

	/**
	 * 显示新建主题页面
	 * 
	 * @param context
	 */
	public static void showTopicPub(Activity context, int sectionID,
			int companyID) {
		Intent intent = new Intent(context, TopicPubActivity.class);
		intent.putExtra("sectionID", sectionID);
		intent.putExtra("companyID", companyID);
		context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT);
	}

	/**
	 * 完成新建主题
	 * 
	 * @param context
	 */
	public static void completeTopicPub(Activity context, BBSTopic bbsTopic) {
		// 返回刚刚发表的主题
		Intent intent = new Intent();
		intent.putExtra("topic", bbsTopic);
		context.setResult(Activity.RESULT_OK, intent);
		// 跳转到文章详情
		context.finish();
	}

	/**
	 * 显示校招详情
	 * 
	 * @param context
	 * @param recruit
	 *            招聘基础信息
	 * @param flag
	 *            若为true则显示招聘信息，若为false显示评论信息
	 */
	public static void showRecruitDetail(Context context, Recruit recruit,
			boolean flag) {
		Intent intent = new Intent(context, RecruitDetailTabActivity.class);
		intent.putExtra("recruit", recruit);
		intent.putExtra("flag", flag);
		context.startActivity(intent);
	}
	
	/**
	 * 显示校招详情
	 * 
	 * @param context
	 * @param recruit
	 *            招聘基础信息
	 * @param flag
	 *            若为true则显示招聘信息，若为false显示评论信息
	 */
	public static void showRecruitSimpleDetail(Context context, Recruit recruit,
			boolean flag) {
		Intent intent = new Intent(context, RecruitSimpleDetailActivity.class);
		intent.putExtra("recruit", recruit);
		intent.putExtra("flag", flag);
		context.startActivity(intent);
	}

	/**
	 * 显示宣讲会详情
	 * 
	 * @param context
	 * @param careerTalk
	 *            宣讲会基础信息
	 * @param flag
	 *            若为true则显示招聘信息，若为false显示评论信息
	 */
	public static void showCareerTalkDetail(Context context,
			CareerTalk careerTalk, boolean flag) {
		MainActivity.toPager = 1;
		Intent intent = new Intent(context, CareerTalkDetailActivity.class);
		intent.putExtra("careerTalk", careerTalk);
		intent.putExtra("flag", flag);
		context.startActivity(intent);
	}

	/**
	 * 显示宣讲会详情
	 * 
	 * @param context
	 * @param careerTalk
	 *            宣讲会基础信息
	 * @param flag
	 *            若为true则显示招聘信息，若为false显示评论信息
	 */
	public static void showCareerTalkDetailByCalendar(Context context,
			int careerTalkID) {
		Intent intent = new Intent(context, CareerTalkDetailActivity.class);
		intent.putExtra("careerTalkID", careerTalkID);
		Log.i("test", careerTalkID + "");
		context.startActivity(intent);
	}

	/**
	 * 显示Topic详情
	 * 
	 * @param context
	 * @param newsId
	 */
	public static void showTopicDetail(Context context, int topicID) {
		Intent intent = new Intent(context, TopicDetailActivity.class);
		intent.putExtra("topicID", topicID);
		context.startActivity(intent);
	}

	/**
	 * 显示Topic详情
	 * 
	 * @param context
	 * @param newsId
	 */
	public static void showTopicList(Context context, BBSSection section) {
		MainActivity.toPager = 2;
		Intent intent = new Intent(context, BBSTopicListActivity.class);
		intent.putExtra("section", section);
		context.startActivity(intent);
	}

	/**
	 * 显示留言对话页面
	 * 
	 * @param context
	 * @param catalog
	 * @param friendid
	 */
	/*
	 * public static void showMessageDetail(Context context, int friendid,
	 * String friendname) { Intent intent = new Intent(context,
	 * MessageDetail.class); intent.putExtra("friend_name", friendname);
	 * intent.putExtra("friend_id", friendid); context.startActivity(intent); }
	 *//**
	 * 显示留言回复界面
	 * 
	 * @param context
	 * @param friendId
	 *            对方id
	 * @param friendName
	 *            对方名称
	 */
	/*
	 * public static void showMessagePub(Activity context, int friendId, String
	 * friendName) { Intent intent = new Intent(); intent.putExtra("user_id",
	 * ((AppContext) context.getApplication()).getLoginUid());
	 * intent.putExtra("friend_id", friendId); intent.putExtra("friend_name",
	 * friendName); intent.setClass(context, MessagePub.class);
	 * context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT); }
	 *//**
	 * 显示转发留言界面
	 * 
	 * @param context
	 * @param friendName
	 *            对方名称
	 * @param messageContent
	 *            留言内容
	 */
	/*
	 * public static void showMessageForward(Activity context, String
	 * friendName, String messageContent) { Intent intent = new Intent();
	 * intent.putExtra("user_id", ((AppContext)
	 * context.getApplication()).getLoginUid()); intent.putExtra("friend_name",
	 * friendName); intent.putExtra("message_content", messageContent);
	 * intent.setClass(context, MessageForward.class);
	 * context.startActivity(intent); }
	 */

	/**
	 * 调用系统安装了的应用分享
	 * 
	 * @param context
	 * @param title
	 * @param url
	 */
	public static void showShareMore(Activity context, final String title,
			final String url) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
		intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
		context.startActivity(Intent.createChooser(intent, "选择分享"));
	}

	/**
	 * 分享到'新浪微博'或'腾讯微博'的对话框
	 * 
	 * @param context
	 *            当前Activity
	 * @param title
	 *            分享的标题
	 * @param url
	 *            分享的链接
	 */
	public static void showShareDialog(final Activity context,
			final String title, final String url) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setTitle(context.getString(R.string.share));
		builder.setItems(R.array.app_share_items,
				new DialogInterface.OnClickListener() {
					AppConfig cfgHelper = AppConfig.getAppConfig(context);
					AccessInfo access = cfgHelper.getAccessInfo();

					public void onClick(DialogInterface arg0, int arg1) {
						switch (arg1) {
						case 0:// 新浪微博
								// 分享的内容
							final String shareMessage = title + " " + url;
							// 初始化微博
							if (SinaWeiboHelper.isWeiboNull()) {
								SinaWeiboHelper.initWeibo();
							}
							// 判断之前是否登陆过
							if (access != null) {
								SinaWeiboHelper.progressDialog = new ProgressDialog(
										context);
								SinaWeiboHelper.progressDialog
										.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								SinaWeiboHelper.progressDialog
										.setMessage(context
												.getString(R.string.sharing));
								SinaWeiboHelper.progressDialog
										.setCancelable(true);
								SinaWeiboHelper.progressDialog.show();
								new Thread() {
									public void run() {
										SinaWeiboHelper.setAccessToken(
												access.getAccessToken(),
												access.getAccessSecret(),
												access.getExpiresIn());
										SinaWeiboHelper.shareMessage(context,
												shareMessage);
									}
								}.start();
							} else {
								SinaWeiboHelper
										.authorize(context, shareMessage);
							}
							break;
						}
					}
				});
		builder.create().show();
	}
	

	/**
	 * 分享到'新浪微博'或'腾讯微博'的对话框
	 * 
	 * @param context
	 *            当前Activity
	 * @param title
	 *            分享的标题
	 */
	public static void showShareDialog(final Activity context,
			final String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setTitle(context.getString(R.string.share));
		builder.setItems(R.array.app_share_items,
				new DialogInterface.OnClickListener() {
					AppConfig cfgHelper = AppConfig.getAppConfig(context);
					AccessInfo access = cfgHelper.getAccessInfo();

					public void onClick(DialogInterface arg0, int arg1) {
						switch (arg1) {
						case 0:// 新浪微博
								// 分享的内容
							final String shareMessage = title;
							// 初始化微博
							if (SinaWeiboHelper.isWeiboNull()) {
								SinaWeiboHelper.initWeibo();
							}
							// 判断之前是否登陆过
							if (access != null) {
								SinaWeiboHelper.progressDialog = new ProgressDialog(
										context);
								SinaWeiboHelper.progressDialog
										.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								SinaWeiboHelper.progressDialog
										.setMessage(context
												.getString(R.string.sharing));
								SinaWeiboHelper.progressDialog
										.setCancelable(true);
								SinaWeiboHelper.progressDialog.show();
								new Thread() {
									public void run() {
										SinaWeiboHelper.setAccessToken(
												access.getAccessToken(),
												access.getAccessSecret(),
												access.getExpiresIn());
										SinaWeiboHelper.shareMessage(context,
												shareMessage);
									}
								}.start();
							} else {
								SinaWeiboHelper
										.authorize(context, shareMessage);
							}
							break;
						}
					}
				});
		builder.create().show();
	}

	/**
	 * 分享到'新浪微博'或'腾讯微博'的对话框
	 * 
	 * @param context
	 *            当前Activity
	 * @param title
	 *            分享的标题
	 */
	public static void showShareRecruitDialog(final Activity context,
			final Recruit recruit) {
		String shareContent = String.format("%s公司2014最新校园招聘 \n %s \n来自一职有你",
				recruit.getCompanyName(), recruit.getUrl());
		showShareDialog(context, shareContent);
	}

	/**
	 * 分享到'新浪微博'或'腾讯微博'的对话框
	 * 
	 * @param context
	 *            当前Activity
	 * @param title
	 *            分享的标题
	 */
	public static void showShareCareerTalkDialog(final Activity context,
			final CareerTalk careerTalk) {
		String shareContent = String.format("%s公司 %s %s-%s,2014最新宣讲会信息 \n %s \n   来自一职有你",
				careerTalk.getCompanyName(), 
				StringUtils.forammtedTime(careerTalk.getCreatedTime()),
				careerTalk.getSchoolName(), careerTalk.getPlace(),
				careerTalk.getUrl());
		showShareDialog(context, shareContent);
	}

	/**
	 * 收藏操作选择框
	 * 
	 * @param context
	 * @param thread
	 */
	/*
	 * public static void showFavoriteOptionDialog(final Activity context, final
	 * Thread thread) { AlertDialog.Builder builder = new
	 * AlertDialog.Builder(context); builder.setIcon(R.drawable.ic_dialog_menu);
	 * builder.setTitle(context.getString(R.string.select));
	 * builder.setItems(R.array.favorite_options, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * arg0, int arg1) { switch (arg1) { case 0:// 删除 thread.start(); break; } }
	 * }); builder.create().show(); }
	 */

	/**
	 * 消息列表操作选择框
	 * 
	 * @param context
	 * @param msg
	 * @param thread
	 */
	/*
	 * public static void showMessageListOptionDialog(final Activity context,
	 * final Messages msg, final Thread thread) { AlertDialog.Builder builder =
	 * new AlertDialog.Builder(context);
	 * builder.setIcon(R.drawable.ic_dialog_menu);
	 * builder.setTitle(context.getString(R.string.select));
	 * builder.setItems(R.array.message_list_options, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * arg0, int arg1) { switch (arg1) { case 0:// 回复 showMessagePub(context,
	 * msg.getFriendId(), msg.getFriendName()); break; case 1:// 转发
	 * showMessageForward(context, msg.getFriendName(), msg.getContent());
	 * break; case 2:// 删除 thread.start(); break; } } });
	 * builder.create().show(); }
	 */

	/**
	 * 消息详情操作选择框
	 * 
	 * @param context
	 * @param msg
	 * @param thread
	 */
	/*
	 * public static void showMessageDetailOptionDialog(final Activity context,
	 * final Comment msg, final Thread thread) { AlertDialog.Builder builder =
	 * new AlertDialog.Builder(context);
	 * builder.setIcon(R.drawable.ic_dialog_menu);
	 * builder.setTitle(context.getString(R.string.select));
	 * builder.setItems(R.array.message_detail_options, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * arg0, int arg1) { switch (arg1) { case 0:// 转发
	 * showMessageForward(context, msg.getAuthor(), msg.getContent()); break;
	 * case 1:// 删除 thread.start(); break; } } }); builder.create().show(); }
	 */

	/**
	 * 评论操作选择框
	 * 
	 * @param context
	 * @param id
	 *            某条新闻，帖子，动弹的id 或者某条消息的 friendid
	 * @param catalog
	 *            该评论所属类型：1新闻 2帖子 3动弹 4动态
	 * @param comment
	 *            本条评论对象，用于获取评论id&评论者authorid
	 * @param thread
	 *            处理删除评论的线程，若无删除操作传null
	 */
	/*
	 * public static void showCommentOptionDialog(final Activity context, final
	 * int id, final int catalog, final Comment comment, final Thread thread) {
	 * AlertDialog.Builder builder = new AlertDialog.Builder(context);
	 * builder.setIcon(R.drawable.ic_dialog_menu);
	 * builder.setTitle(context.getString(R.string.select)); if (thread != null)
	 * { builder.setItems(R.array.comment_options_2, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * arg0, int arg1) { switch (arg1) { case 0:// 回复 showCommentReply(context,
	 * id, catalog, comment.getId(), comment.getAuthorId(), comment.getAuthor(),
	 * comment.getContent()); break; case 1:// 删除 thread.start(); break; } } });
	 * } else { builder.setItems(R.array.comment_options_1, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * arg0, int arg1) { switch (arg1) { case 0:// 回复 showCommentReply(context,
	 * id, catalog, comment.getId(), comment.getAuthorId(), comment.getAuthor(),
	 * comment.getContent()); break; } } }); } builder.create().show(); }
	 */
	/**
	 * 博客列表操作
	 * 
	 * @param context
	 * @param thread
	 */
	/*
	 * public static void showBlogOptionDialog(final Context context, final
	 * Thread thread) { new AlertDialog.Builder(context)
	 * .setIcon(android.R.drawable.ic_dialog_info)
	 * .setTitle(context.getString(R.string.delete_blog))
	 * .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
	 * public void onClick(DialogInterface dialog, int which) { if (thread !=
	 * null) thread.start(); else ToastMessage(context,
	 * R.string.msg_noaccess_delete); dialog.dismiss(); } })
	 * .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener()
	 * { public void onClick(DialogInterface dialog, int which) {
	 * dialog.dismiss(); } }).create().show(); }
	 */

	/**
	 * 动弹操作选择框
	 * 
	 * @param context
	 * @param thread
	 */
	/*
	 * public static void showTweetOptionDialog(final Context context, final
	 * Thread thread) { new AlertDialog.Builder(context)
	 * .setIcon(android.R.drawable.ic_dialog_info)
	 * .setTitle(context.getString(R.string.delete_tweet))
	 * .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
	 * public void onClick(DialogInterface dialog, int which) { if (thread !=
	 * null) thread.start(); else ToastMessage(context,
	 * R.string.msg_noaccess_delete); dialog.dismiss(); } })
	 * .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener()
	 * { public void onClick(DialogInterface dialog, int which) {
	 * dialog.dismiss(); } }).create().show(); }
	 */

	/**
	 * 是否重新发布动弹操对话框
	 * 
	 * @param context
	 * @param thread
	 */
	/*
	 * public static void showResendTweetDialog(final Context context, final
	 * Thread thread) { new AlertDialog.Builder(context)
	 * .setIcon(android.R.drawable.ic_dialog_info)
	 * .setTitle(context.getString(R.string.republish_tweet))
	 * .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
	 * public void onClick(DialogInterface dialog, int which) {
	 * dialog.dismiss(); if (context == TweetPub.mContext && TweetPub.mMessage
	 * != null) TweetPub.mMessage .setVisibility(View.VISIBLE); thread.start();
	 * } }) .setNegativeButton(R.string.cancle, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int which) { dialog.dismiss(); } }).create().show(); }
	 */

	/**
	 * 显示图片对话框
	 * 
	 * @param context
	 * @param imgUrl
	 */
	/*
	 * public static void showImageDialog(Context context, String imgUrl) {
	 * Intent intent = new Intent(context, ImageDialog.class);
	 * intent.putExtra("img_url", imgUrl); context.startActivity(intent); }
	 * 
	 * public static void showImageZoomDialog(Context context, String imgUrl) {
	 * Intent intent = new Intent(context, ImageZoomDialog.class);
	 * intent.putExtra("img_url", imgUrl); context.startActivity(intent); }
	 */

	/**
	 * 显示系统设置界面
	 * 
	 * @param context
	 */
	/*
	 * public static void showSetting(Context context) { Intent intent = new
	 * Intent(context, Setting.class); context.startActivity(intent); }
	 */

	/**
	 * 显示搜索界面
	 * 
	 * @param context
	 */
	/*
	 * public static void showSearch(Context context) { Intent intent = new
	 * Intent(context, Search.class); context.startActivity(intent); }
	 */

	/**
	 * 显示软件界面
	 * 
	 * @param context
	 */
	/*
	 * public static void showSoftware(Context context) { Intent intent = new
	 * Intent(context, SoftwareLib.class); context.startActivity(intent); }
	 *//**
	 * 显示我的资料
	 * 
	 * @param context
	 */
	/*
	 * public static void showUserInfo(Activity context) { AppContext ac =
	 * (AppContext) context.getApplicationContext(); if (!ac.isLogin()) {
	 * showLoginDialog(context); } else { Intent intent = new Intent(context,
	 * UserInfo.class); context.startActivity(intent); } }
	 *//**
	 * 显示路径选择对话框
	 * 
	 * @param context
	 */
	/*
	 * public static void showFilePathDialog(Activity context,
	 * ChooseCompleteListener listener) { new PathChooseDialog(context,
	 * listener).show(); }
	 *//**
	 * 显示用户动态
	 * 
	 * @param context
	 * @param uid
	 * @param hisuid
	 * @param hisname
	 */
	public static void showUserCenter(Context context, String uid,
			String userName) {
		Intent intent = new Intent(context, UserCenterActivity.class);
		intent.putExtra("uid", uid);
		intent.putExtra("userName", userName);
		context.startActivity(intent);
	}

	/**
	 * 显示用户消息
	 * 
	 * @param context
	 * @param uid
	 * @param hisuid
	 * @param hisname
	 */
	public static void showUserReply(Context context) {
		Intent intent = new Intent(context, UserReplyActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示用户消息
	 * 
	 * @param context
	 */
	public static void showPrivateMessage(Context context) {
		Intent intent = new Intent(context, PrivateMessageActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示用户消息详细
	 * 
	 * @param context
	 * @param uid
	 * @param hisuid
	 * @param hisname
	 */
	public static void showPrivateMessageList(Context context, String userID,
			String userName, String lastCreatedTime) {
		Intent intent = new Intent(context, PrivateMessageListActivity.class);
		intent.putExtra("userID", userID);
		intent.putExtra("userName", userName);
		intent.putExtra("lastCreatedTime", lastCreatedTime);
		context.startActivity(intent);
	}

	/**
	 * 显示用户收藏夹
	 * 
	 * @param context
	 */
	/*
	 * public static void showUserFavorite(Context context) { Intent intent =
	 * new Intent(context, UserFavorite.class); context.startActivity(intent); }
	 *//**
	 * 显示用户好友
	 * 
	 * @param context
	 */
	/*
	 * public static void showUserFriend(Context context, int friendType, int
	 * followers, int fans) { Intent intent = new Intent(context,
	 * UserFriend.class); intent.putExtra("friend_type", friendType);
	 * intent.putExtra("friend_followers", followers);
	 * intent.putExtra("friend_fans", fans); context.startActivity(intent); }
	 */

	/**
	 * 加载显示用户头像
	 * 
	 * @param imgFace
	 * @param faceURL
	 */
	public static void showUserFace(final ImageView imgFace,
			final String faceURL) {
		showLoadImage(imgFace, faceURL,
				imgFace.getContext().getString(R.string.msg_load_userface_fail));
	}

	/**
	 * 加载显示图片
	 * 
	 * @param imgFace
	 * @param faceURL
	 * @param errMsg
	 */
	public static void showLoadImage(final ImageView imgView,
			final String imgURL, final String errMsg) {
		// 读取本地图片
		if (StringUtils.isEmpty(imgURL) || imgURL.endsWith("portrait.gif")) {
			Bitmap bmp = BitmapFactory.decodeResource(imgView.getResources(),
					R.drawable.user_face);
			imgView.setImageBitmap(bmp);
			return;
		}

		// 是否有缓存图片
		final String filename = FileUtils.getFileName(imgURL);
		// Environment.getExternalStorageDirectory();返回/sdcard
		String filepath = imgView.getContext().getFilesDir() + File.separator
				+ filename;
		File file = new File(filepath);
		if (file.exists()) {
			Bitmap bmp = ImageUtils.getBitmap(imgView.getContext(), filename);
			imgView.setImageBitmap(bmp);
			return;
		}

		// 从网络获取&写入图片缓存
		String _errMsg = imgView.getContext().getString(
				R.string.msg_load_image_fail);
		if (!StringUtils.isEmpty(errMsg))
			_errMsg = errMsg;
		final String ErrMsg = _errMsg;
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1 && msg.obj != null) {
					imgView.setImageBitmap((Bitmap) msg.obj);
					try {
						// 写图片缓存
						ImageUtils.saveImage(imgView.getContext(), filename,
								(Bitmap) msg.obj);
					} catch (IOException e) {
						
					}
				} else {
					ToastMessage(imgView.getContext(), ErrMsg);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Bitmap bmp = NetApiClient.getNetBitmap(imgURL);
					msg.what = 1;
					msg.obj = bmp;
				} catch (AppException e) {
					
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 打开浏览器
	 * 
	 * @param context
	 * @param url
	 */
	public static void openBrowser(Context context, String url) {
		try {
			if (!url.startsWith("http://") && !url.startsWith("https://"))
				url = "http://" + url;
			Uri uri = Uri.parse(url);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(it);
		} catch (Exception e) {
			
			ToastMessage(context, "无法浏览此网页", 500);
		}
	}

	/**
	 * 获取webviewClient对象
	 * 
	 * @return
	 */
	// public static WebViewClient getWebViewClient() {
	// return new WebViewClient() {
	// @Override
	// public boolean shouldOverrideUrlLoading(WebView view, String url) {
	// showUrlRedirect(view.getContext(), url);
	// return true;
	// }
	// };
	// }

	/**
	 * 获取TextWatcher对象
	 * 
	 * @param context
	 * @param tmlKey
	 * @return
	 */
	public static TextWatcher getTextWatcher(final Activity context,
			final String temlKey) {
		return new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 保存当前EditText正在编辑的内容
				((AppContext) context.getApplication()).setProperty(temlKey,
						s.toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		};
	}

	/**
	 * 编辑器显示保存的草稿
	 * 
	 * @param context
	 * @param editer
	 * @param temlKey
	 */
	public static void showTempEditContent(Activity context, EditText editer,
			String temlKey) {
		String tempContent = ((AppContext) context.getApplication())
				.getProperty(temlKey);
		if (!StringUtils.isEmpty(tempContent)) {
			SpannableStringBuilder builder = parseFaceByText(context,
					tempContent);
			editer.setText(builder);
			editer.setSelection(tempContent.length());// 设置光标位置
		}
	}

	/**
	 * 将[12]之类的字符串替换为表情
	 * 
	 * @param context
	 * @param content
	 */
	public static SpannableStringBuilder parseFaceByText(Context context,
			String content) {
		SpannableStringBuilder builder = new SpannableStringBuilder(content);
		Matcher matcher = facePattern.matcher(content);
		while (matcher.find()) {
			// 使用正则表达式找出其中的数字
			int position = StringUtils.toInt(matcher.group(1));
			int resId = 0;
			try {
				if (position > 65 && position < 102)
					position = position - 1;
				else if (position > 102)
					position = position - 2;
				resId = GridViewFaceAdapter.getImageIds()[position];
				Drawable d = context.getResources().getDrawable(resId);
				d.setBounds(0, 0, 35, 35);// 设置表情图片的显示大小
				ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
				builder.setSpan(span, matcher.start(), matcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} catch (Exception e) {
			}
		}
		return builder;
	}

	/**
	 * 清除文字
	 * 
	 * @param cont
	 * @param editer
	 */
	public static void showClearWordsDialog(final Context cont,
			final EditText editer, final TextView numwords) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setTitle(R.string.clearwords);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 清除文字
						editer.setText("");
						numwords.setText("160");
					}
				});
		builder.setNegativeButton(R.string.cancle,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.show();
	}

	/**
	 * 发送通知广播
	 * 
	 * @param context
	 * @param notice
	 */
	public static void sendBroadCast(Context context, Notice notice) {
		// if (!((AppContext) context.getApplicationContext()).isLogin()
		// || notice == null)
		// return;
		Log.i("test", "sendBroadcast");
		Intent intent = new Intent("com.campusRecruit.action.NOTICE");
		intent.putExtra("recruitCount", notice.getRecruitCount());
		intent.putExtra("careerTalkCount", notice.getCareerTalkCount());
		intent.putExtra("messageCount", notice.getMessageCount());
		intent.putExtra("replyCount", notice.getReplyCount());
		Bundle bundle = new Bundle();
		if (notice.getCareerTalkCount() == 1) {
			bundle.putSerializable("careertalk", notice.getCareerTalk());
		}
		if (notice.getRecruitCount() == 1) {
			bundle.putSerializable("recruit", notice.getRecruit());
		}
		if (notice.getMessageCount() == 1) {
			bundle.putSerializable("message", notice.getMessage());
		}
		if (notice.getReplyCount() == 1) {
			bundle.putSerializable("reply", notice.getReply());
		}
		/*
		 * if (notice.getMessageCount() == 1) { bundle.putSerializable("topic",
		 * notice.getTopic()); }
		 */
		intent.putExtras(bundle);
		Log.i("notice", "begin sendBroadcast!!!!!!!!!!");
		context.sendBroadcast(intent);
	}

	/**
	 * 发送广播-发布动弹
	 * 
	 * @param context
	 * @param notice
	 */
	/*
	 * public static void sendBroadCastTweet(Context context, int what, Result
	 * res, Tweet tweet) { if (res == null && tweet == null) return; Intent
	 * intent = new Intent("net.oschina.app.action.APP_TWEETPUB");
	 * intent.putExtra("MSG_WHAT", what); if (what == 1)
	 * intent.putExtra("RESULT", res); else intent.putExtra("TWEET", tweet);
	 * context.sendBroadcast(intent); }
	 */
	/**
	 * 组合动态的动作文本
	 * 
	 * @param objecttype
	 * @param objectcatalog
	 * @param objecttitle
	 * @return
	 */
	@SuppressLint("NewApi")
	public static SpannableString parseActiveAction(String author,
			int objecttype, int objectcatalog, String objecttitle) {
		String title = "";
		int start = 0;
		int end = 0;
		if (objecttype == 32 && objectcatalog == 0) {
			title = "加入了开源中国";
		} else if (objecttype == 1 && objectcatalog == 0) {
			title = "添加了开源项目 " + objecttitle;
		} else if (objecttype == 2 && objectcatalog == 1) {
			title = "在讨论区提问：" + objecttitle;
		} else if (objecttype == 2 && objectcatalog == 2) {
			title = "发表了新话题：" + objecttitle;
		} else if (objecttype == 3 && objectcatalog == 0) {
			title = "发表了博客 " + objecttitle;
		} else if (objecttype == 4 && objectcatalog == 0) {
			title = "发表一篇新闻 " + objecttitle;
		} else if (objecttype == 5 && objectcatalog == 0) {
			title = "分享了一段代码 " + objecttitle;
		} else if (objecttype == 6 && objectcatalog == 0) {
			title = "发布了一个职位：" + objecttitle;
		} else if (objecttype == 16 && objectcatalog == 0) {
			title = "在新闻 " + objecttitle + " 发表评论";
		} else if (objecttype == 17 && objectcatalog == 1) {
			title = "回答了问题：" + objecttitle;
		} else if (objecttype == 17 && objectcatalog == 2) {
			title = "回复了话题：" + objecttitle;
		} else if (objecttype == 17 && objectcatalog == 3) {
			title = "在 " + objecttitle + " 对回帖发表评论";
		} else if (objecttype == 18 && objectcatalog == 0) {
			title = "在博客 " + objecttitle + " 发表评论";
		} else if (objecttype == 19 && objectcatalog == 0) {
			title = "在代码 " + objecttitle + " 发表评论";
		} else if (objecttype == 20 && objectcatalog == 0) {
			title = "在职位 " + objecttitle + " 发表评论";
		} else if (objecttype == 101 && objectcatalog == 0) {
			title = "回复了动态：" + objecttitle;
		} else if (objecttype == 100) {
			title = "更新了动态";
		}
		title = author + " " + title;
		SpannableString sp = new SpannableString(title);
		// 设置用户名字体大小、加粗、高亮
		sp.setSpan(new AbsoluteSizeSpan(14, true), 0, author.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
				author.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 0,
				author.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置标题字体大小、高亮
		if (!StringUtils.isEmpty(objecttitle)) {
			start = title.indexOf(objecttitle);
			if (objecttitle.length() > 0 && start > 0) {
				end = start + objecttitle.length();
				sp.setSpan(new AbsoluteSizeSpan(14, true), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				sp.setSpan(
						new ForegroundColorSpan(Color.parseColor("#0e5986")),
						start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return sp;
	}

	/**
	 * 组合动态的回复文本
	 * 
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableString parseActiveReply(String name, String body) {
		SpannableString sp = new SpannableString(name + "：" + body);
		// 设置用户名字体加粗、高亮
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
				name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 0,
				name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sp;
	}

	/**
	 * 组合消息文本
	 * 
	 * @param name
	 * @param body
	 * @return
	 */
	/*
	 * public static void parseMessageSpan(LinkView view, String name, String
	 * body, String action) { Spanned span = null; SpannableStringBuilder style
	 * = null; int start = 0; int end = 0; String content = null; if
	 * (StringUtils.isEmpty(action)) { content = name + "：" + body; span =
	 * Html.fromHtml(content); view.setText(span); end = name.length(); } else {
	 * content = action + name + "：" + body; span = Html.fromHtml(content);
	 * view.setText(span); start = action.length(); end = start + name.length();
	 * } view.setMovementMethod(LinkMovementMethod.getInstance());
	 * 
	 * Spannable sp = (Spannable) view.getText(); URLSpan[] urls =
	 * span.getSpans(0, sp.length(), URLSpan.class);
	 * 
	 * style = new SpannableStringBuilder(view.getText()); //
	 * style.clearSpans();// 这里会清除之前所有的样式 for (URLSpan url : urls) {
	 * style.removeSpan(url);// 只需要移除之前的URL样式，再重新设置 MyURLSpan myURLSpan =
	 * view.new MyURLSpan(url.getURL()); style.setSpan(myURLSpan,
	 * span.getSpanStart(url), span.getSpanEnd(url),
	 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); }
	 * 
	 * // 设置用户名字体加粗、高亮 style.setSpan(new
	 * StyleSpan(android.graphics.Typeface.BOLD), start, end,
	 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); style.setSpan(new
	 * ForegroundColorSpan(Color.parseColor("#0e5986")), start, end,
	 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); view.setText(style); }
	 *//**
	 * 组合回复引用文本
	 * 
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableString parseQuoteSpan(String name, String body) {
		SpannableString sp = new SpannableString("回复：" + name + "\n" + body);
		// 设置用户名字体加粗、高亮
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3,
				3 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 3,
				3 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sp;
	}

	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 弹出Toast评论成功消息
	 * 
	 * @param msg
	 */
	public static void ToastMessageCommentSucess(Context cont) {
		Toast.makeText(cont, "评论成功", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 弹出Toast评论成功消息
	 * 
	 * @param msg
	 */
	public static void ToastMessagePubTopicSucess(Context cont) {
		Toast.makeText(cont, "发帖成功", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 弹出Toast收藏成功消息
	 * 
	 * @param msg
	 */
	public static void ToastMessageJoinSucess(Context cont) {
		Toast.makeText(cont, "收藏成功", Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}

	/**
	 * 点击返回监听事件
	 * 
	 * @param activity
	 * @return
	 */
	public static View.OnClickListener finish(final Activity activity) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		};
	}

	/**
	 * 显示关于我们
	 * 
	 * @param context
	 */
	/*
	 * public static void showAbout(Context context) { Intent intent = new
	 * Intent(context, About.class); context.startActivity(intent); }
	 *//**
	 * 显示用户反馈
	 * 
	 * @param context
	 */
	/*
	 * public static void showFeedBack(Context context) { Intent intent = new
	 * Intent(context, FeedBack.class); context.startActivity(intent); }
	 *//**
	 * 菜单显示登录或登出
	 * 
	 * @param activity
	 * @param menu
	 */
	/*
	 * public static void showMenuLoginOrLogout(Activity activity, Menu menu) {
	 * if (((AppContext) activity.getApplication()).isLogin()) {
	 * menu.findItem(R.id.main_menu_user).setTitle( R.string.main_menu_logout);
	 * menu.findItem(R.id.main_menu_user).setIcon( R.drawable.ic_menu_logout); }
	 * else { menu.findItem(R.id.main_menu_user).setTitle(
	 * R.string.main_menu_login); menu.findItem(R.id.main_menu_user)
	 * .setIcon(R.drawable.ic_menu_login); } }
	 *//**
	 * 快捷栏显示登录与登出
	 * 
	 * @param activity
	 * @param qa
	 */
	/*
	 * public static void showSettingLoginOrLogout(Activity activity,
	 * QuickAction qa) { if (((AppContext) activity.getApplication()).isLogin())
	 * { qa.setIcon(MyQuickAction.buildDrawable(activity,
	 * R.drawable.ic_menu_logout));
	 * qa.setTitle(activity.getString(R.string.main_menu_logout)); } else {
	 * qa.setIcon(MyQuickAction.buildDrawable(activity,
	 * R.drawable.ic_menu_login));
	 * qa.setTitle(activity.getString(R.string.main_menu_login)); } }
	 *//**
	 * 快捷栏是否显示文章图片
	 * 
	 * @param activity
	 * @param qa
	 */
	/*
	 * public static void showSettingIsLoadImage(Activity activity, QuickAction
	 * qa) { if (((AppContext) activity.getApplication()).isLoadImage()) {
	 * qa.setIcon(MyQuickAction.buildDrawable(activity,
	 * R.drawable.ic_menu_picnoshow));
	 * qa.setTitle(activity.getString(R.string.main_menu_picnoshow)); } else {
	 * qa.setIcon(MyQuickAction.buildDrawable(activity,
	 * R.drawable.ic_menu_picshow));
	 * qa.setTitle(activity.getString(R.string.main_menu_picshow)); } }
	 *//**
	 * 用户登录或注销
	 * 
	 * @param activity
	 */
	/*
	 * public static void loginOrLogout(Activity activity) { AppContext ac =
	 * (AppContext) activity.getApplication(); if (ac.isLogin()) { ac.Logout();
	 * ToastMessage(activity, "已退出登录"); } else { showLoginDialog(activity); } }
	 *//**
	 * 文章是否加载图片显示
	 * 
	 * @param activity
	 */
	/*
	 * public static void changeSettingIsLoadImage(Activity activity) {
	 * AppContext ac = (AppContext) activity.getApplication(); if
	 * (ac.isLoadImage()) { ac.setConfigLoadimage(false); ToastMessage(activity,
	 * "已设置文章不加载图片"); } else { ac.setConfigLoadimage(true);
	 * ToastMessage(activity, "已设置文章加载图片"); } }
	 * 
	 * public static void changeSettingIsLoadImage(Activity activity, boolean b)
	 * { AppContext ac = (AppContext) activity.getApplication();
	 * ac.setConfigLoadimage(b); }
	 *//**
	 * 清除app缓存
	 * 
	 * @param activity
	 */
	/*
	 * public static void clearAppCache(Activity activity) { final AppContext ac
	 * = (AppContext) activity.getApplication(); final Handler handler = new
	 * Handler() { public void handleMessage(Message msg) { if (msg.what == 1) {
	 * ToastMessage(ac, "缓存清除成功"); } else { ToastMessage(ac, "缓存清除失败"); } } };
	 * new Thread() { public void run() { Message msg = new Message(); try {
	 * ac.clearAppCache(); msg.what = 1; } catch (Exception e) {
	 *  msg.what = -1; } handler.sendMessage(msg); }
	 * }.start(); }
	 *//**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						Intent i = new Intent(Intent.ACTION_SEND);
						// i.setType("text/plain"); //模拟器
						i.setType("message/rfc822"); // 真机
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "lizhe.ted@gmail.com" });
						i.putExtra(Intent.EXTRA_SUBJECT,
								"校园招聘Android客户端 - 错误报告");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						cont.startActivity(Intent.createChooser(i, "发送错误报告"));
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.show();
	}

	/**
	 * 退出程序
	 * 
	 * @param cont
	 */
	/*
	 * public static void Exit(final Context cont) { AlertDialog.Builder builder
	 * = new AlertDialog.Builder(cont);
	 * builder.setIcon(android.R.drawable.ic_dialog_info);
	 * builder.setTitle(R.string.app_menu_surelogout);
	 * builder.setPositiveButton(R.string.sure, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int which) { dialog.dismiss(); // 退出
	 * AppManager.getAppManager().AppExit(cont); } });
	 * builder.setNegativeButton(R.string.cancle, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int which) { dialog.dismiss(); } }); builder.show(); }
	 *//**
	 * /** 添加网页的点击图片展示支持
	 */
	/*
	 * @SuppressLint("SetJavaScriptEnabled") public static void
	 * addWebImageShow(final Context cxt, WebView wv) {
	 * wv.getSettings().setJavaScriptEnabled(true);
	 * wv.addJavascriptInterface(new OnWebViewImageListener() {
	 * 
	 * @Override public void onImageClick(String bigImageUrl) { if (bigImageUrl
	 * != null) UIHelper.showImageZoomDialog(cxt, bigImageUrl); } },
	 * "mWebViewImageListener"); }
	 */

	/*
	 * public static void showImageZoomDialog(Context context, String imgUrl) {
	 * Intent intent = new Intent(context, ImageZoomDialog.class);
	 * intent.putExtra("img_url", imgUrl); context.startActivity(intent); }
	 */
}
