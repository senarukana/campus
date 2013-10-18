package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.CareerTalk;
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

public class ListViewCareerTalkFavorateAdapter extends BaseAdapter {

	private List<CareerTalk> listItem;
	private AppContext appContext;
	private Context context;
	private LayoutInflater listContainer;
	private int itemViewResource;
	private String beforeDate;

	static class ListItemView {
		LinearLayout clickLayout;
		ImageView newFlag;
		LinearLayout dateLayout;
		TextView flagDate;
		Button cancelJoin;
		TextView companyName;
		TextView schoolName;
		TextView place;
		TextView time;
		TextView date;

	}

	public void setData(List<CareerTalk> data) {
		this.listItem = data;
	}

	public ListViewCareerTalkFavorateAdapter(Context context,
			AppContext appContext, int resource) {
		this.context = context;
		this.appContext = appContext;
		this.listContainer = LayoutInflater.from(context);
		this.listItem = new ArrayList<CareerTalk>();
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
		Log.i("info", "favorate start");
		ListItemView holder = null;
		if (convertView == null) {
			holder = new ListItemView();

			convertView = this.listContainer.inflate(this.itemViewResource,
					null);
			// item
			holder.dateLayout = (LinearLayout) convertView
					.findViewById(R.id.career_favorate_date_layout);
			holder.flagDate = (TextView) convertView
					.findViewById(R.id.career_favorate_date);
			holder.clickLayout = (LinearLayout) convertView
					.findViewById(R.id.career_favorate_click_layout);
			holder.companyName = (TextView) convertView
					.findViewById(R.id.career_favorate_company_name);
			holder.time = (TextView) convertView
					.findViewById(R.id.career_favorate_time);
			holder.date = (TextView) convertView
					.findViewById(R.id.career_favorate_simple_date);
			holder.place = (TextView) convertView
					.findViewById(R.id.career_favorate_place);
			holder.schoolName = (TextView) convertView
					.findViewById(R.id.career_favorate_school);
			holder.cancelJoin = (Button) convertView
					.findViewById(R.id.career_favorate_cancel);

			convertView.setTag(holder);
		} else {
			holder = (ListItemView) convertView.getTag();
		}
		Log.i("test","career begin");

		CareerTalk careerTalk = this.listItem.get(position);

		holder.companyName.setText(careerTalk.getCompanyName());
		holder.companyName.setTag(careerTalk.getCareerTalkID());
		holder.place.setText(careerTalk.getPlace());
		String friendlyTime = null;
		try {
			friendlyTime = StringUtils.friendly_happen_time(careerTalk
					.getDate());
		} catch (Exception e) {
			Log.i("bug", "time format is wrong careertalk adapter");
			friendlyTime = careerTalk.getDate();
		}
		holder.date.setText(friendlyTime);
		Log.i("test","career date");
		String trimmedDate = StringUtils.trimed_time(careerTalk.getDate());
		// 0 for init, 1 for header, 2 for others
		if (position == 0 || ((careerTalk.getFlag() == 0 || careerTalk.getFlag() == 1)
				&& (beforeDate == null || !beforeDate.equals(trimmedDate)))) {
			beforeDate = trimmedDate;
			holder.dateLayout.setVisibility(View.VISIBLE);
			holder.flagDate.setText(friendlyTime);
			careerTalk.setFlag(1);
		} else {
			holder.dateLayout.setVisibility(View.GONE);
			careerTalk.setFlag(2);
		}

		holder.time.setText(careerTalk.getTime());
		holder.schoolName.setText(careerTalk.getSchoolName());

		holder.clickLayout.setOnClickListener(new DetailListener(careerTalk));
		holder.cancelJoin.setOnClickListener(new JoinsListener(careerTalk));
		Log.i("test","career favorate");
		return convertView;
	}

	private class DetailListener implements OnClickListener {
		public DetailListener(CareerTalk careerTalk) {
			this.careerTalk = careerTalk;
		}

		private CareerTalk careerTalk;

		@Override
		public void onClick(View v) {
			// 跳转到招聘详情
			UIHelper.showCareerTalkDetail(context, careerTalk, true);
		}

	}

	private class JoinsListener implements OnClickListener {
		public JoinsListener(CareerTalk careerTalk) {
			this.careerTalk = careerTalk;
		}

		private CareerTalk careerTalk;

		@Override
		public void onClick(View v) {

			Log.i("tt", "click");
			AlertDialog.Builder adb = new AlertDialog.Builder(context);
			adb.setTitle("删除收藏?");
			adb.setMessage("你确定要删除[" + careerTalk.getCompanyName() + "]收藏吗?");
			adb.setNegativeButton("取消", null);
			adb.setPositiveButton("确定", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					appContext.caeerTalkDetailJoin(careerTalk, false);
					new Thread() {
						public void run() {
							try {
//								appContext.cancelAlarm(context, careerTalk);
								appContext.joinCareerTalk(
										careerTalk.getCareerTalkID(), false);
							} catch (AppException e) {
								
								e.makeToast(context);
							}

						}
					}.start();
					if (listItem.contains(careerTalk))
						listItem.remove(careerTalk);
					notifyDataSetChanged();
				}
			});
			adb.show();

		}

	}

}
