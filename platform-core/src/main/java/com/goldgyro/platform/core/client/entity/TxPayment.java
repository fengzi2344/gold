package com.goldgyro.platform.core.client.entity;


import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "T_TX_PAYMENT")
public class TxPayment {
    @Id
    @Column(name = "CONSUME_ORDER_ID", length = 32)
    private String consumeOrderId;
    @Column(name = "CUST_ID", length = 32)
    private String custId;
    @Column(name = "PAY_AMOUNT")
    private double payAmount;
    @Column(name = "CARD_ID", length = 32)
    private String cardId;
    @Column(name = "ORDER_STATUS", length = 20)
    private String orderStatus;
    @Column(name = "FEE")
    private double fee;
    @Column(name="CREATE_TIME",insertable=false, updatable=false)
    @Generated(GenerationTime.INSERT)
    private Timestamp createTime;
    @Column(name = "PAY_TIME")
    private Timestamp payTime;
    //支付同步返回信息
    @Column(name = "TB_MSG", length = 4000)
    private String tbMsg;
    @Column(name = "msg", length = 4000)
    private String msg;
    @Column(name = "WORD_ID", length = 32)
    private String workId;

    public String getConsumeOrderId() {
        return consumeOrderId;
    }

    public void setConsumeOrderId(String consumeOrderId) {
        this.consumeOrderId = consumeOrderId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public Timestamp getPayTime() {
        return payTime;
    }

    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getTbMsg() {
        return tbMsg;
    }

    public void setTbMsg(String tbMsg) {
        this.tbMsg = tbMsg;
    }

    public TxPayment(String consumeOrderId, String custId, double payAmount, String cardId, String orderStatus, double fee, Timestamp createTime, Timestamp payTime, String tbMsg, String msg, String workId) {
        this.consumeOrderId = consumeOrderId;
        this.custId = custId;
        this.payAmount = payAmount;
        this.cardId = cardId;
        this.orderStatus = orderStatus;
        this.fee = fee;
        this.createTime = createTime;
        this.payTime = payTime;
        this.tbMsg = tbMsg;
        this.msg = msg;
        this.workId = workId;
    }

    public TxPayment() {
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }
}
