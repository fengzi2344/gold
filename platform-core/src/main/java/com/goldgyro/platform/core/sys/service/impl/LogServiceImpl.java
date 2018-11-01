package com.goldgyro.platform.core.sys.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goldgyro.platform.core.sys.dao.LogDao;
import com.goldgyro.platform.core.sys.entity.LogPO;
import com.goldgyro.platform.core.sys.service.LogService;

/**
 * @author wg2993
 *
 */
@Service
public class LogServiceImpl implements LogService {
	@Autowired
	private LogDao logDao;
	
	@Override
	@Transactional
	public void add(int logType, long userId, long targetId, String ip) {
		LogPO po = new LogPO();
		po.setType(logType);
		po.setTargetId(targetId);
		po.setUserId(userId);
		po.setIp(ip);
		po.setCreated(new Date());
		logDao.save(po);
	}
	
}
