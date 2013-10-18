package com.campusrecruit.activity;

import java.util.List;


public class SchoolActivity extends RadioListActivity {

	@Override
	protected List<String> getData() {
		return appContext.getSchoolList();
	}
	
}
