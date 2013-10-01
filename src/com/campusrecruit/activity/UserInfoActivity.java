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
import com.krislq.sliding.R;

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

public class UserInfoActivity extends Activity {
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

	private AppContext appContext;

	private User user;
	private Handler userInfoHandler;
	private Handler userUpdateHandler;
	private Handler preferenceHandler;
	private String userID;

	private TableLayout textViewMainLayout;
	private ImageView vFace;
	private TextView vUserName;
	private TextView vEmail;
	private ImageView vGender;
	private TextView vSchool;
	private TextView vMajor;
	private Button vLogout;

	private TableLayout editViewMainLayout;
	private EditText vEditEmail;
	private ToggleButton vEditGender;
	private EditText vEditSchool;
	private EditText vEditMajor;

	private RelativeLayout addressSelect = null;
	private TextView selectedProvinceTextview = null;
	private StringBuilder provinceBuilder;
	private ArrayList<Integer> selectProvinceList = new ArrayList<Integer>();
	private Map<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();

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

	private boolean isEdit = true;

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
		appContext = (AppContext) getApplication();
		Log.i("user", "init user view userID");
		textViewMainLayout = (TableLayout) findViewById(R.id.infor_show_layout);
		editViewMainLayout = (TableLayout) findViewById(R.id.infor_edit_layout);
		Log.i("user", "init text view");
		initTextView();
		/*
		 * initPreferenceView(); initPreferenceListener();
		 */

