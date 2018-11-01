package com.goldgyro.platform.core.client.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.goldgyro.platform.core.client.entity.BankCardPO;
import org.springframework.data.repository.query.Param;

/**
 * 客户银行卡信息处理DAO
 * @author wg2993
 * @version 2018/07/10
 */
public interface BankCardDao extends JpaRepository<BankCardPO, String>, JpaSpecificationExecutor<BankCardPO> {
	/**
	 * 查找 客户银行卡
	 * @param custId
	 * @return BankCardPO
	 */
//	@Query(nativeQuery = true,value = "select * from T_BANK_CARD where BANK_CARD_TYPE = 'C' and CUST_ID = ?")
	List<BankCardPO> findByCustIdOrderByCreatedTimeAsc(String custId);
	
	/**
	 * 查找 客户银行卡
	 * @param bankCardCode
	 * @return BankCardPO
	 */
	BankCardPO findBankCardByCustIdAndAccountCode(String custId, String bankCardCode) ;

	/**
	 * 通过ID获取卡信息
	 * @param id
	 */
	BankCardPO findBankCardById(String id);

	@Query(nativeQuery=true, value="select tmp.*, \r\n" +
			"	(select p.PAYMENT_AMT from t_payment_plan p where p.APPLY_ID=tmp.applyId and p.PAYMENT_TIME=(select min(p1.PAYMENT_TIME) from t_payment_plan p1 \r\n" +
			"where p1.PLAN_STATUS='CONFIRMED' and p1.CARD_NO=?)) as currPaymentAmt\r\n" +
			"from (\r\n" +
			"select pa.CARD_CODE as cardNo , pa.ID as applyId, pa.PAYMENT_AMT as applyAmt, \r\n" +
			"(select sum(pp1.PAYMENT_AMT) as balanceAmt from t_payment_plan pp1 where pp1.APPLY_ID = pa.ID and pp1.PLAN_STATUS<>'REPAY_SUCCESS') as balanceAmt,\r\n" +
			"(select sum(ti.TRANS_FEE) as transFee from t_trans_income ti where ti.APPLY_NO = pa.ID) as transFee,\r\n" +
			"(select count(pp2.ID) from t_payment_plan pp2 where pp2.APPLY_ID = pa.ID and pp2.PLAN_STATUS<>'REPAY_SUCCESS') as balanceTerm, (select count(pp3.ID) from t_payment_plan pp3 where pp3.APPLY_ID = pa.ID) as totalTerm,\r\n" +
			"(select max(pp4.PAYMENT_TIME) from t_payment_plan pp4 where pp4.APPLY_ID = pa.ID) as deadline\r\n" +
			"from t_payment_apply pa\r\n" +
			"where pa.CREATED_TIME=(select max(p1.CREATED_TIME) from t_payment_apply p1 where p1.CUST_ID=?) and pa.CARD_CODE=?\r\n" +
			") tmp")
	List<Map<String, Object>> paymentScheduleList(String cardNo, String custId,String cardNo1);

