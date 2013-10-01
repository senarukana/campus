package com.campusrecruit.activity;

import com.krislq.sliding.R;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmptyActivity extends LoadingActivity {
	protected View emptyLayout;
	protected TextView emptyText;

	@Override
	protected void initLoadingView() {
		emptyLayout = findViewById(R.id.empty_layout);
		emptyText = (TextView) findViewById(R.id.empty_msg);
		super.initLoadingView();
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
		Log.i("bug","hide load process");
		if (emptyLayout != null)
			emptyLayout.setVisibility(View.GONE);
		super.hideLoadProgressWithError(v);
	}
}
