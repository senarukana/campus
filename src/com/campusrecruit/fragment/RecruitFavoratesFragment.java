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
import com.campusrecruit.adapter.ListViewRecruitAdapter;
import com.campusrecruit.adapter.ListViewRecruitFavorateAdapter;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.common.UIHelper;
import com.pcncad.campusRecruit.R;

@SuppressLint("ValidFragment")
public class RecruitFavoratesFragment extends EmptyFragment {

	private ListView lvRecruitView;
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
		View recruitView = inflater.inflate(R.layout.recruit_favorate_list,
				null);
		initLoadingView(recruitView);
		setEmptyText(R.string.recruit_favorate_empty);
		emptyText.setTextColor(getResources().getColor(R.color.myblue));
		emptyLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
			}
		});
		
		lvRecruitView = (ListView) recruitView
				.findViewById(R.id.recruit_favorate_list);
		lvRecruitView.setAdapter(appContext.getLvRecruitFavoratesAdapter());
		if (isloading) {
			showLoadProgress(lvRecruitView);
		} else {
			if (appContext.getLvCareerTalkFavorateList().isEmpty()) {
				// read data complete, but data is empty
				showEmptyView(lvRecruitView);
			} else {
				// show the data
				hideLoadProgress(lvRecruitView);
				appContext.getLvRecruitFavoratesAdapter().setData(
						appContext.getLvRecruitFavorateList());
				appContext.getLvRecruitFavoratesAdapter()
						.notifyDataSetChanged();
			}
		}
		Log.i("test","recrui complete");
		return recruitView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
		appContext
				.setLvRecruitFavoratesAdapter(new ListViewRecruitFavorateAdapter(
						getActivity(), appContext,
						R.layout.recruit_favorate_item));
		new RecruitFavorateAsyncTask().execute();
	}

	private class RecruitFavorateAsyncTask extends
			AsyncTask<Void, Void, List<Recruit>> {
		@Override
		protected void onPreExecute() {
			showLoadProgress(lvRecruitView);
		}

		@Override
		protected List<Recruit> doInBackground(Void... params) {
			List<Recruit> list = appContext.getRecruitFavorateList(true);
			return list;
		}

		@Override
		protected void onPostExecute(List<Recruit> list) {
			if (!appContext.getLvRecruitFavorateList().isEmpty()) {
				// 去重
				for (Recruit recruit : list) {
					boolean flag = false;
					for (Recruit recruit2 : appContext
							.getLvRecruitFavorateList()) {
						if (recruit.getRecruitID() == recruit2.getRecruitID()) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						appContext.getLvRecruitFavorateList()
								.add(recruit);
					}
				}
			} else {
				appContext.getLvRecruitFavorateList().addAll(list);
			}
			if (appContext.getLvRecruitFavoratesAdapter() != null) {
				if (appContext.getLvRecruitFavorateList().isEmpty()) {
					showEmptyView(lvRecruitView);
				} else {
					hideLoadProgress(lvRecruitView);
					Collections.sort(appContext.getLvRecruitFavorateList());
					appContext.getLvRecruitFavoratesAdapter().setData(
							appContext.getLvRecruitFavorateList());
					appContext.getLvRecruitFavoratesAdapter()
							.notifyDataSetChanged();
				}
			}
		}

	}
}
