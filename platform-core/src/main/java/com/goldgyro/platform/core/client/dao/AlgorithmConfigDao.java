package com.goldgyro.platform.core.client.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.goldgyro.platform.core.client.entity.AlgorithmConfigPO;

public interface AlgorithmConfigDao extends JpaRepository<AlgorithmConfigPO, String>, JpaSpecificationExecutor<AlgorithmConfigPO> {
	/**
	 * 获取算法配置
	 *  
	 * @return
	 */
	public List<AlgorithmConfigPO> findAlgorithmConfigBy();
}
