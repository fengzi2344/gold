package com.goldgyro.platform.core.client.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 算法配置信息
 * 
 * @author wg2993
 * @version 2018/07/28
 */
@Entity
@Table(name = "T_ALGORITHM_CONFIG")
public class AlgorithmConfigPO {
	@Id
	private String id;
	@Column(name = "ALGORITHM_TYPE")
	private String algorithmType;
	@Column(name = "ALGORITHM_DESC")
	private String algorithmDesc;
	@Column(name = "HANDLE")
	private String handle;
	@Column(name = "IS_INVALID")
	private String isInvalid;
	@Column(name = "CREATED_TIME")
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
