package com.campusrecruit.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.campusrecruit.app.AppException;

import android.R.integer;

public class BBSSection implements Serializable{
	private int sectionID;
	private String sectionName;
	private String companyIndustry;
	private String companyType;
	private int companyID;

	private String admin;
	private int joins;
	private int topics;
	private int watches;
	private int status;
	private int famous;
	private int isJoined;
	
	public int getFamous() {
		return famous;
	}
	public void setFamous(int famous) {
		this.famous = famous;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getSectionID() {
		return sectionID;
	}
	public void setSectionID(int sectionID) {
		this.sectionID = sectionID;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
	}
	public int getJoins() {
		return joins;
	}
	public void setJoins(int joins) {
		this.joins = joins;
	}
	public int getTopics() {
		return topics;
	}
	public void setTopics(int topics) {
		this.topics = topics;
	}
	public int getWatches() {
		return watches;
	}
	public void setWatches(int watches) {
		this.watches = watches;
	}
	public String getCompanyIndustry() {
		return companyIndustry;
	}
	public void setCompanyIndustry(String companyIndustry) {
		this.companyIndustry = companyIndustry;
	}
	public String getCompanyType() {
		return companyType;
	}
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}
	
	public static BBSSection parseBase(JSONObject object) throws AppException {
		try {
			BBSSection section = new BBSSection();
			section.sectionID = object.getInt("id");
			section.sectionName = object.getString("sectionName");
			section.topics = object.getInt("topics");
			section.companyType = object.getString("companyType");
			section.companyIndustry = object.getString("companyIndustry");
			section.joins = object.getInt("joins");
			section.isJoined = object.getInt("join");
			section.famous = object.getInt("famous");
			return section;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
		
	}
}
