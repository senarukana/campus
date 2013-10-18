package com.campusrecruit.activity;

import java.util.List;


public class MajorActivity extends RadioListActivity {

	@Override
	protected List<String> getData() {
		return appContext.getMajorList();
	}
	
}
