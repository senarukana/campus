package com.campusrecruit.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Schedules;
import com.campusrecruit.common.ScheduleAlarmBroadcastReceiver;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.pcncad.campusRecruit.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ListViewCareerTalkAdapter extends BaseAdapter {

	private Context context;
	private AppContext appContext;
	private List<CareerTalk> listItem;
	private LayoutInflater listContainer;
	private int itemViewResource;

	static class ListItemView {
		LinearLayout mainLayout;
		LinearLayout dateLayout;
		TextView flagDate;
		LinearLayout clickLayout;
		ImageView newFlag;
		ImageView famousFlag;
		TextView companyName;
		TextView companyType;
		TextView schoolName;
		TextView place;
		TextView time;
		TextView date;
		TextView sourceFrom;

		LinearLayout detailLayout;
		LinearLayout replysLayout;
		TextView replies;
		TextView clicks;
		LinearLayout joinsLayout;
		TextView joins;
		ToggleButton joinsImageButton;

	}

	public void setData(List<CareerTalk> data) {
		this.listItem = data;
	}

	public ListViewCareerTalkAdapter(Context context, AppContext appContext,
			int resource) {
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
		ListItemView holder = null;
		if (convertView == null) {
			holder = new ListItemView();

			convertView = this.listContainer.inflate(this.itemViewResource,
					null);
			holder.mainLayout = (LinearLayout) convertView
					.findViewById(R.id.careertalk_item_main);
			holder.dateLayout = (LinearLayout) convertView
					.findViewById(R.id.career_date_layout);
			holder.flagDate = (TextView) convertView
					.findViewById(R.id.career_talk_date);
			holder.clickLayout = (LinearLayout) convertView
					.findViewById(R.id.career_click_layout);
			holder.companyName = (TextView) convertView
					.findViewById(R.id.career_company_name);
			holder.companyType = (TextView) convertView
					.findViewById(R.id.career_company_type);
			holder.schoolName = (TextView) convertView
					.findViewById(R.id.career_school);
			holder.place = (TextView) convertView
					.findViewById(R.id.career_place);
			holder.time = (TextView) convertView.findViewById(R.id.career_time);
			holder.date = (TextView) convertView
					.findViewById(R.id.career_simple_date);
			holder.newFlag = (ImageView) convertView
					.findViewById(R.id.career_new_flag);
			holder.sourceFrom = (TextView) convertView
					.findViewById(R.id.career_source_from);
			holder.famousFlag = (ImageView) convertView.findViewById(R.id.career_famous_flag);

			// footer
			holder.detailLayout = (LinearLayout) convertView
					.findViewById(R.id.item_footer_clicks_layout);
			holder.replies = (TextView) convertView
					.findViewById(R.id.item_footer_comments);
			holder.replysLayout = (LinearLayout) convertView
					.findViewById(R.id.item_footer_comments_layout);
			holder.clicks = (TextView) convertView
					.findViewById(R.id.item_footer_clicks);
			holder.joinsImageButton = (ToggleButton) convertView
					.findViewById(R.id.item_footer_joins_image);
			holder.joins = (TextView) convertView
					.findViewById(R.id.item_footer_joins);
			holder.joinsLayout = (LinearLayout) convertView
					.findViewById(R.id.item_footer_joins_layout);

			convertView.setTag(holder);
		} else {
			holder = (ListItemView) convertView.getTag();
		}
		CareerTalk careerTalk = this.listItem.get(position);
		String friendlyTime = null;
		try {
			friendlyTime = StringUtils.friendly_happen_time(careerTalk
					.getDate());
			holder.date.setText(StringUtils
					.friendly_happen_simple_time(careerTalk.getDate()));
		} catch (Exception e) {
			friendlyTime = careerTalk.getDate();
			holder.date.setText(careerTalk.getDate());
		}

		String trimmedDate = StringUtils.trimed_time(careerTalk.getDate());
		String trimmedPreviousDate = null;
		if (position != 0) {
			trimmedPreviousDate = StringUtils.trimed_time(listItem.get(position - 1).getDate());
		}
		// 0 for init, 1 for header, 2 for others
		if (position == 0 || !trimmedDate.equals(trimmedPreviousDate)) {
			holder.dateLayout.setVisibility(View.VISIBLE);
			holder.flagDate.setText(friendlyTime);
		} else {
			holder.dateLayout.setVisibility(View.GONE);
		}
		
		if(StringUtils.isToday(careerTalk.getDate())) {
			holder.newFlag.setVisibility(View.VISIBLE);
		}
		else
			holder.newFlag.setVisibility(View.INVISIBLE);
		
		if (careerTalk.getFamous() == 1)
			holder.famousFlag.setVisibility(View.VISIBLE);
		else
			holder.famousFlag.setVisibility(View.INVISIBLE);

		holder.clicks.setText(careerTalk.getClicks() + "");
		holder.replies.setText(careerTalk.getReplies() + "");
		holder.joins.setText(careerTalk.getJoins() + "");

		holder.companyName.setText(careerTalk.getCompanyName());
		holder.companyName.setTag(careerTalk.getCareerTalkID());
		holder.companyType.setText(careerTalk.getCompanyType());
		holder.schoolName.setText(careerTalk.getSchoolName());
		holder.place.setText(careerTalk.getPlace());
		holder.sourceFrom.setText(careerTalk.getSourceFrom());

		holder.time.setText(careerTalk.getTime());

		if (careerTalk.getIsJoined() == 1)
			holder.joinsImageButton.setChecked(true);
		else
			holder.joinsImageButton.setChecked(false);
		holder.detailLayout.setOnClickListener(new DetailListener(careerTalk));
		holder.clickLayout.setOnClickListener(new DetailListener(careerTalk));
		holder.joinsLayout.setOnClickListener(new JoinsListener(careerTalk,
				holder.joinsImageButton, holder.joins));
		holder.joinsImageButton.setOnClickListener(new JoinsListener(
				careerTalk, holder.joinsImageButton, holder.joins));
		holder.replysLayout.setOnClickListener(new RepliesListener(careerTalk));
		Log.i("info", "career complete");
		return convertView;
	}

	private class DetailListener implements OnClickListener {
		private CareerTalk careerTalk;

		public DetailListener(CareerTalk careerTalk) {
			this.careerTalk = careerTalk;
		}

		@Override
		public void onClick(View v) {
			// 跳转到宣讲会详情
			UIHelper.showCareerTalkDetail(context, careerTalk, true);
		}

	}

	private class JoinsListener implements OnClickListener {
		public JoinsListener(CareerTalk careerTalk, ToggleButton vJoinsImage,
				TextView vJoinsText) {
			this.careerTalk = careerTalk;
			this.vJoinsImage = vJoinsImage;
			this.vJoinsText = vJoinsText;
			this.vSchedule = new Schedules(careerTalk.getCareerTalkID(),
					careerTalk.getCompanyName(), careerTalk.getSchoolName() + " "+ careerTalk.getPlace(),
					careerTalk.getDate(), careerTalk.getTime());
		}

		private CareerTalk careerTalk;
		private ToggleButton vJoinsImage;
		private TextView vJoinsText;
		private Schedules vSchedule;

		@Override
		public void onClick(View v) {
			if (!appContext.isInit()) {
				UIHelper.showLoginDialog((Activity) context);
				vJoinsImage.setChecked(false);
				return;
			}
			if (!appContext.getTutorialCareerFavorate()) {
				UIHelper.showCareerFavorateTutorial((Activity)context);
				appContext.setTutorialCareerFavorate();
			}
			int joins = 0;
			try {
				joins = Integer.parseInt(vJoinsText.getText().toString());
			} catch (NumberFormatException e) {
				Log.i("bug", "tansfrom error");
				vJoinsText.setText("0");
			}
			if (careerTalk.getIsJoined() == 1) {
				careerTalk.setIsJoined(0);
				vJoinsImage.setChecked(false);
				UIHelper.ToastMessage(context, "取消收藏");
				joins--;
				appContext.caeerTalkJoin(careerTalk, false);
			} else {
				vJoinsImage.setChecked(true);
				careerTalk.setIsJoined(1);
				joins++;
				UIHelper.ToastMessage(context, "收藏成功，已经加入到日程表中");
				appContext.caeerTalkJoin(careerTalk, true);
			}
			vJoinsText.setText(joins + "");
			new Thread() {
				public void run() {
					try {
						if (careerTalk.getIsJoined() == 1) {
							appContext.startAlarm(context, vSchedule);
							appContext.joinCareerTalk(
									careerTalk.getCareerTalkID(), true);
						} else {
							appContext.joinCareerTalk(
									careerTalk.getCareerTalkID(), false);
							appContext.cancelAlarm(context, vSchedule);
						}
					} catch (AppException e) {
						
					}
				}
			}.start();
			careerTalk.setJoins(joins);
			notifyDataSetChanged();
		}

	}

	private class RepliesListener implements OnClickListener {
		public RepliesListener(CareerTalk careerTalk) {
			this.careerTalk = careerTalk;
		}

		private CareerTalk careerTalk;

		@Override
		public void onClick(View v) {
			// 跳转到招聘详情
			UIHelper.showCareerTalkDetail(context, careerTalk, false);
		}

	}


}
