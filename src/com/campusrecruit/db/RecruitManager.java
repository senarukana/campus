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
import com.campusrecruit.bean.Company;
import com.campusrecruit.bean.Recruit;

public class RecruitManager implements TruncatableManager {

	private DBHelper helper;
	private SQLiteDatabase db;

	private static final String TABLE_NAME = "recruit";

	public RecruitManager(Context context) {
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
		cursor.close();
		return count;
	}

	public boolean isEmpty(boolean flag) {
		return count(flag) == 0;
	}

	public void updateCompanyInfo(int recruitID, Company company) {
		ContentValues cv = new ContentValues();
		cv.put("company_place", company.getPlace());
		cv.put("company_introduction", company.getIntroduction());
		db.update(TABLE_NAME, cv, "recruit_id=?",
				new String[] { recruitID + "" });
	}

	@Override
	public void truncate() {
		String sql = "DELETE FROM " + TABLE_NAME;
		db.execSQL(sql);
	}
	
	public void truncateObsolete() {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE isjoined != 1";
		db.execSQL(sql);
	}
	

	public void updateRecruitDescription(int recruitID, String description) {
		ContentValues cv = new ContentValues();
		cv.put("description", description);
		db.update(TABLE_NAME, cv, "recruit_id=?",
				new String[] { recruitID + "" });
	}

	public void updateRecruitContact(int recruitID, String contact) {
		ContentValues cv = new ContentValues();
		cv.put("contact", contact);
		db.update(TABLE_NAME, cv, "recruit_id=?",
				new String[] { recruitID + "" });
	}

	public void saveList(List<Recruit> list) {
		try {
			db.beginTransaction();
			int c = count(false) + list.size();
			if (c > AppConfig.SQLITE_MAX_ROW) {
				db.delete(TABLE_NAME, "isjoined!=?",
						new String[] { 1 + "" });
			}
			for (Recruit recruit : list) {
				addBase(recruit);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void addBase(Recruit r) {
		ContentValues cv = new ContentValues();
		cv.put("recruit_id", r.getRecruitID());
		cv.put("topic_id", r.getTopicID());
		cv.put("company_id", r.getCompany().getCompanyID());
		cv.put("company_name", r.getCompany().getCompanyName());
		cv.put("company_type", r.getCompany().getType());
		cv.put("company_industry", r.getCompany().getIndustry());
		cv.put("position", r.getPosition());
		cv.put("time", r.getCreatedTime());
		cv.put("place", r.getPlace());
		cv.put("joins", r.getJoins());
		cv.put("replies", r.getReplies());
		cv.put("isjoined", r.getIsJoined());
		cv.put("clicks", r.getClicks());
		try {
			db.insertOrThrow(TABLE_NAME, null, cv);
		} catch (SQLiteConstraintException e) {
			Log.i("db", "oops the data is alread exist");
			// if the data is already exist then update it
			db.update(TABLE_NAME, cv, "recruit_id=?",
					new String[] { r.getRecruitID() + "" });
		}
	}

	public void updateJoin(int recruitID, boolean isJoin) {
		ContentValues cv = new ContentValues();
		if (isJoin)
			cv.put("isjoined", 1);
		else
			cv.put("isjoined", 0);
		Log.i("db", "careerJoin aa");
		db.update(TABLE_NAME, cv, "recruit_id=?",
				new String[] { recruitID + "" });
		Log.i("db", getFavorates(true).size() + "");
	}

	public ArrayList<Recruit> getAllData() throws SQLiteException {
		return getList(queryAllData());
	}

	public ArrayList<Recruit> getFavorates(boolean flag) throws SQLiteException {
		Log.i("db", "getFavorates");
		return getList(queryFavorates(flag));
	}

	public ArrayList<Recruit> getList(Cursor c) throws SQLiteException {
		ArrayList<Recruit> list = new ArrayList<Recruit>();
		try {
			while (c.moveToNext()) {
				Recruit r = new Recruit();
				r.setRecruitID(c.getInt(c.getColumnIndex("recruit_id")));
				r.setTopicID(c.getInt(c.getColumnIndex("topic_id")));
				r.setPosition(c.getString(c.getColumnIndex("position")));
				r.setCreatedTime(c.getString(c.getColumnIndex("time")));
				r.setPlace(c.getString(c.getColumnIndex("place")));
				r.setReplies(c.getInt(c.getColumnIndex("replies")));
				r.setJoins(c.getInt(c.getColumnIndex("joins")));
				r.setClicks(c.getInt(c.getColumnIndex("clicks")));
				r.setContact(c.getString(c.getColumnIndex("contact")));
				r.setDescription(c.getString(c.getColumnIndex("description")));
				r.setIsJoined(c.getInt(c.getColumnIndex("isjoined")));

				Company company = new Company();
				company.setCompanyID(c.getInt(c.getColumnIndex("company_id")));
				company.setCompanyName(c.getString(c
						.getColumnIndex("company_name")));
				company.setType(c.getString(c.getColumnIndex("company_type")));
				company.setIndustry(c.getString(c
						.getColumnIndex("company_industry")));
				company.setPlace(c.getString(c.getColumnIndex("company_place")));
				company.setIntroduction(c.getString(c
						.getColumnIndex("company_introduction")));
				r.setCompany(company);
				list.add(r);
			}
		} finally {
			c.close();
		}
		Log.i("db", String.format("return data size is %d", list.size()));
		return list;
	}

	/*
	 * public boolean exists(int recruitID){ String[] args = {recruitID}; Cursor
	 * c = db.query(TABLE_NAME, null, "career_talk_id=?", args, null, null,
	 * null); return c.getCount() != 0; }
	 */
	
	private void deleteNotFavorateData() {
		db.delete(TABLE_NAME, "isjoined != ?", new String[]{1 + ""});
	}

	private Cursor queryFavorates(boolean flag) throws SQLiteException {
		String[] columns = new String[] { "recruit_id", "topic_id",
				"company_id", "company_name", "company_type",
				"company_industry", "company_introduction", "company_place",
				"position", "place", "contact", "description", "time", "joins","clicks",
				"replies", "isjoined" };
		Cursor c = db.query(TABLE_NAME, columns, "isJoined=?",
				new String[] { flag == true ? 1 + "" : 2 + "" }, null, null,
				"time DESC");
		return c;
	}

	private Cursor queryAllData() {
		String[] columns = new String[] { "recruit_id", "topic_id",
				"company_id", "company_name", "company_type",
				"company_industry", "company_introduction", "company_place",
				"position", "place", "contact", "description", "time", "joins","clicks",
				"replies", "isjoined" };
		Cursor c = db.query(TABLE_NAME, columns, null, null, null, null,
				"time DESC");
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