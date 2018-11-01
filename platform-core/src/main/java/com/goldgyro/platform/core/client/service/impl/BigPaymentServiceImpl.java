package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.BigPaymentDao;
import com.goldgyro.platform.core.client.dao.CustIncomeDao;
import com.goldgyro.platform.core.client.dao.CustIncomeDetailDao;
import com.goldgyro.platform.core.client.dao.CustomerDao;
import com.goldgyro.platform.core.client.domain.CustIncomeDetail;
import com.goldgyro.platform.core.client.entity.BigPayment;
import com.goldgyro.platform.core.client.entity.CustIncomeDetailPO;
import com.goldgyro.platform.core.client.entity.CustIncomePO;
import com.goldgyro.platform.core.client.entity.CustomerPO;
import com.goldgyro.platform.core.client.service.BigPaymentService;
import com.goldgyro.platform.core.client.service.CustIncomeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class BigPaymentServiceImpl implements BigPaymentService {

    @Autowired
    private BigPaymentDao bigPaymentDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private CustIncomeDao custIncomeDao;
    @Autowired
    private CustIncomeDetailDao custIncomeDetailDao;

    @Autowired
    private Environment env;

    @Override
    public void saveBigPayment(BigPayment bigPayment) {
        bigPaymentDao.saveAndFlush(bigPayment);
    }

    @Override
    public BigPayment findByWorkId(String workId) {
        return bigPaymentDao.findByWorkId(workId);
    }

    @Override
    @Transactional
    public void process(String workId, String orderStatus) {
        BigPayment bigPayment = bigPaymentDao.findByWorkId(workId);
        if ("01".equals(orderStatus)) {
            bigPayment.setStatus("SUCCESS");
            bigPaymentDao.saveAndFlush(bigPayment);
            //提现成功 分润
            CustomerPO self = customerDao.findByCustId(bigPayment.getCustId());
            String mobile = "";
            if (StringUtils.isEmpty((mobile = self.getFatherMobile()))) {
                return;
            }
            CustomerPO father = customerDao.findByCustMobile(mobile);
            String l1 = "";
            String l2 = "";
            if (father == null || StringUtils.isEmpty((l1 = father.getCustLevelSample())) || (l2 = self.getCustLevelSample()).equals(father.getCustLevelSample())) {
                return;
            }
            BigDecimal diffRatio = calcDif(l1, l2);
            if (diffRatio.doubleValue() == 0) {
                return;
            }
            BigDecimal money = new BigDecimal(bigPayment.getPayAmount()).multiply(diffRatio);
            Date now = new Date();
            CustIncomePO custIncomePO = custIncomeDao.findByCustId(father.getCustId());
            if(custIncomePO == null){
                custIncomePO = new CustIncomePO();
                custIncomePO.setId(UUID.randomUUID().toString().replaceAll("-",""));
                custIncomePO.setTransType("C");
                custIncomePO.setCreatedTime(new Timestamp(now.getTime()));
                custIncomePO.setTotalIncomeAmt(money.doubleValue());
                custIncomePO.setCustId(bigPayment.getCustId());
            }else {
                custIncomePO.setTotalIncomeAmt(new BigDecimal(custIncomePO.getTotalIncomeAmt()).add(money).doubleValue());
            }
            CustIncomeDetailPO custIncomeDetailPO = new CustIncomeDetailPO();
            custIncomeDetailPO.setCreatedTime(new Timestamp(now.getTime()));
            custIncomeDetailPO.setCustId(father.getCustId());
            custIncomeDetailPO.setTransIncomeId(custIncomePO.getId());
            custIncomeDetailPO.setIncomeAmt(money.doubleValue());
            custIncomeDetailPO.setDescription("刷卡收益");
            custIncomeDao.saveAndFlush(custIncomePO);
            custIncomeDetailDao.saveAndFlush(custIncomeDetailPO);
        } else if ("02".equals(orderStatus)) {
            bigPayment.setStatus("FAIL");
            bigPaymentDao.saveAndFlush(bigPayment);
        }

    }

    private BigDecimal calcDif(String l1, String l2) {
        BigDecimal b = new BigDecimal("0");
        BigDecimal m = new BigDecimal(env.getProperty("goldgyro.txmember"));
        BigDecimal v = new BigDecimal(env.getProperty("goldgyro.txvip"));
        BigDecimal a = new BigDecimal(env.getProperty("goldgyro.txagent"));
        if ("NORMAL".equals(l2)) {
            BigDecimal n = new BigDecimal(env.getProperty("goldgyro.txnormal"));
            if ("MEMBER".equals(l1)) {
                b = n.subtract(m);
            } else if ("VIP".equals(l1)) {
                b = n.subtract(v);
            } else {
                b = n.subtract(v);
            }
        } else if ("MEMBER".equals(l2)) {
            if ("VIP".equals(l1)) {
                b = m.subtract(v);
            } else if ("AGENT".equals(l1)) {
                b = m.subtract(a);
            }

        }/* else if ("VIP".equals(l2)) {
            if ("AGENT".equals(l1)) {
                b = v.subtract(a);
            }
        }*/
        return b;
    }
}
