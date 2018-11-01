package com.goldgyro.platform.core.client.domain;

import java.sql.Timestamp;

/**
 * 还款申请信息
 * @author wg2993
 * @version 2018/07/14
 */
public class PaymentApply {
	private String id;
	private String custId;
	private String cardCode;
	private String inCardCode;
	private double paymentAmt;
	private String paymentDate;
	private String cashTrans;
	private Timestamp createdTime;
	private String status;
	private Integer accountantBillDate;
	private Integer accountantDays;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	private String city;
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

	public String getCardCode() {
		return cardCode;
	}

	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}

	public String getInCardCode() {
		return inCardCode;
	}

	public void setInCardCode(String inCardCode) {
		this.inCardCode = inCardCode;
	}

	public double getPaymentAmt() {
		return paymentAmt;
	}

	public void setPaymentAmt(double paymentAmt) {
		this.paymentAmt = paymentAmt;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getCashTrans() {
		return cashTrans;
	}

	public void setCashTrans(String cashTrans) {
		this.cashTrans = cashTrans;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getAccountantBillDate() {
		return accountantBillDate;
	}

	public void setAccountantBillDate(Integer accountantBillDate) {
		this.accountantBillDate = accountantBillDate;
	}

	public Integer getAccountantDays() {
		return accountantDays;
	}

	public void setAccountantDays(Integer accountantDays) {
		this.accountantDays = accountantDays;
	}
}
