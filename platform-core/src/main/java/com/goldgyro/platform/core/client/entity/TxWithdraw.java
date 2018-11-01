package com.goldgyro.platform.core.client.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "T_TX_WITHDRAW")
public class TxWithdraw {
    @Id
    @Column(name = "WITHDRAW_ORDER_ID", length = 32)
    private String withdrawOrderId;
    @Column(name = "CONSUME_ORDER_ID", length = 32)
    private String consumeOrderId;
    @Column(name = "CUST_ID", length = 32)
    private String custId;
    @Column(name = "AMOUNT")
    private double amount;
    @Column(name = "CARD_ID", length = 32)
    private String cardId;
    @Column(name = "DRAW_STATUS", length = 255)
    private String drawStatus;
    @Column(name="CREATE_TIME",insertable=false, updatable=false)
    @Generated(GenerationTime.INSERT)
    private Timestamp createTime;
    @Column(name = "ACCOUNTING_DATE")
    private Timestamp accountingDate;
    @Column(name = "TB_MSG", length = 4000)
    private String tbMsg;
    @Column(name = "MSG",length = 4000)
    private String msg;

    public TxWithdraw(String withdrawOrderId, String consumeOrderId, String custId, double amount, String cardId, String drawStatus, Timestamp createTime, Timestamp accountingDate, String tbMsg, String msg) {
        this.withdrawOrderId = withdrawOrderId;
        this.consumeOrderId = consumeOrderId;
        this.custId = custId;
        this.amount = amount;
        this.cardId = cardId;
        this.drawStatus = drawStatus;
        this.createTime = createTime;
        this.accountingDate = accountingDate;
        this.tbMsg = tbMsg;
        this.msg = msg;
    }

    public TxWithdraw() {
    }

    public String getWithdrawOrderId() {
        return withdrawOrderId;
    }

    public void setWithdrawOrderId(String withdrawOrderId) {
        this.withdrawOrderId = withdrawOrderId;
    }

    public String getConsumeOrderId() {
        return consumeOrderId;
    }

    public void setConsumeOrderId(String consumeOrderId) {
        this.consumeOrderId = consumeOrderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getDrawStatus() {
        return drawStatus;
    }

    public void setDrawStatus(String drawStatus) {
        this.drawStatus = drawStatus;
    }

    public Timestamp getAccountingDate() {
        return accountingDate;
    }

    public void setAccountingDate(Timestamp accountingDate) {
        this.accountingDate = accountingDate;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
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
}
