package com.goldgyro.platform.core.client.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.dao.BankCardDao;
import com.goldgyro.platform.core.client.dao.MerchantDao;
import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.entity.BankCardPO;
import com.goldgyro.platform.core.client.entity.MerchantPO;
import com.goldgyro.platform.core.client.service.BankCardService;

@Service
public class BankCardServiceImpl implements BankCardService{
	Logger logger = Logger.getLogger(BankCardServiceImpl.class);
	
	@Autowired
	private BankCardDao custBankCardDao;
	@Autowired
	private MerchantDao merchantDao;
	
	
	/**
	 * 查询卡的正在执行的计划的还款进度
	 * @return
	 */
	public List<Map<String, Object>> paymentScheduleList(String custId, String cardNo) {
		return custBankCardDao.paymentScheduleList(cardNo, custId, cardNo);
	}
	
	
	/**
	 * 通过ID获取卡信息
	 * @param id
	 */
	public BankCard findBankCardById(String id) {
		return toBankCard(custBankCardDao.findBankCardById(id));
	}
	
	@Override
	public List<BankCard> findByCustId(String custId) {
		List<BankCard> bCardList = new ArrayList<BankCard>();
		
		List<BankCardPO> custBankCardList = custBankCardDao.findByCustIdOrderByCreatedTimeAsc(custId);
		for(BankCardPO bCardPO : custBankCardList) {
			bCardList.add(toBankCard(bCardPO));
		}
		
		return bCardList;
	}
	
	public BankCard findBankCardByCustIdAndAccountCode(String custId, String bankCardCode) {
		return toBankCard(custBankCardDao.findBankCardByCustIdAndAccountCode(custId, bankCardCode));
	}

	/**
	 * 存储卡信息并生成虚拟商户
	 */
	@Override
	@Transactional
	public void save(BankCard custBankCard , boolean isWriteMerchant) {
		if(custBankCard == null) {
			logger.error("");
			return;
		}
		
		//卡
		BankCardPO custBankCardPO = new BankCardPO();
		BeanUtils.copyProperties(custBankCard, custBankCardPO);
		
		if(StringUtils.isEmpty(custBankCardPO.getId())){
			custBankCardPO.setId(UUIDUtils.getUUIDNum()) ;
		}
		
		custBankCardDao.saveAndFlush(custBankCardPO);
		
		if(!isWriteMerchant) {
			return;
		}
		//商户
		MerchantPO merchantPO = new MerchantPO();
		merchantPO.setId(UUIDUtils.getUUIDNum());
		merchantPO.setCustId(custBankCardPO.getCustId());
		merchantPO.setMerchantCode(custBankCardPO.getCustId());
		merchantPO.setMerchantName(custBankCardPO.getAccountName()+"商贸有限责任公司");
		merchantPO.setMerchentAddress("成都");
		merchantPO.setValid("Y");
		merchantPO.setMerchantStatus("INIT");
		
		merchantDao.saveAndFlush(merchantPO);
	}
	

	@Override
	@Transactional
	public boolean deleteByCustId(String custId) {
		custBankCardDao.deleteByCustId(custId);
		return true;
	}

	public void delete(BankCard custBankCard) {
		//custDao.delete(cust);
	}

	@Override
	public List<Map<String, Object>> findScheduleList(String custId) {
		return custBankCardDao.findScheduleList(custId);
	}

	@Override
	@Transactional
	public void deleteByCardNo(String cardNo) {
		custBankCardDao.deleteByCardNo(cardNo);
	}

	@Override
	public boolean findRunningPlan(String cardNo) {
		List list = custBankCardDao.existRunningPlan(cardNo);
		return list.isEmpty();
	}

	@Override
	public BankCard findByMerchantCode(String merchantCode) {
		List<BankCardPO> bankCardList = custBankCardDao.findByMerchantCode(merchantCode);
		if(bankCardList.isEmpty()){
			return null;
		}
		BankCard bankCard = new BankCard();
		BeanUtils.copyProperties(bankCardList.get(0),bankCard);
		return bankCard;
	}

	@Override
	public Map<String, Object> queryParams(String cardId) {
		return custBankCardDao.queryParams(cardId);
	}

	@Override
	public BankCardPO findByOpenOrderId(String openOrderId) {
		return custBankCardDao.findByOpenOrderId(openOrderId);
	}

	@Override
	@Transactional
	public void saveBankCard(BankCardPO bankCardPO) {
		custBankCardDao.saveAndFlush(bankCardPO);
	}

	private BankCard toBankCard(BankCardPO custBankCardPO) {
		if(custBankCardPO == null) {
			return null;
		}
		
		BankCard custBankCard = new BankCard();
		BeanUtils.copyProperties(custBankCardPO, custBankCard);
		
		return custBankCard;
	}
}
