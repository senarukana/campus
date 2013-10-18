package com.campusrecruit.fragment;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.campusrecruit.activity.DailyActivity;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.Schedule;
import com.campusrecruit.bean.Schedules;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.DateWidgetDayCell;
import com.campusrecruit.widget.DateWidgetDayHeader;
import com.campusrecruit.widget.DayStyle;
import com.campusrecruit.widget.Lunar;
import com.pcncad.campusRecruit.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

@SuppressLint("ValidFragment")
public class ScheduleFragment extends BaseFragment {
	private ArrayList<DateWidgetDayCell> days = new ArrayList<DateWidgetDayCell>();
	private ArrayList<DateWidgetDayCell> daysNext = new ArrayList<DateWidgetDayCell>();
	private Calendar calStartDate = Calendar.getInstance();
	private Calendar calToday = Calendar.getInstance();
	private Calendar calCalendar = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();
	private Calendar calSchedule = Calendar.getInstance();
	private List<Schedules> calScheduleList = null;
	
	private AppContext mAppContext = null;

	public static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	LinearLayout layContent = null;
	LinearLayout layContentNext = null;
	boolean isOneFrame = true;
	Button btnToday = null;

	private int iFirstDayOfWeek = Calendar.SUNDAY;
	private int iMonthViewCurrentMonth = 0;
	private int iMonthViewCurrentYear = 0;
	public static final int SELECT_DATE_REQUEST = 111;
	private static int iDayCellSize = 38;
	private static final int iDayHeaderHeight = 19;
	private static final int iTotalWidth = (iDayCellSize * 7);
	private TextView tv, yearTextView;
	private int mYear;
	private int mMonth;
	private int mDay;
	
	private List<Schedules> scheduleList;

	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private Animation slideTopIn;
	private Animation slideTopOut;
	private Animation slideBottomIn;
	private Animation slideBottomOut;
	private ViewFlipper viewFlipper;

	private String text;

	public ScheduleFragment() {
	}

