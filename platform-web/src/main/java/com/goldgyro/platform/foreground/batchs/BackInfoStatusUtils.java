package com.goldgyro.platform.foreground.batchs;

import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;

public class BackInfoStatusUtils {
	static Logger logger = Logger.getLogger(BackInfoStatusUtils.class);
	
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static JSONObject getHeadData(String backInfo) {
		if(null == backInfo) {
			return null;
		}
		
		JSONObject head = null;
		
		try {
			String encryptData =  ((Map<String, String>)JSON.parse(backInfo)).get("encryptData");
			String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0,16));
			head = ((Map<String, JSONObject>)JSON.parse(msg)).get("head");
		}catch(Exception ex) {
			logger.error("解析快捷支付接口返回信息时出现异常！", ex);
		}
		
		return head;
	}
	
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static String getStatusFromencryptData(String encryptData) {
		if(null == encryptData) {
			return null;
		}
		
		String returnMsg = "";
		
		try {
			String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0,16));
			JSONObject head = ((Map<String, JSONObject>)JSON.parse(msg)).get("head");
			returnMsg = (String)head.get("respMsg");
		}catch(Exception ex) {
			logger.error("解析快捷支付接口返回信息时出现异常！", ex);
		}
		
		return returnMsg;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static String getInterfaceStatus(String backInfo) {
		if(null == backInfo) {
			return null;
		}
		
		String returnMsg = "";
		
		try {
			String encryptData =  ((Map<String, String>)JSON.parse(backInfo)).get("encryptData");
			return getStatusFromencryptData(encryptData);
		}catch(Exception ex) {
			logger.error("解析快捷支付接口返回信息时出现异常！", ex);
		}
		
		return returnMsg;
	}
	
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static String getRespCode(String backInfo) {
		if(null == backInfo) {
			return null;
		}
		
		String returnMsg = "";
		
		try {
			String encryptData =  ((Map<String, String>)JSON.parse(backInfo)).get("encryptData");
			String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0,16));
			JSONObject head = ((Map<String, JSONObject>)JSON.parse(msg)).get("head");
			returnMsg = (String)head.get("respCode");
		}catch(Exception ex) {
			logger.error("解析快捷支付接口返回信息时出现异常！", ex);
		}
		
		return returnMsg;
	}
}
