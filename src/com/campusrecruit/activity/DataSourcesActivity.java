package com.campusrecruit.activity;

import java.util.List;


public class DataSourcesActivity extends SelectListActivity {
	@Override
	protected List<String> getData() {
		return appContext.getSourceList();
	}
	
}
