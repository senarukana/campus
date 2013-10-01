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
import com.krislq.sliding.R;

public class RecruitDetailFragment extends LoadingFragment {

	/*
	 * private FrameLayout vHead; private ImageView vHome; private ImageView
	 * vRefresh; private TextView vHeadTitle;
	 */
	private LinkView vRecruitDetail;
	private ScrollView scrollView;

	private ImageView vFamousFlag;
	private View vURLLayout;
	private WebView vForm;
	private TextView vSourceFrom;
	private TextView vCompanyName;
	private TextView vCreatedTime;
	private TextView vPosition;
	private TextView vPlace;
	private TextView vCompanyIndustry;
	private TextView vCompanyType;

	private Handler recruitHandler;

	private AppContext appContext;

	private Recruit recruitDetail;

	public RecruitDetailFragment() {
	}

	public static RecruitDetailFragment newInstance(Recruit recruit) {
		RecruitDetailFragment fragment = new RecruitDetailFragment();
		Bundle args = new Bundle();
		args.putSerializable("recruit", recruit);
		fragment.setArguments(args);
		return fragment;
	}

	private void getArgs() {
		this.recruitDetail = (Recruit) getArguments()
				.getSerializable("recruit");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
		if (recruitDetail == null)
			getArgs();
		if (appContext == null)
			Log.i("bug", "fuck!!! recruit detail");
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
		Log.i("bug", "create recruit detail View");
		View recruitDetailView = inflater
				.inflate(R.layout.recruit_detail, null);
		scrollView = (ScrollView) recruitDetailView
				.findViewById(R.id.recruit_description_layout);
		// getActivity().getActionBar().setDisplayShowHomeEnabled(false);
		initLoadingView(recruitDetailView);
		initView(recruitDetailView);
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

	public void initData(AppContext context) {
		if (isloading) {
			return;
		}
		if (context != null)
			this.appContext = context;
		Log.i("test", "init recruit data");
		if (recruitDetail == null)
			getArgs();
		showLoadProgress(scrollView);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					if (appContext == null)
						Log.i("bug", "recruit detail app contest is null !!!!");
					Recruit result = appContext.getRecruitDetail(recruitDetail
							.getRecruitID());
					recruitDetail.setDescription(result.getDescription());
					recruitDetail.setForm(result.getForm());
					recruitDetail.setUrl(result.getUrl());
					msg.what = 1;
				} catch (AppException e) {
					msg.what = -1;
					msg.obj = e;
				}
				recruitHandler.sendMessage(msg);
			}
		}.start();
	}

	private void fillView() {
		Log.i("bug", "fill view");
		if (recruitDetail == null)
			return;
		hideLoadProgress(scrollView);
		if (appContext.recruitLoadFromDisk) {
			appContext.recruitLoadFromDisk = false;
			UIHelper.ToastMessage(getActivity(), getString(R.string.load_fail));
		}
		if (vForm != null) {
			Log.i("bug", "vform");
			if (recruitDetail.getForm() != null
					&& !recruitDetail.getForm().isEmpty()) {
				vForm.setVisibility(View.VISIBLE);
				vForm.getSettings().setSupportZoom(true);
				vForm.getSettings().setBuiltInZoomControls(true);
				vForm.getSettings().setDefaultFontSize(13);
				// String body = UIHelper.WEB_STYLE + recruitDetail.getForm() +
				// "<div style=\"margin-bottom: 20px\" />";
				String body = recruitDetail.getForm();
				vForm.loadDataWithBaseURL(null, body, "text/html", "utf-8",
						null);
			} else {
				vForm.setVisibility(View.GONE);
			}
		}
		if (vURLLayout != null) {
			Log.i("bug", "vurl");
			if (recruitDetail.getUrl() != null
					&& !recruitDetail.getUrl().isEmpty()) {
				vURLLayout.setVisibility(View.VISIBLE);
				vURLLayout.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.i("bug", "click");
						Log.i("bug", "url is :" + recruitDetail.getUrl());
						UIHelper.openBrowser(v.getContext(), recruitDetail.getUrl());

					}
				});
			}
		}
		if (vRecruitDetail != null) {
			if ((recruitDetail.getDescription() == null || recruitDetail.getDescription().isEmpty())
					&& (recruitDetail.getUrl() != null && !recruitDetail.getUrl().isEmpty()))
				vRecruitDetail.setText("暂时没有该数据");
			else
				vRecruitDetail.setText(recruitDetail.getDescription());
		}
		Log.i("bug", "fill view complete");
	}

	// 初始化视图控件
	private void initView(View recruitDetailView) {
		Log.i("ac", "initView");

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
		vRecruitDetail = (LinkView) recruitDetailView
				.findViewById(R.id.recruit_description_info);
		vForm = (WebView) recruitDetailView
				.findViewById(R.id.recruit_detail_webview);
		vURLLayout = recruitDetailView.findViewById(R.id.recruit_original_url_layout);
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

		if (recruitDetail.getDescription() != null) {
			fillView();
		}

		Log.i("ac", "initRecruit");

	}

}
