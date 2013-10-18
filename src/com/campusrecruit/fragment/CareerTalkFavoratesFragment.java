package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.adapter.ListViewCareerTalkFavorateAdapter;
import com.campusrecruit.adapter.ListViewRecruitAdapter;
import com.campusrecruit.adapter.ListViewRecruitFavorateAdapter;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.UIHelper;
import com.pcncad.campusRecruit.R;

public class CareerTalkFavoratesFragment extends EmptyFragment {
	private LinearLayout mainLayout;
	private ListView lvCareerTalkView;

	public CareerTalkFavoratesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
		View careerView = inflater.inflate(R.layout.career_favorate_list, null);
		initLoadingView(careerView);
		setEmptyText(R.string.career_favorate_empty);

		mainLayout = (LinearLayout) careerView
				.findViewById(R.id.career_favorate_main);
		lvCareerTalkView = (ListView) careerView
				.findViewById(R.id.career_favorate_list);
		lvCareerTalkView.setAdapter(appContext
				.getLvCareerTalkFavoratesAdapter());
		emptyText.setTextColor(getResources().getColor(R.color.myblue));
		emptyLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.currentViewPosition = 1;
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
			}
		});

		if (isloading) {
			showLoadProgress(lvCareerTalkView);
		} else {
			if (appContext.getLvCareerTalkFavorateList().isEmpty()) {
				// read data complete, but data is empty
				showEmptyView(lvCareerTalkView);
			} else {
				// show the data
				hideLoadProgress(lvCareerTalkView);
				appContext.getLvCareerTalkFavoratesAdapter().setData(
						appContext.getLvCareerTalkFavorateList());
				appContext.getLvCareerTalkFavoratesAdapter()
						.notifyDataSetChanged();
			}
		}
		Log.i("test", "career complete");
		return careerView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (appContext == null)
			appContext = (AppContext) getActivity().getApplication();
		appContext
				.setLvCareerTalkFavoratesAdapter(new ListViewCareerTalkFavorateAdapter(
						getActivity(), appContext,
						R.layout.career_favorate_item));
		new CareerTalkFavorateAsyncTask().execute();

	}

	private class CareerTalkFavorateAsyncTask extends
			AsyncTask<Void, Void, List<CareerTalk>> {
		@Override
		protected void onPreExecute() {
			showLoadProgress(lvCareerTalkView);
		}

		@Override
		protected List<CareerTalk> doInBackground(Void... params) {
			List<CareerTalk> list = appContext.getCareerTalkFavorateList(true);
			return list;
		}

		@Override
		protected void onPostExecute(List<CareerTalk> list) {
			if (!appContext.getLvCareerTalkFavorateList().isEmpty()) {
				// 去重
				for (CareerTalk careerTalk : list) {
					boolean flag = false;
					for (CareerTalk careerTalk2 : appContext
							.getLvCareerTalkFavorateList()) {
						if (careerTalk.getCareerTalkID() == careerTalk2
								.getCareerTalkID()) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						appContext.getLvCareerTalkFavorateList()
								.add(careerTalk);
					}
				}
			} else {
				appContext.getLvCareerTalkFavorateList().addAll(list);
			}
			if (appContext.getLvCareerTalkFavoratesAdapter() != null) {
				if (appContext.getLvCareerTalkFavorateList().isEmpty()) {
					showEmptyView(lvCareerTalkView);
				} else {
					hideLoadProgress(lvCareerTalkView);
					Collections.sort(appContext.getLvCareerTalkFavorateList());
					appContext.getLvCareerTalkFavoratesAdapter().setData(
							appContext.getLvCareerTalkFavorateList());
					appContext.getLvCareerTalkFavoratesAdapter()
							.notifyDataSetChanged();
				}
			}
		}

	}
}
