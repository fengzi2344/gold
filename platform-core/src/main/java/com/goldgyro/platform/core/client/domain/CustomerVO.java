package com.goldgyro.platform.core.client.domain;

import java.sql.Timestamp;
import java.util.Date;

public class CustomerVO {
    private String custId;
    private String inviterId;
    private String custName;
    //M表示男，G表示女
    private String custMobile;
    private String idCardNo;
    private String custLevelSample;
    private String custStatus;//REG：注册，AUTH：实名认证，FORBID：禁用，DEL：删除
    private Date lastLogin;
    private Timestamp createdTime;

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

    public String getCustMobile() {
        return custMobile;
    }

    public void setCustMobile(String custMobile) {
        this.custMobile = custMobile;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
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
}