		Log.i("user", "init data");
		if (!userID.equals(appContext.getLoginUid())) {
			initUserData();
		} else {
			vFace.setOnClickListener(faceClickListener);
			initEditView();
			user = appContext.getLoginUser();
			// initPreference();
			showUserData();
			// enableToggleButton();
		}
		Log.i("user", "disable data");
		// disableToggleButton();
	}

	// 展示layout 到 修改layout的切换
	private void viewSwitch(boolean flag) {
		if (flag) {
			Log.i("test", "viewswich true");
			vFace.setClickable(false);
			Log.i("test", "viewswich true1");
			textViewMainLayout.setVisibility(View.VISIBLE);
			Log.i("test", "viewswich true2");
			editViewMainLayout.setVisibility(View.GONE);
			Log.i("test", "viewswich true3");
		} else {
			Log.i("test", "viewswich f");
			vFace.setClickable(true);
			Log.i("test", "viewswich f1");
			textViewMainLayout.setVisibility(View.GONE);
			Log.i("test", "viewswich f2");
			editViewMainLayout.setVisibility(View.VISIBLE);
			Log.i("test", "viewswich f3");
		}
	}

	private void initTextView() {
		Log.i("user", "init text view");
		vUserName = (TextView) findViewById(R.id.user_infor_user_name_text);
		vEmail = (TextView) findViewById(R.id.user_infor_email_text);
		vSchool = (TextView) findViewById(R.id.user_infor_school_text);
		vMajor = (TextView) findViewById(R.id.user_infor_major_text);
		vGender = (ImageView) findViewById(R.id.user_infor_gender_text);
		vFace = (ImageView) findViewById(R.id.user_infor_face_show);
		vFace.setClickable(false);
	}

	private void initEditView() {
		Log.i("user", "init edit view");
		vEditEmail = (EditText) findViewById(R.id.user_infor_email);
		vEditSchool = (EditText) findViewById(R.id.user_infor_school);
		vEditMajor = (EditText) findViewById(R.id.user_infor_major);
		vEditGender = (ToggleButton) findViewById(R.id.user_infor_gender);
		/*
		 * vLogout = (Button) findViewById(R.id.logout_btn);
		 * vLogout.setVisibility(View.VISIBLE); vLogout.setOnClickListener(new
		 * View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { AlertDialog.Builder adb = new
		 * AlertDialog.Builder(UserInfoActivity.this); adb.setTitle("退出登陆?");
		 * adb.setMessage("这将会清除掉你所有本地数据，并退出应用"); adb.setNegativeButton("取消",
		 * null); adb.setPositiveButton("确定", new AlertDialog.OnClickListener()
		 * {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * Log.i("test","登出"); appContext.cleanLoginInfo();
		 * Log.i("test","登出 complete"); finish(); Log.i("test","ok"); } });
		 * adb.show();
		 * 
		 * } });
		 */

		user = appContext.getLoginUser();
		if (user.getEmail() != null)
			vEditEmail.setText(user.getEmail());
		if (user.getSchoolName() != null) {
			vEditSchool.setText(user.getSchoolName());
		}
		if (user.getMajorName() != null) {
			vEditMajor.setText(user.getMajorName());
		}
		if (user.getGender() == 1) {
			vEditGender.setChecked(true);
		} else {
			vEditGender.setChecked(false);
		}
		Log.i("user", "init edit view");
	}

	/*
	 * private void initPreferenceView() { industryComputeToggle =
	 * (ToggleButton) findViewById(R.id.recommend_industry_computer_toggle);
	 * industryCommunicationToggle = (ToggleButton)
	 * findViewById(R.id.recommend_industry_communication_toggle);
	 * industryElectronicToggle = (ToggleButton)
	 * findViewById(R.id.recommend_industry_electronic_toggle);
	 * industryEconomyToggle = (ToggleButton)
	 * findViewById(R.id.recommend_industry_economic_toggle); propertySoe =
	 * (ToggleButton) findViewById(R.id.recommend_property_soe_toggle);
	 * propertyPrivate = (ToggleButton)
	 * findViewById(R.id.recommend_property_private_toggle); propertyForeign =
	 * (ToggleButton) findViewById(R.id.recommend_property_foreign_toggle);
	 * typeCarrertalk = (ToggleButton)
	 * findViewById(R.id.recommend_type_careertalk_toggle); typeRecruit =
	 * (ToggleButton) findViewById(R.id.recommend_type_recruit_toggle);
	 * typeMessage = (ToggleButton)
	 * findViewById(R.id.recommend_type_message_toggle); typeReply =
	 * (ToggleButton) findViewById(R.id.recommend_type_reply_toggle);
	 * addressSelect = (RelativeLayout)
	 * findViewById(R.id.recommend_address_select); selectedProvinceTextview =
	 * (TextView) findViewById(R.id.selected_province_textview); }
	 */
	private void showUserData() {
		if (user == null)
			return;
		Log.i("user", "show userData");

		vUserName.setText(user.getName());
		if (user.getEmail() == null
				|| (user.getEmail() != null && user.getEmail().isEmpty())) {
			vEmail.setText(R.string.empty_field);
			Log.i("user", "emai empty");
		} else {
			vEmail.setText(user.getEmail());
			Log.i("user", "emai not empty " + user.getEmail());
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
			vGender.setImageResource(R.drawable.widget_gender_man);
		} else {
			vGender.setImageResource(R.drawable.widget_gender_woman);
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

	/*
	 * private void initPreference() { UserPreference preference =
	 * user.getPreference(); String[] provinces =
	 * preference.getProvince().split(","); String[] industries =
	 * preference.getCompanyIndustry().split(","); String[] companyTypes =
	 * preference.getCompanyType().split(","); String[] notifyTypes =
	 * preference.getNotifyType().split(",");
	 * 
	 * for (int i = 0; i < industries.length; i++) { if
	 * (industries[i].equals("1")) industryComputeToggle.setChecked(true); if
	 * (industries[i].equals("2")) industryCommunicationToggle.setChecked(true);
	 * if (industries[i].equals("3")) industryElectronicToggle.setChecked(true);
	 * if (industries[i].equals("4")) industryEconomyToggle.setChecked(true); }
	 * for (int i = 0; i < companyTypes.length; i++) { if
	 * (companyTypes[i].equals("1")) propertySoe.setChecked(true); if
	 * (companyTypes[i].equals("2")) propertyPrivate.setChecked(true); if
	 * (companyTypes[i].equals("3")) propertyForeign.setChecked(true); } for
	 * (int i = 0; i < notifyTypes.length; i++) { if
	 * (notifyTypes[i].equals("1")) typeCarrertalk.setChecked(true); if
	 * (notifyTypes[i].equals("2")) typeRecruit.setChecked(true); if
	 * (notifyTypes[i].equals("3")) typeMessage.setChecked(true); if
	 * (notifyTypes[i].equals("4")) typeReply.setChecked(true); }
	 * selectProvinceList.clear(); provinceBuilder = new StringBuilder(); for
	 * (int i = 0; i < provinces.length; i++) {
	 * selectProvinceList.add(Integer.parseInt(provinces[i]));
	 * provinceBuilder.append(appContext.getProvinceList()
	 * .get(Integer.parseInt(provinces[i])).getProvinceName() + " "); }
	 * selectedProvinceTextview.setText(provinceBuilder.toString()); }
	 * 
	 * private void initPreferenceListener() {
	 * industryComputeToggle.setOnCheckedChangeListener(toggleListener);
	 * industryCommunicationToggle.setOnCheckedChangeListener(toggleListener);
	 * industryElectronicToggle.setOnCheckedChangeListener(toggleListener);
	 * industryEconomyToggle.setOnCheckedChangeListener(toggleListener);
	 * propertySoe.setOnCheckedChangeListener(toggleListener);
	 * propertyPrivate.setOnCheckedChangeListener(toggleListener);
	 * propertyForeign.setOnCheckedChangeListener(toggleListener);
	 * typeCarrertalk.setOnCheckedChangeListener(toggleListener);
	 * typeRecruit.setOnCheckedChangeListener(toggleListener);
	 * typeMessage.setOnCheckedChangeListener(toggleListener);
	 * typeReply.setOnCheckedChangeListener(toggleListener);
	 * addressSelect.setOnClickListener(addressSelectListener); }
	 * 
	 * private void disableToggleButton() {
	 * industryComputeToggle.setClickable(false);
	 * industryCommunicationToggle.setClickable(false);
	 * industryElectronicToggle.setClickable(false);
	 * industryEconomyToggle.setClickable(false);
	 * propertySoe.setClickable(false); propertyPrivate.setClickable(false);
	 * propertyForeign.setClickable(false); typeCarrertalk.setClickable(false);
	 * typeRecruit.setClickable(false); typeMessage.setClickable(false);
	 * typeReply.setClickable(false); addressSelect.setClickable(false); }
	 * 
	 * private void enableToggleButton() {
	 * industryComputeToggle.setClickable(true);
	 * industryCommunicationToggle.setClickable(true);
	 * industryElectronicToggle.setClickable(true);
	 * industryEconomyToggle.setClickable(true); propertySoe.setClickable(true);
	 * propertyPrivate.setClickable(true); propertyForeign.setClickable(true);
	 * typeCarrertalk.setClickable(true); typeRecruit.setClickable(true);
	 * typeReply.setClickable(true); typeMessage.setClickable(true);
	 * addressSelect.setClickable(true); }
	 */

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

	/*
	 * private void initEditView() { Log.i("user", "init edit view");
	 * vEditUserName = (TextView) findViewById(R.id.user_infor_user_name);
	 * vEditEmail = (EditText) findViewById(R.id.user_infor_email); vEditSchool
	 * = (EditText) findViewById(R.id.user_infor_school); vEditMajor =
	 * (EditText) findViewById(R.id.user_infor_major); vEditGender =
	 * (ToggleButton) findViewById(R.id.user_infor_gender); vEditFace =
	 * (ImageView) findViewById(R.id.user_infor_face);
	 * 
	 * user = appContext.getLoginInfo(); vEditUserName.setText(user.getName());
	 * vEditEmail.setText(user.getEmail());
	 * vEditSchool.setText(user.getSchoolName());
	 * vEditMajor.setText(user.getMajorName()); if (user.getGender() == 1) {
	 * vEditGender.setChecked(true); } else { vEditGender.setChecked(false); }
	 * UIHelper.showUserFace(vEditFace, user.getFace());
	 * vEditFace.setOnClickListener(faceClickListener); }
	 */

	private View.OnClickListener faceClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			faceChanged = true;
			/*
			 * CharSequence[] items = { getString(R.string.img_from_album),
			 * getString(R.string.img_from_camera) }; imageChooseItem(items);
			 */
			CharSequence[] items = { getString(R.string.img_from_album) };
			imageChooseItem(items);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_userinfo, menu);
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
					Log.i("test", "handkle begin");
					showUserData();
					Log.i("test", "handkle");
					viewSwitch(true);
				} else {
					((AppException) msg.obj).makeToast(UserInfoActivity.this);
				}
			}
		};
		_email = vEditEmail.getText().toString();
		_major = vEditMajor.getText().toString();
		_school = vEditSchool.getText().toString();
		_gender = vEditGender.isChecked() == true ? 1 : 0;

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
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				userUpdateHandler.sendMessage(msg);
			}
		}.start();
		isEdit = !isEdit;
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromInputMethod(vEditEmail.getWindowToken(), 0);
		return true;
	}

	/*
	 * private boolean saveInfo() { map.put("province", selectProvinceList);
	 * Log.i("map", map.toString()); boolean mapEmpty = map.isEmpty(); boolean
	 * keyEmpty = map.keySet().size() != 3; boolean valueEmpty = false;
	 * Set<String> names = map.keySet(); Log.i("map set", names.toString());
	 * Iterator<String> l = names.iterator(); while (l.hasNext()) { String name
	 * = l.next(); if (map.get(name).isEmpty()) { valueEmpty = true; break; } }
	 * if (mapEmpty || keyEmpty || valueEmpty) {
	 * Toast.makeText(UserInfoActivity.this, "亲，您有类别没有选择哦",
	 * Toast.LENGTH_SHORT).show(); return false; } else { isEdit = !isEdit;
	 * saveUserInfo(); // savePreference(); return true; } }
	 */
	/*
	 * private void savePreference() {
	 * 
	 * preferenceHandler = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) { Log.i("test",
	 * "preferenceHandler"); if (msg.what == 1) { disableToggleButton(); } else
	 * { ((AppException) (msg.obj)).makeToast(UserInfoActivity.this); } }
	 * 
	 * }; new Thread() { Message msg = new Message();
	 * 
	 * @Override public void run() { try { Log.i("map preference",
	 * map.toString()); appContext.setPreference(map); msg.what = 1; } catch
	 * (AppException e) { e.printStackTrace(); msg.what = -1; msg.obj = e; }
	 * preferenceHandler.sendMessage(msg); } }.start(); }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.userinfo_edit:
			if (isEdit) {
				if (user != null && userID.equals(appContext.getLoginUid())) {
					// enableToggleButton();
					isEdit = !isEdit;
					item.setIcon(R.drawable.ic_userinfo_save);
					item.setTitle(getResources()
							.getText(R.string.userinfo_save));
					// change the layout
					viewSwitch(false);
				} else {
					if (user != null) {
						// 发私信
						UIHelper.showPrivateMessageList(UserInfoActivity.this,
								userID, user.getName(), null);
					}
				}
			} else {
				// change the layout
				if (saveUserInfo()) {
					item.setIcon(R.drawable.ic_userinfo_edit);
					item.setTitle(getResources()
							.getText(R.string.userinfo_edit));
					// disableToggleButton();
				}
			}
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
		Log.i("test", "ttt1");
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);
		Log.i("test", "ttt2");
		// 如果是标准Uri
		if (StringUtils.isEmpty(thePath)) {
			thePath = ImageUtils.getAbsoluteImagePath(this, uri);
		}
		Log.i("test", "ttt3");
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
						e.printStackTrace();
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
		if (resultCode == UIHelper.REQUEST_PROVINCE_FOR_RESULT) {
			selectProvinceList.clear();
			provinceBuilder = new StringBuilder();
			ArrayList<Integer> list = data
					.getIntegerArrayListExtra("selectProvinces");
			for (Integer i : list) {
				selectProvinceList.add(i);
				provinceBuilder.append(appContext.getProvinceList().get(i)
						+ " ");
			}
			selectedProvinceTextview.setText(provinceBuilder.toString());
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

	private OnCheckedChangeListener toggleListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			String tag = (String) buttonView.getTag();
			String[] tags = tag.split("_");
			String tag_pre = tags[0];
			Integer tag_last = Integer.parseInt(tags[1]);
			if (isChecked) {
				if (map.containsKey(tag_pre)) {
					map.get(tag_pre).add(tag_last);
				} else {
					ArrayList<Integer> list = new ArrayList<Integer>();
					list.add(tag_last);
					map.put(tag_pre, list);
				}
			} else {
				map.get(tag_pre).remove(tag_last);
			}

			Log.i("map", map.toString());
		}
	};

	private OnClickListener addressSelectListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			UIHelper.showProvince(UserInfoActivity.this, selectProvinceList);
		}
	};

}