package com.campusrecruit.fragment;

import com.krislq.sliding.R;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SearchableFragment extends LoadingFragment {
	private View searchHeader;
	private View searchHeaderFound;
	private View searchHeaderNotFound;
	private View searchHeaderErr;
	private TextView searchHeaderErrText;
	private TextView searchHeaderText;
	private TextView searchHeaderNum;
	private TextView searchHeaderNotFoundTextView;
	protected View searchFooter;
	

	protected void initLoadingView(View v) {
		searchHeader = v.findViewById(R.id.search_header_layout);
		searchFooter = v.findViewById(R.id.search_footer_layout);
		searchHeaderErr = v.findViewById(R.id.search_header_err_layout);
		searchHeaderFound = v.findViewById(R.id.search_header_found_layout);
		searchHeaderNotFound = v.findViewById(R.id.search_header_not_found_layout);
		searchHeaderErrText = (TextView) v.findViewById(R.id.search_header_err_text);
		searchHeaderNum = (TextView) v.findViewById(R.id.search_header_num);
		searchHeaderText = (TextView) v.findViewById(R.id.search_header_text);
		searchHeaderNotFoundTextView = (TextView) v.findViewById(R.id.search_header_not_found_text);
		
		super.initLoadingView(v);
	}
	
	protected void initSearchReturnBackListener(OnClickListener listener) {
		searchFooter.setOnClickListener(listener);
	}
	
	protected void hideSearchView() {
		isloading = true;
		if (searchHeader != null)
			searchHeader.setVisibility(View.GONE);
		if (searchFooter != null)
			searchFooter.setVisibility(View.GONE);
		
	}
	
	@Override
	protected void hideLoadProgressWithError(View v) {
		if (v != null)
			v.setVisibility(View.VISIBLE);
		hideSearchView();
		isloading = false;
		if (vFooter != null)
			vFooter.setVisibility(View.GONE);
	}
	
	@Override
	protected void showLoadProgress(View v) {
		isloading = true;
		if (searchHeader != null)
			searchHeader.setVisibility(View.GONE);
		if (searchFooter != null)
			searchFooter.setVisibility(View.GONE);
		if (vFooter != null) {
			vFooter.setVisibility(View.VISIBLE);
			vFooterProgressBar.setVisibility(View.VISIBLE);
			vFooterTextView.setText(R.string.load_ing);
		}
		if (v != null)
			v.setVisibility(View.GONE);
	}
	
	@Override
	protected void hideLoadProgress(View v) {
		if (searchHeader != null)
			searchHeader.setVisibility(View.GONE);
		if (searchFooter != null)
			searchFooter.setVisibility(View.GONE);
		super.hideLoadProgress(v);
	}
	
	

	protected void beginSearch(View v) {
		if (searchHeader != null)
			searchHeader.setVisibility(View.GONE);
		if (searchFooter != null)
			searchFooter.setVisibility(View.GONE);
		super.showLoadProgress(v);
	}
	

	protected void completeSearch(View v, int num, String searchStr) {
		isloading = false;
		if (searchHeader != null)
			searchHeader.setVisibility(View.VISIBLE);
		if (searchFooter != null)
			searchFooter.setVisibility(View.VISIBLE);
		if (searchHeaderNotFound != null)
			searchHeaderNotFound.setVisibility(View.GONE);
		if (searchHeaderErr != null)
			searchHeaderErr.setVisibility(View.GONE);
		if (searchHeaderFound != null)
			searchHeaderFound.setVisibility(View.VISIBLE);
		if (searchHeaderText != null) {
			searchHeaderNum.setText(num + "");
			searchHeaderText.setText(searchStr);
		}
		super.hideLoadProgress(v);
	}
	
	protected void completeSearchWithEmpty(View v, String searchStr) {
		isloading = false;
		if (searchHeader != null)
			searchHeader.setVisibility(View.VISIBLE);
		if (searchFooter != null)
			searchFooter.setVisibility(View.VISIBLE);
		if (searchHeaderNotFound != null)
			searchHeaderNotFound.setVisibility(View.VISIBLE);
		if (searchHeaderFound != null)
			searchHeaderFound.setVisibility(View.GONE);
		if (searchHeaderErr != null)
			searchHeaderErr.setVisibility(View.GONE);
		if (searchHeaderNotFoundTextView != null) {
			searchHeaderNotFoundTextView.setText(searchStr);
		}
		if (vFooter != null) {
			vFooter.setVisibility(View.GONE);
		}
		if (v != null)
			v.setVisibility(View.GONE);
	}

	protected void completeSearchWithError(View v) {
		isloading = false;
		if (searchHeader != null)
			searchHeader.setVisibility(View.VISIBLE);
		if (searchFooter != null)
			searchFooter.setVisibility(View.VISIBLE);
		
		if (searchHeaderFound != null)
			searchHeaderFound.setVisibility(View.GONE);
		if (searchHeaderNotFound != null)
			searchHeaderNotFound.setVisibility(View.GONE);
		
		if (searchHeaderErr != null)
			searchHeaderErr.setVisibility(View.VISIBLE);
		
		if (searchHeaderText != null) {
			searchHeaderErrText.setText(R.string.load_over_time);
		}
		if (v != null)
			v.setVisibility(View.GONE);
		if (vFooter != null)
			vFooter.setVisibility(View.GONE);
	}

}
