package com.goldgyro.platform.core.client.domain;

import java.sql.Timestamp;
import java.util.Date;

public class Income {
	private String id;
	private String transNo;
	private String applyNo;
	private String transType;
	private String custId;
	private String custName;
	private String bankNo;
	private Date transTime;
	private double transFee;
	private double incomeAmt;
	private String incomeStatus;
	private Timestamp createdTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTransNo() {
		return transNo;
	}

	public void setTransNo(String transNo) {
		this.transNo = transNo;
	}

	public String getApplyNo() {
		return applyNo;
	}

	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public Date getTransTime() {
		return transTime;
	}

	public void setTransTime(Date transTime) {
		this.transTime = transTime;
	}

	public String getIncomeStatus() {
		return incomeStatus;
	}

	public void setIncomeStatus(String incomeStatus) {
		this.incomeStatus = incomeStatus;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public double getTransFee() {
		return transFee;
	}

	public void setTransFee(double transFee) {
		this.transFee = transFee;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public double getIncomeAmt() {
		return incomeAmt;
	}

	public void setIncomeAmt(double incomeAmt) {
		this.incomeAmt = incomeAmt;
	}

}
