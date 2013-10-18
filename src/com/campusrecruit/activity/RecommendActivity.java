package com.campusrecruit.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.UserPreference;
import com.campusrecruit.common.UIHelper;
import com.pcncad.campusRecruit.R;
import com.tencent.weibo.api.PrivateAPI;

public class RecommendActivity extends BaseActivity {

	private ToggleButton industryComputeToggle = null;
	private ToggleButton industryCommunicationToggle = null;
	private ToggleButton industryElectronicToggle = null;
	private ToggleButton industryEconomyToggle = null;
	private ToggleButton propertySoe = null;
	private ToggleButton propertyPrivate = null;
	private ToggleButton propertyForeign = null;
	private ToggleButton typeCarrertalk = null;
	private ToggleButton typeRecruit = null;
	private ToggleButton typeMessage = null;
	private ToggleButton typeReply = null;
	private ToggleButton sourceDajie = null;
	private ToggleButton sourceByr = null;
	private ToggleButton sourceShuiMu = null;
	private ToggleButton sourceHaiTou = null;
	private View notifyLayout = null;

	private boolean flag = true;
	/*
	 * private ToggleButton typeCarrertalk = null; private ToggleButton
	 * typeRecruit = null; private ToggleButton typeMessage = null; private
	 * ToggleButton typeReply = null;
	 */

	private ProgressDialog vProgress;
	private RelativeLayout addressSelect = null;
	private TextView selectedProvinceTextview = null;
	private TextView selectedProvinceTextviewShow = null;
	private StringBuilder provinceBuilder;
	private Map<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
	private ArrayList<Integer> selectProvinceList = new ArrayList<Integer>();

	private Handler preferenceHandler;

	

