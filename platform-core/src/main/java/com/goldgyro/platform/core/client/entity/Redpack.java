package com.goldgyro.platform.core.client.entity;

import javax.persistence.*;
import java.sql.Timestamp;
@Entity
@Table(name = "T_REDPACK")
public class Redpack {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    @Column(name = "CUST_ID", length = 32)
    private String custId;
    @Column(name = "BALANCE", length = 20)
    private String balance;
    @Column(name = "UPDATE_TIME")
    private Timestamp updateTime;
    @Column(name = "NOTE", length = 255)
    private String note;

    @Column(name = "UNPACK_NUM")
    private Integer unpackNum;

    public Redpack(String id, String custId, String balance, Timestamp updateTime, String note) {
        this.id = id;
        this.custId = custId;
        this.balance = balance;
        this.updateTime = updateTime;
        this.note = note;
    }
    public Redpack(){}

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

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getUnpackNum() {
        return unpackNum;
    }

    public void setUnpackNum(Integer unpackNum) {
        this.unpackNum = unpackNum;
    }
}
