package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.CustIncomeDao;
import com.goldgyro.platform.core.client.dao.CustIncomeDetailDao;
import com.goldgyro.platform.core.client.entity.CustIncomeDetailPO;
import com.goldgyro.platform.core.client.service.CustIncomeDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustIncomeDetailServiceImpl implements CustIncomeDetailService {
    @Autowired
    private CustIncomeDetailDao custIncomeDetailDao;
    @Override
    public void saveCustIncomeDetail(CustIncomeDetailPO custIncomeDetailPO) {
        this.custIncomeDetailDao.saveAndFlush(custIncomeDetailPO);
    }

    @Override
    public List<CustIncomeDetailPO> findByCustId(String custId) {
        return custIncomeDetailDao.findAllByCustIdOrderByCreatedTimeDesc(custId);
    }

    @Override
    public Map<String, Object> findIncomeInfo(String custId) {
        return custIncomeDetailDao.finIncomeInfo(custId);
    }

    @Override
    public List<Map<String, Object>> findTradeDetail(String custId, String analysisType) {
        List<Map<String, Object>> list= null;
        if("D".equals(analysisType)){
            list = custIncomeDetailDao.queryDataByDays(custId);
        }else {
            list = custIncomeDetailDao.queryDataByMonth(custId);
        }
        return list;
    }

    @Override
    public List<CustIncomeDetailPO> findDetails(String custId, String time, String type) {
        if(StringUtils.isEmpty(type)){
            return custIncomeDetailDao.fiinDetailByDay(custId, time);
        }else{
            return custIncomeDetailDao.fiinDetailByMonth(custId, time);
        }
    }
}
