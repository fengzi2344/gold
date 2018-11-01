package com.goldgyro.platform.core.client.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "T_REDPACK_DETAIL")
public class RedpackDetail {
    @Id
    @Column(length = 32)
    private String id;
    @Column(name = "REDPACK_ID", length = 32)
    private String redpackId;
    @Column(name = "SUM")
    private Integer sum;
    @Column(name = "NOTE", length = 255)
    private String note;
    @Column(name = "CREATE_TIME")
    private Timestamp createTime;

    public RedpackDetail(String id, String redpackId, Integer sum, String note, Timestamp createTime) {
        this.id = id;
        this.redpackId = redpackId;
        this.sum = sum;
        this.note = note;
        this.createTime = createTime;
    }

    public RedpackDetail() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRedpackId() {
        return redpackId;
    }

    public void setRedpackId(String redpackId) {
        this.redpackId = redpackId;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
