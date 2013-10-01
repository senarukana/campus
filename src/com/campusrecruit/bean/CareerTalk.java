package com.campusrecruit.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.campusrecruit.app.AppException;

public class CareerTalk implements Serializable, Comparable<CareerTalk>{
	private int careerTalkID;
	private int topicID;
	private int companyID;
	private String companyName;
	private String companyType;

	private String schoolName;
	private String introduction;
	private int famous;
	
	//13:00-15:00
	private String time;
	//2013-08-29 15:00:00
	private String date;
	
	private String createdTime;
	private String place;
	private int clicks;
	private int joins;
	private Company company;
	
//	private int status;


	// private int watches;
	// private double rating;
	private ArrayList<String> tagList;
	private int isJoined;
	private int replies;
	private String sourceFrom;
	public String url;
	
	
	private int flag;

	
	public String getCompanyType() {
		return companyType;
	}
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public int getFamous() {
		return famous;
	}
	public void setFamous(int famous) {
		this.famous = famous;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getSourceFrom() {
		return sourceFrom;
	}
	public void setSourceFrom(String sourceFrom) {
		this.sourceFrom = sourceFrom;
	}
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	/*public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}*/
	
	public int getTopicID() {
		return topicID;
	}

	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public int getIsJoined() {
		return isJoined;
	}

	public void setIsJoined(int isJoined) {
		this.isJoined = isJoined;
	}

	public int getReplies() {
		return replies;
	}

	public void setReplies(int replies) {
		this.replies = replies;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}


	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ArrayList<String> getTagList() {
		return tagList;
	}

	public void setTagList(ArrayList<String> tagList) {
		this.tagList = tagList;
	}

	public int getCareerTalkID() {
		return careerTalkID;
	}

	public void setCareerTalkID(int careerTalkID) {
		this.careerTalkID = careerTalkID;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public int getClicks() {
		return clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
	}

	public int getJoins() {
		return joins;
	}

	public void setJoins(int joins) {
		this.joins = joins;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	
	

	/*
	 * public double getRating() { return rating; }
	 * 
	 * 
	 * public void setRating(double rating) { this.rating = rating; }
	 */

	public static CareerTalk parseBase(JSONObject careerObject)
			throws AppException {
		try {
			CareerTalk careerTalkBase = new CareerTalk();
			careerTalkBase.careerTalkID = careerObject.getInt("id");
			careerTalkBase.schoolName = careerObject.getString("schoolName");
			careerTalkBase.companyID = careerObject.getInt("companyID");
			careerTalkBase.companyName = careerObject.getString("companyName");
			careerTalkBase.companyType = careerObject.getString("type");
			careerTalkBase.topicID = careerObject.getInt("topicID");
			careerTalkBase.place = careerObject.getString("place");
			careerTalkBase.joins = careerObject.getInt("joins");
			careerTalkBase.replies = careerObject.getInt("replies");
			careerTalkBase.time = careerObject.getString("time");
			careerTalkBase.date = careerObject.getString("date");
//			careerTalkBase.createdTime = careerObject.getString("createdTime");
			careerTalkBase.clicks = careerObject.getInt("clicks");
			careerTalkBase.sourceFrom = careerObject.getString("sourceFrom");
			careerTalkBase.url = careerObject.getString("url");
//			careerTalkBase.status = 1; //new data
			careerTalkBase.famous = careerObject.getInt("famous");
			return careerTalkBase;
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
	}

	public static CareerTalk parseNotice(JSONObject careerObject) throws AppException {
		try {
			CareerTalk careerTalk = new CareerTalk();
			careerTalk.companyName = careerObject.getString("companyName");
			careerTalk.schoolName = careerObject.getString("schoolName");
			careerTalk.date = careerObject.getString("date");
			return careerTalk;
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
	}

	@Override
	public int compareTo(CareerTalk another) {
		return this.getDate().compareTo(another.getDate());
	}

}
