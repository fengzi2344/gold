package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.CustIncomeDao;
import com.goldgyro.platform.core.client.dao.CustIncomeDetailDao;
import com.goldgyro.platform.core.client.dao.RedpackDao;
import com.goldgyro.platform.core.client.dao.RedpackDetailDao;
import com.goldgyro.platform.core.client.entity.CustIncomeDetailPO;
import com.goldgyro.platform.core.client.entity.CustIncomePO;
import com.goldgyro.platform.core.client.entity.Redpack;
import com.goldgyro.platform.core.client.entity.RedpackDetail;
import com.goldgyro.platform.core.client.service.RedpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class RedpackServiceImpl implements RedpackService {
    @Autowired
    private RedpackDao redpackDao;

    @Autowired
    private RedpackDetailDao redpackDetailDao;

    @Autowired
    private CustIncomeDao custIncomeDao;

    @Autowired
    private CustIncomeDetailDao custIncomeDetailDao;
    @Override
    public Redpack findByCustId(String custId) {
        return redpackDao.findRedpackByCustId(custId);
    }

    @Override
    @Transactional
    public void transfer(Redpack redpack, String money) {
        BigDecimal b1 = new BigDecimal(money);
        BigDecimal v = new BigDecimal(redpack.getBalance()).subtract(b1);
        redpack.setBalance(v.toString());
        redpackDao.saveAndFlush(redpack);
        CustIncomePO custIncome = custIncomeDao.findByCustId(redpack.getCustId());
        if(custIncome == null){
            custIncome = new CustIncomePO();
            custIncome.setCustId(redpack.getCustId());
            custIncome.setCreatedTime(new Timestamp(new java.util.Date().getTime()));
            custIncome.setTransType("C");
            custIncome.setTotalIncomeAmt(Double.valueOf(money));
            custIncome.setId(UUID.randomUUID().toString().replaceAll("-",""));
            custIncomeDao.saveAndFlush(custIncome);
        }else {
            BigDecimal b2 = new BigDecimal(custIncome.getTotalIncomeAmt());
            BigDecimal v2 = b2.add(b1);
            custIncomeDao.updateIncomeAmt(v2.doubleValue(),custIncome.getId());
        }
        CustIncomeDetailPO custIncomeDetailPO = new CustIncomeDetailPO();
        custIncomeDetailPO.setCustId(redpack.getCustId());
        custIncomeDetailPO.setIncomeAmt(Double.valueOf(money));
        custIncomeDetailPO.setTransIncomeId(custIncome.getId());
        custIncomeDetailPO.setDescription("红包提现");
        custIncomeDetailPO.setCreatedTime(new Timestamp(new java.util.Date().getTime()));
        custIncomeDetailDao.saveAndFlush(custIncomeDetailPO);
    }

    @Override
    @Transactional
    public int[] doUnpack(Redpack redpack) {
        int n = 100;
        Random random = new Random();
        n += random.nextInt(200);
        RedpackDetail detail = new RedpackDetail();
        detail.setId(UUID.randomUUID().toString().replaceAll("-",""));
        detail.setCreateTime(new Timestamp(new java.util.Date().getTime()));
        detail.setNote("邀请用户");
        detail.setSum(n);
        detail.setRedpackId(redpack.getId());
        redpackDetailDao.saveAndFlush(detail);
        redpack.setUnpackNum(redpack.getUnpackNum()-1);

        redpack.setBalance(String.valueOf(new BigDecimal(redpack.getBalance()==null?"0":redpack.getBalance()).intValue()+n));
        redpackDao.saveAndFlush(redpack);
        return new int[]{n,redpack.getUnpackNum()};
    }

}
