package com.goldgyro.platform.core.client.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 提现计划
 *
 * @author wg2993
 * @version 2018/07/26
 */
@Entity
@Table(name = "T_WITHDRAW_PLAN")
public class WithdrawPlanPO {
    @Id
    private String id;
    @Column(name = "CUST_ID", length = 32)
    private String custId;
    @Column(name = "APPLY_ID", length = 32)
    private String applyId;
    @Column(name = "PAYMENT_DATE")
    private Date paymentDate;
    @Column(name = "WITHDRAW_AMT")
    private Double withdrawAmt;
    @Column(name = "WITHDRAW_DATE")
    private Date withdrawDate;
    @Column(name = "WITHDRAW_STATUS", length = 20)
    private String withdrawStatus;
    @Column(name = "CREATED_TIME")
    private Timestamp createdTime;
    @Column(name = "WORK_ID", length = 32)
    private String workId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplyId() {
        return applyId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Double getWithdrawAmt() {
        return withdrawAmt;
    }

    public void setWithdrawAmt(Double withdrawAmt) {
        this.withdrawAmt = withdrawAmt;
    }

    public Date getWithdrawDate() {
        return withdrawDate;
    }

    public void setWithdrawDate(Date withdrawDate) {
        this.withdrawDate = withdrawDate;
    }

    public String getWithdrawStatus() {
        return withdrawStatus;
    }

    public void setWithdrawStatus(String withdrawStatus) {
        this.withdrawStatus = withdrawStatus;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }
}
