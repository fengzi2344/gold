package com.goldgyro.platform.core.client.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "T_BIG_PAYMENT")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class BigPayment {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    @Column(name = "CONSUME_ORDER_ID", length = 20)
    private String consumeOrderId;
    @Column(name = "PAY_AMOUNT", length = 20)
    private String payAmount;
    @Column(name = "CUST_ID", length = 32)
    private String custId;
    @Column(name = "REMARK", length = 255)
    private String remark;
    @Column(name = "WORK_ID", length = 32)
    private String workId;
    @Column(name = "STATUS", length = 10)
    private String status;
    @Column(name = "CREATE_TIME")
    private Timestamp createTime;

    public BigPayment() {
    }

    public BigPayment(String consumeOrderId, String payAmount, String custId, String remark, String workId, String status, Timestamp createTime) {
        this.consumeOrderId = consumeOrderId;
        this.payAmount = payAmount;
        this.custId = custId;
        this.remark = remark;
        this.workId = workId;
        this.status = status;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsumeOrderId() {
        return consumeOrderId;
    }

    public void setConsumeOrderId(String consumeOrderId) {
        this.consumeOrderId = consumeOrderId;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}

