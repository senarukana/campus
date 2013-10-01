package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.activity.RecruitDetailActivity;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.fragment.CommentsFragment;
import com.campusrecruit.fragment.CompanyDetailFragment;
import com.campusrecruit.fragment.ContentFragment;
import com.campusrecruit.fragment.RecruitDetailFragment;
import com.campusrecruit.fragment.RecruitProcessFragment;
import com.campusrecruit.fragment.BaseFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerRecruitDetailAdapter extends FragmentPagerAdapter {

	private final FragmentManager mFragmentManager;
	private FragmentTransaction mTransaction = null;


	public ViewPagerRecruitDetailAdapter(FragmentManager fm) {
		super(fm);
		mFragmentManager = fm;
	}

	@Override
	public int getCount() {
		return 4;
	}

	public Fragment getItem(int position) {
		Log.i("main", "get item" + position);
		switch (position) {
		case 0:
			return RecruitDetailActivity.recruitDetailFragment;
		case 1:
			return RecruitDetailActivity.recruitProcessFragment;
		case 2:
			return RecruitDetailActivity.companyDetailFragment;
		case 3:
			return RecruitDetailActivity.commentsFragment;
		default:
			return RecruitDetailActivity.recruitDetailFragment;
		}
	}

	/*
	 * @Override public boolean isViewFromObject(View view, Object object) {
	 * return ((Fragment) object).getView() == view; }
	 * 
	 * @Override public Object instantiateItem(ViewGroup container, int
	 * position) { if (mTransaction == null) { mTransaction =
	 * mFragmentManager.beginTransaction(); } String name = getTag(position);
	 * Fragment fragment = mFragmentManager.findFragmentByTag(name); if
	 * (fragment != null) { mTransaction.attach(fragment); } else { fragment =
	 * getItem(position); mTransaction.add(container.getId(), fragment,
	 * getTag(position)); } return fragment; }
	 * 
	 * @Override public void destroyItem(ViewGroup container, int position,
	 * Object object) { if (mTransaction == null) { mTransaction =
	 * mFragmentManager.beginTransaction(); } mTransaction.detach((Fragment)
	 * object); }
	 * 
	 * @Override public void finishUpdate(ViewGroup container) { if
	 * (mTransaction != null) { mTransaction.commitAllowingStateLoss();
	 * mTransaction = null; mFragmentManager.executePendingTransactions(); } }
	 * 
	 * public Fragment getItem(int position) { return
	 * mFragmentList.get(position); }
	 * 
	 * public long getItemId(int position) { return position; }
	 * 
	 * protected String getTag(int position) { return
	 * mFragmentList.get(position).getClass().getName(); }
	 */

}
