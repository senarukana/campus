package com.campusrecruit.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.campusrecruit.app.AppException;

public class UserMessage implements Serializable{
	private int messageID;
	private String userID;
	private String otherUserID;
	public String getOtherUserID() {
		return otherUserID;
	}
	public void setOtherUserID(String otherUserID) {
		this.otherUserID = otherUserID;
	}

	private String userName;
	private int face;
	private String title;
	private String content;
	private String createdTime;
	private int status;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getMessageID() {
		return messageID;
	}
	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public int getFace() {
		return face;
	}
	public void setFace(int face) {
		this.face = face;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public static UserMessage parseBase(JSONObject messageJsonObject) throws AppException {
		try {
			UserMessage message = new UserMessage();
			message.messageID = messageJsonObject.getInt("id");
			message.userID = messageJsonObject.getString("userID");
			message.createdTime = messageJsonObject.getString("createdTime");
			message.content = messageJsonObject.getString("content");
			message.userName = messageJsonObject.getString("userName");
			message.face = messageJsonObject.getInt("face");
			message.status = 1;
			return message;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
	}
	

	public static UserMessage parseContact(JSONObject messageJsonObject) throws AppException {
		try {
			UserMessage message = new UserMessage();
			message.userID = messageJsonObject.getString("id");
			message.createdTime = messageJsonObject.getString("createdTime");
			message.userName = messageJsonObject.getString("userName");
			message.face = messageJsonObject.getInt("face");
			message.content = messageJsonObject.getString("content");
			message.status = 1;
			return message;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
	}
	
	public static UserMessage parseNotice(JSONObject messageJsonObject) throws AppException {
		try {
			UserMessage message = new UserMessage();
			message.content = messageJsonObject.getString("content");
			message.userName = messageJsonObject.getString("userName");
			message.status = 1;
			return message;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
	}
	
}
