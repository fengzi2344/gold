package com.goldgyro.platform.core.client.service.impl;

import java.sql.Timestamp;
import java.util.*;

import com.goldgyro.platform.core.client.dao.*;
import com.goldgyro.platform.core.client.domain.CustIncome;
import com.goldgyro.platform.core.client.domain.CustomerVO;
import com.goldgyro.platform.core.client.entity.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.goldgyro.platform.base.lang.Consts;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.comm.utils.BeanMapUtils;
import com.goldgyro.platform.core.sys.domain.AccountProfile;

@Service
public class CustomerServiceImpl implements CustomerService {
    Logger logger = Logger.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerDao custDao;

    @Autowired
    private CustIncomeDao custIncomeDao;

    @Autowired
    private RedpackDao redpackDao;

    @Autowired
    private BonusPointsDao bonusPointsDao;

    @Autowired
    private BonusPointsDetailDao bonusPointsDetailDao;

    @Override
    public Customer findByCustId(String custId) {
//		return setCustRoles(toCustomer(custDao.findByCustId(custId)));
        return toCustomer(custDao.findByCustId(custId));
    }

    @Override
    public Customer findByCustMobile(String custMobile) {
        return toCustomer(custDao.findByCustMobile(custMobile));
    }

    public AccountProfile getProfileByCustMobile(String custMobile) {
        Customer cust = this.findByCustMobile(custMobile);
        AccountProfile u = null;

        Assert.notNull(cust, "账户不存在");

        cust.setLastLogin(Calendar.getInstance().getTime());

        u = BeanMapUtils.copyPassport(cust);


        return u;
    }

    @Override
    @Transactional
    public void save(Customer cust) {
        if (cust == null) {
            logger.error("客户信息是空的");
            return;
        }

        CustomerPO custPO = new CustomerPO();
        BeanUtils.copyProperties(cust, custPO);

        if (StringUtils.isEmpty(custPO.getId())) {
            custPO.setId(UUIDUtils.getUUID());
        }

        custDao.saveAndFlush(custPO);
    }

    @Override
    public boolean deleteByCustId(String custId) {
        return false;
    }

    public void delete(Customer cust) {
        //custDao.delete(cust);
    }


    /**
     * 登录
     */
    @Override
    public AccountProfile login(String mobileNo, String password) {
        Customer cust = this.findByCustMobile(mobileNo);
        AccountProfile u = null;

        Assert.notNull(cust, "账户不存在");

        Assert.state(cust.getCustStatus() != Consts.STATUS_CLOSED, "您的账户已被封禁");

        if (StringUtils.equals(cust.getCustPassword(), password)) {
            cust.setLastLogin(Calendar.getInstance().getTime());

            u = BeanMapUtils.copyPassport(cust);
        }

        return u;
    }

    @Override
    public Map<String, Object> queryCustomerAnaysisData(String custId) {
        return custDao.queryCustomerAnalysisData(custId);
    }

