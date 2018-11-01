package com.goldgyro.platform.core.client.service;

import java.util.List;
import java.util.Map;

import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.entity.BankCardPO;

/**
 * 客户银行卡服务类
 * @author wg2993
 * @version 2018/07/10
 */
public interface BankCardService {
	
	
	/**
	 * 查询卡的正在执行的计划的还款进度
	 * @return
	 */
	public List<Map<String, Object>> paymentScheduleList(String custId, String cardNo) ;
	
	/**
	 * 通过ID获取卡信息
	 * @param id
	 */
	public BankCard findBankCardById(String id);
	/**
	 * 查找客户银行卡
	 * @param custId
	 * @return BankCardPO
	 */
	public List<BankCard> findByCustId(String custId);
	
	/**
	 * 查找 客户银行卡
	 * @param bankCardCode
	 * @return BankCardPO
	 */
	public BankCard findBankCardByCustIdAndAccountCode(String custId, String bankCardCode) ;
	
	/**
	 * 保存客户银行卡信息
	 * @param cust
	 * @return boolean
	 */
	public void save(BankCard cust, boolean isWriteMerchant);
	
	/**
	 * 删除客户银行卡信息
	 * @param custId
	 * @return
	 */
	public boolean deleteByCustId(String custId);
	
	/**
	 * 删除客户银行卡信息
	 * @param cust
	 * @return
	 */
	public void delete(BankCard cust);

	/**
	 * 查询用户 正在执行的信用卡还款进度
	 * @param custId
	 * @return
	 */
    List<Map<String,Object>> findScheduleList(String custId);

	/**
	 * 删除银行卡
	 * @param cardNo
	 */
	void deleteByCardNo(String cardNo);

	/**
	 * 是否执行中的计划关联此卡
	 * @param cardNo
	 * @return
	 */
	boolean findRunningPlan(String cardNo);

	/**
	 * 根据商户号回去默认储蓄卡
	 * @param merchantCode
	 * @return
	 */
    BankCard findByMerchantCode(String merchantCode);

    Map<String,Object> queryParams(String cardId);

	/**
	 * 根据openOrderId 获取银行卡信息
	 * @param openOrderId
	 * @return
	 */
	BankCardPO findByOpenOrderId(String openOrderId);

	void saveBankCard(BankCardPO bankCardPO);

}
