package com.goldgyro.platform.foreground.constants;

public class PayChannelConstants {
	public static String PARTNER_NO = "QI1ScH3M";
	public static String KEY = "RssTuILIVmlhC31i79J4AO6LlKVm2dad";
	public static String URL = "http://fast.jfpays.com:19085/rest/api/";
	public static String ENCODE = "UTF-8";
	
	public static String MERCHANT_REGIST = "710001";	//注册商户
	public static String CARD_REGIST = "710002";	//开卡
	public static String BIND_CARD = "710008";	//绑卡
	public static String PAYMENT = "710003";	//还款
	public static String WITHDRAWAL = "710004";  //商户提现
	public static String PAY_STATUS_QUERY = "710005";//支付状态查询
	public static String WITHDRAW_STATUS_QUERY = "710006";//提现状态查询
	public static String MERCHANT_REGIST_QUERY = "710007"; //商户注册状态查询
	public static String MERCHANT_BALANCE_QUERY = "710011";//商户余额查询
	public static String WITHDRAW_DEPOSITSINGLE_FEE = "100";//单笔提现手续费
	public static String ALTER_FEE_RATIO = "710009";

	
	/**
	 * 还款相关设置
	 */
	//每天最大交易次数
	public static int DAILY_MAX_TRANS_NUM = 2;
	//最小交易额限制
	public static double MIN_TRANS_AMT = 100;
    //最大交易额限制
    public static double MAX_TRANS_AMT = 1000;
	//每笔手续费
	public static double SERVICE_CHARGE = 1.0;
	//还款交易开始时间
	public static String TRANS_START_TIME = "09:00";
	//还款结束时间
	public static String TRANS_END_TIME = "22:00";
	//标准交易间隔时间
	public static int MIN_TIME_INTERVAL = 120;
	//标准交易间隔时间
	public static int MAX_TIME_INTERVAL = 259;
	//还款时间偏差
	public static int PAYMENT_TIME_OFFSET = -20;
	
	
	//商户注册回调地址
	public static String ASYN_MERCHAT_REGIST_URL="http://www.tuoluo718.com/payment/ayncMerchantRegist";
	
	//开卡前后台回调地址
	public static String ASYN_CARD_FORCE_URL = "http://www.tuoluo718.com/open/forceBack";
	//开卡后台回调地址
	public static String ASYN_CARD_BACK_URL = "http://www.tuoluo718.com/open/backs";
	//还款回调地址
	public static String ASYN_PAYMENT_STATUS_BACK_URL = "/payment/ayncPayStatus";
}
