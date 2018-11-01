package com.goldgyro.platform.core.comm.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * 短信处理帮助类
 *
 * @author wg2993
 * @version 2018/07/11
 */
public class SMSUtils {
    private static final String TARGET_URL = "http://api.feige.ee/SmsService/Template";
    private static final String ENCODE = "UTF-8";
    private static final String REGIST_TEMPLATE_ID = "35192";
    private static final String WITHDRAW_TEMPLATE_ID = "38891";
    private static final String SECURITY_CODE_TEMPLATE_ID = "34360";
    private static final String NOTIFY_LEE_TEMPLATE_ID = "38954";
    private static final String LEE_MOBILE = "13981965083";


    public static void post(String mobileNo, String content,String templateId) {
        try {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("Account", "15002311788"));
            formparams.add(new BasicNameValuePair("Pwd", "020b57a8f789a892ac41ecbbd"));
            formparams.add(new BasicNameValuePair("TemplateId", templateId));
            formparams.add(new BasicNameValuePair("SignId", "45275"));
            formparams.add(new BasicNameValuePair("Content", content));
            formparams.add(new BasicNameValuePair("Mobile", mobileNo));

            SendInfoUtils.post(formparams, TARGET_URL, ENCODE, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendRegist(String mobileNo, String content){
        post(mobileNo,content,REGIST_TEMPLATE_ID);
    }

    public static void sendSecurityCode(String mobileNo, String content){
        post(mobileNo,content,SECURITY_CODE_TEMPLATE_ID);
    }

    public static void sendWithdraw1(String mobileNo, String content){
        post(mobileNo,content,WITHDRAW_TEMPLATE_ID);
    }
    public static void sendWithdraw2( String content){
        post(LEE_MOBILE,content,NOTIFY_LEE_TEMPLATE_ID);
    }

}
