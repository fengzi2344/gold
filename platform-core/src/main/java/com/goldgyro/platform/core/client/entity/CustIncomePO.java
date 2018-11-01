package com.goldgyro.platform.core.client.entity;

import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

import javax.persistence.*;

/**
 * 客户收益
 * @author wg2993
 * @version 2018/07/26
 */
@Entity
@Table(name="T_CUST_INCOME")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class CustIncomePO {
	@Id
	@GeneratedValue(generator = "jpa-uuid")
	@Column(length = 32)
	private String id             ; 
	@Column(name="CUST_ID", length=32)
	private String custId         ; 
	@Column(name="CUST_NAME", length=20)
	private String custName       ; 
	@Column(name="TRANS_TYPE", length=20)
	private String transType      ; 
	@Column(name="TOTAL_INCOME_AMT")
	private double totalIncomeAmt ; 
	@Column(name="CREATED_TIME")
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
