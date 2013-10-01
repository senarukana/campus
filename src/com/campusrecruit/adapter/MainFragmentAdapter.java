package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.fragment.CareerTalkFragment;
import com.campusrecruit.fragment.BBSSectionFragment;
import com.campusrecruit.fragment.ScheduleFragment;
import com.campusrecruit.fragment.BaseFragment;
import com.campusrecruit.fragment.RecruitFragment;


public class MainFragmentAdapter extends FragmentPagerAdapter {

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction = null;
    
    
    public MainFragmentAdapter(FragmentManager fm) {
    	super(fm);
        mFragmentManager = fm;

/*        mFragmentList.add(new CareerTalkFragment("招聘信息"));
        mFragmentList.add(new RecruitFragment("校园招聘"));
        mFragmentList.add(new ScheduleFragment("个人日程安排"));
        mFragmentList.add(new BBSSectionFragment("讨论组"));*/

    }
    @Override
    public int getCount() {
        return 3;
    }

/*    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }*/

/*    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mTransaction == null) {
            mTransaction = mFragmentManager.beginTransaction();
        }
        String name = getTag(position);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            mTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            mTransaction.add(container.getId(), fragment,
                    getTag(position));
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mTransaction == null) {
            mTransaction = mFragmentManager.beginTransaction();
        }
        mTransaction.detach((Fragment) object);
    }
    @Override
    public void finishUpdate(ViewGroup container) {
        if (mTransaction != null) {
            mTransaction.commitAllowingStateLoss();
            mTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }*/
    public Fragment getItem(int position){
    	Log.i("main","get item" + position);
       switch (position) {
		case 0:
			return MainActivity.recruitFragment;
		case 1:
			return MainActivity.careerTalkFragment;
		case 2:
			return MainActivity.bbsSectionFragment;
		default:
			return MainActivity.recruitFragment;
		}
    }
/*    public long getItemId(int position) {
        return position;
    }*/
    
    

}
