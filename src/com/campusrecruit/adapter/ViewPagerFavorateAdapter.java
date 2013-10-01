package com.campusrecruit.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.campusrecruit.activity.UserFavroateActivity;
import com.campusrecruit.fragment.CareerTalkFavoratesFragment;
import com.campusrecruit.fragment.RecruitFavoratesFragment;

public class ViewPagerFavorateAdapter extends FragmentPagerAdapter{
	private final FragmentManager mFragmentManager;
	private FragmentTransaction mTransaction = null;
	
	public ViewPagerFavorateAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
		mFragmentManager = fm;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		switch(position){
		case 0:
			return new RecruitFavoratesFragment();
		case 1:
			return new CareerTalkFavoratesFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}
}
