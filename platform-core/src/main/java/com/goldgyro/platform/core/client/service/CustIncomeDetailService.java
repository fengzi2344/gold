package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.CustIncomeDetailPO;

import java.util.List;
import java.util.Map;

public interface CustIncomeDetailService {
    void saveCustIncomeDetail(CustIncomeDetailPO custIncomeDetailPO);
    List<CustIncomeDetailPO> findByCustId(String custId);

    Map<String,Object> findIncomeInfo(String custId);

    List<Map<String,Object>> findTradeDetail(java.lang.String custId, java.lang.String analysisType);

    List<CustIncomeDetailPO> findDetails(String custId, String time, String type);
}
