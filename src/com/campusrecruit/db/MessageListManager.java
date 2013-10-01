package com.campusrecruit.db;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.bean.UserMessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MessageListManager implements TruncatableManager{

	private static final String MESSAGELIST_TABLE_NAME = "message";

	private DBHelper helper;
	private SQLiteDatabase db;
	
	public MessageListManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}
	
	@Override
	public void truncate() {
		String sql = "DELETE FROM " + MESSAGELIST_TABLE_NAME;
		db.execSQL(sql);
	}

	public ArrayList<UserMessage> getUserMessages(){
		ArrayList<UserMessage> list = new ArrayList<UserMessage>();
		Cursor c = queryTheCursor();
		try{
			while(c.moveToNext()){
				UserMessage message = new UserMessage();
				message.setUserID(c.getString(c.getColumnIndex("user_id")));
				message.setUserName(c.getString(c.getColumnIndex("user_name")));
				message.setContent(c.getString(c.getColumnIndex("last_message")));
				message.setCreatedTime(c.getString(c.getColumnIndex("last_time")));
				list.add(message);
			}
		}finally{
			c.close();
		}
		return list;
	}
	
	public void saveList(List<UserMessage> messages) {
		for (UserMessage message : messages)
			messageListAddOne(message);
	}
	
	public void messageListAddOne(UserMessage message){
		ContentValues cv = new ContentValues();
		cv.put("user_id", message.getUserID());
		cv.put("user_name", message.getUserName());
		cv.put("last_message", message.getContent());
		cv.put("last_time", message.getCreatedTime());
		try {
			db.insertOrThrow(MESSAGELIST_TABLE_NAME, null, cv);
		} catch (SQLiteConstraintException e) {
			Log.i("db", "oops the data is alread exist");
			cv.remove("user_id");
			db.update(MESSAGELIST_TABLE_NAME, cv, "user_id=?",
					new String[] { message.getUserID() });
		}
	}
	
	public Cursor queryTheCursor() {
		Cursor c = db.rawQuery(String.format("SELECT * FROM %s ORDER BY last_time DESC",
				MESSAGELIST_TABLE_NAME), null);
		return c;
	}
	
	public void close() {
		if (db != null) {
			db.close();
		}
	}
}