    @Override
    @Transactional
    public void saveAndLinked(Customer cust) {
        if (cust == null) {
            logger.error("客户信息为空");
            return;
        }

        CustomerPO custPO = new CustomerPO();
        BeanUtils.copyProperties(cust, custPO);

        if (StringUtils.isEmpty(custPO.getId())) {
            custPO.setId(UUIDUtils.getUUID());
        }

        custDao.saveAndFlush(custPO);
        Timestamp now = new Timestamp(new Date().getTime());
        CustIncomePO custIncomePO = new CustIncomePO();
        custIncomePO.setTransType("C");
        custIncomePO.setCustId(cust.getCustId());
        custIncomePO.setCreatedTime(now);
        custIncomePO.setTotalIncomeAmt(0);
        Redpack redPack = new Redpack();
        redPack.setBalance("0");
        redPack.setCustId(cust.getCustId());
        redPack.setUpdateTime(now);
        redPack.setUnpackNum(1);
        custIncomeDao.saveAndFlush(custIncomePO);
        Redpack friend = redpackDao.findRedpackByMobile(custPO.getInviterId());
        if(friend != null){
            friend.setUnpackNum((friend.getUnpackNum()==null?0:friend.getUnpackNum())+1);
            redpackDao.saveAndFlush(friend);
        }
        redpackDao.save(redPack);
        BonusPoints bonusPoints = new BonusPoints(cust.getCustId(),10,now);
        bonusPoints.setId(UUID.randomUUID().toString().replaceAll("-",""));
        BonusPointsDetail bonusPointsDetail = new BonusPointsDetail(bonusPoints.getId(),10,"注册用户奖励",now);
        bonusPointsDetail.setId(UUID.randomUUID().toString().replaceAll("-",""));
        bonusPointsDao.saveAndFlush(bonusPoints);
        bonusPointsDetailDao.saveAndFlush(bonusPointsDetail);
        CustomerPO inviteMan = custDao.findByCustMobile(custPO.getInviterId());
        if(inviteMan !=null){
            BonusPoints bp = bonusPointsDao.findByCustId(inviteMan.getCustId());
            if(bp!=null){
                bp.setSum(bp.getSum()+8);
                bp.setUpdateTime(now);
            }else{
                bp = new BonusPoints(inviteMan.getCustId(),8,now);
                bp.setId(UUID.randomUUID().toString().replaceAll("-",""));
            }
            BonusPointsDetail bpd = new BonusPointsDetail(bp.getId(),8,"邀请用户"+custPO.getCustMobile()+"奖励",now);
            bpd.setId(UUID.randomUUID().toString().replaceAll("-",""));
            bonusPointsDao.saveAndFlush(bp);
            bonusPointsDetailDao.saveAndFlush(bpd);
        }
    }

    @Override
    public Map<String, Object> queryParamMap(String custId) {
        return custDao.queryParamMap(custId);
    }

    @Override
    public void modifyMerchant(String code) {
        custDao.modifyMerchant(code);
    }

    @Override
    public int findNumByLevelCode(String levelCode) {
        return custDao.findNumByLevelCode(levelCode,levelCode+"%");
    }

    @Override
    public List<CustomerPO> findCustList() {
        return custDao.findCustList();
    }

    @Override
    public List<CustomerPO> findChilList(String custMobile) {
        return custDao.findAllByInviterId(custMobile);
    }

    @Override
    @Transactional
    public void saveList(List<CustomerPO> customerList) {
        this.custDao.save(customerList);
    }

    @Override
    public Integer findMaxCode() {
        return custDao.findMaxCode();
    }

    @Override
    public List<CustomerVO> findZtList(String custId) {
        List<CustomerPO> list = custDao.findAllByInviterId(custId);
        List<CustomerVO> voList = new ArrayList<>();
        for(CustomerPO po:list){
            CustomerVO vo = new CustomerVO();
            BeanUtils.copyProperties(po,vo);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public List<CustomerVO> findVipList(String custId) {
        List<CustomerPO> list = custDao.findVipList(custId);
        List<CustomerVO> voList = new ArrayList<>();
        for(CustomerPO po:list){
            CustomerVO vo = new CustomerVO();
            BeanUtils.copyProperties(po,vo);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public List<CustomerPO> findNotNormal(String levelCode) {
        return custDao.findNotNormal(levelCode);
    }

    @Override
    public List<CustomerVO> findDirectValidList(String mobile) {
        List<CustomerPO> list = custDao.findDirectValidList(mobile);
        List<CustomerVO> voList = new ArrayList<>();
        for(CustomerPO po:list){
            CustomerVO vo = new CustomerVO();
            BeanUtils.copyProperties(po,vo);
            voList.add(vo);
        }
        return voList;
    }

    private Customer toCustomer(CustomerPO custPO) {
        if (custPO == null) {
            return null;
        }

        Customer cust = new Customer();
        BeanUtils.copyProperties(custPO, cust);

        return cust;
    }
}
