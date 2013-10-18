package com.campusrecruit.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.User;
import com.campusrecruit.bean.UserPreference;
import com.campusrecruit.common.BitmapManager;
import com.campusrecruit.common.FileUtils;
import com.campusrecruit.common.ImageUtils;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.LoadingDialog;
import com.pcncad.campusRecruit.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class UserInfoActivity extends BaseActivity {
	private final static int CROP = 200;
	private final static String FILE_SAVEPATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/CampusRecruit/Portrait/";
	private Uri origUri;
	private Uri cropUri;
	private File protraitFile;
	private Bitmap protraitBitmap;
	private String protraitPath;
	private LoadingDialog loading;

	private User user;
	private Handler userInfoHandler;
	private Handler userUpdateHandler;
	private String userID;

	private ImageView vFace;
	private TextView vUserName;
	private TextView vEmail;
	private ToggleButton vGender;
	private TextView vSchool;
	private TextView vMajor;

	private EditText vEditEmail;

	/*
	 * private ToggleButton industryComputeToggle = null; private ToggleButton
	 * industryCommunicationToggle = null; private ToggleButton
	 * industryElectronicToggle = null; private ToggleButton
	 * industryEconomyToggle = null; private ToggleButton propertySoe = null;
	 * private ToggleButton propertyPrivate = null; private ToggleButton
	 * propertyForeign = null; private ToggleButton typeCarrertalk = null;
	 * private ToggleButton typeRecruit = null; private ToggleButton typeMessage
	 * = null; private ToggleButton typeReply = null;
	 */

	private String _email;
	private String _major;
	private String _school;
	private int _gender;

	private boolean faceChanged = true;

	private boolean isEdit = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("user", "init user view");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infor);
		Log.i("user", "init user view action bar");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("个人中心");
		Log.i("user", "init user action ok");
		userID = getIntent().getStringExtra("userID");
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		initTextView();
		/*
		 * initPreferenceView(); initPreferenceListener();
		 */

		Log.i("user", "init data");
		if (!userID.equals(appContext.getLoginUid())) {
			vEmail.setClickable(false);
			vMajor.setClickable(false);
			vSchool.setClickable(false);
			vFace.setClickable(false);
			vGender.setClickable(false);
			initUserData();
		} else {
			vFace.setOnClickListener(faceClickListener);
			initEditView();
			user = appContext.getLoginUser();
			showUserData();
		}
		Log.i("user", "disable data");
	}

	private void initTextView() {
		Log.i("user", "init text view");
		vUserName = (TextView) findViewById(R.id.user_infor_user_name_text);
		vEmail = (TextView) findViewById(R.id.user_infor_email_text);
		vSchool = (TextView) findViewById(R.id.user_infor_school_text);
		vMajor = (TextView) findViewById(R.id.user_infor_major_text);
		vGender = (ToggleButton) findViewById(R.id.user_infor_gender_text);
		vFace = (ImageView) findViewById(R.id.user_infor_face_show);
		vSchool.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.showSchool(UserInfoActivity.this, user.getSchoolName());
			}
		});

		vMajor.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.showMajor(UserInfoActivity.this, user.getMajorName());
			}
		});

		vEmail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);
				vEditEmail.setVisibility(View.VISIBLE);
				if (user.getEmail() != null && !user.getEmail().isEmpty()) {
					vEditEmail.setText(vEmail.getText());
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.showSoftInputFromInputMethod(vEditEmail.getWindowToken(), 0);
				vEditEmail.requestFocus();
			}
		});
		vGender.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isEdit = true;
			}
		});
	}

	private void initEditView() {
		vEditEmail = (EditText) findViewById(R.id.user_infor_email);

	}

	private void showUserData() {
		if (user == null)
			return;
		Log.i("user", "show userData");

		vUserName.setText(user.getName());
		if (user.getEmail() == null
				|| (user.getEmail() != null && user.getEmail().isEmpty())) {
			vEmail.setText(R.string.empty_field);
		} else {
			vEmail.setText(user.getEmail());
			if (user.getUid().equals(appContext.getLoginUid())) {
				vEditEmail.setText(user.getEmail());
			}
		}
		if (user.getSchoolName() == null
				|| (user.getSchoolName() != null && user.getSchoolName()
						.isEmpty())) {
			vSchool.setText(R.string.empty_field);
		} else {
			vSchool.setText(user.getSchoolName());
		}
		if (user.getMajorName() == null
				|| (user.getMajorName() != null && user.getMajorName()
						.isEmpty()))
			vMajor.setText(R.string.empty_field);
		else {
			vMajor.setText(user.getMajorName());
		}
		if (user.getGender() == 0) {
			vGender.setChecked(false);
		} else {
			vGender.setChecked(true);
		}
		BitmapManager bmpManager = new BitmapManager(
				BitmapFactory.decodeResource(this.getResources(),
						R.drawable.user_face));
		if (appContext.isLogin() && appContext.getLoginUser().isShowPicture()) {
			if (faceChanged) {
				faceChanged = false;
				if (user.getHasFace() == 0) {
					vFace.setImageResource(R.drawable.user_face);
				} else {
					if (protraitBitmap == null) {
						bmpManager.loadBitmap(userID, vFace);
					} else {
						vFace.setImageBitmap(protraitBitmap);
					}
				}
			}
		} else {
			vFace.setVisibility(View.GONE);
		}
	}

	private void initUserData() {
		Log.i("user", "initUser Data");
		userInfoHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					user = (User) msg.obj;
					// initPreference();
					showUserData();
				} else {
					((AppException) msg.obj).makeToast(UserInfoActivity.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					User userInfo = appContext.getUserInformation(userID);
					msg.what = 1;
					msg.obj = userInfo;
				} catch (AppException e) {
					msg.what = -1;
					msg.obj = e;
				}
				userInfoHandler.sendMessage(msg);
			}
		}.start();
	}

	private View.OnClickListener faceClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			faceChanged = true;
			CharSequence[] items = { getString(R.string.img_from_album) };
			imageChooseItem(items);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (userID.equals(appContext.getLoginUid())) {
			getMenuInflater().inflate(R.menu.menu_userinfo, menu);
		}
		return true;
	}

	private boolean saveUserInfo() {
		userUpdateHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					User u = (User) msg.obj;
					if (!u.getEmail().isEmpty()) {
						user.setEmail(u.getEmail());
					}
					if (!u.getSchoolName().isEmpty()) {
						user.setSchoolName(u.getSchoolName());
					}
					if (!u.getMajorName().isEmpty()) {
						user.setMajorName(u.getMajorName());
					}
					if (u.getGender() != 0) {
						user.setGender(u.getGender());
					}
					vEditEmail.setVisibility(View.GONE);
					vEmail.setText(vEditEmail.getText());
					vEmail.setVisibility(View.VISIBLE);
					showUserData();
				} else {
					((AppException) msg.obj).makeToast(UserInfoActivity.this);
				}
			}
		};
		_email = vEditEmail.getText().toString();
		_major = vMajor.getText().toString();
		_school = vSchool.getText().toString();
		_gender = vGender.isChecked() == true ? 1 : 0;

		if (!_email.isEmpty() && !StringUtils.isEmail(_email)) {
			UIHelper.ToastMessage(this, R.string.msg_login_email_error);
			return false;
		}
		appContext.getLoginUser().setEmail(_email);
		appContext.getLoginUser().setMajorName(_major);
		appContext.getLoginUser().setSchoolName(_school);
		appContext.getLoginUser().setGender(_gender);
		Log.i("user", _email + _major + _school + _gender);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Log.i("user", _email + _major + _school + _gender);
					User updateUser = new User(_email, _major, _school, _gender);
					appContext.setUserInformation(userID, _email, _major,
							_school, _gender);
					msg.what = 1;
					msg.obj = updateUser;
				} catch (AppException e) {

					msg.what = -1;
					msg.obj = e;
				}
				userUpdateHandler.sendMessage(msg);
			}
		}.start();
		isEdit = false;
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromInputMethod(vEditEmail.getWindowToken(), 0);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.userinfo_edit:
			// change the layout
			if (saveUserInfo()) {
				/*
				 * item.setIcon(R.drawable.ic_userinfo_edit);
				 * item.setTitle(getResources
				 * ().getText(R.string.userinfo_edit));
				 */
				// disableToggleButton();
				UIHelper.ToastMessage(UserInfoActivity.this, "资料保存成功");
			}
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (!user.getUid().equals(appContext.getLoginUid())) {
			backPressed();
		} else {
			if (vEditEmail.getVisibility() == View.VISIBLE) {
				vEditEmail.setVisibility(View.GONE);
				vEmail.setVisibility(View.VISIBLE);
				if (!StringUtils.isEmpty(vEditEmail.getText().toString())) {
					user.setEmail(vEmail.getText().toString());
					vEmail.setText(vEditEmail.getText());
				} else {
					user.setEmail(null);
					vEmail.setText(R.string.empty_field);
				}
			} else if (isEdit
					|| (user.getEmail() != null && !user.getEmail().isEmpty() && !user
							.getEmail().equals(vEmail.getText().toString()))) {
				AlertDialog.Builder adb = new AlertDialog.Builder(
						UserInfoActivity.this);
				adb.setTitle("保存修改的数据吗?");
				adb.setMessage("你做出了修改，但尚未保存");
				adb.setNegativeButton("取消", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						backPressed();
					}
				});
				adb.setPositiveButton("保存", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveUserInfo();
						backPressed();
					}
				});
				adb.show();
			} else {
				backPressed();
			}
		}
	}

	private void backPressed() {
		super.onBackPressed();
	}

	// 裁剪头像的绝对路径
	private Uri getUploadTempFile(Uri uri) {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			UIHelper.ToastMessage(this, "无法保存上传的头像，请检查SD卡是否挂载");
			return null;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);
		// 如果是标准Uri
		if (StringUtils.isEmpty(thePath)) {
			thePath = ImageUtils.getAbsoluteImagePath(this, uri);
		}
		String ext = FileUtils.getFileFormat(thePath);
		ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
		// 照片命名
		String cropFileName = "osc_crop_" + timeStamp + "." + ext;
		// 裁剪头像的绝对路径
		protraitPath = FILE_SAVEPATH + cropFileName;
		protraitFile = new File(protraitPath);
		Log.i("test", "ttt4");
		cropUri = Uri.fromFile(protraitFile);
		return this.cropUri;
	}

	// 拍照保存的绝对路径
	private Uri getCameraTempFile() {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			UIHelper.ToastMessage(this, "无法保存上传的头像，请检查SD卡是否挂载");
			return null;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		// 照片命名
		String cropFileName = "osc_camera_" + timeStamp + ".jpg";
		// 裁剪头像的绝对路径
		protraitPath = FILE_SAVEPATH + cropFileName;
		protraitFile = new File(protraitPath);
		cropUri = Uri.fromFile(protraitFile);
		this.origUri = this.cropUri;
		return this.cropUri;
	}

	/**
	 * 操作选择
	 * 
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items) {
		AlertDialog imageDialog = new AlertDialog.Builder(this)
				.setTitle("上传头像").setIcon(android.R.drawable.btn_star)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// 相册选图
						if (item == 0) {
							startImagePick();
						}
						/*
						 * // 手机拍照 else if (item == 1) { startActionCamera(); }
						 */
					}
				}).create();

		imageDialog.show();
	}

	/**
	 * 选择图片裁剪
	 * 
	 * @param output
	 */
	private void startImagePick() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "选择图片"),
				ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
	}

	/**
	 * 相机拍照
	 * 
	 * @param output
	 */
	private void startActionCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, this.getCameraTempFile());
		startActivityForResult(intent,
				ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
	}

	/**
	 * 拍照后裁剪
	 * 
	 * @param data
	 *            原始图片
	 * @param output
	 *            裁剪后图片
	 */
	private void startActionCrop(Uri data) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("output", this.getUploadTempFile(data));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		startActivityForResult(intent,
				ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
	}

	/**
	 * 上传新照片
	 */
	private void uploadNewPhoto() {
		Log.i("face", "upload");
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (loading != null)
					loading.dismiss();
				if (msg.what == 1) {
					if (user.getHasFace() == 0) {
						Log.i("face", "upload face complete");
						user.setHasFace(1);
						appContext.saveLoginInfo();
					}
					// 提示信息
					UIHelper.ToastMessage(UserInfoActivity.this, "上传头像成功");
					// 显示新头像
					vFace.setImageBitmap(protraitBitmap);
				} else if (msg.what == -1 && msg.obj != null) {
					((AppException) msg.obj).makeToast(UserInfoActivity.this);
				}
			}
		};

		if (loading != null) {
			loading.setLoadText("正在上传头像···");
			loading.show();
		}

		new Thread() {
			public void run() {
				Log.i("face", "begin");
				// 获取头像缩略图
				if (!StringUtils.isEmpty(protraitPath) && protraitFile.exists()) {
					protraitBitmap = ImageUtils.loadImgThumbnail(protraitPath,
							200, 200);
				} else {
					loading.setLoadText("图像不存在，上传失败·");
					loading.hide();
				}
				Log.i("face", "middle");
				if (protraitBitmap != null) {
					Message msg = new Message();
					try {
						Log.i("face", "update");
						appContext.updatePortrait(protraitFile);
						Log.i("face", "complete" + user.getFace());
						// 保存新头像到缓存
						ImageUtils.saveImage(UserInfoActivity.this, userID,
								protraitBitmap);

						Log.i("face", "save complete");
						msg.what = 1;
					} catch (AppException e) {
						loading.setLoadText("上传出错·");
						loading.hide();
						msg.what = -1;
						msg.obj = e;
					} catch (IOException e) {

					}
					handler.sendMessage(msg);
				} else {
					loading.setLoadText("图像不存在，上传失败·");
					loading.hide();
				}
			};
		}.start();
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode,
			final Intent data) {
		if (requestCode == UIHelper.REQUEST_SCHOOL_RESULT
				&& resultCode == RESULT_OK) {
			String school = data.getStringExtra("selectStr");
			vSchool.setText(school);
			isEdit = true;
			return;
		} else if (requestCode == UIHelper.REQUEST_MAJOR_RESULT
				&& resultCode == RESULT_OK) {
			String major = data.getStringExtra("selectStr");
			vMajor.setText(major);
			isEdit = true;
			return;
		}

		if (resultCode != Activity.RESULT_OK)
			return;

		switch (requestCode) {
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
			startActionCrop(origUri);// 拍照后裁剪
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
			startActionCrop(data.getData());// 选图后裁剪
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
			uploadNewPhoto();// 上传新照片
			break;
		}
	}

}