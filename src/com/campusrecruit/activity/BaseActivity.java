package com.campusrecruit.activity;

import com.campusrecruit.app.AppContext;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {
	protected AppContext appContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (AppContext)getApplication();
	}
}
