package com.goldgyro.platform.core.client.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.goldgyro.platform.core.client.entity.*;
import org.springframework.data.repository.query.Param;

import com.goldgyro.platform.core.client.domain.FeeRate;
import com.goldgyro.platform.core.client.domain.Income;
import com.goldgyro.platform.core.client.domain.PaymentApply;
import com.goldgyro.platform.core.client.domain.PaymentPlan;
import com.goldgyro.platform.core.client.domain.WithdrawPlan;

public interface PayCardService {
	/**
	 * 更新还款计划
	 * @param applyId
	 * @param status
	 */
	public void cancelConfirmedPaymentPlan(String applyId, String status);
	
	/**
	 * 更新还款计划
	 * @param status
	 * @param id
	 */
	public void updatePaymentPlanById(String status, String id);
	
	/**
	 * 获取商户注册信息
	 */
	public Map<String, Object> findBusiRegistrations(String custId);

	/**
	 * 更新商户注册状态
	 * @param merchCode
	 * @param status
	 * @throws Exception
	 */
	public void updateMerchantRegistStatus( @Param(value = "merchCode")String merchCode, @Param(value = "status")String status) throws Exception;
	
	/**
	 * 查询需要开卡的信用卡信息
	 * @return
	 */
//	public List<Map<String, Object>> findCreditCardRegist(String accountCode);
	
	/**
	 * 查询需要绑卡的信用卡信息
	 * @return
	 */
	public Map<String, Object> findCreditCardBind(String custId, String accountCode);
	
	/**
	 * 查询需要绑卡的信用卡信息
	 * @return
	 */
	public List<Map<String, Object>> findCardBindList();
	
	/**
	 * 更新信用卡注册状态
	 * @param id
	 * @param status
	 * @throws Exception
	 */
	public void updateCardBindStatus(@Param(value = "id")String id, @Param(value = "status")String status) throws Exception;

	/**
	 * 保存还款申请信息
	 * @param paymenApply
	 * @param paymentPlanList
	 * @param withdrawPlanList
	 * @param incomeList
	 * @throws Exception
	 */
	public void savePaymentApplyAndPlan(PaymentApply paymenApply, List<PaymentPlan> paymentPlanList, List <WithdrawPlan> withdrawPlanList, List<Income> incomeList) throws Exception;
	
	/**
	 * 将指定的金额分成多份，保留两位小数，最后一位确保所有金额之和等于总额
	 * @param amount
	 * @param num
	 * @return
	 */
	public double[] calcAvgAmt(double amount, int num, boolean[] flags);

	/**
	 * 将金额随机分成多份，每份必须大于minAmt.
	 * @param amount
	 * @param minTransAmt
	 * @param dailyTransNum
	 * @return
	 */
	public double[] calcRandomAmt(double amount, double minTransAmt,double maxTransAmt, int dailyTransNum);

	public double[] calcRandomAmount(double amount, double minTransAmt,double maxTransAmt, int dailyTransNum);

	/**
	 * 生成交易时间
	 * @param paymentAmts
	 * @param currDate
	 * @param startTime
	 * @param endTime
	 * @param minTimeInterval
	 * @param maxTimeInterval
	 * @return
	 */
	public Date[]  transTime(boolean isFirst, double[] paymentAmts, String currDate, String startTime, String endTime, int minTimeInterval, int maxTimeInterval);
	
	/**
	 * 生成还款计划
	 * @param paymentPlanList
	 * @param paymentAmts
	 * @param transTimes
	 * @param paymenApply
	 * @return
	 */
	public BigDecimal generPaymentPlan(boolean isLastItem,List<PaymentPlan> paymentPlanList, List<WithdrawPlan> withdrawPlanList, List<Income> incomeList, double[] paymentAmts, Date[] transTimes,
									   PaymentApply paymenApply, boolean isFirst, FeeRatePO feeRatePO, double serviceChare, BigDecimal left);
	
	/**
	 * 获取客户交易费率
	 * @param custId
	 * @return
	 */
	public FeeRatePO transRate(String custId, String transType);
	
	/**
	 * 获取所胡利率
	 * @return
	 */
	public Map<String, FeeRate> transRateMap();
	
	/**
	 * 获取还款计划
	 * @param custId
	 * @return List<PaymentPlan>
	 */
	public List<Map<String, Object>> findPaymentPlanList(String custId, String cardNo);
	
	/**
	 * 获取还款计划
	 * @param custId
	 * @return List<PaymentPlan>
	 */
	public List<Map<String, Object>> findPaymentPlanListByStatus(String custId, String cardNo, String status);
	
	/**
	 * 获取还款计划
	 * @param id
	 * @return List<PaymentPlan>
	 */
	public PaymentPlan findPaymentPlanDetail(String id);
	
	/**
	 * 获取还款计划:获取当前时间前interval分钟的还款计划进行交易
	 * @param interval
	 * @return List<PaymentPlan>
	 */
	public List<Map<String, Object>> findPaymentPlanList(int interval);

	/**
	 * 查询还款申请
	 * @param custId
	 * @param cardNo
	 * @return
	 */
	public Map<String, Object> findPaymentApply(String custId, String cardNo);

	/**
	 * 查询还款申请
	 * @param custId
	 * @param cardNo
	 * @param strPaymentDate
	 */
	public List<Map<String, Object>> findPaymentPlanListByDate(String applyId,String custId, String cardNo, String strPaymentDate);

	/**
	 * 根据还款异步通知参数 查询还款状态 后续数据处理
	 * @param consumeOrderId
	 * @param orderStatus
	 */
	void doProcessAfter(String consumeOrderId, String orderStatus);

	/**
	 * 更新信用卡开卡状态
	 * @param busiId
	 * @param s
	 */
	void updateCardOpenStatus(String busiId, String s);

	/**
	 * 根据计划id 查询已还金额
	 * @param id
	 * @return
	 */
	Map<String,Object> findFinAmt(String id);

	/**
	 * 计划是否在执行中
	 * @param applyId
	 * @return
	 */
	boolean haveBegun(String applyId);

	/**
	 * 申请还款计划，一次性扣除费用
	 * @param paymenApply
	 * @param applyFee
	 * @param paymentListList
	 * @param withdrawPlanList
	 */
	void doProcessApply(PaymentApply paymenApply, ApplyFee applyFee, List<PaymentPlanPO> paymentListList, List<WithdrawPlanPO> withdrawPlanList);

	/**
	 * 根据id 更改计划执行状态
	 * @param status
	 * @param applyId
	 */
	void updateApplyStatus(String status, Object applyId);

	void saveWithdraw(WithdrawPlanPO withdrawPlanPO);

	List<Map<String,Object>> queryHistoryByCardNo(String cardNo);

	PaymentApplyPO findById(String applyId);

	Map<String,Object> findPaymentObject(String applyId);

	int syncStatus();

	List<Map<String,Object>> findWyrList(String applyId);

    Integer findBalance(String applyId);

    Map<String,Object> findWithdrawMap(String applyId);

	Map<String,Object> findLatestPlanStatus(String accountCode);
}
