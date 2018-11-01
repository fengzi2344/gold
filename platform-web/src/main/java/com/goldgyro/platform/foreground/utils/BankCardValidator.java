package com.goldgyro.platform.foreground.utils;

import com.alibaba.fastjson.JSON;
import com.goldgyro.platform.core.client.domain.BankCard;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class BankCardValidator {
    private static final String host = "https://bcard3and4.market.alicloudapi.com";
    private static final String path = "/bankCheck4";
    private static final String method = "GET";
    public static Map<String,String> check4Elements(BankCard bankCard,String appcode,String idCard){
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("accountNo", bankCard.getAccountCode());
        querys.put("idCard", idCard);
        querys.put("mobile", bankCard.getMobileNo());
        querys.put("name", bankCard.getAccountName());

        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            //System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
            //状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
            //获取response的body
            System.out.println();
            Map<String, String> map = (Map<String, String>) JSON.parse(EntityUtils.toString(response.getEntity()));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE 0e78c491874740efa843baf17d660a7f");
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("accountNo", "6221532320011921835");
        querys.put("idCard", "510107199002045423");
        querys.put("mobile", "13551843354");
        querys.put("name", "王静");

        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            //System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
            //状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
            //获取response的body
            System.out.println();
            Map<String, String> map = (Map<String, String>) JSON.parse(EntityUtils.toString(response.getEntity()));
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
