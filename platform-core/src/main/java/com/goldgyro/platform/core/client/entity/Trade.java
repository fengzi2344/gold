package com.goldgyro.platform.core.client.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "TRADE")
public class Trade {
    @Id
    @Column(name = "OUT_TRADE_NO", length = 32)
    private String outTradeNo;
    @Column(name = "CUST_ID", length = 32)
    private String custId;
    @Column(name = "SUBJECT", length = 255)
    private String subject;
    @Column(name = "TOTAL_AMOUNT")
    private double totalAmount;
    @Column(name = "BODY", length = 255)
    private String body;
    @Column(name = "TYPE", length = 20)
    private String type;
    @Column(name = "PAY_TIME")
    private Timestamp payTime;
    @Column(name = "TRADE_STATUS")
    private String tradeStatus;

    public Trade() {

    }

    public Trade(String outTradeNo, String custId, String subject, double totalAmount, String body, String type, Timestamp payTime, String tradeStatus) {
        this.outTradeNo = outTradeNo;
        this.custId = custId;
        this.subject = subject;
        this.totalAmount = totalAmount;
        this.body = body;
        this.type = type;
        this.payTime = payTime;
        this.tradeStatus = tradeStatus;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getPayTime() {
        return payTime;
    }

    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }

    public String getTradeStatus() { return tradeStatus; }

    public void setTradeStatus(String tradeStatus) { this.tradeStatus = tradeStatus; }
}
