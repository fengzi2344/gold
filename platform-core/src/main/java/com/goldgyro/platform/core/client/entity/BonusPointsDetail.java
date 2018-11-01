package com.goldgyro.platform.core.client.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "T_BONUS_POINTS_DETAIL")
public class BonusPointsDetail {
    @Id
    @Column(length = 32)
    private String id;
    @Column(name="POINT_ID", length = 32)
    private String pointId;
    @Column(name="NUM", length = 10)
    private Integer sum;
    @Column(name="NOTE",length = 255)
    private String note;
    @Column(name="CREATE_TIME")
    private Timestamp createTime;

    public BonusPointsDetail() {
    }

    public BonusPointsDetail(String pointId, Integer sum, String note, Timestamp createTime) {
        this.pointId = pointId;
        this.sum = sum;
        this.note = note;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
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
