package com.campusrecruit.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateDataFormat {

	public static final String DATE_FORMAT = "yyyyMMddHHmmss";
	private static final SimpleDateFormat dateFormat = new
	   SimpleDateFormat(DATE_FORMAT);
	 
	public static long formatDateAsLong(Calendar cal){
	   return Long.parseLong(dateFormat.format(cal.getTime()));
	}
	 
	public static Calendar getCalendarFromFormattedLong(long l){
	   try {
	                 Calendar c = Calendar.getInstance();
	                 c.setTime(dateFormat.parse(String.valueOf(l)));
	                 return c;
	                  
	          } catch (ParseException e) {
	                 return null;
	          }
	}
}
