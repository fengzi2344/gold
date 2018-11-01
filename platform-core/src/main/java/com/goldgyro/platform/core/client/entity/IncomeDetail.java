package com.goldgyro.platform.core.client.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 从用户钱包提现申请记录
 */
@Entity
@Table(name = "T_INCOME_DETAIL")
public class IncomeDetail {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    @Column(name = "CUST_INCOME_ID")
    private String custIncomeId;
    @Column(name = "AMOUNT", length = 20)
    private String amount;
    @Column(name = "APPLY_TIME")
    private Timestamp applyTime;
    @Column(name = "FIN_TIME")
    private Timestamp finTime;
    @Column(name = "STATUS", length = 20)
    private String status;
    @Column(name="CARD_NO")
    private String cardNo;
    @Column(name = "NOTE", length = 255)
    private String note;

    public IncomeDetail(String custIncomeId, String amount, Timestamp applyTime, Timestamp finTime, String status, String cardNo, String note) {
        this.custIncomeId = custIncomeId;
        this.amount = amount;
        this.applyTime = applyTime;
        this.finTime = finTime;
        this.status = status;
        this.cardNo = cardNo;
        this.note = note;
    }

    public IncomeDetail(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustIncomeId() {
        return custIncomeId;
    }

    public void setCustIncomeId(String custIncomeId) {
        this.custIncomeId = custIncomeId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Timestamp getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Timestamp applyTime) {
        this.applyTime = applyTime;
    }

    public Timestamp getFinTime() {
        return finTime;
    }

    public void setFinTime(Timestamp finTime) {
        this.finTime = finTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
}
