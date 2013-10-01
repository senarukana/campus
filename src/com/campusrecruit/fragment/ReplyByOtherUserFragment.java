package com.campusrecruit.fragment;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.campusrecruit.activity.RecruitDetailActivity;
import com.campusrecruit.adapter.ListViewReplyByOthersAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.krislq.sliding.R;

public class ReplyByOtherUserFragment extends EmptyFragment {
	private AppContext appContext;
	private PullToRefreshListView pvReplyByOthers;
	private List<BBSReply> replyByOtherList = new ArrayList<BBSReply>();
	private ListViewReplyByOthersAdapter pvReplyByOthersAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("main", "reply byother init view");
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.reply_by_other, null);
		initLoadingView(view);
		setEmptyText(R.string.reply_by_other_empty);
		// fill data
		initView(view);
		if (isloading) {
			showLoadProgress(pvReplyByOthers);
		} else {
			if (loadError) {
				hideLoadProgressWithError(pvReplyByOthers);
			} else {
				if (replyByOtherList.isEmpty()) {
					showEmptyView(pvReplyByOthers);
				} else {
					hideLoadProgress(pvReplyByOthers);
					if (replyByOtherList.size() < AppConfig.PAGE_SIZE) {
						pvReplyByOthersAdapter.notifyDataSetChanged();
						vFooterTextView.setText(R.string.load_full);
					} else if (replyByOtherList.size() >= AppConfig.PAGE_SIZE) {
						pvReplyByOthersAdapter.notifyDataSetChanged();
						vFooterTextView.setText(R.string.load_more);
					}
				}
			}
		}
		Log.i("bug", "reply by other complete");
		return view;
	}

	private void initView(View view) {
		pvReplyByOthers = (PullToRefreshListView) view
				.findViewById(R.id.reply_by_other_listview);

		pvReplyByOthers.setAdapter(pvReplyByOthersAdapter);
		pvReplyByOthers.setOnRefreshListener(new refreshListener());

	}

	private class refreshListener implements
			PullToRefreshListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			Log.i("test", "load data refresh");
			new ReplyByOtherAsyncTask().execute();
		}

	}

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (appContext == null) {
			appContext = (AppContext) getActivity().getApplication();
		}
		pvReplyByOthersAdapter = new ListViewReplyByOthersAdapter(
				getActivity(), appContext, R.layout.reply_by_other_item);
		/*
		 * if (!isloading && replyByOtherList.isEmpty()) { new
		 * ReplyByOtherAsyncTask().execute(); }
		 */
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("rd", "comments pub handler");
		if (resultCode != Activity.RESULT_OK)
			return;
		Toast.makeText(getActivity(), "回复成功", Toast.LENGTH_SHORT).show();
	}

	public void initData(AppContext appContext) {
		if (this.appContext == null) {
			this.appContext = appContext;
		}
		if (!isloading) {
			new ReplyByOtherAsyncTask().execute();
		}
	}

	private class ReplyByOtherAsyncTask extends
			AsyncTask<Void, Void, List<BBSReply>> {
		@Override
		protected void onPreExecute() {
			showLoadProgress(pvReplyByOthers);
		}

		@Override
		protected List<BBSReply> doInBackground(Void... voids) {
			List<BBSReply> list;
			try {
				list = appContext.getUserReplyByOthers();
				Log.i("test", "get list");
				return list;
			} catch (AppException e) {
				hideLoadProgressWithError(pvReplyByOthers);
				e.makeToast(getActivity());
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<BBSReply> list) {
			pvReplyByOthers.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
			if (appContext.replyByLoadFromDisk && !list.isEmpty()) {
				UIHelper.ToastMessage(getActivity(),
						getString(R.string.load_fail));
				hideLoadProgressWithError(pvReplyByOthers);
			}
			pvReplyByOthers.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
			replyByOtherList = list;
			if (replyByOtherList.isEmpty()) {
				showEmptyView(pvReplyByOthers);
			} else {
				if (!appContext.replyByLoadFromDisk) {
					hideLoadProgress(pvReplyByOthers);
				}
				if (pvReplyByOthersAdapter != null) {
					pvReplyByOthersAdapter.setData(list);
					pvReplyByOthersAdapter.notifyDataSetChanged();
				}
			}
		}

	}
}
