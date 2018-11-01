package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.CustIncomeDao;
import com.goldgyro.platform.core.client.dao.IncomeDetailDao;
import com.goldgyro.platform.core.client.entity.CustIncomePO;
import com.goldgyro.platform.core.client.entity.IncomeDetail;
import com.goldgyro.platform.core.client.service.CustIncomeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class CustIncomeServiceImpl implements CustIncomeService {
    @Autowired
    private CustIncomeDao custIncomeDao;
    @Autowired
    private IncomeDetailDao incomeDetailDao;

    @Override
    public CustIncomePO findByCustId(String id) {
        return custIncomeDao.findByCustId(id);
    }

    @Override
    public void save(CustIncomePO custIncomePO) {
        custIncomeDao.saveAndFlush(custIncomePO);
    }

    @Override
    @Transactional
    public void updateIncomeAmt(String id, double incomeAmount) {
        custIncomeDao.updateIncomeAmt(incomeAmount, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdrawFromIncome(CustIncomePO custIncome, String money, String cardNo) throws Exception {
        IncomeDetail incomeDetail = new IncomeDetail();
        incomeDetail.setCustIncomeId(custIncome.getId());
        incomeDetail.setCardNo(cardNo);
        incomeDetail.setAmount(money);
        incomeDetail.setApplyTime(new Timestamp(new Date().getTime()));
        incomeDetail.setStatus("PROCESSING");
        BigDecimal b = new BigDecimal(custIncome.getTotalIncomeAmt());
        custIncomeDao.updateIncomeAmt(b.subtract(new BigDecimal(money.trim())).doubleValue(), custIncome.getId());
        incomeDetailDao.saveAndFlush(incomeDetail);
    }

    @Override
    public List<Map<String, Object>> findTradeDetail(String custId) {
        List<Object> list = custIncomeDao.findTradeDetail(custId);
        List<Map<String, Object>> mapList = new ArrayList();
        for (Object obj : list) {
            Map<String, Object> map = new HashMap<>();
            Object[] objects = (Object[]) obj;
            map.put("tradeTime", objects[0]);
            String money = objects[1].toString();
            if (!money.startsWith("-")) {
                money = "+" + money;
            }
            map.put("money", money);
            map.put("note", objects[2]);
            if(objects[3] != null && !StringUtils.isEmpty(objects[3].toString())){
                map.put("status","PROCESSING".equals(objects[3].toString())?"已受理":("SUCCESS".equals(objects[3].toString())?"已打款":"打款失败"));
            }
            mapList.add(map);
        }
        return mapList;
    }
}
