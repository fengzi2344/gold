package com.goldgyro.platform.foreground.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.dao.CustomerDao;
import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.BankCardPO;
import com.goldgyro.platform.core.client.entity.BigPayment;
import com.goldgyro.platform.core.client.entity.MerchantPO;
import com.goldgyro.platform.core.client.entity.PaymentPlanPO;
import com.goldgyro.platform.core.client.service.BankCardService;
import com.goldgyro.platform.core.client.service.BigPaymentService;
import com.goldgyro.platform.core.client.service.InterfaceLogService;
import com.goldgyro.platform.core.client.service.MerchantService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.foreground.LogUtils;
import com.goldgyro.platform.foreground.batchs.AES;
import com.goldgyro.platform.foreground.batchs.Base64;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;
import com.goldgyro.platform.foreground.utils.Api;
import com.goldgyro.platform.foreground.utils.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/txWithdraw")
public class BigWithdrawContoller {
    private static final String NOTIFY_URL = "http://www.tuoluo718.com/txWithdraw/callback";
    @Autowired
    private BigPaymentService bigPaymentService;

    @Autowired
    private BankCardService bankCardService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private InterfaceLogService interfaceLogService;

    @PostMapping("/bigPaySms")
    public InterfaceResponseInfo withdraw(String custId, String cardId, String payAmount) {
        MerchantPO merchant = merchantService.findMerchantByCustId(custId);
        if (merchant == null || StringUtils.isEmpty(merchant.getPlatMerchantCode())) {
            return new InterfaceResponseInfo("0", "商户尚未实名认证", null);
        }
        BankCard bankCard = bankCardService.findBankCardById(cardId);
        if (bankCard == null || StringUtils.isEmpty(bankCard.getOpenCardId())) {
            return new InterfaceResponseInfo("0", "卡片暂未开通支付", null);
        }
        Map<String, Object> paramMap = initMap(merchant, bankCard);
        paramMap.put("payAmount", payAmount);
        Map<String, Object> headMap = RequestUtil.preHead(Api.BIGPAYSMS.get());
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        Map<String, Object> resMap = RequestUtil.execute(Api.BIGPAYSMS.get(), JSONObject.toJSONString(paramMap).trim(), null, headMap.get("orderId").toString());
        Object workId = null;
        if (resMap != null && (workId = resMap.get("workId")) != null) {
            Map<String, String> map = new HashMap<>();
            map.put("workId", workId.toString());
            paramMap.put("workId", workId);
            BigPayment bigPayment = instanceBigPayment(paramMap, custId);
            this.bigPaymentService.saveBigPayment(bigPayment);
            return new InterfaceResponseInfo(map);
        } else {
            return new InterfaceResponseInfo("0", "申请失败，请联系客服", null);
        }
    }

    @PostMapping("/bigPay")
    public InterfaceResponseInfo bigPay(String workId, String smsCode) {
        if (StringUtils.isEmpty(workId)) {
            return new InterfaceResponseInfo("0", "流水号不能为空", null);
        }
        if (StringUtils.isEmpty(smsCode)) {
            return new InterfaceResponseInfo("0", "短信验证码不能为空", null);
        }
        Map<String, Object> headMap = RequestUtil.preHead(Api.BIGPAY.get());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("workId", workId);
        paramMap.put("smsCode", smsCode);
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        Map<String, Object> resMap = RequestUtil.execute(Api.BIGPAY.get(), JSONObject.toJSONString(paramMap).trim(), null, headMap.get("orderId").toString());
        if (resMap != null) {
            return new InterfaceResponseInfo("1", "申请成功", null);
        } else {
            return new InterfaceResponseInfo("0", "申请失败，请联系客服", null);
        }
    }
    @RequestMapping("/callback")
    public String callBack(String encryptData) {
        LogUtils.writeStringToFile(encryptData);
        String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0, 16));
        Map<String, Object> map = ((Map<String, Object>) JSON.parse(msg));
        String orderStatus = map.get("orderStatus").toString();
        String workId = map.get("workId").toString();
        bigPaymentService.process(workId, orderStatus);
        return "000000";
    }
    @GetMapping("/just")
    public String test(String workId, String orderStatus){
        bigPaymentService.process(workId,orderStatus);
        return "success";
    }
    private BigPayment instanceBigPayment(Map<String, Object> paramMap, String custId) {
        BigPayment payment = new BigPayment();
        payment.setConsumeOrderId(paramMap.get("consumeOrderId").toString());
        payment.setCreateTime(new Timestamp(new Date().getTime()));
        payment.setCustId(custId);
        payment.setStatus("PROCESSING");
        payment.setRemark(paramMap.get("remark").toString());
        payment.setWorkId(paramMap.get("workId").toString());
        payment.setPayAmount(paramMap.get("payAmount").toString());
        return payment;
    }

    private Map<String, Object> initMap(MerchantPO merchant, BankCard bankCard) {
        Map<String, Object> map = new HashMap<>();
        map.put("platMerchantCode", merchant.getPlatMerchantCode());
        map.put("consumeOrderId", UUIDUtils.getUUIDNum(20));
        map.put("openCardId", bankCard.getOpenCardId());
        map.put("remark", "withdraw");
        map.put("notifyUrl", NOTIFY_URL);
        map.put("productCode", "QUICKPAY_OF_NP");
        return map;
    }
}
