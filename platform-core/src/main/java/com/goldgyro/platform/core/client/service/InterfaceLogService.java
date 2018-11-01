package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.domain.InterfaceLog;
import com.goldgyro.platform.core.client.entity.InterfaceLogPO;

public interface InterfaceLogService {
	public void writeLog(InterfaceLogPO logPO);

	/**
	 * 通过Id查询日志
	 */
	public InterfaceLog findInterfaceLogBySerialNo(String serialNo);

    void writeLogAndUpdateWithraw(InterfaceLogPO interfaceLogPO, String orderId);
}
