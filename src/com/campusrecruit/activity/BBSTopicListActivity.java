package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.campusrecruit.adapter.ListViewTopicsAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSSection;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.pcncad.campusRecruit.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BBSTopicListActivity extends BaseActivity {
	// private ProgressBar mHeadProgress;

	private PullToRefreshListView pvTopics;
	private ListViewTopicsAdapter lvTopicsAdapter;
	private Handler lvTopicsHandler;
	private List<BBSTopic> lvTopicsData = new ArrayList<BBSTopic>();
	private int lvTopicsSumData;
	private View vFooter;
	private TextView vFooterTextView;
	private ProgressBar vFooterProgressBar;

	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;

	private BBSSection bbsSection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("ac", "initTopicList");
		setContentView(R.layout.topic_list);
		getActionBar().setHomeButtonEnabled(true);
		bbsSection = (BBSSection) getIntent().getSerializableExtra("section");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(bbsSection.getSectionName());
		initTopicsListView();
		initTopicsListData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bbs_topic_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_bbs_add:
			if (!appContext.isLogin()) {
				UIHelper.showLoginDialog(BBSTopicListActivity.this);
				return true;
			}
			UIHelper.showTopicPub(BBSTopicListActivity.this,
					bbsSection.getSectionID(), bbsSection.getCompanyID());
			return true;
		case R.id.menu_refresh_id:
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("ac", "new topic");
		if (resultCode != RESULT_OK)
			return;
		if (data == null)
			return;
		Log.i("ac", "new topic");
		final BBSTopic topic = (BBSTopic) data.getSerializableExtra("topic");
		lvTopicsData.add(0, topic);
		lvTopicsAdapter.notifyDataSetChanged();
		pvTopics.setSelection(0);
		appContext.commentPubAfter(topic);
		new Thread() {
			@Override
			public void run() {
				appContext.saveUserTopic(topic.getTopicID(),
						topic.getTitle(), topic.getCreatedTime());
			}

		}.start();
	}
	
	
	// 初始化控件数据
	private void initTopicsListData() {
		Log.i("test", "initTopicsListData");
		// 加载TopicList
		lvTopicsHandler = new Handler() {
			public void handleMessage(Message msg) {
				Log.i("test", "handleData");
				if (msg.what >= 0) {
					List<BBSTopic> list = (ArrayList<BBSTopic>) msg.obj;
					if (!list.isEmpty() && appContext.topicsLoadFromDisk) {
						UIHelper.ToastMessage(BBSTopicListActivity.this,
								getString(R.string.load_fail));
					}
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
						lvTopicsSumData = msg.what;
						lvTopicsData.clear();// 先清除原有数据
						lvTopicsData.addAll(list);
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						lvTopicsSumData += msg.what;
						if (lvTopicsData.size() > 0) {
							for (BBSTopic ntopic : list) {
								boolean b = false;
								for (BBSTopic otopic : lvTopicsData) {
									if (ntopic.getTopicID() == otopic
											.getTopicID()) {
										b = true;
										break;
									}
								}
								if (!b)
									lvTopicsData.add(ntopic);
							}
						} else {
							lvTopicsData.addAll(list);
						}
						break;
					}
					lvTopicsAdapter.setData(lvTopicsData);
					if (msg.what < 20) {
						lvTopicsAdapter.notifyDataSetChanged();
						vFooterTextView.setText(R.string.load_full);
					} else if (msg.what == 20) {
						lvTopicsAdapter.notifyDataSetChanged();
						vFooterTextView.setText(R.string.load_more);
					}
				} else if (msg.what == -1) {
					// 有异常--也显示更多 & 弹出错误消息
					vFooterTextView.setText(R.string.load_more);
					((AppException) msg.obj)
							.makeToast(BBSTopicListActivity.this);
				}
				if (lvTopicsData.size() == 0) {
					vFooterTextView.setText(R.string.load_empty);
				}
				vFooterProgressBar.setVisibility(View.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH)
					pvTopics.onRefreshComplete(getString(R.string.pull_to_refresh_update)
							+ new Date().toLocaleString());
			}
		};
		this.loadLvTopicsData(0, lvTopicsHandler, UIHelper.LISTVIEW_ACTION_INIT);
	}

	/**
	 * 初始化帖子列表
	 */
	private void initTopicsListView() {
		Log.i("test", "initView");
		lvTopicsAdapter = new ListViewTopicsAdapter(this,
				R.layout.topic_listitem);
		vFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
		vFooterTextView = (TextView) vFooter
				.findViewById(R.id.listview_foot_more);
		vFooterProgressBar = (ProgressBar) vFooter
				.findViewById(R.id.listview_foot_progress);
		pvTopics = (PullToRefreshListView) findViewById(R.id.topic_listview);
		pvTopics.addFooterView(vFooter);// 添加底部视图 必须在setAdapter前
		pvTopics.setAdapter(lvTopicsAdapter);
		pvTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!appContext.isLogin()) {
					UIHelper.showLoginDialog(BBSTopicListActivity.this);
					return;
				}
				// 点击头部、底部栏无效
				if (position == 0 || view == vFooter)
					return;

				BBSTopic topic = null;
				TextView tv = (TextView) view
						.findViewById(R.id.topics_listitem_title);
				topic = (BBSTopic) tv.getTag();
				if (topic == null)
					return;

				// 跳转到主題詳情
				UIHelper.showTopicDetail(view.getContext(), topic.getTopicID());
			}
		});
		pvTopics.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				pvTopics.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvTopicsData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(vFooter) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(pvTopics.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					pvTopics.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					vFooterTextView.setText(R.string.load_ing);
					vFooterProgressBar.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = lvTopicsSumData / AppConfig.PAGE_SIZE;
					loadLvTopicsData(pageIndex, lvTopicsHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				pvTopics.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
		pvTopics.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvTopicsData(0, lvTopicsHandler,
						UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
	}

	/**
	 * 线程加载数据
	 * 
	 * @param pageIndex
	 *            当前页数
	 * @param handler
	 *            处理器
	 * @param action
	 *            动作标识
	 */
	private void loadLvTopicsData(final int pageIndex, final Handler handler,
			final int action) {
		Log.i("test", "loadTopicsData");
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					List<BBSTopic> list;
					if (bbsSection.getSectionID() != 0) {
						list = appContext.getBBSTopicList(
								bbsSection.getSectionID(), pageIndex,
								action == UIHelper.LISTVIEW_ACTION_REFRESH);
					} else {
						list = appContext.getBBSTopicListByCompanyID(
								bbsSection.getCompanyID(), pageIndex);
					}
					msg.what = 1;
					msg.obj = list;
				} catch (AppException e) {
					
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				handler.sendMessage(msg);
			}
		}.start();
	}

}
