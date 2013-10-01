package com.campusrecruit.db;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.bean.Schedule;
import com.campusrecruit.common.DateDataFormat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager implements TruncatableManager {

	private static final String SCHEDULE_TABLE_NAME = "schedule";

	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	@Override
	public void truncate() {
		String sql = "DELETE FROM " + SCHEDULE_TABLE_NAME;
		db.execSQL(sql);
	}

	public List<Schedule> schedule_get_all() {
		List<Schedule> list = new ArrayList<Schedule>();
		Cursor c = queryTheCursor(SCHEDULE_TABLE_NAME);
		try {
			while (c.moveToNext()) {
				Schedule schedule = new Schedule();
				schedule.setScheduleID(c.getInt(c
						.getColumnIndex("schedule_id")));
				schedule.setCompanyName(c.getString(c
						.getColumnIndex("company_name")));
				schedule.setPlace(c.getString(c.getColumnIndex("place")));
				schedule.setDate(DateDataFormat.getCalendarFromFormattedLong(c
						.getLong(c.getColumnIndex("date"))));
				list.add(schedule);
			}
		} finally {
			c.close();
		}
		return list;
	}

	public void schedule_add_one(Schedule s) {
		int id = s.getScheduleID();
		String name = s.getCompanyName();
		String place = s.getPlace();
		Long date = DateDataFormat.formatDateAsLong(s.getDate());
		db.execSQL(
				"INSERT INTO " + SCHEDULE_TABLE_NAME + " VALUES(?, ?, ?, ?)",
				new Object[] { id, name, place, date });
	}

	public void schedule_add_all(List<Schedule> l) {
		try {
			db.beginTransaction();
			for (Schedule s : l) {
				int id = s.getScheduleID();
				String name = s.getCompanyName();
				String place = s.getPlace();
				Long date = DateDataFormat.formatDateAsLong(s.getDate());
				db.execSQL("INSERT INTO " + SCHEDULE_TABLE_NAME
						+ " VALUES(?, ?, ?, ?)", new Object[] { id, name,
						place, date });
			}
		} finally {
			db.endTransaction();
		}
	}

	public boolean exists(String id) {
		String[] args = { id };
		Cursor c = db.query(SCHEDULE_TABLE_NAME, null, "schedule_id=?", args,
				null, null, null);
		int count = c.getCount();
		c.close();
		return count == 0 ? false : true;
	}

	public void schedule_delete_by_id(String id) {
		// db.execSQL("DELETE FROM " + SCHEDULE_TABLE_NAME +
		// "WHERE schedule_id="+id);
		if (exists(id)) {
			String[] args = { id };
			db.delete(SCHEDULE_TABLE_NAME, "schedule_id=?", args);
		}
	}

	public Cursor queryTheCursor(String table_name) {
		Cursor c = db.rawQuery("select * from " + table_name, null);
		return c;
	}

	public void close() {
		if (db != null) {
			db.close();
		}
	}

}
