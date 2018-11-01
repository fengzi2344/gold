package com.goldgyro.platform.core.client.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商户信息
 *
 * @author wg2993
 * @version 2018/07/16
 */
@Entity
@Table(name = "T_VIRTUAL_MERCHANT")
public class MerchantPO {
    @Id
    private String id;
    @Column(name = "CUST_ID", unique = true, length = 20)
    private String custId;
    @Column(name = "MERCHANT_CODE", length = 20)
    private String merchantCode;
    @Column(name = "MERCHANT_NAME", length = 60)
    private String merchantName;
    @Column(name = "MER_SHOT_NAME", length = 60)
    private String merShortName;
    @Column(name = "MERCHENT_ADDRESS", length = 120)
    private String merchentAddress;
    @Column(name = "MERCHANT_STATUS", length = 20)
    private String merchantStatus;  //状态（INIT 待注册、REG_ING 注册中、REG_SUCCESS 注册成功、REG_FAIL 注册失败）
    @Column(name = "MERCHANT_VALID", length = 1)
    private String valid;
    @Column(name = "PLAT_MERCHANT_CODE", length = 20)
    private String platMerchantCode;
    @Column(name = "PROVINCE", length = 32)
    private String province;
    @Column(name = "CITY", length = 32)
    private String city;


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

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchentAddress() {
        return merchentAddress;
    }

    public void setMerchentAddress(String merchentAddress) {
        this.merchentAddress = merchentAddress;
    }

    public String getMerchantStatus() {
        return merchantStatus;
    }

    public void setMerchantStatus(String merchantStatus) {
        this.merchantStatus = merchantStatus;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getPlatMerchantCode() {
        return platMerchantCode;
    }

    public void setPlatMerchantCode(String platMerchantCode) {
        this.platMerchantCode = platMerchantCode;
    }

    public String getMerShortName() {
        return merShortName;
    }

    public void setMerShortName(String merShortName) {
        this.merShortName = merShortName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
