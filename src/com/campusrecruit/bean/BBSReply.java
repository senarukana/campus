package com.campusrecruit.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.campusrecruit.app.AppException;

public class BBSReply implements Serializable{
	private int replyID;
	private int topicID;
	private String userID;
	private String userName;
	private int face;
	private String content;
	private String createdTime;
	private String modifiedTime;
	private int status;
	

	//用于展示他人回复我的内容
	private String myContent;
	
	public int getFace() {
		return face;
	}
	public void setFace(int face) {
		this.face = face;
	}

	private List<BBSReply> replies;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getReplyID() {
		return replyID;
	}
	public void setReplyID(int replyID) {
		this.replyID = replyID;
	}
	public int getTopicID() {
		return topicID;
	}
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public String getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public List<BBSReply> getReplies() {
		return replies;
	}
	public void setReplies(List<BBSReply> replies) {
		this.replies = replies;
	}
	public String getMyContent() {
		return myContent;
	}
	public void setMyContent(String myContent) {
		this.myContent = myContent;
	}

	

	public static BBSReply parseBase(JSONObject replyJsonObject) throws AppException {
		try {
			BBSReply reply = new BBSReply();
			reply.replyID = replyJsonObject.getInt("id");
			reply.userName = replyJsonObject.getString("userName");
			reply.content = replyJsonObject.getString("content");
			reply.createdTime = replyJsonObject.getString("createdTime");
			reply.userID = replyJsonObject.getString("userID");
			reply.face = replyJsonObject.getInt("face");
			return reply;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
	}
	
	public static BBSReply parseDetail(JSONArray replyJsonArray) throws AppException {
		try {
			BBSReply reply = parseBase(replyJsonArray.getJSONObject(0));
			JSONArray replyArray = replyJsonArray.getJSONArray(1);
			reply.replies = new ArrayList<BBSReply>();
			for (int i = 0; i < replyArray.length(); i++) {
				reply.replies.add(parseBase(replyArray.getJSONObject(i)));
			}
			return reply;
		} catch (JSONException e) {	
			
			throw AppException.json(e);
		}
	} 
	
	public static BBSReply parseReplyByOther(JSONObject replyJsonObject) throws AppException {
		try {
			BBSReply reply = new BBSReply();
			reply.setTopicID(replyJsonObject.getInt("topicID"));
			reply.setReplyID(replyJsonObject.getInt("replyID"));
			reply.setFace(replyJsonObject.getInt("face"));
			reply.content = replyJsonObject.getString("content");
			reply.myContent = replyJsonObject.getString("mycontent");
			reply.userID = replyJsonObject.getString("userID");
			reply.userName = replyJsonObject.getString("userName");
			reply.createdTime = replyJsonObject.getString("createdTime");
			return reply;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
	}
	
	public static BBSReply parseReplyToOther(JSONObject replyJsonObject) throws AppException {
		try {
			BBSReply reply = new BBSReply();
			reply.setTopicID(replyJsonObject.getInt("topicID"));
			reply.setFace(replyJsonObject.getInt("face"));
			reply.content = replyJsonObject.getString("content");
			reply.myContent = replyJsonObject.getString("mycontent");
			reply.userID = replyJsonObject.getString("userID");
			reply.userName = replyJsonObject.getString("userName");
			reply.createdTime = replyJsonObject.getString("createdTime");
			return reply;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
	}
	
	public static BBSReply parseNotice(JSONObject replyJsonObject) throws AppException {
		try {
			BBSReply reply = new BBSReply();
			reply.content = replyJsonObject.getString("content");
			reply.userName = replyJsonObject.getString("userName");
			Log.i("test","parse reply complete");
			return reply;
		} catch (JSONException e) {
			
			throw AppException.json(e);
		}
	}
}
