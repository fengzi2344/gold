package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.BankCardDao;
import com.goldgyro.platform.core.client.dao.CustomerDao;
import com.goldgyro.platform.core.client.dao.MerchantDao;
import com.goldgyro.platform.core.client.dao.RedpackDao;
import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.BankCardPO;
import com.goldgyro.platform.core.client.entity.CustomerPO;
import com.goldgyro.platform.core.client.entity.MerchantPO;
import com.goldgyro.platform.core.client.service.MerchantService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    private MerchantDao merchantDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private BankCardDao bankCardDao;

    @Autowired
    private Environment env;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Override
    @Transactional
    public void persist(MerchantPO merchantPO, BankCard bankCard, Customer customer) {
        merchantDao.saveAndFlush(merchantPO);
        if (StringUtils.isEmpty(bankCard.getId())) {
            bankCard.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        }
        customer.setCustStatus("AUTH");
        CustomerPO customerPO = new CustomerPO();
        BeanUtils.copyProperties(customer, customerPO);
        customerDao.saveAndFlush(customerPO);
        BankCardPO bankCardPO = new BankCardPO();
        BeanUtils.copyProperties(bankCard, bankCardPO);
        bankCardPO.setDefaultCard("YES"); //设置实名认证的储蓄卡为默认提现收款账户
        bankCardDao.saveAndFlush(bankCardPO);
        customerDao.modifyMerchant(customer.getLevelCode());
        List<CustomerPO> list = customerDao.findAllByInviterIdAndCustStatus(customer.getInviterId(), "AUTH");
        if (!list.isEmpty() && list.size() >= 10) {
            //如果直推10人 普通用户享受会员的费率 但不参与分润
            CustomerPO c1 = customerDao.findByCustMobile(customer.getInviterId());
            if (c1 != null && "NORMAL".equals(c1.getCustLevelSample()) && (c1.getFreeMember() == null || !"1".equals(c1.getFreeMember()))) {
                c1.setFreeMember("1");
                ResponseEntity<Boolean> entity = restTemplateBuilder.build().getForEntity(env.getProperty("domainUrl") + "/alipay/updateRate?custId="
                        + c1.getCustId() + "&vipRate=" + env.getProperty("goldgyro.member"), Boolean.class);
                System.out.println(entity.getBody());
                customerDao.saveAndFlush(c1);
            }
        }
    }

    public MerchantPO findMerchantByCustId(String custId) {
        return merchantDao.findMerchantByCustId(custId);
    }

    public List<MerchantPO> findMerchant(String merchantStatus) {
        return merchantDao.findMerchantByMerchantStatus(merchantStatus);
    }

    @Transactional
    public void udpate(String merchantCode, String platMerchantCode, String status) {
        merchantDao.udpate(platMerchantCode, status, merchantCode);
    }

    @Override
    @Transactional
    public void processReistStatus(String merchantCode, String platMerchantCode, String status) {
        if ("REG_SUCCESS".equals(status)) {
            //注册成功
            merchantDao.udpate(platMerchantCode, status, merchantCode);
            customerDao.updateCustStatus("AUTH", merchantCode);
        } else if ("REG_FAIL".equals(status)) {
            merchantDao.udpate(null, status, merchantCode);
        }
    }
}
