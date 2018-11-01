package com.goldgyro.platform.foreground.batchs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.goldgyro.platform.base.utils.DateTimeUtils;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.InterfaceLogPO;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.client.service.InterfaceLogService;
import com.goldgyro.platform.core.client.service.MerchantService;
import com.goldgyro.platform.core.client.service.PayCardService;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;
import com.goldgyro.platform.foreground.controller.PayCardController;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardProcess {
	@Autowired
	private Environment env;
	Logger logger = Logger.getLogger(PayCardController.class);
	/**
	 * VIP 中 P
	 */
//	private static String agentPhones = "13486756777,13980599839,13981965083,13730613093,15892831555,13880978061,18257737688";
	@SuppressWarnings({ "deprecation", "rawtypes" })
	@Transactional
	public String merchantRegist(String custId, CustomerService customerService, PayCardService payCardService,
			MerchantService merchantService, InterfaceLogService interfaceLogService) {
		Map<String, Object> merchantData = payCardService.findBusiRegistrations(custId);
		if (null == merchantData) {
			System.out.print("没有获取到需要注册的商户信息！");
			return null;
		}
		String orderId = UUIDUtils.getUUIDNum(24);
		merchantData.put("backURL", PayChannelConstants.ASYN_MERCHAT_REGIST_URL);
		merchantData.put("head", prepareHead(PayChannelConstants.MERCHANT_REGIST, orderId));

		InterfaceLogPO logPO = new InterfaceLogPO();
		logPO.setBusiType("商户注册");
		logPO.setCardNo((String) merchantData.get("bankAccountNo"));
		logPO.setCustId((String) merchantData.get("custId"));
		logPO.setCustName((String) merchantData.get("bankAccountName"));
		logPO.setSerialNo(orderId);
		logPO.setBusiId((String) merchantData.get("id"));
//		String phoneno = merchantData.get("phoneno").toString();
//		//根据给定手机号 判断是否是vip中p
//		if(agentPhones.indexOf(phoneno) > -1){
//			merchantData.put("txnRate", env.getProperty("goldgyro.agent"));
//		}
		String resp = BaseCtl.execute(PayChannelConstants.URL, PayChannelConstants.MERCHANT_REGIST,
				JSON.toJSONString(merchantData), null, PayChannelConstants.PARTNER_NO, orderId, interfaceLogService,
				logPO);

		String encryptData = (String) ((Map) JSON.parse(resp)).get("encryptData");
		String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0, 16));
		logger.info(msg);
		Object platMerchantCode = ((Map) JSON.parse(msg)).get("paltMerchantCode");
		this.logger.info("平台返回商户号：===========" + platMerchantCode);
		String status = "";
		if (StringUtils.isEmpty((String) platMerchantCode)) {
			status = "REG_FAIL";
			merchantService.udpate((String) merchantData.get("merchantCode"), null, "REG_FAIL");
		} else {
			status = "REG_SUCCESS";
			merchantService.udpate((String) merchantData.get("merchantCode"), (String) platMerchantCode, "REG_SUCCESS");
			Customer customer = customerService.findByCustId(custId);
			customer.setCustStatus("AUTH");
			customerService.save(customer);
		}
		merchantService.processReistStatus((String) merchantData.get("merchantCode"), platMerchantCode.toString(), status);
		return resp;
	}

	public Map<String, String> openCard(BankCard bCard, String orderId, PayCardService payCardService) {
		Map<String, Object> cardData = payCardService.findCreditCardBind(bCard.getCustId(), bCard.getAccountCode());
		if (null == cardData) {
			System.out.print("没有获取到需要开卡的信用卡信息！");
			return null;
		}
		cardData.put("openOrderId",orderId);
		cardData.put("pageReturnUrl", PayChannelConstants.ASYN_CARD_FORCE_URL+"?cardId="+bCard.getId());
		cardData.put("offlineNotifyUrl", PayChannelConstants.ASYN_CARD_BACK_URL+"?cardId="+bCard.getId());
		cardData.put("head", prepareHead(PayChannelConstants.CARD_REGIST, orderId));

		Map<String, String> cardInfoMap = BaseCtl.prepareData(JSON.toJSONString(cardData), orderId);

		return cardInfoMap;
	}

	public String bindCard(Map<String, Object> cardData, PayCardService payCardService,
			InterfaceLogService interfaceLogService) throws Exception {
		if (null == cardData) {
			System.out.print("没有获取到需要绑卡的信用卡信息！");
			return null;
		}
		String orderId = UUIDUtils.getUUIDNum();
		cardData.put("head", prepareHead(PayChannelConstants.BIND_CARD, orderId));

		InterfaceLogPO logPO = new InterfaceLogPO();
		logPO.setBusiType("绑卡");
		logPO.setCardNo((String) cardData.get("cardNo"));
		logPO.setCustId((String) cardData.get("custId"));
		logPO.setCustName((String) cardData.get("accountName"));
		logPO.setSerialNo(orderId);
		logPO.setBusiId((String) cardData.get("id"));
		logPO.setId(UUIDUtils.getUUID());

		String resp = BaseCtl.execute(PayChannelConstants.URL, PayChannelConstants.BIND_CARD,
				JSON.toJSONString(cardData), null, PayChannelConstants.PARTNER_NO, orderId, interfaceLogService, logPO);

		String status = BackInfoStatusUtils.getInterfaceStatus(resp);
		payCardService.updateCardBindStatus((String) cardData.get("id"),
				"SUCCESS".equalsIgnoreCase(status) ? "REG_SUCCESS" : "REG_FAIL");
		this.logger.info("绑卡返回信息====================================" + resp);

		return resp;
	}

	public String trans(Map<String, Object> params, String orderId, String api, InterfaceLogPO logPO,
			InterfaceLogService interfaceLogService) {
		params.put("head", prepareHead(api, orderId));

		return BaseCtl.execute(PayChannelConstants.URL, api, JSON.toJSONString(params), null,
				PayChannelConstants.PARTNER_NO, orderId, interfaceLogService, logPO);
	}

	public String queryQJInterface(Map<String, Object> paramsMap, String orderId, String api, InterfaceLogPO logPO,
			InterfaceLogService interfaceLogService) {
		paramsMap.put("head", prepareHead(api, orderId));

		return BaseCtl.execute(PayChannelConstants.URL, api, JSON.toJSONString(paramsMap), null,
				PayChannelConstants.PARTNER_NO, orderId, interfaceLogService, logPO);
	}

	private Map<String, Object> prepareHead(String txnCode, Object orderId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("version", "1.0.0");
		map.put("charset", "UTF-8");
		map.put("partnerNo", PayChannelConstants.PARTNER_NO);
		map.put("partnerType", "OUTER");
		map.put("txnCode", txnCode);
		map.put("orderId", orderId);
		map.put("reqDate", DateTimeUtils.formatDateTime(new Date(), "yyyyMMdd"));
		map.put("reqTime", DateTimeUtils.formatDateTime(new Date(), "yyyyMMddHHmmss"));

		return map;
	}
}
