package com.goldgyro.platform.foreground.controller;

import com.alibaba.fastjson.JSONObject;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.entity.BankCardPO;
import com.goldgyro.platform.core.client.entity.InterfaceLogPO;
import com.goldgyro.platform.core.client.entity.MerchantPO;
import com.goldgyro.platform.core.client.service.BankCardService;
import com.goldgyro.platform.core.client.service.InterfaceLogService;
import com.goldgyro.platform.core.client.service.MerchantService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.foreground.batchs.BaseCtl;
import com.goldgyro.platform.foreground.utils.Api;
import com.goldgyro.platform.foreground.utils.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/bk")
public class BankCardContoller {
    @Autowired
    private BankCardService bankCardService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private InterfaceLogService interfaceLogService;

    @RequestMapping("/openCardSms")
    public InterfaceResponseInfo openCard(String cardId, String cvn2, String expired) {
        BankCard bankCard = bankCardService.findBankCardById(cardId);
        if (bankCard == null) {
            return new InterfaceResponseInfo("0", "参数错误", null);
        }
        MerchantPO merchant = merchantService.findMerchantByCustId(bankCard.getCustId());
        if (merchant == null || StringUtils.isEmpty(merchant.getPlatMerchantCode())) {
            return new InterfaceResponseInfo("0", "您尚未实名认证", null);
        }
        Map<String, Object> paramMap = bankCardService.queryParams(cardId);
        if (paramMap == null) {
            return new InterfaceResponseInfo("0", "请求失败", null);
        }
        paramMap.put("cvn2", cvn2);
        paramMap.put("expired", expired);
        String openOrderId = UUIDUtils.getUUIDNum(20);
        paramMap.put("openOrderId", openOrderId);
        Map<String, Object> headMap = RequestUtil.preHead(Api.OPENCARDSMS.get());
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        InterfaceLogPO interfaceLogPO = new InterfaceLogPO();
        interfaceLogPO.setBusiId(bankCard.getId());
        interfaceLogPO.setTableName("T_BANK_CARD");
        interfaceLogPO.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        interfaceLogPO.setCreatedTime(new Timestamp(new Date().getTime()));
        interfaceLogPO.setBusiType("银联认证短信");
        Map<String, Object> resMap = RequestUtil.executeAndSaveLog(Api.OPENCARDSMS.get(), JSONObject.toJSONString(paramMap).trim(), null, headMap.get("orderId").toString(),interfaceLogService,interfaceLogPO);
        Map<String, Object> headMap1 = BaseCtl.getHeadMap(resMap.get("head").toString());
        if (headMap1 != null && headMap1.get("respMsg") != null && "success".equals(headMap1.get("respMsg").toString())) {
            Map<String, String> map = new HashMap<>();
            map.put("openOrderId", openOrderId);
            bankCard.setCvn2(cvn2);
            bankCard.setExpired(expired);
            bankCard.setOpenOrderId(openOrderId);
            bankCardService.save(bankCard,false);
            return new InterfaceResponseInfo("1", "请求成功", map);
        }
        return new InterfaceResponseInfo("0", "请求失败", null);
    }

    @RequestMapping("/openCard")
    public InterfaceResponseInfo openCard(String smsCode, String openOrderId) {
        BankCardPO bankCardPO = bankCardService.findByOpenOrderId(openOrderId);
        Map<String, Object> headMap = RequestUtil.preHead(Api.OPENCARD.get());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("smsCode", smsCode);
        paramMap.put("openOrderId", openOrderId);
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        InterfaceLogPO interfaceLogPO = new InterfaceLogPO();
        interfaceLogPO.setBusiId(bankCardPO.getId());
        interfaceLogPO.setTableName("T_BANK_CARD");
        interfaceLogPO.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        interfaceLogPO.setCreatedTime(new Timestamp(new Date().getTime()));
        interfaceLogPO.setBusiType("银联认证");
        Map<String, Object> resMap = RequestUtil.executeAndSaveLog(Api.OPENCARD.get(), JSONObject.toJSONString(paramMap).trim(), null, headMap.get("orderId").toString(),interfaceLogService,interfaceLogPO);
        Object openCardId = null;
        if(resMap != null ){
            if((openCardId = resMap.get("openCardId"))!= null){
                bankCardPO.setOpenCardId(openCardId.toString());
                bankCardPO.setBindStatus("REG_SUCCESS");
                bankCardPO.setOpenStatus("OPEN_SUCCESS");
                bankCardService.saveBankCard(bankCardPO);
                return new InterfaceResponseInfo("1","开卡成功",null);
            }else {
                Map<String, Object> resHeadMap = BaseCtl.getHeadMap(resMap.get("head").toString());
                if(resHeadMap!=null && resHeadMap.get("respMsg") != null){
                    String respMsg = resHeadMap.get("respMsg").toString();
                    return new InterfaceResponseInfo("0","开卡失败:"+respMsg,null);
                }
            }
        }
        return new InterfaceResponseInfo("0","开卡失败，请联系客服，谢谢！",null);
    }
    @GetMapping("/balance/{platMerchantCode}")
    public Map<String, Object> balance(@PathVariable String platMerchantCode){
        Map<String, Object> headMap = RequestUtil.preHead(Api.QUERYBALANCE.get());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("platMerchantCode", platMerchantCode);
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        Map<String, Object> resMap = RequestUtil.execute(Api.QUERYBALANCE.get(), JSONObject.toJSONString(paramMap).trim(), null, headMap.get("orderId").toString());
        return resMap;
    }
}
