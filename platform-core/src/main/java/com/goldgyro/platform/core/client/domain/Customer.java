package com.goldgyro.platform.core.client.domain;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 客户实体类
 * 
 * @author wg2993
 * @version 2018/07/09
 */
public class Customer {
	private String id;	
	private String custId;
	private String inviterId;
	private String custPassword;
	private String custName;
	//M表示男，G表示女
	private String custSex;
	private Number custAge;
	private String custMobile;
	private String custEmail;
	private String idCardNo;
	private String address;
	private String custLevelSample;
	private String custStatus;//REG：注册，AUTH：实名认证，FORBID：禁用，DEL：删除
	private Date lastLogin;
	private Timestamp createdTime;
	private String fatherMobile;//父节点非普通用户手机号
	private String patternPassword;
	
	private List<BankCard> bankCards; 
	
	private String identifyingCode;

	private String levelCode;

	private Integer groupNum;

	private Integer directNum;

	private String custCode;

	private String freeMember;

	private String token;

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

	public String getInviterId() {
		return inviterId;
	}

	public void setInviterId(String inviterId) {
		this.inviterId = inviterId;
	}

	public String getCustPassword() {
		return custPassword;
	}

	public void setCustPassword(String custPassword) {
		this.custPassword = custPassword;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustSex() {
		return custSex;
	}

	public void setCustSex(String custSex) {
		this.custSex = custSex;
	}

	public Number getCustAge() {
		return custAge;
	}

	public void setCustAge(Number custAge) {
		this.custAge = custAge;
	}

	public String getCustMobile() {
		return custMobile;
	}

	public void setCustMobile(String custMobile) {
		this.custMobile = custMobile;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCustLevelSample() {
		return custLevelSample;
	}

	public void setCustLevelSample(String custLevelSample) {
		this.custLevelSample = custLevelSample;
	}

	public String getCustStatus() {
		return custStatus;
	}

	public void setCustStatus(String custStatus) {
		this.custStatus = custStatus;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public List<BankCard> getBankCards() {
		return bankCards;
	}

	public void setBankCards(List<BankCard> bankCards) {
		this.bankCards = bankCards;
	}

	public String getIdentifyingCode() {
		return identifyingCode;
	}

	public void setIdentifyingCode(String identifyingCode) {
		this.identifyingCode = identifyingCode;
	}

	public String getFatherMobile() {
		return fatherMobile;
	}

	public void setFatherMobile(String fatherMobile) {
		this.fatherMobile = fatherMobile;
	}

	public String getPatternPassword() {
		return patternPassword;
	}

	public void setPatternPassword(String patternPassword) {
		this.patternPassword = patternPassword;
	}

	public String getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}

	public Integer getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(Integer groupNum) {
		this.groupNum = groupNum;
	}

	public Integer getDirectNum() {
		return directNum;
	}

	public void setDirectNum(Integer directNum) {
		this.directNum = directNum;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getFreeMember() {
		return freeMember;
	}

	public void setFreeMember(String freeMember) {
		this.freeMember = freeMember;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
