package com.campusrecruit.fragment;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.adapter.ListViewCareerTalkAdapter;
import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.krislq.sliding.R;

import android.R.bool;
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
import android.widget.PopupMenu.OnMenuItemClickListener;

@SuppressLint("ValidFragment")
public class CareerTalkFragment extends ListFragment implements
		SearchView.OnQueryTextListener {

	private PullToRefreshListView pvCareerTalks = null;
	private View pvFooter = null;
	private TextView pvFooterTextView = null;
	private ProgressBar pvFooterProgressBar = null;

	private Handler lvCareerTalkHandler = null;
	private Handler lvCareerTalkDiskHandler = null;
	// private Handler lvCareerTalkSortHandler = null;
	private Handler lvCareerTalkSearchHandler = null;

	// private int lvCareerTalkSum = 0;
	private AppContext appContext;

	private String saveFooterText = null;

	private SearchView mSearchView = null;
	private String _searchStr;
	private List<CareerTalk> savedList = new ArrayList<CareerTalk>();

	private int sortby = AppConfig.SORT_BY_CREATED_TIME;
	private boolean famousFlag = false;

	private MenuItem sortItem = null;
	private MenuItem famousItem = null;
	int icon_resources[] = { R.drawable.ic_menu_insert_time,
			R.drawable.ic_action_sort, R.drawable.ic_menu_join,
			R.drawable.ic_menu_reply, R.drawable.ic_menu_click };
	int icon_famous_res = R.drawable.header_all;

	// TODO 应该有更好解决办法
	// 如果是用户向上拉更新数据则不需要清空老数据，如果是通过sort方法则需要清空
	// false代表用户 true代表手动sort, 默认是手动
	private boolean manualRefresh = false;

	public CareerTalkFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View careerView = inflater.inflate(R.layout.careertalk_list, null);
		initLoadingView(careerView);
		initSearchReturnBackListener(new ReturnBackListener());

		pvCareerTalks = (PullToRefreshListView) careerView
				.findViewById(R.id.careertalk_listview);
		pvFooter = inflater.inflate(R.layout.listview_footer, null);
		pvFooterTextView = (TextView) pvFooter
				.findViewById(R.id.listview_foot_more);
		pvFooterProgressBar = (ProgressBar) pvFooter
				.findViewById(R.id.listview_foot_progress);
		pvCareerTalks.addFooterView(pvFooter);
		initCareerTalkListView(pvCareerTalks);

		if (saveFooterText != null && pvFooterTextView != null)
			pvFooterTextView.setText(saveFooterText);

		if (!appContext.getLvCareerTalkList().isEmpty()) {
			appContext.getLvCareerTalkListAdapter().setData(
					appContext.getLvCareerTalkList());
			appContext.getLvCareerTalkListAdapter().notifyDataSetChanged();
		}
		initListHeaderView(inflater, pvCareerTalks);
		Log.i("main", "career fragment init");
		return careerView;
	}

	private class ReturnBackListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (!savedList.isEmpty()) {
				hideLoadProgress(pvCareerTalks);
				UIHelper.ToastMessage(getActivity(),
						getString(R.string.search_return, Toast.LENGTH_SHORT));
				appContext.setLvCareerTalkList(savedList);
				appContext.getLvCareerTalkListAdapter().setData(
						appContext.getLvCareerTalkList());
				appContext.getLvCareerTalkListAdapter().notifyDataSetChanged();
			} else {
				loadCareerTalkDataFromDisk();
			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("career", "oncreate!!!!!!!");
		appContext = (AppContext) getActivity().getApplication();
		initHandler();
		setHasOptionsMenu(true);
		if (appContext == null) {
			Log.i("bug", "career appcontext is null");
			return;
		}
		if (appContext.getLvCareerTalkListAdapter() == null) {
			appContext
					.setLvCareerTalkListAdapter(new ListViewCareerTalkAdapter(
							getActivity(), appContext, R.layout.careertalk_item));
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		this.saveFooterText = pvFooterTextView.getText().toString();

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (sortItem != null) {
			sortItem.setIcon(icon_resources[sortby]);
		}
		Log.i("test", "set famous");
		if (famousItem != null) {
			Log.i("test", "set famous");
			famousItem.setIcon(icon_famous_res);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("career", "resume");
		/*
		 * if(sortItem == null){ Log.i("menu", "null"); } if(sortItem != null){
		 * sortItem.setIcon(icon_res); famousItem.setIcon(icon_famous_res); }
		 */

		sortby = AppConfig.SORT_BY_INSERT_TIME_CT;
		if (sortItem != null)
			sortItem.setIcon(icon_resources[sortby]);
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

		famousFlag = false;
		if (famousItem != null)
			famousItem.setIcon(icon_famous_res);
		View memuItemView1 = getActivity().findViewById(R.id.menu_famous);
		if (memuItemView1 != null) {
			PopupMenu popup1 = new PopupMenu(getActivity(), memuItemView1);
			if (popup1 != null) {
				MenuInflater inflater1 = popup1.getMenuInflater();
				if (inflater1 != null) {
					inflater1.inflate(R.menu.sub_menu_famous, popup1.getMenu());
					popup1.getMenu().getItem(famousFlag == true ? 0 : 1)
							.setChecked(true);
				}
			}
		}
	}

	protected boolean isAlwaysExpanded() {
		return false;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_search_sort, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		sortItem = menu.findItem(R.id.menu_sort);
		famousItem = menu.findItem(R.id.menu_famous);
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
			// inflater.inflate(R.menu.sort, popup.getMenu());
			inflater.inflate(R.menu.sort_career_talk, popup.getMenu());
			popup.getMenu().getItem(sortby).setChecked(true);
			popup.setOnMenuItemClickListener(menuItemClickListener);
			popup.show();
			break;
		case R.id.menu_famous:
			View memuItemView1 = getActivity().findViewById(R.id.menu_famous);
			PopupMenu popup1 = new PopupMenu(getActivity(), memuItemView1);
			MenuInflater inflater1 = popup1.getMenuInflater();
			inflater1.inflate(R.menu.sub_menu_famous, popup1.getMenu());
			popup1.getMenu().getItem(famousFlag == true ? 0 : 1)
					.setChecked(true);
			popup1.setOnMenuItemClickListener(menuItemClickListener1);
			popup1.show();
		}
		return super.onOptionsItemSelected(item);
	}

	public void ThreadSearch(final int pageIndex, final int action) {
		hideSearchView();
		if (action == UIHelper.LISTVIEW_ACTION_SORT) {
			showLoadProgress(pvCareerTalks);
		} else if (action == UIHelper.LISTVIEW_ACTION_SCROLL) {
			pvFooterProgressBar.setVisibility(View.VISIBLE);
		}
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					List<CareerTalk> list = appContext
							.getCareerTalkListFromInternet(pageIndex, sortby,
									famousFlag);
					msg.obj = list;
					msg.what = list.size();
					msg.arg1 = action;
				} catch (AppException e) {
					msg.what = -1;
					msg.obj = e;
				}
				lvCareerTalkHandler.sendMessage(msg);
			}
		}.start();
	}

	private OnMenuItemClickListener menuItemClickListener = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.sort_by_time_ct:
				if (sortby == AppConfig.SORT_BY_CREATED_TIME_CT) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				manualRefresh = true;
				sortby = AppConfig.SORT_BY_CREATED_TIME_CT;
				sortItem.setIcon(icon_resources[sortby]);
				pvCareerTalks.clickRefresh();
				return true;
			case R.id.sort_by_insert_time_ct:
				if (sortby == AppConfig.SORT_BY_INSERT_TIME_CT) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				manualRefresh = true;
				sortby = AppConfig.SORT_BY_INSERT_TIME_CT;
				sortItem.setIcon(icon_resources[sortby]);
				pvCareerTalks.clickRefresh();
				return true;
			case R.id.sort_by_join_ct:
				if (sortby == AppConfig.SORT_BY_JOINS_CT) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				manualRefresh = true;
				sortby = AppConfig.SORT_BY_JOINS_CT;
				sortItem.setIcon(icon_resources[sortby]);
				pvCareerTalks.clickRefresh();
				return true;
			case R.id.sort_by_reply_ct:
				if (sortby == AppConfig.SORT_BY_REPLYS_CT) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				manualRefresh = true;
				sortby = AppConfig.SORT_BY_REPLYS_CT;
				sortItem.setIcon(icon_resources[sortby]);
				pvCareerTalks.clickRefresh();
				return true;
			case R.id.sort_by_click_ct:
				if (sortby == AppConfig.SORT_BY_CLICKS_CT) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				manualRefresh = true;
				sortby = AppConfig.SORT_BY_CLICKS_CT;
				sortItem.setIcon(icon_resources[sortby]);
				pvCareerTalks.clickRefresh();
				return true;
			default:
				return false;
			}
		}

	};

	private OnMenuItemClickListener menuItemClickListener1 = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.sort_by_famous:
				if (famousFlag) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				Log.i("famous", "famous");
				icon_famous_res = R.drawable.header_famous;
				famousItem.setIcon(R.drawable.header_famous);
				famousFlag = true;
				manualRefresh = true;
				pvCareerTalks.clickRefresh();
				return true;
			case R.id.sort_by_common:
				if (!famousFlag) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return true;
				}
				Log.i("famous", "common");
				icon_famous_res = R.drawable.header_all;
				famousItem.setIcon(R.drawable.header_all);
				manualRefresh = true;
				famousFlag = false;
				pvCareerTalks.clickRefresh();
				return true;
			default:
				return false;
			}
		}

	};

	private void setupSearchView(MenuItem searchItem) {

		if (isAlwaysExpanded()) {
			mSearchView.setIconifiedByDefault(false);
		} else {
			searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}

		mSearchView.setOnQueryTextListener(this);
	}

	private void initCareerTalkListView(PullToRefreshListView listView) {

		listView.setAdapter(appContext.getLvCareerTalkListAdapter());
		Log.i("main", "career view set adapter complete");
		listView.setOnRefreshListener(new refreshListener());
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				pvCareerTalks.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				pvCareerTalks.onScrollStateChanged(view, scrollState);
				if (appContext.getLvCareerTalkList().isEmpty())
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
				int footerState = StringUtils.toInt(pvCareerTalks.getTag());
				if (scrollEnd && (footerState == UIHelper.LISTVIEW_DATA_MORE)) {
					Log.i("test", "scorllEnd datamore");
					pvCareerTalks.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					pvFooterTextView.setText(R.string.load_ing);
					pvFooterProgressBar.setVisibility(View.VISIBLE);

					int pageIndex = appContext.getLvCareerTalkList().size()
							/ AppConfig.PAGE_SIZE;
					ThreadSearch(pageIndex, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
		});

		if (!appContext.getLvCareerTalkList().isEmpty()) {
			Log.i("bug", appContext.getLvCareerTalkList().size() + "");
			pvFooterProgressBar.setVisibility(View.GONE);
			if (saveFooterText != null)
				pvFooterTextView.setText(saveFooterText);
			else {
				pvFooterTextView.setText(R.string.load_more);
				pvCareerTalks.setTag(UIHelper.LISTVIEW_DATA_MORE);
			}
			appContext.getLvCareerTalkListAdapter().setData(
					appContext.getLvCareerTalkList());
			appContext.getLvCareerTalkListAdapter().notifyDataSetChanged();
		}

		Log.i("main", "career view complete");
	}

	private class refreshListener implements
			PullToRefreshListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			if (manualRefresh) {
				ThreadSearch(0, UIHelper.LISTVIEW_ACTION_SORT);
				manualRefresh = false;
			} else {
				ThreadSearch(0, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		}

	}

	public void loadCareerTalkDataFromDisk() {
		if (appContext.careerTalkIsInit)
			return;
		Log.i("career bug", "begin load from disk!!!!!!1");
		lvCareerTalkDiskHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what > 0) {
					handleLvData((List<CareerTalk>) msg.obj,
							UIHelper.LISTVIEW_ACTION_REFRESH, true);
					/*
					 * appContext.getLvCareerTalkList().addAll(
					 * (List<CareerTalk>) msg.obj);
					 */
					// lvCareerTalkSum +=
					// appContext.getLvCareerTalkList().size();
					appContext.getLvCareerTalkListAdapter().setData(
							appContext.getLvCareerTalkList());
					appContext.getLvCareerTalkListAdapter()
							.notifyDataSetChanged();
				}
				pvCareerTalks.clickRefresh();

			}
		};
		new Thread() {
			@Override
			public void run() {
				Message msg = new Message();
				List<CareerTalk> list = appContext.getCareerTalkListFromDisk();
				Log.i("ddd", "send cd data complete");
				if (list != null) {
					msg.what = list.size();
					msg.obj = list;
					Log.i("ddd", list.size() + "");
				} else {
					msg.what = 0;
					msg.obj = null;
				}
				lvCareerTalkDiskHandler.sendMessage(msg);
				Log.i("ddd", "send complete cd data");
			}
		}.start();
	}

	private void initHandler() {
		lvCareerTalkHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				pvFooter.setVisibility(View.VISIBLE);
				Log.i("ddd", "handle  careertalk data what" + msg.what);
				if (msg.what >= 0) {
					handleLvData((List<CareerTalk>) msg.obj, msg.arg1, false);
					if (msg.what < AppConfig.PAGE_SIZE) {
						pvCareerTalks.setTag(UIHelper.LISTVIEW_DATA_FULL);
						pvFooterTextView.setText(getString(R.string.load_full));
					} else if (msg.what == AppConfig.PAGE_SIZE) {
						pvCareerTalks.setTag(UIHelper.LISTVIEW_DATA_MORE);
						pvFooterTextView.setText(getString(R.string.load_more));
					}
				} else if (msg.what == -1) {
					pvCareerTalks.setTag(UIHelper.LISTVIEW_DATA_FULL);
					pvFooterTextView
							.setText(getString(R.string.load_over_time));
					((AppException) msg.obj).makeToast(getActivity());
					pvCareerTalks
							.onRefreshComplete(getString(R.string.pull_to_refresh_update)
									+ new Date().toLocaleString());

				}
				if (appContext.getLvCareerTalkListAdapter().getCount() == 0) {
					pvCareerTalks.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					pvFooterTextView.setText(getString(R.string.load_empty));
				}
				pvFooterProgressBar.setVisibility(View.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH
						|| (msg.arg1 == UIHelper.LISTVIEW_ACTION_SORT)) {
					pvCareerTalks
							.onRefreshComplete(getString(R.string.pull_to_refresh_update)
									+ new Date().toLocaleString());
					pvCareerTalks.setSelection(0);
				}

			}

		};
		lvCareerTalkSearchHandler = new Handler() {
			public void handleMessage(Message msg) {
				pvFooter.setVisibility(View.GONE);
				savedList.clear();
				savedList.addAll(appContext.getLvCareerTalkList());
				if (msg.what == 0) {
					completeSearchWithEmpty(pvCareerTalks, _searchStr);
					return;
				}
				if (msg.what >= 1) {
					List<CareerTalk> list = (List<CareerTalk>) msg.obj;
					pvCareerTalks.setTag(UIHelper.LISTVIEW_DATA_FULL);
					for (CareerTalk c1 : list) {
						for (CareerTalk c2 : appContext.getLvCareerTalkList()) {
							if (c1.getCareerTalkID() == c2.getCareerTalkID()) {
								if (c2.getIsJoined() == 1)
									c1.setIsJoined(1);
								break;
							}
						}
					}
					appContext.getLvCareerTalkList().clear();
					completeSearch(pvCareerTalks, list.size(), _searchStr);
					Collections.sort(list);
					appContext.setLvCareerTalkList(list);
					appContext.getLvCareerTalkListAdapter().setData(list);
					appContext.getLvCareerTalkListAdapter()
							.notifyDataSetChanged();
				} else {
					((AppException) msg.obj).makeToast(getActivity());
					completeSearchWithError(pvCareerTalks);
				}
			}
		};

	}

	public void handleLvData(List<CareerTalk> list, int actionType,
			boolean isDisk) {
		if (list == null)
			return;
		// 如果是读取磁盘数据则证明需要初始化数据
		// 如果是排序也需要清空数据
		if (isDisk || actionType == UIHelper.LISTVIEW_ACTION_SORT) {
			// lvCareerTalkSum = list.size();
			appContext.setLvCareerTalkList(list);
			appContext.getLvCareerTalkListAdapter().setData(
					appContext.getLvCareerTalkList());
			appContext.getLvCareerTalkListAdapter().notifyDataSetChanged();
			if (!isDisk) {
				Toast.makeText(getActivity(),
						getString(R.string.load_complete, "宣讲会"),
						Toast.LENGTH_SHORT).show();
			}
			hideLoadProgress(pvCareerTalks);
		} else {
			List<CareerTalk> newList = new ArrayList<CareerTalk>();
			int count = 0;
			// 计算更新数据
			if (appContext.getLvCareerTalkList().isEmpty()) {
				count = list.size();
				newList.addAll(list);
			} else {
				for (CareerTalk c1 : list) {
					boolean flag = false;
					for (CareerTalk c2 : appContext.getLvCareerTalkList()) {
						if (c1.getCareerTalkID() == c2.getCareerTalkID()) {
							if (c2.getIsJoined() == 1)
								c1.setIsJoined(1);
							appContext.getLvCareerTalkList().remove(c2);
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

			if (MainActivity.CarrerTalkCount != 0) {
				Toast.makeText(
						getActivity(),
						getString(R.string.new_data_str_toast_message,
								"宣讲会", MainActivity.CarrerTalkCount), Toast.LENGTH_SHORT).show();
			} else {

				if (count > 0) {
					Toast.makeText(
							getActivity(),
							getString(R.string.new_data_str_toast_message,
									"宣讲会", count), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.new_data_toast_none),
							Toast.LENGTH_SHORT).show();
				}
			}

			if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH) {
				MainActivity.CarrerTalkCount = 0;
				MainActivity.bvCareerTalk.hide();
			}

			// lvCareerTalkSum += count;

			appContext.getLvCareerTalkList().addAll(newList);
			Collections.sort(appContext.getLvCareerTalkList());
			appContext.getLvCareerTalkListAdapter().setData(
					appContext.getLvCareerTalkList());
			appContext.getLvCareerTalkListAdapter().notifyDataSetChanged();
			appContext.saveCareerTalkList(newList);
			Log.i("career bug", String.format("sort size is %d", appContext
					.getLvCareerTalkList().size()));
		}

	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		Log.i("search", "careertalk " + query);
		final String companyString = query;
		_searchStr = query;
		beginSearch(pvCareerTalks);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					List<CareerTalk> list = appContext
							.getCareerTalkListByCompanyName(companyString);
					msg.obj = list;
					msg.what = list.size();
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				lvCareerTalkSearchHandler.sendMessage(msg);
			}
		}.start();
		mSearchView.clearFocus();
		return true;
	}
	/*
	 * public void handleLvData(int what, Object object, int objectType, int
	 * actionType) { Log.i("ct", "handle data"); List<CareerTalk> newList = new
	 * ArrayList<CareerTalk>(); switch (actionType) { case
	 * UIHelper.LISTVIEW_ACTION_INIT: case UIHelper.LISTVIEW_ACTION_REFRESH:
	 * switch (objectType) { case UIHelper.LISTVIEW_DATATYPE_CAREER_TALK: //
	 * 计算更新数据 List<CareerTalk> list = (List<CareerTalk>) object;
	 * appContext.getLvCareerTalkList().clear();
	 * appContext.getLvCareerTalkList().addAll(list); Log.i("ct",
	 * "handle refresh data"); } if (actionType ==
	 * UIHelper.LISTVIEW_ACTION_REFRESH) { if (newList.size() > 0) {
	 * Toast.makeText( getActivity(), getString(R.string.new_data_toast_message,
	 * newList.size()), Toast.LENGTH_SHORT).show(); } else {
	 * Toast.makeText(getActivity(), getString(R.string.new_data_toast_none),
	 * Toast.LENGTH_SHORT).show(); } } break; case
	 * UIHelper.LISTVIEW_ACTION_SCROLL: switch (objectType) { case
	 * UIHelper.LISTVIEW_DATATYPE_CAREER_TALK: List<CareerTalk> list =
	 * (List<CareerTalk>) object; lvCareerTalkSum += what; if
	 * (appContext.getLvCareerTalkList().size() > 0) { for (CareerTalk ct0 :
	 * list) { boolean b = false; for (CareerTalk ct1 :
	 * appContext.getLvCareerTalkList()) { if (ct0.getCareerTalkID() ==
	 * ct1.getCareerTalkID()) { appContext.getLvCareerTalkList().remove(ct1);
	 * appContext.getLvCareerTalkList().add(ct0); b = true; break; } } if (!b)
	 * appContext.getLvCareerTalkList().add(ct0); } } else {
	 * appContext.getLvCareerTalkList().addAll(list); break; } Log.i("ct",
	 * "handle scoll data"); break; } } Log.i("ct", "save data");
	 * appContext.saveCareerTalkList(newList); }
	 */

	@Override
	protected void refreshData() {
		manualRefresh = true;
		pvCareerTalks.clickRefresh();
	}

	/*
	 * public List<CareerTalk> getLvCareerTalkList(){ CareerTalk talk0 = new
	 * CareerTalk(); talk0.setCareerTalkID("123456");
	 * talk0.setSchoolName("BUPT"); talk0.setCompanyName("Baidu");
	 * talk0.setPlace("Building Three");
	 * talk0.setStartTime("2013-09-12 14:30:00");
	 * talk0.setEndTime("2013-09-12 16:30:00");
	 * 
	 * CareerTalk talk1 = new CareerTalk(); talk1.setCareerTalkID("12345");
	 * talk0.setSchoolName("BUPT"); talk1.setCompanyName("Sina");
	 * talk1.setPlace("Building Three");
	 * talk1.setStartTime("2013-09-12 14:30:00");
	 * talk0.setEndTime("2013-09-12 16:30:00");
	 * 
	 * List<CareerTalk> li = new ArrayList<CareerTalk>(); li.add(talk0);
	 * li.add(talk1);
	 * 
	 * return li; }
	 */
}
