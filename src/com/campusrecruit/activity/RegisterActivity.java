package com.campusrecruit.activity;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.User;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.net.NetApiClient;
import com.pcncad.campusRecruit.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends BaseActivity {
	private ProgressDialog mProgress;
	private EditText eUserName;
	private EditText ePwd;
	private EditText ePwdAgain;
	private Button btn_login;

	private InputMethodManager imm;
	private boolean isInit = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		isInit = getIntent().getBooleanExtra("isInit", false);

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		eUserName = (EditText) findViewById(R.id.register_user_name);
		ePwd = (EditText) findViewById(R.id.register_user_passwd);
		ePwdAgain = (EditText) findViewById(R.id.register_user_passwd_again);
		btn_login = (Button) findViewById(R.id.register_btn);
		btn_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				imm.showSoftInputFromInputMethod(v.getWindowToken(), 0);
				String userName = eUserName.getText().toString();
				String pwd = ePwd.getText().toString();

				if (!ePwd.getText().toString()
						.equals(ePwdAgain.getText().toString())) {
					UIHelper.ToastMessage(v.getContext(),
							getString(R.string.msg_login_pwd_again));
					return;
				}
				// 判断输入
				if (userName.length() < 3) {
					UIHelper.ToastMessage(v.getContext(),
							getString(R.string.msg_login_username_error));
					return;
				}
				if (pwd.length() < 6) {
					UIHelper.ToastMessage(v.getContext(),
							getString(R.string.msg_login_pwd_err));
					return;
				}
				imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
				mProgress = ProgressDialog.show(v.getContext(), null, "注册中···",
						true, true);
				register(userName, pwd);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_login:
			UIHelper.showLogin(this);
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (this.isInit)
			UIHelper.showStart(this);
	}

	private void register(final String userName, final String pwd) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				mProgress.dismiss();
				if (msg.what == 1) {
					Log.i("test", "register ok");
					// 清空原先cookie
					NetApiClient.cleanCookie();
					// 保存用户信息
					Log.i("test", "save info");
					appContext.saveLoginInfo();
					// 提示登陆成功
					UIHelper.ToastMessage(RegisterActivity.this,
							R.string.msg_register_success);
					if (isInit) {
						UIHelper.showHome(RegisterActivity.this);
					} else {
						finish();
					}
				} else if (msg.what == 0) {
					UIHelper.ToastMessage(RegisterActivity.this,
							getString(R.string.msg_register_fail));
				} else if (msg.what == -1) {
					((AppException) msg.obj).makeToast(RegisterActivity.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					AppContext ac = (AppContext) getApplication();
					int result = ac.registerUser(userName, pwd);
					msg.what = result;
				} catch (AppException e) {
					
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}
}
