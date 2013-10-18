package com.campusrecruit.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.UIHelper;
import com.pcncad.campusRecruit.R;

public class RecruitDetailDescriptionFragment extends RecruitDetailFragment {
	
	private WebView vForm;

	public static RecruitDetailDescriptionFragment newInstance(Recruit recruit) {
		RecruitDetailDescriptionFragment fragment = new RecruitDetailDescriptionFragment();
		Bundle args = new Bundle();
		args.putSerializable("recruit", recruit);
		fragment.setArguments(args);
		return fragment;
	}
	protected Recruit getData() throws AppException{
		return appContext.getRecruitDetail(recruitDetail.getRecruitID());
	}
	
	@Override
	protected void initView(View v) {
		super.initView(v);
		vForm = (WebView) v.findViewById(R.id.recruit_detail_webview);
		vContentHeaderTextView.setText("职位描述");
	}
	
	protected void fillView() {
		super.fillView();
		Log.i("test","recruit detail fill view");
		if (recruitDetail == null || recruitDetail.getDescription() == null)
			return ;
		Log.i("test","recruit detail fill view 2");
		if (appContext.recruitLoadFromDisk) {
			appContext.recruitLoadFromDisk = false;
			UIHelper.ToastMessage(getActivity(), getString(R.string.load_fail));
		}
		Log.i("test","recruit detail fill view 3");
		if (vForm != null) {
			if (recruitDetail.getForm().isEmpty()) {
				vForm.setVisibility(View.GONE);
			} else {
				vForm.setVisibility(View.VISIBLE);
				vForm.getSettings().setSupportZoom(true);
				vForm.getSettings().setBuiltInZoomControls(true);
				vForm.getSettings().setDefaultFontSize(13);
				String body = recruitDetail.getForm();
				vForm.loadDataWithBaseURL(null, body, "text/html", "utf-8",null);
			}
		}
		Log.i("test","recruit detail fill view 4");
		if (recruitDetail.getDescription().isEmpty()) {
			vContentTextView.setText(R.string.empty_field);
		} else {
			vContentTextView.setText(recruitDetail.getDescription());
		}
	}
}
