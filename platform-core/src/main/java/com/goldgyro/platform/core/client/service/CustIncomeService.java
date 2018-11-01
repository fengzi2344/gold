package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.CustIncomePO;

import java.util.List;
import java.util.Map;

public interface CustIncomeService {
    CustIncomePO findByCustId(String id);
    void save(CustIncomePO custIncomePO);
    void updateIncomeAmt(String id, double incomeAmount);

    void withdrawFromIncome(CustIncomePO custIncome, String money,String cardNo) throws Exception;

    List<Map<String,Object>> findTradeDetail(String custId);
}
