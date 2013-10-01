/*package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import com.krislq.sliding.R;

public class PrivateMessageFragment extends EmptyFragment {
	private AppContext appContext;

	private PullToRefreshListView pvMessage;
	private List<UserMessage> messageList = new ArrayList<UserMessage>();
	private ListViewUserMessageAdapter pvMessageAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("private", "private oncreate view");
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.private_message, null);
		initLoadingView(view);
		setEmptyText(R.string.private_message_list_empty);
		// fill data
		pvMessage = (PullToRefreshListView) view.findViewById(R.id.user_meesage_listview);

		pvMessage.setAdapter(pvMessageAdapter);
		if (isloading) {
			showLoadProgress(pvMessage);
		} else {
			if (loadError) {
				hideLoadProgressWithError(pvMessage);
			} else {
				if (messageList.isEmpty()) {
					showEmptyView(pvMessage);
				} else {
					hideLoadProgress(pvMessage);
					if (messageList.size() < AppConfig.PAGE_SIZE) {
						pvMessageAdapter.notifyDataSetChanged();
						vFooterTextView.setText(R.string.load_full);
					} else if (messageList.size() >= AppConfig.PAGE_SIZE) {
						pvMessageAdapter.notifyDataSetChanged();
						vFooterTextView.setText(R.string.load_more);
					}
				}
			}
		}
		// appContext.getLvUserTopicsAdapter().notifyDataSetChanged();
		pvMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView vUserName = (TextView) view
						.findViewById(R.id.private_message_user_name);
				UserMessage message = (UserMessage) vUserName.getTag();
				String userId = message.getUserID();
				String myId = appContext.getLoginUid();
				Intent intent = new Intent(getActivity(),
						PrivateMessageListActivity.class);
				intent.putExtra("userid", userId);
				intent.putExtra("myid", myId);
				Log.i("private message", "fragment" + userId + " " + myId);
				startActivity(intent);
				// 跳转到主題詳情
				
				 * UIHelper.showCommentPub(getActivity(), reply.getTopicID(),
				 * reply.getReplyID(), reply.getUserName(), reply.getContent());
				 
			}

		});

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (appContext == null) {
			appContext = (AppContext) getActivity().getApplication();
		}
		pvMessageAdapter = new ListViewUserMessageAdapter(getActivity(),
				R.layout.private_message_item);
		if (isloading messageList.isEmpty()) {
			new PrivateMessageAsyncTask().execute();
		}

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
				list = appContext.getMessageListLocal();
				return list;
			} catch (AppException e) {
				e.makeToast(getActivity());
				hideLoadProgressWithError(pvMessage);
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<UserMessage> list) {
			hideLoadProgress(pvMessage);
			messageList = list;
			if (pvMessageAdapter != null) {
				if (messageList.isEmpty()) {
					showEmptyView(pvMessage);
				} else {
					pvMessageAdapter.setData(messageList);
					pvMessageAdapter.notifyDataSetChanged();
				}
			}
		}

	}
}
*/