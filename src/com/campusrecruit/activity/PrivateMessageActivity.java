package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.campusrecruit.activity.PrivateMessageListActivity;
import com.campusrecruit.activity.RecruitDetailActivity;
import com.campusrecruit.adapter.ListViewReplyByOthersAdapter;
import com.campusrecruit.adapter.ListViewUserMessageAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.UserMessage;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.pcncad.campusRecruit.R;

public class PrivateMessageActivity extends EmptyActivity {
	

	private PullToRefreshListView pvMessage;
	private List<UserMessage> messageList = new ArrayList<UserMessage>();
	private ListViewUserMessageAdapter pvMessageAdapter;
	private boolean isinit = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_message);
		if (appContext == null) {
			
		}
		initView();
		if (messageList.isEmpty()) {
			new PrivateMessageDiskAsyncTask().execute();
		}
		Log.i("bug", "pa oncreate complete");

	}
	

	private void initView() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("私信");
		initLoadingView();
		setEmptyText(R.string.private_message_empty);

		pvMessage = (PullToRefreshListView) findViewById(R.id.user_meesage_listview);
		pvMessageAdapter = new ListViewUserMessageAdapter(this, appContext,
				R.layout.private_message_item);
		pvMessage.setAdapter(pvMessageAdapter);
		pvMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0)
					return;
				TextView vUserName = (TextView) view
						.findViewById(R.id.private_message_user_name);
				UserMessage message = (UserMessage) vUserName.getTag();
				String userId = message.getUserID();
				Log.i("td", userId + ":" + message.getCreatedTime());
				UIHelper.showPrivateMessageList(PrivateMessageActivity.this,
						userId, message.getUserName(), message.getCreatedTime());
				// 跳转到主題詳情
				/*
				 * UIHelper.showCommentPub(getActivity(), reply.getTopicID(),
				 * reply.getReplyID(), reply.getUserName(), reply.getContent());
				 */
			}

		});
		pvMessage.setOnRefreshListener(new refreshListener());
	}

	private class refreshListener implements
			PullToRefreshListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			new PrivateMessageAsyncTask().execute();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;

		default:
			return false;
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		new PrivateMessageDiskAsyncTask().execute();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("rd", "comments pub handler");
		if (resultCode != Activity.RESULT_OK)
			return;
		Toast.makeText(PrivateMessageActivity.this, "回复成功", Toast.LENGTH_SHORT)
				.show();
	}

	private class PrivateMessageDiskAsyncTask extends
			AsyncTask<Void, Void, List<UserMessage>> {

		@Override
		protected List<UserMessage> doInBackground(Void... voids) {
			List<UserMessage> list;
			list = appContext.getMessageListLocal();
			return list;
		}

		@Override
		protected void onPostExecute(List<UserMessage> list) {
			messageList.clear();
			messageList.addAll(list);
			if (!list.isEmpty() && pvMessageAdapter != null) {
				pvMessageAdapter.setData(messageList);
				pvMessageAdapter.notifyDataSetChanged();
			}
			new PrivateMessageAsyncTask().execute();

		}
	}

	private class PrivateMessageAsyncTask extends
			AsyncTask<Void, Void, List<UserMessage>> {
		@Override
		protected void onPreExecute() {
			showLoadProgress(pvMessage);
		}

		@Override
		protected List<UserMessage> doInBackground(Void... voids) {
			List<UserMessage> list;
			try {
				Log.i("test", "do");
				list = appContext.getMessageListFromInternet();
				return list;
			} catch (AppException e) {
				// e.makeToast(PrivateMessageActivity.this);
				hideLoadProgressWithError(pvMessage);
				pvMessage.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
				Log.i("test", "hide error");
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<UserMessage> list) {
			Log.i("bug", "post ok");
			pvMessage.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
			if (pvMessageAdapter != null) {
				if (messageList.isEmpty()) {
					messageList.addAll(list);
				} else {
					Log.i("test", "message size is " + list.size());
					// 标识新到的请求
					for (UserMessage comingMessage : list) {
						Log.i("test",comingMessage.getUserID());
						boolean flag = false;
						for (UserMessage message : messageList) {
							Log.i("test","message userId" + message.getUserID());
							if (comingMessage.getUserID().equals(message.getUserID())) {
								Log.i("test","wwwwww");
								flag = true;
								message.setFace(comingMessage.getFace());
								if (message.getCreatedTime().compareTo(
										comingMessage.getCreatedTime()) < 0) {
									// 用户信息时间戳更大,则表明有新的消息
									message.setCreatedTime(comingMessage
											.getCreatedTime());
									message.setContent(comingMessage.getContent());
									message.setStatus(1);
								}
								break;
							}
						}
						// 没有找到该用户,则说明新的用户发来了私信
						if (!flag) {
							messageList.add(comingMessage);
						}
					}
				}
				if (!messageList.isEmpty()) {
					appContext.saveMessages(messageList);
					hideLoadProgress(pvMessage);
					pvMessageAdapter.setData(messageList);
					pvMessageAdapter.notifyDataSetChanged();
				}
			}
			if (messageList.isEmpty()) {
				showEmptyView(pvMessage);
			}
		}
	}
}
