package com.goldgyro.platform.foreground.batchs;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.MerchantPO;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.client.service.MerchantService;
import com.goldgyro.platform.core.client.service.PayCardService;
import com.xiaoleilu.hutool.http.HttpUtil;
import com.xiaoleilu.hutool.util.ReUtil;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goldgyro.platform.base.utils.DateTimeUtils;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.entity.InterfaceLogPO;
import com.goldgyro.platform.core.client.service.InterfaceLogService;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;
import org.jsoup.Connection;

/**
 * Created by jiangzeng on 2017/6/19.
 */

public class BaseCtl {
    /**
     *  数据准备
     * @param dataJson
     * @return
     */
    protected static Map<String, String> prepareData(String dataJson, String orderId) {
		System.out.print("====================调用外部接口：参数================\n"+dataJson);
        String signKey = PayChannelConstants.KEY.substring(16);
        String dataKey = PayChannelConstants.KEY.substring(0,16);
        String sign = DigestUtils.sha1Hex(dataJson + signKey);

        Map<String, String> params = new HashMap<String, String>();
        params.put("encryptData", Base64.encode(AES.encode(dataJson, dataKey)));
        params.put("signData", sign);
        params.put("orderId",orderId);
        params.put("partnerNo", PayChannelConstants.PARTNER_NO);
        params.put("ext","to be or not to be");
        
        return params;
    }
    public static String execute(String url, String api,String dataJson,String partnerNo,String orderId){
        String signKey = PayChannelConstants.KEY.substring(16);
        String dataKey = PayChannelConstants.KEY.substring(0,16);
        String sign = DigestUtils.sha1Hex(dataJson+signKey);
        Map<String, String> params = new HashMap<String, String>();
        params.put("encryptData",Base64.encode(AES.encode(dataJson,dataKey)));
        params.put("signData",sign);
        params.put("orderId",orderId);
        params.put("partnerNo",partnerNo);
        params.put("ext","fuek");
        String resStr = null;
        byte[] restByte = HttpClient4Util.getInstance().doPost(url+api,null,params);
        try {
            resStr = new String(restByte,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return resStr;
    }
    protected static String execute(String url, String api, String dataJson, Map<String, String> hander, String partnerNo, String orderId,
    		InterfaceLogService logService, InterfaceLogPO interfaceLogPO) {
        System.out.print("============ 数据:" + dataJson);
        String signKey = PayChannelConstants.KEY.substring(16);
        String dataKey = PayChannelConstants.KEY.substring(0,16);
        String sign = DigestUtils.sha1Hex(dataJson + signKey);

        Map<String, String> params = new HashMap<String, String>();
        //params.put("head", JSONObject.toJSONString(hander));
        params.put("encryptData", Base64.encode(AES.encode(dataJson, dataKey)));
        params.put("signData", sign);
        params.put("orderId",orderId);
        params.put("partnerNo", partnerNo);
        params.put("ext","to be or not to be");
        String resStr = "";
        try {
            byte[] resByte = HttpClient4Util.getInstance().doPost(url + api, hander, params);//Json(url + api, JSONObject.toJSONString(params));
            resStr = new String(resByte, "UTF-8");
            return resStr;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	if(null != logService && null != interfaceLogPO) {
            	interfaceLogPO.setInputParams(JSON.toJSONString(params));
            	interfaceLogPO.setOutParams(resStr);
            	if(null == interfaceLogPO.getId() || "".equals(interfaceLogPO.getId())) {
            		interfaceLogPO.setId(UUIDUtils.getUUIDNum(24));
            	}
            	if(PayChannelConstants.WITHDRAWAL.equals(api)){
                    logService.writeLogAndUpdateWithraw(interfaceLogPO,orderId);
                }else {
                    logService.writeLog(interfaceLogPO);
                }
            }
        }
        return null;
    }
    public static String execute(String key, String url, String api, String dataJson, Map<String, String> hander,
                                 String partnerNo, String orderId) {
        key = PayChannelConstants.KEY;
        String signKey = key.substring(16);
        String dataKey = key.substring(0, 16);
        String sign = DigestUtils.sha1Hex(dataJson + signKey);

        Map<String, String> params = new HashMap<String, String>();
        params.put("encryptData", Base64.encode(AES.encode(dataJson, dataKey)));
        params.put("signData", sign);
        params.put("orderId", orderId);
        params.put("partnerNo", partnerNo);
        params.put("ext", "to be or not to be");

        try {
            byte[] resByte = HttpClient4Util.getInstance().doPost(url + api, hander, params);
            String resStr = new String(resByte, "UTF-8");
            return resStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Object getHead(String id,String partnerNo,String txnCode) {
        JSONObject head = new JSONObject();
        head.put("version", "1.0.0");
        head.put("charset", "UTF-8");
        head.put("partnerNo", partnerNo);
        head.put("txnCode", txnCode);
        head.put("orderId", id);
        head.put("reqDate", DateTimeUtils.formatDateTime(new Date(), "yyyyMMdd"));
        head.put("reqTime", DateTimeUtils.formatDateTime(new Date(), "yyyyMMddHHmmss"));
        return head;
    }
    public static Map<String, Object> initHeadParam(String api) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("version", "1.0.0");// 版本
        map.put("charset", "UTF-8");// 字符集
        map.put("partnerNo", "A700003");// 合作方标识号
        map.put("partnerType", "OUTER");// 合作方类型
        map.put("txnCode", api);// 交易代码
        map.put("orderId", "921114" + getDate("yyyyMMddHHmmss"));// 交易订单号
        map.put("reqDate", getDate("yyyyMMdd"));// 请求日期
        map.put("reqTime", getDate("yyyyMMddHHmmss"));// 请求时间
        return map;
    }
    public static String getDate(String str) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(str);
        String dateNowStr = sdf.format(d);
        return dateNowStr;
    }
    /**
     *
     * @param resStr
     * @return
     */
    public static Map<String,Object> getReturnMap(String resStr) {
        Map<String, Object> map = ((Map<String, Object>) JSON.parse(resStr));
        String encryptData = map.get("encryptData").toString();
        String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0, 16));
        Map<String, Object> finalMap = ((Map<String, Object>) JSON.parse(msg));
        return finalMap;
    }
    public static Map<String,Object> getHeadMap(String head){
        return ((Map<String, Object>) JSON.parse(head));
    }

