package com.campusrecruit.activity;

import java.util.List;


public class CompanyIndustryActivity extends SelectListActivity {
	@Override
	protected List<String> getData() {
		return appContext.getCompanyIndustryList();
	}
}
