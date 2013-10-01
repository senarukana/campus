package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.activity.RecommendActivity;
import com.campusrecruit.adapter.MainFragmentAdapter;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.BitmapManager;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.BadgeView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.krislq.sliding.R;
import com.tencent.weibo.api.PrivateAPI;

@SuppressLint("ResourceAsColor")
public class MenuFragment extends Fragment {

	private String[] items = { "首页", "日程", "收藏", "消息", "帖子", "偏好", "设置" };
	private int[] item_pic_res = { R.drawable.home, R.drawable.schedule,
			R.drawable.favorate, R.drawable.user_message, R.drawable.articles, R.drawable.user_preference,
			R.drawable.user_setting };

	private int index = -1;
	private MainActivity mActivity = null;
	private LinearLayout mLinearLayout = null;
	private TabHost tabHost = null;
	private BitmapManager bmpManager;

	private ImageView vUserImage;
	private TextView vUserName;

	private AppContext appContext;

	@Override
	public void onResume() {
		super.onResume();
		if (appContext.isLogin()) {
			Log.i("life","resume face " + appContext.getLoginUser().getHasFace());
			vUserName.setText(appContext.getLoginUser().getName());
			if (appContext.getLoginUser().isShowPicture()
					&& appContext.getLoginUser().getHasFace() == 1) {
//				UIHelper.showUserFace(vUserImage, appContext.getLoginUid());
				bmpManager.loadBitmap(appContext.getLoginUid(), vUserImage);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		appContext = (AppContext) getActivity().getApplication();
		bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				getActivity().getResources(), R.drawable.user_face));
		Log.i("main", "onCreate fragment");
		mActivity = (MainActivity) getActivity();
		mLinearLayout = (LinearLayout) getActivity().findViewById(R.id.content);
		tabHost = (TabHost) getActivity().findViewById(R.id.tab_host);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frame_menu_v1, container, false);
		MainActivity.menuListView = (ListView) view
				.findViewById(R.id.menu_listview);
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < items.length; i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("item", items[i]);
			item.put("image", item_pic_res[i]);
			listItems.add(item);
		}
		Log.i("main", "onCreateView fragment");
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), listItems,
				R.layout.frame_menu_v1_item, new String[] { "item", "image" },
				new int[] { R.id.menu_item, R.id.menu_img });
		Log.i("main", "create fragment");

		// initFragmentData();

		MainActivity.menuListView.setAdapter(adapter);
		MainActivity.menuListView
				.setOnItemClickListener(new onItemClickListener());

		vUserName = (TextView) view.findViewById(R.id.menu_user_name);
		vUserImage = (ImageView) view.findViewById(R.id.menu_user_image);
		if (appContext.isLogin()) {
			vUserName.setText(appContext.getLoginUser().getName());
			if (appContext.getLoginUser().getHasFace() == 1) {
				Log.i("life", "show user imsage !!! begin!!");
//				UIHelper.showUserFace(vUserImage, appContext.getLoginUid());
				bmpManager.loadBitmap(appContext.getLoginUid(), vUserImage);
			}
		} else {
			vUserName.setText(R.string.default_menu_user_name);
		}
		vUserImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("user", "click");
				if (!appContext.isLogin()) {
					UIHelper.showLoginDialog(getActivity());
					return;
				}
				UIHelper.showUserInfo(getActivity(), appContext.getLoginUid());
			}
		});

		Log.i("main", "return fragment");
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private class onItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (!appContext.isInit()) {
				UIHelper.showLoginDialog(getActivity());
				return;
			}
			Log.i("click", parent.getChildCount() + "");
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			switch (position) {
			case 0:
				if (index == 0) {
					((MainActivity) getActivity()).getSlidingMenu().toggle();
					return;
				}
				index = 0;
				mActivity.showViewPager();
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				break;
			case 1:
				if (index == 1) {
					((MainActivity) getActivity()).getSlidingMenu().toggle();
					return;
				}
				index = 1;

				getActivity().getActionBar().setNavigationMode(
						ActionBar.NAVIGATION_MODE_STANDARD);
				UIHelper.showSchedule(getActivity());
				index = -1;
				break;
			case 2:
				if (index == 2) {
					((MainActivity) getActivity()).getSlidingMenu().toggle();
					return;
				}
				index = 2;
				getActivity().getActionBar().setNavigationMode(
						ActionBar.NAVIGATION_MODE_STANDARD);
				UIHelper.showUserFavroate(getActivity());
				index = -1;
				break;
			case 3:
				if (index == 3) {
					((MainActivity) getActivity()).getSlidingMenu().toggle();
					return;
				}
				index = 3;

				getActivity().getActionBar().setNavigationMode(
						ActionBar.NAVIGATION_MODE_STANDARD);
				UIHelper.showPrivateMessage(getActivity());
				index = -1;
				break;
			case 4:
				if (index == 4) {
					((MainActivity) getActivity()).getSlidingMenu().toggle();
					return;
				}
				index = 4;

				getActivity().getActionBar().setNavigationMode(
						ActionBar.NAVIGATION_MODE_STANDARD);
				UIHelper.showUserReply(getActivity());
				index = -1;
				/*
				 * index = 4; mLinearLayout.setVisibility(View.VISIBLE);
				 * tabHost.setVisibility(View.GONE); getSlidingMenu().toggle();
				 * getActivity().getActionBar().setNavigationMode(
				 * ActionBar.NAVIGATION_MODE_STANDARD); //
				 * UIHelper.showRecommends(getActivity(), false);
				 * FragmentManager fragmentManager = ((MainActivity)
				 * getActivity()) .getSupportFragmentManager();
				 * PrivateMessageFragment messageFragment = new
				 * PrivateMessageFragment(); fragmentManager.beginTransaction()
				 * .replace(R.id.content, messageFragment, "C").commit(); index
				 * = -1;
				 */
				break;
			case 5:
				if (index == 5) {
					((MainActivity) getActivity()).getSlidingMenu().toggle();
					return;
				}
				index = 5;

				getActivity().getActionBar().setNavigationMode(
						ActionBar.NAVIGATION_MODE_STANDARD);
				Intent intent = new Intent(getActivity(), RecommendActivity.class);
				intent.putExtra("init", false);
				getActivity().startActivityForResult(intent, UIHelper.REQUEST_CODE_FOR_RECOMMEND);
				index = -1;
				break;
			case 6:
				if (index == 6) {
					((MainActivity) getActivity()).getSlidingMenu().toggle();
					return;
				}
				index = 6;

				getActivity().getActionBar().setNavigationMode(
						ActionBar.NAVIGATION_MODE_STANDARD);
				UIHelper.showSettingsForResult(getActivity());
				index = -1;
				break;
			}
			// anyway , show the sliding menu

		}

	}

	private SlidingMenu getSlidingMenu() {
		return mActivity.getSlidingMenu();
	}

}
