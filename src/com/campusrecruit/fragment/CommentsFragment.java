package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.campusrecruit.activity.CareerTalkDetailActivity;
import com.campusrecruit.activity.RecruitDetailActivity;
import com.campusrecruit.activity.TopicDetailActivity;
import com.campusrecruit.adapter.ListViewReplyAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.pcncad.campusRecruit.R;

@SuppressLint("ValidFragment")
public class CommentsFragment extends EmptyFragment {

	// reply list
	private PullToRefreshListView pvReply;
	private ListViewReplyAdapter lvReplyAdapter;

	
	private View pvFooter;
	private TextView pvFooterTextView;
	private ProgressBar pvFooterProgressBar;

	private Handler lvReplyHandler;
	private RecruitDetailActivity recruitDetailActivity;

	
	private int topicID;
	private Recruit recruitDetail;

	private int curLvDataState;
	private int curLvPosition;// 当前listview选中的item位置

	private ScrollView vScrollView;

	public static CommentsFragment newInstance(int topicID, Recruit recruit) {
		CommentsFragment fragment = new CommentsFragment();
		Bundle args = new Bundle();
		args.putInt("topicID", topicID);
		args.putSerializable("recruit", recruit);
		fragment.setArguments(args);
		return fragment;
	}

	private void getArgs() {
		this.topicID = getArguments().getInt("topicID", 0);
		this.recruitDetail = (Recruit) getArguments()
				.getSerializable("recruit");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("test", "comments oncreate");
		if (appContext == null)
			appContext = (AppContext) getActivity().getApplication();
		recruitDetailActivity = (RecruitDetailActivity) getActivity();
		lvReplyAdapter = new ListViewReplyAdapter(getActivity(), appContext,
				recruitDetailActivity.lvReplyData, R.layout.comment_listitem);
		initCommentsHandler();
		getArgs();
		loadLvReplyData(0, UIHelper.LISTVIEW_ACTION_INIT);
	}

