package com.campusrecruit.bean;

import org.json.JSONException;
import org.json.JSONObject;

import com.campusrecruit.app.AppException;

public class UserPreference {
/*	private enum CompanyScale {
		SMALL , NORMAL, BIG;
	}*/
	private String province;
	/*
	 * 1 国企
	 * 2 私企
	 * 3 外企
	 */
	private String companyType;
	/*
	 * 1 计算机
	 * 2 通信
	 * 3 电子
	 */
	private String companyIndustry;
	private String position;
	/*
	 * 1 宣讲会
	 * 2 校园招聘
	 * 3 私信
	 * 4 回复
	 */
	private String notifyType = "1,2,3,4";
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCompanyType() {
		return companyType;
	}
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}
	public String getCompanyIndustry() {
		return companyIndustry;
	}
	public void setCompanyIndustry(String companyIndustry) {
		this.companyIndustry = companyIndustry;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getNotifyType() {
		return notifyType;
	}
	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}
	
	public static UserPreference parse(JSONObject preferenceJsonObject) throws AppException {
		try {
			UserPreference preference = new UserPreference();
			preference.province = preferenceJsonObject.getString("province");
			preference.companyType = preferenceJsonObject.getString("companyType");
			preference.companyIndustry = preferenceJsonObject.getString("industry");
			preference.notifyType = preferenceJsonObject.getString("notifyType");
			return preference;
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
	}
}