	@Query(nativeQuery = true, value = "SELECT\n" +
			"\tb.account_name accountName,\n" +
			"\tb.MOBILE_NO mobileNo,\n" +
			"\tb.id id,\n" +
			"\tb.bank_name bankName,\n" +
			"\tb.ACCOUNT_CODE cardNo,\n" +
			"\tb.ACCOUNT_CODE accountCode,\n" +
			"\tb.BIND_STATUS bindStatus,\n" +
			"\tb.OPEN_STATUS openStatus,\n" +
			"\tIFNULL(tmp.applyId, '') applyId,\n" +
			"\tIFNULL(tmp.totalTerm, 0) totalTerm,\n" +
			"\tIFNULL(tmp.balanceTerm, 0) balanceTerm,\n" +
			"\tIFNULL(tmp.applyAmt, 0) applyAmt,\n" +
			"\tIFNULL(tmp.balanceAmt, 0) balanceAmt,\n" +
			"\ttmp.createTime,\n" +
			"\ttmp.transFee,\n" +
			"\ttmp.deadline,\n" +
			"\ttmp.currPaymentAmt,\n" +
			"\tb.default_area defaultArea,\n" +
			"\tb.accountant_bill_date accountantBillDate,\n" +
			"\tb.accountant_dates accountantDates\n" +
			"FROM\n" +
			"\tt_bank_card b\n" +
			"LEFT JOIN (\n" +
			"\tSELECT\n" +
			"\t\ts.id applyId,\n" +
			"\t\ts.card_code,\n" +
			"\t\tt.id bid,\n" +
			"\t\t(\n" +
			"\t\t\tSELECT\n" +
			"\t\t\t\tSUM(1)\n" +
			"\t\t\tFROM\n" +
			"\t\t\t\tt_withdraw_plan w\n" +
			"\t\t\tWHERE\n" +
			"\t\t\t\tw.apply_id = s.id\n" +
			"\t\t) AS totalTerm,\n" +
			"\t\tIFNULL(\n" +
			"\t\t\t(\n" +
			"\t\t\t\tSELECT\n" +
			"\t\t\t\t\tSUM(1)\n" +
			"\t\t\t\tFROM\n" +
			"\t\t\t\t\tt_withdraw_plan w\n" +
			"\t\t\t\tWHERE\n" +
			"\t\t\t\t\tw.apply_id = s.id\n" +
			"\t\t\t\tAND w.withdraw_status = 'PLAN'\n" +
			"\t\t\t),\n" +
			"\t\t\t0\n" +
			"\t\t) AS balanceTerm,\n" +
			"\t\ts.payment_amt AS applyAmt,\n" +
			"\t\tIFNULL(\n" +
			"\t\t\ts.payment_amt - IFNULL(\n" +
			"\t\t\t\t(\n" +
			"\t\t\t\t\tSELECT\n" +
			"\t\t\t\t\t\tSUM(\n" +
			"\t\t\t\t\t\t\tCAST(\n" +
			"\t\t\t\t\t\t\t\tw.withdraw_amt AS DECIMAL (10, 2)\n" +
			"\t\t\t\t\t\t\t) - 1\n" +
			"\t\t\t\t\t\t)\n" +
			"\t\t\t\t\tFROM\n" +
			"\t\t\t\t\t\tt_withdraw_plan w\n" +
			"\t\t\t\t\tWHERE\n" +
			"\t\t\t\t\t\tw.apply_id = s.id\n" +
			"\t\t\t\t\tAND w.withdraw_status = 'SUCCESS'\n" +
			"\t\t\t\t),\n" +
			"\t\t\t\t0\n" +
			"\t\t\t),\n" +
			"\t\t\t0.00\n" +
			"\t\t) AS balanceAmt,\n" +
			"\t\ts.CREATED_TIME createTime,\n" +
			"\t\t(\n" +
			"\t\t\tSELECT\n" +
			"\t\t\t\tSUM(\n" +
			"\t\t\t\t\tCAST(\n" +
			"\t\t\t\t\t\ttti.trans_fee AS DECIMAL (10, 2)\n" +
			"\t\t\t\t\t)\n" +
			"\t\t\t\t)\n" +
			"\t\t\tFROM\n" +
			"\t\t\t\tt_trans_income tti\n" +
			"\t\t\tWHERE\n" +
			"\t\t\t\ttti.apply_no = s.id\n" +
			"\t\t) AS transFee,\n" +
			"\t\t(\n" +
			"\t\t\tSELECT\n" +
			"\t\t\t\tMAX(w.withdraw_date)\n" +
			"\t\t\tFROM\n" +
			"\t\t\t\tt_withdraw_plan w\n" +
			"\t\t\tWHERE\n" +
			"\t\t\t\tw.apply_id = s.id\n" +
			"\t\t) AS deadline,\n" +
			"\t\t0 currPaymentAmt\n" +
			"\tFROM\n" +
			"\t\tt_bank_card t,\n" +
			"\t\tt_payment_apply s\n" +
			"\tWHERE\n" +
			"\t\tt.CUST_ID = s.CUST_ID\n" +
			"\tAND t.cust_id = :custId\n" +
			"\tAND t.bank_card_type = 'C'\n" +
			"\tAND (\n" +
			"\t\tEXISTS (\n" +
			"\t\t\tSELECT\n" +
			"\t\t\t\t1\n" +
			"\t\t\tFROM\n" +
			"\t\t\t\tt_payment_plan p\n" +
			"\t\t\tWHERE\n" +
			"\t\t\t\tp.apply_id = s.id\n" +
			"\t\t\tAND p.plan_status = 'CONFIRMED'\n" +
			"\t\t)\n" +
			"\t\tOR EXISTS (\n" +
			"\t\t\tSELECT\n" +
			"\t\t\t\t1\n" +
			"\t\t\tFROM\n" +
			"\t\t\t\tt_withdraw_plan w\n" +
			"\t\t\tWHERE\n" +
			"\t\t\t\tw.apply_id = s.id\n" +
			"\t\t\tAND w.withdraw_status = 'PLAN'\n" +
			"\t\t)\n" +
			"\t)\n" +
			") tmp ON b.id = tmp.bid\n" +
			"AND b.account_code = tmp.card_code\n" +
			"WHERE\n" +
			"\tb.cust_id = :custId\n" +
			"AND b.bank_card_type = 'C'\n" +
			"ORDER BY\n" +
			"\tb.created_time")
    List<Map<String,Object>> findScheduleList(@Param("custId") String custId);

	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM t_bank_card WHERE account_code = ?")
    void deleteByCardNo(String cardNo);
	@Query(nativeQuery = true, value = "SELECT \n" +
			"  1 \n" +
			"FROM\n" +
			"  t_bank_card b \n" +
			"WHERE b.account_code = ? \n" +
			"  AND (\n" +
			"    EXISTS \n" +
			"    (SELECT \n" +
			"      1 \n" +
			"    FROM\n" +
			"      t_payment_plan p1 \n" +
			"    WHERE p1.card_no = b.account_code \n" +
			"      AND (\n" +
			"        p1.plan_status = 'CONFIRMED' \n" +
			"        OR p1.plan_status = 'PROCESSING'\n" +
			"      )) \n" +
			"    OR EXISTS \n" +
			"    (SELECT \n" +
			"      1 \n" +
			"    FROM\n" +
			"      t_withdraw_plan p2,\n" +
			"      t_payment_apply p \n" +
			"    WHERE p2.apply_id = p.id \n" +
			"      AND p.card_code = b.ACCOUNT_CODE \n" +
			"      AND (\n" +
			"        p2.withdraw_status = 'PLAN' \n" +
			"        OR p2.withdraw_status = 'PROCESSING'\n" +
			"      ))\n" +
			"  )")
	List existRunningPlan(String cardNo);

