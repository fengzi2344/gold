package com.goldgyro.platform.core.client.domain;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 还款计划
 * 
 * @author wg2993
 * 
 */
public class PaymentPlan {
	private String id;
	private String custId;
	private String applyId;
	private String cardNo;
	private double paymentAmt;
	private Date paymentTime;
	private String planStatus;// '状态（INIT 待确认、WAIT_CONFIR待确认、REPAY_ING 还款中、REPAY_SUCCESS还款成功、REPAY_FAIL
									// 还款失败）'
	private String transNo;

	private Timestamp createdTime;

	private String mccCode;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getApplyId() {
		return applyId;
	}
	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public double getPaymentAmt() {
		return paymentAmt;
	}
	public void setPaymentAmt(double paymentAmt) {
		this.paymentAmt = paymentAmt;
	}
	public Date getPaymentTime() {
		return paymentTime;
	}
	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}

	public String getPlanStatus() {
		return planStatus;
	}
	public void setPlanStatus(String planStatus) {
		this.planStatus = planStatus;
	}
	public String getTransNo() {
		return transNo;
	}
	public void setTransNo(String transNo) {
		this.transNo = transNo;
	}
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public String getMccCode() {
		return mccCode;
	}

	public void setMccCode(String mccCode) {
		this.mccCode = mccCode;
	}
}
