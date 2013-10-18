package com.campusrecruit.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.Company;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.LinkView;
import com.pcncad.campusRecruit.R;

public class RecruitDetailFragment extends LoadingFragment {

	/*
	 * private FrameLayout vHead; private ImageView vHome; private ImageView
	 * vRefresh; private TextView vHeadTitle;
	 */
	protected ScrollView scrollView;
	protected TextView vContentHeaderTextView;
	protected TextView vContentTextView;
	protected TextView vGoToBBSSection;

	private ImageView vFamousFlag;
	private View vURLLayout;
	private TextView vSourceFrom;
	private TextView vCompanyName;
	private TextView vCreatedTime;
	private TextView vPosition;
	private TextView vPlace;
	private TextView vCompanyIndustry;
	private TextView vCompanyType;

	private Handler recruitHandler;

	protected Recruit recruitDetail;

	public RecruitDetailFragment() {
	}

	private void getArgs() {
		this.recruitDetail = (Recruit) getArguments()
				.getSerializable("recruit");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (recruitDetail == null)
			getArgs();
		if (recruitDetail.getDescription() == null)
			initData(appContext);
		if (recruitHandler == null) {
			recruitHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						fillView();
					} else {
						hideLoadProgressWithError(scrollView);
						((AppException) msg.obj).makeToast(getActivity());
					}
				}
			};
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View recruitDetailView = inflater
				.inflate(R.layout.recruit_detail, null);
		initView(recruitDetailView);
		// getActivity().getActionBar().setDisplayShowHomeEnabled(false);
		initLoadingView(recruitDetailView);
		if (!isloading && recruitDetail.getDescription() != null) {
			hideLoadProgress(scrollView);
			fillView();
		}
		if (isloading) {
			showLoadProgress(scrollView);
		}
		if (loadError) {
			hideLoadProgressWithError(scrollView);
		}
		Log.i("bug", "create View complete");
		return recruitDetailView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void initHandler() {
		if (recruitHandler == null) {
			recruitHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						fillView();
					} else {
						hideLoadProgressWithError(scrollView);
						((AppException) msg.obj).makeToast(getActivity());
					}
				}
			};
		}
	}

	protected Recruit getData() throws AppException {
		return null;
	}

	public void initData(AppContext context) {
		if (isloading) {
			return;
		}
		if (context != null)
			this.appContext = context;
		Log.i("test", "init recruit data");
		if (recruitDetail == null)
			getArgs();
		initHandler();
		showLoadProgress(scrollView);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					if (appContext == null)
						Log.i("bug", "recruit detail app contest is null !!!!");
					Recruit result = getData();
					if (result.getDescription() != null) {
						recruitDetail.setDescription(result.getDescription());
						recruitDetail.setForm(result.getForm());
						recruitDetail.setUrl(result.getUrl());
					} else {
						recruitDetail.setContent(result.getContent());
						recruitDetail.setUrl(result.getUrl());
						Log.i("test","url is "+recruitDetail.getUrl());
					}
					msg.what = 1;
				} catch (AppException e) {
					msg.what = -1;
					msg.obj = e;
				}
				recruitHandler.sendMessage(msg);
			}
		}.start();
	}

	protected void fillView() {
		Log.i("ac", "fill View");
		if (recruitDetail == null || recruitDetail.getUrl() == null)
			return;
		hideLoadProgress(scrollView);
		if (vURLLayout != null) {
			if (!recruitDetail.getUrl().isEmpty()) {
				vURLLayout.setVisibility(View.VISIBLE);
			}
		}
		Log.i("ac", "fill View 2");
		if (appContext.recruitLoadFromDisk) {
			appContext.recruitLoadFromDisk = false;
			UIHelper.ToastMessage(getActivity(),
					getString(R.string.load_fail));
		}
	}

	// 初始化视图控件
	protected void initView(View recruitDetailView) {
		Log.i("ac", "initView");
		scrollView = (ScrollView) recruitDetailView
				.findViewById(R.id.recruit_description_layout);
		vContentHeaderTextView = (TextView) recruitDetailView
				.findViewById(R.id.recruit_content_header_text);
		vContentTextView = (TextView) recruitDetailView
				.findViewById(R.id.recruit_content_info);
		Log.i("ac", "1");
		vFamousFlag = (ImageView) recruitDetailView
				.findViewById(R.id.recruit_famous_flag);
		vSourceFrom = (TextView) recruitDetailView
				.findViewById(R.id.recruit_source_from);
		vCompanyName = (TextView) recruitDetailView
				.findViewById(R.id.recruit_company_name);
		vCreatedTime = (TextView) recruitDetailView
				.findViewById(R.id.recruit_created_time);
		vPosition = (TextView) recruitDetailView
				.findViewById(R.id.recruit_position);
		vPlace = (TextView) recruitDetailView.findViewById(R.id.recruit_place);
		vCompanyType = (TextView) recruitDetailView
				.findViewById(R.id.recruit_company_type);
		vCompanyIndustry = (TextView) recruitDetailView
				.findViewById(R.id.recruit_company_industry);
		vGoToBBSSection = (TextView)recruitDetailView
				.findViewById(R.id.recruit_goto_section);
		vURLLayout = recruitDetailView
				.findViewById(R.id.recruit_original_url_layout);
		vURLLayout.setVisibility(View.GONE);
		if (recruitDetail.getFamous() == 1)
			vFamousFlag.setVisibility(View.VISIBLE);
		else {
			vFamousFlag.setVisibility(View.INVISIBLE);
		}
		vSourceFrom.setText(recruitDetail.getSourceFrom());
		vCompanyName.setText(recruitDetail.getCompany().getCompanyName());
		vPosition.setText(recruitDetail.getPosition());
		vPlace.setText(recruitDetail.getPlace());
		
		String friendlyTime = null;
		try {
			friendlyTime = StringUtils.friendly_created_time(recruitDetail
					.getCreatedTime());
		} catch (Exception e) {
			Log.i("bug", "time format is wrong recruit adapter");
			friendlyTime = recruitDetail.getCreatedTime();
		}
		vCreatedTime.setText(friendlyTime);
		vCompanyIndustry.setText(recruitDetail.getCompany().getIndustry());
		vCompanyType.setText(recruitDetail.getCompany().getType());
		/*
		 * vJoins.setText(recruitDetail.getJoins() + "");
		 * vReplies.setText(recruitDetail.getReplies() + "");
		 */
		if (vURLLayout == null) {
			Log.i("test","vurl is null");
		}
		vURLLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("bug", "click");
				Log.i("bug", "url is :" + recruitDetail.getUrl());
				UIHelper.openBrowser(v.getContext(), recruitDetail.getUrl());

			}
		});
		
		vURLLayout.setVisibility(View.GONE);
		Log.i("ac", "fillView");
		fillView();

		Log.i("ac", "initRecruit");

	}

}
