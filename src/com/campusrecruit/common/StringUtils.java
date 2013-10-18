package com.campusrecruit.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import android.util.Log;

public class StringUtils {
	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	// private final static SimpleDateFormat dateFormater = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// private final static SimpleDateFormat dateFormater2 = new
	// SimpleDateFormat("yyyy-MM-dd");

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	private final static ThreadLocal<SimpleDateFormat> dateFormater3 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
	};

	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		Log.i("bugdate", strDate);
		return strDate;
	}

	public static String calendarToString(Calendar cal) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		String strDate = sdfDate.format(cal.getTime());
		return strDate;
	}

	public static Calendar stringToCalendar(String str) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdfDate.parse(str));
		} catch (ParseException e) {
			return null;
		}
		return cal;
	}

	public static String forammtedTime(String str) {
		Calendar cal = stringToCalendar(str);
		return String.format("%d年%d月%d日", cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}

	public static String getCurrentTimeStamp(int minute) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		Log.i("bugdate", strDate);
		return strDate;
	}

	public static String getYesterdayTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");// dd/MM/yyyy
		Date now = new Date();
		now.setDate(now.getDate() - 1);
		String strDate = sdfDate.format(now);
		return strDate;
	}

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toAlarmDate(String sdate) {
		try {
			return dateFormater3.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toHappenDate(String sdate) {
		try {
			return dateFormater2.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/*
	 * return yyyy-MM-dd
	 */
	public static String trimed_time(String sdate) {
		int index = sdate.indexOf(' ');
		if (index == -1)
			return sdate;
		Log.i("bug", "trimmed data" + sdate.substring(0, index));
		return sdate.substring(0, index);
	}

	public static String friendly_happen_simple_time(String sdate) {
		Date dataTime = toHappenDate(sdate);
		if (dataTime == null) {
			return "Unknown";
		}
		Calendar dataCalendar = Calendar.getInstance();
		dataCalendar.setTime(dataTime);
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.get().format(cal.getTime());
		long lt = dataTime.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (lt - ct);
		boolean sameDay = dataCalendar.get(Calendar.YEAR) == cal
				.get(Calendar.YEAR)
				&& dataCalendar.get(Calendar.DAY_OF_YEAR) == cal
						.get(Calendar.DAY_OF_YEAR);
		if (sameDay) {
			ftime = "今天";
		} else if (dataCalendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
				&& (dataCalendar.get(Calendar.DAY_OF_YEAR)
						- cal.get(Calendar.DAY_OF_YEAR) == 1)) {
			ftime = "明天";
		} else if (dataCalendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
				&& (dataCalendar.get(Calendar.DAY_OF_YEAR)
						- cal.get(Calendar.DAY_OF_YEAR) == 2)) {
			ftime = "后天";
		} else {
			ftime = dateFormater2.get().format(dataTime);
		}
		return ftime;
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_happen_time(String sdate) {
		Date dataTime = toHappenDate(sdate);
		if (dataTime == null) {
			return "Unknown";
		}
		Calendar dataCalendar = Calendar.getInstance();
		dataCalendar.setTime(dataTime);
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.get().format(cal.getTime());
		if (curDate.equals(dataTime)) {
			ftime = "今天";
		}
		long lt = dataTime.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (lt - ct);
		boolean sameDay = dataCalendar.get(Calendar.YEAR) == cal
				.get(Calendar.YEAR)
				&& dataCalendar.get(Calendar.DAY_OF_YEAR) == cal
						.get(Calendar.DAY_OF_YEAR);
		if (days < 0 && !sameDay) {
			Log.i("bug", "data date " + dataCalendar.get(Calendar.DAY_OF_YEAR));
			Log.i("bug", "cal date " + cal.get(Calendar.DAY_OF_YEAR));
			return "过期数据";
		}
		if (sameDay) {
			ftime = "今天";
		} else if (dataCalendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
				&& (dataCalendar.get(Calendar.DAY_OF_YEAR)
						- cal.get(Calendar.DAY_OF_YEAR) == 1)) {
			ftime = "明天";
		} else if (dataCalendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
				&& (dataCalendar.get(Calendar.DAY_OF_YEAR)
						- cal.get(Calendar.DAY_OF_YEAR) == 2)) {
			ftime = "后天";
		} else {
			ftime = dateFormater2.get().format(dataTime);
		}
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		int week = dataCalendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (week < 0)
			week = 0;
		return ftime + " " + weekDays[week] + " ";
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_created_time(String sdate) {
		Date dataTime = toHappenDate(sdate);
		if (dataTime == null) {
			return "Unknown";
		}
		Calendar dataCalendar = Calendar.getInstance();
		dataCalendar.setTime(dataTime);
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.get().format(cal.getTime());
		long lt = dataTime.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		boolean sameDay = dataCalendar.get(Calendar.YEAR) == cal
				.get(Calendar.YEAR)
				&& dataCalendar.get(Calendar.DAY_OF_YEAR) == cal
						.get(Calendar.DAY_OF_YEAR);
		int days = (int) (ct - lt);
		if (sameDay) {
			ftime = "今天";
		} else if (dataCalendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
				&& (dataCalendar.get(Calendar.DAY_OF_YEAR)
						- cal.get(Calendar.DAY_OF_YEAR) == -1)) {
			ftime = "昨天";
		} else if (dataCalendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
				&& (dataCalendar.get(Calendar.DAY_OF_YEAR)
						- cal.get(Calendar.DAY_OF_YEAR) == -2)) {
			ftime = "前天";
		} else {
			ftime = dateFormater2.get().format(dataTime);
		}
		Log.i("ft", ftime);
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		int week = dataCalendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (week < 0)
			week = 0;
		return ftime + " " + weekDays[week];
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_time(String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.get().format(cal.getTime());
		String paramDate = dateFormater2.get().format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 10) {
			ftime = days + "天前";
		} else if (days > 10) {
			ftime = dateFormater2.get().format(time);
		}
		return ftime;
	}

	private static final String IPV4_BASIC_PATTERN_STRING = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}"
			+ // initial 3 fields, 0-255 followed by .
			"([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"; // final field,
																// 0-255

	private static final Pattern IPV4_PATTERN = Pattern.compile("^"
			+ IPV4_BASIC_PATTERN_STRING + "$");

	private static final Pattern IPV4_MAPPED_IPV6_PATTERN = // TODO does not
															// allow for
															// redundant leading
															// zeros
	Pattern.compile("^::[fF]{4}:" + IPV4_BASIC_PATTERN_STRING + "$");

	private static final Pattern IPV6_STD_PATTERN = Pattern
			.compile("^[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7}$");

	private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern
			.compile("^(([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,5})?)" + // 0-6
																		// hex
																		// fields
					"::" + "(([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,5})?)$"); // 0-6
																				// hex
																				// fields

	/*
	 * The above pattern is not totally rigorous as it allows for more than 7
	 * hex fields in total
	 */
	private static final char COLON_CHAR = ':';

	// Must not have more than 7 colons (i.e. 8 fields)
	private static final int MAX_COLON_COUNT = 7;

	/**
	 * Checks whether the parameter is a valid IPv4 address
	 * 
	 * @param input
	 *            the address string to check for validity
	 * @return true if the input parameter is a valid IPv4 address
	 */
	public static boolean isIPv4Address(final String input) {
		return IPV4_PATTERN.matcher(input).matches();
	}

	public static boolean isIPv4MappedIPv64Address(final String input) {
		return IPV4_MAPPED_IPV6_PATTERN.matcher(input).matches();
	}

	/**
	 * Checks whether the parameter is a valid standard (non-compressed) IPv6
	 * address
	 * 
	 * @param input
	 *            the address string to check for validity
	 * @return true if the input parameter is a valid standard (non-compressed)
	 *         IPv6 address
	 */
	public static boolean isIPv6StdAddress(final String input) {
		return IPV6_STD_PATTERN.matcher(input).matches();
	}

	/**
	 * Checks whether the parameter is a valid compressed IPv6 address
	 * 
	 * @param input
	 *            the address string to check for validity
	 * @return true if the input parameter is a valid compressed IPv6 address
	 */
	public static boolean isIPv6HexCompressedAddress(final String input) {
		int colonCount = 0;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == COLON_CHAR) {
				colonCount++;
			}
		}
		return colonCount <= MAX_COLON_COUNT
				&& IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches();
	}

	/**
	 * Checks whether the parameter is a valid IPv6 address (including
	 * compressed).
	 * 
	 * @param input
	 *            the address string to check for validity
	 * @return true if the input parameter is a valid standard or compressed
	 *         IPv6 address
	 */
	public static boolean isIPv6Address(final String input) {
		return isIPv6StdAddress(input) || isIPv6HexCompressedAddress(input);
	}

	/**
	 * 判断ip是否在指定网段中
	 * 
	 * @author dh
	 * @param iparea
	 * @param ip
	 * @return boolean
	 */
	/*
	 * 59.64.128.0 ~ 59.64.255.255 118.229.128.0 ~ 118.229.255.255 211.68.68.0
	 * ~211.68.71.255
	 */
	public static boolean ipIsInNet(String ipAddress) {
		try {
			if (ipAddress.contains("::") || ipAddress.contains("wlan")
					|| isIPv6Address(ipAddress)) {
				return false;
			}
			boolean isInnerIp = false;
			long ipNum = getIpNum(ipAddress);
			long aBegin = getIpNum("59.64.128.0");
			long aEnd = getIpNum("59.64.255.255");
			long bBegin = getIpNum("118.229.128.0");
			long bEnd = getIpNum("118.229.255.255");
			long cBegin = getIpNum("211.68.68.0");
			long cEnd = getIpNum("192.168.255.255");
			isInnerIp = isInner(ipNum, aBegin, aEnd)
					|| isInner(ipNum, bBegin, bEnd)
					|| isInner(ipNum, cBegin, cEnd)
					|| ipAddress.equals("127.0.0.1");
			return isInnerIp;
		} catch (Exception e) {
			return false;
		}
	}

	private static long getIpNum(String ipAddress) {
		String[] ip = ipAddress.split("\\.");
		long a = Integer.parseInt(ip[0]);
		long b = Integer.parseInt(ip[1]);
		long c = Integer.parseInt(ip[2]);
		long d = Integer.parseInt(ip[3]);

		long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
		return ipNum;
	}

	private static boolean isInner(long userIp, long begin, long end) {
		return (userIp >= begin) && (userIp <= end);
	}

	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = dateFormater2.get().format(today);
			String timeDate = dateFormater2.get().format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}
}
