package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.*;
import com.goldgyro.platform.core.client.domain.CustIncome;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.*;
import com.goldgyro.platform.core.client.service.TradeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Service
public class TradeServiceImpl implements TradeService {
    @Autowired
    private TradeDao tradeDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private CustIncomeDao custIncomeDao;

    @Autowired
    private BonusPointsDao bonusPointsDao;

    @Autowired
    private BonusPointsDetailDao bonusPointsDetailDao;

    @Autowired
    private CustIncomeDetailDao custIncomeDetailDao;

    @Override
    @Transactional
    public void save(Trade trade) {
        tradeDao.saveAndFlush(trade);
    }

    @Override
    public Trade findByOrderId(String orderNo) {
        return tradeDao.findOne(orderNo);
    }

    @Override
    @Transactional
    public void updateFeeAnStatus(Trade trade, String type) {
        tradeDao.saveAndFlush(trade);
        customerDao.updateCustType(type, trade.getCustId());
        CustomerPO self = customerDao.findByCustId(trade.getCustId());
        if (self != null && !StringUtils.isEmpty(self.getInviterId())) {
            CustomerPO father = customerDao.findByCustMobile(self.getInviterId());
            if (father != null) {
                //推荐人发300元
                CustIncomePO fatherIncome = processIncome(father,"VIP".equals(type)?"30000":"20000");
                custIncomeDao.saveAndFlush(fatherIncome);
                CustIncomeDetailPO custIncomeDetailPO = new CustIncomeDetailPO();
                custIncomeDetailPO.setDescription("购买会员");
                custIncomeDetailPO.setIncomeAmt("VIP".equals(type)?Double.valueOf("30000"):Double.valueOf("20000"));
                custIncomeDetailPO.setCreatedTime(new Timestamp(new Date().getTime()));
                custIncomeDetailPO.setTransIncomeId(fatherIncome.getId());
                custIncomeDetailPO.setTransType("C");
                custIncomeDetailPO.setCustId(father.getCustId());
                custIncomeDetailDao.saveAndFlush(custIncomeDetailPO);
                CustomerPO grandFather = customerDao.findByCustMobile(father.getInviterId());
                if (grandFather != null) {
                    //推荐人的推荐人发200元
                    CustIncomePO grandFatherIncome = processIncome(grandFather,"VIP".equals(type)?"20000":"10000");
                    custIncomeDao.saveAndFlush(grandFatherIncome);
                    CustIncomeDetailPO custIncomeDetailPO1 = new CustIncomeDetailPO();
                    custIncomeDetailPO1.setDescription("充值VIP奖励");
                    custIncomeDetailPO1.setIncomeAmt("VIP".equals(type)?Double.valueOf("20000"):Double.valueOf("10000"));
                    custIncomeDetailPO1.setCreatedTime(new Timestamp(new Date().getTime()));
                    custIncomeDetailPO1.setTransIncomeId(fatherIncome.getId());
                    custIncomeDetailPO1.setTransType("C");
                    custIncomeDetailPO1.setCustId(grandFather.getCustId());
                    custIncomeDetailDao.saveAndFlush(custIncomeDetailPO1);
                }
            }
        }
        Timestamp now = new Timestamp(new java.util.Date().getTime());
        BonusPoints bonusPoints = bonusPointsDao.findByCustId(trade.getCustId());
        if(bonusPoints != null){
            bonusPoints.setSum(bonusPoints.getSum()+88);
        }else {
            bonusPoints = new BonusPoints(trade.getCustId(),88,now);
            bonusPoints.setId(UUID.randomUUID().toString().replaceAll("-",""));
        }
        BonusPointsDetail bonusPointsDetail = new BonusPointsDetail(bonusPoints.getId(),88,"升级VIP奖励",now);
        bonusPointsDetail.setId(UUID.randomUUID().toString().replaceAll("-",""));
        bonusPointsDao.saveAndFlush(bonusPoints);
        bonusPointsDetailDao.saveAndFlush(bonusPointsDetail);
    }

    private CustIncomePO processIncome(CustomerPO father, String money) {
        CustIncomePO custIncome = custIncomeDao.findByCustId(father.getCustId());
        if (custIncome != null) {
            BigDecimal b1 = new BigDecimal(String.valueOf(custIncome.getTotalIncomeAmt()));
            custIncome.setTotalIncomeAmt(b1.add(new BigDecimal(money)).doubleValue());
            custIncomeDao.updateIncomeAmt(custIncome.getTotalIncomeAmt(), custIncome.getId());
        } else {
            custIncome = new CustIncomePO();
            custIncome.setTotalIncomeAmt(Double.valueOf(money));
            custIncome.setCustId(father.getCustId());
            custIncome.setCreatedTime(new Timestamp(new java.util.Date().getTime()));
            custIncome.setCustName(father.getCustName());
            custIncome.setTransType("C");
        }
        return custIncome;
    }
}
