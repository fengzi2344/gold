package com.goldgyro.platform.core.client.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 收益实体类
 * 
 * @author wg2993
 * @version 2018/07/17
 */
@Entity
@Table(name = "T_TRANS_INCOME")
public class IncomePO {
	@Id
	private String id;
	@Column(name = "TRANS_NO", unique = true, length = 32)
	private String transNo;
	@Column(name = "APPLY_NO", length = 32)
	private String applyNo;
	@Column(name = "TRANS_TYPE", length = 1)
	private String transType;
	@Column(name = "CUST_ID", length = 12)
	private String custId;
	@Column(name = "CUST_NAME", length = 20)
	private String custName;
	@Column(name = "BANK_NO", length = 20)
	private String bankNo;
	@Column(name = "TRANS_TIME", length = 20)
	private Date transTime;
	@Column(name="TRANS_FEE")
	private double transFee;
	@Column(name = "INCOME_AMT")
	private double incomeAmt;
	@Column(name = "INCOME_STATUS", length = 10)
	private String incomeStatus;
	@Column(name = "CTREATED_TIME")
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

	public String getIncomeStatus() {
		return incomeStatus;
	}

	public void setIncomeStatus(String incomeStatus) {
		this.incomeStatus = incomeStatus;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
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

	public double getIncomeAmt() {
		return incomeAmt;
	}

	public void setIncomeAmt(double incomeAmt) {
		this.incomeAmt = incomeAmt;
	}

	public double getTransFee() {
		return transFee;
	}

	public void setTransFee(double transFee) {
		this.transFee = transFee;
	}

}
