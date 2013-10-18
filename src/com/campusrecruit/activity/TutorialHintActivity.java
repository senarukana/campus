package com.campusrecruit.activity;

import com.campusrecruit.common.UIHelper;
import com.pcncad.campusRecruit.R;

import android.os.Bundle;
import android.view.View;

public class TutorialHintActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.tutorial_hint);
		int image_id = getIntent().getIntExtra("image", 0);
		View v = findViewById(R.id.tutorial_hint_layout);
		v.setBackgroundDrawable(getResources().getDrawable(image_id));
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_OK);
				finish();
			}
		});
		super.onCreate(savedInstanceState);
	}
}
