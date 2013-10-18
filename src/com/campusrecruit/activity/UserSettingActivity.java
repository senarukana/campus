package com.campusrecruit.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.app.AppManager;
import com.campusrecruit.bean.User;
import com.campusrecruit.common.FileUtils;
import com.campusrecruit.common.MethodsCompat;
import com.campusrecruit.common.UIHelper;
import com.pcncad.campusRecruit.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class UserSettingActivity extends BaseActivity {
	private Switch vBackgroundNotice;
	private Switch vShowPicture;
	private View vBackgroundLayout;
	private View vShowPictureLayout;
	private View vCleanLayout;
	private TextView vCurrentCache;
	private TextView vAlarmText;
	private Button vLogout;
	private Button vClean;
	private Button vAlarmModify;
	
	private String alarmStr = null;
	
	
	private User user;
	private boolean background;
	private boolean showPicture;
	
	private boolean isChanged = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		Log.i("user", "init user view action bar");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("系统设置");
		
		user = appContext.getLoginUser();
		initView();
		fillView();

	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_finish, menu);
		return true;
	}*/
	
	
	
	
	private void initView() {
		// vBackgroundNotice = (ToggleButton)
		// findViewById(R.id.settings_background);
		vBackgroundNotice = (Switch) findViewById(R.id.settings_background);
		// vShowPicture = (ToggleButton) findViewById(R.id.settings_pic_mode);
		vShowPicture = (Switch) findViewById(R.id.settings_pic_mode);
		vCleanLayout = findViewById(R.id.settings_clean_layout);
		vCurrentCache = (TextView) findViewById(R.id.settings_clean_text);
		vAlarmText = (TextView) findViewById(R.id.settings_alarm_time);
		vLogout = (Button) findViewById(R.id.settings_logout);
		vAlarmModify = (Button) findViewById(R.id.settings_alarm_modify);
		vClean = (Button) findViewById(R.id.settings_clean_btn);
		vBackgroundLayout = findViewById(R.id.settings_background_layout);
		vShowPictureLayout = findViewById(R.id.settings_pic_mode_layout);

		alarmStr = appContext.getAlarmTimelList().get(appContext.getClockAlarmTime());
		vAlarmText.setText(alarmStr);
		
		vBackgroundNotice.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!vBackgroundNotice.isChecked()) {
					vBackgroundNotice.setChecked(true);
					AlertDialog.Builder adb = new AlertDialog.Builder(
							UserSettingActivity.this);
					adb.setTitle("不接受后台消息?");
					adb.setMessage("您可能第一时间接受不到最新推送消息");
					adb.setNegativeButton("取消", null);
					adb.setPositiveButton("确定",
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									background = false;
									vBackgroundNotice.setChecked(false);
									appContext
											.saveSettingsBackground(background);
								}
							});
					adb.show();
				} else {
					background = true;
					appContext.saveSettingsBackground(background);
				}
			}
		});
		vBackgroundLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				vBackgroundNotice.toggle();
			}
		});

		vShowPicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!vShowPicture.isChecked()) {
					Log.i("test", "vpicture");
					vShowPicture.setChecked(true);
					AlertDialog.Builder adb = new AlertDialog.Builder(
							UserSettingActivity.this);
					adb.setTitle("确定不显示图片?");
					adb.setMessage("默认仅在WIFI模式下显示图片");
					adb.setNegativeButton("取消", null);
					adb.setPositiveButton("确定",
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									showPicture = false;
									vShowPicture.setChecked(false);
									appContext.saveSettingsPicture(showPicture);
								}
							});
					adb.show();
				} else {
					showPicture = true;
					appContext.saveSettingsPicture(showPicture);
				}
			}
		});

		vShowPictureLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				vShowPicture.toggle();
			}
		});

		vLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder adb = new AlertDialog.Builder(
						UserSettingActivity.this);
				adb.setTitle("登出账号?");
				adb.setMessage("这将会清理所有用户数据，并回到欢迎界面");
				adb.setNegativeButton("取消", null);
				adb.setPositiveButton("确定", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						appContext.clearAppCache();
						appContext.cleanLoginInfo();
						appContext.initAppList();
						UIHelper.showStart(UserSettingActivity.this);
						setResult(RESULT_FIRST_USER);
						AppManager.getAppManager().finishAllActivity();
						finish();
					}
				});
				adb.show();
			}
		});

		vClean.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				appContext.truncateSqlite(false);
//				appContext.cleanLoginInfo();
				appContext.clearAppCache();
				UIHelper.ToastMessage(UserSettingActivity.this, "缓存已经清空");
				vCurrentCache.setText("当前没有缓存");
				// appContext.cleanLoginInfo();
			}
		});
		
		vAlarmModify.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				UIHelper.showAlarmTime(UserSettingActivity.this, alarmStr);
			}
		});

	}
	
	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == UIHelper.REQUEST_SET_ALARM_TIME_RESULT && responseCode == RESULT_OK) {
			alarmStr = intent.getStringExtra("selectStr");
			int pos = appContext.getAlarmTimelList().indexOf(alarmStr);
			if (pos!= -1) {
				appContext.setClockAlarmTime(pos);
			}
			vAlarmText.setText(alarmStr);
			UIHelper.ToastMessage(UserSettingActivity.this, "修改成功");
		}
	}

/*	@Override
	public void onBackPressed() {
		if (isChanged) {
			AlertDialog.Builder adb = new AlertDialog.Builder(
					UserSettingActivity.this);
			adb.setTitle("保存修改的数据吗?");
			adb.setMessage("你做出了修改，但尚未保存");
			adb.setNegativeButton("取消", null);
			adb.setPositiveButton("保存", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					savePreference();
				}
			});
			adb.show();
		}
		super.onBackPressed();
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
/*		case R.id.menu_finish_id:
			savePreference();
			return true;*/
		default:
			return false;
		}
	}

	private void fillView() {
		if (user == null)
			return;
		Log.i("user", "fill view");
		if (user.isBackgroundNotice()) {
			vBackgroundNotice.setChecked(true);
			Log.i("test", "background checked");
		} else
			vBackgroundNotice.setChecked(false);

		if (user.isShowPicture())
			vShowPicture.setChecked(true);
		else
			vShowPicture.setChecked(false);
		Log.i("user", "fill view calculate");
		// 计算缓存大小
		long fileSize = 0;
		String cacheSize = "0KB";
		File filesDir = getFilesDir();
		File cacheDir = getCacheDir();

		fileSize += FileUtils.getDirSize(filesDir);
		fileSize += FileUtils.getDirSize(cacheDir);
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			File externalCacheDir = MethodsCompat.getExternalCacheDir(this);
			fileSize += FileUtils.getDirSize(externalCacheDir);
		}
		if (fileSize > 0)
			cacheSize = FileUtils.formatFileSize(fileSize);
		vCurrentCache.setText("当前缓存大小\n" + cacheSize);

		Log.i("user", "fill view ok");
	}

}
