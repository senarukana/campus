package com.campusrecruit.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.krislq.sliding.R;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "interview.db";  
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private SQLiteDatabase db;

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.context = context;
	}
	
	public DBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	public void initDatabase() throws Exception{
		InputStream myInput = context.getResources().openRawResource(
                R.raw.career);
        InputStreamReader reader = new InputStreamReader(myInput);
        BufferedReader breader = new BufferedReader(reader);
        
        String str;
        
		while ((str = breader.readLine()) != null) {
			Log.i("sql", str);
		    db.execSQL(str); //exec your SQL line by line.
		}
		myInput.close();
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		this.db = db;
		try {
			this.initDatabase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
