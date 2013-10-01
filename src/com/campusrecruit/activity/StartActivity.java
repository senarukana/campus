package com.campusrecruit.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.common.UIHelper;
import com.krislq.sliding.R;

public class StartActivity extends Activity {


	private int count = 0;
	private int pic_type = 0;
	private AppContext appContext = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		final View view = View.inflate(this, R.layout.start, null);
		FrameLayout layout = (FrameLayout) view.findViewById(R.id.mainLayout);
		FrameLayout buttonGroup = (FrameLayout) view
				.findViewById(R.id.start_button_group);
		appContext = (AppContext) getApplication();
//		appContext.cleanLoginInfo();
		appContext.initLoginInfo();
		if (appContext.isInit()) {
			buttonGroup.setVisibility(View.GONE);
			setContentView(view);
			layout.setAnimation(getRedirectAnimation(view));
		} else {
			setContentView(view);
			layout.setAnimation(getLoginAnimation(view));
		}
	}

	public void loginStart(View view) {
		UIHelper.showLogin(StartActivity.this);
	}

	public void registerStart(View view) {
		UIHelper.showRegister(StartActivity.this, true);
	}

	public void justSeeStart(View view) {
		((AppContext) getApplication()).createDefaultUser();
		((AppContext) getApplication()).saveLoginInfo();
		UIHelper.showRecommends(StartActivity.this);
	}
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder adb = new AlertDialog.Builder(
				StartActivity.this);
		adb.setTitle("退出 一职有你？");
		adb.setMessage("确定退出吗?");
		adb.setNegativeButton("取消", null);
		adb.setPositiveButton("确定", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.isLogout = true;
				finish();
			}
		});
		adb.show();
	}

	public Animation getLoginAnimation(final View view) {
		AlphaAnimation aa = new AlphaAnimation(0.6f, 1.0f);
		aa.setDuration(5000);
		aa.setRepeatCount(Animation.INFINITE);
		aa.setRepeatMode(Animation.REVERSE);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				// redirectTo();
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
		if (appContext.isSetUserPreference())
			intent = new Intent(this, MainActivity.class);
		else
			intent = new Intent(this, RecommendActivity.class);
		startActivity(intent);
		finish();
	}
}