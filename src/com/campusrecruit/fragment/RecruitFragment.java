package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.R.integer;
import android.annotation.SuppressLint;
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
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.adapter.ListViewRecruitAdapter;
import com.campusrecruit.adapter.ListViewRecruitAdapter;
import com.campusrecruit.adapter.ListViewRecruitFavorateAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.pcncad.campusRecruit.R;

@SuppressLint("ValidFragment")
public class RecruitFragment extends ListFragment implements
		SearchView.OnQueryTextListener {

	private PullToRefreshListView pvRecruits = null;

	private MenuItem sortItem = null;
	// private MenuItem famousItem = null;
	int icon_res = R.drawable.ic_action_sort;
	// int icon_famous_res = R.drawable.header_all;

	private View pvFooter;
	private TextView pvFooterTextView;
	private ProgressBar pvFooterProgressBar;

	private int lvRecruitSum = 0;

	private Handler lvRecruitHandler = null;
	private Handler lvRecruitDiskHandler = null;
	private Handler lvRecruitSearchHandler = null;

	private boolean isSearch = false;

	private List<Recruit> savedList = new ArrayList<Recruit>();
	private SearchView mSearchView = null;
	private String _searchStr;

	// private String saveFooterText = null;

	private int sortby = AppConfig.SORT_BY_CREATED_TIME;

	// TODO 应该有更好解决办法
	// 如果是用户向上拉更新数据则不需要清空老数据，如果是通过sort方法则需要清空
	// false代表用户 true代表手动sort, 默认是用户
	private boolean manualRefresh = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View recruit_view = inflater.inflate(R.layout.recruit_list, null);
		initLoadingView(recruit_view);
		initSearchReturnBackListener(new ReturnBackListener());

		pvRecruits = (PullToRefreshListView) recruit_view
				.findViewById(R.id.recruit_listview);
		pvFooter = inflater.inflate(R.layout.listview_footer, null);
		pvFooterTextView = (TextView) pvFooter
				.findViewById(R.id.listview_foot_more);
		pvFooterProgressBar = (ProgressBar) pvFooter
				.findViewById(R.id.listview_foot_progress);
		pvRecruits.addFooterView(pvFooter);
		initListHeaderView(inflater, pvRecruits, true);

		if (!appContext.getLvRecruitList().isEmpty()) {
			appContext.getLvRecruitListAdapter().setData(
					appContext.getLvRecruitList());
			appContext.getLvRecruitListAdapter().notifyDataSetChanged();
		}
		initRecruitListView(pvRecruits);
		return recruit_view;
	}

	private class ReturnBackListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (!savedList.isEmpty()) {
				hideLoadProgress(pvRecruits);
				UIHelper.ToastMessage(getActivity(),
						getString(R.string.search_return, Toast.LENGTH_SHORT));
				appContext.setLvRecruitList(savedList);
				appContext.getLvRecruitListAdapter().setData(
						appContext.getLvRecruitList());
				appContext.getLvRecruitListAdapter().notifyDataSetChanged();
			} else {
				loadRecruitDataFromDisk();
			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
		if (appContext.getLvRecruitListAdapter() == null) {
			appContext.setLvRecruitListAdapter(new ListViewRecruitAdapter(
					getActivity(), appContext, R.layout.recruit_item));
		}
		initHandler();
		setHasOptionsMenu(true);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (sortItem != null) {
			sortItem.setIcon(icon_res);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * if (sortItem != null) { sortItem.setIcon(icon_res);
		 * famousItem.setIcon(icon_famous_res); }
		 */
		sortby = AppConfig.SORT_BY_CREATED_TIME;
		if (sortItem != null)
			sortItem.setIcon(icon_res);
		View menuItemView = getActivity().findViewById(R.id.menu_sort);
		if (menuItemView != null) {
			PopupMenu popup = new PopupMenu(getActivity(), menuItemView);
			if (popup != null) {
				MenuInflater inflater = popup.getMenuInflater();
				if (inflater != null) {
					inflater.inflate(R.menu.sort, popup.getMenu());
					popup.getMenu().getItem(sortby).setChecked(true);
				}
			}
		}

		/*
		 * famousFlag = false; if (famousItem != null)
		 * famousItem.setIcon(icon_famous_res); View memuItemView1 =
		 * getActivity().findViewById(R.id.menu_famous); if (memuItemView1 !=
		 * null) { PopupMenu popup1 = new PopupMenu(getActivity(),
		 * memuItemView1); if (popup1 != null) { MenuInflater inflater1 =
		 * popup1.getMenuInflater(); if (inflater1 != null) {
		 * inflater1.inflate(R.menu.sub_menu_famous, popup1.getMenu());
		 * popup1.getMenu().getItem(famousFlag == true ? 0 : 1)
		 * .setChecked(true); } } }
		 */
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_search_sort, menu);
		sortItem = menu.findItem(R.id.menu_sort);
		// famousItem = menu.findItem(R.id.menu_famous);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) searchItem.getActionView();
		setupSearchView(searchItem);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_sort:
			View memuItemView = getActivity().findViewById(R.id.menu_sort);
			PopupMenu popup = new PopupMenu(getActivity(), memuItemView);
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(R.menu.sort, popup.getMenu());
			popup.getMenu().getItem(sortby).setChecked(true);
			popup.setOnMenuItemClickListener(menuItemClickListener);
			popup.show();
			break;
		/*
		 * case R.id.menu_famous: View memuItemView1 =
		 * getActivity().findViewById(R.id.menu_famous); PopupMenu popup1 = new
		 * PopupMenu(getActivity(), memuItemView1); MenuInflater inflater1 =
		 * popup1.getMenuInflater(); inflater1.inflate(R.menu.sub_menu_famous,
		 * popup1.getMenu()); popup1.getMenu().getItem(famousFlag == true ? 0 :
		 * 1) .setChecked(true);
		 * popup1.setOnMenuItemClickListener(menuItemClickListener1);
		 * popup1.show();
		 */
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		Log.i("search", "recruit " + query);
		final String companyName = query;
		_searchStr = query;
		/*
		 * pvRecruits.setSelection(0); pvRecruits.progressHeaderRefresh();
		 */
		beginSearch(pvRecruits);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Log.i("test", "begin search");
					List<Recruit> list = appContext
							.getRecruitListByCompanyName(companyName);

					Log.i("test", "aaaa");
					// Log.i("fm", "got re list " + list.size());
					msg.obj = list;
					msg.what = list.size();
				} catch (AppException e) {

					msg.what = -1;
					msg.obj = e;
				}
				lvRecruitSearchHandler.sendMessage(msg);
			}
		}.start();
		mSearchView.clearFocus();
		return false;
	}

	public void ThreadSearch(final int pageIndex, final int action) {
		isSearch = false;
		hideSearchView();
		isloading = true;
		if (lvRecruitSum < (pageIndex + 1) * AppConfig.PAGE_SIZE) {
			lvRecruitSum = (pageIndex + 1) * AppConfig.PAGE_SIZE;
		}
		if (action == UIHelper.LISTVIEW_ACTION_SORT) {
			showLoadProgress(pvRecruits);
		} else if (action == UIHelper.LISTVIEW_ACTION_SCROLL) {
			pvFooterProgressBar.setVisibility(View.VISIBLE);
		}
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					List<Recruit> list = appContext.getRecruitListFromInternet(
							pageIndex, sortby, isFamous, selectProvinceList,
							selectCompanyTypeList, selectCompanyIndustryList,
							selectSourceList);
					msg.obj = list;
					msg.what = list.size();
					msg.arg1 = action;
				} catch (AppException e) {
					msg.what = -1;
					msg.obj = e;
				}
				lvRecruitHandler.sendMessage(msg);
			}
		}.start();
	}

	private OnMenuItemClickListener menuItemClickListener = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.sort_by_time:
				if (sortby == AppConfig.SORT_BY_CREATED_TIME) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				manualRefresh = true;
				icon_res = R.drawable.ic_action_add_sort;
				sortItem.setIcon(R.drawable.ic_action_sort);
				sortby = AppConfig.SORT_BY_CREATED_TIME;
				pvRecruits.clickRefresh();
				return true;
			case R.id.sort_by_join:
				if (sortby == AppConfig.SORT_BY_JOINS) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				manualRefresh = true;
				icon_res = R.drawable.ic_menu_join;
				sortItem.setIcon(R.drawable.ic_menu_join);
				sortby = AppConfig.SORT_BY_JOINS;
				pvRecruits.clickRefresh();
				return true;
			case R.id.sort_by_reply:
				if (sortby == AppConfig.SORT_BY_REPLYS) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				manualRefresh = true;
				icon_res = R.drawable.ic_menu_reply;
				sortItem.setIcon(R.drawable.ic_menu_reply);
				Log.i("sort", "careertalk reply");
				sortby = AppConfig.SORT_BY_REPLYS;
				pvRecruits.clickRefresh();
				return true;
			case R.id.sort_by_click:
				if (sortby == AppConfig.SORT_BY_CLICKS) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				manualRefresh = true;
				icon_res = R.drawable.ic_menu_click;
				sortItem.setIcon(R.drawable.ic_menu_click);
				Log.i("sort", "careertalk click");
				sortby = AppConfig.SORT_BY_CLICKS;
				pvRecruits.clickRefresh();
				return true;
			default:
				return false;
			}
		}

	};

	/*
	 * private OnMenuItemClickListener menuItemClickListener1 = new
	 * OnMenuItemClickListener() {
	 * 
	 * @Override public boolean onMenuItemClick(MenuItem item) { switch
	 * (item.getItemId()) { case R.id.sort_by_famous: if (famousFlag) {
	 * UIHelper.ToastMessage(getActivity(), getString(R.string.sort_same),
	 * Toast.LENGTH_SHORT); return true; } icon_famous_res =
	 * R.drawable.header_famous; Log.i("famous", "famous");
	 * famousItem.setIcon(R.drawable.header_famous); famousFlag = true;
	 * manualRefresh = true; pvRecruits.clickRefresh(); return true; case
	 * R.id.sort_by_common: if (!famousFlag) {
	 * UIHelper.ToastMessage(getActivity(), getString(R.string.sort_same),
	 * Toast.LENGTH_SHORT); return true; } icon_famous_res =
	 * R.drawable.header_all; Log.i("famous", "common");
	 * famousItem.setIcon(R.drawable.header_all); famousFlag = false;
	 * manualRefresh = true; pvRecruits.clickRefresh(); return true; default:
	 * return false; } }
	 * 
	 * };
	 */

	private void setupSearchView(MenuItem searchItem) {

		if (isAlwaysExpanded()) {
			mSearchView.setIconifiedByDefault(false);
		} else {
			searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}

		mSearchView.setOnQueryTextListener(this);
	}

	protected boolean isAlwaysExpanded() {
		return false;
	}

	/*
	 * @Override public void onStop() { super.onStop(); if (pvFooterTextView !=
	 * null) { this.saveFooterText = pvFooterTextView.getText().toString(); }
	 * 
	 * }
	 */

	private void initRecruitListView(PullToRefreshListView listView) {

		Log.i("eee", "init start");
		listView.setAdapter(appContext.getLvRecruitListAdapter());
		listView.setOnRefreshListener(new refreshListener());
		listView.setOnScrollListener(new bottomScrollListener());
		if (!appContext.getLvRecruitList().isEmpty()) {
			Log.i("bug", appContext.getLvRecruitList().size() + "");
			pvFooterProgressBar.setVisibility(View.GONE);
			/*
			 * if (saveFooterText != null)
			 * pvFooterTextView.setText(saveFooterText);
			 */
			pvFooterTextView.setText(R.string.load_more);
			pvRecruits.setTag(UIHelper.LISTVIEW_DATA_MORE);
			appContext.getLvRecruitListAdapter().setData(
					appContext.getLvRecruitList());
			appContext.getLvRecruitListAdapter().notifyDataSetChanged();
		}

	}

	private void initHandler() {
		lvRecruitHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				/*
				 * Log.i("fm", "handle recruit internet" + msg.what);
				 * Log.i("fm", "handle recruit disk" +
				 * appContext.getLvRecruitList().size());
				 */
				pvFooter.setVisibility(View.VISIBLE);
				if (msg.what >= 0) {
					handleRecruitData((List<Recruit>) msg.obj, msg.arg1, false);
					if (msg.what < AppConfig.PAGE_SIZE) {
						pvRecruits.setTag(UIHelper.LISTVIEW_DATA_FULL);
						pvFooterTextView.setText(getString(R.string.load_full));
					} else if (msg.what == AppConfig.PAGE_SIZE) {
						pvRecruits.setTag(UIHelper.LISTVIEW_DATA_MORE);
						pvFooterTextView.setText(getString(R.string.load_more));
						Log.i("fm", msg.what + "handle recruit load more");
					}
				} else if (msg.what == -1) {
					Log.i("bug", "what the fuck recruit handle aaaaaa");
					pvRecruits.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					pvFooterTextView
							.setText(getString(R.string.load_over_time));
					((AppException) msg.obj).makeToast(getActivity());
					pvRecruits
							.onRefreshComplete(getString(R.string.pull_to_refresh_update)
									+ new Date().toLocaleString());
				}
				if (appContext.getLvRecruitListAdapter().getCount() == 0) {
					pvRecruits.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					pvFooterTextView.setText(getString(R.string.load_empty));
				}
				pvFooterProgressBar.setVisibility(View.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH
						|| (msg.arg1 == UIHelper.LISTVIEW_ACTION_SORT)) {
					pvRecruits
							.onRefreshComplete(getString(R.string.pull_to_refresh_update)
									+ new Date().toLocaleString());
					pvRecruits.setSelection(0);
				}

				// pvRecruits.addFooterView(vFooter);
			}

		};

		lvRecruitSearchHandler = new Handler() {
			public void handleMessage(Message msg) {

				pvFooter.setVisibility(View.GONE);
				if (!isSearch) {
					savedList.clear();
					savedList.addAll(appContext.getLvRecruitList());
				}
				isSearch = true;
				if (msg.what == 0) {
					completeSearchWithEmpty(pvRecruits, _searchStr);
					return;
				}
				if (msg.what >= 1) {
					List<Recruit> list = (List<Recruit>) msg.obj;
					Log.i("test", "search ccomplete");
					pvRecruits.setTag(UIHelper.LISTVIEW_DATA_FULL);
					for (Recruit c1 : list) {
						for (Recruit c2 : appContext.getLvRecruitList()) {
							if (c1.getRecruitID() == c2.getRecruitID()) {
								if (c2.getIsJoined() == 1)
									c1.setIsJoined(1);
								break;
							}
						}
					}
					appContext.getLvRecruitList().clear();
					completeSearch(pvRecruits, list.size(), _searchStr);
					appContext.setLvRecruitList(list);
					appContext.getLvRecruitListAdapter().setData(list);
					appContext.getLvRecruitListAdapter().notifyDataSetChanged();
				} else {
					((AppException) msg.obj).makeToast(getActivity());
					completeSearchWithError(pvRecruits);
				}
			}
		};

	}

	private class refreshListener implements
			PullToRefreshListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			if (manualRefresh) {
				ThreadSearch(0, UIHelper.LISTVIEW_ACTION_SORT);
				manualRefresh = false;
				lvRecruitSum = AppConfig.PAGE_SIZE;
			} else {
				ThreadSearch(0, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		}

	}

	private class bottomScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			pvRecruits.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			Log.i("rf", "onScrollStateChanged");
			pvRecruits.onScrollStateChanged(view, scrollState);
			if (appContext.getLvRecruitList().isEmpty())
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
			int footerState = StringUtils.toInt(pvRecruits.getTag());
			if (scrollEnd && (footerState == UIHelper.LISTVIEW_DATA_MORE)) {
				// Log.i("test", "scorllEnd datamore" + lvRecruitSum);
				pvRecruits.setTag(UIHelper.LISTVIEW_DATA_LOADING);
				pvFooterTextView.setText(R.string.load_ing);
				pvFooterProgressBar.setVisibility(View.VISIBLE);

				int pageIndex = lvRecruitSum / AppConfig.PAGE_SIZE;
				ThreadSearch(pageIndex, UIHelper.LISTVIEW_ACTION_SCROLL);
			}
		}

	}

	public void setViewFooter(String text) {
		pvFooterProgressBar.setVisibility(View.GONE);
		pvFooterTextView.setText(text);
	}

	public void loadRecruitDataFromDisk() {
		// no need to load disk
		if (appContext.recruitIsInit)
			return;
		/*
		 * Log.i("recruit bug", "load recruit data from disk!!!!!!!!!!!!!" +
		 * lvRecruitSum);
		 */
		lvRecruitDiskHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i("ddd", "handle recruit data");
				if (msg.what > 0) {
					handleRecruitData((List<Recruit>) msg.obj,
							UIHelper.LISTVIEW_ACTION_REFRESH, true);
					appContext.getLvRecruitListAdapter().setData(
							appContext.getLvRecruitList());
					appContext.getLvRecruitListAdapter().notifyDataSetChanged();

				}
				Log.i("ddd", "begin load recruit data");
				pvRecruits.clickRefresh();
			}
		};
		new Thread() {
			@Override
			public void run() {
				Message msg = new Message();
				List<Recruit> list = appContext.getRecruitListFromDisk();
				if (list != null) {
					msg.what = list.size();
					msg.obj = list;
				} else {
					msg.what = 0;
					msg.obj = null;
				}
				lvRecruitDiskHandler.sendMessage(msg);
			}
		}.start();
	}

	public void handleRecruitData(List<Recruit> list, int actionType,
			boolean isDisk) {
		if (list == null)
			return;
		hideLoadProgress(pvRecruits);
		// 如果是读的磁盘数据，或者是刷新事件，清空list
		if (isDisk || actionType == UIHelper.LISTVIEW_ACTION_SORT) {
			// lvRecruitSum = list.size();
			appContext.getLvRecruitList().clear();
			appContext.setLvRecruitList(list);
			appContext.getLvRecruitListAdapter().setData(
					appContext.getLvRecruitList());
			appContext.getLvRecruitListAdapter().notifyDataSetChanged();
			Log.i("recruit bug", "now the recruit list size is"
					+ appContext.getLvRecruitList().size());
			if (!isDisk) {
				Toast.makeText(getActivity(),
						getString(R.string.load_complete, "校园招聘"),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.i("recruit bug", "recruit disk inter list size is"
					+ appContext.getLvRecruitList().size());
			Log.i("recruit bug", "recruit inter list size is" + list.size());
			List<Recruit> newList = new ArrayList<Recruit>();
			int count = 0;
			// 计算更新数据
			if (appContext.getLvRecruitList().isEmpty()) {
				count = list.size();
				newList.addAll(list);
			} else {
				for (Recruit c1 : list) {
					boolean flag = false;
					for (Recruit c2 : appContext.getLvRecruitList()) {
						if (c1.getRecruitID() == c2.getRecruitID()) {
							if (c2.getIsJoined() == 1)
								c1.setIsJoined(1);
							c1.setStatus(0); // not new
							appContext.getLvRecruitList().remove(c2);
							newList.add(c1);
							flag = true;
							break;
						}
					}
					if (!flag) {
						newList.add(c1);
						count++;
					}
				}
			}
			if (MainActivity.RecruitCount != 0) {
				Toast.makeText(
						getActivity(),
						getString(R.string.new_data_str_toast_message, "校园招聘",
								MainActivity.RecruitCount), Toast.LENGTH_SHORT)
						.show();
				if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH) {
					MainActivity.RecruitCount = 0;
					MainActivity.bvRecruit.hide();
				}
			} else {

				if (count > 0) {
					Toast.makeText(
							getActivity(),
							getString(R.string.new_data_str_toast_message,
									"校园招聘", count), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.new_data_toast_none),
							Toast.LENGTH_SHORT).show();
				}
			}

			// lvRecruitSum += count;
			appContext.getLvRecruitList().addAll(newList);
			if (sortby == AppConfig.SORT_BY_CREATED_TIME) {
				Collections.sort(appContext.getLvRecruitList());
			}
			appContext.getLvRecruitListAdapter().setData(
					appContext.getLvRecruitList());
			appContext.getLvRecruitListAdapter().notifyDataSetChanged();
			appContext.saveRecruitList(newList);
		}
	}

	@Override
	protected void refreshData() {
		manualRefresh = true;
		pvRecruits.clickRefresh();
	}

	// public void handleRecruitData(List<Recruit> list, int actionType,
	// boolean isDisk) {
	// if (list == null)
	// return;
	// Log.i("main", "handle recruit data internet size" + list.size());
	// Log.i("main", "handle recruit data disk size"
	// + appContext.getLvRecruitList().size());
	// boolean savedIsInit = true;
	// // 只有读取了网络数据才算初始化完成
	// if (!appContext.recruitIsInit && !isDisk) {
	// // 初始化完成
	// appContext.recruitIsInit = true;
	// savedIsInit = false;
	// }
	//
	// if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH) {
	// MainActivity.CarrerTalkCount = 0;
	// MainActivity.bvCareerTalk.hide();
	// }
	//
	// // 如果是读的磁盘数据，或者是刷新事件，清空list
	// if (isDisk
	// || (savedIsInit && actionType == UIHelper.LISTVIEW_ACTION_REFRESH)) {
	// lvRecruitSum = list.size();
	// appContext.getLvRecruitList().clear();
	// appContext.setLvRecruitList(list);
	// appContext.getLvRecruitListAdapter().setData(
	// appContext.getLvRecruitList());
	// appContext.getLvRecruitListAdapter().notifyDataSetChanged();
	// Log.i("recruit bug", "now the recruit list size is"
	// + appContext.getLvRecruitList().size());
	// if (!isDisk) {
	// Toast.makeText(getActivity(),
	// getString(R.string.load_complete, "校园招聘"),
	// Toast.LENGTH_SHORT).show();
	// }
	// } else {
	//
	// List<Recruit> newList = new ArrayList<Recruit>();
	// int count = 0;
	// // 计算更新数据
	// if (appContext.getLvRecruitList().isEmpty()) {
	// count = list.size();
	// newList.addAll(list);
	// } else {
	// for (Recruit c1 : list) {
	// boolean flag = false;
	// for (Recruit c2 : appContext.getLvRecruitList()) {
	// if (c1.getRecruitID() == c2.getRecruitID()) {
	// Log.i("fm", "equal!!!!!");
	// if (c2.getIsJoined() == 1)
	// c1.setIsJoined(1);
	// c1.setStatus(0); // not new
	// appContext.getLvRecruitList().remove(c2);
	// newList.add(c1);
	// flag = true;
	// break;
	// }
	// }
	// if (!flag) {
	// newList.add(c1);
	// count++;
	// }
	// }
	// }
	//
	// if (count > 0) {
	// Toast.makeText(
	// getActivity(),
	// getString(R.string.new_data_str_toast_message, "校园招聘",
	// count), Toast.LENGTH_SHORT).show();
	// } else {
	// Toast.makeText(getActivity(),
	// getString(R.string.new_data_toast_none),
	// Toast.LENGTH_SHORT).show();
	// }
	// lvRecruitSum += count;
	// appContext.getLvRecruitList().addAll(newList);
	// Collections.sort(appContext.getLvRecruitList());
	// appContext.getLvRecruitListAdapter().setData(
	// appContext.getLvRecruitList());
	// appContext.getLvRecruitListAdapter().notifyDataSetChanged();
	// appContext.saveRecruitList(newList);
	// Log.i("recruit bug", "now the recruit list size is"
	// + appContext.getLvRecruitList().size());
	// Log.i("recruit bug", "load recruit data from disk!!!!!!!!!!!!!"
	// + lvRecruitSum);
	// }

}
