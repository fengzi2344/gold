package com.goldgyro.platform.core.client.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.goldgyro.platform.core.client.entity.MerchantPO;

public interface MerchantDao extends JpaRepository<MerchantPO, String>, JpaSpecificationExecutor<MerchantPO> {

	MerchantPO findMerchantByCustId(String custId);

	@Modifying
	@Query(nativeQuery=true, value = "update T_VIRTUAL_MERCHANT vm set vm.PLAT_MERCHANT_CODE=?, vm.MERCHANT_STATUS=? where vm.MERCHANT_CODE=?")
	public void udpate(String platMerchantCode, String status, String merchantCode );
	
	public List<MerchantPO> findMerchantByMerchantStatus(String merchantStatus);

}
