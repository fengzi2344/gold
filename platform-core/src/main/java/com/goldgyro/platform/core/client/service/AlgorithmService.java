package com.goldgyro.platform.core.client.service;

import java.util.List;

import com.goldgyro.platform.core.client.domain.AlgorithmConfig;

public interface AlgorithmService {
	/**
	 * 获取算法配置
	 *  
	 * @return
	 */
	public List<AlgorithmConfig> findAlgorithmConfig();
	
	/**
	 * 计算平级收益
	 */
	public void calcLevelOfIncome();

	/**
	 * 计算下级带来的收益
	 */
	public void calcTwoLevelIncome();
	
	/**
	 * 计算下级的下级带来的收益
	 */
	public void calcThreeLevelAlgorithm();
}
