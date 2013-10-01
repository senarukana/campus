package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.adapter.GridViewFaceAdapter;
import com.campusrecruit.adapter.ListViewReplyAdapter;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Company;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.Result;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.BadgeView;
import com.campusrecruit.widget.LinkView;
import com.campusrecruit.widget.PullToRefreshListView;
import com.krislq.sliding.R;

public class CareerTalkDetailActivity extends LoadingActivity {

	/*
	 * private FrameLayout vHead; private ImageView vRefresh; private TextView
	 * vHeadTitle; private ProgressBar vProgressbar;
	 */

	private ToggleButton vJoined;
	private LinearLayout vFooter;

	// comment footer
	private ImageView mFace;
	private GridView mGridView;
	private GridViewFaceAdapter mGVFaceAdapter;
	private ViewSwitcher mFootCommentViewSwitcher;
	private ImageView mFootEditebox;
	private EditText mFootEditer;
	private Button mFootPubcomment;
	private ProgressDialog mProgress;
	private InputMethodManager imm;
	private String tempCommentKey = AppConfig.TEMP_COMMENT;

	//footer
	private ViewSwitcher mCareerDetailViewSwitcher;
	private BadgeView bv_comment;
	private ImageView vDetail;
	private ImageView vReplyList;
	private ImageView vShare;

	//companyView
	private ImageView vFamousFlag;
	private TextView vSourceFrom;
	private View vURLLayout;
	private TextView vCompanyName;
	private TextView vPlace;
	private TextView vTime;
	private TextView vDate;
	private TextView vSimpleDate;
	private TextView vSchoolName;

	private View companyMainView;
	private View companyInfoView;
	private TextView vdetailCompanyName;
	private TextView vCompanyPlace;
	private TextView vCompanyIndustry;
	private LinkView vCompanyIntroduction;

	private Handler careerDetailHandler;
	private Handler recruitReplyHandler;

	private final static int VIEWSWITCH_TYPE_DETAIL = 0x001;
	private final static int VIEWSWITCH_TYPE_COMMENTS = 0x002;

	// loading
	private boolean isloading = false;

	private PullToRefreshListView pvReply;
	private ListViewReplyAdapter lvReplyAdapter;
	private List<BBSReply> lvReplyData = new ArrayList<BBSReply>();
	private View lvReply_footer;
	private TextView lvReply_foot_more;
	private ProgressBar lvReply_foot_progress;
	private int lvSumData;

	private int curLvDataState;
	private int curLvPosition;// 当前listview选中的item位置

	private CareerTalk careerTalkDetail;
	private int careerTalkID;
	private int topicID;
	private boolean flag;

	private AppContext appContext;
	private String _content;

	private GestureDetector gd;
	private boolean isFullScreen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("cd", "career talk onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.career_detail);

		careerTalkDetail = (CareerTalk) getIntent().getSerializableExtra(
				"careerTalk");
		flag = getIntent().getBooleanExtra("flag", true);
		appContext = (AppContext) getApplication();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setHomeButtonEnabled(true);
		initView();
		initCompanyView();
		initReplyView();
		if (careerTalkDetail == null) {
			careerTalkID = getIntent().getIntExtra("careerTalkID", 0);
			initCareerTalkData();
		} else {
			fillView();
		}

		// 初始化表情视图
		this.initGridView();

		// 注册双击全屏事件
		regOnDoubleEvent();

		if (!flag)
			viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
	}

