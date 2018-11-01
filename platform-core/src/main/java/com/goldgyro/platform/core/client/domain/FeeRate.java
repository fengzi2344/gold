package com.goldgyro.platform.core.client.domain;

/**
 * 费率
 * @author wg2993
 * @version2018-07-17
 */
public class FeeRate {
	private String id;
	private String clevelSample;
	private String ttypeCode;
	private double cfeeRate;//还款消费费率
	private double outRate;
	private String description;
	private double dfeeRate;//提现消费费率
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
