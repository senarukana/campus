package com.campusrecruit.fragment;

import com.krislq.sliding.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmptyFragment extends LoadingFragment {
	protected View emptyLayout;
	protected TextView emptyText;

	@Override
	protected void initLoadingView(View v) {
		emptyLayout = v.findViewById(R.id.empty_layout);
		emptyText = (TextView) v.findViewById(R.id.empty_msg);
		super.initLoadingView(v);
	}

	protected void setEmptyText(int resid) {
		if (emptyText != null) {
			emptyText.setText(resid);
		}
	}

	protected void showEmptyView(View v) {
		isloading = false;
		if (emptyLayout != null) {
			emptyLayout.setVisibility(View.VISIBLE);
			vFooter.setVisibility(View.GONE);
		}
		if (v != null)
			v.setVisibility(View.GONE);
	}

	@Override
	protected void showLoadProgress(View v) {
		if (emptyLayout != null)
			emptyLayout.setVisibility(View.GONE);
		super.showLoadProgress(v);
	}

	@Override
	protected void hideLoadProgress(View v) {
		if (emptyLayout != null)
			emptyLayout.setVisibility(View.GONE);
		super.hideLoadProgress(v);
	}

	@Override
	protected void hideLoadProgressWithError(View v) {
		if (emptyLayout != null)
			emptyLayout.setVisibility(View.GONE);
		super.hideLoadProgressWithError(v);
	}
}
