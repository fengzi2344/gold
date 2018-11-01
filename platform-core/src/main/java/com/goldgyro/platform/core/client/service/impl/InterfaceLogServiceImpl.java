package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.WithdrawPlanDao;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goldgyro.platform.core.client.dao.InterfaceLogDao;
import com.goldgyro.platform.core.client.domain.InterfaceLog;
import com.goldgyro.platform.core.client.entity.InterfaceLogPO;
import com.goldgyro.platform.core.client.service.InterfaceLogService;

@Service
public class InterfaceLogServiceImpl implements InterfaceLogService {
	
	@Autowired
	private InterfaceLogDao interfaceLogDao;
	@Autowired
	private WithdrawPlanDao withdrawPlanDao;
	
	@Override
	@Transactional
	public void writeLog(InterfaceLogPO logPO) {
		interfaceLogDao.saveAndFlush(logPO);
	}

	/**
	 * 通过Id查询日志
	 */
	public InterfaceLog findInterfaceLogBySerialNo(String serialNo) {
		InterfaceLogPO interfaceLogPO = interfaceLogDao.findInterfaceLogBySerialNo(serialNo);
		InterfaceLog interfaceLog = new InterfaceLog();
		
		BeanUtils.copyProperties(interfaceLogPO, interfaceLog);
		
		return interfaceLog;
	}

	@Override
	@Transactional
	public void writeLogAndUpdateWithraw(InterfaceLogPO interfaceLogPO, String orderId) {
		interfaceLogDao.saveAndFlush(interfaceLogPO);
		withdrawPlanDao.updateStatus("PROCESSING",orderId);
	}
}
