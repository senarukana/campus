package com.campusrecruit.bean;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.campusrecruit.app.AppException;

import android.R.integer;
import android.net.ParseException;
import android.util.Log;
import android.util.Xml;

public class User {

	public final static int RELATION_ACTION_DELETE = 0x00;// 取消关注
	public final static int RELATION_ACTION_ADD = 0x01;// 加关注

	public final static int RELATION_TYPE_BOTH = 0x01;// 双方互为粉丝
	public final static int RELATION_TYPE_FANS_HIM = 0x02;// 你单方面关注他
	public final static int RELATION_TYPE_NULL = 0x03;// 互不关注
	public final static int RELATION_TYPE_FANS_ME = 0x04;// 只有他关注我

	private String uid;
	private String location;
	private String name;
	private String email;
	private String pwd;
	private String jointime;
	private String face;
	private int hasFace;

	private int gender;
	private String latestonline;
	private String schoolName;
	private String majorName;
	private String introduction;
	private UserPreference preference;
	
	//setttings
	private boolean backgroundNotice = true;
	private boolean showPicture = true;

	public boolean isBackgroundNotice() {
		return backgroundNotice;
	}

	public void setBackgroundNotice(boolean backgroundNotice) {
		this.backgroundNotice = backgroundNotice;
	}

	public boolean isShowPicture() {
		return showPicture;
	}

	public void setShowPicture(boolean showPicture) {
		this.showPicture = showPicture;
	}

	public User() {

	}

	public User(String email, String majorName, String schoolName, int gender) {
		this.email = email;
		this.majorName = majorName;
		this.schoolName = schoolName;
		this.gender = gender;
	}

	// private int followers;
	// private int fans;
	// private int score;
	// private String account;
	// private boolean isRememberMe;

	// private String expertise;
	// private int relation;
	
	public int getHasFace() {
		return hasFace;
	}

	public void setHasFace(int hasFace) {
		this.hasFace = hasFace;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getMajorName() {
		return majorName;
	}

	public void setMajorName(String majorName) {
		this.majorName = majorName;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public UserPreference getPreference() {
		return preference;
	}

	public void setPreference(UserPreference preference) {
		this.preference = preference;
	}

	public String getJointime() {
		return jointime;
	}

	public void setJointime(String jointime) {
		this.jointime = jointime;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	// public String getExpertise() {
	// return expertise;
	// }
	// public void setExpertise(String expertise) {
	// this.expertise = expertise;
	// }
	// public int getRelation() {
	// return relation;
	// }
	// public void setRelation(int relation) {
	// this.relation = relation;
	// }
	public String getLatestonline() {
		return latestonline;
	}

	public void setLatestonline(String latestonline) {
		this.latestonline = latestonline;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// public int getFollowers() {
	// return followers;
	// }
	// public void setFollowers(int followers) {
	// this.followers = followers;
	// }
	// public int getFans() {
	// return fans;
	// }
	// public void setFans(int fans) {
	// this.fans = fans;
	// }
	// public int getScore() {
	// return score;
	// }
	// public void setScore(int score) {
	// this.score = score;
	// }
	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public static User parse(JSONObject userJsonObject) throws AppException {
		try {
			User user = new User();
			user.uid = userJsonObject.getString("id");
			user.name = userJsonObject.getString("userName");
			user.email = userJsonObject.getString("email");
			user.gender = userJsonObject.getInt("gender");
			user.majorName = userJsonObject.getString("majorName");
			user.schoolName = userJsonObject.getString("schoolName");
			user.introduction = userJsonObject.getString("introduction");
			return user;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
	}

	public static User parseDetail(JSONObject userJsonObject)
			throws AppException {
		try {
			User user = new User();
			if (userJsonObject.has("id")) {
				user.uid = userJsonObject.getString("id");
			}
			user.hasFace = userJsonObject.getInt("face");
			user.name = userJsonObject.getString("userName");
			user.email = userJsonObject.getString("email");
			user.gender = userJsonObject.getInt("gender");
			user.majorName = userJsonObject.getString("majorName");
			user.schoolName = userJsonObject.getString("schoolName");
			user.introduction = userJsonObject.getString("introduction");
			user.preference = UserPreference.parse(userJsonObject);
			return user;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
	}

}
