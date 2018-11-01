package com.goldgyro.platform.core.client.service.impl;

import javax.transaction.Transactional;

import com.goldgyro.platform.core.client.entity.IncomePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goldgyro.platform.core.client.dao.IncomeDao;
import com.goldgyro.platform.core.client.service.IncomeService;

@Service
public class IncomeServiceImpl implements IncomeService{
	@Autowired
	private IncomeDao incomeDao;
	
	@Transactional
	public void updateIncome(String status, String applyNo) {
		incomeDao.updateByApplyNo(status, applyNo);
	}

	@Override
	public IncomePO findByTransNo(String tranNo) {
		return incomeDao.findByTransNo(tranNo);
	}
}
