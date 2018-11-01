package com.goldgyro.platform.core.client.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.goldgyro.platform.core.client.dao.PaymentPlanDao;
import com.goldgyro.platform.core.client.service.CallBack;

public class PaymentCallBackImpl implements CallBack {
	@Autowired
	private PaymentPlanDao paymentPlanDao;
	
	
	@Override
	public void accessCallBack(Object paramObjs) {
		if(null == paramObjs) {
			return;
		}
		@SuppressWarnings("unchecked")
		Map <String, String> paramMap = (Map<String, String>)paramObjs;
		paymentPlanDao.updatePaymentPlanById(paramMap.get("id"), paramMap.get("status"));
	}
	
	@Override
	public void failCallBack(Object paramObjs) {
		if(null == paramObjs) {
			return;
		}
		return;
	}
}
