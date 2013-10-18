package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.activity.ScheduleActivity;
import com.campusrecruit.adapter.ListViewScheduleAdapter;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.Schedules;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.pcncad.campusRecruit.R;

public class ScheduleListFragment extends EmptyFragment {

	private PullToRefreshListView pvSchedule = null;
	private Handler scheduleHandler;
	private AppContext appContext = null;
	LinearLayout emptyView;
	private ListViewScheduleAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
		mAdapter = new ListViewScheduleAdapter(getActivity(),
				(AppContext) getActivity().getApplication(),
				R.layout.schedule_day_item);
		initHandler();
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.schedule_day_list, null);
		initLoadingView(view);
		setEmptyText(R.string.schedule_list_empty);
		emptyText.setTextColor(getResources().getColor(R.color.myblue));
		emptyLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity.currentViewPosition = 1;
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
			}
		});

		pvSchedule = (PullToRefreshListView) view
				.findViewById(R.id.schedule_day_listview);
		Log.i("bug", "test");
		pvSchedule.setAdapter(mAdapter);
		pvSchedule.setOnRefreshListener(new refreshListener());

		if (isloading) {
			showLoadProgress(pvSchedule);
		} else {
			if (ScheduleActivity.scheduleList.isEmpty()) {
				// read data complete, but data is empty
				showEmptyView(pvSchedule);
			} else {
				hideLoadProgress(pvSchedule);
				mAdapter.setData(ScheduleActivity.scheduleList);
				mAdapter.notifyDataSetChanged();
			}
		}
		return view;
	}

	private void initHandler() {
		if (scheduleHandler == null) {
			scheduleHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					pvSchedule.onRefreshComplete();
					ScheduleActivity.scheduleList = (List<Schedules>) msg.obj;
					if (ScheduleActivity.scheduleList.isEmpty()) {
						showEmptyView(pvSchedule);
					} else {
						hideLoadProgress(pvSchedule);
						mAdapter.setData(ScheduleActivity.scheduleList);
						mAdapter.notifyDataSetChanged();
					}
				}
			};
		}
	}

	public void initData() {
		if (isloading)
			return;
		initHandler();
		showLoadProgress(pvSchedule);
		new Thread() {
			public void run() {
				Message msg = new Message();
				Log.i("bug", "init sc data");
				List<Schedules> list = appContext.scheduleGetAll();
				if (ScheduleActivity.scheduleList == null)
					list = new ArrayList<Schedules>();
				Log.i("bug", "read sc data");
				msg.what = 1;
				msg.obj = list;
				scheduleHandler.sendMessage(msg);
			};
		}.start();
	}

	private class refreshListener implements
			PullToRefreshListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			initData();
		}

	}

}