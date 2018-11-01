package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.domain.CustomerVO;
import com.goldgyro.platform.core.client.entity.CustomerPO;
import com.goldgyro.platform.core.sys.domain.AccountProfile;

import java.util.List;
import java.util.Map;

/**
 * 客户服务类
 * @author wg2993
 * @version 2018/07/09
 */
public interface CustomerService {
	
	/**
	 * 查询客户信息
	 * @param custId
	 * @return
	 */
	public Customer findByCustId(String custId);
	
	/**
	 * 通过客户手机号号码获取客户信息
	 * @param custMobile
	 * @return
	 */
	public Customer findByCustMobile(String custMobile);
	
	/**
	 * 获取profile
	 * @param custMobile
	 * @return
	 */
	public AccountProfile getProfileByCustMobile(String custMobile);
	
	/**
	 * 注册客户
	 * @param cust
	 * @return boolean
	 */
	public void save(Customer cust);
	
	/**
	 * 删除客户信息
	 * @param custId
	 * @return
	 */
	public boolean deleteByCustId(String custId);
	
	/**
	 * 删除客户
	 * @param cust
	 * @return
	 */
	public void delete(Customer cust);
	
	/**
	 * 客户登录
	 * @param custId
	 * @param password
	 * @return
	 */
	public AccountProfile login(String custId, String password);


    Map<String,Object> queryCustomerAnaysisData(String custId);

    void saveAndLinked(Customer cust);

    Map<String,Object> queryParamMap(String custId);

	/**
	 * 实名认证成功，递归更新父节点团队数（+1）及直接父节点直推数（+1）
	 * @param code
	 */
	void modifyMerchant(String code);

	int findNumByLevelCode(String levelCode);

	List<CustomerPO> findCustList();

	List<CustomerPO> findChilList(String custMobile);

	void saveList(List<CustomerPO> customerList);

	Integer findMaxCode();

    List<CustomerVO> findZtList(String custId);

	List<CustomerVO> findVipList(String custId);

	List<CustomerPO> findNotNormal(String levelCode);

	List<CustomerVO> findDirectValidList(String mobile);
}
