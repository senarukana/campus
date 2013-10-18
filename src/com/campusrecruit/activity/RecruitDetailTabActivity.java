package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.MenuItem;

import com.campusrecruit.adapter.ViewPagerRecruitDetailAdapter;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.fragment.BaseFragment;
import com.campusrecruit.fragment.CommentsFragment;
import com.campusrecruit.fragment.CompanyDetailFragment;
import com.campusrecruit.fragment.RecruitDetailDescriptionFragment;
import com.campusrecruit.fragment.RecruitDetailFragment;
import com.campusrecruit.fragment.RecruitProcessFragment;
import com.pcncad.campusRecruit.R;

public class RecruitDetailTabActivity extends RecruitDetailActivity{
	
	private CompanyDetailFragment companyDetailFragment;
	private RecruitDetailDescriptionFragment recruitDetailFragment;
	private RecruitProcessFragment recruitProcessFragment;
	private CommentsFragment commentsFragment;

	private final int CATALOG_DESCRIPTION = 0;
	private final int CATALOG_PROCESS = 1;
	private final int CATALOG_COMPANY = 2;
	private final int CATALOG_COMMENTS = 3;

	private boolean descriptionDatInited = false;
	private boolean processDatInited = false;
	private boolean companyDatInited = false;
	private boolean commentsDatInited = false;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.menu_refresh_id:
			Log.i("refresh", "touched");
			// refresh event
			switch (viewPager.getCurrentItem()) {
			case CATALOG_DESCRIPTION:
				descriptionDatInited = false;
				break;
			case CATALOG_PROCESS:
				processDatInited = false;
				break;
			case CATALOG_COMPANY:
				companyDatInited = false;
				break;
			case CATALOG_COMMENTS:
				commentsDatInited = false;
				break;
			default:
				break;
			}
			Log.i("bug","refresh fragment");
			initFragmentData(viewPager.getCurrentItem());
			return true;
		}
		return false;
	}
	
	
	
	@Override
	protected void initTabData() {
		addTab("职位描述");
		addTab("招聘流程");
		addTab("公司简介");
		addTab("评论信息");
	}
	
	@Override
	protected void initFragment() {
		fragmentList = new ArrayList<BaseFragment>();
		companyDetailFragment = CompanyDetailFragment.newInstance(recruitDetail
				.getCompany());
		recruitProcessFragment = RecruitProcessFragment
				.newInstance(recruitDetail);
		recruitDetailFragment = RecruitDetailDescriptionFragment
				.newInstance(recruitDetail);
		commentsFragment = CommentsFragment.newInstance(
				recruitDetail.getTopicID(), recruitDetail);
		fragmentList.add(recruitDetailFragment);
		fragmentList.add(recruitProcessFragment);
		fragmentList.add(companyDetailFragment);
		fragmentList.add(commentsFragment);
		Log.i("ac", "init rdview");
	}
	
	@Override
	protected CommentsFragment getCommentsFragment() {
		return commentsFragment;
	}
	
	@Override
	protected int getCommentFragmentPosition() {
		return 3;
	}
	


	protected void initFragmentData(int type) {
		switch (type) {
		case CATALOG_DESCRIPTION:
			if (!descriptionDatInited) {
				recruitDetailFragment.initData(appContext);
				descriptionDatInited = true;
			}
			break;
		case CATALOG_PROCESS:
			Log.i("ac", "process");
			if (!processDatInited) {
				recruitProcessFragment.initData(appContext);
				processDatInited = true;
			}
			break;
		case CATALOG_COMPANY:
			if (!companyDatInited) {
				companyDetailFragment.initData(appContext);
				companyDatInited = true;
			}
			break;
		case CATALOG_COMMENTS:
			if (!commentsDatInited) {
				commentsFragment.setAppContext(appContext);
				commentsFragment.loadLvReplyData(0,
						UIHelper.LISTVIEW_ACTION_INIT);
				commentsDatInited = true;
			}
			break;
		default:
			break;
		}
		Log.i("ac", "load fragment data complete");
	}
}
