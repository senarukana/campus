package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.campusrecruit.activity.RecruitDetailActivity;
import com.campusrecruit.adapter.ListViewReplyByOthersAdapter;
import com.campusrecruit.adapter.ListViewReplyToOthersAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.krislq.sliding.R;

public class ReplyToOtherUserFragment extends EmptyFragment {
	private AppContext appContext;
	private PullToRefreshListView pvReplyToOthers;
	private List<BBSReply> replyToOtherList = new ArrayList<BBSReply>();
	private ListViewReplyToOthersAdapter lvReplyToOthersAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("bug", "reply toother init view");
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.reply_to_other, null);
		initLoadingView(view);
		setEmptyText(R.string.reply_to_other_empty);
		// fill data
		pvReplyToOthers = (PullToRefreshListView) view
				.findViewById(R.id.reply_to_other_listview);
		pvReplyToOthers.setAdapter(lvReplyToOthersAdapter);
		pvReplyToOthers.setOnRefreshListener(new refreshListener());
		if (isloading) {
			Log.i("bug", "++++++");
			showLoadProgress(pvReplyToOthers);
			Log.i("bug", "okkkkkkkkkk");
		} else {
			Log.i("bug", "000000");
			if (loadError) {
				Log.i("bug", "---------------");
				hideLoadProgressWithError(pvReplyToOthers);
			} else {
				Log.i("bug", "1111");
				if (replyToOtherList.isEmpty()) {
					Log.i("bug", "2222");
					showEmptyView(pvReplyToOthers);
					Log.i("bug", "333");
				} else {
					Log.i("bug", "444");
					lvReplyToOthersAdapter.setData(replyToOtherList);
					hideLoadProgress(pvReplyToOthers);
					if (vFooterTextView == null)
						Log.i("bug", "shitttt");
					if (replyToOtherList.size() < AppConfig.PAGE_SIZE) {
						Log.i("bug", "555");
						lvReplyToOthersAdapter.notifyDataSetChanged();
						vFooterTextView.setText(R.string.load_full);
					} else if (replyToOtherList.size() >= AppConfig.PAGE_SIZE) {
						Log.i("bug", "666");
						lvReplyToOthersAdapter.notifyDataSetChanged();
						vFooterTextView.setText(R.string.load_more);
					}
				}
			}
		}
		// appContext.getLvUserTopicsAdapter().notifyDataSetChanged();
		Log.i("bug", "reply to other complete");
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (appContext == null) {
			appContext = (AppContext) getActivity().getApplication();
		}
		lvReplyToOthersAdapter = new ListViewReplyToOthersAdapter(
				getActivity(), appContext, R.layout.reply_to_other_item);
		/*
		 * if (!isloading && replyToOtherList.isEmpty()) { new
		 * ReplyToOtherAsyncTask().execute(); }
		 */
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("rd", "comments pub handler");
		if (resultCode != Activity.RESULT_OK)
			return;
		Toast.makeText(getActivity(), "回复成功", Toast.LENGTH_SHORT).show();

	}

	private class refreshListener implements
			PullToRefreshListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			Log.i("test", "load data refresh");
			new ReplyToOtherAsyncTask().execute();
		}

	}

	public void initData(AppContext appContext) {
		if (this.appContext == null) {
			this.appContext = appContext;
		}
		if (!isloading)
			new ReplyToOtherAsyncTask().execute();
	}

	private class ReplyToOtherAsyncTask extends
			AsyncTask<Void, Void, List<BBSReply>> {
		@Override
		protected void onPreExecute() {
			showLoadProgress(pvReplyToOthers);
		}

		@Override
		protected List<BBSReply> doInBackground(Void... voids) {
			List<BBSReply> list;
			try {
				list = appContext.getUserReplyToOthers();
				return list;
			} catch (AppException e) {
				hideLoadProgressWithError(pvReplyToOthers);
				e.makeToast(getActivity());
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<BBSReply> list) {
			pvReplyToOthers.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
			if (appContext.replyToLoadFromDisk && !list.isEmpty()) {
				UIHelper.ToastMessage(getActivity(),
						getString(R.string.load_fail));
				hideLoadProgressWithError(pvReplyToOthers);
			}
			replyToOtherList = list;
			if (replyToOtherList.isEmpty()) {
				showEmptyView(pvReplyToOthers);
			} else {
				if (!appContext.replyToLoadFromDisk) {
					hideLoadProgress(pvReplyToOthers);
				}
				if (lvReplyToOthersAdapter != null) {
					Log.i("bug", "reply to other list size is"
							+ replyToOtherList.size());
					Log.i("bug", "set adapter complete reply to");
					lvReplyToOthersAdapter.setData(replyToOtherList);
					lvReplyToOthersAdapter.notifyDataSetChanged();
					Log.i("bug", "set adapter complete reply to complete");
				}
			}
		}

	}
}
