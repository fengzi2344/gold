package com.goldgyro.platform.core.client.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 费率
 * @author wg2993
 * @version2018-07-17
 */
@Entity
@Table(name = "T_TRANS_FEE_RATE")
public class FeeRatePO {
	@Id
	private String id;
	@Column(name="CLEVEL_SAMPLE", length = 32)
	private String clevelSample;
	@Column(name="TTYPE_CODE", length = 1)
	private String ttypeCode;
	@Column(name="CFEE_RATE")
	private double cfeeRate;
	@Column(name="OUT_RATE")
	private double outRate;
	@Column(name="DESCRIPTION", length = 255)
	private String description;
	@Column(name="DFEE_RATE")
	private double dfeeRate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClevelSample() {
		return clevelSample;
	}
	public void setClevelSample(String clevelSample) {
		this.clevelSample = clevelSample;
	}
	public String getTtypeCode() {
		return ttypeCode;
	}
	public void setTtypeCode(String ttypeCode) {
		this.ttypeCode = ttypeCode;
	}
	public double getCfeeRate() {
		return cfeeRate;
	}
	public void setCfeeRate(double cfeeRate) {
		this.cfeeRate = cfeeRate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getOutRate() {
		return outRate;
	}
	public void setOutRate(double outRate) {
		this.outRate = outRate;
	}

	public double getDfeeRate() {
		return dfeeRate;
	}

	public void setDfeeRate(double dfeeRate) {
		this.dfeeRate = dfeeRate;
	}
}
