package com.krislq.adapter;

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
		// TODO Auto-generated constructor stub
		mFragmentManager = fm;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return 2;
	}

}
