package com.goldgyro.platform.base.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间工具
 * @author wg2993
 * @version 2018/7/22
 */
public class DateTimeUtils {
	
	/**
	 * 将时间加N分钟
	 * @param startDate：原时间
	 * @param minute：要增加或减少的分数
	 * @return
	 */
	public static Date addTime(Date startDate, int minute) {
		Calendar calendar = Calendar.getInstance();
        Date date = new Date(startDate.getTime());
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);

        return calendar.getTime();
	}
	
	/**
	 * 计算两个时间差（分）
	 * 
	 * @param startTime：开始时间
	 * @param endTime：结束时间
	 * @return
	 */
	public static int calcTimeSub(String startTime, String endTime) {
		DateFormat df = new SimpleDateFormat("hh:mm");
		int minute = 0;
		
		try {
			Date st = new Date(df.parse(startTime).getTime());
			Date et = new Date(df.parse(endTime).getTime());
			minute = (int)(et.getTime()-st.getTime())/1000/60;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return minute;
	}
	
	
	/**
	 * 计算两个时间差（分）
	 * 
	 * @param startTime：开始时间
	 * @param endTime：结束时间
	 * @return
	 */
	public static long calcTimeSub(Date startTime, Date endTime) {
		return (int)(endTime.getTime()-startTime.getTime())/1000/60;
	}
	
	/**
	 * 格式化日期时间
	 * @param strDateTime：原时间
	 * @param formatter：格式
	 * @return
	 */
	public static Date formatDateTime(String strDateTime , String formatter) {
		DateFormat df = new SimpleDateFormat(formatter);
		Date dateTime = null;
		
		try {
			dateTime = df.parse(strDateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return dateTime;
	}
	
	/**
	 *  格式化日期时间
	 * @param dateTime：原时间
	 * @param formatter：格式
	 * @return
	 */
	public static String formatDateTime(Date dateTime , String formatter) {
		DateFormat df = new SimpleDateFormat(formatter);
		String strDateTime = null;
		
		strDateTime = df.format(dateTime);
		
		return strDateTime;
	}
	
	/**
	 *  格式化日期
	 * @param dateTime：原时间
	 * @param formatter：格式
	 * @return
	 */
	public static Date formatDate(Date dateTime , String formatter) {
		DateFormat df = new SimpleDateFormat(formatter);
		Date date = null;
		
		try {
			date = df.parse(df.format(dateTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
}
