package com.campusrecruit.bean;

import java.io.Serializable;
import java.util.Calendar;

import android.R.bool;
import android.R.integer;

public class Schedules implements Serializable {

	private int num; // for adapter
	private boolean isInit;
	
	private int scheduleID;
	private String companyName;
	private String place;
	private String date;
	private String time;

	public Schedules() {

	}

	public Schedules(int id, String name, String place, String date, String time) {
		this.scheduleID = id;
		this.companyName = name;
		this.place = place;
		this.date = date;
		this.time = time;
	}

	public int getScheduleID() {
		return scheduleID;
	}

	public void setScheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