	private void fillView() {
		getActionBar().setTitle(careerTalkDetail.getCompanyName());
		if (careerTalkDetail.getIsJoined() == 1)
			vJoined.setChecked(true);
		fillCareerTalkView();
		initCompanyData();
		initReplyData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_refresh, menu);
		initCompanyData();
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
			initCompanyData();
			return true;
		}
		return false;
	}

	private void initCareerTalkData() {
		careerDetailHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					Log.i("cd", "handle detail ");
					careerTalkDetail = (CareerTalk) msg.obj;
					fillView();
				} else {
					((AppException) msg.obj)
							.makeToast(CareerTalkDetailActivity.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					CareerTalk careerTalk = appContext
							.getCareerTalkDetail(careerTalkID);
					msg.what = 1;
					msg.obj = careerTalk;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				careerDetailHandler.sendMessage(msg);
			}
		}.start();
	}

	private void initCompanyView() {
		Log.i("test", "init companyView");
		companyMainView = findViewById(R.id.company_info_main_layout);
		companyInfoView = findViewById(R.id.company_info_layout);
		vdetailCompanyName = (TextView) findViewById(R.id.company_detail_name);
		vCompanyPlace = (TextView) findViewById(R.id.company_detail_address);
		vCompanyIndustry = (TextView) findViewById(R.id.company_detail_industry);
		vCompanyIntroduction = (LinkView) findViewById(R.id.company_detail_info);
		initLoadingView(companyMainView);
		Log.i("test", "init companyView complete");
	}

	private void initCompanyData() {
		Log.i("test", "init company data");
		Handler companyInfoHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					Log.i("test", "handle company data");
					hideLoadProgress(companyInfoView);
					Company newCompany = (Company) msg.obj;
					
					if (appContext.companyLoadFromDisk) {
						UIHelper.ToastMessage(CareerTalkDetailActivity.this,
								getString(R.string.load_fail));
					}
					if (newCompany.getIndustry() != null && !newCompany.getIndustry().isEmpty()) {
						vCompanyIndustry.setText(newCompany.getIndustry());
					} else {
						vCompanyIndustry.setText(R.string.nodata);
					}
					if (newCompany.getPlace() != null && !newCompany.getPlace().isEmpty()) {
						vCompanyPlace.setText(newCompany.getPlace());
					}
					else {
						vCompanyPlace.setText(R.string.nodata);
					}
					if (newCompany.getIntroduction() != null && !newCompany.getIntroduction().isEmpty()) {
						vCompanyIntroduction.setText(newCompany
								.getIntroduction());
					}
					else {
						vCompanyIntroduction.setText(R.string.nodata);
					}
					/*
					 * recruit.getCompany().setPlace(newCompany.getPlace() +
					 * " "); recruit.getCompany().setIntroduction(newCompany.
					 * getIntroduction() + " ");
					 * appContext.saveCompanyInfo(recruit.getRecruitID(),
					 * newCompany);
					 */
				} else {
					hideLoadProgressWithError(companyInfoView);
					((AppException) msg.obj)
							.makeToast(CareerTalkDetailActivity.this);
				}
				Log.i("rd", "load company data complete");
			}
		};
		loadCompanyData(companyInfoHandler);
	}

	private void loadCompanyData(final Handler companyInfoHandler) {
		Log.i("test", "init company data");
		if (isloading)
			return;
		showLoadProgress(companyInfoView);
		new Thread() {
			@Override
			public void run() {
				Message message = new Message();
				try {
					Company newCompany = appContext
							.getCompanyDetail(careerTalkDetail.getCompanyID());
					
					appContext.clickCareerTalk(careerTalkID);
					message.what = 1;
					message.obj = newCompany;
				} catch (AppException e) {
					e.printStackTrace();
					message.what = -1;
					message.obj = e;
				}
				companyInfoHandler.sendMessage(message);
			}
		}.start();
	}
	
	private void initCommentFooter() {
		mFace = (ImageView) findViewById(R.id.detail_foot_face);
		mFace.setOnClickListener(facesClickListener);

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		Log.i("td", "initFoot");
		// foot
		mFootCommentViewSwitcher = (ViewSwitcher) findViewById(R.id.detail_foot_viewswitcher);
		mFootPubcomment = (Button) findViewById(R.id.detail_foot_pubcomment);
		mFootPubcomment.setOnClickListener(commentpubClickListener);
		mFootEditebox = (ImageView) findViewById(R.id.detail_footbar_editebox);
		mFootEditebox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFootCommentViewSwitcher.showNext();
				mFootEditer.setVisibility(View.VISIBLE);
				mFootEditer.requestFocus();
				mFootEditer.requestFocusFromTouch();
				imm.showSoftInput(mFootEditer, 0);// 显示软键盘
			}
		});
		mFootEditer = (EditText) findViewById(R.id.detail_foot_editer);
		mFootEditer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 显示软键盘&隐藏表情
				showIMM();
			}
		});
		mFootEditer.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mFootCommentViewSwitcher.getDisplayedChild() == 1) {
						mFootCommentViewSwitcher.setDisplayedChild(0);
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


	// 初始化视图控件
	private void initView() {
		Log.i("cd", "career talk init");

		vFamousFlag = (ImageView) findViewById(R.id.career_famous_flag);
		vSourceFrom = (TextView) findViewById(R.id.career_source_from);
		vURLLayout = findViewById(R.id.career_original_url_layout);
		vSimpleDate = (TextView) findViewById(R.id.career_simple_date);
		vDate = (TextView) findViewById(R.id.career_talk_date);
		vCompanyName = (TextView) findViewById(R.id.career_company_name);
		vPlace = (TextView) findViewById(R.id.career_place);
		vTime = (TextView) findViewById(R.id.career_time);
		vSchoolName = (TextView) findViewById(R.id.career_school);
		Log.i("cd", "career talk init ok");

		/*
		 * vHead = (FrameLayout) findViewById(R.id.detail_header); vHome
		 * = (ImageView) findViewById(R.id.detail_home); vRefresh =
		 * (ImageView) findViewById(R.id.detail_refresh); vHeadTitle =
		 * (TextView) findViewById(R.id.detail_head_title); vProgressbar
		 * = (ProgressBar) findViewById(R.id.detail_head_progress);
		 */

		vFooter = (LinearLayout) findViewById(R.id.detail_footer);
		vDetail = (ImageView) findViewById(R.id.detail_footbar_detail);
		vReplyList = (ImageView) findViewById(R.id.detail_footbar_commentlist);
		vShare = (ImageView) findViewById(R.id.detail_footbar_share);
		vJoined = (ToggleButton) findViewById(R.id.detail_footbar_joined);
		if (vDetail == null)
			Log.i("bug", " caeer vdetail is null !!!!");
		vDetail.setEnabled(false);
		mCareerDetailViewSwitcher = (ViewSwitcher) findViewById(R.id.career_detail_viewswitcher);
		Log.i("cd", "init view set listener");
		vJoined.setOnClickListener(joinedClickListener);
		vShare.setOnClickListener(shareClickListener);
		vDetail.setOnClickListener(detailClickListener);
		vReplyList.setOnClickListener(commentlistClickListener);
		Log.i("cd", "init view set listener complete");
		bv_comment = new BadgeView(this, vReplyList);
		bv_comment.setBackgroundResource(R.drawable.widget_count_bg2);
		bv_comment.setIncludeFontPadding(false);
		bv_comment.setGravity(Gravity.CENTER);
		bv_comment.setTextSize(8f);
		bv_comment.setTextColor(Color.WHITE);

		initCommentFooter();
		Log.i("cd", "init view complete");

	}

	private void fillCareerTalkView() {
		// 加载宣讲会详细信息
		topicID = careerTalkDetail.getTopicID();
		if (careerTalkDetail.getFamous() == 1) {
			vFamousFlag.setVisibility(View.VISIBLE);
		} else {
			vFamousFlag.setVisibility(View.INVISIBLE);
		}
		vSourceFrom.setText(careerTalkDetail.getSourceFrom());
		if (vURLLayout != null) {
			if (careerTalkDetail.getUrl() != null
					&& !careerTalkDetail.getUrl().isEmpty()) {
				vURLLayout.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.i("bug", "click");
						Log.i("bug", "url is :" + careerTalkDetail.getUrl());
						UIHelper.openBrowser(v.getContext(), careerTalkDetail.getUrl());

					}
				});
				vURLLayout.setVisibility(View.VISIBLE);
			}
		}
		vCompanyName.setText(careerTalkDetail.getCompanyName());
		vPlace.setText(careerTalkDetail.getPlace());
		vTime.setText(careerTalkDetail.getTime() + "");
		try {
			String friendlyTime = StringUtils
					.friendly_happen_time(careerTalkDetail.getDate());
			vSimpleDate.setText(StringUtils.trimed_time(careerTalkDetail
					.getDate()));
			vDate.setText(friendlyTime);
		} catch (Exception e) {
			Log.i("bug", "time format is wrong careertalk adapter");
			vDate.setText(careerTalkDetail.getDate());
			vSimpleDate.setText(careerTalkDetail.getDate());
		}
		vSchoolName.setText(careerTalkDetail.getSchoolName());
		vdetailCompanyName.setText(careerTalkDetail.getCompanyName());


		// 显示评论数
		if (careerTalkDetail.getReplies() > 0) {
			bv_comment.setText(careerTalkDetail.getReplies() + "");
			bv_comment.show();
		} else {
			bv_comment.setText("");
			bv_comment.hide();
		}
	}

	/**
	 * 底部栏切换
	 * 
	 * @param type
	 */
	private void viewSwitch(int type) {
		switch (type) {
		case VIEWSWITCH_TYPE_DETAIL:
			vDetail.setEnabled(false);
			vReplyList.setEnabled(true);
			// vHeadTitle.setText(R.string.detail_head_title);
			mCareerDetailViewSwitcher.setDisplayedChild(0);
			break;
		case VIEWSWITCH_TYPE_COMMENTS:
			vDetail.setEnabled(true);
			vReplyList.setEnabled(false);
			// vHeadTitle.setText(R.string.comment_list_head_title);
			mCareerDetailViewSwitcher.setDisplayedChild(1);
			break;
		}
	}

	private View.OnClickListener homeClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			UIHelper.showHome(CareerTalkDetailActivity.this);
		}
	};

	private View.OnClickListener refreshClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			// initCareerTalkData(true);
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
			UIHelper.showShareDialog(CareerTalkDetailActivity.this,
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

			if (!appContext.isLogin()) {
				UIHelper.showLoginDialog(CareerTalkDetailActivity.this);
				vJoined.setChecked(false);
				return;
			}

			if (careerTalkDetail.getIsJoined() == 1) {
				careerTalkDetail.setIsJoined(0);
				UIHelper.ToastMessage(CareerTalkDetailActivity.this, "取消收藏");
				appContext.caeerTalkDetailJoin(careerTalkDetail, false);
			} else {
				careerTalkDetail.setIsJoined(1);
				UIHelper.ToastMessage(CareerTalkDetailActivity.this, "收藏成功");
				appContext.caeerTalkDetailJoin(careerTalkDetail, true);
			}

			new Thread() {
				public void run() {

					try {
						if (careerTalkDetail.getIsJoined() == 1) {
							appContext.startAlarm(CareerTalkDetailActivity.this, careerTalkDetail);
							appContext.joinCareerTalk(
									careerTalkDetail.getCareerTalkID(), true);
						} else {
							appContext.cancelAlarm(CareerTalkDetailActivity.this, careerTalkDetail);
							appContext.joinCareerTalk(
									careerTalkDetail.getCareerTalkID(), false);
						}
					} catch (AppException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	};

	// 初始化视图控件
	private void initReplyView() {
		Log.i("cd", "initReplyView");
		lvReply_footer = getLayoutInflater().inflate(R.layout.listview_footer,
				null);
		lvReply_foot_more = (TextView) lvReply_footer
				.findViewById(R.id.listview_foot_more);
		lvReply_foot_progress = (ProgressBar) lvReply_footer
				.findViewById(R.id.listview_foot_progress);

		lvReplyAdapter = new ListViewReplyAdapter(this, appContext, lvReplyData,
				R.layout.comment_listitem);
		Log.i("cd", "adpater");
		pvReply = (PullToRefreshListView) findViewById(R.id.comment_list_listview);
		pvReply.addFooterView(lvReply_footer);// 添加底部视图 必须在setAdapter前
		pvReply.setAdapter(lvReplyAdapter);
		pvReply.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!appContext.isLogin()) {
					UIHelper.showLoginDialog(CareerTalkDetailActivity.this);
					return;
				}
				// 点击底部栏无效
				if ( view == lvReply_footer)
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
					UIHelper.ToastMessage(CareerTalkDetailActivity.this,
							"不能回复自己");
					return;
				}

				// 跳转--回复评论界面
				UIHelper.showCommentPub(CareerTalkDetailActivity.this, topicID,
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
				if (msg.what > 0) {
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
					/*
					 * // 发送通知广播 if (notice != null) {
					 * UIHelper.sendBroadCast(RecruitDetailActivity.this,
					 * notice); }
					 */
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
					lvReply_foot_more.setText(R.string.load_error);
					((AppException) msg.obj)
							.makeToast(CareerTalkDetailActivity.this);
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
					List<BBSReply> replylist = ((AppContext) getApplication())
							.getBBSReplyList(topicID, pageIndex, isRefresh);
					msg.what = replylist.size();
					msg.obj = replylist;
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

	// 保存用戶發帖信息
	private void saveUserReplyTopic() {
		// 添加用户帖子
		BBSTopic topic = new BBSTopic();
		topic.setTopicID(careerTalkDetail.getTopicID());
		topic.setCreatedTime(careerTalkDetail.getCreatedTime());
		topic.setTitle(careerTalkDetail.getCompanyName() + "宣讲会");
		topic.setStatus(1);
		Log.i("test", "comment after career talk");
		appContext.commentPubAfter(topic);

		new Thread() {
			@Override
			public void run() {
				appContext.saveUserTopic(careerTalkDetail.getTopicID(),
						careerTalkDetail.getCompanyName(),
						careerTalkDetail.getCreatedTime());
			}

		}.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		if (data == null)
			return;

		viewSwitch(VIEWSWITCH_TYPE_COMMENTS);// 跳到评论列表

		BBSReply reply = (BBSReply) data
				.getSerializableExtra("COMMENT_SERIALIZABLE");
		reply.setStatus(1);
		lvReplyData.add(reply);
		pvReply.setSelection(lvReplyData.size() - 1);
		lvReplyAdapter.notifyDataSetChanged();
		// 显示评论数
		int count = careerTalkDetail.getReplies() + 1;
		careerTalkDetail.setReplies(count);
		bv_comment.setText(count + "");
		bv_comment.show();
		saveUserReplyTopic();
	}

	private View.OnClickListener commentpubClickListener = new View.OnClickListener() {
		public void onClick(View v) {

			if (!appContext.isLogin()) {
				UIHelper.showLoginDialog(CareerTalkDetailActivity.this);
				return;
			}

			_content = mFootEditer.getText().toString();
			if (StringUtils.isEmpty(_content)) {
				UIHelper.ToastMessage(v.getContext(), "请输入评论内容");
				return;
			}

			if (!appContext.isLogin()) {
				UIHelper.showLoginDialog(CareerTalkDetailActivity.this);
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
						UIHelper.ToastMessageCommentSucess(CareerTalkDetailActivity.this);
						mFootCommentViewSwitcher.setDisplayedChild(0);
						mFootEditer.clearFocus();
						mFootEditer.setText("");
						mFootEditer.setVisibility(View.GONE);
						// 跳到评论列表
						viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
						
						// 隐藏软键盘
						imm.hideSoftInputFromWindow(
								mFootEditer.getWindowToken(), 0);
						// 隐藏表情
						hideFace();
						
						// 更新评论列表
						lvReplyData.add(reply);
						pvReply.setSelection(lvReplyData.size() - 1);
						lvReplyAdapter.notifyDataSetChanged();
						// 显示评论数
						int count = careerTalkDetail.getReplies() + 1;
						careerTalkDetail.setReplies(count);
						bv_comment.setText(count + "");
						bv_comment.show();
						// 清除之前保存的编辑内容
						appContext.removeProperty(tempCommentKey);
						// 保存用戶發帖信息
						saveUserReplyTopic();

					} else {
						((AppException) msg.obj)
								.makeToast(CareerTalkDetailActivity.this);
					}
				}
			};
			new Thread() {
				public void run() {
					Message msg = new Message();
					try {
						// 发表评论
						BBSReply reply = appContext.addReply(topicID, _content);
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

	// 初始化表情控件
	private void initGridView() {
		mGVFaceAdapter = new GridViewFaceAdapter(this);
		mGridView = (GridView) findViewById(R.id.detail_foot_faces);
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

	/**
	 * 注册双击全屏事件
	 */
	private void regOnDoubleEvent() {
		gd = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						isFullScreen = !isFullScreen;
						if (!isFullScreen) {
							WindowManager.LayoutParams params = getWindow()
									.getAttributes();
							params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
							getWindow().setAttributes(params);
							getWindow()
									.clearFlags(
											WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
							// vHead.setVisibility(View.VISIBLE);
							vFooter.setVisibility(View.VISIBLE);
						} else {
							WindowManager.LayoutParams params = getWindow()
									.getAttributes();
							params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
							getWindow().setAttributes(params);
							getWindow()
									.addFlags(
											WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
							// vHead.setVisibility(View.GONE);
							vFooter.setVisibility(View.GONE);
						}
						return true;
					}
				});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		/*
		 * if (isAllowFullScreen()) { gd.onTouchEvent(event); }
		 */
		gd.onTouchEvent(event);
		return super.dispatchTouchEvent(event);
	}
}
