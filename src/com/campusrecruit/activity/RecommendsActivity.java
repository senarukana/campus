package com.campusrecruit.activity;

import java.util.List;

import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.common.UIHelper;
import com.pcncad.campusRecruit.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class RecommendsActivity extends BaseActivity{
	private Button vProvinceButton;
	private ToggleButton vCareerNotifyToggle;
	private ToggleButton vRecruitNotifyToggle;
	
	private List<Integer> selectedProvince;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	private void initView() {
		vProvinceButton = (Button)findViewById(R.id.btn_recommends_province);
		vProvinceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				UIHelper.showProvince(RecommendsActivity.this);
			}
			
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		if (data == null)
			return;

		selectedProvince = (List<Integer>) data.getIntegerArrayListExtra("selectProvinces");
	}
}
