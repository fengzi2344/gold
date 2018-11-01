package com.goldgyro.platform.foreground.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 错误信息
 * @author wg2993
 * @version 2018/07/09
 */
public class MessageConstant {
	private static Map<String,String> messageConstantMap = null;
	static {
		
	}
	
	/**
	 * 获取处理状态信息
	 * @param code
	 * @return
	 */
	public static String getMessage(String code) {
		if(null == messageConstantMap) {
			messageConstantMap = new HashMap<String, String>();
			
			//登录
			messageConstantMap.put(CUST_ERROR_CODE_001, "请填写登录名和密码！");
			messageConstantMap.put(CUST_ERROR_CODE_002, "输入的登录名或密码错误！");
			messageConstantMap.put(CUST_ERROR_CODE_003, "账户不存在（登录名或密码错误）！");
			messageConstantMap.put(CUST_ERROR_CODE_004, "账户被禁用！");
			
			//注册
			messageConstantMap.put(CUST_ERROR_CODE_010, "客户信息不能为空！");
			messageConstantMap.put(CUST_ERROR_CODE_011, "此手机号已注册！");
			messageConstantMap.put(CUST_ERROR_CODE_012, "存储客户信息失败！");
			
			//改密码
			messageConstantMap.put(CUST_ERROR_CODE_021, "手机号码和密码不能为空！");
			messageConstantMap.put(CUST_ERROR_CODE_022, "验证码不正确！");
			
			//用户认证
			messageConstantMap.put(CUST_ERROR_CODE_030, "用户认证信息保存失败！");
			messageConstantMap.put(CUST_INFO_CODE_030, "没有添加银行卡信息！"); 
			messageConstantMap.put(CUST_ERROR_CODE_040, "性别值只能是M(男)或G(女)！");
			messageConstantMap.put(CUST_ERROR_CODE_041, "身份证号不正确！"); 
			
			//添加银行卡
			messageConstantMap.put(BCARD_ERROR_CODE_001, "此银行卡已注册，不能重复注册！");
			messageConstantMap.put(BCARD_ERROR_CODE_002, "此银行卡未注册，不能更新或删除！"); 
			messageConstantMap.put(BCARD_ERROR_CODE_003, "绑定银行卡时发生异常！"); 
			messageConstantMap.put(BCARD_ERROR_CODE_010, "银行卡号不能为空！");
			
			//验证码
			messageConstantMap.put(VALIDCODE_ERROR_CODE_001, "操作失败，请检查手机号是否正确！"); 
			
			
			messageConstantMap.put(OK, "操作成功！");
			messageConstantMap.put(FAIL, "操作失败！");
		}
		return messageConstantMap.get(code);
	}
	
	
	//系统
	public static String SYS_BOOT_001 = "SYS_BOOT_001"; //系统启动过程中加载配置出现异常
	
	
	
	/**
	 * 客户信息相关错误信息
	 */
	public static String CUST_ERROR_CODE_001 = "CUST_ERROR_CODE_001";//登录时用户名密码不能为空
	public static String CUST_ERROR_CODE_002 = "CUST_ERROR_CODE_002";//登录时用户名或密码错误
	public static String CUST_ERROR_CODE_003 = "CUST_ERROR_CODE_003";//用户不存在
	public static String CUST_ERROR_CODE_004 = "CUST_ERROR_CODE_004";//用户被禁用
	
	public static String CUST_ERROR_CODE_010 = "CUST_ERROR_CODE_010";//客户信息为空
	public static String CUST_ERROR_CODE_011 = "CUST_ERROR_CODE_011";//手机号已注册
	public static String CUST_ERROR_CODE_012 = "CUST_ERROR_CODE_012";//存储客户信息失败
	
	public static String CUST_ERROR_CODE_021 = "CUST_ERROR_CODE_021";//手机号码和密码不能为空
	public static String CUST_ERROR_CODE_022 = "CUST_ERROR_CODE_022";//验证码不正确
	
	public static String CUST_ERROR_CODE_030 = "CUST_ERROR_CODE_030";//用户认证信息保存失败！
	
	//验证
	public static String CUST_ERROR_CODE_040 = "CUST_ERROR_CODE_040";//性别值只能是M(男)或G(女)！
	public static String CUST_ERROR_CODE_041 = "CUST_ERROR_CODE_041";//身份证号不正确
	
	
	//提示信息
	public static String CUST_INFO_CODE_030 = "CUST_INFO_CODE_030";//没有添加银行卡信息！
	
	
	//银行卡信息处理相关错误信息
	public static String BCARD_ERROR_CODE_001 = "BCARD_ERROR_CODE_001";//此银行卡已注册，不能重复注册
	public static String BCARD_ERROR_CODE_002 = "BCARD_ERROR_CODE_002";//此银行卡未注册，不能更新或删除
	public static String BCARD_ERROR_CODE_003 = "BCARD_ERROR_CODE_003";//绑定银行卡时发生异常
	public static String BCARD_ERROR_CODE_010 = "BCARD_ERROR_CODE_010";//银行卡号不能为空
	
	//验证码
	public static String VALIDCODE_ERROR_CODE_001 = "VALIDCODE_ERROR_CODE_001";//申请发送验证码出现异常
	
	
	public static String OK = "1"; //成功
	public static String FAIL = "0"; //成功
	
	
	
	
}
