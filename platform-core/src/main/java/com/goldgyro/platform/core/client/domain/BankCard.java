package com.goldgyro.platform.core.client.domain;

import java.sql.Timestamp;

/**
 * 用户银行卡信息
 * @author wg2993
 * @version 2081/07/10
 */
public class BankCard {
	private String id;
	private String custId;
	private String accountCode;
	private String accountName;
	private String bankCardType;  //C信用卡，D借记卡
	private String bankCode;
	private String bankName;
	private String openingSubBankName;
	private String openningBankProvince;
	private String openningBankCity;
	private String openningBankAddress;
	private String mobileNo;
	private String defaultCard;	//Y是，N否
	private String ioType; //I收入户，O支出户
	private String valid;
	private String openStatus;
	private String bindStatus;
	private Timestamp createdTime;
	private String icon;//银行图标 此处是图片url
	private String bankUnitNo;
	private String openCardId;
	private String cvn2;
	private String expired;
	private boolean havePaymentPlan;
	private String openOrderId;
	private String latestPlanStatus;
	private String failMsg;
	private String defaultArea;//默认消费地区编码
	private Integer accountantBillDate;//信用卡账单日
	private Integer accountantDays;//最长还款天数

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
	public String getOpenningBankAddress() {
		return openningBankAddress;
	}
	public void setOpenningBankAddress(String openningBankAddress) {
		this.openningBankAddress = openningBankAddress;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
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
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
	public boolean isHavePaymentPlan() {
		return havePaymentPlan;
	}
	public void setHavePaymentPlan(boolean havePaymentPlan) {
		this.havePaymentPlan = havePaymentPlan;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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

	public String getLatestPlanStatus() {
		return latestPlanStatus;
	}

	public void setLatestPlanStatus(String latestPlanStatus) {
		this.latestPlanStatus = latestPlanStatus;
	}

	public String getFailMsg() {
		return failMsg;
	}

	public void setFailMsg(String failMsg) {
		this.failMsg = failMsg;
	}

	public String getDefaultArea() {
		return defaultArea;
	}

	public void setDefaultArea(String defaultArea) {
		this.defaultArea = defaultArea;
	}
}
