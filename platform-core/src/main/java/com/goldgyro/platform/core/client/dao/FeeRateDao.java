package com.goldgyro.platform.core.client.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.goldgyro.platform.core.client.entity.FeeRatePO;
import org.springframework.data.jpa.repository.Query;

public interface FeeRateDao extends JpaRepository<FeeRatePO, String>, JpaSpecificationExecutor<FeeRatePO> {
	//此方法应加TransTypeCode 
	FeeRatePO findFeeRateByClevelSampleAndTtypeCode(String clevelSample, String ttypeCode);

	List<FeeRatePO> findFeeRateListBy();

	@Query(nativeQuery = true, value = "select * from t_trans_fee_rate t where exists (select 1 from t_customer_info s where s.cust_id = ? and s.cust_level_sample = t.CLEVEL_SAMPLE)")
	FeeRatePO findByCustId(String custId);
}
