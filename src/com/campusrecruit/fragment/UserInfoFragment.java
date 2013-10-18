/*package com.campusrecruit.fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.User;
import com.campusrecruit.common.FileUtils;
import com.campusrecruit.common.ImageUtils;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.LoadingDialog;
import com.pcncad.campusRecruit.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class UserInfoFragment extends BaseFragment{
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
	
	private ImageView vFace;
	private TextView vUserName;
	private EditText vEmail;
	private ToggleButton vGender;
	private EditText vSchool;
	private EditText vMajor;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View mainView = inflater.inflate(R.layout.infor, null);
		vUserName = (TextView) mainView.findViewById(R.id.user_infor_user_name);
		vEmail = (EditText) mainView.findViewById(R.id.user_infor_email);
		vSchool = (EditText) mainView.findViewById(R.id.user_infor_school);
		vMajor = (EditText) mainView.findViewById(R.id.user_infor_major);
		vGender = (ToggleButton) mainView.findViewById(R.id.user_infor_gender);
		vFace = (ImageView) mainView.findViewById(R.id.user_infor_face);
		
		AppContext appContext = (AppContext)getActivity().getApplication();
		user = appContext.getLoginInfo();
		vUserName.setText(user.getName());
		vEmail.setText(user.getEmail());
		vSchool.setText(user.getSchoolName());
		vMajor.setText(user.getMajorName());
		if (user.getGender() == 1) {
			vGender.setChecked(true);
		} else {
			vGender.setChecked(false);
		}
		
		vFace.setOnClickListener(faceClickListener);
		return mainView;
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			CharSequence[] items = { getString(R.string.img_from_album),
					getString(R.string.img_from_camera) };
			imageChooseItem(items);
		}
	};
	
	// 裁剪头像的绝对路径
	private Uri getUploadTempFile(Uri uri) {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			UIHelper.ToastMessage(getActivity(), "无法保存上传的头像，请检查SD卡是否挂载");
			return null;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

		// 如果是标准Uri
		if (StringUtils.isEmpty(thePath)) {
			thePath = ImageUtils.getAbsoluteImagePath(getActivity(), uri);
		}
		String ext = FileUtils.getFileFormat(thePath);
		ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
		// 照片命名
		String cropFileName = "osc_crop_" + timeStamp + "." + ext;
		// 裁剪头像的绝对路径
		protraitPath = FILE_SAVEPATH + cropFileName;
		protraitFile = new File(protraitPath);

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
			UIHelper.ToastMessage(getActivity(), "无法保存上传的头像，请检查SD卡是否挂载");
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

	*//**
	 * 操作选择
	 * 
	 * @param items
	 *//*
	public void imageChooseItem(CharSequence[] items) {
		AlertDialog imageDialog = new AlertDialog.Builder(getActivity())
				.setTitle("上传头像").setIcon(android.R.drawable.btn_star)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// 相册选图
						if (item == 0) {
							startImagePick();
						}
						// 手机拍照
						else if (item == 1) {
							startActionCamera();
						}
					}
				}).create();

		imageDialog.show();
	}

	*//**
	 * 选择图片裁剪
	 * 
	 * @param output
	 *//*
	private void startImagePick() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "选择图片"),
				ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
	}

	*//**
	 * 相机拍照
	 * 
	 * @param output
	 *//*
	private void startActionCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, this.getCameraTempFile());
		startActivityForResult(intent,
				ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
	}

	*//**
	 * 拍照后裁剪
	 * 
	 * @param data
	 *            原始图片
	 * @param output
	 *            裁剪后图片
	 *//*
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

	*//**
	 * 上传新照片
	 *//*
	private void uploadNewPhoto() {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (loading != null)
					loading.dismiss();
				if (msg.what == 1 && msg.obj != null) {
					// 提示信息
//					UIHelper.ToastMessage(UserInfoFragment.this, res.getErrorMessage());
						// 显示新头像
					vFace.setImageBitmap(protraitBitmap);
				} else if (msg.what == -1 && msg.obj != null) {
					((AppException) msg.obj).makeToast(getActivity());
				}
			}
		};

		if (loading != null) {
			loading.setLoadText("正在上传头像···");
			loading.show();
		}

		new Thread() {
			public void run() {
				// 获取头像缩略图
				if (!StringUtils.isEmpty(protraitPath) && protraitFile.exists()) {
					protraitBitmap = ImageUtils.loadImgThumbnail(protraitPath,
							200, 200);
				} else {
					loading.setLoadText("图像不存在，上传失败·");
					loading.hide();
				}

				if (protraitBitmap != null) {
					Message msg = new Message();
					try {
						appContext
								.updatePortrait(protraitFile);
							// 保存新头像到缓存
							String filename = FileUtils.getFileName(user
									.getFace());
							ImageUtils.saveImage(getActivity(), filename,
									protraitBitmap);
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
	public void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
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
	
	
}*/