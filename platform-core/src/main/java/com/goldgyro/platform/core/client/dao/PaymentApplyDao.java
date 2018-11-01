package com.goldgyro.platform.core.client.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.goldgyro.platform.core.client.entity.PaymentApplyPO;
import org.springframework.data.repository.query.Param;

public interface PaymentApplyDao  extends JpaRepository<PaymentApplyPO, String>, JpaSpecificationExecutor<PaymentApplyPO>{

	@Query(nativeQuery = true, value="SELECT \n" +
			"  pa.ID,\n" +
			"  pa.CUST_ID AS custId,\n" +
			"  pa.CARD_CODE AS cardCode,\n" +
			"  pa.PAYMENT_AMT AS paymentAmt,\n" +
			"  pa.PAYMENT_DATE AS paymentDate,\n" +
			"  pa.CREATED_TIME AS createdTime,\n" +
			"  CAST(\n" +
			"    af.total_fee / 100- pa.payment_amt AS DECIMAL (10, 2)\n" +
			"  ) AS totalFee \n" +
			"FROM\n" +
			"  T_PAYMENT_APPLY pa,\n" +
			"  t_apply_fee af \n" +
			"WHERE pa.CUST_ID = ? \n" +
			"  AND pa.CARD_CODE = ? \n" +
			"  AND af.apply_id = pa.id \n" +
			"  AND pa.CREATED_TIME = \n" +
			"  (SELECT \n" +
			"    MAX(pa1.CREATED_TIME) \n" +
			"  FROM\n" +
			"    T_PAYMENT_APPLY pa1 \n" +
			"  WHERE pa1.CUST_ID = ? \n" +
			"    AND pa1.CARD_CODE = ?)")
	Map<String, Object> findPayementApply(String custId, String cardNo, String custId1, String cardNo1);

	PaymentApplyPO findPaymentApplyPOById(String id);

	List<PaymentApplyPO> queryByCustId(String custId);
    @Query(nativeQuery = true, value = "SELECT \n" +
            "  * \n" +
            "FROM\n" +
            "  t_payment_apply t \n" +
            "WHERE EXISTS \n" +
            "  (SELECT \n" +
            "    1 \n" +
            "  FROM\n" +
            "    t_withdraw_plan p \n" +
            "  WHERE p.id = :withdrawId \n" +
            "    AND p.apply_id = t.id) \n" +
            "  AND NOT EXISTS \n" +
            "  (SELECT \n" +
            "    1 \n" +
            "  FROM\n" +
            "    t_withdraw_plan wp \n" +
            "  WHERE wp.apply_id = t.id \n" +
            "    AND wp.id <> :withdrawId \n" +
            "    AND wp.withdraw_status <> 'SUCCESS')")
	PaymentApplyPO queryByWithdrawId(@Param("withdrawId")String withdrawId);

	@Query(nativeQuery = true, value = "SELECT \n" +
			"  (SELECT \n" +
			"    COUNT(*) \n" +
			"  FROM\n" +
			"    t_withdraw_plan t \n" +
			"  WHERE EXISTS \n" +
			"    (SELECT \n" +
			"      1 \n" +
			"    FROM\n" +
			"      t_withdraw_plan s \n" +
			"    WHERE s.apply_id = t.apply_id \n" +
			"      AND s.id = :id) \n" +
			"    AND t.withdraw_status <> 'SUCCESS' \n" +
			"    AND t.id <> :id) AS num1,\n" +
			"  (SELECT \n" +
			"    COUNT(*) \n" +
			"  FROM\n" +
			"    t_payment_apply tp \n" +
			"  WHERE EXISTS \n" +
			"    (SELECT \n" +
			"      1 \n" +
			"    FROM\n" +
			"      t_payment_apply tp1,\n" +
			"      t_withdraw_plan wp1 \n" +
			"    WHERE tp1.id = wp1.apply_id \n" +
			"      AND tp1.CUST_ID = tp.cust_id \n" +
			"      AND tp1.id <> tp.id \n" +
			"      AND wp1.id = :id) \n" +
			"    AND EXISTS \n" +
			"    (SELECT \n" +
			"      1 \n" +
			"    FROM\n" +
			"      t_withdraw_plan w2 \n" +
			"    WHERE w2.apply_id = tp.id \n" +
			"      AND w2.withdraw_status = 'SUCCESS')) AS num2 \n" +
			"FROM\n" +
			"  DUAL ")
    Map<String,Object> findMap(@Param("id") String id);

