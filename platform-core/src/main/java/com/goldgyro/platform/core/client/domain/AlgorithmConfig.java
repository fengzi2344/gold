package com.goldgyro.platform.core.client.domain;

import java.sql.Timestamp;

/**
 * 算法配置信息
 * 
 * @author wg2993
 * @version 2018/07/28
 */
public class AlgorithmConfig {
	private String id;
	private String algorithmType;
	private String algorithmDesc;
	private String handle;
	private String isInvalid;
	private Timestamp createdTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlgorithmType() {
		return algorithmType;
	}

	public void setAlgorithmType(String algorithmType) {
		this.algorithmType = algorithmType;
	}

	public String getAlgorithmDesc() {
		return algorithmDesc;
	}

	public void setAlgorithmDesc(String algorithmDesc) {
		this.algorithmDesc = algorithmDesc;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getIsInvalid() {
		return isInvalid;
	}

	public void setIsInvalid(String isInvalid) {
		this.isInvalid = isInvalid;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

}
