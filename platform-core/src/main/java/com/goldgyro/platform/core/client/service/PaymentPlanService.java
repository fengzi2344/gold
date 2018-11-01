package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.PaymentPlanPO;

public interface PaymentPlanService {
	/**
	 * 通过ID更新状态
	 * @param id
	 * @param status
	 */
	void updatePaymentPlanById(String id, String status);

	/**
	 * 通过申请号更新计划状态
	 * @param applyId
	 * @param status
	 */
	void cancelConfirmedPaymentPlan(String applyId, String status);

	/**
	 * 根据订单号（库里的trans_no字段）更改还款状态
	 * @param planStatus
	 * @param transNo
	 */
	void updatePlanStatusByTransNo(String planStatus, String transNo);

	void updateDescr(String repMsg, String transNo);

    void savePlan(PaymentPlanPO paymentPlanPO);

	PaymentPlanPO findByWorkId(String workId);

	PaymentPlanPO findByTransNo(String transNo);

    void updateMccById(String mccCode, String id);
}
