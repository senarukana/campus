package com.campusrecruit.activity;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.User;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.net.NetApiClient;
import com.krislq.sliding.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	AppContext appcontext;
	private ProgressDialog mProgress;
	private EditText mUserName;
	private EditText mPwd;
	private Button btn_login;
	/*
	 * //0其他 1初始化 private int initFlag;
	 */

	private InputMethodManager imm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		appcontext = (AppContext) getApplication();

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mUserName = (EditText) findViewById(R.id.login_user_name);
		imm.showSoftInputFromInputMethod(mUserName.getWindowToken(), 0);
		mPwd = (EditText) findViewById(R.id.login_user_passwd);
		btn_login = (Button) findViewById(R.id.login_btn);
		btn_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String userName = mUserName.getText().toString();
				String pwd = mPwd.getText().toString();
				// 判断输入
				if (userName.isEmpty()) {
					UIHelper.ToastMessage(v.getContext(),
							getString(R.string.msg_login_user_name_null));
					return;
				}
				if (pwd.length() < 6) {
					UIHelper.ToastMessage(v.getContext(),
							getString(R.string.msg_login_pwd_null));
					return;
				}
				imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
				mProgress = ProgressDialog.show(v.getContext(), null, "登陆中···",
						true, true);
				login(userName, pwd);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu_registe, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_registe:
			UIHelper.showRegiste(this);
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		UIHelper.showStart(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.showSoftInput(mUserName, InputMethodManager.SHOW_IMPLICIT);
	}

	private void login(final String email, final String pwd) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				mProgress.dismiss();
				if (msg.what == 1) {
					// 清空原先cookie
					NetApiClient.cleanCookie();
					// 提示登陆成功
					UIHelper.ToastMessage(LoginActivity.this,
							R.string.msg_login_success);
					UIHelper.showHome(LoginActivity.this);
					/*
					 * if (initFlag == 1) UIHelper.showHome(LoginActivity.this);
					 * else { //从其他页面进入，登陆成功后关闭 finish(); }
					 */
				} else if (msg.what == 0) {
					UIHelper.ToastMessage(LoginActivity.this,
							getString(R.string.msg_login_fail));
				} else if (msg.what == -1) {
					((AppException) msg.obj).makeToast(LoginActivity.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					AppContext ac = (AppContext) getApplication();
					boolean result = ac.loginVerify(email, pwd);
					if (result) {
						msg.what = 1;// 成功
					} else {
						msg.what = 0;// 失败
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}

}
