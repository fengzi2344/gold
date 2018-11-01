package com.goldgyro.platform.core.client.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author wg2993
 *
 */
@Entity
@Table(name="T_CREDIT_TRANS_RECORD")
public class CreditTransRecordPO {
	@Id
	private String id;
	@Column(name="CUST_ID", length=20)
	private String custId;
	@Column(name="CUST_NAME", length=30)
	private String custName;
	@Column(name="CARD_NO", length=20)
	private String cardNo;
	@Column(name="APPLY_NO", length=24)
	private String applyNo;
	@Column(name="PLAN_NO", length=24)
	private String planNo;
	@Column(name="SERIAL_NO", length=24)
	private String serialNo;
	@Column(name="TRANS_TYPE", length=20)
	private String transType;
	@Column(name="TRANS_AMT")
	private double transAmt;
	@Column(name="TRANS_DATE")
	private Date transDate;
	@Column(name="ACTUAL_AMT")
	private double actualAmt;
	@Column(name="TRANS_STATUS", length=20)
	private String transStatus;
	@Column(name="CREATED_TIME")
	private Timestamp createdTime;
	
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
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getApplyNo() {
		return applyNo;
	}
	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}
	public String getPlanNo() {
		return planNo;
	}
	public void setPlanNo(String planNo) {
		this.planNo = planNo;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public double getTransAmt() {
		return transAmt;
	}
	public void setTransAmt(double transAmt) {
		this.transAmt = transAmt;
	}
	public Date getTransDate() {
		return transDate;
	}
	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}
	public double getActualAmt() {
		return actualAmt;
	}
	public void setActualAmt(double actualAmt) {
		this.actualAmt = actualAmt;
	}
	public String getTransStatus() {
		return transStatus;
	}
	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
}
