package com.goldgyro.platform.core.client.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户银行卡信息
 * @author wg2993
 * @version 2081/07/10
 */
@Entity
@Table(name = "T_BANK_CARD")
public class BankCardPO {
	@Id
	private String id;
	
	@Column(name="CUST_ID", length=20)
	private String custId;
	
	@Column(name="ACCOUNT_CODE", length=20)
	private String accountCode;
	
	@Column(name="ACCOUNT_NAME", length=20)
	private String accountName;
	
	//C、信用卡，D、借记卡
	@Column(name="BANK_CARD_TYPE", length=10)
	private String bankCardType;
	
	@Column(name="BANK_CODE", length=20)
	private String bankCode;
	
	@Column(name="BANK_NAME", length=60)
	private String bankName;
	
	@Column(name="OPENING_SUB_BANK_NAME", length=60)
	private String openingSubBankName;
	
	@Column(name="OPENNING_BANK_PROVINCE", length=30)
	private String openningBankProvince;
	
	@Column(name="OPENNING_BANK_CITY", length=30)
	private String openningBankCity;
	
	@Column(name="MOBILE_NO", length=30)
	private String mobileNo;
	
	@Column(name="OPENNING_BANK_ADDRESS", length=120)
	private String openningBankAddress;
	
	//0否 ， 1是
	@Column(name="IS_DEFAULT")
	private String defaultCard;
	
	@Column(name="IO_TYPE")
	private String ioType; //i收入户，O支出户,IO收入支出皆可
	
	@Column(name="VALID", length=1)
	private String valid;	//Y有效，N无效。默认为Y
	
	@Column(name="OPEN_STATUS", length=20)
	private String openStatus;
	
	@Column(name="BIND_STATUS", length=20)
	private String bindStatus;
	
	@Column(name="CREATED_TIME")
	private Timestamp createdTime;
	@Column(name = "BANK_UNIT_NO", length = 32)
	private String bankUnitNo;
	@Column(name = "OPEN_CARD_ID", length = 32)
	private String openCardId;
	@Column(name = "CVN2", length = 3)
	private String cvn2;
	@Column(name = "EXPIRED", length = 4)
	private String expired;
	@Column(name = "OPEN_ORDER_ID", length = 20)
	private String openOrderId;
	@Column(name = "DEFAULT_AREA",length = 20)
	private String defaultArea;
	@Column(name = "ACCOUNTANT_BILL_DATE")
	private Integer accountantBillDate;
	@Column(name = "ACCOUNTANT_DATES")
	private Integer accountantDays;
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

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getBankCardType() {
		return bankCardType;
	}

	public void setBankCardType(String bankCardType) {
		this.bankCardType = bankCardType;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getOpeningSubBankName() {
		return openingSubBankName;
	}

	public void setOpeningSubBankName(String openingSubBankName) {
		this.openingSubBankName = openingSubBankName;
	}

	public String getOpenningBankProvince() {
		return openningBankProvince;
	}

	public void setOpenningBankProvince(String openningBankProvince) {
		this.openningBankProvince = openningBankProvince;
	}

	public String getOpenningBankCity() {
		return openningBankCity;
	}

	public void setOpenningBankCity(String openningBankCity) {
		this.openningBankCity = openningBankCity;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getOpenningBankAddress() {
		return openningBankAddress;
	}

	public void setOpenningBankAddress(String openningBankAddress) {
		this.openningBankAddress = openningBankAddress;
	}

	public String getDefaultCard() {
		return defaultCard;
	}

	public void setDefaultCard(String defaultCard) {
		this.defaultCard = defaultCard;
	}

	public String getIoType() {
		return ioType;
	}

	public void setIoType(String ioType) {
		this.ioType = ioType;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getOpenStatus() {
		return openStatus;
	}

	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}

	public String getBindStatus() {
		return bindStatus;
	}

	public void setBindStatus(String bindStatus) {
		this.bindStatus = bindStatus;
	}

	public String getBankUnitNo() {
		return bankUnitNo;
	}

	public void setBankUnitNo(String bankUnitNo) {
		this.bankUnitNo = bankUnitNo;
	}

	public String getOpenCardId() {
		return openCardId;
	}

	public void setOpenCardId(String openCardId) {
		this.openCardId = openCardId;
	}

	public String getCvn2() {
		return cvn2;
	}

	public void setCvn2(String cvn2) {
		this.cvn2 = cvn2;
	}

	public String getExpired() {
		return expired;
	}

	public void setExpired(String expired) {
		this.expired = expired;
	}

	public String getOpenOrderId() {
		return openOrderId;
	}

	public void setOpenOrderId(String openOrderId) {
		this.openOrderId = openOrderId;
	}

	public String getDefaultArea() {
		return defaultArea;
	}

	public void setDefaultArea(String defaultArea) {
		this.defaultArea = defaultArea;
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
