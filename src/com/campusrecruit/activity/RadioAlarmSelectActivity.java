package com.campusrecruit.activity;

import java.util.List;


public class RadioAlarmSelectActivity extends RadioListActivity {

	@Override
	protected List<String> getData() {
		return appContext.getAlarmTimelList();
	}
	
}
