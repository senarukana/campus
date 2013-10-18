/*package com.campusrecruit.fragment;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.adapter.DemoFragmentAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.pcncad.campusRecruit.R;


public class MenuFragment_v extends PreferenceFragment implements OnPreferenceClickListener{
    private int index = -1;
    private ViewPager mViewPager = null;
    private MainActivity   mActivity = null;
    private LinearLayout mLinearLayout = null;
    private RadioGroup radioGroup = null;
    private TabHost tabHost = null;
    
        
          
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mActivity = (MainActivity)getActivity();
        mLinearLayout = (LinearLayout)mActivity.findViewById(R.id.content);
        mViewPager = (ViewPager)mActivity.findViewById(R.id.viewpager);    
        radioGroup = (RadioGroup)mActivity.findViewById(R.id.radio_group);
        tabHost = (TabHost)mActivity.findViewById(R.id.tab_host);
        
        //set the preference xml to the content view
        addPreferencesFromResource(R.xml.menu);
        //add listener
        findPreference("a").setOnPreferenceClickListener(this);
        findPreference("b").setOnPreferenceClickListener(this);
        findPreference("n").setOnPreferenceClickListener(this);
        
        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        String title = (String) preference.getTitle();
        getActivity().getActionBar().setTitle(title);
        if("a".equals(key)) {
            //if the content view is that we need to show . show directly
            if(index == 0) {
                ((MainActivity)getActivity()).getSlidingMenu().toggle();
                return true;
            }
            //otherwise , replace the content view via a new Content fragment
            index = 0;
            mLinearLayout.setVisibility(View.VISIBLE);
            tabHost.setVisibility(View.GONE);
                       
            getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            getActivity().getActionBar().setTitle(title);
            FragmentManager fragmentManager = ((MainActivity)getActivity()).getFragmentManager();
            ContentFragment contentFragment = (ContentFragment)fragmentManager.findFragmentByTag("A");
            fragmentManager.beginTransaction()
            .replace(R.id.content, contentFragment == null ?new ContentFragment("收藏"):contentFragment ,"A")
            .commit();
        }else if("b".equals(key)) {
        	getActivity().getActionBar().setTitle("宣讲会");
            if(index == 1) {
                ((MainActivity)getActivity()).getSlidingMenu().toggle();
                return true;
            }
            index = 1;
            mLinearLayout.setVisibility(View.GONE);
            tabHost.setVisibility(View.VISIBLE);
            Log.i("menu", "begin b");
            
            DemoFragmentAdapter demoFragmentAdapter = new DemoFragmentAdapter(mActivity.getFragmentManager());
            Log.i("menu", "end adapter");
            mViewPager.setOffscreenPageLimit(5);
            mViewPager.setAdapter(demoFragmentAdapter);
            mViewPager.setOnPageChangeListener(onPageChangeListener);
            getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            mViewPager.setCurrentItem(0);
           
        }else if("n".equals(key)) {

            if(index == 2) {
                ((MainActivity)getActivity()).getSlidingMenu().toggle();
                return true;
            }
            index = 2;
            mLinearLayout.setVisibility(View.VISIBLE);
            tabHost.setVisibility(View.GONE);

            getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            FragmentManager fragmentManager = ((MainActivity)getActivity()).getFragmentManager();
            ContentFragment contentFragment = (ContentFragment)fragmentManager.findFragmentByTag("N");
            fragmentManager.beginTransaction()
            .replace(R.id.content, contentFragment == null ? new ContentFragment("其它"):contentFragment,"N")
            .commit();
        }
        //anyway , show the sliding menu
        ((MainActivity)getActivity()).getSlidingMenu().toggle();
        return false;
    }
    private SlidingMenu getSlidingMenu() {
        return mActivity.getSlidingMenu();
    }
    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
    	
    	
		
        @Override
        public void onPageSelected(int position) {
            //getActivity().getActionBar().setSelectedNavigationItem(position);
        	getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            switch (position) {
                case 0:
                    getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    radioGroup.check(R.id.radio_career_talk);
                    getActivity().getActionBar().setTitle("宣讲会");
                    break;
                case 1:
                	radioGroup.check(R.id.radio_campus_recruit);
                	getActivity().getActionBar().setTitle("校园招聘");
                	break;
                case 2:
                	radioGroup.check(R.id.radio_schedule);
                	getActivity().getActionBar().setTitle("日程安排");
                	break;
                case 3:
                	radioGroup.check(R.id.radio_discuss);
                	getActivity().getActionBar().setTitle("讨论组");
                	break;
                default:
                    getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    break;
            }
            
           			
			
        }

    };
    
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			switch(checkedId){
			case R.id.radio_career_talk:
				mViewPager.setCurrentItem(0);
				break;
			case R.id.radio_campus_recruit:
				mViewPager.setCurrentItem(1);
				break;
			case R.id.radio_schedule:
				mViewPager.setCurrentItem(2);
				break;
			case R.id.radio_discuss:
				mViewPager.setCurrentItem(3);
				break;
			}
			
		}
    	
    };
    
    
    
}*/