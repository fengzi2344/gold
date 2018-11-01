package com.goldgyro.platform.core.client.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.goldgyro.platform.core.client.dao.PayCardDao;
import com.goldgyro.platform.core.client.service.CallBack;

public class CardRegistCallBackImpl implements CallBack {
	@Autowired
	private PayCardDao payCardDao;
	
	
	@Override
	public void accessCallBack(Object paramObjs) {
		if(null == paramObjs) {
			return;
		}
		@SuppressWarnings("unchecked")
		Map <String, String> paramMap = (Map<String, String>)paramObjs;
		payCardDao.updateCardBindStatus(paramMap.get("id"), paramMap.get("status"));
	}
	
	@Override
	public void failCallBack(Object paramObjs) {
		if(null == paramObjs) {
			return;
		}
		return;
	}
}
