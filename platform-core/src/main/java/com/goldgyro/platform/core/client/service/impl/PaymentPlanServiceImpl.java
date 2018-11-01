package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.PaymentPlanDao;
import com.goldgyro.platform.core.client.entity.PaymentPlanPO;
import com.goldgyro.platform.core.client.service.PaymentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PaymentPlanServiceImpl implements PaymentPlanService{

	@Autowired
	private PaymentPlanDao planDao;

	@Transactional
	public void updatePaymentPlanById(String id, String status) {
		planDao.updatePaymentPlanById(id, status);
	}

	@Transactional
	public void cancelConfirmedPaymentPlan(String applyId, String status) {
		planDao.cancelConfirmedPaymentPlan(status, applyId);
	}

	@Override
	@Transactional
	public void updatePlanStatusByTransNo(String planStatus, String transNo) {
		planDao.updatePlanStatusByTransNo(planStatus,transNo);
	}

	@Override
	@Transactional
	public void updateDescr(String repMsg, String transNo) {
		planDao.updateDescr(repMsg,transNo);
	}

	@Override
	@Transactional
	public void savePlan(PaymentPlanPO paymentPlanPO) {
		planDao.saveAndFlush(paymentPlanPO);
	}

	@Override
	public PaymentPlanPO findByWorkId(String workId) {
		return planDao.findByWorkId(workId);
	}

	@Override
	public PaymentPlanPO findByTransNo(String transNo) {
		return planDao.findByTransNo(transNo);
	}

	@Override
	@Transactional
	public void updateMccById(String mccCode, String id) {
		planDao.updateMccCodeById(mccCode,id);
	}
}
