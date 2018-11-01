package com.goldgyro.platform.core.client.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.goldgyro.platform.core.client.entity.PaymentPlanPO;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

public interface PaymentPlanDao extends JpaRepository<PaymentPlanPO, String>, JpaSpecificationExecutor<PaymentPlanPO>{

	/**
	 * 查询还款计划
	 * @return
	 */
	@Modifying
	@Query(nativeQuery=true, value="select pp.ID, pp.CUST_ID as custId, vm.MERCHANT_CODE as platMerchantCode, pp.APPLY_ID as applyId, pp.CARD_NO as cardNo,\r\n" + 
			"pp.PAYMENT_AMT as paymentAmt, pp.PAYMENT_TIME as paymentTime, pp.PLAN_STATUS as paymentStatus, \r\n" + 
			"pp.TRANS_NO as transNo, pp.CREATED_TIME  as createdTime\r\n" + 
			"from T_PAYMENT_PLAN pp , t_virtual_merchant vm, T_PAYMENT_APPLY pa \r\n" + 
			"where pp.CUST_ID = vm.CUST_ID and pp.APPLY_ID=pa.ID and pp.CUST_ID=? and pp.CARD_NO=? and pa.CREATED_TIME = (\r\n" + 
			"       select max(pa1.CREATED_TIME) from T_PAYMENT_APPLY pa1 where pa1.CUST_ID=? and pa1.CARD_CODE=?)"
			+ " order by pp.PAYMENT_TIME")
	List<Map<String, Object>> findPaymentPlanList(String custId, String cardNo, String custId1, String cardNo1);
	
	/**
	 * 查询还款计划
	 * @return
	 */
	@Query(nativeQuery=true, value="SELECT\n" +
            "\tpp.ID,\n" +
            "\tpp.CUST_ID AS custId,\n" +
            "\tvm.MERCHANT_CODE AS platMerchantCode,\n" +
            "\tpp.APPLY_ID AS applyId,\n" +
            "\tpp.CARD_NO AS cardNo,\n" +
            "\tpp.PAYMENT_AMT AS paymentAmt,\n" +
            "\tpp.PAYMENT_TIME AS paymentTime,\n" +
            "\tpp.PLAN_STATUS AS paymentStatus,\n" +
            "\tpp.TRANS_NO AS transNo,\n" +
            "\tpp.CREATED_TIME AS createdTime,\n" +
            "\t0 transFee,\n" +
            "\td.NAME AS hyName\n" +
            "FROM\n" +
            "\tT_PAYMENT_PLAN pp\n" +
            "LEFT JOIN t_dictionary d ON pp.mcc_code = d. CODE,\n" +
            " t_virtual_merchant vm,\n" +
            " T_PAYMENT_APPLY pa\n" +
            "WHERE\n" +
            "\tpp.CUST_ID = vm.CUST_ID\n" +
            "AND pp.APPLY_ID = pa.ID\n" +
            "AND pa.CUST_ID = ?\n" +
            "AND pa.CARD_CODE = ?\n" +
            "AND pa.CREATED_TIME = (\n" +
            "\tSELECT\n" +
            "\t\tMAX(pa1.CREATED_TIME)\n" +
            "\tFROM\n" +
            "\t\tT_PAYMENT_APPLY pa1\n" +
            "\tWHERE\n" +
            "\t\tpa1.CUST_ID = ?\n" +
            "\tAND pa1.CARD_CODE = ?\n" +
            ")\n" +
            "AND DATE_FORMAT(pp.PAYMENT_TIME, '%Y-%m-%d') = STR_TO_DATE(?, '%Y-%m-%d')\n" +
            "ORDER BY\n" +
            "\tpp.payment_time")
	List<Map<String, Object>> findPaymentPlanListByDate(String custId, String cardNo, String custId1, String cardNo1, String strPaymentDate);

