package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.campusrecruit.adapter.GridViewFaceAdapter;
import com.campusrecruit.adapter.ListViewPrivateMessageListAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.Notice;
import com.campusrecruit.bean.UserMessage;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.krislq.sliding.R;

public class PrivateMessageListActivity extends EmptyActivity {

	private String userId = null;
	private String userName = null;
	private String lastCreatedTime = null;
	private boolean periodicFlag = true;
	private boolean isInit = true;

	private AppContext appContext = null;
	private PullToRefreshListView pvMessage;
	private List<UserMessage> messageList = new ArrayList<UserMessage>();
	private ListViewPrivateMessageListAdapter pvMessageAdapter = null;

	// foot
	private ViewSwitcher mFootViewSwitcher;
	private ImageView mFootEditebox;
	private EditText mFootEditer;
	private Button mFootPubcomment;
	private ProgressDialog mProgress;
	private InputMethodManager imm;
	private String tempCommentKey = AppConfig.TEMP_COMMENT;
	private String _content;

	private ImageView mFace;
	private GridView mGridView;
	private GridViewFaceAdapter mGVFaceAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_message_list);
		initLoadingView();
		setEmptyText(R.string.private_message_list_empty);

		appContext = (AppContext) getApplication();
		Intent intent = getIntent();
		userId = intent.getStringExtra("userID");
		userName = intent.getStringExtra("userName");
		lastCreatedTime = intent.getStringExtra("lastCreatedTime");

		initView();
		initFooter();
		// 初始化表情视图
		this.initGridView();
		if (messageList.isEmpty()) {
			new PrivateMessageAsyncTask().execute();
		} else {
			pvMessageAdapter.setData(messageList);
			pvMessageAdapter.notifyDataSetChanged();
		}
		getMessagePeriodically();

	}

	@Override
	protected void onStop() {
		super.onStop();
		periodicFlag = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		periodicFlag = true;
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

	private void initView() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("私信内容");
		pvMessage = (PullToRefreshListView) findViewById(R.id.user_meesage_list_listview);
		Log.i("private message", "adapter begin");
		pvMessageAdapter = new ListViewPrivateMessageListAdapter(this,
				appContext, R.layout.private_message_list_item);
		pvMessage.setAdapter(pvMessageAdapter);
		pvMessage.setOnRefreshListener(new refreshListener());
	}

	private void initFooter() {
		mFace = (ImageView) findViewById(R.id.topic_detail_foot_face);
		mFace.setOnClickListener(facesClickListener);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		Log.i("td", "initFoot");
		// foot
		mFootViewSwitcher = (ViewSwitcher) findViewById(R.id.topic_detail_foot_viewswitcher);
		mFootPubcomment = (Button) findViewById(R.id.topic_detail_foot_pubcomment);
		mFootPubcomment.setOnClickListener(commentpubClickListener);
		mFootEditebox = (ImageView) findViewById(R.id.topic_detail_footbar_editebox);
		mFootEditebox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFootViewSwitcher.showNext();
				mFootEditer.setVisibility(View.VISIBLE);
				mFootEditer.requestFocus();
				mFootEditer.requestFocusFromTouch();
				imm.showSoftInput(mFootEditer, 0);// 显示软键盘
			}
		});
		mFootEditer = (EditText) findViewById(R.id.topic_detail_foot_editer);
		mFootEditer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 显示软键盘&隐藏表情
				showIMM();
			}
		});
		mFootEditer.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mFootViewSwitcher.getDisplayedChild() == 1) {
						mFootViewSwitcher.setDisplayedChild(0);
						mFootEditer.clearFocus();// 隐藏软键盘
						mFootEditer.setVisibility(View.GONE);// 隐藏编辑框
						hideFace();// 隐藏表情
					}
					return true;
				}
				return false;
			}
		});
		// 编辑器添加文本监听
		mFootEditer.addTextChangedListener(UIHelper.getTextWatcher(this,
				tempCommentKey));

		// 显示临时编辑内容
		UIHelper.showTempEditContent(this, mFootEditer, tempCommentKey);
	}

	private class refreshListener implements
			PullToRefreshListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			new PrivateMessageAsyncTask().execute();
		}

	}

	// 初始化表情控件
	private void initGridView() {
		mGVFaceAdapter = new GridViewFaceAdapter(this);
		mGridView = (GridView) findViewById(R.id.topic_detail_foot_faces);
		mGridView.setAdapter(mGVFaceAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 插入的表情
				SpannableString ss = new SpannableString(view.getTag()
						.toString());
				Drawable d = getResources().getDrawable(
						(int) mGVFaceAdapter.getItemId(position));
				d.setBounds(0, 0, 35, 35);// 设置表情图片的显示大小
				ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
				ss.setSpan(span, 0, view.getTag().toString().length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// 在光标所在处插入表情
				mFootEditer.getText().insert(mFootEditer.getSelectionStart(),
						ss);
			}
		});
	}

	private void showIMM() {
		mFace.setTag(1);
		showOrHideIMM();
	}

	private void showFace() {
		mFace.setImageResource(R.drawable.widget_bar_keyboard);
		mFace.setTag(1);
		mGridView.setVisibility(View.VISIBLE);
	}

	private void hideFace() {
		mFace.setImageResource(R.drawable.widget_bar_face);
		mFace.setTag(null);
		mGridView.setVisibility(View.GONE);
	}

	private void showOrHideIMM() {
		if (mFace.getTag() == null) {
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(mFootEditer.getWindowToken(), 0);
			// 显示表情
			showFace();
		} else {
			// 显示软键盘
			imm.showSoftInput(mFootEditer, 0);
			// 隐藏表情
			hideFace();
		}
	}

	// 表情控件点击事件
	private View.OnClickListener facesClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			showOrHideIMM();
		}
	};

	private View.OnClickListener commentpubClickListener = new View.OnClickListener() {
		public void onClick(View v) {

			if (!appContext.isLogin()) {
				UIHelper.showLoginDialog(PrivateMessageListActivity.this);
				return;
			}

			_content = mFootEditer.getText().toString();
			if (StringUtils.isEmpty(_content)) {
				UIHelper.ToastMessage(v.getContext(), "请输入评论内容");
				return;
			}
			Log.i("td", _content);

			final AppContext ac = (AppContext) getApplication();

			mProgress = ProgressDialog.show(v.getContext(), null, "发布中···",
					true, true);
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {

					if (mProgress != null)
						mProgress.dismiss();

					if (msg.what == 1 && msg.obj != null) {
						UserMessage message = (UserMessage) msg.obj;
						if (lastCreatedTime.compareTo(message.getCreatedTime()) < 0) {
							lastCreatedTime = message.getCreatedTime();
						}
						UIHelper.ToastMessageCommentSucess(PrivateMessageListActivity.this);
						// 恢复初始底部栏
						mFootViewSwitcher.setDisplayedChild(0);
						mFootEditer.clearFocus();
						mFootEditer.setText("");
						mFootEditer.setVisibility(View.GONE);
						// 隐藏软键盘
						imm.hideSoftInputFromWindow(
								mFootEditer.getWindowToken(), 0);
						// 隐藏表情
						hideFace();
						if (messageList.isEmpty()) {
							hideLoadProgress(pvMessage);
						}
						// 更新评论列表
						messageList.add(message);
						pvMessage.setSelection(messageList.size() - 1);
						pvMessageAdapter.setData(messageList);
						pvMessageAdapter.notifyDataSetChanged();
						// 清除之前保存的编辑内容
						ac.removeProperty(tempCommentKey);

						periodicFlag = true;
						getMessagePeriodically();
						// save userTopic to disk
						// saveUserTopic();
					} else {
						((AppException) msg.obj)
								.makeToast(PrivateMessageListActivity.this);
					}
				}
			};
			// 开始插入数据时，停止轮询
			periodicFlag = false;
			new Thread() {
				public void run() {
					Message msg = new Message();
					UserMessage message = null;
					try {
						Log.i("td", "addReply");
						// 发表评论
						message = ac.addMessage(userId, userName, _content);
						msg.what = 1;
						msg.obj = message;
					} catch (AppException e) {
						e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
					}
					handler.sendMessage(msg);
				}
			}.start();
		}
	};

	private void getMessagePeriodically() {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what >= 1) {
					handleData((List<UserMessage>) msg.obj);
					pvMessageAdapter.setData(messageList);
					pvMessageAdapter.notifyDataSetChanged();
				}
				if (periodicFlag) {
					getMessagePeriodically();
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					sleep(10 * 1000);
					List<UserMessage> list = appContext.getPrivateMessageList(
							userId, lastCreatedTime);
					msg.what = list.size();
					msg.obj = list;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	private class PrivateMessageAsyncTask extends
			AsyncTask<Void, Void, List<UserMessage>> {
		@Override
		protected void onPreExecute() {
			showLoadProgress(pvMessage);

		}

		// createdtime
		@Override
		protected List<UserMessage> doInBackground(Void... voids) {
			List<UserMessage> list;
			try {
				// list = appContext.getMessageList();
				Log.i("private message", "begin ays");
				if (isInit) {
					list = appContext.getPrivateMessageList(userId, null);
				} else {
					list = appContext.getPrivateMessageList(userId,
							lastCreatedTime);
				}
				return list;
			} catch (AppException e) {
				e.makeToast(PrivateMessageListActivity.this);
				hideLoadProgressWithError(pvMessage);
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<UserMessage> list) {
			if (isInit && !list.isEmpty()) {
				// not new
				for (UserMessage message : list)
					message.setStatus(0);
			}
			isInit = false;
			pvMessage.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
			hideLoadProgress(pvMessage);
			if (appContext.privateMessageLoadFromDisk) {
				appContext.privateMessageLoadFromDisk = false;
				UIHelper.ToastMessage(PrivateMessageListActivity.this,
						getString(R.string.load_fail));
			}
			handleData(list);
			if (messageList.isEmpty()) {
				showEmptyView(pvMessage);
			} else {
				hideLoadProgress(pvMessage);
				pvMessageAdapter.setData(messageList);
				pvMessageAdapter.notifyDataSetChanged();
			}
			Log.i("test", "complete");

		}
	}

	private void handleData(List<UserMessage> list) {
		for (UserMessage message : messageList)
			message.setStatus(0);
		if (list.isEmpty())
			return;
		messageList.addAll(list);
		// 检查是否是新来的数据
		if (lastCreatedTime != null) {
			for (UserMessage message : messageList) {
				if (message.getCreatedTime().compareTo(lastCreatedTime) > 0) {
					message.setStatus(1);

				}
			}
		}
		// 最后的时间最大
		lastCreatedTime = list.get(list.size() - 1).getCreatedTime();
		if (appContext != null) {
			UserMessage message = list.get(list.size() - 1);
			boolean flag = false;
			if (!message.getUserID().equals(userId)) {
				message.setUserID(userId);
				message.setUserName(userName);
				flag = true;
			}
			appContext.saveMessage(message);
			if (flag) {
				message.setUserID(appContext.getLoginUid());
			}
		}
	}

}
