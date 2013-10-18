package com.campusrecruit.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.util.Log;

import com.campusrecruit.app.AppException;
import com.campusrecruit.common.StringUtils;


public class Notice implements Serializable {
	
	public final static String UTF8 = "UTF-8";
	public final static String NODE_ROOT = "camputRecruit";
	
	public final static int	TYPE_RECRUIT = 1;
	public final static int	TYPE_DISCUSS = 2;

	private int recruitCount;
	private int careerTalkCount;
	private int messageCount;
	private int replyCount;
	private int discussCount;
	private int sum;

	private CareerTalk careerTalk;
	private Recruit recruit;
	public UserMessage getMessage() {
		return message;
	}


	public void setMessage(UserMessage message) {
		this.message = message;
	}


	public BBSReply getReply() {
		return reply;
	}


	public void setReply(BBSReply reply) {
		this.reply = reply;
	}

	private UserMessage message;
	private BBSReply reply;
	private BBSTopic topic;

	public int getMessageCount() {
		return messageCount;
	}


	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}


	public int getReplyCount() {
		return replyCount;
	}


	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	public int getRecruitCount() {
		return recruitCount;
	}


	public void setRecruitCount(int recruitCount) {
		this.recruitCount = recruitCount;
	}

	public int getCareerTalkCount() {
		return careerTalkCount;
	}


	public void setCareerTalkCount(int careerTalkCount) {
		this.careerTalkCount = careerTalkCount;
	}


	public int getDiscussCount() {
		return discussCount;
	}


	public void setDiscussCount(int discussCount) {
		this.discussCount = discussCount;
	}

	public CareerTalk getCareerTalk() {
		return careerTalk;
	}


	public void setCareerTalk(CareerTalk careerTalk) {
		this.careerTalk = careerTalk;
	}


	public void setRecruit(Recruit recruit) {
		this.recruit = recruit;
	}


	public Recruit getRecruit() {
		return recruit;
	}


	public BBSTopic getTopic() {
		return topic;
	}


	public void setTopic(BBSTopic topic) {
		this.topic = topic;
	}
	
	
	public int getSum() {
		return sum;
	}


	public void setSum(int sum) {
		this.sum = sum;
	}

	public static Notice parse(JSONArray jsonArray) throws AppException {
		Notice notice = new Notice();
		try {
			notice.recruitCount =  jsonArray.getJSONObject(1).getInt("RecruitCount");
			notice.careerTalkCount = jsonArray.getJSONObject(0).getInt("CareerTalkCount");
			notice.messageCount = jsonArray.getJSONObject(2).getInt("MessageCount");
			notice.replyCount = jsonArray.getJSONObject(3).getInt("ReplyCount");
			int sum = notice.recruitCount + notice.careerTalkCount + notice.messageCount + notice.replyCount;
			if (notice.careerTalkCount == 1  && sum == 1) {
				notice.careerTalk = CareerTalk.parseNotice(jsonArray.getJSONObject(4));
			}
			if (notice.recruitCount == 1 && sum == 1) {
				notice.recruit = Recruit.parseNotice(jsonArray.getJSONObject(4));
			}
			if (notice.messageCount == 1 && sum == 1) {
				notice.message = UserMessage.parseNotice(jsonArray.getJSONObject(4));
			}
			if (notice.replyCount == 1 && sum == 1) {
				notice.reply = BBSReply.parseNotice(jsonArray.getJSONObject(4));
			}
			notice.sum = 0;
			return notice;
		
		} catch (JSONException e) {
			
			throw AppException.json(e);
		} 
		
        //获得XmlPullParser解析器
        /*XmlPullParser xmlParser = Xml.newPullParser();
        try {        	
            xmlParser.setInput(inputStream, UTF8);
            //获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
            int evtType=xmlParser.getEventType();
			//一直循环，直到文档结束    
			while(evtType!=XmlPullParser.END_DOCUMENT){ 
	    		String tag = xmlParser.getName(); 
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:			    		
			            //通知信息
			            if(tag.equalsIgnoreCase("notice"))
			    		{
			            	notice = new Notice();
			    		}
			            else if(notice != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				notice.setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	notice.setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	notice.setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	notice.setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:		    		
				       	break; 
			    }
			    //如果xml没有结束，则导航到下一个节点
			    evtType=xmlParser.next();
			}		
        } catch (XmlPullParserException e) {
			throw AppException.xml(e);
        } finally {
        	inputStream.close();	
        }      */
//        return notice;       
	}
}