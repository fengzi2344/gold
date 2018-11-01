package com.goldgyro.platform.core.client.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 还款申请信息
 *
 * @author wg2993
 * @version 2018/07/14
 */
@Entity
@Table(name = "T_PAYMENT_APPLY")
public class PaymentApplyPO {
    @Id
    private String id;
    @Column(name = "CUST_ID", length = 32)
    private String custId;
    @Column(name = "CARD_CODE", length = 20)
    private String cardCode;
    @Column(name = "IN_CARD_CODE", length = 20)
    private String inCardCode;
    @Column(name = "PAYMENT_AMT")
    private double paymentAmt;
    @Column(name = "PAYMENT_DATE", length = 4000)
    private String paymentDate;// 逗号分隔
    @Column(name = "CASH_TRANS", length = 1)
    private String cashTrans;
    @Column(name = "CREATED_TIME")
    private Timestamp createdTime;
    @Column(name = "CITY", length = 32)
    private String city;
    @Column(name = "STATUS", length = 32)
    private String status;

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


    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getInCardCode() {
        return inCardCode;
    }

    public void setInCardCode(String inCardCode) {
        this.inCardCode = inCardCode;
    }

    public double getPaymentAmt() {
        return paymentAmt;
    }

    public void setPaymentAmt(double paymentAmt) {
        this.paymentAmt = paymentAmt;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getCashTrans() {
        return cashTrans;
    }

    public void setCashTrans(String cashTrans) {
        this.cashTrans = cashTrans;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