	/*
	 * public CommentsFragment (int topicID, Recruit recruit, List<BBSReply>
	 * recruitDetailActivity.lvReplyData) { this.topicID = topicID;
	 * this.recruitDetailActivity.lvReplyData =
	 * recruitDetailActivity.lvReplyData; this.recruitDetail = recruit; }
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("bug", "create comment View");
		super.onCreateView(inflater, container, savedInstanceState);
		View commentsView = inflater.inflate(R.layout.comment_list, null);
		pvReply = (PullToRefreshListView) commentsView
				.findViewById(R.id.comment_list_listview);
		pvFooter = inflater.inflate(R.layout.listview_footer, null);
		pvFooterTextView = (TextView) pvFooter
				.findViewById(R.id.listview_foot_more);
		pvFooterProgressBar = (ProgressBar) pvFooter
				.findViewById(R.id.listview_foot_progress);
		pvReply.addFooterView(pvFooter);
		initLoadingView(commentsView);
		setEmptyText(R.string.comment_list_empty);
		initReplyView();
		redrawView();
		return commentsView;
	}

	public void redrawView() {
		pvFooterProgressBar.setVisibility(View.GONE);
		if (recruitDetailActivity.lvReplyData.size() < AppConfig.PAGE_SIZE) {
			pvFooterTextView.setText(R.string.load_full);
		} else {
			pvFooterTextView.setText(R.string.load_more);
		}
		if (isloading) {
			showLoadProgress(pvReply);
		} else {
			if (loadError) {
				hideLoadProgressWithError(pvReply);
			} else {
				Log.i("bug",
						"not loading"
								+ recruitDetailActivity.lvReplyData.size());
				if (recruitDetailActivity.lvReplyData.isEmpty()) {
					showEmptyView(pvReply);
				} else {
					hideLoadProgress(pvReply);
					if (recruitDetailActivity.lvReplyData.size() < AppConfig.PAGE_SIZE) {
						curLvDataState = UIHelper.LISTVIEW_DATA_FULL;
						lvReplyAdapter.notifyDataSetChanged();
						vFooterTextView.setText(R.string.load_full);
					} else if (recruitDetailActivity.lvReplyData.size() >= AppConfig.PAGE_SIZE) {
						curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
						lvReplyAdapter.notifyDataSetChanged();
						vFooterTextView.setText(R.string.load_more);
					}
				}
			}
		}
	}

	// 初始化视图控件
	private void initReplyView() {
		pvReply.setAdapter(lvReplyAdapter);
		pvReply.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!appContext.isLogin()) {
					UIHelper.showLoginDialog(getActivity());
					return;
				}
				if (position == 0 || view == pvFooter)
					return;
				BBSReply bbsReply = null;
				// 判断是否是TextView
				if (view instanceof TextView) {
					bbsReply = (BBSReply) view.getTag();
				} else {
					ImageView img = (ImageView) view
							.findViewById(R.id.comment_listitem_userface);
					bbsReply = (BBSReply) img.getTag();
				}
				if (bbsReply == null)
					return;
				if (bbsReply.getUserID().equals(appContext.getLoginUid())) {
					UIHelper.ToastMessage(getActivity(), "不能回复自己");
					return;
				}

				// 跳转--回复评论界面
				UIHelper.showCommentPub(getActivity(), topicID,
						bbsReply.getReplyID(), bbsReply.getUserName(),
						bbsReply.getContent());
			}
		});
		pvReply.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				PullToRefreshListView mLvReply2 = pvReply;
				mLvReply2.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (recruitDetailActivity.lvReplyData.size() == 0)
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(pvFooter) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				if (scrollEnd && curLvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					mLvReply2.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					vFooterTextView.setText(R.string.load_ing);
					vFooterProgressBar.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = recruitDetailActivity.lvReplyData.size() / AppConfig.PAGE_SIZE;
					loadLvReplyData(pageIndex, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				pvReply.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
		pvReply.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvReplyData(0, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});

	}

	// 初始化评论数据
	private void initCommentsHandler() {
		if (lvReplyHandler == null) {
			lvReplyHandler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what > 0) {
						hideLoadProgress(pvReply);
						List<BBSReply> list = (List<BBSReply>) msg.obj;
						if (appContext.commentsLoadFromDisk) {
							appContext.commentsLoadFromDisk = false;
							UIHelper.ToastMessage(getActivity(),
									getString(R.string.load_fail));
						}
						// 处理listview数据
						switch (msg.arg1) {
						case UIHelper.LISTVIEW_ACTION_INIT:
						case UIHelper.LISTVIEW_ACTION_REFRESH:
							recruitDetailActivity.lvReplyData.clear();// 先清除原有数据
							recruitDetailActivity.lvReplyData.addAll(list);
							break;
						case UIHelper.LISTVIEW_ACTION_SCROLL:
							if (recruitDetailActivity.lvReplyData.size() > 0) {
								for (BBSReply reply : list) {
									boolean b = false;
									for (BBSReply existReply : recruitDetailActivity.lvReplyData) {
										if (existReply.getReplyID() == reply
												.getReplyID()) {
											b = true;
											break;
										}
									}
									if (!b)
										recruitDetailActivity.lvReplyData
												.add(reply);
								}
							} else {
								recruitDetailActivity.lvReplyData.addAll(list);
							}
							break;
						}
						if (msg.what < AppConfig.PAGE_SIZE) {
							curLvDataState = UIHelper.LISTVIEW_DATA_FULL;
							pvFooterTextView.setText(R.string.load_full);
							lvReplyAdapter.notifyDataSetChanged();
						} else if (msg.what == AppConfig.PAGE_SIZE) {
							curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
							pvFooterTextView.setText(R.string.load_more);
							lvReplyAdapter.notifyDataSetChanged();
						}
					} else if (msg.what == -1) {
						// 有异常--显示加载出错 & 弹出错误消息
						curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
						hideLoadProgressWithError(pvReply);
						((AppException) msg.obj).makeToast(getActivity());
					}
					if (recruitDetailActivity.lvReplyData.size() == 0) {
						Log.i("bug", "test size is 0");
						curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
						showEmptyView(pvReply);
					}
					pvFooterProgressBar.setVisibility(View.GONE);
					if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
						pvReply.onRefreshComplete(getString(R.string.pull_to_refresh_update)
								+ new Date().toLocaleString());
						pvReply.setSelection(0);
					}
				}
			};
		}
	}

	public void setAppContext(AppContext context) {
		if (context != null)
			this.appContext = context;
		Log.i("bug", "set app context");
	}

	/**
	 * 线程加载评论数据
	 * 
	 * @param topicID
	 *            当前话题ID
	 * @param pageIndex
	 *            当前页数
	 * @param handler
	 *            处理器
	 * @param action
	 *            动作标识
	 */
	public void loadLvReplyData(final int pageIndex, final int action) {
		if (isloading)
			return;
		Log.i("bug", "load reply data");
		if (topicID == 0)
			getArgs();
		initCommentsHandler();
		if (action == UIHelper.LISTVIEW_ACTION_SCROLL) {
			pvFooter.setVisibility(View.VISIBLE);
			pvFooterProgressBar.setVisibility(View.VISIBLE);
			pvFooterTextView.setText(R.string.load_ing);
		} else {
			showLoadProgress(pvReply);
		}
		if (appContext == null) {
			appContext = (AppContext) getActivity().getApplication();
			Log.i("bug", "comments app context is null");
		}
		Log.i("test", "load reply data");
		new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					List<BBSReply> replylist = appContext.getBBSReplyList(
							topicID, pageIndex, isRefresh);
					msg.what = replylist.size();
					msg.obj = replylist;
					Log.i("bug", "reply list size is" + msg.what);
				} catch (AppException e) {
					
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;// 告知handler当前action
				lvReplyHandler.sendMessage(msg);
			}
		}.start();
	}

}