	public ScheduleFragment(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Log.i("schedule", "onCreate begins");
		
		mAppContext = (AppContext)getActivity().getApplicationContext();

		iFirstDayOfWeek = Calendar.SUNDAY;
		mYear = calSelected.get(Calendar.YEAR);
		mMonth = calSelected.get(Calendar.MONTH);
		mDay = calSelected.get(Calendar.DAY_OF_MONTH);

		// initial the calSchedule
		calScheduleList = getSchedulesList();

		setDayCellSize();

		Log.i("schedule", "onCreate ends");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getActivity().getActionBar().setTitle("日程安排");
		isOneFrame = true;
		View view = generateContentView();
		initSlide();		
		calStartDate = getCalendarStartDate();
		DateWidgetDayCell daySelected = slideCalendar();
		
		// updateControlsState();
		if (daySelected != null) {
			daySelected.requestFocus();
		}
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private ViewFlipper createViewFlipper() {
		ViewFlipper vf = new ViewFlipper(this.getActivity());
		vf.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		return vf;
	}

	private LinearLayout createLayout(int iOrientation) {
		LinearLayout lay = new LinearLayout(this.getActivity());
		lay.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lay.setOrientation(iOrientation);
		return lay;
	}

	private void initSlide() {
		slideLeftIn = AnimationUtils.loadAnimation(this.getActivity(),
				R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(this.getActivity(),
				R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(this.getActivity(),
				R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this.getActivity(),
				R.anim.slide_right_out);
		slideTopIn = AnimationUtils.loadAnimation(this.getActivity(),
				R.anim.slide_top_in);
		slideTopOut = AnimationUtils.loadAnimation(this.getActivity(),
				R.anim.slide_top_out);
		slideBottomIn = AnimationUtils.loadAnimation(this.getActivity(),
				R.anim.slide_bottom_in);
		slideBottomOut = AnimationUtils.loadAnimation(this.getActivity(),
				R.anim.slide_bottom_out);
	}

	private void slideRight() {
		setPrevMonthViewItem();
		viewFlipper.setInAnimation(slideRightIn);
		viewFlipper.setOutAnimation(slideRightOut);
		viewFlipper.showPrevious();
	}

	private void slideLeft() {
		setNextMonthViewItem();
		viewFlipper.setInAnimation(slideLeftIn);
		viewFlipper.setOutAnimation(slideLeftOut);
		viewFlipper.showNext();
	}

	private void generateTopButtons(LinearLayout layTopControls) {

		final int iSmallButtonWidth = 70;
		final int iSamllHeight = 35;

		yearTextView = new TextView(this.getActivity());
		yearTextView.setText(mYear + "年" + format(mMonth + 1) + "月");
		yearTextView.setTextColor(Color.BLACK);
		yearTextView.setWidth(150);
		yearTextView.setHeight(iSamllHeight);

		Button btnPrevMonth = new Button(this.getActivity());
		btnPrevMonth.setLayoutParams(new LayoutParams(iSmallButtonWidth / 2,
				iSamllHeight * 2));
		btnPrevMonth.setBackgroundResource(R.drawable.prev_month);

		Button btnNextMonth = new Button(this.getActivity());
		btnNextMonth.setLayoutParams(new LayoutParams(iSmallButtonWidth / 2,
				iSamllHeight * 2));
		btnNextMonth.setBackgroundResource(R.drawable.next_month);

		// set events
		btnPrevMonth.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				slideRight();
			}
		});

		btnNextMonth.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				slideLeft();
			}
		});

		layTopControls.setGravity(Gravity.CENTER_HORIZONTAL);
		layTopControls.addView(btnPrevMonth);
		layTopControls.addView(yearTextView);
		layTopControls.addView(btnNextMonth);

	}

	private View generateContentView() {
		Log.i("schedule content create", "content view start");

		LinearLayout mainLayout = createLayout(LinearLayout.VERTICAL);
		mainLayout.setBackgroundColor(this.getResources().getColor(
				R.color.solid_backgroudcolor));
		LinearLayout firstLayout = createLayout(LinearLayout.VERTICAL);
		firstLayout.setPadding(0, 0, 0, 0);

		layContent = createLayout(LinearLayout.VERTICAL);
		layContent.setPadding(0, 0, 0, 0);
		generateCalendar(layContent, days);
		Log.i("daysInfo days", days.toString());
		firstLayout.addView(layContent);

		viewFlipper = createViewFlipper();
		viewFlipper.setBackgroundColor(this.getResources().getColor(
				R.color.solid_backgroudcolor));

		LinearLayout secondLayout = createLayout(LinearLayout.VERTICAL);
		secondLayout.setPadding(0, 0, 0, 0);

		LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL);
		layTopControls.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.topbar_bg));
		generateTopButtons(layTopControls);

		layContentNext = createLayout(LinearLayout.VERTICAL);
		layContentNext.setPadding(0, 0, 0, 0);
		generateCalendar(layContentNext, daysNext);
		Log.i("daysInfo daysNext", daysNext.toString());
		secondLayout.addView(layContentNext);

		mainLayout.addView(layTopControls);
		viewFlipper.addView(firstLayout);
		viewFlipper.addView(secondLayout);
		mainLayout.addView(viewFlipper);

		tv = new TextView(this.getActivity());
		tv.setPadding(0, 0, 0, 0);
		mainLayout.addView(tv);

		return mainLayout;
	}

	private View generateCalendarRow(ArrayList<DateWidgetDayCell> dayList) {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
		for (int iDay = 0; iDay < 7; iDay++) {
			DateWidgetDayCell dayCell = new DateWidgetDayCell(
					this.getActivity(), iDayCellSize,
					(int) Math.floor(iDayCellSize * 1.2));
			dayCell.setItemClick(mOnDayCellClick);
			dayList.add(dayCell);
			layRow.addView(dayCell);
		}
		return layRow;
	}

	private View generateCalendarHeader() {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
		for (int iDay = 0; iDay < 7; iDay++) {
			DateWidgetDayHeader day = new DateWidgetDayHeader(
					this.getActivity(), iDayCellSize, iDayHeaderHeight);
			final int iWeekDay = DayStyle.getWeekDay(iDay, iFirstDayOfWeek);
			day.setData(iWeekDay);
			layRow.addView(day);
		}
		return layRow;
	}

	private void generateCalendar(LinearLayout layContent,
			ArrayList<DateWidgetDayCell> dayList) {
		Log.i("schedule generate calendar", "start");
		layContent.addView(generateCalendarHeader());

		Log.i("schedule generate calendar header", "ends");

		dayList.clear();
		for (int iRow = 0; iRow < 6; iRow++) {
			layContent.addView(generateCalendarRow(dayList));
		}
		Log.i("schedule generate calendar row", "end");
	}

	// query the sqlite and return the schedule list
	private List<Schedules> getSchedulesList(){
//		List<Schedules> list = new ArrayList<Schedules>();
//		Schedules s1 = new Schedules(1, "Baidu", "BUPT", "2013-09-11", "12:30");
//		Schedules s2 = new Schedules(2, "Sina", "BUPT", "2013-09-11", "13:50");
//
//		list.add(s1);
//		list.add(s2);
		if (scheduleList == null) {
			scheduleList = mAppContext.scheduleGetAll();
		}
		return scheduleList;
//		return list;
	}
	
	/*private List<Schedule> getScheduleList() {
		List<Schedule> list = new ArrayList<Schedule>();
		Calendar c1 = Calendar.getInstance();
		c1.set(2013, 8, 11, 13, 30);
		Schedule s1 = new Schedule(1, "Baidu", "BUPT", c1);

		Calendar c2 = Calendar.getInstance();
		c2.set(2013, 8, 11, 15, 30);
		Schedule s2 = new Schedule(2, "Sina", "BUPT", c2);

		list.add(s1);
		list.add(s2);
		return list;
	}
*/
	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		Log.i("schedule calSelected", calSelected.getTimeInMillis() + "");
		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}
		updateStartDateForMonth();

		return calStartDate;
	}

	private DateWidgetDayCell slideCalendar() {
		ArrayList<DateWidgetDayCell> daylist = null;
		Log.i("isOneFrame before in slideCalendar", isOneFrame ? "true"
				: "false");
		if (isOneFrame) {
			daylist = days;
			isOneFrame = false;
		} else {
			daylist = daysNext;
			isOneFrame = true;
		}
		Log.i("isOneFrame after in slideCalendar", isOneFrame ? "true"
				: "false");
		return updateCalendar(daylist);
	}

	private DateWidgetDayCell updateCalendar(ArrayList<DateWidgetDayCell> day) {
		DateWidgetDayCell daySelected = null;
		boolean bSelected = false;
		final boolean bIsSelection = (calSelected.getTimeInMillis() != 0);
		final int iSelectedYear = calSelected.get(Calendar.YEAR);
		final int iSelectedMonth = calSelected.get(Calendar.MONTH);
		final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
		calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());
		for (int i = 0; i < day.size(); i++) {
			final int iYear = calCalendar.get(Calendar.YEAR);
			final int iMonth = calCalendar.get(Calendar.MONTH);
			final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
			final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);

			Lunar lunar = new Lunar(calCalendar);
			final String iLunarDay = lunar.getChinaDays();
			DateWidgetDayCell dayCell = day.get(i);
			// check today
			boolean bToday = false;
			if (calToday.get(Calendar.YEAR) == iYear)
				if (calToday.get(Calendar.MONTH) == iMonth)
					if (calToday.get(Calendar.DAY_OF_MONTH) == iDay)
						bToday = true;
			// check schedule
			boolean bScheduled = false;

			for (Schedules s : getSchedulesList()) {
				String date = s.getDate();
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					c.setTime(sdFormat.parse(date));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
				}
				if (c.get(Calendar.YEAR) == iYear
						&& c.get(Calendar.MONTH) == iMonth
						&& c.get(Calendar.DAY_OF_MONTH) == iDay) {
					bScheduled = true;
					break;
				}
			}
			// check holiday
			boolean bHoliday = false;
			if ((iDayOfWeek == Calendar.SATURDAY)
					|| (iDayOfWeek == Calendar.SUNDAY))
				bHoliday = true;
			if ((iMonth == Calendar.JANUARY) && (iDay == 1))
				bHoliday = true;

			dayCell.setData(iYear, iMonth, iDay, bToday, bScheduled,
					bHoliday, iMonthViewCurrentMonth, iDayOfWeek);
			bSelected = false;
			if (bIsSelection)
				if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth)
						&& (iSelectedYear == iYear)) {
					bSelected = true;
				}
			dayCell.setSelected(bSelected);
			if (bSelected)
				daySelected = dayCell;
			calCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		// layContent.invalidate();
		// layContentNext.invalidate();
		return daySelected;
	}

	private void updateStartDateForMonth() {
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		UpdateCurrentMonthDisplay();
		// update days for week
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
	}

	private void UpdateCurrentMonthDisplay() {

		mYear = calCalendar.get(Calendar.YEAR);
	}

	private void setPrevMonthViewItem() {
		iMonthViewCurrentMonth--;
		if (iMonthViewCurrentMonth == -1) {
			iMonthViewCurrentMonth = 11;
			iMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		updateDate();
		Log.i("fdfd", iMonthViewCurrentMonth + "");
		updateCenterTextView(iMonthViewCurrentMonth, iMonthViewCurrentYear);
	}

	private void setNextMonthViewItem() {
		iMonthViewCurrentMonth++;
		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		updateDate();
		updateCenterTextView(iMonthViewCurrentMonth, iMonthViewCurrentYear);
	}


	private DateWidgetDayCell.OnItemClick mOnDayCellClick = new DateWidgetDayCell.OnItemClick() {
		public void OnClick(DateWidgetDayCell item) {
			calSelected.setTimeInMillis(item.getDate().getTimeInMillis());
			item.setSelected(true);
			Log.i("isOneFrame before onClick", isOneFrame ? "true" : "false");
			isOneFrame = isOneFrame ? false : true;
			Log.i("isOneFrame after onClick", isOneFrame ? "true" : "false");
			slideCalendar();
			// this is the schedule list that contains the schedule on the click
			// day
			ArrayList<Schedules> schedule_intent_list = new ArrayList<Schedules>();
			for (Schedules s : getSchedulesList()) {
				String date = s.getDate();
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					c.setTime(sdFormat.parse(date));
				} catch (ParseException e) {
					
				}
				if (c.get(Calendar.YEAR) == calSelected.get(Calendar.YEAR)
						&& c.get(Calendar.MONTH) == calSelected
								.get(Calendar.MONTH)
						&& c.get(Calendar.DAY_OF_MONTH) == calSelected
								.get(Calendar.DAY_OF_MONTH)) {
					schedule_intent_list.add(s);
				}
			}
			Intent i = new Intent(getActivity(), DailyActivity.class);
			i.putExtra("schedule_list", schedule_intent_list);
			startActivityForResult(i,UIHelper.REQUEST_CODE_FOR_SCHEDULE);
		}

		@Override
		public void OnTouchMove(DateWidgetDayCell item, int type) {
			switch (type) {
			case 1:
				// slideLeft();
				break;
			case 2:
				// slideRight();
				break;
			default:
				break;
			}
		}
	};
	

	private void updateCenterTextView(int iMonthViewCurrentMonth,
			int iMonthViewCurrentYear) {
		yearTextView.setText(iMonthViewCurrentYear + "年"
				+ format(iMonthViewCurrentMonth + 1) + "月");
	}

	private void updateDate() {
		updateStartDateForMonth();
		slideCalendar();
	}


	private String format(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}

	private void setDayCellSize() {
		DisplayMetrics dm = new DisplayMetrics();
		this.getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		iDayCellSize = dm.widthPixels / 7 + 1;
		Log.i("schedule iDayCellSize", dm.widthPixels + ", " + iDayCellSize
				+ "");
	}
}
