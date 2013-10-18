package com.campusrecruit.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
//import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import com.campusrecruit.adapter.GridViewFaceAdapter;
import com.campusrecruit.adapter.ListViewReplyAdapter;
import com.campusrecruit.adapter.ViewPagerRecruitDetailAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.Company;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.fragment.BaseFragment;
import com.campusrecruit.fragment.CommentsFragment;
import com.campusrecruit.fragment.CompanyDetailFragment;
import com.campusrecruit.fragment.RecruitDetailFragment;
import com.campusrecruit.fragment.RecruitFragment;
import com.campusrecruit.fragment.RecruitProcessFragment;
import com.campusrecruit.widget.BadgeView;
import com.campusrecruit.widget.PullToRefreshListView;
import com.pcncad.campusRecruit.R;

public abstract class RecruitDetailActivity extends BaseActivity {

	protected List<BaseFragment> fragmentList;
	protected ViewPager viewPager = null;
	
	private ActionBar actionBar;

	// footer
	private ToggleButton vJoined;
	private BadgeView bv_comment;
	private ImageView vDetail;
	private ImageView vReplyList;
	private ImageView vShare;
	private LinearLayout vFooter;

	// comment footer
	private ImageView mFace;
	private GridView mGridView;
	private GridViewFaceAdapter mGVFaceAdapter;
	private ViewSwitcher mFootViewSwitcher;
	private ImageView mFootEditebox;
	private EditText mFootEditer;
	private Button mFootPubcomment;
	private ProgressDialog mProgress;
	private InputMethodManager imm;
	private String tempCommentKey = AppConfig.TEMP_COMMENT;

	protected Recruit recruitDetail;
	public ArrayList<BBSReply> lvReplyData = new ArrayList<BBSReply>();

	private String _content;
	boolean flag;