	@Query(nativeQuery = true, value = "SELECT \n" +
			"  pa.ID,\n" +
			"  pa.CUST_ID AS custId,\n" +
			"  pa.CARD_CODE AS cardCode,\n" +
			"  pa.PAYMENT_AMT AS paymentAmt,\n" +
			"  pa.PAYMENT_DATE AS paymentDate,\n" +
			"  pa.CREATED_TIME AS createdTime,\n" +
			"  CAST(\n" +
			"    af.total_fee / 100- pa.payment_amt AS DECIMAL (10, 2)\n" +
			"  ) AS totalFee \n" +
			"FROM\n" +
			"  T_PAYMENT_APPLY pa,\n" +
			"  t_apply_fee af \n" +
			"WHERE pa.CUST_ID = ? \n" +
			"  AND pa.CARD_CODE = ? \n" +
			"  AND af.apply_id = pa.id \n" +
			"  AND NOT EXISTS \n" +
			"  (SELECT \n" +
			"    1 \n" +
			"  FROM\n" +
			"    t_payment_plan p \n" +
			"  WHERE p.APPLY_ID = pa.id \n" +
			"    AND p.PLAN_STATUS IN ('CANCELED', 'CONFIRMED')) \n" +
			"ORDER BY pa.CREATED_TIME DESC")
	List<Map<String,Object>> findHistoryList(String custId, String cardNo);

	@Modifying
	@Query(nativeQuery = true, value = "update t_payment_apply set status = ? where id = ?")
    void updateStatusById(String status, String id);

	@Query(nativeQuery = true, value = "SELECT\n" +
			"\tmth,\n" +
			"\tGROUP_CONCAT(STATUS) statuses,\n" +
			"\tGROUP_CONCAT(id) ids,\n" +
			"\tGROUP_CONCAT(startDate) startDates,\n" +
			"\tGROUP_CONCAT(endDate) endDates,\n" +
			"\tGROUP_CONCAT(terms) terms,\n" +
			"\tGROUP_CONCAT(paymentAmt) paymentAmts,\n" +
			"\tGROUP_CONCAT(finTerms) finTerms,\n" +
			"\tGROUP_CONCAT(balanceAmts) balanceAmts\n" +
			"FROM\n" +
			"\t(\n" +
			"\t\tSELECT\n" +
			"\t\t\tt.id,\n" +
			"\t\t\tIFNULL(t. STATUS, 'H') STATUS,\n" +
			"\t\t\tSUBSTR(payment_date, 1, 7) mth,\n" +
			"\t\t\tSUBSTR(payment_date, 1, 10) startDate,\n" +
			"\t\t\tSUBSTR(\n" +
			"\t\t\t\tpayment_date,\n" +
			"\t\t\t\tLENGTH(payment_date) - 9\n" +
			"\t\t\t) endDate,\n" +
			"\t\t\t(\n" +
			"\t\t\t\tSELECT\n" +
			"\t\t\t\t\tsum(1)\n" +
			"\t\t\t\tFROM\n" +
			"\t\t\t\t\tt_withdraw_plan w\n" +
			"\t\t\t\tWHERE\n" +
			"\t\t\t\t\tw.apply_id = t.id\n" +
			"\t\t\t) AS terms,\n" +
			"\t\t\t(\n" +
			"\t\t\t\tSELECT\n" +
			"\t\t\t\t\tIFNULL(sum(1), 0)\n" +
			"\t\t\t\tFROM\n" +
			"\t\t\t\t\tt_withdraw_plan w\n" +
			"\t\t\t\tWHERE\n" +
			"\t\t\t\t\tw.apply_id = t.id\n" +
			"\t\t\t\tAND w.withdraw_status = 'SUCCESS'\n" +
			"\t\t\t) AS finTerms,\n" +
			"\t\t\t(\n" +
			"\t\t\t\tSELECT\n" +
			"\t\t\t\t\tIFNULL(sum(w.withdraw_amt - 1), 0)\n" +
			"\t\t\t\tFROM\n" +
			"\t\t\t\t\tt_withdraw_plan w\n" +
			"\t\t\t\tWHERE\n" +
			"\t\t\t\t\tw.apply_id = t.id\n" +
			"\t\t\t\tAND w.withdraw_status = 'SUCCESS'\n" +
			"\t\t\t) balanceAmts,\n" +
			"\t\t\tt.PAYMENT_AMT paymentAmt\n" +
			"\t\tFROM\n" +
			"\t\t\tt_payment_apply t\n" +
			"\t\tWHERE\n" +
			"\t\t\tt.CARD_CODE = ?\n" +
			"\t\tAND t. STATUS <> 'CANCEL'\n" +
			"\t\tORDER BY\n" +
			"\t\t\tSUBSTR(payment_date, 1, 10) DESC\n" +
			"\t) tp\n" +
			"GROUP BY\n" +
			"\ttp.mth\n" +
			"ORDER BY\n" +
			"\tmth DESC")
	List<Map<String,Object>> queryHistoryByCardNo(String cardNo);

