package com.campusrecruit.activity;

import java.util.List;


public class CompanyTypeActivity extends SelectListActivity {
	@Override
	protected List<String> getData() {
		return appContext.getCompanyTypeList();
	}
	
}
