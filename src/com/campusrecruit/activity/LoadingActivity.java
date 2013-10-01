package com.campusrecruit.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.krislq.sliding.R;

public class LoadingActivity extends Activity{

	protected View vFooter = null;
	protected TextView vFooterTextView = null;
	protected ProgressBar vFooterProgressBar = null;
	protected boolean isloading = false;
	
	protected void initLoadingView() {
		vFooter = findViewById(R.id.loading_footer_layout);
		vFooterTextView = (TextView) vFooter
				.findViewById(R.id.loading_foot_more);
		vFooterProgressBar = (ProgressBar) vFooter
				.findViewById(R.id.loading_foot_progress);
	}
	
	protected void initLoadingView(View v) {
		vFooter = v.findViewById(R.id.loading_footer_layout);
		vFooterTextView = (TextView) vFooter
				.findViewById(R.id.loading_foot_more);
		vFooterProgressBar = (ProgressBar) vFooter
				.findViewById(R.id.loading_foot_progress);
	}

	
	protected void hideLoadProgress(View v) {
		isloading = false;
		if (vFooter != null) {
			vFooter.setVisibility(View.GONE);
		}
		if (v != null) {
			v.setVisibility(View.VISIBLE);
		}
	}
	
	protected void showLoadProgress(View v) {
		isloading = true;
		if (vFooter != null) {
			vFooter.setVisibility(View.VISIBLE);
		}
		if (v != null) {
			v.setVisibility(View.GONE);
		}
	}
	
	protected void hideLoadProgressWithError(View v) {
		Log.i("bug","hide load process with error");
		isloading = false;
		if (vFooter != null) {
			vFooter.setVisibility(View.VISIBLE);
			vFooterProgressBar.setVisibility(View.GONE);
			vFooterTextView.setText(R.string.load_over_time);
		}
		if (v != null) {
			v.setVisibility(View.GONE);
		}
	}
}
