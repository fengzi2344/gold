package com.goldgyro.platform.core.client.service.impl;

import java.sql.Timestamp;
import java.util.*;

import javax.jws.Oneway;
import javax.transaction.Transactional;

import com.goldgyro.platform.core.client.dao.*;
import com.goldgyro.platform.core.client.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goldgyro.platform.core.client.service.WithdrawPlanService;

@Service
public class WithdrawPlanServiceImpl implements WithdrawPlanService {

    @Autowired
    private WithdrawPlanDao withdrawPlanDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private BonusPointsDao bonusPointsDao;

    @Autowired
    private BonusPointsDetailDao bonusPointsDetailDao;

    @Autowired
    private PaymentApplyDao paymentApplyDao;

    @Autowired
    private FeeRateDao feeRateDao;

    @Override
    @Transactional
    public void save(WithdrawPlanPO withdrawPlanPO) {
        withdrawPlanDao.saveAndFlush(withdrawPlanPO);
    }

    @Transactional
    public void updateStatus(String id, String status) {
        withdrawPlanDao.updateStatus(status, id);
        if ("SUCCESS".equalsIgnoreCase(status)) {
            Map<String, List> map = doBonus(id);
            if (map != null) {
                List<BonusPoints> bpList = map.get("bpList");
                List<BonusPointsDetail> bpdList = map.get("bpdList");
                this.bonusPointsDao.save(bpList);
                this.bonusPointsDetailDao.save(bpdList);
            }
        }
    }

    private Map<String, List> doBonus(String id) {
        PaymentApplyPO paymentApply = paymentApplyDao.queryByWithdrawId(id);
        if (paymentApply == null) {
            //判断是不是一条计划的最后一笔提现
            return null;
        }
        Map<String, List> map = new HashMap<>();
        CustomerPO customer = customerDao.queryByWithdrawId(id);
        BonusPoints bp = bonusPointsDao.findByCustId(customer.getCustId());
        List<BonusPoints> bpList = new ArrayList<>();
        List<BonusPointsDetail> bpdList = new ArrayList<>();
        Timestamp now = new Timestamp(new Date().getTime());
        int bonus = initBonus(paymentApply);
        if (bp != null) {
            bp.setSum(bp.getSum() + bonus);
            bp.setUpdateTime(now);
        } else {
            bp = new BonusPoints(customer.getCustId(), bonus, now);
            bp.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        }
        BonusPointsDetail bpd = new BonusPointsDetail(bp.getId(), bonus, "完成还款奖励", now);
        bpd.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        bpList.add(bp);
        bpdList.add(bpd);
        try {
            Map<String, Object> pMap = paymentApplyDao.findMap(id);
            int num1 = Integer.valueOf(pMap.get("num1").toString());
            int num2 = Integer.valueOf(pMap.get("num2").toString());
            if (num1 == 0 && num2 == 0 && !StringUtils.isEmpty(customer.getInviterId())) {
                CustomerPO inviterCustomer = customerDao.findByCustMobile(customer.getInviterId());
                if (inviterCustomer != null) {
                    BonusPoints bp1 = bonusPointsDao.findByCustId(inviterCustomer.getCustId());
                    if (bp1 != null) {
                        bp1.setSum(bp1.getSum() + 28);
                    } else {
                        bp1 = new BonusPoints(inviterCustomer.getCustId(),28,now);
                        bp1.setId(UUID.randomUUID().toString().replaceAll("-",""));
                    }
                    BonusPointsDetail bpd1 = new BonusPointsDetail(bp1.getId(), bonus, "完成还款奖励", now);
                    bpd1.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    bpList.add(bp1);
                    bpdList.add(bpd1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("bpList", bpList);
        map.put("bpdList", bpdList);

        return map;
    }

    public static void main(String[] args) {
        PaymentApplyPO paymentApplyPO = new PaymentApplyPO();
        paymentApplyPO.setCustId("9a639aeaa46811e89abc");
        paymentApplyPO.setPaymentAmt(12400);
        paymentApplyPO.setPaymentDate("2018-08-31,2018-09-01,2018-09-02,2018-09-03,2018-09-04,2018-09-05,2018-09-06,2018-09-07,2018-09-08,2018-09-09");
        System.out.println(new WithdrawPlanServiceImpl().initBonus(paymentApplyPO));
    }
    private int initBonus(PaymentApplyPO paymentApply) {
        FeeRatePO feeRate = feeRateDao.findByCustId(paymentApply.getCustId());
        double amount = paymentApply.getPaymentAmt();
        double rate = 0;
        if(feeRate != null && (rate=feeRate.getCfeeRate()) != 0){
            return (int) ((amount+paymentApply.getPaymentDate().split(",").length)/(1.0-rate) -amount);
        }
        int bonus = 0;
        if (amount <= 10000) {
            bonus = 5;
        } else if (amount <= 20000) {
            bonus = 10;
        } else if (amount <= 30000) {
            bonus = 20;
        } else if (amount <= 40000) {
            bonus = 30;
        } else if (amount <= 60000) {
            bonus = 60;
        } else if (amount <= 80000) {
            bonus = 80;
        } else if (amount <= 100000) {
            bonus = 100;
        } else {
            bonus = 180;
        }
        return bonus;
    }

    /**
     * 待提现信息获取
     *
     * @return
     */
    @Transactional
    public List<Map<String, Object>> findWithdrawPlanList() {
        return withdrawPlanDao.findWithdrawPlanList();
    }


    /**
     * 获取还款申请
     *
     * @param applyId
     * @param strPaymentDate
     * @return
     */
    public Map<String, Object> findWithdrawPlan(String applyId, String strPaymentDate) {
        return withdrawPlanDao.findWithdrawPlan(applyId, strPaymentDate);
    }

    @Override
    @Transactional
    public void cancelByTransNo(String transNo) {
        withdrawPlanDao.cancelByTransNo(transNo);
    }

    @Override
    public WithdrawPlanPO findById(String withdrawOrderId) {
        return withdrawPlanDao.findOne(withdrawOrderId);
    }

    @Override
    public WithdrawPlanPO findByWorkId(String workId) {
        return withdrawPlanDao.findByWorkId(workId);
    }
}
