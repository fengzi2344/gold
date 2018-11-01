package com.goldgyro.platform.foreground.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.domain.InterfaceLog;
import com.goldgyro.platform.core.client.entity.InterfaceLogPO;
import com.goldgyro.platform.core.client.service.InterfaceLogService;
import com.goldgyro.platform.core.client.service.PayCardService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.foreground.batchs.BackInfoStatusUtils;
import com.goldgyro.platform.foreground.batchs.CardProcess;
import com.goldgyro.platform.foreground.constants.MessageConstant;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;

@RestController
@RequestMapping(value = "/cash")
public class WithDrawController {
	Logger logger = Logger.getLogger(WithDrawController.class);
	
	@Autowired
	InterfaceLogService interfaceLogService;
	
	@Autowired
	private PayCardService payCardService;
	
	@RequestMapping(value = "/withdraw", method = RequestMethod.POST)
	@ResponseBody
	public InterfaceResponseInfo withdraw(@RequestParam(value="platMerchantCode", required=false)String platMerchantCode,
			@RequestParam(value="amount", required=false)String amount, 
			@RequestParam(value="bankAccountNo", required=false)String bankAccountNo, HttpServletRequest request) {  
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		String withdrawOrderId = UUIDUtils.getUUIDNum();
		
		logger.info("withdrawOrderId=============="+withdrawOrderId);
		logger.info("platMerchantCode=============="+platMerchantCode);
		logger.info("amount=============="+amount);
		
		paramsMap.put("withdrawOrderId", withdrawOrderId);
		paramsMap.put("platMerchantCode", platMerchantCode);
		paramsMap.put("amount", amount);
		paramsMap.put("bankAccountNo", bankAccountNo);
		paramsMap.put("backUrl", "http://47.106.103.104/cash/withdrawBackUrl");
		
		String transNo = UUIDUtils.getUUIDNum(24);
		InterfaceLogPO logPO = new InterfaceLogPO();
		logPO.setBusiType("提现");
		logPO.setCardNo(bankAccountNo);
		logPO.setSerialNo(transNo);
		logPO.setBusiId(withdrawOrderId);
		
		String resp = new CardProcess().trans(paramsMap, transNo, PayChannelConstants.WITHDRAWAL, logPO, interfaceLogService);
		logger.info("提现返回信息=================="+resp);
		
		JSONObject head = BackInfoStatusUtils.getHeadData(resp);
		InterfaceLog interfaceLog = interfaceLogService.findInterfaceLogBySerialNo((String)head.get("orderId"));
		
		if ("SUCCESS".equalsIgnoreCase((String)head.get("respMsg"))) { 
			payCardService.updatePaymentPlanById("OPEN_SUCCESS" , interfaceLog.getBusiId());
			logger.info("【提现】提现成功！");
			return new InterfaceResponseInfo(MessageConstant.OK, "提现成功！", null); 
		}else {
			payCardService.updatePaymentPlanById("OPEN_FAIL", interfaceLog.getBusiId());
			logger.info("【提现】提现失败！");
			return new InterfaceResponseInfo(MessageConstant.OK, "提现失败！", null); 
		}
	}
}
