package com.goldgyro.platform.core.client.entity;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.*;

/**
 * 客户实体类
 * 
 * @author wg2993
 * @version 2018/07/09
 */
@Entity
@Table(name = "T_CUSTOMER_INFO")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@NamedStoredProcedureQuery(name="modify_merchant", procedureName = "modify_merchant",parameters= {
		@StoredProcedureParameter(mode=ParameterMode.IN, type=String.class, name="v_code")
})
public class CustomerPO implements Comparable<CustomerPO>{
	@Id
	private String id;	
	
	@Column(name = "CUST_ID", unique = true, length = 20)
	private String custId;
	/**
	 * 此字段 由原来的推荐人custId 变更为推荐人手机号
	 */
	@Column(name="INVITER_ID", length = 20)
	private String inviterId;
	
	@Column(name="CUST_PASSWORD", length=32)
	private String custPassword;
	
	@Column(name = "CUST_NAME", length = 60)
	private String custName;
	
	//M表示男，G表示女
	@Column(name = "CUST_SEX", length = 1)
	private String custSex;
	
	@Column(name = "CUST_AGE")
	private Number custAge;
	
	@Column(name = "CUST_MOBILE", length = 20)
	private String custMobile;
	
	@Column(name="CUST_EMAIL", length = 60)
	private String custEmail;
	
	@Column(name = "ID_CARD_NO", length = 18)
	private String idCardNo;
	
	@Column(name = "ADDRESS", length = 120)
	private String address;
	
	@Column(name = "CUST_LEVEL_SAMPLE", length = 20)
	private String custLevelSample;
	
	//REG：注册，AUTH：实名认证，FORBID：禁用，DEL：删除
	@Column(name = "CUST_STATUS", length = 10)
	private String custStatus;
	
	@Column(name = "lastLogin")
	private Date lastLogin;
	
	@Column(name = "CREATED_TIME")
	private Timestamp createdTime;

	@Column(name = "father_mobile")
	private String fatherMobile;

	@Column(name ="PATTERN_PASSWORD", length = 32)
	private String patternPassword;

	@Column(name = "LEVEL_CODE", length = 4000)
	private String levelCode;

	@Column(name = "GROUP_NUM")
	private Integer groupNum;

	@Column(name = "DIRECT_NUM")
	private Integer directNum;

	@Column(name = "CUST_CODE",length = 6)
	private String custCode;

	@Column(name = "FREE_MEMBER")
	private String freeMember;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustPassword() {
		return custPassword;
	}

	public void setCustPassword(String custPassword) {
		this.custPassword = custPassword;
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

	@Override
    public int compareTo(CustomerPO o) {
	    int l = o.getLevelCode().length()-this.getLevelCode().length();
	    if(l>0){
            return 1;
        }else if(l < 0){
	        return -1;
        }else {
	        return 0;
        }

    }
}
