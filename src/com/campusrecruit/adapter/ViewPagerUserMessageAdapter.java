package com.campusrecruit.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.campusrecruit.activity.UserFavroateActivity;
import com.campusrecruit.activity.UserReplyActivity;
import com.campusrecruit.fragment.BaseFragment;
import com.campusrecruit.fragment.ReplyByOtherUserFragment;
import com.campusrecruit.fragment.ReplyToOtherUserFragment;
import com.campusrecruit.fragment.UserTopicFragment;

public class ViewPagerUserMessageAdapter extends FragmentPagerAdapter{
	private final FragmentManager mFragmentManager;
	private FragmentTransaction mTransaction = null;
	private List<BaseFragment> fragments;
	
	public ViewPagerUserMessageAdapter(FragmentManager fm, List<BaseFragment> fragments) {
		super(fm);
		mFragmentManager = fm;
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		if (position > 3)
			return null;
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return 3;
	}
}