	private boolean init;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend);

		init = getIntent().getBooleanExtra("init", true);

		getActionBar().setTitle("偏好设置");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initView();
		if (appContext.getLoginUser().getPreference() != null) {
			Log.i("test", appContext.getLoginUser().getPreference()
					.getNotifyType()
					+ "Xxxxxxxxxxxxxxxxx");
			Log.i("recommend", "not null");
			initPreference();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_recommend, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.recommend_save:
			int count = 0;
			for (String key : map.keySet()) {
				if (map.get(key).isEmpty()) {
					String v = null;
					if (key.equals("property"))
						v = "公司性质";
					else if (key.equals("industry"))
						v = "公司类型";
					else if (key.equals("type"))
						v = "推送消息类型";
					else
						v = "数据来源";
					UIHelper.ToastMessage(RecommendActivity.this,
							getString(R.string.recommend_hint, v));
					return true;
				} else {
					count += map.get(key).size();
				}
			}
			map.put("province", selectProvinceList);
			if (selectProvinceList.size() == 0) {
				UIHelper.ToastMessage(RecommendActivity.this, "还未选择心仪的工作地点");
				return true;
			}

			/*if (count < 3 && flag) {
				AlertDialog.Builder adb = new AlertDialog.Builder(
						RecommendActivity.this);
				adb.setTitle(R.string.recommend_too_litter_hint);
				adb.setMessage("过少的公司特征，将有可能造成信息量过小");
				adb.setNegativeButton("取消", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						flag = false;
						return;
					}
				});
				adb.show();
				
				AlertDialog.Builder adbConfirm = new AlertDialog.Builder(
						RecommendActivity.this);
				adbConfirm.setPositiveButton("确定",  new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setPreference();
						return;
					}
				});
				adbConfirm.show();
			}*/
			/*if (flag) {
				setPreference();
			} else {
				return true;
			}*/

			/*
			 * Log.i("map", map.toString()); boolean mapEmpty = map.isEmpty();
			 * boolean keyEmpty = map.keySet().size() != 3; boolean valueEmpty =
			 * false; Set<String> names = map.keySet(); Log.i("map set",
			 * names.toString()); Iterator<String> l = names.iterator(); int
			 * count = 0; while (l.hasNext()) { String name = l.next(); if
			 * (map.get(name).isEmpty()) { valueEmpty = true; break; } else {
			 * count += map.get(name).size(); } }
			 * 
			 * if (mapEmpty || valueEmpty) {
			 * UIHelper.ToastMessage(RecommendActivity.this,
			 * "选上公司信息，这将对信息过滤很重要", Toast.LENGTH_SHORT); }
			 * 
			 * if (mapEmpty || keyEmpty || valueEmpty) {
			 * Toast.makeText(RecommendActivity.this, "亲，您有类别没有选择哦",
			 * Toast.LENGTH_SHORT).show(); } else { setPreference(); }
			 */
			setPreference();
			return true;
		default:
			return false;
		}
	}

	private void initView() {
		/* btnSubmit = (Button)findViewById(R.id.recommend_submit_button); */
		addressSelect = (RelativeLayout) findViewById(R.id.recommend_address_select);
		notifyLayout = findViewById(R.id.recommend_notify_layout);

		selectedProvinceTextview = (TextView) findViewById(R.id.selected_province_textview);
		selectedProvinceTextviewShow = (TextView) findViewById(R.id.selected_province_text_show_view);
		industryComputeToggle = (ToggleButton) findViewById(R.id.recommend_industry_computer_toggle);
		industryCommunicationToggle = (ToggleButton) findViewById(R.id.recommend_industry_communication_toggle);
		industryEconomyToggle = (ToggleButton) findViewById(R.id.recommend_industry_economic_toggle);
		industryElectronicToggle = (ToggleButton) findViewById(R.id.recommend_industry_electronic_toggle);
		propertySoe = (ToggleButton) findViewById(R.id.recommend_property_soe_toggle);
		propertyPrivate = (ToggleButton) findViewById(R.id.recommend_property_private_toggle);
		propertyForeign = (ToggleButton) findViewById(R.id.recommend_property_foreign_toggle);
		typeCarrertalk = (ToggleButton) findViewById(R.id.recommend_type_careertalk_toggle);
		typeRecruit = (ToggleButton) findViewById(R.id.recommend_type_recruit_toggle);
		typeMessage = (ToggleButton) findViewById(R.id.recommend_type_message_toggle);
		typeReply = (ToggleButton) findViewById(R.id.recommend_type_reply_toggle);
		sourceDajie = (ToggleButton) findViewById(R.id.recommend_source_dajie_toggle);
		sourceByr = (ToggleButton) findViewById(R.id.recommend_source_byr_toggle);
		sourceShuiMu = (ToggleButton) findViewById(R.id.recommend_source_shuimu_toggle);
		sourceHaiTou = (ToggleButton) findViewById(R.id.recommend_source_haitou_toggle);

		industryComputeToggle.setOnCheckedChangeListener(toggleListener);
		industryCommunicationToggle.setOnCheckedChangeListener(toggleListener);
		industryElectronicToggle.setOnCheckedChangeListener(toggleListener);
		industryEconomyToggle.setOnCheckedChangeListener(toggleListener);
		propertySoe.setOnCheckedChangeListener(toggleListener);
		propertyPrivate.setOnCheckedChangeListener(toggleListener);
		propertyForeign.setOnCheckedChangeListener(toggleListener);
		typeCarrertalk.setOnCheckedChangeListener(toggleListener);
		typeRecruit.setOnCheckedChangeListener(toggleListener);
		typeReply.setOnCheckedChangeListener(toggleListener);
		typeMessage.setOnCheckedChangeListener(toggleListener);
		sourceDajie.setOnCheckedChangeListener(toggleListener);
		sourceByr.setOnCheckedChangeListener(toggleListener);
		sourceShuiMu.setOnCheckedChangeListener(toggleListener);
		sourceHaiTou.setOnCheckedChangeListener(toggleListener);
		
		provinceBuilder = new StringBuilder();

		addressSelect.setOnClickListener(addressSelectListener);
		if (!init) {
			notifyLayout.setVisibility(View.VISIBLE);
		} else {
			notifyLayout.setVisibility(View.GONE);
		}
		/* btnSubmit.setOnClickListener(buttonListener); */
	}

	private void initPreference() {
		UserPreference preference = appContext.getLoginUser().getPreference();
		String[] provinces = preference.getProvince().split(",");
		String[] industries = preference.getCompanyIndustry().split(",");
		String[] companyTypes = preference.getCompanyType().split(",");
		String[] notifyTypes = preference.getNotifyType().split(",");
		String[] sources = preference.getSources().split(",");
		
		for (int i = 0; i < industries.length; i++) {
			if (industries[i].equals("1"))
				industryComputeToggle.setChecked(true);
			if (industries[i].equals("2"))
				industryCommunicationToggle.setChecked(true);
			if (industries[i].equals("3"))
				industryElectronicToggle.setChecked(true);
			if (industries[i].equals("4"))
				industryEconomyToggle.setChecked(true);
		}
		for (int i = 0; i < companyTypes.length; i++) {
			if (companyTypes[i].equals("1"))
				propertySoe.setChecked(true);
			if (companyTypes[i].equals("2"))
				propertyPrivate.setChecked(true);
			if (companyTypes[i].equals("3"))
				propertyForeign.setChecked(true);
		}
		
		for (int i = 0; i < notifyTypes.length; i++) {
			if (notifyTypes[i].equals("1"))
				typeCarrertalk.setChecked(true);
			if (notifyTypes[i].equals("2"))
				typeRecruit.setChecked(true);
			if (notifyTypes[i].equals("3"))
				typeMessage.setChecked(true);
			if (notifyTypes[i].equals("4"))
				typeReply.setChecked(true);
		}
		for (int i = 0; i < sources.length; i++) {
			if (sources[i].equals("1"))
				sourceDajie.setChecked(true);
			if (sources[i].equals("2"))
				sourceByr.setChecked(true);
			if (sources[i].equals("3"))
				sourceShuiMu.setChecked(true);
			if (sources[i].equals("4")) 
				sourceHaiTou.setChecked(true);
		}
		 

		selectProvinceList.clear();
		provinceBuilder = new StringBuilder();
		for (int i = 0; i < provinces.length; i++) {
			selectProvinceList.add(Integer.parseInt(provinces[i]));
			provinceBuilder.append(appContext.getProvinceList().get(
					Integer.parseInt(provinces[i]))
					+ " ");
			Log.i("recommend",
					appContext.getProvinceList().get(
							Integer.parseInt(provinces[i])));
		}
		selectedProvinceTextview.setText("");
		selectedProvinceTextviewShow.setText(provinceBuilder.toString() + "");
	}

	private void setPreference() {
		vProgress = ProgressDialog
				.show(this, null, "稍等片刻，准备数据中···", true, true);
		preferenceHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				vProgress.dismiss();
				Log.i("test", "preferenceHandler");
				if (msg.what == 1) {
					if (init) {
						UIHelper.showHome(RecommendActivity.this);
					} else {
						Intent intent = getIntent();
						setResult(RESULT_OK, intent);
						finish();
					}
				} else {
					((AppException) (msg.obj))
							.makeToast(RecommendActivity.this);
				}
			}

		};
		new Thread() {
			Message msg = new Message();

			@Override
			public void run() {
				try {
					appContext.setPreference(map,init);
					msg.what = 1;
				} catch (AppException e) {
					
					msg.what = -1;
					msg.obj = e;
				}
				preferenceHandler.sendMessage(msg);
			}
		}.start();
	}

	private OnCheckedChangeListener toggleListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			String tag = (String) buttonView.getTag();
			String[] tags = tag.split("_");
			String tag_pre = tags[0];
			Integer tag_last = Integer.parseInt(tags[1]);
			if (isChecked) {
				if (map.containsKey(tag_pre)) {
					map.get(tag_pre).add(tag_last);
				} else {
					ArrayList<Integer> list = new ArrayList<Integer>();
					list.add(tag_last);
					map.put(tag_pre, list);
				}
			} else {
				map.get(tag_pre).remove(tag_last);
			}

			Log.i("map", map.toString());
		}
	};
	
	private OnClickListener addressSelectListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			UIHelper.showProvince(RecommendActivity.this, selectProvinceList);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == UIHelper.REQUEST_PROVINCE_FOR_RESULT && resultCode == RESULT_OK) {
			selectProvinceList.clear();
			provinceBuilder = new StringBuilder();
			ArrayList<Integer> list = data
					.getIntegerArrayListExtra("selection");
			for (Integer i : list) {
				selectProvinceList.add(i);
				provinceBuilder.append(appContext.getProvinceList().get(i) + " ");
			}
			selectedProvinceTextviewShow.setText(provinceBuilder.toString());
		} 

	}

	private class toggleButtonListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			String tag = (String) buttonView.getTag();
		}

	}

}