	@Query(nativeQuery = true, value = "SELECT \n" +
			"  * \n" +
			"FROM\n" +
			"  t_bank_card t \n" +
			"WHERE EXISTS \n" +
			"  (SELECT \n" +
			"    1 \n" +
			"  FROM\n" +
			"    t_virtual_merchant v \n" +
			"  WHERE v.cust_id = t.cust_id \n" +
			"    AND v.merchant_code = ?) \n" +
			"  AND t.BANK_CARD_TYPE = 'D' ")
    List<BankCardPO> findByMerchantCode(String merchantCode);

	@Modifying
	@Query(nativeQuery = true, value = "delete from t_bank_card where cust_id = ?")
    void deleteByCustId(String custId);

	@Query(nativeQuery = true, value = "SELECT\n" +
			"\tv.PLAT_MERCHANT_CODE platMerchantCode,\n" +
			"\tb.ACCOUNT_NAME accountName,\n" +
			"\tb.ACCOUNT_CODE cardNo,\n" +
			"\tc.ID_CARD_NO certNo,\n" +
			"\tb.MOBILE_NO phoneno\n" +
			"FROM\n" +
			"\tt_bank_card b,\n" +
			"\tt_customer_info c,\n" +
			"\tt_virtual_merchant v\n" +
			"WHERE\n" +
			"\tb.CUST_ID = c.CUST_ID\n" +
			"AND b.CUST_ID = v.CUST_ID\n" +
			"and b.id = ?")
    Map<String,Object> queryParams(String cardId);

	BankCardPO findByOpenOrderId(String openOrderId);
}

