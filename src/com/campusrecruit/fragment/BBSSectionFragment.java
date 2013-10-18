package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.adapter.ListViewBBSSectionAdapter;
import com.campusrecruit.adapter.ListViewTopicsAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSSection;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.pcncad.campusRecruit.R;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class BBSSectionFragment extends SearchableFragment implements
		SearchView.OnQueryTextListener {

	private MenuItem sortItem = null;
	private MenuItem famousItem = null;
	private int icon_res = R.drawable.ic_action_sort;
	private int icon_famous_res = R.drawable.header_collect;

	private int actionType;
	public static int displayFlag = 0; // 0 favorate 1:all 2:famous

	private PullToRefreshListView pvBBSSections = null;
	// pvFooter
	private View pvFooter;
	private TextView pvFooterTextView;
	private ProgressBar pvFooterProgressBar;

	private Handler lvBBSSearchHandler;

	private int sortby = AppConfig.SORT_BY_CREATED_TIME;

	private String saveFooterText = null;
	private SearchView mSearchView = null;
	private List<BBSSection> savedList = new ArrayList<BBSSection>();
	private String _searchStr;
	private boolean manualRefresh = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("main", "section fragment init");
		super.onCreateView(inflater, container, savedInstanceState);
		View bbssection_view = inflater.inflate(R.layout.bbssection_list, null);
		initLoadingView(bbssection_view);
		initSearchReturnBackListener(new ReturnBackListener());

		pvBBSSections = (PullToRefreshListView) bbssection_view
				.findViewById(R.id.bbssection_listview);
		pvFooter = inflater.inflate(R.layout.listview_footer, null);
		pvFooterTextView = (TextView) pvFooter
				.findViewById(R.id.listview_foot_more);
		pvFooterProgressBar = (ProgressBar) pvFooter
				.findViewById(R.id.listview_foot_progress);
		initBBSSectionListView(pvBBSSections);
		pvBBSSections.addFooterView(pvFooter);
		if (saveFooterText != null)
			pvFooterTextView.setText(saveFooterText);
		if (!appContext.getLvBBSSectionList().isEmpty()) {
			appContext.getLvSectionAdapter().setData(
					appContext.getLvBBSSectionList());
			appContext.getLvSectionAdapter().notifyDataSetChanged();
		}

		Log.i("main", "section fragment complete");
		return bbssection_view;
	}

	private class ReturnBackListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (!savedList.isEmpty()) {
				hideLoadProgress(pvBBSSections);
				UIHelper.ToastMessage(getActivity(),
						getString(R.string.search_return, Toast.LENGTH_SHORT));
				appContext.setLvBBSSectionList(savedList);
				appContext.getLvSectionAdapter().setData(
						appContext.getLvBBSSectionList());
				appContext.getLvSectionAdapter().notifyDataSetChanged();
			}
		}

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		Log.i("bbssection", "prepare");
		if (sortItem != null) {
			sortItem.setIcon(icon_res);
		}
		if (famousItem != null) {
			famousItem.setIcon(icon_famous_res);
		}
		Log.i("bbssection", "prepare complete");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("section", "oncreate");
		super.onCreate(savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
		if (appContext == null) {
			Log.i("bug", "bbssection appcontext is null");
			return;
		}
		appContext.setLvSectionAdapter(new ListViewBBSSectionAdapter(
				getActivity(),appContext, R.layout.bbssection_item));
		Log.i("bbssection", "set adapteer");
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		Log.i("bbssection", "create menu");
		inflater.inflate(R.menu.menu_search_sort_show, menu);
		sortItem = menu.findItem(R.id.menu_sort_bbs);
		famousItem = menu.findItem(R.id.menu_bbs_show_bbs);
		Log.i("bbssection", "complete menu");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_sort_bbs:
			View memuItemView1 = getActivity().findViewById(R.id.menu_sort_bbs);
			PopupMenu popup1 = new PopupMenu(getActivity(), memuItemView1);
			MenuInflater inflater1 = popup1.getMenuInflater();
			inflater1.inflate(R.menu.sort_bbs_section, popup1.getMenu());
			popup1.getMenu().getItem(sortby).setChecked(true);
			popup1.setOnMenuItemClickListener(menuItemClickListener1);
			popup1.show();
			break;
		case R.id.menu_bbs_show_bbs:
			View memuItemView2 = getActivity().findViewById(
					R.id.menu_bbs_show_bbs);
			PopupMenu popup2 = new PopupMenu(getActivity(), memuItemView2);
			MenuInflater inflater2 = popup2.getMenuInflater();
			inflater2.inflate(R.menu.sub_menu_bbs_show, popup2.getMenu());
			popup2.getMenu().getItem(displayFlag).setChecked(true);
			popup2.setOnMenuItemClickListener(menuItemClickListener2);
			popup2.show();
		}
		Log.i("bbssection", "slected");
		return super.onOptionsItemSelected(item);
	}

	private void ThreadSearch(final String companyName) {
		Log.i("bbssection", "thread search");
		beginSearch(pvBBSSections);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					List<BBSSection> list = appContext
							.getBBSSectionListByCompanyName(companyName);
					msg.obj = list;
					msg.what = list.size();
				} catch (AppException e) {
					msg.what = -1;
					msg.obj = e;
				}
				lvBBSSearchHandler.sendMessage(msg);
			}
		}.start();
	}

	private OnMenuItemClickListener menuItemClickListener1 = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			actionType = UIHelper.LISTVIEW_ACTION_SORT;
			// TODO Auto-generated method stub
			switch (item.getItemId()) {
			/*case R.id.sort_by_time:
				if (sortby == AppConfig.SORT_BY_CREATED_TIME) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				icon_res = R.drawable.ic_action_sort;
				sortItem.setIcon(R.drawable.ic_action_sort);
				sortby = AppConfig.SORT_BY_CREATED_TIME;
				manualRefresh = true;
				pvBBSSections.clickRefresh();
				return true;*/
			case R.id.sort_by_join_bs:
				if (sortby == AppConfig.SORT_BY_JOINS_BS) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				icon_res = R.drawable.ic_menu_join;
				sortItem.setIcon(R.drawable.ic_menu_join);
				Log.i("sort", "bbs join");
				sortby = AppConfig.SORT_BY_JOINS_BS;
				manualRefresh = true;
				pvBBSSections.clickRefresh();
				return true;
			case R.id.sort_by_reply_bs:
				if (sortby == AppConfig.SORT_BY_REPLYS_BS) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				icon_res = R.drawable.ic_menu_reply;
				sortItem.setIcon(R.drawable.ic_menu_reply);
				Log.i("sort", "bbs reply");
				sortby = AppConfig.SORT_BY_REPLYS_BS;
				manualRefresh = true;
				pvBBSSections.clickRefresh();
				return true;
			/*case R.id.sort_by_click:
				if (sortby == AppConfig.SORT_BY_CLICKS) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				icon_res = R.drawable.ic_menu_click;
				sortItem.setIcon(R.drawable.ic_menu_click);
				Log.i("sort", "bbs click");
				manualRefresh = true;
				sortby = AppConfig.SORT_BY_CLICKS;
				pvBBSSections.clickRefresh();
				return true;*/
			default:
				return false;
			}
		}

	};

	@Override
	public void onStop() {
		super.onStop();
		this.saveFooterText = pvFooterTextView.getText().toString();

	}

	private OnMenuItemClickListener menuItemClickListener2 = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.bbs_menu_show_collection:
				Log.i("bbs_show", "show collection");
				if (displayFlag == 0) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				icon_famous_res = R.drawable.header_collect;
				Log.i("famous", "famous");
				famousItem.setIcon(R.drawable.header_collect);
				displayFlag = 0;
				manualRefresh = true;
				pvBBSSections.clickRefresh();
				return true;
			case R.id.bbs_menu_show_all:
				Log.i("bbs_show", "show all");
				if (displayFlag == 1) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				icon_famous_res = R.drawable.header_all;
				Log.i("famous", "common");
				famousItem.setIcon(R.drawable.header_all);
				displayFlag = 1;
				manualRefresh = true;
				pvBBSSections.clickRefresh();
				return true;
			case R.id.bbs_menu_show_famous:
				Log.i("bbs_show", "show famous");
				if (displayFlag == 2) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				icon_famous_res = R.drawable.header_famous;
				Log.i("famous", "common");
				famousItem.setIcon(R.drawable.header_famous);
				displayFlag = 2;
				manualRefresh = true;
				pvBBSSections.clickRefresh();

				return true;
			default:
				return false;
			}
		}

	};

