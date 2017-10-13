package com.jl.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
	 public static String TIMEZONE = "GMT+08:00";
	//public static String TIMEZONE = Constants.getValue("timezone");

	// str format: yyyy-mm-dd
	public static java.sql.Date getDateFromString(String str) {
		if (str == null || str.equals(""))
			return null;
		java.sql.Date date = null;
		try {
			date = java.sql.Date.valueOf(str);
			return date;
		} catch (Exception e) {
		}
		return null;
	}

	public static java.sql.Timestamp getTimestampFromString(String str) {
		if (str == null || str.equals(""))
			return null;
		java.sql.Timestamp date = null;
		try {
			// System.out.println(str);
			if (str.split(":").length == 3)
				date = java.sql.Timestamp.valueOf(str);
			else
				date = java.sql.Timestamp.valueOf(str + ":00.0");

			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	// str format: yyyy-MM-dd hh:mm:ss.fffffffff --
	// java.sql.Timestamp.valueOf(str)
	// str format: yyyy-mm-dd -- first java.sql.Date , second java.sql.Timestamp
	public static java.sql.Timestamp getTimestampFromShortString(String str) {
		if (str == null || str.equals(""))
			return null;
		try {
			java.sql.Date dt = getDateFromString(str);
			long l = dt.getTime();
			java.sql.Timestamp ts = new java.sql.Timestamp(l);
			return ts;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getStringFromDate(java.sql.Date dt) {
		if (dt == null)
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String st = "";
		try {
			st = df.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return st;
	}

	public static String getStringFromDate(java.util.Date dt) {
		if (dt == null)
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String st = "";
		try {
			st = df.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return st;
	}

	public static Timestamp getTimestampFromDate(java.util.Date dt) {
		if (dt == null)
			return null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// df.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
		Timestamp st = null;
		try {
			String str = df.format(dt);
			st = DateUtil.getTimestampFromString(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return st;
	}
	public static String getCurrentYear() {
		java.util.Date dt = Calendar.getInstance().getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		return df.format(dt);
	}

	public static java.sql.Date getCurDate() {
		java.sql.Date mDate;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String mDateTime = formatter.format(cal.getTime());
		return (java.sql.Date.valueOf(mDateTime));
	}

	public static Timestamp getCurTimestamp() {
		String ct = getCurrentDateAndTime();
		return Timestamp.valueOf(ct);
	}

	// return yyyy-MM-dd
	public static String getCurentDate() {
		//java.util.Date dt = Calendar.getInstance().getTime();
		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//return df.format(dt);
		TimeZone tzCN = TimeZone.getTimeZone(TIMEZONE);
		Calendar calendar = Calendar.getInstance(tzCN);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return year + "-" + lpad(month) + "-" + lpad(day);
	}

	public static Date getCurentDateByTimeZone() throws Exception {
		TimeZone tzCN = TimeZone.getTimeZone(TIMEZONE);
		Calendar calendar = Calendar.getInstance(tzCN);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int _hh = calendar.get(Calendar.HOUR_OF_DAY);
		int _mm = calendar.get(Calendar.MINUTE);
		int _ss = calendar.get(Calendar.SECOND);
		String month_str = String.valueOf(month);
		if (month < 10) {
			month_str = "0" + month;
		}
		String day_str = String.valueOf(day);
		if (day < 10) {
			day_str = "0" + day;
		}
		String hh_str = String.valueOf(_hh);
		if (_hh < 10) {
			hh_str = "0" + _hh;
		}
		String mm_str = String.valueOf(_mm);
		if (_mm < 10) {
			mm_str = "0" + _mm;
		}
		String ss_str = String.valueOf(_ss);
		if (_ss < 10) {
			ss_str = "0" + _ss;
		}
		String currentDateTimeNoSencond = year + "-" + month_str + "-"
				+ day_str + " " + hh_str + ":" + mm_str + ":" + ss_str;

		java.text.SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return sdf.parse(currentDateTimeNoSencond);
	}

	public static String getPreviousMonthDate() {
		TimeZone tzCN = TimeZone.getTimeZone(TIMEZONE);
		Calendar calendar = Calendar.getInstance(tzCN);
		calendar.add(Calendar.MONTH, -1);
		int beforeyear = calendar.get(Calendar.YEAR);
		int beforemonth = calendar.get(Calendar.MONTH) + 1;
		int beforeday = calendar.get(Calendar.DAY_OF_MONTH);
		return beforeyear + "-" + lpad(beforemonth) + "-" + lpad(beforeday);
	}

	public static String getCurrentDateVersion() {
		java.util.Date dt = Calendar.getInstance().getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		return df.format(dt);
	}

	// test function
	public static String getTestCurrentDateAndTimeNoTimeZone() {
		java.util.Date dt = Calendar.getInstance().getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(dt);
	}

	// test function
	public static String getTestCurrentDateAndTime() {
		TimeZone tzCN = TimeZone.getTimeZone(TIMEZONE);
		java.util.Date dt = Calendar.getInstance(tzCN).getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(dt);
	}

	// test function
	public static String getTestCurDateAndTime() {
		TimeZone tzCN = TimeZone.getTimeZone(TIMEZONE);
		Locale lc = Locale.SIMPLIFIED_CHINESE;
		java.util.Date dt = Calendar.getInstance(tzCN, lc).getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", lc);
		return df.format(dt);
	}

	public static String getCurrentDateAndTime(){
		TimeZone tzCN = TimeZone.getTimeZone(TIMEZONE);
		/*
		 * java.util.Date dt = Calendar.getInstance(tzCN).getTime();
		 * SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 * return df.format(dt);
		 */
		Calendar calendar = Calendar.getInstance(tzCN);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int ss = calendar.get(Calendar.SECOND);
		return year + "-" + lpad(month) + "-" + lpad(day) + " " + lpad(hour)
				+ ":" + lpad(minute) + ":" + lpad(ss);
	}

	public static String lpad(int a) {
		return a < 10 ? "0" + a : String.valueOf(a);
	}

	public static String getShortFromDateTime(java.sql.Timestamp dt) {
		if (dt == null)
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String st = "";
		try {
			st = df.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return st;
	}

	public static String getStringFromDateTime(java.sql.Timestamp dt) {
		if (dt == null)
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String st = "";
		try {
			st = df.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return st;
	}

	public static String getStringFromDateTimeNoSec(java.util.Date dt) {
		if (dt == null)
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String st = "";
		try {
			st = df.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return st;
	}

	public static String getStringFromDateTimeNoSec(java.sql.Timestamp dt) {
		if (dt == null)
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String st = "";
		try {
			st = df.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return st;
	}

	public static String getStringFromDateTime(java.sql.Date dt) {
		if (dt == null)
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String st = "";
		try {
			st = df.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return st;
	}

	public static String getStringFromDateTime(java.util.Date dt) {
		if (dt == null)
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String st = "";
		try {
			st = df.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return st;
	}

	public static java.sql.Date getDateAndTimeFromString(String str) {
		if (str == null || str.equals(""))
			return null;
		java.sql.Date date = null;
		try {
			date = java.sql.Date.valueOf(str);

			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 
	 * @param str
	 *            String
	 * @return Timestamp
	 */
	public static java.sql.Timestamp getTimestampAndTimeFromString(String str) {
		if (str == null || str.equals(""))
			return null;
		java.sql.Timestamp ts = null;
		try {
			if (str.length() <= 10) {
				str += " 00:00:0";
			} else if (str.length() <= 16) {
				str += ":00.0";
			}
			ts = java.sql.Timestamp.valueOf(str);

			return ts;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getDateAndHhMmFromString(String str) {
		if (str == null || str.trim().equals("")) {
			return "";
		}
		String result = "";
		try {
			int index = str.lastIndexOf(":");
			int length = str.length();
			result = str.substring(0, index);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getCurentMonthLastDayAsString() {
		java.util.Calendar currentCal = java.util.Calendar.getInstance();
		int year = currentCal.get(currentCal.YEAR);
		int month = currentCal.get(currentCal.MONTH) + 1;
		String smon = (month < 10) ? "0" + month : "" + month;
		int day = currentCal.getActualMaximum(currentCal.DAY_OF_MONTH);
		String sday = (day < 10) ? "0" + day : "" + day;
		String currentMonthLastDate = year + "-" + smon + "-" + sday;
		return currentMonthLastDate;
	}

	public static String getCurentMonthFirstDayAsString() {
		java.util.Calendar currentCal = java.util.Calendar.getInstance();
		int year = currentCal.get(currentCal.YEAR);
		int month = currentCal.get(currentCal.MONTH) + 1;
		String smon = (month < 10) ? "0" + month : "" + month;
		String currentMonthFirstDate = year + "-" + smon + "-" + "01";
		return currentMonthFirstDate;
	}

	public static void main(String args[]) {
		// java.util.Date dt = Calendar.getInstance().getTime();
		// java.sql.Timestamp ts =new java.sql.Timestamp(dt.getTime());
		// System.out.println(ts.toString());
		// System.out.println(DateUtil.getTimestampFromString("2004-2-8
		// 11:23"));
		System.out.println("mains");
		System.out.println(DateUtil
				.getTimestampFromString("2004-02-8 13:22:18").toString());
	}
}