	@Query(nativeQuery = true, value = "SELECT\n" +
			"\tpa.ID,\n" +
			"\tpa.CUST_ID AS custId,\n" +
			"\tpa.CARD_CODE AS cardCode,\n" +
			"\tpa.PAYMENT_AMT AS paymentAmt,\n" +
			"\tpa.PAYMENT_DATE AS paymentDate,\n" +
			"\tpa.CREATED_TIME AS createdTime,\n" +
			"\tCAST(\n" +
			"\t\taf.total_fee / 100 - pa.payment_amt AS DECIMAL (10, 2)\n" +
			"\t) AS totalFee\n" +
			"FROM\n" +
			"\tT_PAYMENT_APPLY pa,\n" +
			"\tt_apply_fee af\n" +
			"WHERE\n" +
			"\taf.apply_id = pa.id\n" +
			"AND pa.id = ?")
	Map<String,Object> findPenmentObject(String applyId);

	@Query(nativeQuery = true, value = "SELECT\n" +
            "\ttmp1.*, tmp2.planMoney,\n" +
            "\ttmp2.withdrawTime\n" +
            "FROM\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tpaymentDate,\n" +
            "\t\t\tGROUP_CONCAT(ID ORDER BY paymentTime) ids,\n" +
            "\t\t\tGROUP_CONCAT(cardNo ORDER BY paymentTime) AS cardNos,\n" +
            "\t\t\tGROUP_CONCAT(\n" +
            "\t\t\t\tpaymentAmt\n" +
            "\t\t\t\tORDER BY\n" +
            "\t\t\t\t\tpaymentTime\n" +
            "\t\t\t) AS paymentAmts,\n" +
            "\t\t\tGROUP_CONCAT(\n" +
            "\t\t\t\tpaymentTime\n" +
            "\t\t\t\tORDER BY\n" +
            "\t\t\t\t\tpaymentTime\n" +
            "\t\t\t) AS paymentTimes,\n" +
            "\t\t\tGROUP_CONCAT(\n" +
            "\t\t\t\tpaymentStatus\n" +
            "\t\t\t\tORDER BY\n" +
            "\t\t\t\t\tpaymentTime\n" +
            "\t\t\t) AS paymentStatuses,\n" +
            "\t\t\tGROUP_CONCAT(transNo ORDER BY paymentTime) AS transNos,\n" +
            "\t\t\tGROUP_CONCAT(\n" +
            "\t\t\t\tcreatedTime\n" +
            "\t\t\t\tORDER BY\n" +
            "\t\t\t\t\tpaymentTime\n" +
            "\t\t\t) AS createdTimes,\n" +
            "\t\t\tGROUP_CONCAT(\n" +
            "\t\t\t\tIFNULL(hyName, '服饰')\n" +
            "\t\t\t\tORDER BY\n" +
            "\t\t\t\t\tpaymentTime\n" +
            "\t\t\t) AS hyNames\n" +
            "\t\tFROM\n" +
            "\t\t\t(\n" +
            "\t\t\t\tSELECT\n" +
            "\t\t\t\t\tsubstr(PAYMENT_TIME, 1, 10) paymentDate,\n" +
            "\t\t\t\t\tpp.ID,\n" +
            "\t\t\t\t\tpp.CARD_NO AS cardNo,\n" +
            "\t\t\t\t\tpp.PAYMENT_AMT AS paymentAmt,\n" +
            "\t\t\t\t\tpp.PAYMENT_TIME AS paymentTime,\n" +
            "\t\t\t\t\tpp.PLAN_STATUS AS paymentStatus,\n" +
            "\t\t\t\t\tpp.TRANS_NO AS transNo,\n" +
            "\t\t\t\t\tpp.CREATED_TIME AS createdTime,\n" +
            "\t\t\t\t\td. NAME AS hyName\n" +
            "\t\t\t\tFROM\n" +
            "\t\t\t\t\tT_PAYMENT_PLAN pp\n" +
            "\t\t\t\tLEFT JOIN t_dictionary d ON pp.mcc_code = d. CODE,\n" +
            "\t\t\t\tT_PAYMENT_APPLY pa\n" +
            "\t\t\tWHERE\n" +
            "\t\t\t\tpp.APPLY_ID = pa.ID\n" +
            "\t\t\tAND pp.APPLY_ID = :applyId\n" +
            "\t\t\t) tmp\n" +
            "\t\tGROUP BY\n" +
            "\t\t\tpaymentDate\n" +
            "\t\tORDER BY\n" +
            "\t\t\tpaymentDate ASC\n" +
            "\t) tmp1,\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tDATE_FORMAT(\n" +
            "\t\t\t\twp.withdraw_date,\n" +
            "\t\t\t\t'%Y-%m-%d'\n" +
            "\t\t\t) wDate,\n" +
            "\t\t\twp.withdraw_amt - 1 AS planMoney,\n" +
            "\t\t\twp.withdraw_date withdrawTime\n" +
            "\t\tFROM\n" +
            "\t\t\tt_withdraw_plan wp\n" +
            "\t\tWHERE\n" +
            "\t\t\twp.apply_id = :applyId\n" +
            "\t) tmp2\n" +
            "WHERE\n" +
            "\ttmp1.paymentDate = tmp2.wDate\n" +
            "ORDER BY\n" +
            "\ttmp1.paymentDate")
    List<Map<String,Object>> findWyrList(@Param("applyId") String applyId);

