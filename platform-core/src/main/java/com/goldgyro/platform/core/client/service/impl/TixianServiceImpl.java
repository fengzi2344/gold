package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.dao.*;
import com.goldgyro.platform.core.client.domain.CustIncome;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.*;
import com.goldgyro.platform.core.client.service.TixianService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class TixianServiceImpl implements TixianService {
    @Autowired
    private TxPaymentDao txPaymentDao;
    @Autowired
    private TxWithdrawDao txWithdrawDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    Environment env;
    @Autowired
    private CustIncomeDao custIncomeDao;
    @Autowired
    private CustIncomeDetailDao custIncomeDetailDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TxPayment doProcess(String custId, String inCardId, String outCardId, Double amount) {
        TxPayment txPayment = initPayment(custId, outCardId, amount);
        TxWithdraw txWithdraw = initWithdraw(custId, inCardId, amount, txPayment);
        try {
            txPayment = txPaymentDao.saveAndFlush(txPayment);
            txWithdrawDao.saveAndFlush(txWithdraw);
            return txPayment;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, Object> findParamMap(String consumeOrderId) {
        return txPaymentDao.findParamMap(consumeOrderId);
    }

    @Override
    public Map<String, Object> findWithDrawMap(String consumeOrderId) {
        return txWithdrawDao.findParamMap(consumeOrderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePayment(String consumeOrderId, String orderStatus, String msg) {
        txPaymentDao.updateStatus(orderStatus, msg, consumeOrderId);
        TxPayment payment = txPaymentDao.findOne(consumeOrderId);
        List fl = initDetail(payment);
        if(fl.isEmpty()){
            return;
        }
        CustIncomeDetailPO custIncomeDetailPO = (CustIncomeDetailPO) fl.get(1);
        String custName = fl.get(0).toString();
        CustIncomePO custIncomePO = initCustIncome(custIncomeDetailPO,custName);
        custIncomeDetailDao.saveAndFlush(custIncomeDetailPO);
        if(StringUtils.isEmpty(custIncomePO.getId())){
            custIncomeDao.saveAndFlush(custIncomePO);
        }else {
            custIncomeDao.updateIncomeAmt(custIncomePO.getTotalIncomeAmt(),custIncomePO.getId());
        }
    }

    private CustIncomePO initCustIncome(CustIncomeDetailPO detailPO, String custName) {
        CustIncomePO cip = custIncomeDao.findByCustId(detailPO.getCustId());
        if(cip == null){
            cip = new CustIncomePO();
            cip.setCustId(detailPO.getCustId());
            cip.setTransType("C");
            cip.setTotalIncomeAmt(detailPO.getIncomeAmt());
            cip.setCustName(custName);
            cip.setCreatedTime(new Timestamp(new Date().getTime()));
        }else{
            BigDecimal m = new BigDecimal(cip.getTotalIncomeAmt());
            cip.setTotalIncomeAmt(m.add(new BigDecimal(detailPO.getIncomeAmt())).doubleValue());
        }
        return cip;
    }

    /**
     * 初始化 收益明细记录
     * @param payment
     * @return
     */
    private List initDetail(TxPayment payment) {
        List list = new ArrayList();
        CustIncomeDetailPO detailPO = new CustIncomeDetailPO();
        CustomerPO customerPO = customerDao.findByCustId(payment.getCustId());
        CustomerPO fatherCustomer = customerDao.queryCustPo(payment.getCustId());
        if(fatherCustomer == null){
            return list;
        }
        BigDecimal diffRatio = caclDiffRatio(customerPO,fatherCustomer);
        detailPO.setTransType("D");
        detailPO.setIncomeAmt(new BigDecimal(payment.getPayAmount()).multiply(diffRatio).doubleValue());
        detailPO.setCustId(fatherCustomer.getCustId());
        detailPO.setTransIncomeId(payment.getConsumeOrderId());
        detailPO.setCreatedTime(new Timestamp(new Date().getTime()));
        list.add(fatherCustomer.getCustName());
        list.add(detailPO);
        return list;
    }

    private BigDecimal caclDiffRatio(CustomerPO c1, CustomerPO c2) {
        if(StringUtils.isEmpty(c1.getCustLevelSample()) || StringUtils.isEmpty(c2.getCustLevelSample())){
            return null;
        }
        BigDecimal b = null;
        if(c1.getCustLevelSample().equals(c2.getCustLevelSample())){
            b = new BigDecimal(env.getProperty("goldgyro.sameLevel"));
        }else{
            if("VIP".equals(c1.getCustLevelSample())){
                b =  new BigDecimal(env.getProperty("goldgyro.agent")).subtract(new BigDecimal(env.getProperty("goldgyro.vip")));
            }else if("NORMAL".equals(c1.getCustLevelSample())){
                if("VIP".equals(c2.getCustLevelSample())){
                    b =  new BigDecimal(env.getProperty("goldgyro.vip")).subtract(new BigDecimal(env.getProperty("goldgyro.normal")));
                }else if("MEMBER".equals(c2.getCustLevelSample())){
                    b =  new BigDecimal(env.getProperty("goldgyro.member")).subtract(new BigDecimal(env.getProperty("goldgyro.normal")));
                }else {
                    b =  new BigDecimal(env.getProperty("goldgyro.agent")).subtract(new BigDecimal(env.getProperty("goldgyro.normal")));
                }
            }else if("MEMBER".equals(c1.getCustLevelSample())){
                if("VIP".equals(c2.getCustLevelSample())){
                    b =  new BigDecimal(env.getProperty("goldgyro.vip")).subtract(new BigDecimal(env.getProperty("goldgyro.member")));
                }else if("AGENT".equals(c2.getCustLevelSample())){
                    b =  new BigDecimal(env.getProperty("goldgyro.agent")).subtract(new BigDecimal(env.getProperty("goldgyro.member")));
                }
            }
        }
        return b;
    }

    @Override
    @Transactional
    public void updateWithdraw(String withdrawOrderId, String orderStatus, String msg) {
        txWithdrawDao.updateStatus(orderStatus, msg, withdrawOrderId);
    }

    @Override
    @Transactional
    public void updatePayTbMsg(String tbMsg, String consumeOrderId) {
        this.txPaymentDao.updatePayTbMsg(tbMsg, consumeOrderId);
    }

    @Override
    @Transactional
    public void updateWithdrawTbMsg(String tbMsg, String consumeOrderid) {
        this.txWithdrawDao.updateWithdrawTbMsg(tbMsg, consumeOrderid);

    }

    @Override
    public boolean validate(String custId, String inCardId, String outCardId) {
        List<CustomerPO> customerPOList = customerDao.findCustomer(custId,inCardId,outCardId);
        if(customerPOList == null || customerPOList.size() == 0)
            return false;
        return true;
    }

    private TxWithdraw initWithdraw(String custId, String inCardId, double amount, TxPayment pxPayment) {
        TxWithdraw withdraw = new TxWithdraw();
        withdraw.setWithdrawOrderId(UUID.randomUUID().toString().replaceAll("-", ""));
        withdraw.setCustId(custId);
        withdraw.setConsumeOrderId(pxPayment.getConsumeOrderId());
        withdraw.setAmount(amount - pxPayment.getFee());
        withdraw.setCardId(inCardId);
        withdraw.setCreateTime(new Timestamp(new Date().getTime()));
        withdraw.setDrawStatus("INIT");
        return withdraw;
    }

    private TxPayment initPayment(String custId, String outCardId, Double amount) {
        BigDecimal feeRatio = caclRatio(custId);
        TxPayment payment = new TxPayment();
        payment.setCustId(custId);
        payment.setConsumeOrderId(UUID.randomUUID().toString().replaceAll("-", ""));
        payment.setPayAmount(amount);
        payment.setCardId(outCardId);
        payment.setFee((new BigDecimal(amount).multiply(feeRatio)).doubleValue());
        payment.setOrderStatus("INIT");
        payment.setCreateTime(new Timestamp(new Date().getTime()));
        return payment;
    }

    private BigDecimal caclRatio(String custId) {
        CustomerPO customerPo = customerDao.findByCustId(custId);
        BigDecimal ratio = new BigDecimal(env.getProperty("goldgyro.normal"));
        if (StringUtils.isEmpty(customerPo.getCustLevelSample()) || "NORMAL".equals(customerPo.getCustLevelSample())) {
//            ratio = Double.valueOf(env.getProperty("goldgyro.normal"));
        } else if ("VIP".equals(customerPo.getCustLevelSample())) {
            ratio = new BigDecimal(env.getProperty("goldgyro.vip"));
        } else if ("AGENT".equals(customerPo.getCustLevelSample())) {
            ratio = new BigDecimal(env.getProperty("goldgyro.agent"));
        } else if("MEMBER".equals(customerPo.getCustLevelSample())){
            ratio = new BigDecimal(env.getProperty("goldgyro.member"));
        }
        return ratio;
    }
}
