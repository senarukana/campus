package com.campusrecruit.activity;

import java.util.List;


public class SchoolMultipleActivity extends SelectListActivity {

	@Override
	protected List<String> getData() {
		return appContext.getSchoolMultipleList();
	}
	
}
