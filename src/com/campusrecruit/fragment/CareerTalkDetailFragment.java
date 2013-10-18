/*package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.adapter.ListViewReplyAdapter;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.Result;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.BadgeView;
import com.campusrecruit.widget.PullToRefreshListView;
import com.pcncad.campusRecruit.R;

*//**
 * 新闻详情
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 *//*
public class CareerTalkDetailFragment extends BaseFragment {

	private FrameLayout vHead;
	private LinearLayout vFooter;
	private ImageView vHome;
	private ImageView vJoined;
	private ImageView vRefresh;
	private TextView vHeadTitle;
	private ProgressBar vProgressbar;
	private ScrollView vScrollView;
	private ViewSwitcher vViewSwitcher;

	private BadgeView bv_comment;
	private ImageView vDetail;
	private ImageView vReplyList;
	private ImageView vShare;

	TextView vCompanyName;
	TextView vPlace;
	TextView vTime;
	TextView vSchoolName;

	private Handler careerDetailHandler;
	private Handler recruitReplyHandler;

	private final static int VIEWSWITCH_TYPE_DETAIL = 0x001;
	private final static int VIEWSWITCH_TYPE_COMMENTS = 0x002;

	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	private final static int DATA_LOAD_FAIL = 0x003;

	private PullToRefreshListView pvReply;
	private ListViewReplyAdapter lvReplyAdapter;
	private List<BBSReply> lvReplyData = new ArrayList<BBSReply>();
	private View lvReply_footer;
	private TextView lvReply_foot_more;
	private ProgressBar lvReply_foot_progress;
	private int lvSumData;

	private int curLvDataState;
	private int curLvPosition;// 当前listview选中的item位置

	private ViewSwitcher mFootViewSwitcher;
	private ImageView vFootEditebox;
	private EditText mFootEditer;
	private Button vFootPubcomment;
	private ProgressDialog mProgress;
	private InputMethodManager imm;
	private String tempReplyKey = AppConfig.TEMP_COMMENT;

	private CareerTalk careerTalkDetail;
	private int topicID;
	private boolean flag;
	

	private String _content;

	private GestureDetector gd;
	private boolean isFullScreen;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("cd", "career talk onCreate");
		super.onCreate(savedInstanceState);

		View careerDetailView = inflater.inflate(R.layout.career_detail, null);
		// getActionBar().setDisplayShowHomeEnabled(false);
		careerTalkDetail = (CareerTalk) getActivity().getIntent()
				.getSerializableExtra("careerTalk");
		flag = getActivity().getIntent().getBooleanExtra("flag", true);

		this.initView(careerDetailView);
//		this.initData();

		// 加载评论视图&数据
		this.initReplyView(careerDetailView, inflater);
		this.initReplyData();

		// 注册双击全屏事件
		this.regOnDoubleEvent();

		if (!flag)
			viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
		return careerDetailView;
	}

	// 初始化视图控件
	private void initView(View careerDetailView) {
		Log.i("cd", "career talk init");

		vCompanyName = (TextView) careerDetailView
				.findViewById(R.id.career_company_name);
		vPlace = (TextView) careerDetailView.findViewById(R.id.career_place);
		vTime = (TextView) careerDetailView.findViewById(R.id.career_time);
		vSchoolName = (TextView) careerDetailView
				.findViewById(R.id.career_school);

		vCompanyName.setText(careerTalkDetail.getCompanyName());
		vCompanyName.setText(careerTalkDetail.getPlace());
		vTime.setText(careerTalkDetail.getTime() + "");
		vSchoolName.setText(careerTalkDetail.getSchoolName());
		Log.i("cd", "career talk init ok");

		vHead = (FrameLayout) careerDetailView
				.findViewById(R.id.career_detail_header);
		vFooter = (LinearLayout) careerDetailView
				.findViewById(R.id.detail_footer);
		vHome = (ImageView) careerDetailView
				.findViewById(R.id.career_detail_home);
		vRefresh = (ImageView) careerDetailView
				.findViewById(R.id.career_detail_refresh);
		vHeadTitle = (TextView) careerDetailView
				.findViewById(R.id.career_detail_head_title);
		vProgressbar = (ProgressBar) careerDetailView
				.findViewById(R.id.career_detail_head_progress);
		vViewSwitcher = (ViewSwitcher) careerDetailView
				.findViewById(R.id.career_detail_viewswitcher);
		vScrollView = (ScrollView) careerDetailView
				.findViewById(R.id.career_detail_scrollview);

		vDetail = (ImageView) careerDetailView
				.findViewById(R.id.detail_footbar_detail);
		vReplyList = (ImageView) careerDetailView
				.findViewById(R.id.detail_footbar_commentlist);
		vShare = (ImageView) careerDetailView
				.findViewById(R.id.detail_footbar_share);
		vJoined = (ImageView) careerDetailView
				.findViewById(R.id.detail_footbar_joined);

		vDetail.setEnabled(false);

		vHome.setOnClickListener(homeClickListener);
		vJoined.setOnClickListener(joinedClickListener);
		vRefresh.setOnClickListener(refreshClickListener);
		vShare.setOnClickListener(shareClickListener);
		vDetail.setOnClickListener(detailClickListener);
		vReplyList.setOnClickListener(commentlistClickListener);

		bv_comment = new BadgeView(getActivity(), vReplyList);
		bv_comment.setBackgroundResource(R.drawable.widget_count_bg2);
		bv_comment.setIncludeFontPadding(false);
		bv_comment.setGravity(Gravity.CENTER);
		bv_comment.setTextSize(8f);
		bv_comment.setTextColor(Color.WHITE);

		imm = (InputMethodManager) getActivity().getSystemService(
				getActivity().INPUT_METHOD_SERVICE);

		mFootViewSwitcher = (ViewSwitcher) careerDetailView
				.findViewById(R.id.detail_foot_viewswitcher);
		vFootPubcomment = (Button) careerDetailView
				.findViewById(R.id.detail_foot_pubcomment);
		vFootPubcomment.setOnClickListener(commentpubClickListener);
		vFootEditebox = (ImageView) careerDetailView
				.findViewById(R.id.detail_footbar_editebox);
		vFootEditebox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFootViewSwitcher.showNext();
				mFootEditer.setVisibility(View.VISIBLE);
				mFootEditer.requestFocus();
				mFootEditer.requestFocusFromTouch();
			}
		});
		mFootEditer = (EditText) careerDetailView
				.findViewById(R.id.detail_foot_editer);
		mFootEditer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					imm.showSoftInput(v, 0);
				} else {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		});
		mFootEditer.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mFootViewSwitcher.getDisplayedChild() == 1) {
						mFootViewSwitcher.setDisplayedChild(0);
						mFootEditer.clearFocus();
						mFootEditer.setVisibility(View.GONE);
					}
					return true;
				}
				return false;
			}
		});
		// 编辑器添加文本监听
		mFootEditer.addTextChangedListener(UIHelper.getTextWatcher(
				getActivity(), tempReplyKey));

		// 显示临时编辑内容
		UIHelper.showTempEditContent(getActivity(), mFootEditer, tempReplyKey);

		// 加载宣讲会详细信息
		topicID = careerTalkDetail.getTopicID();
		Log.i("cd", String.format("TopicId %s", topicID));
		
		 * vTitle.setText(careerTalkDetail.getCompanyName());
		 * vPubDate.setText(StringUtils.friendly_time(careerTalkDetail
		 * .getTime()));
		 
		// vReplyCount.setText(String.valueOf(careerTalkDetail.getReplies()));

		// 是否参与
		if (careerTalkDetail.getIsJoined() == 1)
			vJoined.setImageResource(R.drawable.widget_bar_favorite2);
		else
			vJoined.setImageResource(R.drawable.widget_bar_favorite);

		// 显示评论数
		if (careerTalkDetail.getReplies() > 0) {
			bv_comment.setText(careerTalkDetail.getReplies() + "");
			bv_comment.show();
		} else {
			bv_comment.setText("");
			bv_comment.hide();
		}
	}

	// 初始化控件数据
	private void initData() {
		Log.i("cd", "initData");
		careerDetailHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					headButtonSwitch(DATA_LOAD_COMPLETE);
					// TODO
					// 加载数据到view中

				} else if (msg.what == 0) {
					headButtonSwitch(DATA_LOAD_FAIL);

					UIHelper.ToastMessage(getActivity(),
							R.string.msg_load_is_null);
				} else if (msg.what == -1 && msg.obj != null) {
					headButtonSwitch(DATA_LOAD_FAIL);
					((AppException) msg.obj).makeToast(getActivity());
				}
			}
		};

		initCareerTalkData(false);
	}

	private void initCareerTalkData(final boolean isRefresh) {
		Log.i("cd", "initCareerTalkData");
		headButtonSwitch(DATA_LOAD_ING);

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					if (isRefresh || careerTalkDetail.getIntroduction() == null) {
						((AppContext) getActivity().getApplication())
								.setCareerTalkDetail(careerTalkDetail);
					}
					msg.what = (careerTalkDetail.getIntroduction() != null) ? 1
							: 0;
				} catch (AppException e) {
					
					msg.what = -1;
					msg.obj = e;
				}
				careerDetailHandler.sendMessage(msg);
			}
		}.start();
	}

	*//**
	 * 底部栏切换
	 * 
	 * @param type
	 *//*
	private void viewSwitch(int type) {
		switch (type) {
		case VIEWSWITCH_TYPE_DETAIL:
			vDetail.setEnabled(false);
			vReplyList.setEnabled(true);
			vHeadTitle.setText(R.string.career_detail_head_title);
			vViewSwitcher.setDisplayedChild(0);
			break;
		case VIEWSWITCH_TYPE_COMMENTS:
			vDetail.setEnabled(true);
			vReplyList.setEnabled(false);
			vHeadTitle.setText(R.string.comment_list_head_title);
			vViewSwitcher.setDisplayedChild(1);
			break;
		}
	}

	*//**
	 * 头部按钮展示
	 * 
	 * @param type
	 *//*
	private void headButtonSwitch(int type) {
		switch (type) {
		case DATA_LOAD_ING:
			vScrollView.setVisibility(View.GONE);
			vProgressbar.setVisibility(View.VISIBLE);
			vRefresh.setVisibility(View.GONE);
			break;
		case DATA_LOAD_COMPLETE:
			vScrollView.setVisibility(View.VISIBLE);
			vProgressbar.setVisibility(View.GONE);
			vRefresh.setVisibility(View.VISIBLE);
			break;
		case DATA_LOAD_FAIL:
			vScrollView.setVisibility(View.GONE);
			vProgressbar.setVisibility(View.GONE);
			vRefresh.setVisibility(View.VISIBLE);
			break;
		}
	}

	private View.OnClickListener homeClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			UIHelper.showHome(getActivity());
		}
	};

	private View.OnClickListener refreshClickListener = new View.OnClickListener() {
		public void onClick(View v) {
//			initCareerTalkData(true);
			loadLvReplyData(0, recruitReplyHandler,
					UIHelper.LISTVIEW_ACTION_REFRESH);
		}
	};

	private View.OnClickListener shareClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (careerTalkDetail == null) {
				UIHelper.ToastMessage(v.getContext(),
						R.string.msg_read_detail_fail);
				return;
			}
			// 分享到
			UIHelper.showShareDialog(getActivity(),
					careerTalkDetail.getCompanyName());
		}
	};

	private View.OnClickListener detailClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (careerTalkDetail == null) {
				return;
			}
			// 切换到详情
			viewSwitch(VIEWSWITCH_TYPE_DETAIL);
		}
	};

	private View.OnClickListener commentlistClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (careerTalkDetail == null) {
				return;
			}
			// 切换到评论
			viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
		}
	};

	private View.OnClickListener joinedClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (careerTalkDetail == null) {
				return;
			}

			final AppContext ac = (AppContext) getActivity().getApplication();
			if (!ac.isLogin()) {
				// TODO
				// UIHelper.showLoginDialog(RecruitDetailActivity.this);
				return;
			}

			new Thread() {
				public void run() {
					try {
						if (careerTalkDetail.getIsJoined() == 1) {
							ac.joinRecruit(careerTalkDetail.getCareerTalkID(),
									false);
						} else {
							ac.joinRecruit(careerTalkDetail.getCareerTalkID(),
									true);
						}
					} catch (AppException e) {
						
					}
				}
			}.start();
		}
	};

	// 初始化视图控件
	private void initReplyView(View careerDetailView, LayoutInflater inflater) {
		Log.i("cd", "initReplyView");
		lvReply_footer = inflater.inflate(R.layout.listview_footer, null);
		lvReply_foot_more = (TextView) lvReply_footer
				.findViewById(R.id.listview_foot_more);
		lvReply_foot_progress = (ProgressBar) lvReply_footer
				.findViewById(R.id.listview_foot_progress);

		lvReplyAdapter = new ListViewReplyAdapter(getActivity(), a lvReplyData,
				R.layout.comment_listitem);
		Log.i("cd", "adpater");
		pvReply = (PullToRefreshListView) careerDetailView
				.findViewById(R.id.comment_list_listview);

		pvReply.addFooterView(lvReply_footer);// 添加底部视图 必须在setAdapter前
		Log.i("cd", "add fotter");
		pvReply.setAdapter(lvReplyAdapter);
		Log.i("cd", "set adpater");
		pvReply.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击头部、底部栏无效
				if (position == 0 || view == lvReply_footer)
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

				// 跳转--回复评论界面
				UIHelper.showCommentPub(getActivity(), topicID,
						bbsReply.getReplyID(), bbsReply.getUserName(),
						bbsReply.getContent());
			}
		});
		pvReply.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				PullToRefreshListView pvReply2 = pvReply;
				pvReply2.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvReplyData.size() == 0)
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvReply_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				if (scrollEnd && curLvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					pvReply2.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvReply_foot_more.setText(R.string.load_ing);
					lvReply_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = lvSumData / 20;
					loadLvReplyData(pageIndex, recruitReplyHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL);
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
				loadLvReplyData(0, recruitReplyHandler,
						UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
	}

	// 初始化评论数据
	private void initReplyData() {
		Log.i("cd", "initReplyData");
		recruitReplyHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what >= 0) {
					List<BBSReply> list = (List<BBSReply>) msg.obj;
					// 处理listview数据
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
						lvSumData = msg.what;
						lvReplyData.clear();// 先清除原有数据
						lvReplyData.addAll(list);
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						lvSumData += msg.what;
						if (lvReplyData.size() > 0) {
							for (BBSReply reply : list) {
								boolean b = false;
								for (BBSReply existReply : lvReplyData) {
									if (existReply.getReplyID() == reply
											.getReplyID()) {
										b = true;
										break;
									}
								}
								if (!b)
									lvReplyData.add(reply);
							}
						} else {
							lvReplyData.addAll(list);
						}
						break;
					}

					// 评论数更新
					if (careerTalkDetail != null
							&& lvReplyData.size() > careerTalkDetail
									.getReplies()) {
						bv_comment.setText(lvReplyData.size() + "");
						bv_comment.show();
					}

					if (msg.what < 20) {
						curLvDataState = UIHelper.LISTVIEW_DATA_FULL;
						lvReplyAdapter.notifyDataSetChanged();
						lvReply_foot_more.setText(R.string.load_full);
					} else if (msg.what == 20) {
						curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
						lvReplyAdapter.notifyDataSetChanged();
						lvReply_foot_more.setText(R.string.load_more);
					}
					
					 * // 发送通知广播 if (notice != null) {
					 * UIHelper.sendBroadCast(RecruitDetailActivity.this,
					 * notice); }
					 
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
					lvReply_foot_more.setText(R.string.load_error);
					((AppException) msg.obj).makeToast(getActivity());
				}
				if (lvReplyData.size() == 0) {
					curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
					lvReply_foot_more.setText(R.string.load_empty);
				}
				lvReply_foot_progress.setVisibility(View.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					pvReply.onRefreshComplete(getString(R.string.pull_to_refresh_update)
							+ new Date().toLocaleString());
					pvReply.setSelection(0);
				}
			}
		};
		this.loadLvReplyData(0, recruitReplyHandler,
				UIHelper.LISTVIEW_ACTION_INIT);
	}

	*//**
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
	 *//*
	private void loadLvReplyData(final int pageIndex, final Handler handler,
			final int action) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					List<BBSReply> replylist = ((AppContext) getActivity()
							.getApplication()).getBBSReplyList(topicID,
							pageIndex, isRefresh);
					msg.what = replylist.size();
					msg.obj = replylist;
				} catch (AppException e) {
					
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;// 告知handler当前action
				handler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != getActivity().RESULT_OK)
			return;
		if (data == null)
			return;

		viewSwitch(VIEWSWITCH_TYPE_COMMENTS);// 跳到评论列表

		BBSReply comm = (BBSReply) data
				.getSerializableExtra("COMMENT_SERIALIZABLE");
		lvReplyData.add(0, comm);
		lvReplyAdapter.notifyDataSetChanged();
		pvReply.setSelection(0);
		// 显示评论数
		int count = careerTalkDetail.getReplies() + 1;
		careerTalkDetail.setReplies(count);
		bv_comment.setText(count + "");
		bv_comment.show();
	}

	private View.OnClickListener commentpubClickListener = new View.OnClickListener() {
		public void onClick(View v) {

			_content = mFootEditer.getText().toString();
			if (StringUtils.isEmpty(_content)) {
				UIHelper.ToastMessage(v.getContext(), "请输入评论内容");
				return;
			}

			final AppContext ac = (AppContext) getActivity().getApplication();
			if (!ac.isLogin()) {
				// TODO
				// UIHelper.showLoginDialog(RecruitDetailActivity.this);
				return;
			}

			mProgress = ProgressDialog.show(v.getContext(), null, "发表中···",
					true, true);

			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {

					if (mProgress != null)
						mProgress.dismiss();

					if (msg.what == 1) {
						BBSReply reply = (BBSReply) msg.obj;
						UIHelper.ToastMessageCommentSucess(getActivity());
						mFootViewSwitcher.setDisplayedChild(0);
						mFootEditer.clearFocus();
						mFootEditer.setText("");
						mFootEditer.setVisibility(View.GONE);
						// 跳到评论列表
						viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
						// 更新评论列表
						lvReplyData.add(0, reply);
						lvReplyAdapter.notifyDataSetChanged();
						pvReply.setSelection(0);
						// 显示评论数
						int count = careerTalkDetail.getReplies() + 1;
						careerTalkDetail.setReplies(count);
						bv_comment.setText(count + "");
						bv_comment.show();
						// 清除之前保存的编辑内容
						ac.removeProperty(tempReplyKey);
					} else {
						((AppException) msg.obj).makeToast(getActivity());
					}
				}
			};
			new Thread() {
				public void run() {
					Message msg = new Message();
					try {
						// 发表评论
						BBSReply reply = ac.addReply(topicID, _content);
						msg.what = 1;
						msg.obj = reply;
					} catch (AppException e) {
						
						msg.what = -1;
						msg.obj = e;
					}
					handler.sendMessage(msg);
				}
			}.start();
		}
	};

	*//**
	 * 注册双击全屏事件
	 *//*
	private void regOnDoubleEvent() {
		gd = new GestureDetector(getActivity(),
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						isFullScreen = !isFullScreen;
						if (!isFullScreen) {
							WindowManager.LayoutParams params = getActivity()
									.getWindow().getAttributes();
							params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
							getActivity().getWindow().setAttributes(params);
							getActivity()
									.getWindow()
									.clearFlags(
											WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
							vHead.setVisibility(View.VISIBLE);
							vFooter.setVisibility(View.VISIBLE);
						} else {
							WindowManager.LayoutParams params = getActivity()
									.getWindow().getAttributes();
							params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
							getActivity().getWindow().setAttributes(params);
							getActivity()
									.getWindow()
									.addFlags(
											WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
							vHead.setVisibility(View.GONE);
							vFooter.setVisibility(View.GONE);
						}
						return true;
					}
				});
	}

	
	 * @Override public boolean dispatchTouchEvent(MotionEvent event) { if
	 * (isAllowFullScreen()) { gd.onTouchEvent(event); } gd.onTouchEvent(event);
	 * return super.dispatchTouchEvent(event); }
	 
}
*/