	private GestureDetector gd;
	private boolean isFullScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("test","oncreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recruit_detail_info);
		
		if (!appContext.getTutorialRecruitDetail()) {
			UIHelper.showRecruitDetailTutorial(this);
			appContext.setTutorialRecruitDetail();
		}
		

		recruitDetail = (Recruit) getIntent().getSerializableExtra("recruit");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(recruitDetail.getCompany().getCompanyName());
		Log.i("test","fra");
		initFragment();
		Log.i("test","vp");
		initViewPager();
		Log.i("test","tab");
		initTab();
		Log.i("test","view");
		initView();

		// 初始化表情视图
		initGridView();
		// initFragmentData(CATALOG_DESCRIPTION);
		// 注册双击全屏事件
		regOnDoubleEvent();

		flag = getIntent().getBooleanExtra("flag", true);

		if (flag) {
			initFragmentData(0);
			viewPager.setCurrentItem(0);
		} else {
			initFragmentData(getCommentFragmentPosition());
			viewPager.setCurrentItem(getCommentFragmentPosition());
		}
		
		Log.i("test","init complete");
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_refresh, menu);
		return true;
	}

	private void initCommentFooter() {
		mFace = (ImageView) findViewById(R.id.detail_foot_face);
		mFace.setOnClickListener(facesClickListener);

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		Log.i("td", "initFoot");
		// foot
		mFootViewSwitcher = (ViewSwitcher) findViewById(R.id.detail_foot_viewswitcher);
		mFootPubcomment = (Button) findViewById(R.id.detail_foot_pubcomment);
		mFootPubcomment.setOnClickListener(commentpubClickListener);
		mFootEditebox = (ImageView) findViewById(R.id.detail_footbar_editebox);
		mFootEditebox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFootViewSwitcher.showNext();
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

	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.detail_viewPager);
		ViewPagerRecruitDetailAdapter adapter = new ViewPagerRecruitDetailAdapter(
				getSupportFragmentManager(), fragmentList);
		viewPager.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		viewPager.setOnPageChangeListener(onPageChangeListener);
	}

	private SimpleOnPageChangeListener onPageChangeListener = new SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			initFragmentData(position);
			getActionBar().setSelectedNavigationItem(position);
		}

	};
	
	protected void addTab(String text) {
		actionBar.addTab(actionBar.newTab().setText(text)
				.setTabListener(tabListener));
	}
	
	private TabListener tabListener = new TabListener() {

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {

		}

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			if (viewPager.getCurrentItem() != tab.getPosition())
				viewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {

		}

	};
	
	

	private void initTab() {
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.removeAllTabs();
		initTabData();
	}
	
	protected abstract void initTabData();

	protected abstract void initFragment();

	protected abstract int getCommentFragmentPosition();

	protected abstract CommentsFragment getCommentsFragment();

	protected abstract void initFragmentData(int type);

	protected void initView() {
		vDetail = (ImageView) findViewById(R.id.detail_footbar_detail);
		vReplyList = (ImageView) findViewById(R.id.detail_footbar_commentlist);
		vShare = (ImageView) findViewById(R.id.detail_footbar_share);
		vJoined = (ToggleButton) findViewById(R.id.detail_footbar_joined);

		vJoined.setOnClickListener(joinedClickListener);
		vShare.setOnClickListener(shareClickListener);
		vDetail.setOnClickListener(detailClickListener);
		vReplyList.setOnClickListener(commentlistClickListener);

		if (recruitDetail.getIsJoined() == 1)
			vJoined.setChecked(true);

		bv_comment = new BadgeView(this, vReplyList);
		bv_comment.setBackgroundResource(R.drawable.widget_count_bg2);
		bv_comment.setIncludeFontPadding(false);
		bv_comment.setGravity(Gravity.CENTER);
		bv_comment.setTextSize(8f);
		bv_comment.setTextColor(Color.WHITE);

		initCommentFooter();
		/*
		 * // 是否参与 if (recruitDetail.getIsJoined() == 1)
		 * vJoined.setImageResource(R.drawable.widget_bar_favorite_y); else
		 * vJoined.setImageResource(R.drawable.widget_bar_favorite_n);
		 */
		// 显示评论数
		if (recruitDetail.getReplies() > 0) {
			bv_comment.setText(recruitDetail.getReplies() + "");
			bv_comment.show();
		} else {
			bv_comment.setText("");
			bv_comment.hide();
		}

		Log.i("ac", "rd init view complete");

		/*
		 * lvReplyAdapter = new ListViewReplyAdapter(this, lvReplyData,
		 * R.layout.comment_listitem);
		 */
	}

	private void saveUserReplyTopic() {
		// 添加用户帖子
		BBSTopic topic = new BBSTopic();
		topic.setTopicID(recruitDetail.getTopicID());
		topic.setCreatedTime(recruitDetail.getCreatedTime());
		topic.setTitle(recruitDetail.getCompany().getCompanyName() + "校园招聘");
		topic.setStatus(1);
		appContext.commentPubAfter(topic);
		new Thread() {
			@Override
			public void run() {
				appContext.saveUserTopic(recruitDetail.getTopicID(),
						recruitDetail.getCompany().getCompanyName(),
						recruitDetail.getCreatedTime());
			}

		}.start();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("rd", "comments pub handler");
		if (requestCode == UIHelper.REQUEST_CODE_FOR_TUTORIAL_RECRUIT_DETAIL) {
			appContext.setTutorialRecruitDetail();
			return;
		}
		if (resultCode != RESULT_OK)
			return;
		if (data == null)
			return;
		// viewSwitch(VIEWSWITCH_TYPE_COMMENTS);// 跳到评论列表
		BBSReply reply = (BBSReply) data
				.getSerializableExtra("COMMENT_SERIALIZABLE");
		reply.setStatus(1);
		lvReplyData.add(reply);
		saveUserReplyTopic();

		// 显示评论数
		int count = recruitDetail.getReplies() + 1;
		recruitDetail.setReplies(count);
		bv_comment.setText(count + "");
		bv_comment.show();
		Log.i("rd", "comments pub handler complete");
		/*
		 * bv_comment.setText(count + ""); bv_comment.show();
		 */
	}

	private View.OnClickListener commentpubClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (!appContext.isLogin()) {
				UIHelper.showLoginDialog(RecruitDetailActivity.this);
				return;
			}
			_content = mFootEditer.getText().toString();
			if (StringUtils.isEmpty(_content)) {
				UIHelper.ToastMessage(v.getContext(), "请输入评论内容");
				return;
			}
			if (!appContext.isLogin()) {
				UIHelper.redirectToLogin(RecruitDetailActivity.this);
				return;
			}

			mProgress = ProgressDialog.show(v.getContext(), null, "发表中···",
					true, true);

			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {

					if (mProgress != null)
						mProgress.dismiss();

					if (msg.what == 1) {
						Log.i("ac", "click");
						BBSReply reply = (BBSReply) msg.obj;
						UIHelper.ToastMessageCommentSucess(RecruitDetailActivity.this);
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
						lvReplyData.add(reply);
						if (getCommentsFragment().isVisible()) {
							getCommentsFragment().redrawView();
						}
						Log.i("ac", "test");
						// 显示评论数
						int count = recruitDetail.getReplies() + 1;
						recruitDetail.setReplies(count);
						bv_comment.setText(count + "");
						bv_comment.show();
						// 清除之前保存的编辑内容
						appContext.removeProperty(tempCommentKey);
						viewPager.setCurrentItem(getCommentFragmentPosition());
						saveUserReplyTopic();
					} else {
						((AppException) msg.obj)
								.makeToast(RecruitDetailActivity.this);
					}
				}
			};
			new Thread() {
				public void run() {
					Message msg = new Message();
					try {
						// 发表评论
						BBSReply reply = appContext.addReply(
								recruitDetail.getTopicID(), _content);
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

	private View.OnClickListener shareClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (recruitDetail == null) {
				UIHelper.ToastMessage(v.getContext(),
						R.string.msg_read_detail_fail);
				return;
			}
			// 分享到微博
			UIHelper.showShareRecruitDialog(RecruitDetailActivity.this,
					recruitDetail);
		}
	};

	private View.OnClickListener detailClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			// 切换到详情
			viewPager.setCurrentItem(0);
			// viewSwitch(VIEWSWITCH_TYPE_DETAIL);
		}
	};

	private View.OnClickListener commentlistClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			// 切换到评论
			viewPager.setCurrentItem(3);
			// viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
		}
	};

	private View.OnClickListener joinedClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (!appContext.isLogin()) {
				UIHelper.showLoginDialog(RecruitDetailActivity.this);
				vJoined.setChecked(false);
				return;
			}
			Log.i("test", "click");
			if (recruitDetail.getIsJoined() == 1) {
				recruitDetail.setIsJoined(0);
				// vJoined.setImageResource(R.drawable.widget_bar_favorite_n);
				// vJoined.setImageDrawable(getResources().getDrawable(
				// R.drawable.widget_bar_favorite_y));
				UIHelper.ToastMessage(RecruitDetailActivity.this, "取消收藏");
				appContext.recruitDetailJoin(recruitDetail, false);
			} else {
				// vJoined.setImageResource(R.drawable.widget_bar_favorite_y);
				// vJoined.setImageDrawable(getResources().getDrawable(
				// R.drawable.widget_bar_favorite_n));
				recruitDetail.setIsJoined(1);
				UIHelper.ToastMessage(RecruitDetailActivity.this, "收藏成功");
				appContext.recruitDetailJoin(recruitDetail, true);
			}
			new Thread() {
				public void run() {
					try {
						if (recruitDetail.getIsJoined() == 1) {
							appContext.joinRecruit(
									recruitDetail.getRecruitID(), true);
							// vJoined.setImageResource(R.drawable.widget_bar_favorite2);
							Log.i("test", "click");
						} else {
							appContext.joinRecruit(
									recruitDetail.getRecruitID(), false);
							// vJoined.setImageResource(R.drawable.widget_bar_favorite);
							Log.i("test", "click2");
						}
					} catch (AppException e) {
						
					}

				}
			}.start();
		}
	};

	/*
	 * @Override public boolean dispatchTouchEvent(MotionEvent event) {
	 * 
	 * if (isAllowFullScreen()) { gd.onTouchEvent(event); }
	 * 
	 * gd.onTouchEvent(event); return super.dispatchTouchEvent(event); }
	 */

}
