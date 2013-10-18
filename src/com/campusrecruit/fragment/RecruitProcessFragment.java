package com.campusrecruit.fragment;

import com.campusrecruit.activity.RecruitDetailActivity;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.Company;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.LinkView;
import com.pcncad.campusRecruit.R;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RecruitProcessFragment extends LoadingFragment {

	private GestureDetector gd;
	// footer
	private Recruit recruit;
	private TextView vNetTime;
	private TextView vWritenExamTime;
	private TextView vFaceExamTime;

	private View recruitProcessLayout;
	private LinearLayout recruitProcessHeaderLayout;

	private ImageView vNetImage;
	private ImageView vWrieImage;
	private ImageView vFaceImage;

	private ImageView vNetToWritenImage;
	private ImageView vWrienToFaceImage;

	private Handler recruitHandler;

	AppContext appContext;

	private LinkView vProcessInfo = null;

	public RecruitProcessFragment() {
	}

	public static RecruitProcessFragment newInstance(Recruit recruit) {
		RecruitProcessFragment fragment = new RecruitProcessFragment();
		Bundle args = new Bundle();
		args.putSerializable("recruit", recruit);
		fragment.setArguments(args);
		return fragment;
	}

	private void getArgs() {
		this.recruit = (Recruit) getArguments().getSerializable("recruit");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (recruit == null)
			getArgs();
		if (appContext == null)
			appContext = (AppContext) getActivity().getApplication();

		if (appContext == null) {
			Log.i("bug", "fuck process");
		}
		initHandler();
		if (recruit.getContact() == null)
			initData(appContext);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.i("test", "init recruit process view");
		// inflater the layout
		View view = inflater.inflate(R.layout.recruit_process, null);

		recruitProcessLayout = (View) view
				.findViewById(R.id.recruit_process_layout);
		initLoadingView(view);
		vProcessInfo = (LinkView) view.findViewById(R.id.recruit_process_info);
		recruitProcessHeaderLayout = (LinearLayout) view
				.findViewById(R.id.recruit_process_header_layout);
		vNetTime = (TextView) view.findViewById(R.id.recruit_process_net_time);
		vWritenExamTime = (TextView) view
				.findViewById(R.id.recruit_process_writen_exam_time);
		vFaceExamTime = (TextView) view
				.findViewById(R.id.recruit_process_face_exam_time);

		vNetImage = (ImageView) view
				.findViewById(R.id.recruit_process_net_image);
		vWrieImage = (ImageView) view
				.findViewById(R.id.recruit_process_write_image);
		vFaceImage = (ImageView) view
				.findViewById(R.id.recruit_process_face_image);
		vNetToWritenImage = (ImageView) view
				.findViewById(R.id.recruit_process_net_to_wirte);
		vWrienToFaceImage = (ImageView) view
				.findViewById(R.id.recruit_process_write_to_face);
		if (!isloading && recruit.getContact() != null) {
			hideLoadProgress(recruitProcessLayout);
			fillView();
		}
		if (isloading) {
			showLoadProgress(recruitProcessLayout);
		}
		if (loadError) {
			hideLoadProgressWithError(recruitProcessLayout);
		}
		return view;
	}

	private void initHandler() {
		if (recruitHandler == null) {
			recruitHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						if (vProcessInfo != null) {
							fillView();
						}
					} else {
						hideLoadProgressWithError(recruitProcessLayout);
						((AppException) msg.obj).makeToast(getActivity());
						Log.i("bug", "recruit process err");
					}
				}
			};
		}
	}

	public void initData(AppContext context) {
		if (context != null)
			appContext = context;
		if (recruit == null)
			getArgs();
		if (isloading)
			return;
		initHandler();
		Log.i("test", "company init data");
		if (isloading)
			return;
		Log.i("test", "show load progress init data");
		showLoadProgress(recruitProcessLayout);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Recruit result = appContext.getRecruitProcessInfo(recruit
							.getRecruitID());
					recruit.setContact(result.getContact());
					recruit.setState(result.getState());
					recruit.setStatTime(result.getStatTime());
					msg.what = 1;
					Log.i("process", "send msg");
				} catch (AppException e) {
					
					Log.i("bug", "recruit process err");
					msg.what = -1;
					msg.obj = e;
				}
				recruitHandler.sendMessage(msg);
			}
		}.start();
	}

	private void fillView() {
		if (appContext.recruitLoadFromDisk) {
			appContext.recruitLoadFromDisk = false;
			UIHelper.ToastMessage(getActivity(), getString(R.string.load_fail));
		}
		hideLoadProgress(recruitProcessLayout);
		if (recruit.getContact() != null && !recruit.getContact().isEmpty()) {
			vProcessInfo.setText(recruit.getContact());
		} else {
			vProcessInfo.setText(R.string.nodata);
		}
		if (recruit.getState() != null && !recruit.getState().isEmpty()) {
			Log.i("test", recruit.getState() + recruit.getStatTime());
			if (recruit.getState().equals("网申")) {
				vNetImage.setImageResource(R.drawable.net_exam1);
				vNetTime.setText(recruit.getStatTime());
			} else if (recruit.getState().equals("笔试")) {
				vNetTime.setText("过期");
				vNetImage.setImageResource(R.drawable.net_exam1);
				vWrieImage.setImageResource(R.drawable.write_exam1);
				vNetToWritenImage
						.setImageResource(R.drawable.recruit_processed);
				vWritenExamTime.setText(recruit.getStatTime());
			} else if (recruit.getState().equals("笔试")) {
				vNetTime.setText("过期");
				vWritenExamTime.setText("过期");
				vNetImage.setImageResource(R.drawable.net_exam1);
				vWrieImage.setImageResource(R.drawable.write_exam1);
				vFaceImage.setImageResource(R.drawable.face_exam1);
				vNetToWritenImage
						.setImageResource(R.drawable.recruit_processed);
				vWrienToFaceImage
						.setImageResource(R.drawable.recruit_processed);
				vFaceExamTime.setText(recruit.getStatTime());
			}
		} else {
			Log.i("bug", "no state");
			recruitProcessHeaderLayout.setVisibility(View.GONE);
		}
	}

}
