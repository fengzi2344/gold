package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.IncomePO;

public interface IncomeService {
	void  updateIncome(String status, String applyNo);

	IncomePO findByTransNo(String tranNo);
}
