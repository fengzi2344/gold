package com.goldgyro.platform.core.client.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "T_APPLY_FEE")
public class ApplyFee {
    @Id
//    @GeneratedValue(generator = "jpa-uuid")
    private String id;
    @Column(name = "APPLY_ID", length = 32)
    private String applyId;
    @Column(name = "INCOME_AMT", length = 20)
    private Integer incomeAmt;
    @Column(name = "TOTAL_FEE", length = 20)
    private Integer totalFee;
    @Column(name = "THIRD_FEE", length = 20)
    private Integer thirdFee;
    @Column(name = "CREATE_TEIME")
    private Timestamp createTime;
    @Column(name = "FLAG")
    private String flag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public Integer getIncomeAmt() {
        return incomeAmt;
    }

    public void setIncomeAmt(Integer incomeAmt) {
        this.incomeAmt = incomeAmt;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getThirdFee() {
        return thirdFee;
    }

    public void setThirdFee(Integer thirdFee) {
        this.thirdFee = thirdFee;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ApplyFee() {
    }

    public ApplyFee(String id,String applyId, Integer incomeAmt, Integer totalFee, Integer thirdFee, Timestamp createTime, String flag) {
        this.id = id;
        this.applyId = applyId;
        this.incomeAmt = incomeAmt;
        this.totalFee = totalFee;
        this.thirdFee = thirdFee;
        this.createTime = createTime;
        this.flag = flag;
    }
}
