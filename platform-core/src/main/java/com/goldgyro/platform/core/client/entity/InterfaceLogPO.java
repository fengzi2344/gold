package com.goldgyro.platform.core.client.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 与第三方支付平台的日志记录
 * @author wg2993
 * @version 2018/07/22
 */
@Entity
@Table(name="T_INTERFACE_LOG")
public class InterfaceLogPO {
	@Id
	private String id;
	@Column(name="BUSI_TYPE", length=20)
	private String busiType;
	@Column(name="CUST_ID", length=20)
	private String custId;
	@Column(name="CUST_NAME", length=20)
	private String custName;
	@Column(name="CARD_NO", length=20)
	private String cardNo;
	@Column(name="SERIAL_NO", length=32)
	private String serialNo;
	@Column(name="BUSI_ID", length=32)
	private String busiId;
	public String getBusiId() {
		return busiId;
	}

	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}

	@Column(name="INPUT_PARAMS", length=4000)
	private String inputParams;
	@Column(name="OUT_PARAMS", length=4000)
	private String outParams;
	@Column(name="CREATED_TIME")
	private Timestamp createdTime;

	@Column(name = "TABLE_NAME", length = 200)
	private String tableName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
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

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getInputParams() {
		return inputParams;
	}

	public void setInputParams(String inputParams) {
		this.inputParams = inputParams;
	}

	public String getOutParams() {
		return outParams;
	}

	public void setOutParams(String outParams) {
		this.outParams = outParams;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