/*	private void setupSearchView(MenuItem searchItem) {

		if (isAlwaysExpanded()) {
			mSearchView.setIconifiedByDefault(false);
		} else {
			searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}

		mSearchView.setOnQueryTextListener(this);
	}*/

	protected boolean isAlwaysExpanded() {
		return false;
	}

	public void loadData(int pageIndex, int action) {
		Log.i("bbssection", "load data");
		if (lvBBSSearchHandler == null)
			initHandler();
		actionType = action;
		Log.i("bbssection", "begin load data");
		new BBSSectionAsyncTask().execute(pageIndex);
	}

	private void initHandler() {
		lvBBSSearchHandler = new Handler() {
			public void handleMessage(Message msg) {
				pvFooter.setVisibility(View.GONE);
				savedList.clear();
				savedList.addAll(appContext.getLvBBSSectionList());
				if (msg.what == 0) {
					completeSearchWithEmpty(pvBBSSections, _searchStr);
					return;
				}
				if (msg.what >= 1) {
					List<BBSSection> list = (List<BBSSection>) msg.obj;
					Log.i("test", "search ccomplete");
					pvBBSSections.setTag(UIHelper.LISTVIEW_DATA_FULL);
					for (BBSSection c1 : list) {
						for (BBSSection c2 : appContext.getLvBBSSectionList()) {
							if (c1.getSectionID() == c2.getSectionID()) {
								if (c2.getIsJoined() == 1)
									c1.setIsJoined(1);
								break;
							}
						}
					}
					appContext.getLvBBSSectionList().clear();
					completeSearch(pvBBSSections, list.size(), _searchStr);
					appContext.setLvBBSSectionList(list);
					appContext.getLvSectionAdapter().setData(list);
					appContext.getLvSectionAdapter().notifyDataSetChanged();
				} else {
					((AppException) msg.obj).makeToast(getActivity());
					completeSearchWithError(pvBBSSections);
				}
			}
		};
	}

	private void initBBSSectionListView(PullToRefreshListView listView) {

		listView.setAdapter(appContext.getLvSectionAdapter());
		listView.setOnRefreshListener(new refreshListener());
		listView.setOnScrollListener(new bottomScrollListener());
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i("fragment", "click");
				// 点击头部、底部栏无效
				if (position == 0 || view == pvFooter)
					return;

				BBSSection section = null;
				TextView tv = (TextView) view
						.findViewById(R.id.bbssection_company_name);
				section = (BBSSection) tv.getTag();
				if (section == null)
					return;
				// 跳转到讨论区主题列表
				Log.i("fragment", "show TopicList");
				UIHelper.showTopicList(view.getContext(), section);
			}
		});

		if (!appContext.getLvBBSSectionList().isEmpty()) {
			Log.i("bug", appContext.getLvBBSSectionList().size() + "");
			pvFooterProgressBar.setVisibility(View.GONE);
			if (saveFooterText != null)
				pvFooterTextView.setText(saveFooterText);
			else {
				pvFooterTextView.setText(R.string.load_more);
				pvBBSSections.setTag(UIHelper.LISTVIEW_DATA_MORE);
			}
			appContext.getLvCareerTalkListAdapter().setData(
					appContext.getLvCareerTalkList());
			appContext.getLvCareerTalkListAdapter().notifyDataSetChanged();
		}

	}

	/*
	 * * public void setFooterTextView(String text) { if
	 * (pvFooterTextView.getText().toString().equals(R.string.bbs_section_null))
	 * pvFooterTextView.setText(R.string.load_full); }
	 */

	private class refreshListener implements
			PullToRefreshListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			Log.i("test", "load data refresh");
			if (manualRefresh) {
				loadData(0, UIHelper.LISTVIEW_ACTION_SORT);
			} else {
				loadData(0, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		}

	}

	private class bottomScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			pvBBSSections.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			Log.i("rf", "onScrollStateChanged");
			pvBBSSections.onScrollStateChanged(view, scrollState);
			if (appContext.getLvBBSSectionList().isEmpty())
				return;

			boolean scrollEnd = false;
			try {
				if (view.getLastVisiblePosition() == view
						.getPositionForView(pvFooter)) {
					scrollEnd = true;
				}
			} catch (Exception e) {
				scrollEnd = false;
			}
			int footerState = StringUtils.toInt(pvBBSSections.getTag());
			if (scrollEnd && (footerState == UIHelper.LISTVIEW_DATA_MORE)) {
				pvBBSSections.setTag(UIHelper.LISTVIEW_DATA_LOADING);
				pvFooterTextView.setText(R.string.load_ing);
				pvFooterProgressBar.setVisibility(View.VISIBLE);
				int pageIndex = appContext.getLvBBSSectionList().size()
						/ AppConfig.PAGE_SIZE;
				loadData(pageIndex, UIHelper.LISTVIEW_ACTION_SCROLL);
			}
		}

	}

	private class BBSSectionAsyncTask extends
			AsyncTask<Integer, Void, List<BBSSection>> {
		// page index
		@Override
		protected List<BBSSection> doInBackground(Integer... params) {
			try {
				Log.i("test","do in background");
				List<BBSSection> list = appContext.getBBSSectionList(params[0],
						displayFlag, sortby);
				return list;
			} catch (AppException e) {
				Log.i("test","on do error");
				pvFooterTextView.setText(R.string.load_error);
				pvBBSSections.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
				e.makeToast(getActivity());
				// if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH)
				pvBBSSections.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
				if (pvFooterProgressBar != null)
					pvFooterProgressBar.setVisibility(View.GONE);
				hideLoadProgressWithError(pvBBSSections);
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<BBSSection> list) {
			Log.i("test","on postcreate");
			pvFooterProgressBar.setVisibility(View.GONE);
			pvBBSSections.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
			if (actionType == UIHelper.LISTVIEW_ACTION_SORT) {
				hideLoadProgress(pvBBSSections);
				appContext.getLvBBSSectionList().clear();
			}
			// 网络加载出错，加载的本地数据
			if (!list.isEmpty() && appContext.sectionLoadFromDisk) {
				UIHelper.ToastMessage(getActivity(),
						getString(R.string.load_fail));
				appContext.sectionLoadFromDisk = false;
				appContext.setLvBBSSectionList(list);
				pvFooterTextView.setText(R.string.load_error);
				pvBBSSections.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
			} else {
				if (list.isEmpty()) {
					appContext.setLvBBSSectionList(new ArrayList<BBSSection>());
					pvFooterTextView.setText(R.string.bbs_section_null);
					return;
				} else {
					Log.i("bbssection", "list size is " + list.size());
					if (list.size() < AppConfig.PAGE_SIZE) {
						pvFooterTextView.setText(R.string.load_full);
						pvBBSSections.setTag(UIHelper.LISTVIEW_DATA_FULL);
					} else if (list.size() >= AppConfig.PAGE_SIZE) {
						pvFooterTextView.setText(R.string.load_more);
						pvBBSSections.setTag(UIHelper.LISTVIEW_DATA_MORE);
					}
					if (actionType == UIHelper.LISTVIEW_ACTION_INIT) {
						// lvBBSSectionSum = list.size();
						appContext.setLvBBSSectionList(list);
					} else if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH
							|| (actionType == UIHelper.LISTVIEW_ACTION_SORT)) {
						// lvBBSSectionSum = list.size();
						appContext.setLvBBSSectionList(list);
						pvBBSSections.setSelection(0);
						pvBBSSections.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
						UIHelper.ToastMessage(getActivity(),
								getString(R.string.load_complete, "讨论组"));
					} else {
						// lvBBSSectionSum += list.size();
						appContext.getLvBBSSectionList().addAll(list);
					}
				}
			}
			Log.i("test", "bbssection");
			if (appContext.getLvSectionAdapter() != null
					&& !appContext.getLvBBSSectionList().isEmpty()) {
				appContext.getLvSectionAdapter().setData(
						appContext.getLvBBSSectionList());
				appContext.getLvSectionAdapter().notifyDataSetChanged();
			}
			Log.i("test", "bbssection complete");
		}

		@Override
		protected void onPreExecute() {
			Log.i("bbssection", "preexecute");
			hideSearchView();
			if (actionType == UIHelper.LISTVIEW_ACTION_SORT) {
				showLoadProgress(pvBBSSections);
			} else if (actionType == UIHelper.LISTVIEW_ACTION_SCROLL) {
				pvFooterProgressBar.setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		Log.i("search", "bbs " + query);
		final String companyName = query;
		_searchStr = query;
		beginSearch(pvBBSSections);
		ThreadSearch(companyName);
		mSearchView.clearFocus();
		return false;
	}

}