package com.goldgyro.platform.core.client.domain;

import java.sql.Timestamp;

public class CustIncomeDetail {
	private String  id             ; 
	private String  custId        ; 
	private String  custName      ; 
	private String  transType     ; 
	private String  transIncomeId; 
	private double  incomeAmt     ; 
	private Timestamp  createdTime   ;
	
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
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public String getTransIncomeId() {
		return transIncomeId;
	}
	public void setTransIncomeId(String transIncomeId) {
		this.transIncomeId = transIncomeId;
	}
	public double getIncomeAmt() {
		return incomeAmt;
	}
	public void setIncomeAmt(double incomeAmt) {
		this.incomeAmt = incomeAmt;
	}
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
}
