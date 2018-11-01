package com.goldgyro.platform.foreground.utils;

import com.alibaba.fastjson.JSONObject;
import com.goldgyro.platform.core.client.entity.InterfaceLogPO;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.client.service.InterfaceLogService;
import com.goldgyro.platform.foreground.LogUtils;
import com.goldgyro.platform.foreground.batchs.AES;
import com.goldgyro.platform.foreground.batchs.Base64;
import com.goldgyro.platform.foreground.batchs.BaseCtl;
import com.goldgyro.platform.foreground.batchs.HttpClient4Util;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class RequestUtil {
    //    private static final String KEY = "00000000000000000000000000000000";
    private static final String KEY = "RssTuILIVmlhC31i79J4AO6LlKVm2dad";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    //  private static final String PARTNER_NO = "0000TLSL";
    private static final String PARTNER_NO = "QI1ScH3M";
    private static final String URL = "http://fast.jfpays.com:19085/rest/api/";
    private static final String VERSION = "1.0.0";
    public static final String REGISTRY = "610001";

    public static Map<String, Object> execute(String api, String dataJson, Map<String, String> hander, String orderId) {
        String signKey = KEY.substring(16);
        String dataKey = KEY.substring(0, 16);
        String sign = DigestUtils.sha1Hex(dataJson + signKey);

        Map<String, String> params = new HashMap<String, String>();
        params.put("encryptData", Base64.encode(AES.encode(dataJson, dataKey)));
        params.put("signData", sign);
        params.put("orderId", orderId);
        params.put("partnerNo", PARTNER_NO);
        try {
            LogUtils.writeStringToFile(JSONObject.toJSONString(params));
            byte[] resByte = HttpClient4Util.getInstance().doPost(URL + api, hander, params);
            String resStr = new String(resByte, UTF_8);
            LogUtils.writeStringToFile(resStr);
            return BaseCtl.getReturnMap(resStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Object> executeAndSaveLog(String api, String dataJson, Map<String, String> handler, String orderId, InterfaceLogService logService, InterfaceLogPO interfaceLogPO) {
        String signKey = KEY.substring(16);
        String dataKey = KEY.substring(0, 16);
        String sign = DigestUtils.sha1Hex(dataJson + signKey);
        Map<String, Object> resMap = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("encryptData", Base64.encode(AES.encode(dataJson, dataKey)));
        params.put("signData", sign);
        params.put("orderId", orderId);
        params.put("partnerNo", PARTNER_NO);
        try {
            interfaceLogPO.setInputParams(JSONObject.toJSONString(params));
            byte[] resByte = HttpClient4Util.getInstance().doPost(URL + api, handler, params);
            String resStr = new String(resByte, UTF_8);
            interfaceLogPO.setOutParams(resStr);
            resMap = BaseCtl.getReturnMap(resStr);
        } catch (Exception e) {
            resMap = null;
            e.printStackTrace();
        }finally {
            logService.writeLog(interfaceLogPO);
        }
        return resMap;
    }

    /**
     * 初始化头部
     *
     * @param txnCode
     * @return
     */
    public static Map<String, Object> preHead(String txnCode) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        DateFormat tf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        Map<String, Object> head = new HashMap<String, Object>();
        head.put("version", VERSION);
        head.put("charset", UTF_8);
        head.put("partnerNo", PARTNER_NO);
        head.put("txnCode", txnCode);
        head.put("orderId", tf.format(now) + new Date().getTime());
        head.put("reqDate", df.format(now));
        head.put("reqTime", tf.format(now));
        return head;
    }

    public static List<Map<String, String>> initProduct(String... ratios) {
        List<Map<String, String>> productList = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("QUICKPAY_OF_NP", ratios[0]);
        productList.add(map);
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("QUICKPAY_NOSMS", ratios[1]);
        productList.add(map1);
        return productList;
    }

    public static Map<String, Object> queryStatus(String transNo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("consumeOrderId", transNo);
        Map<String, Object> headMap = preHead(Api.QUERYSTATUS.get());
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        Map<String, Object> resMap = execute(Api.QUERYSTATUS.get(), JSONObject.toJSONString(paramMap).trim(), null, headMap.get("orderId").toString());
        return resMap;
    }

    public static String[] initRates(String vipRate) {
        String[] rates = new String[2];
        rates[1] = vipRate;
        if ("0.0068".equals(vipRate)) {
            rates[0] = "0.006";
        } else if ("0.0063".equals(vipRate)) {
            rates[0] = "0.0058";
        } else {
            rates[0] = "0.0055";
        }
        return rates;
    }
    /**
     * 更新用户费率
     * @param vipRate
     * @param custId
     * @return
     */
    public static boolean updateFee(CustomerService customerService,String vipRate, String custId) {
        Map<String,Object> headMap = RequestUtil.preHead(Api.UPDATEFEE.get());
        Map<String,Object> paramMap = customerService.queryParamMap(custId);
        String[] rates1 = RequestUtil.initRates(vipRate);
        paramMap.put("productList",RequestUtil.initProduct(rates1));
        paramMap.put("withdrawDepositSingleFee",PayChannelConstants.WITHDRAW_DEPOSITSINGLE_FEE);
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        Map<String, Object> resMap = RequestUtil.execute(Api.UPDATEFEE.get(), JSONObject.toJSONString(paramMap).trim(), null, headMap.get("orderId").toString());
        Object head = null;
        if(resMap != null && (head=resMap.get("head")) != null){
            Map<String, Object> resHeadMap = BaseCtl.getHeadMap(head.toString());
            if(resHeadMap != null && resHeadMap.get("respMsg") != null && "success".equalsIgnoreCase(resHeadMap.get("respMsg").toString())){
                return true;
            }
        }
        LogUtils.writeStringToFile(resMap.get("encryptData").toString());
        return false;
    }
}
