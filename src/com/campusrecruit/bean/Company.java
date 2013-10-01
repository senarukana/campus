package com.campusrecruit.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.campusrecruit.app.AppException;

public class Company implements Serializable{
	private int companyID;
	private String companyName;
	private String type;
	private String industry;
	private String introduction;
	private String place; //公司地点
	
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public int getCompanyID() {
		return companyID;
	}
	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	
	public static Company parseDetail(JSONObject object) throws AppException {
		Company company = new Company();
		try {
			company.type = object.getString("type");
			company.industry = object.getString("industry");
			company.place = object.getString("place");
			company.introduction = object.getString("introduction");
			return company;
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
	}
	
	
}
