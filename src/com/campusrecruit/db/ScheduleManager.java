package com.campusrecruit.db;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.bean.Schedule;
import com.campusrecruit.bean.Schedules;
import com.campusrecruit.common.DateDataFormat;
import com.campusrecruit.common.StringUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ScheduleManager implements TruncatableManager {

	private static final String SCHEDULE_TABLE_NAME = "schedule";

	private DBHelper helper;
	private SQLiteDatabase db;

	public ScheduleManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	private void deleteObsoleteData() {
		String curDate = StringUtils.getYesterdayTimeStamp();
		String whereClause = String.format("delete from %s where date <= '%s'",
				SCHEDULE_TABLE_NAME, curDate);
		db.execSQL(whereClause);
	}

	@Override
	public void truncate() {
		String sql = "DELETE FROM " + SCHEDULE_TABLE_NAME;
		db.execSQL(sql);
	}

	public ArrayList<Schedules> scheduleGetAll() {
		ArrayList<Schedules> list = new ArrayList<Schedules>();
		deleteObsoleteData();
		Cursor c = queryTheCursor();
		try {
			while (c.moveToNext()) {
				Schedules schedule = new Schedules();
				schedule.setScheduleID(c.getInt(c.getColumnIndex("schedule_id")));
				schedule.setCompanyName(c.getString(c
						.getColumnIndex("company_name")));
				schedule.setPlace(c.getString(c.getColumnIndex("place")));
				schedule.setDate(c.getString(c.getColumnIndex("date")));
				schedule.setTime(c.getString(c.getColumnIndex("time")));
				schedule.setAlarmTime(c.getString(c
						.getColumnIndex("alarm_time")));
				Log.i("alarm",schedule.getAlarmTime());
				list.add(schedule);
			}
		} finally {
			c.close();
		}
		return list;
	}

	public void scheduleAddOne(Schedules s) {
		Log.i("test", "schedule add one");
		int id = s.getScheduleID();
		String name = s.getCompanyName();
		String place = s.getPlace();
		String date = s.getDate();
		String time = s.getTime();
		ContentValues cv = new ContentValues();
		cv.put("schedule_id", id);
		cv.put("company_name", name);
		cv.put("place", place);
		cv.put("date", date);
		cv.put("time", time);
		cv.put("company_name", name);
		cv.put("alarm_time", s.getAlarmTime());
		try {
			db.insertOrThrow(SCHEDULE_TABLE_NAME, null, cv);
		} catch (SQLiteConstraintException e) {
			
			db.update(SCHEDULE_TABLE_NAME, cv, "schedule_id=?",
					new String[] { id + "" });
		}
		Log.i("test", "schedule add");
	}

	public void scheduleUpdate(Schedules s) {
		int id = s.getScheduleID();
		String name = s.getCompanyName();
		String place = s.getPlace();
		String date = s.getDate();
		String time = s.getTime();
		ContentValues cv = new ContentValues();
		cv.put("schedule_id", id);
		cv.put("company_name", name);
		cv.put("place", place);
		cv.put("date", date);
		cv.put("time", time);
		cv.put("company_name", name);
		cv.put("alarm_time", s.getAlarmTime());
		db.update(SCHEDULE_TABLE_NAME, cv, "schedule_id=?", new String[] { id
				+ "" });
		Log.i("test", "schedule update" + s.getAlarmTime());
	}

	public void scheduleAddAll(ArrayList<Schedules> l) {
		try {
			db.beginTransaction();
			for (Schedules s : l) {
				scheduleAddOne(s);
			}
		} finally {
			db.endTransaction();
		}
	}

	public boolean exists(int id) {
		String[] args = { Integer.toString(id) };
		Cursor c = db.query(SCHEDULE_TABLE_NAME, null, "schedule_id=?", args,
				null, null, null);
		int count = c.getCount();
		Log.i("test", count + "exists");
		c.close();
		return count == 0 ? false : true;
	}

	public void schedule_delete_by_id(int id) {
		// db.execSQL("DELETE FROM " + SCHEDULE_TABLE_NAME +
		// "WHERE schedule_id="+id);
		Log.i("test", "schedule delete");
		if (exists(id)) {
			Log.i("test", "schedule delete ok");
			String[] args = { Integer.toString(id) };
			db.delete(SCHEDULE_TABLE_NAME, "schedule_id=?", args);
		}
		Log.i("test", "schedule delete complete");
	}

	public Cursor queryTheCursor() {
		Cursor c = db.rawQuery(String.format("SELECT * FROM %s ORDER BY date",
				SCHEDULE_TABLE_NAME), null);
		return c;
	}

	public void close() {
		if (db != null) {
			db.close();
		}
	}

}
