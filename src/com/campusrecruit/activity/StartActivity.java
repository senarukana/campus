package com.campusrecruit.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.app.AppManager;
import com.campusrecruit.common.UIHelper;
import com.pcncad.campusRecruit.R;

public class StartActivity extends BaseActivity {

	public AppContext getApp() {
		return (AppContext) getApplication();
	}

	private Handler preferenceHandler;
	private ProgressDialog vProgress;
	private int count = 0;
	private int pic_type = 0;
	private boolean isAboutQuit = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		final View view = View.inflate(this, R.layout.start, null);
		FrameLayout layout = (FrameLayout) view.findViewById(R.id.mainLayout);
		FrameLayout buttonGroup = (FrameLayout) view
				.findViewById(R.id.start_button_group);
		getApp().initLoginInfo();
		if (getApp().isInit()) {
			buttonGroup.setVisibility(View.GONE);
			setContentView(view);
			layout.setAnimation(getRedirectAnimation(view));
		} else {
			if (!getApp().getTutorialCompleted()) {
				Intent intent = new Intent(getApplicationContext(),
						TutorialActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				overridePendingTransition(0, 0);
				startActivity(intent);
			}
			setContentView(view);
			layout.setAnimation(getLoginAnimation(view));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isAboutQuit = false;
	}

	public void loginStart(View view) {
		UIHelper.showLogin(StartActivity.this);
	}

	public void registerStart(View view) {
		UIHelper.showRegister(StartActivity.this, true);
	}

	public void justSeeStart(View view) {
		vProgress = ProgressDialog
				.show(this, null, "稍等片刻，准备数据中···", true, true);
		preferenceHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				vProgress.dismiss();
				if (msg.what == 1) {
					UIHelper.showHome(StartActivity.this);
					finish();
				} else {
					((AppException) (msg.obj)).makeToast(StartActivity.this);
				}
			}

		};
		new Thread() {
			Message msg = new Message();

			@Override
			public void run() {
				try {
					((AppContext) getApplication()).createDefaultUser();
					msg.what = 1;
				} catch (AppException e) {
					
					msg.what = -1;
					msg.obj = e;
				}
				preferenceHandler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	public void onBackPressed() {

		if (!isAboutQuit) {
			UIHelper.ToastMessage(this, "再按一次退出程序");
			isAboutQuit = true;
		} else {
			AppManager.getAppManager().AppExitQuickly(this);
		}
	}

	public Animation getLoginAnimation(final View view) {
		AlphaAnimation aa = new AlphaAnimation(0.6f, 1.0f);
		aa.setDuration(5000);
		aa.setRepeatCount(Animation.INFINITE);
		aa.setRepeatMode(Animation.REVERSE);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				count++;
				if (count == 2) {
					FrameLayout mainLayout = (FrameLayout) view
							.findViewById(R.id.mainLayout);
					switch (pic_type) {
					case 0:
						pic_type = 1;
						mainLayout.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.main1));
						break;
					case 1:
						pic_type = 2;
						mainLayout.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.main2));
						break;
					case 2:
						pic_type = 0;
						mainLayout.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.main0));
					}
					count = 0;
				}
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});
		return aa;
	}

	public Animation getRedirectAnimation(final View view) {
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(2000);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});
		return aa;
	}

	/**
	 * 跳转到...
	 */
	private void redirectTo() {
		Intent intent;
		if (getApp().isSetUserPreference())
			intent = new Intent(this, MainActivity.class);
		else
			intent = new Intent(this, RecommendActivity.class);
		startActivity(intent);
		finish();
	}
}