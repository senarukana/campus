package com.campusrecruit.fragment;

import com.campusrecruit.activity.RecruitDetailActivity;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSSection;
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

public class RecruitSimpleDetailFragment extends RecruitDetailFragment {
	
	public static RecruitSimpleDetailFragment newInstance(Recruit recruit) {
		RecruitSimpleDetailFragment fragment = new RecruitSimpleDetailFragment();
		Bundle args = new Bundle();
		args.putSerializable("recruit", recruit);
		fragment.setArguments(args);
		return fragment;
	}
	
	protected Recruit getData() throws AppException{
		return appContext.getRecruitSimpleDetail(recruitDetail.getRecruitID());
	}
	
	@Override
	protected void initView(View v) {
		super.initView(v);
		vContentHeaderTextView.setText("校园招聘");
		vGoToBBSSection.setVisibility(View.VISIBLE);
		vGoToBBSSection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BBSSection section = new BBSSection();
				section.setCompanyID(recruitDetail.getCompany().getCompanyID());
				section.setSectionName(recruitDetail.getCompany().getCompanyName());
				UIHelper.showTopicList(getActivity(), section);
			}
		});
	}

	protected void fillView() {
		super.fillView();
		if (recruitDetail.getContent() == null)
			return;
		else {
			if (recruitDetail.getContent().isEmpty()) {
				vContentTextView.setText(R.string.empty_field);
			} else {
				vContentTextView.setText(recruitDetail.getContent());
			}
			
		}
	}

	

}
