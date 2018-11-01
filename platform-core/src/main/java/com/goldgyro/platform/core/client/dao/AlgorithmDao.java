package com.goldgyro.platform.core.client.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.goldgyro.platform.core.client.entity.AlgorithmConfigPO;

public interface AlgorithmDao extends JpaRepository<AlgorithmConfigPO, String>, JpaSpecificationExecutor <AlgorithmConfigPO>{

	@Modifying
	@Query(nativeQuery=true, value="call calcLevelOfIncome()")
	public void calcLevelOfIncome();

	@Modifying
	@Query(nativeQuery=true, value="call calcTwoLevelIncome()")
	public void calcTwoLevelIncome();

	@Modifying
	@Query(nativeQuery=true, value="call calcThreeLevelIncome()")
	public void calcThreeLevelAlgorithm();
	
}
