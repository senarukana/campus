package com.campusrecruit.activity;

import java.util.List;


public class ProvinceActivity extends SelectListActivity {

	@Override
	protected List<String> getData() {
		return appContext.getProvinceList();
	}
	
}
