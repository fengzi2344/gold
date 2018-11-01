package com.goldgyro.platform.base.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * UUID生成工具类
 * @author wg2993
 * @version 2018/7/8
 */
public class UUIDUtils {
	/**
	 * 自动生成32位的UUid，对应数据库的主键id进行插入用。
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * 生成24位数字唯一编码
	 * @return
	 */
	public static String getUUIDNum(){ 
		String orderNo = "" ; 
		String sdf = new SimpleDateFormat("yyyyMMddHHMMSS").format(new Date()); 
		
		String trandNo = String.valueOf((Math.random() * 9 + 1) * 1000000); 
		trandNo = trandNo.replaceAll("\\.", "");
		
		orderNo = trandNo.substring(0, 10); 
		orderNo = sdf+orderNo; 
		
		return orderNo ; 
	}
	
	/**
	 * 生成指定位数的数字唯一编码
	 * @param length ： UUID的长度，必须>15
	 */
	public static String getUUIDNum(int length){
		String orderNo = "" ;
		String trandNo = String.valueOf((Math.random() * 9 + 1) * 1000000); 
		trandNo = trandNo.replaceAll("\\.", "");
		String sdf = new SimpleDateFormat("yyyyMMddHHMMSS").format(new Date()); 
		orderNo = trandNo.substring(0, length - sdf.length()); 
		orderNo = sdf+orderNo; 
		return orderNo ; 
	}
	public static void main(String[] args){
		System.out.println(getUUIDNum(24));
	}
}