	@Query(nativeQuery = true, value = "SELECT\n" +
			"\tpp.ID,\n" +
			"\tpp.CUST_ID AS custId,\n" +
			"\tvm.MERCHANT_CODE AS platMerchantCode,\n" +
			"\tpp.APPLY_ID AS applyId,\n" +
			"\tpp.CARD_NO AS cardNo,\n" +
			"\tpp.PAYMENT_AMT AS paymentAmt,\n" +
			"\tpp.PAYMENT_TIME AS paymentTime,\n" +
			"\tpp.PLAN_STATUS AS paymentStatus,\n" +
			"\tpp.TRANS_NO AS transNo,\n" +
			"\tpp.CREATED_TIME AS createdTime,\n" +
			"\t0 transFee,\n" +
			"\td. NAME AS hyName\n" +
			"FROM\n" +
			"\tT_PAYMENT_PLAN pp\n" +
			"LEFT JOIN t_dictionary d ON pp.mcc_code = d. CODE,\n" +
			" t_virtual_merchant vm,\n" +
			" T_PAYMENT_APPLY pa\n" +
			"WHERE\n" +
			"\tpp.CUST_ID = vm.CUST_ID\n" +
			"AND pp.APPLY_ID = pa.ID\n" +
			"AND pp.APPLY_ID = ?\n" +
			"AND DATE_FORMAT(pp.PAYMENT_TIME, '%Y-%m-%d') = STR_TO_DATE(?, '%Y-%m-%d')\n" +
			"ORDER BY\n" +
			"\tpp.payment_time")
	List<Map<String,Object>> findPaymentPlanListByApplyId(String applyId, String strPaymentDate);
	
	/**
	 * 后期加分页
	 * 获取N分钟前的还款计划
	 * @param interval
	 * @return
	 */
	@Modifying
	@Query(nativeQuery=true, value="SELECT\n" +
			"\tpp.ID AS id,\n" +
			"\tROUND(pp.PAYMENT_AMT * 100) AS payAmount,\n" +
			"\tpp.CARD_NO AS cardNo,\n" +
			"\tvm.PLAT_MERCHANT_CODE AS platMerchantCode,\n" +
			"\tpp.APPLY_ID AS applyId,\n" +
			"\ttb.open_card_id openCardId,\n" +
			"\tpp.TRANS_NO transNo,\n" +
			"\tIFNULL(p.city, '510100') city,\n" +
			"\tpp.mcc_code mccId,\n" +
			"\tc.cust_name custName,\n" +
			"\tc.cust_id custId\n" +
			"FROM\n" +
			"\tT_PAYMENT_PLAN pp,\n" +
			"\tt_virtual_merchant vm,\n" +
			"\tt_bank_card tb,\n" +
			"\tt_payment_apply p,\n" +
			"\tt_customer_info c\n" +
			"WHERE\n" +
			"\tpp.CUST_ID = vm.CUST_ID\n" +
			"AND c.cust_id = pp.CUST_ID\n" +
			"AND pp.CARD_NO = tb.ACCOUNT_CODE\n" +
			"AND pp.APPLY_ID = p.id\n" +
			"AND pp.PLAN_STATUS = 'CONFIRMED'\n" +
			"AND pp.PAYMENT_TIME < CURRENT_TIMESTAMP\n" +
			"AND pp.PAYMENT_TIME > DATE_ADD(\n" +
			"\tCURRENT_TIMESTAMP,\n" +
			"\tINTERVAL ? MINUTE\n" +
			")")
	List<Map<String, Object>> findPaymentPlan(int interval);
	
	/**
	 * 查询还款计划详细
	 * @return
	 */
	PaymentPlanPO findPaymentPlanById(String id);
	
	/**
	 * 有失败交易，撤消所有未执行的还款计划
	 * @param status
	 * @param applyId
	 */
	@Modifying
	@Query(nativeQuery=true, value="update t_payment_plan pp set pp.plan_status=? where pp.APPLY_ID=? and pp.PLAN_STATUS = 'CONFIRMED'")
	void cancelConfirmedPaymentPlan(String status, String applyId);
	
	/**
	 * 更新还款计划状态
	 * @param id
	 * @param status
	 */
	@Modifying
	@Query(nativeQuery=true, value="update t_payment_plan pp set pp.plan_status=? where pp.id=?")
	void updatePaymentPlanById(String status, String id);

