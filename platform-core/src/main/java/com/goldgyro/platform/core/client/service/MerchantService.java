package com.goldgyro.platform.core.client.service;

import java.util.List;

import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.MerchantPO;

public interface MerchantService {
    void persist(MerchantPO merchantPO, BankCard bankCard, Customer customer);

    public MerchantPO findMerchantByCustId(String custId);
	
	public List<MerchantPO> findMerchant(String merchantStatus);
	
	public void udpate(String merchantCode, String platMerchantCode, String status );

	void processReistStatus(String merchantCode, String platMerchantCode, String status);
}
