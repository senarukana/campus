package com.campusrecruit.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.campusrecruit.app.AppConfig;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Company;

public class CopyOfCareerTalkManager implements TruncatableManager {

	private DBHelper helper;
	private SQLiteDatabase db;

	private static final String TABLE_NAME = "career_talk";

	public CopyOfCareerTalkManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	// true 所有信息 false 未收藏信息
	public int count(boolean flag) {
		String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
		if (!flag)
			sql += " WHERE isjoined = 0";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		Log.i("db", String.format("careertalk count size %d", count));
		cursor.close();
		return count;
	}

	public void truncate() {
		String sql = "DELETE FROM " + TABLE_NAME;
		db.execSQL(sql);
	}

	public boolean isEmpty(boolean flag) {
		return count(flag) == 0;
	}

	public void updateCompanyInfo(int careerTalkID, Company company) {
		ContentValues cv = new ContentValues();
		cv.put("company_place", company.getPlace());
		cv.put("company_introduction", company.getIntroduction());
		db.update(TABLE_NAME, cv, "career_talk_id=?",
				new String[] { careerTalkID + "" });
	}

	public void updateJoin(int careerTalkID, boolean isJoin) {
		ContentValues cv = new ContentValues();
		if (isJoin)
			cv.put("isjoined", 1);
		else
			cv.put("isjoined", 2);
		Log.i("db", "careerJoin");
		db.update(TABLE_NAME, cv, "career_talk_id=?",
				new String[] { careerTalkID + "" });
	}

	public void saveList(List<CareerTalk> list) {
		Log.i("db", "save ct list");
		try {
			db.beginTransaction();
			int c = count(false) + list.size();
			if (c > AppConfig.SQLITE_MAX_ROW) {
				Log.i("db", "save ct list");
				db.delete(TABLE_NAME, "isjoined!=? LIMIT "
						+ (c - AppConfig.SQLITE_MAX_ROW),
						new String[] { 0 + "" });
			}
			for (CareerTalk career : list) {
				addBase(career);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void addBase(CareerTalk careerTalk) {
		ContentValues cv = new ContentValues();
		cv.put("career_talk_id", careerTalk.getCareerTalkID());
		cv.put("topic_id", careerTalk.getTopicID());
		cv.put("company_id", careerTalk.getCompanyID());
		cv.put("company_name", careerTalk.getCompanyName());
		cv.put("school_name", careerTalk.getCompanyName());
		cv.put("place", careerTalk.getPlace());
		cv.put("date", careerTalk.getDate());
		cv.put("time", careerTalk.getTime());
		cv.put("joins", careerTalk.getJoins());
		cv.put("replies", careerTalk.getReplies());
		cv.put("isjoined", careerTalk.getIsJoined());
		try {
			db.insertOrThrow(TABLE_NAME, null, cv);
		} catch (SQLiteConstraintException e) {
			Log.i("db", "oops the data is alread exist");
			// if the data is already exist then update it
			db.update(TABLE_NAME, cv, "career_talk_id=?",
					new String[] { careerTalk.getCareerTalkID() + "" });
		}
	}

	public void updateDetail(CareerTalk careerTalk) {
		int id = careerTalk.getCareerTalkID();
		String introduction = careerTalk.getIntroduction();
		ContentValues cv = new ContentValues();
		cv.put("introduction", introduction);
		db.update(TABLE_NAME, cv, "career_talk_id=?", new String[] { id + "" });
	}

	public List<CareerTalk> getList(Cursor c) {
		Log.i("sqlite", "getCareerTalk List");
		List<CareerTalk> list = new ArrayList<CareerTalk>();
		try {
			while (c.moveToNext()) {
				CareerTalk careerTalk = new CareerTalk();
				careerTalk.setCareerTalkID(c.getInt(c
						.getColumnIndex("career_talk_id")));
				careerTalk.setTopicID(c.getInt(c.getColumnIndex("topic_id")));
				careerTalk
						.setCompanyID(c.getInt(c.getColumnIndex("company_id")));
				careerTalk.setSchoolName(c.getString(c
						.getColumnIndex("school_name")));
				careerTalk.setCompanyName(c.getString(c
						.getColumnIndex("company_name")));
				careerTalk.setDate(c.getString(c.getColumnIndex("date")));
				careerTalk.setTime(c.getString(c.getColumnIndex("time")));
				careerTalk.setPlace(c.getString(c.getColumnIndex("place")));
				careerTalk.setReplies(c.getInt(c.getColumnIndex("replies")));
				careerTalk.setJoins(c.getInt(c.getColumnIndex("joins")));
				careerTalk.setIntroduction(c.getString(c
						.getColumnIndex("introduction")));
				careerTalk.setIsJoined(c.getInt(c.getColumnIndex("isjoined")));
				Company company = new Company();
				company.setCompanyName(c.getString(c
						.getColumnIndex("company_name")));
				company.setType(c.getString(c.getColumnIndex("company_type")));
				company.setIndustry(c.getString(c
						.getColumnIndex("company_industry")));
				company.setPlace(c.getString(c.getColumnIndex("company_place")));
				company.setIntroduction(c.getString(c
						.getColumnIndex("company_introduction")));
				careerTalk.setCompany(company);
				list.add(careerTalk);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (c != null)
				c.close();
		}
		Log.i("sqlite",
				String.format("CareerTalk List length is %d", list.size()));
		return list;
	}

	public List<CareerTalk> getAllData() {
		return getList(queryAllData());
	}

	public List<CareerTalk> getFavorates(boolean flag) {
		return getList(queryFavorates(flag));
	}

	private Cursor queryFavorates(boolean flag) {
		String[] columns = new String[] { "career_talk_id", "topic_id",
				"company_id", "company_name", "company_type",
				"company_industry", "company_introduction", "company_place",
				"school_name", "date", "time", "place", "joins", "replies",
				"isjoined", "introduction" };
		Cursor c = db.query(TABLE_NAME, columns, "isjoined=?",
				new String[] { flag == true ? 1 + "" : 2 + "" }, null, null,
				"career_talk_id DESC");
		return c;
	}

	private Cursor queryAllData() {
		String[] columns = new String[] { "career_talk_id", "topic_id",
				"company_name", "company_id", "company_type",
				"company_industry", "company_introduction", "company_place",
				"school_name", "date", "time", "place", "joins", "replies",
				"isjoined", "introduction" };
		Cursor c = db.query(TABLE_NAME, columns, null, null, null, null,
				"career_talk_id DESC");
		Log.i("db", String.format("ct all data len is %d test", c.getCount()));
		return c;
	}

	@Override
	public String toString() {
		return "DBM   lee";
	}

	public void closeDB() {
		db.close();
	}

}