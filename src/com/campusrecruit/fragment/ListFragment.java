package com.campusrecruit.fragment;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.bean.UserPreference;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.PullToRefreshListView;
import com.pcncad.campusRecruit.R;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public abstract class ListFragment extends SearchableFragment {

	private View listHeader;

	// header
	private TextView vIndustry;
	private TextView vPlace;
	private TextView vType;
	private TextView vSource;
	private TextView vSchool;

	protected ArrayList<Integer> selectCompanyIndustryList = new ArrayList<Integer>();
	protected ArrayList<Integer> selectCompanyTypeList = new ArrayList<Integer>();
	protected ArrayList<Integer> selectProvinceList = new ArrayList<Integer>();
	protected ArrayList<Integer> selectSourceList = new ArrayList<Integer>();
	protected ArrayList<Integer> selectSchoolList = new ArrayList<Integer>();

	private TextView vFamous;
	private TextView vAll;

	protected boolean isFamous;

	// flag: true recruit, false careertalk
	protected void initListHeaderView(LayoutInflater inflater,
			PullToRefreshListView pv, boolean flag) {
		listHeader = inflater.inflate(R.layout.list_header, null);
		vIndustry = (TextView) listHeader
				.findViewById(R.id.list_header_industry);
		vPlace = (TextView) listHeader.findViewById(R.id.list_header_place);
		vType = (TextView) listHeader.findViewById(R.id.list_header_type);
		vSource = (TextView) listHeader.findViewById(R.id.list_header_source);
		vSchool = (TextView) listHeader.findViewById(R.id.list_header_school);

		vFamous = (TextView) listHeader.findViewById(R.id.list_header_famous);
		vAll = (TextView) listHeader.findViewById(R.id.list_header_all);

		pv.addHeaderView(listHeader);
		Log.i("test", "init header view");
		vFamous.setOnClickListener(new TextFamousOnClickListener());
		vAll.setOnClickListener(new TextFamousOnClickListener());
		vAll.setTextColor(getResources().getColor(R.color.myblue));
		Log.i("test", "init header view2");
		vIndustry.setOnClickListener(new SelectedListOnClickListener());
		vPlace.setOnClickListener(new SelectedListOnClickListener());
		vType.setOnClickListener(new SelectedListOnClickListener());
		vSource.setOnClickListener(new SelectedListOnClickListener());
		vSchool.setOnClickListener(new SelectedListOnClickListener());
		initSelectedList();

		if (!flag) {
			vSource.setVisibility(View.GONE);
			vSchool.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void showLoadProgress(View v) {
		super.showLoadProgress(v);
		listHeader.setVisibility(View.GONE);
	}

	@Override
	protected void hideLoadProgress(View v) {
		super.hideLoadProgress(v);
		listHeader.setVisibility(View.VISIBLE);
	}

	@Override
	protected void hideLoadProgressWithError(View v) {
		super.hideLoadProgressWithError(v);
		listHeader.setVisibility(View.GONE);
	}

	@Override
	protected void beginSearch(View v) {
		super.beginSearch(v);
		listHeader.setVisibility(View.GONE);
	}

	@Override
	protected void completeSearch(View v, int num, String searchStr) {
		super.completeSearch(v, num, searchStr);
		listHeader.setVisibility(View.GONE);
	}

	@Override
	protected void completeSearchWithEmpty(View v, String searchStr) {
		super.completeSearchWithEmpty(v, searchStr);
		listHeader.setVisibility(View.GONE);
	}

	@Override
	protected void completeSearchWithError(View v) {
		super.completeSearchWithError(v);
		listHeader.setVisibility(View.GONE);
	}

	protected abstract void refreshData();

	private class SelectedListOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.list_header_place:
				Log.i("selection",
						"province list size is" + selectProvinceList.size());
				UIHelper.showProvince(ListFragment.this, selectProvinceList);
				return;
			case R.id.list_header_industry:
				Log.i("selection", "province list size is"
						+ selectCompanyIndustryList.size());
				UIHelper.showCompanyIndustry(ListFragment.this,
						selectCompanyIndustryList);
				return;
			case R.id.list_header_type:
				UIHelper.showCompanyType(ListFragment.this,
						selectCompanyTypeList);
				return;
			case R.id.list_header_source:
				UIHelper.showDataSources(ListFragment.this, selectSourceList);
				return;
			case R.id.list_header_school:
				UIHelper.showSchoolMultiple(ListFragment.this, selectSchoolList);
				return;
			}
		}
	}

	private class TextFamousOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.list_header_famous:
				if (isFamous) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return;
				}
				vAll.setTextColor(getResources()
						.getColor(R.color.listitem_gray));
				vFamous.setTextColor(getResources().getColor(R.color.myblue));
				isFamous = true;
				break;
			case R.id.list_header_all:
				if (!isFamous) {
					UIHelper.ToastMessage(getActivity(),
							getString(R.string.sort_same), Toast.LENGTH_SHORT);
					return;
				}
				vAll.setTextColor(getResources().getColor(R.color.myblue));
				vFamous.setTextColor(getResources().getColor(
						R.color.listitem_gray));
				isFamous = false;
				break;
			}
			refreshData();
		}

	}

	public void initSelectedList() {
		selectCompanyIndustryList.clear();
		selectCompanyTypeList.clear();
		selectProvinceList.clear();
		selectSourceList.clear();
		UserPreference preference = appContext.getLoginUser().getPreference();
		for (String s : preference.getCompanyType().split(",")) {
			selectCompanyTypeList.add(Integer.valueOf(s));
		}
		for (String s : preference.getCompanyIndustry().split(",")) {
			selectCompanyIndustryList.add(Integer.valueOf(s));
		}
		for (String s : preference.getProvince().split(",")) {
			selectProvinceList.add(Integer.valueOf(s));
		}
		for (String s : preference.getSources().split(",")) {
			selectSourceList.add(Integer.valueOf(s));
		}
		
		if (selectSchoolList.size() == 0) {
			selectSchoolList.add(0);
		}

		fillTextView(selectProvinceList, appContext.getProvinceList(), vPlace,
				"地区");
		fillTextView(selectCompanyIndustryList,
				appContext.getCompanyIndustryList(), vIndustry, "行业");
		fillTextView(selectCompanyTypeList, appContext.getCompanyTypeList(),
				vType, "性质");
		fillTextView(selectSourceList, appContext.getSourceList(), vSource,
				"来源");
		fillTextView(selectSchoolList, appContext.getSchoolMultipleList(),
				vSchool, "学校");

	}

	private void fillTextView(List<Integer> selectList, List<String> data,
			TextView v, String multiText) {
		if (selectList.size() > 1) {
			if ((selectList.size() >= data.size() - 1)
					|| selectList.contains(0))
				v.setText("全" + multiText);
			else
				v.setText("多" + multiText);
		} else if (selectList.size() == 1) {
			if (selectList.contains(0)) {
				v.setText("全" + multiText);
			} else {
				v.setText(data.get(selectList.get(0)));
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("selection", "list result!!!!!!!!!!1");
		boolean flag = false;
		if (requestCode == UIHelper.REQUEST_PROVINCE_FOR_RESULT
				&& resultCode == Activity.RESULT_OK) {
			selectProvinceList.clear();
			ArrayList<Integer> list = data
					.getIntegerArrayListExtra("selection");
			for (Integer i : list) {
				selectProvinceList.add(i);
			}
			fillTextView(selectProvinceList, appContext.getProvinceList(),
					vPlace, "地区");
			flag = true;
		} else if (requestCode == UIHelper.REQUEST_COMPANY_INDUSTRY_RESULT
				&& resultCode == Activity.RESULT_OK) {
			selectCompanyIndustryList.clear();
			ArrayList<Integer> list = data
					.getIntegerArrayListExtra("selection");
			selectCompanyIndustryList.addAll(list);
			fillTextView(selectCompanyIndustryList,
					appContext.getCompanyIndustryList(), vIndustry, "行业");
			flag = true;

		} else if (requestCode == UIHelper.REQUEST_COMPANY_TYPE_FOR_RESULT
				&& resultCode == Activity.RESULT_OK) {
			selectCompanyTypeList.clear();
			ArrayList<Integer> list = data
					.getIntegerArrayListExtra("selection");
			for (Integer i : list) {
				selectCompanyTypeList.add(i);
			}
			fillTextView(selectCompanyTypeList,
					appContext.getCompanyTypeList(), vType, "性质");
			flag = true;

		} else if (requestCode == UIHelper.REQUEST_DATA_SOURCE_RESULT
				&& resultCode == Activity.RESULT_OK) {
			selectSourceList.clear();
			ArrayList<Integer> list = data
					.getIntegerArrayListExtra("selection");
			for (Integer i : list) {
				selectSourceList.add(i);
			}
			fillTextView(selectSourceList, appContext.getSourceList(), vSource,
					"来源");
			flag = true;
		} else if (requestCode == UIHelper.REQUEST_SCHOOL_RESULT
				&& resultCode == Activity.RESULT_OK) {
			selectSchoolList.clear();
			ArrayList<Integer> list = data
					.getIntegerArrayListExtra("selection");
			for (Integer i : list) {
				selectSchoolList.add(i);
			}
			Log.i("test",selectSchoolList.toString());
			fillTextView(selectSchoolList, appContext.getSchoolMultipleList(),
					vSchool, "学校");
			flag = true;
		}
		if (flag) {
			refreshData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