	@Query(nativeQuery = true, value = "SELECT\n" +
			"\tfloor(\n" +
			"\t\t(\n" +
			"\t\t\tSELECT\n" +
			"\t\t\t\tsum(PAYMENT_AMT) * (\n" +
			"\t\t\t\t\t1 - (\n" +
			"\t\t\t\t\t\tSELECT\n" +
			"\t\t\t\t\t\t\tf.CFEE_RATE\n" +
			"\t\t\t\t\t\tFROM\n" +
			"\t\t\t\t\t\t\tt_trans_fee_rate f,\n" +
			"\t\t\t\t\t\t\tt_customer_info c\n" +
			"\t\t\t\t\t\tWHERE\n" +
			"\t\t\t\t\t\t\tf.CLEVEL_SAMPLE = c.cust_level_sample\n" +
			"\t\t\t\t\t\tAND c.cust_id = t.CUST_ID\n" +
			"\t\t\t\t\t)\n" +
			"\t\t\t\t)\n" +
			"\t\t\tFROM\n" +
			"\t\t\t\tt_payment_plan p\n" +
			"\t\t\tWHERE\n" +
			"\t\t\t\tp.APPLY_ID = t.id\n" +
			"\t\t\tAND p.plan_status = 'REPAY_SUCCESS'\n" +
			"\t\t)\n" +
			"\t),\n" +
			"\t(\n" +
			"\t\tSELECT\n" +
			"\t\t\tsum(w.withdraw_amt)\n" +
			"\t\tFROM\n" +
			"\t\t\tt_withdraw_plan w\n" +
			"\t\tWHERE\n" +
			"\t\t\tw.apply_id = t.id\n" +
			"\t\tAND w.withdraw_status = 'SUCCESS'\n" +
			"\t)\n" +
			"FROM\n" +
			"\tt_payment_apply t\n" +
			"WHERE\n" +
			"\tt.id = ?")
    Integer findBalance(String applyId);

