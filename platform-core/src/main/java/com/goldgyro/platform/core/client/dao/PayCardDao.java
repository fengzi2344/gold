package com.goldgyro.platform.core.client.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.goldgyro.platform.core.client.entity.PaymentApplyPO;

/**
 * 还卡DAO
 * @author wg2993
 *
 */
public interface PayCardDao  extends JpaRepository<PaymentApplyPO, String>, JpaSpecificationExecutor<PaymentApplyPO> {
	/**
	 * 获取待注册的商户信息
	 * @return
	 */
	@Query(nativeQuery=true,value="select DISTINCT vm.ID as id, vm.MERCHANT_CODE as merchantCode, ci.CUST_NAME as bankAccountName, cb.MOBILE_NO as phoneno, ci.ID_CARD_NO as idCardNo, cb.ACCOUNT_CODE as bankAccountNo, \r\n" + 
			"cb.BANK_NAME as bankName, cb.BANK_CARD_TYPE as bankCardType, cb.OPENING_SUB_BANK_NAME as bankSubName, vm.MERCHANT_NAME as merName, vm.MERCHENT_ADDRESS as merAddress, tfr.CFEE_RATE as txnRate, \r\n" + 
			"cb.OPENNING_BANK_PROVINCE as bankProvince, cb.OPENNING_BANK_CITY as bankCity, 100 as withdrawDepositSingleFee  from t_customer_info ci, t_bank_card cb , T_VIRTUAL_MERCHANT vm, T_TRANS_FEE_RATE tfr \r\n" + 
			"where cb.CUST_ID = ci.CUST_ID and ci.CUST_ID = vm.CUST_ID and ci.CUST_LEVEL_SAMPLE = tfr.CLEVEL_SAMPLE and cb.IS_DEFAULT = 'Y' and cb.BANK_CARD_TYPE='D' 	\r\n" + 
			"and tfr.TTYPE_CODE='C' and vm.MERCHANT_VALID='Y' and vm.MERCHANT_STATUS='INIT'  and ci.CUST_ID = ?")
	Map<String, Object> findBusiRegistrations(String custId);
	
	/**
	 * 更新注册状态
	 */
	@Modifying
	@Query(nativeQuery=true, value = "update T_VIRTUAL_MERCHANT vm set vm.MERCHANT_STATUS=? where vm.MERCHANT_CODE=?")
	void updateMerchantRegistStatus(@Param(value = "status")String status, @Param(value = "merchCode")String merchCode);
	
	/**
	 * 获取待开卡的信用卡信息
	 * @return
	 */
	@Query(nativeQuery=true, value = "select cb.ID as id, vm.PLAT_MERCHANT_CODE as platMerchantCode, cb.ACCOUNT_NAME as accountName, cb.BANK_CARD_TYPE as cardType, cb.ACCOUNT_CODE as cardNo, "
			+ "'ID' as certType , ci.ID_CARD_NO as certNo, cb.MOBILE_NO as phoneno, ci.CUST_ID as custId, \r\n" + 
			"cb.OPENNING_BANK_PROVINCE as bankProvince, cb.OPENNING_BANK_CITY as bankCity, cb.OPENING_SUB_BANK_NAME as bankSubName, cb.BANK_NAME as bankName \r\n" + 
			"from t_customer_info ci, t_bank_card cb, t_virtual_merchant vm \r\n" + 
			"where ci.CUST_ID = cb.CUST_ID and ci.CUST_ID = vm.CUST_ID and ci.CUST_ID = ? and cb.ACCOUNT_CODE = ? \r\n" + 
			"and cb.VALID = 'Y' and cb.OPEN_STATUS = 'INIT' and cb.BANK_CARD_TYPE='C' and cb.IS_DEFAULT = 'Y' and vm.MERCHANT_STATUS = 'REG_SUCCESS'")
	Map<String, Object> findCreditCardBind(String custId, String accountCode);
	
	
	/**
	 * 查询需要绑卡的信用卡信息
	 * @return
	 */
	@Query(nativeQuery=true, value = "SELECT \n" +
			"  cb.ID AS id,\n" +
			"  vm.PLAT_MERCHANT_CODE AS platMerchantCode,\n" +
			"  cb.ACCOUNT_NAME AS accountName,\n" +
			"  cb.BANK_CARD_TYPE AS cardType,\n" +
			"  cb.ACCOUNT_CODE AS cardNo,\n" +
			"  'ID' AS certType,\n" +
			"  ci.ID_CARD_NO AS certNo,\n" +
			"  cb.MOBILE_NO AS phoneno,\n" +
			"  ci.CUST_ID AS custId,\n" +
			"  cb.OPENNING_BANK_PROVINCE AS bankProvince,\n" +
			"  cb.OPENNING_BANK_CITY AS bankCity,\n" +
			"  cb.OPENING_SUB_BANK_NAME AS bankSubName,\n" +
			"  cb.BANK_NAME AS bankName \n" +
			"FROM\n" +
			"  t_customer_info ci,\n" +
			"  t_bank_card cb,\n" +
			"  t_virtual_merchant vm \n" +
			"WHERE ci.CUST_ID = cb.CUST_ID \n" +
			"  AND ci.CUST_ID = vm.CUST_ID \n" +
			"  AND cb.VALID = 'Y' \n" +
			"  AND cb.BIND_STATUS = 'INIT' \n" +
			"  AND vm.MERCHANT_STATUS = 'REG_SUCCESS' \n" +
			"  AND cb.BANK_CARD_TYPE = 'D' \n" +
			"  AND (\n" +
			"    cb.IO_TYPE = 'I' \n" +
			"    OR cb.IO_TYPE = 'IO'\n" +
			"  )")
	public List<Map<String, Object>> findCardBindList();
	
	
	/**
	 * 更新信用卡绑卡状态
	 * @param id
	 * @Param status
	 */
	@Modifying
	@Query(nativeQuery=true, value = "update t_bank_card cb set cb.BIND_STATUS = :status where cb.id = :id")
	void updateCardBindStatus(@Param(value = "id")String id, @Param(value = "status")String status);

	/**
	 * 更新信用卡开卡状态
	 * @param id
	 * @param status
	 */
	@Modifying
	@Query(nativeQuery = true, value = "update t_bank_card cb set cb.OPEN_STATUS = :status where cb.id = :id")
	void updateCardOpenStatus(@Param(value = "id") String id, @Param(value = "status") String status);
	@Query(nativeQuery = true, value = "SELECT\n" +
            "\tsum(\n" +
            "\t\tcast(\n" +
            "\t\t\tt.withdraw_amt AS DECIMAL (10, 2)\n" +
            "\t\t) - 1\n" +
            "\t) AS finAmt\n" +
            "FROM\n" +
            "\tt_withdraw_plan t\n" +
            "WHERE\n" +
            "\tt.apply_id = ?\n" +
            "AND t.withdraw_status = 'SUCCESS'")
    Map<String,Object> finFinAmt(String id);
}
