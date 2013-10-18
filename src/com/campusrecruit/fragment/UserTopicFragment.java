package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.adapter.ListViewTopicsAdapter;
import com.campusrecruit.adapter.ListViewUserTopicsAdapter;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.pcncad.campusRecruit.R;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class UserTopicFragment extends EmptyFragment {
	
	private PullToRefreshListView pvTopic;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("main", "careate topic fra");
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.user_topic, null);
		initLoadingView(view);
		setEmptyText(R.string.user_topic_empty);
		// fill data
		pvTopic = (PullToRefreshListView) view
				.findViewById(R.id.user_topic_listview);
		pvTopic.setAdapter(appContext.getLvUserTopicsAdapter());
		if (isloading) {
			showLoadProgress(pvTopic);
		} else {
			if (appContext.getLvUserTopicList().isEmpty()) {
				// read data complete, but data is empty
				showEmptyView(pvTopic);
			} else {
				// show the data
				hideLoadProgress(pvTopic);
				appContext.getLvUserTopicsAdapter().setData(
						appContext.getLvUserTopicList());
				appContext.getLvUserTopicsAdapter().notifyDataSetChanged();
			}
		}
		pvTopic.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return;
				}
				TextView title = (TextView) view
						.findViewById(R.id.user_topics_item_title);
				int topicID = Integer.parseInt(title.getTag().toString());
				// 跳转到主題詳情
				UIHelper.showTopicDetail(view.getContext(), topicID);
			}

		});
		pvTopic.setOnRefreshListener(new refreshListener());
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.appContext == null) {
			appContext = (AppContext) getActivity().getApplication();
		}
		appContext.setLvUserTopicsAdapter(new ListViewUserTopicsAdapter(
				getActivity(), R.layout.user_topic_item));
	}

	private class refreshListener implements
			PullToRefreshListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			Log.i("test", "load data refresh");
			new UserTopicAsyncTask().execute();
		}

	}

	public void initData(AppContext appContext) {
		if (this.appContext == null) {
			this.appContext = appContext;
		}
		if (!isloading) {
			new UserTopicAsyncTask().execute();
		}
	}

	private class UserTopicAsyncTask extends
			AsyncTask<Void, Void, List<BBSTopic>> {
		@Override
		protected void onPreExecute() {
			isloading = true;
		}

		@Override
		protected List<BBSTopic> doInBackground(Void... params) {
			List<BBSTopic> list = appContext.getUserTopics();
			return list;
		}

		@Override
		protected void onPostExecute(List<BBSTopic> list) {
			pvTopic.onRefreshComplete();
			if (!appContext.getLvUserTopicList().isEmpty()) {
				// 去重
				for (BBSTopic topic : list) {
					boolean flag = false;
					for (BBSTopic topic2 : appContext.getLvUserTopicList()) {
						if (topic.getTopicID() == topic2.getTopicID()) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						appContext.getLvUserTopicList().add(topic);
					}
				}
			} else {
				appContext.getLvUserTopicList().addAll(list);
			}
			if (appContext.getLvUserTopicList().isEmpty()) {
//				hideLoadProgress(pvTopic);
				showEmptyView(pvTopic);
//				new UserTopicInternetAsyncTask().execute();
			} else {
				hideLoadProgress(pvTopic);
				if (appContext.getLvUserTopicsAdapter() != null) {
					appContext.getLvUserTopicsAdapter().setData(
							appContext.getLvUserTopicList());
					appContext.getLvUserTopicsAdapter().notifyDataSetChanged();
				}
			}
			Log.i("bug", appContext.getLvUserTopicList() + " topic list size ");

		}

	}

	/*private class UserTopicInternetAsyncTask extends
			AsyncTask<Void, Void, List<BBSTopic>> {
		@Override
		protected void onPreExecute() {
			isloading = true;
		}

		@Override
		protected List<BBSTopic> doInBackground(Void... params) {
			List<BBSTopic> list;
			try {
				list = appContext.getUserTopicsFromInternet();
				return list;
			} catch (AppException e) {
				e.makeToast(getActivity());
				hideLoadProgressWithError(pvTopic);
				pvTopic.onRefreshComplete(new Date().toLocaleString());
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<BBSTopic> list) {
			if (list == null)
				return;
			pvTopic.onRefreshComplete(new Date().toLocaleString());
			Log.i("bug", "user topic handle");
			if (!appContext.getLvUserTopicList().isEmpty()) {
				// 去重
				for (BBSTopic topic : list) {
					boolean flag = false;
					for (BBSTopic topic2 : appContext.getLvUserTopicList()) {
						if (topic.getTopicID() == topic2.getTopicID()) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						appContext.getLvUserTopicList().add(topic);
					}
				}
			} else {
				appContext.getLvUserTopicList().addAll(list);
			}
			if (appContext.getLvUserTopicList().isEmpty()) {
				showEmptyView(pvTopic);
			} else {
				hideLoadProgress(pvTopic);
				if (appContext.getLvUserTopicsAdapter() != null) {
					appContext.getLvUserTopicsAdapter().setData(
							appContext.getLvUserTopicList());
					appContext.getLvUserTopicsAdapter().notifyDataSetChanged();
				}
			}
			Log.i("bug", appContext.getLvUserTopicList() + " topic list size ");

		}

	}*/

}
