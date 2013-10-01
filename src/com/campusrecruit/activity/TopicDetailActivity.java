package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.campusrecruit.adapter.GridViewFaceAdapter;
import com.campusrecruit.adapter.ListViewReplyAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.Result;
import com.campusrecruit.common.BitmapManager;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.krislq.sliding.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * 动弹详情
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class TopicDetailActivity extends LoadingActivity {

	private LinearLayout mLinearlayout;

	private Handler topicDetailHandler;

	private PullToRefreshListView mLvComment;
	private ListViewReplyAdapter lvCommentAdapter;
	private List<BBSReply> lvCommentData = new ArrayList<BBSReply>();
	private View pvCommentFooter;
	private TextView pvCommentFooterText;
	private ProgressBar pvCommentFooterProgress;
	private Handler commentHandler;
	private int lvSumData;
	private boolean refreshing = false;

	// head
	private View lvHeader;
	private ImageView userface;
	private TextView username;
	private TextView date;
	private TextView commentCount;
	TextView content;
	// private ImageView image;

	// private ImageView image;
	private int curLvDataState;

	// foot
	private ViewSwitcher mFootViewSwitcher;
	private ImageView mFootEditebox;
	private EditText mFootEditer;
	private Button mFootPubcomment;
	private ProgressDialog mProgress;
	private InputMethodManager imm;
	private String tempCommentKey = AppConfig.TEMP_COMMENT;

	private ImageView mFace;
	private GridView mGridView;
	private GridViewFaceAdapter mGVFaceAdapter;

	private int topicID;
	private BBSTopic topicDetail;
	private String _content;

	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;

	private AppContext appContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("td", "begin");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		appContext = (AppContext) getApplication();
		topicID = getIntent().getIntExtra("topicID", 0);
		Log.i("test", "topicID" + topicID);
		// 初始化视图控件
		this.initView();
		this.initTopicData();
		// 初始化控件数据
		this.initData();
		// 初始化表情视图
		this.initGridView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_refresh, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.menu_refresh_id:
			Log.i("refresh", "touched");
			if (!refreshing) {
				refreshing = true;
				this.initTopicData();
				this.initData();
			}
			refreshing = false;
			return true;
		}
		return false;
	}

	private void fillView() {
		username.setText(topicDetail.getUserName());
		BitmapManager bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				this.getResources(), R.drawable.user_face));
		//TODO
		userface.setOnClickListener(faceClickListener);
		content.setText(topicDetail.getBody());
		date.setText(topicDetail.getCreatedTime());
		commentCount.setText(topicDetail.getReplies() + "");
		getActionBar().setTitle(topicDetail.getTitle());
	}

	private void initTopicData() {
		topicDetailHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				hideLoadProgress(content);
				if (msg.what == 1) {
					topicDetail = (BBSTopic) msg.obj;
					// 加载主帖数据
					fillView();
				} else {
					((AppException) msg.obj)
							.makeToast(TopicDetailActivity.this);
					hideLoadProgressWithError(content);
				}
			}
		};
		showLoadProgress(content);
		new Thread() {
			@Override
			public void run() {
				Message msg = new Message();
				try {
					BBSTopic topic = ((AppContext) getApplication())
							.getTopicDetail(topicID);
					msg.what = 1;
					msg.obj = topic;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				topicDetailHandler.sendMessage(msg);
			}
		}.start();
	}

	// 初始化视图控件
	private void initView() {
		Log.i("td", "init view");
		// Head
		mLinearlayout = (LinearLayout) findViewById(R.id.topic_detail_linearlayout);

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
		Log.i("td", "initTopic");

		// Topic Head
		lvHeader = View.inflate(this, R.layout.topic_detail_content, null);
		initLoadingView(lvHeader);
		userface = (ImageView) lvHeader
				.findViewById(R.id.topic_listitem_userface);
		username = (TextView) lvHeader
				.findViewById(R.id.topic_listitem_username);
		date = (TextView) lvHeader.findViewById(R.id.topic_listitem_date);
		commentCount = (TextView) lvHeader
				.findViewById(R.id.topic_listitem_commentCount);
		// image = (ImageView)lvHeader.findViewById(R.id.topic_listitem_image);

		content = (TextView) lvHeader.findViewById(R.id.topic_listitem_content);

		// content =
		// (WebView)lvHeader.findViewById(R.id.topic_listitem_content);
		// content.getSettings().setJavaScriptEnabled(false);
		// content.getSettings().setSupportZoom(true);
		// content.getSettings().setBuiltInZoomControls(true);
		// content.getSettings().setDefaultFontSize(12);

		Log.i("td", "initComment");
		pvCommentFooter = getLayoutInflater().inflate(R.layout.listview_footer,
				null);
		pvCommentFooterText = (TextView) pvCommentFooter
				.findViewById(R.id.listview_foot_more);
		pvCommentFooterProgress = (ProgressBar) pvCommentFooter
				.findViewById(R.id.listview_foot_progress);

		lvCommentAdapter = new ListViewReplyAdapter(this, appContext,
				lvCommentData, R.layout.comment_listitem);
		mLvComment = (PullToRefreshListView) findViewById(R.id.topic_detail_commentlist);

		mLvComment.addHeaderView(lvHeader);// 把动弹详情放进listview头部
		mLvComment.addFooterView(pvCommentFooter);// 添加底部视图 必须在setAdapter前
		mLvComment.setAdapter(lvCommentAdapter);
		mLvComment
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.i("bug","click");
						if (!appContext.isLogin()) {
							UIHelper.showLoginDialog(TopicDetailActivity.this);
							return;
						}

						// 点击头部、底部栏无效
						if (position == 0 || view == pvCommentFooter
								|| position == 1 || view == lvHeader)
							return;
						Log.i("reply", "click");
						BBSReply reply = null;

						// 判断是否是TextView
						if (view instanceof TextView) {
							reply = (BBSReply) view.getTag();
						} else {
							ImageView img = (ImageView) view
									.findViewById(R.id.comment_listitem_userface);
							reply = (BBSReply) img.getTag();
						}
						if (reply == null) {
							Log.i("test", "ooops");
						}
						// 系统用户内容不能回复
						if (reply.getUserID().equals("0")) {
							UIHelper.ToastMessage(TopicDetailActivity.this,
									"不能回复系统");
							return;
						}
						if (reply.getUserID().equals(appContext.getLoginUid())) {
							UIHelper.ToastMessage(TopicDetailActivity.this,
									"不能回复自己");
							return;
						}
						// 跳转--回复评论界面
						/*
						 * Log.i("td",
						 * String.format("topicID %d, %d, %s, %s, %s",
						 * topicDetail.getTopicID(), reply.getReplyID(),
						 * reply.getUserID(), reply.getUserName(),
						 * reply.getContent()));
						 */
						Log.i("test", "show comment pub");
						UIHelper.showCommentPub(TopicDetailActivity.this,
								topicDetail.getTopicID(), reply.getReplyID(),
								reply.getUserName(), reply.getContent());
					}
				});
		mLvComment.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mLvComment.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvCommentData.size() == 0)
					return;
				Log.i("activity", "scroll begin");
				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(pvCommentFooter) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				Log.i("activity", "scroll middle");
				if (scrollEnd && curLvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					mLvComment.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					pvCommentFooterText.setText(R.string.load_ing);
					pvCommentFooterProgress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = lvSumData / 20;
					loadLvCommentData(pageIndex, commentHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL);
				}
				Log.i("activity", "scroll end");
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mLvComment.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
		mLvComment
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// 点击头部、底部栏无效
						if (position == 0 || view == pvCommentFooter
								|| position == 1 || view == lvHeader)
							return false;

						BBSReply _reply = null;
						// 判断是否是TextView
						if (view instanceof TextView) {
							_reply = (BBSReply) view.getTag();
						} else {
							ImageView img = (ImageView) view
									.findViewById(R.id.comment_listitem_userface);
							_reply = (BBSReply) img.getTag();
						}
						if (_reply == null)
							return false;

						final BBSReply reply = _reply;

						final AppContext ac = (AppContext) getApplication();
						// 操作--回复 & 删除
						String uid = ac.getLoginUid();
						// 判断该评论是否是当前登录用户发表的：true--有删除操作 false--没有删除操作
						if (uid == reply.getUserID()) {
							final Handler handler = new Handler() {
								public void handleMessage(Message msg) {
									if (msg.what == 1) {
										Result res = (Result) msg.obj;
										if (res.OK()) {
											lvSumData--;
											lvCommentData.remove(reply);
											lvCommentAdapter
													.notifyDataSetChanged();
										}
										UIHelper.ToastMessage(
												TopicDetailActivity.this,
												res.getErrorMessage());
									} else {
										((AppException) msg.obj)
												.makeToast(TopicDetailActivity.this);
									}
								}
							};
							final Thread thread = new Thread() {
								public void run() {
									Message msg = new Message();
									try {
										Result res = ac.delReply(reply
												.getReplyID());
										msg.what = 1;
										msg.obj = res;
									} catch (AppException e) {
										e.printStackTrace();
										msg.what = -1;
										msg.obj = e;
									}
									handler.sendMessage(msg);
								}
							};
							// UIHelper.showCommentOptionDialog(TweetActivity.this,
							// curId, curCatalog, com, thread);
						} else {
							/*
							 * UIHelper.showCommentOptionDialog(TweetActivity.this
							 * , curId, curCatalog, com, null);
							 */
						}
						return true;
					}
				});
		mLvComment
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {
						loadLvCommentData(0, commentHandler,
								UIHelper.LISTVIEW_ACTION_REFRESH);
					}
				});
		Log.i("td", "init td complete");

	}

	// 初始化控件数据
	private void initData() {
		Log.i("td", "begin load comment");
		// 加载评论
		commentHandler = new Handler() {
			public void handleMessage(Message msg) {
				Log.i("td", "comment handler");
				if (msg.what >= 0) {
					List<BBSReply> list = (ArrayList<BBSReply>) msg.obj;
					if (msg.what > 0 && appContext.commentsLoadFromDisk) {
						appContext.commentsLoadFromDisk = false;
						UIHelper.ToastMessage(TopicDetailActivity.this,
								getString(R.string.load_fail));
					}
					Log.i("td",
							String.format("bbsReply list len is %d",
									list.size()));
					// 处理listview数据
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
						lvSumData = msg.what;
						lvCommentData.clear();// 先清除原有数据
						lvCommentData.addAll(list);
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						lvSumData += msg.what;
						if (lvCommentData.size() > 0) {
							for (BBSReply com1 : list) {
								boolean b = false;
								for (BBSReply com2 : lvCommentData) {
									if (com1.getReplyID() == com2.getReplyID()) {
										b = true;
										break;
									}
								}
								if (!b)
									lvCommentData.add(com1);
							}
						} else {
							lvCommentData.addAll(list);
						}
						break;
					}

					if (msg.what < 20) {
						curLvDataState = UIHelper.LISTVIEW_DATA_FULL;
						lvCommentAdapter.notifyDataSetChanged();
						pvCommentFooterText.setText(R.string.load_full);
					} else if (msg.what == 20) {
						curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
						lvCommentAdapter.notifyDataSetChanged();
						pvCommentFooterText.setText(R.string.load_more);
					}
				} else if (msg.what == -1) {
					// 有异常--也显示更多 & 弹出错误消息
					curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
					if (!appContext.commentsLoadFromDisk) {
						pvCommentFooterText.setText(R.string.load_more);
						((AppException) msg.obj)
								.makeToast(TopicDetailActivity.this);
					}
				}
				if (lvCommentData.size() == 0) {
					curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
					pvCommentFooterText.setText(R.string.load_empty);
				}
				pvCommentFooterProgress.setVisibility(View.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH)
					mLvComment
							.onRefreshComplete(getString(R.string.pull_to_refresh_update)
									+ new Date().toLocaleString());
				Log.i("td", "complete");
			}
		};
		this.loadLvCommentData(0, commentHandler, UIHelper.LISTVIEW_ACTION_INIT);
	}

	/**
	 * 线程加载评论数据
	 * 
	 * @param id
	 *            当前文章id
	 * @param catalog
	 *            分类
	 * @param pageIndex
	 *            当前页数
	 * @param handler
	 *            处理器
	 * @param action
	 *            动作标识
	 */
	private void loadLvCommentData(final int pageIndex, final Handler handler,
			final int action) {
		Log.i("td", "loadLvCommentData");
		pvCommentFooterProgress.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					List<BBSReply> commentlist = ((AppContext) getApplication())
							.getBBSReplyList(topicID, pageIndex, isRefresh);
					msg.what = commentlist.size();
					msg.obj = commentlist;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;// 告知handler当前action
				handler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("td", "onActivityResult");
		if (resultCode != RESULT_OK)
			return;
		if (data == null)
			return;
		if (requestCode == UIHelper.REQUEST_CODE_FOR_RESULT) {
			BBSReply reply = (BBSReply) data
					.getSerializableExtra("COMMENT_SERIALIZABLE");
			reply.setStatus(1);
			lvCommentData.add(reply);
			lvCommentAdapter.notifyDataSetChanged();
			mLvComment.setSelection(lvCommentData.size() - 1);

			// save topic
			saveUserTopic();
		}
	}

	private void saveUserTopic() {
		Log.i("topic", "save user topic");
		// save topic
		BBSTopic topic = new BBSTopic();
		topic.setTopicID(topicID);
		topic.setCreatedTime(topicDetail.getCreatedTime());
		topic.setTitle(topicDetail.getTitle());
		topic.setStatus(1);
		Log.i("topic", "comment pub user topic");
		appContext.commentPubAfter(topic);

		new Thread() {
			@Override
			public void run() {
				appContext.saveUserTopic(topicDetail.getTopicID(),
						topicDetail.getTitle(), topicDetail.getCreatedTime());
			}

		}.start();
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

	private View.OnClickListener faceClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (topicDetail != null) {
				if (topicDetail.getUserID().equals("1")) {
					UIHelper.ToastMessage(TopicDetailActivity.this, "不能查看系统用户", Toast.LENGTH_SHORT);
					return;
				}
				UIHelper.showUserInfo(TopicDetailActivity.this,
						topicDetail.getUserID());
			}
		}
	};

	private View.OnClickListener commentpubClickListener = new View.OnClickListener() {
		public void onClick(View v) {

			if (!appContext.isLogin()) {
				UIHelper.showLoginDialog(TopicDetailActivity.this);
				return;
			}
			Log.i("td", "commentpubClickListener");

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
						BBSReply reply = (BBSReply) msg.obj;
						UIHelper.ToastMessageCommentSucess(TopicDetailActivity.this);
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
						// 更新评论列表
						lvCommentData.add(reply);
						mLvComment.setSelection(lvCommentData.size() - 1);
						lvCommentAdapter.notifyDataSetChanged();
						// 清除之前保存的编辑内容
						ac.removeProperty(tempCommentKey);

						// save userTopic to disk
						saveUserTopic();
					} else {
						((AppException) msg.obj)
								.makeToast(TopicDetailActivity.this);
					}
				}
			};
			new Thread() {
				public void run() {
					Message msg = new Message();
					BBSReply reply = null;
					try {
						Log.i("td", "addReply");
						// 发表评论
						reply = ac.addReply(topicID, _content);
						msg.what = 1;
						msg.obj = reply;
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
}
