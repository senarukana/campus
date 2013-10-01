package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.activity.ScheduleActivity;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.Result;
import com.campusrecruit.bean.Schedule;
import com.campusrecruit.bean.Schedules;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.fragment.RecruitFragment;
import com.krislq.sliding.R;

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

public class ListViewScheduleAdapter extends BaseAdapter {

	private List<Schedules> listItem;
	private AppContext appContext;
	private Context context;
	private LayoutInflater listContainer;
	private int itemViewResource;
	private String beforeDate;

	static class ListItemView {
		LinearLayout itemMainLayout;
		LinearLayout dateLayout;
		TextView flagDate;
		TextView time;
		TextView companyName;
		TextView place;
		Button cancel;

	}

	public void setData(List<Schedules> data) {
		this.listItem = data;
	}

	public ListViewScheduleAdapter(Context context, AppContext appContext,
			int resource) {
		this.context = context;
		this.appContext = appContext;
		this.listContainer = LayoutInflater.from(context);
		this.listItem = new ArrayList<Schedules>();
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
		Log.i("bug", "schedule start");
		ListItemView holder = null;
		if (convertView == null) {
			holder = new ListItemView();

			convertView = this.listContainer.inflate(this.itemViewResource,
					null);
			// item
			holder.itemMainLayout = (LinearLayout) convertView
					.findViewById(R.id.schedule_day_item_main);
			holder.companyName = (TextView) convertView
					.findViewById(R.id.schedule_day_item_company_name);
			holder.dateLayout = (LinearLayout) convertView
					.findViewById(R.id.schedule_day_date_layout);
			holder.flagDate = (TextView) convertView
					.findViewById(R.id.schedule_day_date);
			holder.time = (TextView) convertView
					.findViewById(R.id.schedule_day_item_time);
			holder.place = (TextView) convertView
					.findViewById(R.id.schedule_day_item_place);
			holder.cancel = (Button) convertView
					.findViewById(R.id.schedule_day_item_cancel);
			convertView.setTag(holder);
		} else {
			holder = (ListItemView) convertView.getTag();
		}

		Schedules schedule = this.listItem.get(position);

		String friendlyTime = null;
		try {
			friendlyTime = StringUtils.friendly_happen_time(schedule.getDate());
		} catch (Exception e) {
			Log.i("bug", "time format is wrong careertalk adapter");
			friendlyTime = schedule.getDate();
		}
		Log.i("bug", "schedule ok");

		String trimmedDate = StringUtils.trimed_time(schedule.getDate());

		if ((schedule.getNum() == 0 || schedule.getNum() == 1)
				&& (beforeDate == null || !beforeDate.equals(trimmedDate) || position == 0)) {
			beforeDate = trimmedDate;
			holder.dateLayout.setVisibility(View.VISIBLE);
			holder.flagDate.setText(friendlyTime);
		} else {
			holder.dateLayout.setVisibility(View.GONE);
		}
		holder.companyName.setText(schedule.getCompanyName());
		holder.place.setText(schedule.getPlace());
		holder.time.setText(schedule.getTime());
		holder.itemMainLayout.setOnClickListener(new DetailListener(schedule
				.getScheduleID()));
		holder.cancel.setOnClickListener(new CancelListener(schedule));
		Log.i("bug", "schudle complete");
		return convertView;
	}

	private class DetailListener implements OnClickListener {
		public DetailListener(int careerTalkID) {
			this.careerTalkID = careerTalkID;
		}

		private int careerTalkID;

		@Override
		public void onClick(View v) {
			Log.i("schudle", "click");
			// 跳转到宣讲会详情
			UIHelper.showCareerTalkDetailByCalendar(context, careerTalkID);
		}

	}
	
	private class CancelListener implements OnClickListener {
		public CancelListener(Schedules schedules) {
			this.schedule = schedules;
			this.careerTalk = new CareerTalk();
			careerTalk.setCareerTalkID(schedules.getScheduleID());
			careerTalk.setCompanyName(schedules.getCompanyName());
			careerTalk.setCreatedTime(schedules.getTime());
			careerTalk.setDate(schedules.getDate());
		}

		private CareerTalk careerTalk;
		private Schedules schedule;

		@Override
		public void onClick(View v) {

			AlertDialog.Builder adb = new AlertDialog.Builder(context);
			adb.setTitle("删除日程?");
			adb.setMessage("你确定要删除[" + careerTalk.getCompanyName() + "]吗?");
			adb.setNegativeButton("取消", null);
			adb.setPositiveButton("确定", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					appContext.caeerTalkDetailJoin(careerTalk, false);
					new Thread() {
						public void run() {
							try {
								appContext.cancelAlarm(context, schedule);
								appContext.joinCareerTalk(
										careerTalk.getCareerTalkID(), false);
								for (Schedules s : ScheduleActivity.scheduleList) {
									if (s.getScheduleID() == schedule.getScheduleID()) {
										ScheduleActivity.scheduleList.remove(s);
										break;
									}
								}
							} catch (AppException e) {
								e.printStackTrace();
								e.makeToast(context);
							}

						}
					}.start();
					if (listItem.contains(schedule))
						listItem.remove(schedule);
					notifyDataSetChanged();
				}
			});
			adb.show();

		}

	}

}
