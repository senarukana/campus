package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.Result;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.fragment.RecruitFragment;
import com.pcncad.campusRecruit.R;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewRecruitFavorateAdapter extends BaseAdapter {

	private List<Recruit> listItem;
	
	private AppContext appContext;
	private Context context;
	private LayoutInflater listContainer;
	private int itemViewResource;
	private String beforeDate;

	static class ListItemView {
		LinearLayout clickLayout;
		ImageView famousFlag;
		ImageView newFlag;

		LinearLayout dateLayout;
		TextView companyName;
		TextView createdTime;
		TextView position;
		TextView place;
		TextView companyIndustry;
		TextView companyType;
		Button cancelJoin;

	}

	public void setData(List<Recruit> data) {
		this.listItem = data;
	}

	public ListViewRecruitFavorateAdapter(Context context,
			AppContext appContext, int resource) {
		this.context = context;
		this.appContext = appContext;
		this.listContainer = LayoutInflater.from(context);
		this.listItem = new ArrayList<Recruit>();
		this.itemViewResource = resource;
	}

	@Override
	public int getCount() {
		return this.listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("test", "favorate start");
		ListItemView holder = null;
		if (convertView == null) {
			holder = new ListItemView();

			convertView = this.listContainer.inflate(this.itemViewResource,
					null);
			// item
			holder.dateLayout = (LinearLayout) convertView
					.findViewById(R.id.recruit_favorate_date_layout);
			holder.clickLayout = (LinearLayout) convertView
					.findViewById(R.id.recruit_favorate_click_layout);
			holder.companyName = (TextView) convertView
					.findViewById(R.id.recruit_favorate_company_name);
			holder.createdTime = (TextView) convertView
					.findViewById(R.id.recruit_favorate_created_time);
			holder.position = (TextView) convertView
					.findViewById(R.id.recruit_favorate_position);
			holder.place = (TextView) convertView
					.findViewById(R.id.recruit_favorate_place);
			holder.companyType = (TextView) convertView
					.findViewById(R.id.recruit_favorate_company_type);
			holder.companyIndustry = (TextView) convertView
					.findViewById(R.id.recruit_favorate_company_industry);
			holder.cancelJoin = (Button) convertView
					.findViewById(R.id.recruit_favorate_cancel);

			convertView.setTag(holder);
		} else {
			holder = (ListItemView) convertView.getTag();
		}

		Recruit recruit = this.listItem.get(position);

		// date header
		String friendlyTime = null;
		try {
			friendlyTime = StringUtils.friendly_created_time(recruit
					.getCreatedTime());
		} catch (Exception e) {
			Log.i("bug", "time format is wrong recruit adapter");
			friendlyTime = recruit.getCreatedTime();
		}
		String trimmedDate = StringUtils.trimed_time(recruit.getCreatedTime());
		if (position == 0 ||((recruit.getFlag() == 0 || recruit.getFlag() == 1)
				&& (beforeDate == null || !beforeDate.equals(trimmedDate)))) {
			beforeDate = trimmedDate;
			holder.dateLayout.setVisibility(View.VISIBLE);
			holder.createdTime.setText(friendlyTime);
			Log.i("test","test1");
			recruit.setFlag(1);
		} else {
			holder.dateLayout.setVisibility(View.GONE);
			recruit.setFlag(2);
		}
		Log.i("test","test2");
		
		

		holder.companyName.setText(recruit.getCompany().getCompanyName());
		holder.companyName.setTag(recruit.getRecruitID());
		holder.position.setText(recruit.getPosition());
		holder.place.setText(recruit.getPlace());
		Log.i("test","test3");
		holder.companyIndustry.setText(recruit.getCompany().getIndustry());
		holder.companyType.setText(recruit.getCompany().getType());
		Log.i("test","test4");
		holder.clickLayout.setOnClickListener(new DetailListener(recruit));
		holder.cancelJoin.setOnClickListener(new JoinsListener(recruit));
		Log.i("test", "favorate complete");

		return convertView;
	}

	private class DetailListener implements OnClickListener {
		public DetailListener(Recruit recruit) {
			this.recruit = recruit;
		}

		private Recruit recruit;

		@Override
		public void onClick(View v) {
			Log.i("recruit", recruit.getPosition());
			// 跳转到招聘详情
			if (recruit.getSourceFrom().equals("大街网")) {
				UIHelper.showRecruitDetail(context, recruit, true);
			} else {
				UIHelper.showRecruitSimpleDetail(context, recruit, true);
			}
		}

	}

	private class JoinsListener implements OnClickListener {
		public JoinsListener(Recruit recruit) {
			this.recruit = recruit;
		}

		private Recruit recruit;

		@Override
		public void onClick(View v) {
			Log.i("tt", "click");
			AlertDialog.Builder adb = new AlertDialog.Builder(context);
			adb.setTitle("删除收藏?");
			adb.setMessage("你确定要删除" + recruit.getCompany().getCompanyName() + "吗?");
			adb.setNegativeButton("取消", null);
			adb.setPositiveButton("确定", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.i("test", "detail join");
					appContext.recruitDetailJoin(recruit, false);
					Log.i("test", "detail join");
					new Thread() {
						public void run() {
							try {
								appContext.joinRecruit(recruit.getRecruitID(),
										false);
							} catch (AppException e) {
								
								e.makeToast(context);
							}

						}
					}.start();
					if (listItem.contains(recruit))
						listItem.remove(recruit);
					notifyDataSetChanged();
				}
			});
			adb.show();
		}

	}

}
