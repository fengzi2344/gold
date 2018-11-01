package com.goldgyro.platform.core.client.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.goldgyro.platform.core.client.entity.WithdrawPlanPO;

/**
 * 客户提现信息处理DAO
 * @author wg2993
 * @version 2018/07/10
 */
public interface WithdrawPlanDao extends JpaRepository<WithdrawPlanPO, String>, JpaSpecificationExecutor<WithdrawPlanPO> {

	@Query(nativeQuery=true, value="SELECT\n" +
			"\twp.ID AS withdrawOrderId,\n" +
			"\tvm.PLAT_MERCHANT_CODE AS platMerchantCode,\n" +
			"\tFLOOR(wp.WITHDRAW_AMT * 100)  AS amount,\n" +
			"\tpa.CARD_CODE AS bankAccountNo,\n" +
			"\twp.CUST_ID AS custId,\n" +
			"\tpa.CASH_TRANS AS cashTrans,\n" +
			"\tpa.IN_CARD_CODE AS inCardCode,\n" +
			"\ttb.open_card_id openCardId\n" +
			"FROM\n" +
			"\tT_WITHDRAW_PLAN wp,\n" +
			"\tt_virtual_merchant vm,\n" +
			"\tt_payment_apply pa,\n" +
			"\tt_bank_card tb\n" +
			"WHERE\n" +
			"\twp.CUST_ID = vm.CUST_ID\n" +
			"AND wp.APPLY_ID = pa.ID\n" +
			"AND pa.card_code = tb.account_code\n" +
			"AND wp.WITHDRAW_STATUS = 'PLAN'\n" +
			"AND wp.WITHDRAW_DATE <= CURRENT_TIME")
	List<Map<String, Object>> findWithdrawPlanList();
	
	@Modifying
	@Query(nativeQuery=true, value="update T_WITHDRAW_PLAN wp set wp.WITHDRAW_STATUS = ? where wp.id = ?")
	void updateStatus(String status, String id);

	@Query(nativeQuery=true, value="select distinct wp.ID as withdrawOrderId, wp.CUST_ID as custId, wp.PAYMENT_DATE as payemtnDate, wp.WITHDRAW_AMT as withdrawAmt,"
			+ " wp.WITHDRAW_DATE as withdrawDate, wp.WITHDRAW_STATUS as withdrawStatus " + 
						"from T_WITHDRAW_PLAN wp " + 
					"where wp.APPLY_ID=? and wp.PAYMENT_DATE=?")
	Map<String, Object> findWithdrawPlan(String applyId, String strPaymentDate);

	@Modifying
	@Query(nativeQuery = true, value = "update t_withdraw_plan set withdraw_status = 'CANCELED' where apply_id = ?")
    void cancelWithrawPlan(String applyId);
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE \n" +
			"  t_withdraw_plan s \n" +
			"SET\n" +
			"  s.withdraw_status = 'CANCELED' \n" +
			"WHERE EXISTS \n" +
			"  (SELECT \n" +
			"    1 \n" +
			"  FROM\n" +
			"    t_payment_plan t \n" +
			"  WHERE t.trans_no = ? \n" +
			"    AND t.apply_id = s.apply_id) \n" +
			"  AND s.withdraw_status = 'PLAN' ")
	void cancelByTransNo(String transNo);

	@Query(nativeQuery = true, value = "select * from t_withdraw_plan where work_id = ? limit 0,1")
	WithdrawPlanPO findByWorkId(String workId);

	@Query(nativeQuery = true, value = "select 1 from t_withdraw_plan t where t.id = ? and t.withdraw_date = (select max(s.withdraw_date) from t_withdraw_plan s where s.apply_id = t.apply_id)")
	List isLastOne(String id);
}

