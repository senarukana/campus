package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.campusrecruit.common.ScheduleAlarmBroadcastReceiver;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.fragment.RecruitFragment;
import com.pcncad.campusRecruit.R;

import android.R.integer;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ListViewScheduleAdapter extends BaseAdapter {

	private List<Schedules> listItem;

	private Context context;
	private AppContext appContext;
	private LayoutInflater listContainer;
	private int itemViewResource;
	private String beforeDate;
	private Calendar calSet;
	private Calendar calAlarm;
	private String newAlarmTime;

	private int _pos = -1;

	private TimePickerDialog timePickerDialog;
	private DatePickerDialog datePickerDialog;
	final static int RQS_1 = 1;

	static class ListItemView {
		LinearLayout itemMainLayout;
		LinearLayout dateLayout;
		TextView flagDate;
		TextView time;
		TextView companyName;
		Button buttonstartSetDialog;
		TextView place;
		Button cancel;
		TextView textAlarmPrompt;
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
			holder.textAlarmPrompt = (TextView) convertView
					.findViewById(R.id.schedule_day_item_alarm_time);
			holder.buttonstartSetDialog = (Button) convertView
					.findViewById(R.id.schedule_day_item_set_alarm_date);
			convertView.setTag(holder);
		} else {
			holder = (ListItemView) convertView.getTag();
		}

		final Schedules schedule = this.listItem.get(position);

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
		Log.i("alarmTime", schedule.getAlarmTime());
		if (_pos == position) {
			holder.textAlarmPrompt.setTextColor(context.getResources()
					.getColor(R.color.red));
			holder.textAlarmPrompt.setText(newAlarmTime);
		} else {
			holder.textAlarmPrompt.setTextColor(context.getResources()
					.getColor(R.color.black));
			holder.textAlarmPrompt.setText(schedule.getAlarmTime());
		}
		holder.cancel.setOnClickListener(new CancelListener(schedule));
		holder.buttonstartSetDialog.setOnClickListener(new DatePickListener(
				position));
		Log.i("bug", "schudle complete");
		return convertView;
	}

	private class DatePickListener implements OnClickListener {
		public DatePickListener(int pos) {
			this.pos = pos;
		}

		int pos;

		@Override
		public void onClick(View v) {
			_pos = pos;
			openDatePickerDialog(listItem.get(_pos).getAlarmTime());
		}

	}

	private void openDatePickerDialog(String time) {
		Date alarmDate = StringUtils.toAlarmDate(time);
		calAlarm = Calendar.getInstance();
		calAlarm.setTime(alarmDate);
		datePickerDialog = new DatePickerDialog(context, onDateSetListener,
				calAlarm.get(Calendar.YEAR), calAlarm.get(Calendar.MONTH),
				calAlarm.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.setTitle("设置日期");
		datePickerDialog.show();

	}

	private void openTimePickerDialog(boolean is24r) {

		timePickerDialog = new TimePickerDialog(context, onTimeSetListener,
				calAlarm.get(Calendar.HOUR_OF_DAY),
				calAlarm.get(Calendar.MINUTE), is24r);
		timePickerDialog.setTitle("设置时间");
		timePickerDialog.show();

	}

	OnDateSetListener onDateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar calNow = Calendar.getInstance();
			calSet = (Calendar) calNow.clone();

			calSet.set(Calendar.YEAR, year);
			calSet.set(Calendar.MONTH, monthOfYear);
			calSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			openTimePickerDialog(false);
		}
	};

	OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			Calendar calNow = Calendar.getInstance();

			calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calSet.set(Calendar.MINUTE, minute);
			calSet.set(Calendar.SECOND, 0);
			calSet.set(Calendar.MILLISECOND, 0);

			if (calSet.compareTo(calNow) <= 0) {
				// Today Set time passed, count to tomorrow
				calSet.add(Calendar.DATE, 1);
			}
			setAlarm(calSet);
		}
	};

	private void setAlarm(Calendar targetCal) {
		newAlarmTime = StringUtils.calendarToString(targetCal);
		listItem.get(_pos).setAlarmTime(newAlarmTime);
		appContext.updateAlarm(listItem.get(_pos));
		appContext.setAlarm(appContext, listItem.get(_pos));
		UIHelper.ToastMessage(context, "设定成功!");
		notifyDataSetChanged();

	}

	private class DetailListener implements OnClickListener {
		public DetailListener(int careerTalkID) {
			this.careerTalkID = careerTalkID;
		}

		private int careerTalkID;

		@Override
		public void onClick(View v) {
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
							appContext.cancelAlarm(context, schedule);
							/*
							 * appContext.joinCareerTalk(
							 * careerTalk.getCareerTalkID(), false);
							 */
							for (Schedules s : ScheduleActivity.scheduleList) {
								if (s.getScheduleID() == schedule
										.getScheduleID()) {
									ScheduleActivity.scheduleList.remove(s);
									break;
								}
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
