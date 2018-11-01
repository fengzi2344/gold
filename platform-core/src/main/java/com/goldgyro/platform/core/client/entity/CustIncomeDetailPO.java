package com.goldgyro.platform.core.client.entity;

import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

import javax.persistence.*;

@Entity
@Table(name = "T_CUST_INCOME_DETAIL")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class CustIncomeDetailPO {
	@Id
	@GeneratedValue(generator = "jpa-uuid")
	@Column(length = 32)
	private String id;
	@Column(name = "CUST_ID", length = 32)
	private String custId;
	@Column(name = "CUST_NAME", length = 30)
	private String custName;
	@Column(name = "TRANS_TYPE", length = 20)
	private String transType;
	@Column(name = "TRANS_INCOME_ID", length = 32)
	private String transIncomeId;
	@Column(name = "INCOME_AMT")
	private double incomeAmt;
	@Column(name = "CREATED_TIME")
	private Timestamp createdTime;
	@Column(name = "DESCRIPTION", length = 255)
	private String description;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
