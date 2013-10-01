package com.campusrecruit.activity;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.User;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.krislq.sliding.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

public class LoginDialogActivity extends Activity {

	private ImageButton btn_close;
	private Button btn_login;
	private Button btn_regisger;
	private ProgressDialog mProgress;
	private AutoCompleteTextView mAccount;
	private EditText mPwd;
	private AnimationDrawable loadingAnimation;
	private InputMethodManager imm;
	private AppContext appContext;

	public final static int LOGIN_OTHER = 0x00;
	public final static int LOGIN_MAIN = 0x01;
	public final static int LOGIN_SETTING = 0x02;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_dialog);

		appContext = (AppContext) getApplication();

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


		mAccount = (AutoCompleteTextView) findViewById(R.id.login_account);
		mPwd = (EditText) findViewById(R.id.login_password);

		btn_close = (ImageButton) findViewById(R.id.login_close_button);
		btn_close.setOnClickListener(UIHelper.finish(this));

		btn_login = (Button) findViewById(R.id.login_btn_login);
		btn_login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 隐藏软键盘
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

				String account = mAccount.getText().toString();
				String pwd = mPwd.getText().toString();
				// 判断输入
				if (StringUtils.isEmpty(account)) {
					UIHelper.ToastMessage(v.getContext(),
							getString(R.string.msg_login_user_name_null));
					return;
				}
				if (StringUtils.isEmpty(pwd)) {
					UIHelper.ToastMessage(v.getContext(),
							getString(R.string.msg_login_pwd_null));
					return;
				}

				btn_close.setVisibility(View.GONE);
				mProgress = ProgressDialog.show(v.getContext(), null, "登陆中···",
						true, true);

				login(account, pwd);
			}
		});
		
		btn_regisger = (Button) findViewById(R.id.login_register_btn);
		btn_regisger.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showRegister(LoginDialogActivity.this, false);
				finish();
			}
		});

		// 是否显示登录信息
		AppContext ac = (AppContext) getApplication();
		User user = ac.getLoginInfo();
		if (!StringUtils.isEmpty(user.getPwd())) {
			mPwd.setText(user.getPwd());
		}
	}

	// 登录验证
	private void login(final String account, final String pwd) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				mProgress.dismiss();
				if (msg.what == 1) {
					// 提示登陆成功
					UIHelper.ToastMessage(LoginDialogActivity.this,
							R.string.msg_login_success);
					finish();
				} else if (msg.what == 0) {
					btn_close.setVisibility(View.VISIBLE);
					UIHelper.ToastMessage(LoginDialogActivity.this,
							getString(R.string.msg_login_fail));
				} else if (msg.what == -1) {
					btn_close.setVisibility(View.VISIBLE);
					((AppException) msg.obj)
							.makeToast(LoginDialogActivity.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					AppContext ac = (AppContext) getApplication();
					boolean result = ac.loginVerify(account, pwd);
					if (result) {
						msg.what = 1;// 成功
					} else {
						ac.cleanLoginInfo();// 清除登录信息
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.onDestroy();
		}
		return super.onKeyDown(keyCode, event);
	}
}
