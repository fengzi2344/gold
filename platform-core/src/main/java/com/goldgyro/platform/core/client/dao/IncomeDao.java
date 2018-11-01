package com.goldgyro.platform.core.client.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.goldgyro.platform.core.client.entity.IncomePO;

public interface IncomeDao  extends JpaRepository<IncomePO, String>, JpaSpecificationExecutor<IncomePO> {
	
	@Modifying
	@Query(nativeQuery=true, value = "update T_TRANS_INCOME ti set ti.INCOME_STATUS=? where ti.APPLY_NO=?")
	void updateByApplyNo(String status, String applyNo);

	IncomePO findByTransNo(String transNo);

	/**
	 * 根据transNo更新收益状态
	 * @param inocmeStatus
	 * @param transNo
	 */
	@Modifying
	@Query(nativeQuery=true, value = "update T_TRANS_INCOME set INCOME_STATUS=? where APPLY_NO=? and INCOME_STATUS=?")
	void updateIncomeStatusByApplyNo(String inocmeStatus, String transNo, String incomeStatus1);

	/**
	 *
	 * @param incomeStatus
	 * @param transNo
	 */
	@Modifying
	@Query(nativeQuery=true, value = "update T_TRANS_INCOME set INCOME_STATUS=? where TRANS_NO=?")
	void updateIncomeStatusByTransNo(String incomeStatus,String transNo);
}