	@Query(nativeQuery = true, value = "SELECT\n" +
            "\ttmp.*, (\n" +
            "\t\tSELECT\n" +
            "\t\t\t(\n" +
            "\t\t\t\tfloor(\n" +
            "\t\t\t\t\t(\n" +
            "\t\t\t\t\t\tSELECT\n" +
            "\t\t\t\t\t\t\tsum(PAYMENT_AMT) * (\n" +
            "\t\t\t\t\t\t\t\t1 - (\n" +
            "\t\t\t\t\t\t\t\t\tSELECT\n" +
            "\t\t\t\t\t\t\t\t\t\tf.CFEE_RATE\n" +
            "\t\t\t\t\t\t\t\t\tFROM\n" +
            "\t\t\t\t\t\t\t\t\t\tt_trans_fee_rate f,\n" +
            "\t\t\t\t\t\t\t\t\t\tt_customer_info c\n" +
            "\t\t\t\t\t\t\t\t\tWHERE\n" +
            "\t\t\t\t\t\t\t\t\t\tf.CLEVEL_SAMPLE = c.cust_level_sample\n" +
            "\t\t\t\t\t\t\t\t\tAND c.cust_id = t.CUST_ID\n" +
            "\t\t\t\t\t\t\t\t)\n" +
            "\t\t\t\t\t\t\t)\n" +
            "\t\t\t\t\t\tFROM\n" +
            "\t\t\t\t\t\t\tt_payment_plan p\n" +
            "\t\t\t\t\t\tWHERE\n" +
            "\t\t\t\t\t\t\tp.APPLY_ID = t.id\n" +
            "\t\t\t\t\t\tAND p.plan_status = 'REPAY_SUCCESS'\n" +
            "\t\t\t\t\t)\n" +
            "\t\t\t\t) - IFNULL(\n" +
            "\t\t\t\t\t(\n" +
            "\t\t\t\t\t\tSELECT\n" +
            "\t\t\t\t\t\t\tsum(w.withdraw_amt)\n" +
            "\t\t\t\t\t\tFROM\n" +
            "\t\t\t\t\t\t\tt_withdraw_plan w\n" +
            "\t\t\t\t\t\tWHERE\n" +
            "\t\t\t\t\t\t\tw.apply_id = t.id\n" +
            "\t\t\t\t\t\tAND w.withdraw_status = 'SUCCESS'\n" +
            "\t\t\t\t\t),\n" +
            "\t\t\t\t\t0\n" +
            "\t\t\t\t)\n" +
            "\t\t\t) * 100 balance\n" +
            "\t\tFROM\n" +
            "\t\t\tt_payment_apply t\n" +
            "\t\tWHERE\n" +
            "\t\t\tt.id = :applyId\n" +
            "\t) amount\n" +
            "FROM\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\twp.ID AS withdrawOrderId,\n" +
            "\t\t\tvm.PLAT_MERCHANT_CODE AS platMerchantCode,\n" +
            "\t\t\tpa.CARD_CODE AS bankAccountNo,\n" +
            "\t\t\ttb.open_card_id openCardId,\n" +
            "\t\t\twp.withdraw_date\n" +
            "\t\tFROM\n" +
            "\t\t\tT_WITHDRAW_PLAN wp,\n" +
            "\t\t\tt_virtual_merchant vm,\n" +
            "\t\t\tt_payment_apply pa,\n" +
            "\t\t\tt_bank_card tb\n" +
            "\t\tWHERE\n" +
            "\t\t\twp.CUST_ID = vm.CUST_ID\n" +
            "\t\tAND wp.APPLY_ID = pa.ID\n" +
            "\t\tAND pa.card_code = tb.account_code\n" +
            "\t\tAND pa.id = :applyId\n" +
            "\t\tORDER BY\n" +
            "\t\t\twp.withdraw_date\n" +
            "\t\tLIMIT 0,\n" +
            "\t\t1\n" +
            "\t) tmp")
    Map<String,Object> findWithdrawMap(@Param("applyId") String applyId);

	@Query(nativeQuery = true, value = "SELECT\n" +
			"\tt.status,\n" +
			"\t(\n" +
			"\t\tSELECT\n" +
			"\t\t\tp.DESCR\n" +
			"\t\tFROM\n" +
			"\t\t\tt_payment_plan p\n" +
			"\t\tWHERE\n" +
			"\t\t\tp.APPLY_ID = t.id\n" +
			"\t\tAND p.DESCR IS NOT NULL\n" +
			"\t\tLIMIT 0,\n" +
			"\t\t1\n" +
			"\t) reason\n" +
			"FROM\n" +
			"\tt_payment_apply t\n" +
			"WHERE\n" +
			"\tt.CARD_CODE = :accountCode\n" +
			"AND t.CREATED_TIME = (\n" +
			"\tSELECT\n" +
			"\t\tmax(s.CREATED_TIME)\n" +
			"\tFROM\n" +
			"\t\tt_payment_apply s\n" +
			"\tWHERE\n" +
			"\t\ts.CARD_CODE = :accountCode\n" +
			")")
    Map<String,Object> findLatestPlanStatus(@Param("accountCode") String accountCode);
}
