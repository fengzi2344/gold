package com.goldgyro.platform.core.client.service;

import java.util.List;
import java.util.Map;

import com.goldgyro.platform.core.client.entity.WithdrawPlanPO;

public interface WithdrawPlanService {
	void save(WithdrawPlanPO withdrawPlanPO);
	
	void updateStatus(String id, String status);
	
	/**
	 * 待提现信息获取
	 * @return
	 */
	List<Map<String, Object>> findWithdrawPlanList();

	/**
	 * 获取还款申请
	 * @param applyId
	 * @param strPaymentDate
	 */
	Map<String, Object> findWithdrawPlan(String applyId, String strPaymentDate);

	/**
	 * 根据消费transNo 取消还款计划
	 * @param transNo
	 */
	void cancelByTransNo(String transNo);

	WithdrawPlanPO findById(String withdrawOrderId);

	WithdrawPlanPO findByWorkId(String workId);
}
