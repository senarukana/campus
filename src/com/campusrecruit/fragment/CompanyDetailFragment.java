package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSSection;
import com.campusrecruit.bean.Company;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.LinkView;
import com.pcncad.campusRecruit.R;

import android.annotation.SuppressLint;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class CompanyDetailFragment extends LoadingFragment {

	private ScrollView scrollView;

	private Company company = null;
	private TextView vCompanyName;
	private TextView vCompanyPlace;
	private TextView vCompanyIndustry;
	private TextView vCompanyGoToSection;
	private LinkView vCompanyIntroduction;

	

	private Handler companyHandler;

	public static CompanyDetailFragment newInstance(Company company) {
		CompanyDetailFragment fragment = new CompanyDetailFragment();
		Bundle args = new Bundle();
		args.putSerializable("company", company);
		fragment.setArguments(args);
		return fragment;
	}

	private void getArgs() {
		this.company = (Company) getArguments().getSerializable("company");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("bug", "company detail oncreate");
		if (appContext == null)
			appContext = (AppContext) getActivity().getApplication();
		if (company == null)
			getArgs();
		if (companyHandler != null) {
			initHandler();
		}
	}

	private void initHandler() {
		companyHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				isloading = false;
				if (msg.what == 1)
					fillView();
				else {
					hideLoadProgressWithError(scrollView);
				}
			}
		};
	}

	public void initData(AppContext context) {
		if (context != null)
			appContext = context;
		if (company == null)
			getArgs();
		if (companyHandler == null) {
			initHandler();
		}
		if (isloading)
			return;
		isloading = true;
		showLoadProgress(scrollView);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Company newCompany = appContext.getCompanyDetail(company
							.getCompanyID());
					company.setIntroduction(newCompany.getIntroduction());
					company.setPlace(newCompany.getPlace());
					msg.what = 1;
					Log.i("company", "send msg");
				} catch (AppException e) {
					
					msg.what = -1;
					msg.obj = e;
				}
				companyHandler.sendMessage(msg);
			}
		}.start();
	}

	private void fillView() {
		hideLoadProgress(scrollView);
		if (appContext.companyLoadFromDisk && 
				(company.getIntroduction() != null)) {
			appContext.companyLoadFromDisk = false;
			UIHelper.ToastMessage(getActivity(), getString(R.string.load_fail));
		}
		if (vCompanyPlace != null) {
			if (company.getPlace() != null && !company.getPlace().isEmpty())
				vCompanyPlace.setText(company.getPlace());
			else
				vCompanyPlace.setText(R.string.nodata);
			if (company.getIntroduction() != null
					&& !company.getIntroduction().isEmpty())
				vCompanyIntroduction.setText(company.getIntroduction());
			else
				vCompanyIntroduction.setText(R.string.nodata);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// inflater the layout
		View view = inflater.inflate(R.layout.company_info, null);
		initLoadingView(view);
		scrollView = (ScrollView) view.findViewById(R.id.company_info_layout);
		vCompanyGoToSection = (TextView) view.findViewById(R.id.company_goto_section);
		vCompanyName = (TextView) view.findViewById(R.id.company_detail_name);
		vCompanyPlace = (TextView) view
				.findViewById(R.id.company_detail_address);
		vCompanyIndustry = (TextView) view
				.findViewById(R.id.company_detail_industry);
		vCompanyIntroduction = (LinkView) view
				.findViewById(R.id.company_detail_info);
		vCompanyName.setText(company.getCompanyName());
		vCompanyIndustry.setText(company.getIndustry());
		vCompanyGoToSection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				BBSSection section = new BBSSection();
				section.setCompanyID(company.getCompanyID());
				section.setSectionName(company.getCompanyName());
				UIHelper.showTopicList(getActivity(), section);
			}
		});
		if (isloading) {
			showLoadProgress(scrollView);
		} else {
			if (loadError) {
				hideLoadProgressWithError(scrollView);
			} else {
				if (company.getIntroduction() != null) {
					Log.i("test", "compay introduction complete");
					hideLoadProgress(scrollView);
					fillView();
				} else {
					initData(appContext);
				}
			}
		}
		return view;
	}

}