	/**
	 * 查询还款计划
	 * @return
	 */
	@Modifying
	@Query(nativeQuery=true, value="select pp.ID, pp.CUST_ID as custId, vm.MERCHANT_CODE as platMerchantCode, pp.APPLY_ID as applyId, pp.CARD_NO as cardNo,\r\n" + 
			"pp.PAYMENT_AMT as paymentAmt, pp.PAYMENT_TIME as paymentTime, pp.PLAN_STATUS as paymentStatus, \r\n" + 
			"pp.TRANS_NO as transNo, pp.CREATED_TIME  as createdTime\r\n" + 
			"from T_PAYMENT_PLAN pp , t_virtual_merchant vm, T_PAYMENT_APPLY pa \r\n" + 
			"where pp.CUST_ID = vm.CUST_ID and pp.APPLY_ID=pa.ID and pp.CUST_ID=? and pp.CARD_NO=? and pa.CREATED_TIME = (\r\n" + 
			"       select max(pa1.CREATED_TIME) from T_PAYMENT_APPLY pa1 where pa1.CUST_ID=? and pa1.CARD_CODE=?) and pp.PLAN_STATUS=?"
			+ " order by pp.PAYMENT_TIME")
	List<Map<String, Object>> findPaymentPlanListByStatus(String custId, String cardNo, String custId2, String cardNo2, String status);


	@Modifying
	@Query(nativeQuery = true,value="update T_PAYMENT_PLAN set plan_status=? where trans_no=?")
	void updatePlanStatusByTransNo(String planStatus, String transNo);

	/**
	 *
	 * @param planStaus
	 * @param applyId
	 * @param planStatus1
	 */
	@Modifying
	@Query(nativeQuery = true,value="update T_PAYMENT_PLAN set plan_status=? where apply_id=? and plan_status=?")
	void updatePlanStatusByApplyId(String planStaus, String applyId,String planStatus1);

	/**
	 * 根据tranNo更新状态
	 * @param transNo
	 */
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE\n" +
			"  t_payment_plan\n" +
			"SET\n" +
			"  PLAN_STATUS = :status\n" +
			"WHERE id IN\n" +
			"  (SELECT\n" +
			"    a.id\n" +
			"  FROM\n" +
			"    (SELECT\n" +
			"      ts.id\n" +
			"    FROM\n" +
			"      t_payment_plan ts\n" +
			"    WHERE ts.apply_id =\n" +
			"      (SELECT\n" +
			"        s.apply_id\n" +
			"      FROM\n" +
			"        t_payment_plan s\n" +
			"      WHERE s.`TRANS_NO` = :tranNo)\n" +
			"      AND ts.payment_time >=\n" +
			"      (SELECT\n" +
			"        MAX(s.payment_time)\n" +
			"      FROM\n" +
			"        t_payment_plan s\n" +
			"      WHERE s.TRANS_NO = :transNo)) a)")
	void updatePlanStatusByTranNo(@Param("transNo") String transNo, @Param("status") String status);

	@Query(nativeQuery = true, value = "SELECT 1 FROM t_payment_plan t WHERE t.APPLY_ID=? AND t.PLAN_STATUS <> 'CONFIRMED'")
    List haveBegun(String applyId);

	@Modifying
	@Query(nativeQuery = true, value = "update t_payment_plan set descr = ?,plan_status = 'REPAY_FAIL' where trans_no = ?")
	void updateDescr(String repMsg, String transNo);

	@Query(nativeQuery = true, value = "select * from t_payment_plan where trans_no = ?")
	PaymentPlanPO findByTransNo(String transNo);

	@Query(nativeQuery = true, value = "select * from t_payment_plan where work_id = ? limit 0,1")
	PaymentPlanPO findByWorkId(String workId);

	@Modifying
	@Query(nativeQuery = true, value = "update t_payment_plan set mcc_code = ? where id = ?")
    void updateMccCodeById(String mccCode, String id);
}
