package com.goldgyro.platform.core.client.domain;

import java.sql.Timestamp;

/**
 * 客户收益
 * @author wg2993
 * @version 2018/07/26
 */
public class CustIncome {
	private String id             ; 
	private String custId         ; 
	private String custName       ; 
	private String transType      ; 
	private double totalIncomeAmt ; 
	private Timestamp createdTime ;
	
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
	public double getTotalIncomeAmt() {
		return totalIncomeAmt;
	}
	public void setTotalIncomeAmt(double totalIncomeAmt) {
		this.totalIncomeAmt = totalIncomeAmt;
	}
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	} 
}
