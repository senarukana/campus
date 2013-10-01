package com.campusrecruit.adapter;

import com.campusrecruit.activity.ScheduleActivity;
import com.campusrecruit.fragment.ScheduleFragment;
import com.campusrecruit.fragment.ScheduleListFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

public class ViewPagerScheduleAdapter extends FragmentPagerAdapter{

	private final FragmentManager mFragmentManager;
	private FragmentTransaction mTransaction = null;
	
	public ViewPagerScheduleAdapter(FragmentManager fm) {
		super(fm);
		mFragmentManager = fm;
	}

	@Override
	public Fragment getItem(int position) {
		switch(position){
		case 0:
			return ScheduleActivity.mScheduleListFragment;
		case 1:
			return ScheduleActivity.mScheduleFragment;
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}

}
