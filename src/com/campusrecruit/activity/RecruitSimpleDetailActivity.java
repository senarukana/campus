package com.campusrecruit.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.campusrecruit.common.UIHelper;
import com.campusrecruit.fragment.BaseFragment;
import com.campusrecruit.fragment.CommentsFragment;
import com.campusrecruit.fragment.CompanyDetailFragment;
import com.campusrecruit.fragment.RecruitSimpleDetailFragment;
import com.pcncad.campusRecruit.R;

public class RecruitSimpleDetailActivity extends RecruitDetailActivity{

	private RecruitSimpleDetailFragment detailFragment;
//	private CompanyDetailFragment companyFragment;
	private CommentsFragment commentsFragment;
	
	private final int CATALOG_DETAIL = 0;
//	private final int CATALOG_COMPANY = 1;
	private final int CATALOG_COMMENTS = 1;
	
	private boolean detailDatInited = false;
//	private boolean companyDatInited = false;
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
			case CATALOG_DETAIL:
				detailDatInited = false;
				break;
			/*case CATALOG_COMPANY:
				companyDatInited = false;
				break;*/
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
	protected void initFragment() {
		fragmentList = new ArrayList<BaseFragment>();
		detailFragment = RecruitSimpleDetailFragment.newInstance(recruitDetail);
//		companyFragment = CompanyDetailFragment.newInstance(recruitDetail.getCompany());
		commentsFragment = CommentsFragment.newInstance(
				recruitDetail.getTopicID(), recruitDetail);
		fragmentList.add(detailFragment);
//		fragmentList.add(companyFragment);
		fragmentList.add(commentsFragment);
	}

	@Override
	protected int getCommentFragmentPosition() {
		return 1;
	}

	@Override
	protected CommentsFragment getCommentsFragment() {
		return commentsFragment;
	}

	@Override
	protected void initFragmentData(int type) {
		switch (type) {
		case CATALOG_DETAIL:
			if (!detailDatInited) {
				detailFragment.initData(appContext);
				detailDatInited = true;
			}
			break;
		/*case CATALOG_COMPANY:
			if (!companyDatInited) {
				companyFragment.initData(appContext);
				companyDatInited = true;
			}
			break;*/
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
	}

	@Override
	protected void initTabData() {
		Log.i("test","init tab data");
		addTab("校园招聘");
//		addTab("公司介绍");
		addTab("评论信息");
		Log.i("test","init tab data complete");
	}

}