    /**
     * 根据银行卡号获取银行名称
     * @param bankNo
     * @return 这里暂时不用
     */
    private static String queryBankNo(String bankNo){
        //银行代码请求接口 url
        String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo="+bankNo+"&cardBinCheck=true";
        //发送请求，得到 josn 类型的字符串
        String result = HttpUtil.get(url);
        // 转为 Json 对象
        com.xiaoleilu.hutool.json.JSONObject json = new com.xiaoleilu.hutool.json.JSONObject(result);
        //获取到 bank 代码
        String bank = String.valueOf(json.get("bank"));
        //爬取支付宝银行合作商页面
        String listContent = HttpUtil.get("http://ab.alipay.com/i/yinhang.htm","gb2312");
        //过滤得到需要的银行名称
        List<String> titles = ReUtil.findAll("<span title=\"(.*?)\" class=\"icon "+bank+"\">(.*?)</span>", listContent, 2);
        if(titles.size()>0) {
            return titles.get(0);
        }else {
            return null;
        }
    }
    public static String queryBankCode(String cardNo){
        String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo="+cardNo+"&cardBinCheck=true";
        //发送请求，得到 josn 类型的字符串
        String result = HttpUtil.get(url);
        // 转为 Json 对象
        com.xiaoleilu.hutool.json.JSONObject json = new com.xiaoleilu.hutool.json.JSONObject(result);
        //获取到 bank 代码
        String bank = String.valueOf(json.get("bank"));
        return bank;
    }

    public static boolean bindCardProcess(BankCard bCard, MerchantService mService, CustomerService cService, PayCardService pService, InterfaceLogService iService) {
        MerchantPO merchant =mService.findMerchantByCustId(bCard.getCustId());
        if(merchant == null || org.apache.commons.lang3.StringUtils.isEmpty(merchant.getPlatMerchantCode())){
            return false;
        }
        Customer customer = cService.findByCustId(bCard.getCustId());
        if(customer == null || org.apache.commons.lang3.StringUtils.isEmpty(customer.getIdCardNo())){
            return false;
        }
        Map<String,Object> cardMap = new HashMap<>();
        cardMap.put("id",bCard.getId());
        cardMap.put("platMerchantCode",merchant.getPlatMerchantCode());
        cardMap.put("accountName",bCard.getAccountName());
        cardMap.put("cardType",bCard.getBankCardType());
        cardMap.put("cardNo",bCard.getAccountCode());
        cardMap.put("certType","ID");
        cardMap.put("certNo",customer.getIdCardNo());
        cardMap.put("phoneno",bCard.getMobileNo());
        cardMap.put("custId",bCard.getCustId());
        cardMap.put("bankProvince",bCard.getOpenningBankProvince());
        cardMap.put("bankCity",bCard.getOpenningBankCity());
        cardMap.put("bankSubName",bCard.getOpeningSubBankName());
        cardMap.put("bankName",bCard.getBankName());
        try {
            String resp = new CardProcess().bindCard(cardMap, pService, iService);
            String status = BackInfoStatusUtils.getInterfaceStatus(resp);
            return "SUCCESS".equalsIgnoreCase(status);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        String encryptData = "zM4H2W1kYvmw9i5IrM6Yf81FRA3pCDE33uTgxKABBz8MbRARqtqkfYOJcM/FlvAn7YuwYHfuGbFFa21cqhoxQatS56U9NAQ7X2RdistFSJOPsWb2XF4dlqzl7G0JN06/c2HKLQUAcAUg9E2nmvvkVNalcPD2L7h3HmCHFEjCCCnGhfVnoyGa2kdi/psdZ2lAOdTB/LSRa7AGxrvoeMObyLA07dsX8orxq6tdPPDKkqOrsSzLdMPq7uk7Lq+YjcQL3hCeif01Oyk+ywfMkZs1nazXFlHW5JYt0APPRCRyk5Vt3RIohCSInbwteH2GYl/NQ2dScBLHu5kPIhhAC/Hiq70LWRIjMLGsG/Jnet+mBx4IZ7TYAQ7h/VoagihtkVhB7lsnDBKwcP3vVWv30xGHFdxttqxhm+K2FsokMtG5D8s=";
        String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0, 16));
        Map<String, Object> finalMap = ((Map<String, Object>) JSON.parse(msg));
        String head = finalMap.get("head").toString();
        Map<String, Object> map = getHeadMap(head);
        for(Map.Entry<String,Object> entry:map.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }
}
