package com.campusrecruit.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.util.Log;

import com.campusrecruit.app.AppException;


public class Recruit implements Serializable, Comparable<Recruit>{
	private int recruitID;
	private int topicID;
	private String position; //职位
	private String createdTime;
	private String place;
	private int joins; //收藏数量
	private int replies; //回复数量
	private String description; //职位信息
	private int famous; //名企
	private int companyID;
	private String companyName;
	private Company company;
	private int isJoined;
	private int clicks;
	private String sourceFrom;
	private String url;
	private String form;
	
	private int status;

	private String contact; //招聘方式
	private String state;
	private String statTime;
	
	private String content;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	//for adapter header
	private int flag = 0;
//	private String endTime;
//	private ArrayList<String> tagList;
//	private int likes;
//	private int watches;
//	private double rating;
//	private int isWatched;
	
	
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
	public int getClicks() {
		return clicks;
	}
	public void setClicks(int clicks) {
		this.clicks = clicks;
	}
	
	public int getFamous() {
		return famous;
	}
	public void setFamous(int famous) {
		this.famous = famous;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStatTime() {
		return statTime;
	}
	public void setStatTime(String statTime) {
		this.statTime = statTime;
	}
	public int getCompanyID() {
		return companyID;
	}
	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public int getJoins() {
		return joins;
	}
	public void setJoins(int joins) {
		this.joins = joins;
	}
	public int getReplies() {
		return replies;
	}
	public void setReplies(int replies) {
		this.replies = replies;
	}
	public int getIsJoined() {
		return isJoined;
	}
	public void setIsJoined(int isJoined) {
		this.isJoined = isJoined;
	}
	public int getTopicID() {
		return topicID;
	}
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	
	public int getRecruitID() {
		return recruitID;
	}
	public void setRecruitID(int recruitID) {
		this.recruitID = recruitID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	/*
	public String getRequirement() {
		return requirement;
	}
	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	
	public ArrayList<String> getTagList() {
		return tagList;
	}
	public void setTagList(ArrayList<String> tagList) {
		this.tagList = tagList;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getClicks() {
		return clicks;
	}
	public void setClicks(int clicks) {
		this.clicks = clicks;
	
	public int getIsWatched() {
		return isWatched;
	}
	public void setIsWatched(int isWatched) {
		this.isWatched = isWatched;
	}

	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}*/
	
	public static Recruit parseBase(JSONObject recruitObject) throws AppException {
		try {
			Recruit recruitBase = new Recruit();
			recruitBase.recruitID = recruitObject.getInt("id");
			recruitBase.topicID = recruitObject.getInt("topicID");
			recruitBase.company = new Company();
			recruitBase.company.setCompanyName(recruitObject.getString("companyName"));
			recruitBase.company.setType(recruitObject.getString("type"));
			recruitBase.company.setCompanyID(recruitObject.getInt("companyID"));
			recruitBase.company.setIndustry(recruitObject.getString("industry"));
			recruitBase.joins = recruitObject.getInt("joins");
			recruitBase.replies = recruitObject.getInt("replies");
			recruitBase.createdTime = recruitObject.getString("createdTime");
			recruitBase.position = recruitObject.getString("position");
			recruitBase.place = recruitObject.getString("place");
			recruitBase.clicks = recruitObject.getInt("clicks");
			recruitBase.famous = recruitObject.getInt("famous");
			recruitBase.sourceFrom = recruitObject.getString("sourceFrom");
			recruitBase.status = 1; //new data
			return recruitBase;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
	}
	
	public static Recruit parseNotice(JSONObject recruitObject) throws AppException {
		try {
			Recruit recruitBase = new Recruit();
			recruitBase.companyName = recruitObject.getString("companyName");
			recruitBase.position = recruitObject.getString("position");
			return recruitBase;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
	}
	
	public static Recruit parseDetail(JSONObject recruitObject) throws AppException {
		Recruit recruit = new Recruit();
		try {
			recruit.description = recruitObject.getString("description");
			recruit.form = recruitObject.getString("form");
			recruit.url = recruitObject.getString("url");
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
		return recruit;
	}
	
	public static Recruit parseDetailSimple(JSONObject recruitObject) throws AppException {
		Recruit recruit = new Recruit();
		try {
			recruit.content = recruitObject.getString("content");
			recruit.url = recruitObject.getString("url");
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
		return recruit;
	}
	
	public static Recruit parseRecruitProcess(JSONObject recruitObject) throws AppException {
		Recruit recruit = new Recruit();
		try {
			recruit.contact = recruitObject.getString("contact");
			if (recruitObject.has("state")) {
				recruit.state = recruitObject.getString("state");
				recruit.statTime = recruitObject.getString("stateTime");
			}
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
		return recruit;
	}
	@Override
	public int compareTo(Recruit another) {
		return another.createdTime.compareTo(this.createdTime);
	}
}
