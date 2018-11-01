package com.goldgyro.platform.foreground.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.domain.InterfaceLog;
import com.goldgyro.platform.core.client.entity.InterfaceLogPO;
import com.goldgyro.platform.core.client.service.BankCardService;
import com.goldgyro.platform.core.client.service.InterfaceLogService;
import com.goldgyro.platform.core.client.service.PayCardService;
import com.goldgyro.platform.foreground.batchs.AES;
import com.goldgyro.platform.foreground.batchs.Base64;
import com.goldgyro.platform.foreground.batchs.CardProcess;
import com.goldgyro.platform.foreground.constants.MessageConstant;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OpenCardController {
	Logger logger = Logger.getLogger(PayCardController.class);

	@Autowired
	private BankCardService bCardService;
	@Autowired
	private PayCardService payCardService;
	@Autowired
	private InterfaceLogService logService;

	/**
	 * 开户、绑卡、开卡
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/open/card")
	public String openCard(HttpServletRequest request) {
		String custId = (String) request.getAttribute("custId");
		String accountNo = (String) request.getAttribute("accountNo");
		BankCard bCard = bCardService.findBankCardByCustIdAndAccountCode(custId, accountNo);
		if (null == bCard) {
			logger.error("【开卡】指定的卡号不正确！");
		}
		try {
			String orderId = UUIDUtils.getUUIDNum(24);
			Map<String, String> paramsMap = new CardProcess().openCard(bCard, orderId, payCardService);
			if (null != paramsMap) {

				logger.info("encryptData=========" + paramsMap.get("encryptData"));
				logger.info("signData=========" + paramsMap.get("signData"));
				logger.info("orderId=========" + paramsMap.get("orderId"));
				logger.info("partnerNo=========" + paramsMap.get("partnerNo"));

				InterfaceLogPO interfaceLogPO = new InterfaceLogPO();
				interfaceLogPO.setBusiType("开卡");
				interfaceLogPO.setInputParams(JSON.toJSONString(paramsMap.get("encryptData")));
				if (null == interfaceLogPO.getId() || "".equals(interfaceLogPO.getId())) {
					interfaceLogPO.setId(UUIDUtils.getUUIDNum(24));
				}
				interfaceLogPO.setBusiId(bCard.getId());
				interfaceLogPO.setSerialNo(orderId);
				logService.writeLog(interfaceLogPO);

				request.setAttribute("encryptData", paramsMap.get("encryptData"));
				request.setAttribute("signData", paramsMap.get("signData"));
				request.setAttribute("orderId", orderId);
				request.setAttribute("partnerNo", paramsMap.get("partnerNo"));
			}
		} catch (Exception ex) {
			logger.error(MessageConstant.BCARD_ERROR_CODE_003, ex);
		}

		return "/goldgyro/openCard";
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@RequestMapping(value = "/open/forceBack")
	public String openCardBackForce(String errorCode,String cardId) {

		System.out.println("========开卡前台通知==========");
		System.out.println(errorCode);
		if("SUCCESS".equals(errorCode)){
			payCardService.updateCardOpenStatus(cardId,"OPEN_SUCCESS");
		}else if("ERROR".equals(errorCode)){
			payCardService.updateCardOpenStatus(cardId,"OPEN_FAIL");
		}

		return "/goldgyro/openCardBack";
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@RequestMapping(value = "/open/backs")
	public void openCardBack(String encryptData, String signature, String cardId, HttpServletRequest request) {
		logger.info("【后台开卡通知】encryptData=========" + encryptData);
		logger.info("【后台开卡通知】signature=========" + signature);

		InterfaceLogPO interfaceLogPO = new InterfaceLogPO();
		interfaceLogPO.setBusiType("后台开卡通知");
		interfaceLogPO.setInputParams(JSON.toJSONString(encryptData));
		if (null == interfaceLogPO.getId() || "".equals(interfaceLogPO.getId())) {
			interfaceLogPO.setId(UUIDUtils.getUUIDNum(24));
		}
		logService.writeLog(interfaceLogPO);

		try {
			String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0, 16));
			Map<String,Object> map = (Map<String,Object>)JSON.parse(msg);
			String activateStatus = (String) map.get("activateStatus");
			payCardService.updateCardOpenStatus(cardId,
					"SUCCESS".equalsIgnoreCase(activateStatus) ? "OPEN_SUCCESS" : "OPEN_FAIL");
		} catch (Exception ex) {
			logger.error("开卡更新卡状态出现异常！", ex);
		}
	}

}
