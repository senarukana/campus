package com.campusrecruit.fragment;

import com.campusrecruit.app.AppContext;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.LinearLayout;

public class BaseFragment extends Fragment {
	protected AppContext appContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (appContext == null) {
			appContext = (AppContext) getActivity().getApplication();
		}
	}

}
