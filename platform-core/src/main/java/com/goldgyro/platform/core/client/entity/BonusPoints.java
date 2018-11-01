package com.goldgyro.platform.core.client.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "T_BONUS_POINTS")
public class BonusPoints {
    @Id
    @Column(length = 32)
    private String id;
    @Column(name="CUST_ID", length = 32)
    private String custId;
    @Column(name="NUM", length = 10)
    private Integer sum;
    @Column(name="UPDATE_TIME")
    private Timestamp updateTime;

    public BonusPoints() {
    }

    public BonusPoints(String custId, Integer sum, Timestamp updateTime) {
        this.custId = custId;
        this.sum = sum;
        this.updateTime = updateTime;
    }

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

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
