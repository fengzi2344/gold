package com.goldgyro.platform.core.client.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 还款计划
 *
 * @author wg2993
 */
@Entity
@Table(name = "T_PAYMENT_PLAN")
public class PaymentPlanPO {
    @Id
    private String id;
    @Column(name = "CUST_ID", length = 32)
    private String custId;
    @Column(name = "APPLY_ID", length = 32)
    private String applyId;
    @Column(name = "CARD_NO", length = 32)
    private String cardNo;
    @Column(name = "PAYMENT_AMT",precision = 2)
    private double paymentAmt;
    @Column(name = "PAYMENT_TIME")
    private Date paymentTime;
    @Column(name = "PLAN_STATUS", length = 20)
    private String planStatus;// '状态（INIT 待确认、WAIT_CONFIR待确认、REPAY_ING 还款中、REPAY_SUCCESS还款成功、REPAY_FAIL
    // 还款失败）'
    @Column(name = "TRANS_NO", length = 32)
    private String transNo;
    @Column(name = "cusumer_order_id", length = 32)
    private String cusumerOrderId;
    @Column(name = "CREATED_TIME")
    private Timestamp createdTime;

    @Column(name = "WORK_ID", length = 32)
    private String workId;

    @Column(name = "ORDER_ID", length = 32)
    private String orderId;

    @Column(name = "MCC_CODE", length = 32)
    private String mccCode;

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

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public double getPaymentAmt() {
        return paymentAmt;
    }

    public void setPaymentAmt(double paymentAmt) {
        this.paymentAmt = paymentAmt;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getCusumerOrderId() {
        return cusumerOrderId;
    }

    public void setCusumerOrderId(String cusumerOrderId) {
        this.cusumerOrderId = cusumerOrderId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getMccCode() {
        return mccCode;
    }

    public void setMccCode(String mccCode) {
        this.mccCode = mccCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
