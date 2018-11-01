package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.base.utils.DateTimeUtils;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.dao.*;
import com.goldgyro.platform.core.client.domain.*;
import com.goldgyro.platform.core.client.entity.*;
import com.goldgyro.platform.core.client.entity.Dictionary;
import com.goldgyro.platform.core.client.service.PayCardService;
import com.goldgyro.platform.core.comm.utils.SMSUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class PayCardServiceImpl implements PayCardService {
    Logger logger = Logger.getLogger(PayCardServiceImpl.class);

    @Autowired
    private PayCardDao payCardDao;
    @Autowired
    private FeeRateDao feeRateDao;
    @Autowired
    private PaymentApplyDao paymentApplyDao;
    @Autowired
    private PaymentPlanDao paymentPlanDao;
    @Autowired
    private IncomeDao incomeDao;
    @Autowired
    private WithdrawPlanDao withdrawPlanDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private CustIncomeDao custIncomeDao;
    @Autowired
    private CustIncomeDetailDao custIncomeDetailDao;
    @Autowired
    private ApplyFeeDao applyFeeDao;
    @Autowired
    private DictionaryDao dictionaryDao;

    @Autowired
    private BankCardDao bankCardDao;

    @Autowired
    private RedpackDetailDao redpackDetailDao;
    @Value("${goldgyro.normal}")
    private double normal;

    @Value("${goldgyro.vip}")
    private double vip;

    @Value("${goldgyro.agent}")
    private double agent;

    @Value("${goldgyro.member}")
    private double member;

    public double getMember() {
        return member;
    }

    public void setMember(double member) {
        this.member = member;
    }

    public double getNormal() {
        return normal;
    }

    public void setNormal(double normal) {
        this.normal = normal;
    }

    public double getVip() {
        return vip;
    }

    public void setVip(double vip) {
        this.vip = vip;
    }

    public double getAgent() {
        return agent;
    }

    public void setAgent(double agent) {
        this.agent = agent;
    }

    /**
     * 取消还款计划
     *
     * @param applyId
     * @param status
     */
    @Transactional
    public void cancelConfirmedPaymentPlan(String applyId, String status) {
        paymentPlanDao.cancelConfirmedPaymentPlan(status, applyId);
        withdrawPlanDao.cancelWithrawPlan(applyId);
        paymentApplyDao.updateStatusById("CANCEL", applyId);
    }

    /**
     * 更新还款计划
     *
     * @param id
     * @param status
     */
    @Transactional
    public void updatePaymentPlanById(String status, String id) {
        paymentPlanDao.updatePaymentPlanById(status, id);
    }

    /**
     * 生成还款计划
     *
     * @param paymentPlanList
     * @param paymentAmts
     * @param transTimes
     * @param paymenApply
     * @return
     */
    public BigDecimal generPaymentPlan(boolean isLastItem, List<PaymentPlan> paymentPlanList, List<WithdrawPlan> withdrawPlanList, List<Income> incomeList, double[] paymentAmts, Date[] transTimes,
                                       PaymentApply paymenApply, boolean isFirst, FeeRatePO feeRatePO, double serviceChare, BigDecimal left) {
        //计算手续费:第一次交易收取
        double transRate = feeRatePO.getCfeeRate();//0.0068
        double outRate = feeRatePO.getOutRate();//0.0045


        double withdrawTotalAmt = 0;

        if (StringUtils.isEmpty(paymenApply.getId())) {
            paymenApply.setId(UUIDUtils.getUUIDNum(24));
        }
        BigDecimal _left = new BigDecimal("0");
        //还款计划
        for (int i = 0; i < paymentAmts.length; i++) {
            String transCode = UUIDUtils.getUUIDNum(24);
            double transAmt = i == 0 ? (paymentAmts[i] + serviceChare) : paymentAmts[i];
            //交易应付费用额
            BigDecimal b1 = new BigDecimal(String.valueOf(transAmt));
            BigDecimal b2 = new BigDecimal(String.valueOf(1 - transRate));
            BigDecimal a1 = b1.divide(b2, 5, BigDecimal.ROUND_FLOOR);
            BigDecimal b3 = b1.divide(b2, 0, BigDecimal.ROUND_UP);
            _left = b3.subtract(a1);
            left = left.add(_left);
            //交易应付费用额
            transAmt = b3.doubleValue();
            //支付给第三方平台的费用
            double outFee = roundUp(transAmt * outRate);

            generPaymentPlan(paymentPlanList, paymenApply, transAmt, transTimes[i], transCode);
            double transFee = roundUp(transAmt * transRate);
            //收益
            generIncome(incomeList, paymenApply, transFee, outFee, transCode);

            withdrawTotalAmt += paymentAmts[i] + (i == 0 ? 1 : 0) + (isLastItem ? (left.divide(new BigDecimal("1"), 4, BigDecimal.ROUND_FLOOR)).doubleValue() : 0);
        }
        //提现计划
        generWithdrawPlan(withdrawPlanList, paymenApply, transTimes, withdrawTotalAmt);
        return left;
    }

    public void generPaymentPlan(List<PaymentPlan> paymentPlanList, PaymentApply paymenApply, double paymentAmt, Date transTime, String transCode) {
        //还款计划
        PaymentPlan paymentPlan = new PaymentPlan();
        paymentPlan.setId(UUIDUtils.getUUIDNum(24));
        paymentPlan.setPaymentAmt(paymentAmt);
        paymentPlan.setPaymentTime(transTime);
        paymentPlan.setCustId(paymenApply.getCustId());
        paymentPlan.setApplyId(paymenApply.getId());
        paymentPlan.setCardNo(paymenApply.getCardCode());
        paymentPlan.setPlanStatus("CONFIRMED");
        paymentPlan.setTransNo(transCode);

        paymentPlanList.add(paymentPlan);
    }

    public void generWithdrawPlan(List<WithdrawPlan> withdrawPlanList, PaymentApply paymenApply, Date[] transTimes, double withdrawTotalAmt) {
        //提现计划
        WithdrawPlan withdrawPlan = new WithdrawPlan();
        withdrawPlan.setId(UUIDUtils.getUUIDNum(24));
        withdrawPlan.setCustId(paymenApply.getCustId());
        withdrawPlan.setApplyId(paymenApply.getId());
        withdrawPlan.setPaymentDate(DateTimeUtils.formatDate(transTimes[0], "yyyy-MM-dd"));
        withdrawPlan.setWithdrawAmt(withdrawTotalAmt);
        withdrawPlan.setWithdrawDate(transTime(transTimes[1]));
        withdrawPlan.setWithdrawStatus("PLAN");

        withdrawPlanList.add(withdrawPlan);
    }

    public void generIncome(List<Income> incomeList, PaymentApply paymentApply, double transFee, double outFee, String firstTransCode) {
        //收益
        Income income = new Income();
        income.setId(UUIDUtils.getUUIDNum(24));
        income.setBankNo(paymentApply.getCardCode());
        income.setCustId(paymentApply.getCustId());
        income.setTransFee(transFee);
        income.setIncomeAmt((transFee - outFee));
        income.setTransNo(firstTransCode);
        income.setApplyNo(paymentApply.getId());
        income.setTransType("C");
        income.setIncomeStatus("PLAN");

        incomeList.add(income);
    }


    public Date transTime(Date startTime) {
        int maxTimeInterval = 60;
        int minTimeInterval = 180;
        //交易开始时间
        int interval = (int) (Math.random() * (maxTimeInterval - minTimeInterval + 1)) + minTimeInterval;

        return DateTimeUtils.addTime(startTime, interval);
    }

    /**
     * 生成交易时间
     *
     * @param
     * @return
     */
    public Date[] transTime(boolean isFirstDay, double[] paymentAmts, String currDate, String startTime, String endTime, int minTimeInterval, int maxTimeInterval) {
        Date[] transDateTimes = new Date[paymentAmts.length];
        Random random = new Random();
        //交易开始时间
        Date tranStartDate = DateTimeUtils.formatDateTime((currDate + " " + startTime), "yyyy-MM-dd hh:mm");

        if (DateTimeUtils.calcTimeSub(new Date(), tranStartDate) <= 0) {
            tranStartDate = new Date();
        }
        Date today = new Date();
        int interval = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //第一次交易时间
        if (isFirstDay && currDate.equals(df.format(today))) {
            interval = 20;
            transDateTimes[0] = DateTimeUtils.addTime(isBeforeNineOclock(today) ? tranStartDate : today, interval);
        } else {
            interval = random.nextInt(maxTimeInterval - minTimeInterval) + minTimeInterval;
            transDateTimes[0] = DateTimeUtils.addTime(tranStartDate, interval);
        }

        //第二次交易时间
        interval = random.nextInt(maxTimeInterval - minTimeInterval) + minTimeInterval;
        transDateTimes[1] = DateTimeUtils.addTime(transDateTimes[0], interval);
        return transDateTimes;
    }

    public boolean isBeforeNineOclock(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String str = df.format(date) + " 09:00";
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd hh").parse(str);
            return date.getTime() < date1.getTime();
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 将指定的金额分成多份，保留两位小数，最后一位确保所有金额之和等于总额
     *
     * @param amount
     * @param num
     * @return
     */
    public double[] calcAvgAmt(double amount, int num, boolean[] flags) {
        /*int sum = 0;
        for (boolean b : flags) {
            if (b) {
                sum += 3;
            } else {
                sum += 2;
            }
        }
        double[] results = new double[num];
        double av = Math.ceil(amount / sum);
        Stack<Double> ds = new Stack<>();
        for (int i = 0; i < sum - 1; i++) {
            ds.push(av);
        }
        ds.push(amount - av * (sum - 1));
        for (int i = 0; i < num; i++) {
            results[i] = ds.pop();
            if (flags[i]) {
                results[i] += ds.pop();
                results[i] += ds.pop();
            } else {
                results[i] += ds.pop();
            }
        }*/
        double[] results = new double[num];
        double average = Math.ceil(amount / num);
        for (int i = 1; i < num; i++) {
            results[i] = average;
        }
        results[0] = amount - average * (num - 1);
        return results;
    }

    /**
     * 将金额随机分成2份，每份必须大于minAmt.
     *
     * @param amount
     * @param minTransAmt
     * @param dailyTransNum
     * @return
     */
    public double[] calcRandomAmt(double amount, double minTransAmt, double maxTransAmt, int dailyTransNum) {
        if (amount > (minTransAmt + maxTransAmt)) {
            return cac(amount, minTransAmt, maxTransAmt, dailyTransNum);
        }
        double[] results = new double[dailyTransNum];
        for (int i = 0; i < dailyTransNum; i++) {
            results[i] = minTransAmt;
        }
        amount = amount - dailyTransNum * minTransAmt;
        double dblSum = 0;
        // 每次还款金额
        for (int i = 0; i < dailyTransNum - 1; i++) {

            double v = Math.round(amount * (Math.random()));
            //随机产生交易额
            results[i] = results[i] + v;

            //计算交易费用
            dblSum += v;
        }
        results[dailyTransNum - 1] = results[dailyTransNum - 1] + amount - dblSum;

        return results;
    }

    public double[] calcRandomAmount(double amount, double minTransAmt, double maxTransAmt, int dailyTransNum) {
        if (dailyTransNum == 2) {
            if (amount > minTransAmt + maxTransAmt) {
                return cac(amount, minTransAmt, maxTransAmt, dailyTransNum);
            }
            double results[] = new double[dailyTransNum];
            for (int i = 0; i < results.length; i++) {
                results[i] = minTransAmt;
            }
            double remain = amount - (minTransAmt * dailyTransNum);
            double done = 0.0;
            for (int i = 0; i < dailyTransNum - 1; i++) {
                double partRatio = Math.random() / (dailyTransNum - 1);
                results[i] += Math.ceil(partRatio * remain);
                done += results[i];
            }
            results[dailyTransNum - 1] = amount - done;
            return results;
        }
        return null;
    }

    private double[] cac(double amount, double minTransAmt, double maxTransAmt, int dailyTransNum) {
        double results[] = new double[dailyTransNum];
        results[0] = Math.round(amount / 2 + (maxTransAmt - amount / 2) * Math.random());
        results[1] = amount - results[0];
        if (results[0] - results[1] >= 2) {
            results[0] = results[0] - 1;
            results[1] = results[1] + 1;
        }
        if (results[1] - results[0] >= 2) {
            results[1] = results[1] - 1;
            results[0] = results[0] + 1;
        }
        return results;
    }

    /**
     * 获取商户注册信息
     */
    public Map<String, Object> findBusiRegistrations(String custId) {
        return payCardDao.findBusiRegistrations(custId);
    }

    @Override
    @Transactional
    public void updateMerchantRegistStatus(@Param(value = "merchCode") String merchCode, @Param(value = "status") String status) throws Exception {
        payCardDao.updateMerchantRegistStatus(status, merchCode);
    }

    public Map<String, Object> findCreditCardBind(String custId, String accountCode) {
        return payCardDao.findCreditCardBind(custId, accountCode);
    }


    /**
     * 查询需要绑卡的信用卡信息
     *
     * @return
     */
    public List<Map<String, Object>> findCardBindList() {
        return payCardDao.findCardBindList();
    }

    /**
     * 更新信用卡注册状态
     *
     * @param id
     */
    @Override
    @Transactional
    public void updateCardBindStatus(@Param(value = "id") String id, @Param(value = "status") String status) throws Exception {
        payCardDao.updateCardBindStatus(id, status);
    }

    /**
     * 保存还款申请、还款计划信息
     *
     * @param paymenApply
     */
    @Override
    @Transactional
    public void savePaymentApplyAndPlan(PaymentApply paymenApply, List<PaymentPlan> paymentPlanList, List<WithdrawPlan> withdrawPlanList, List<Income> incomeList) throws Exception {
        //还款申请
        PaymentApplyPO paymentApplyPO = new PaymentApplyPO();
        BeanUtils.copyProperties(paymenApply, paymentApplyPO);

        if (null == paymentApplyPO.getId()) {
            paymentApplyPO.setId(UUIDUtils.getUUIDNum(24));
        }
        paymentApplyDao.saveAndFlush(paymentApplyPO);

        //还款计划
        for (PaymentPlan paymentPlan : paymentPlanList) {
            PaymentPlanPO paymentPlanPO = new PaymentPlanPO();
            BeanUtils.copyProperties(paymentPlan, paymentPlanPO);

            if (null == paymentPlanPO.getId()) {
                paymentPlanPO.setId(UUIDUtils.getUUIDNum(24));
            }
            paymentPlanPO.setApplyId(paymenApply.getId());

            paymentPlanDao.saveAndFlush(paymentPlanPO);
        }

        //提现计划
        for (WithdrawPlan withdrawPlan : withdrawPlanList) {
            WithdrawPlanPO withdrawPlanPO = new WithdrawPlanPO();
            BeanUtils.copyProperties(withdrawPlan, withdrawPlanPO);

            withdrawPlanDao.saveAndFlush(withdrawPlanPO);
        }

        for (Income income : incomeList) {
            //收益
            IncomePO incomePO = new IncomePO();
            BeanUtils.copyProperties(income, incomePO);

            if (null == incomePO.getId()) {
                incomePO.setId(UUIDUtils.getUUIDNum(24));
            }

            incomeDao.saveAndFlush(incomePO);
        }
    }

    /**
     * 获取客户交易费率
     * //	 * @param custId
     *
     * @return
     */
    public FeeRatePO transRate(String levelSample, String transType) {
        return feeRateDao.findFeeRateByClevelSampleAndTtypeCode(levelSample, transType);
    }

    /**
     * 获取所胡利率
     *
     * @return
     */
    public Map<String, FeeRate> transRateMap() {
        List<FeeRatePO> feeRateList = feeRateDao.findFeeRateListBy();

        Map<String, FeeRate> feeRateMaps = new HashMap<String, FeeRate>();
        if (null == feeRateList || feeRateList.size() < 1) {
            return feeRateMaps;
        }

        for (FeeRatePO feeRatePO : feeRateList) {
            FeeRate feeRate = new FeeRate();
            BeanUtils.copyProperties(feeRatePO, feeRate);
            feeRateMaps.put(feeRatePO.getClevelSample(), feeRate);
        }

        return feeRateMaps;
    }

    /**
     * 获取还款计划:获取当前时间前interval分钟的还款计划进行交易
     *
     * @param interval
     * @return List<PaymentPlan>
     */
    public List<Map<String, Object>> findPaymentPlanList(int interval) {
        return paymentPlanDao.findPaymentPlan(interval);
    }

    /**
     * 获取还款计划
     *
     * @param custId
     * @return List<PaymentPlan>
     */
    public List<Map<String, Object>> findPaymentPlanList(String custId, String cardNo) {
        return paymentPlanDao.findPaymentPlanList(custId, cardNo, custId, cardNo);
    }

    /**
     * 获取还款计划
     *
     * @param custId
     * @return List<PaymentPlan>
     */
    public List<Map<String, Object>> findPaymentPlanListByStatus(String custId, String cardNo, String status) {
        return paymentPlanDao.findPaymentPlanListByStatus(custId, cardNo, custId, cardNo, status);
    }

    /**
     * 获取还款计划
     * //	 * @param custId
     *
     * @return List<PaymentPlan>
     */
    public PaymentPlan findPaymentPlanDetail(String id) {
        PaymentPlanPO paymentPlanPO = paymentPlanDao.findPaymentPlanById(id);
        if (null == paymentPlanPO) {
            return null;
        }

        PaymentPlan paymentPlan = new PaymentPlan();
        BeanUtils.copyProperties(paymentPlanPO, paymentPlan);

        return paymentPlan;
    }

    /**
     * 查询还款申请
     *
     * @param custId
     * @param cardNo
     * @return
     */
    @Override
    public Map<String, Object> findPaymentApply(String custId, String cardNo) {
        return paymentApplyDao.findPayementApply(custId, cardNo, custId, cardNo);
    }

    /**
     * 查询还款申请
     *
     * @param custId
     * @param cardNo
     * @param strPaymentDate
     */
    public List<Map<String, Object>> findPaymentPlanListByDate(String applyId, String custId, String cardNo, String strPaymentDate) {
        if (StringUtils.isEmpty(applyId)) {
            return paymentPlanDao.findPaymentPlanListByDate(custId, cardNo, custId, cardNo, strPaymentDate);
        } else {
            return paymentPlanDao.findPaymentPlanListByApplyId(applyId, strPaymentDate);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doProcessAfter(String consumeOrderId, String orderStatus) {
        ApplyFee applyFee = applyFeeDao.findByMaxTime(consumeOrderId);
        if ("01".equals(orderStatus)) {
            paymentPlanDao.updatePlanStatusByTransNo("REPAY_SUCCESS", consumeOrderId);
            if (applyFee != null) {
                List list = doProfitNew(applyFee);
                if (list != null && !list.isEmpty()) {
                    custIncomeDao.save((List<CustIncomePO>) list.get(0));
                    custIncomeDetailDao.save((List<CustIncomeDetailPO>)list.get(1));
                    applyFee.setFlag("1");
                    applyFeeDao.saveAndFlush(applyFee);
                }
                CustomerPO customerPO = customerDao.findByTranNo(consumeOrderId);
                String levelCode = null;
                if(customerPO !=null && !StringUtils.isEmpty((levelCode=customerPO.getLevelCode()))){
                    String code = levelCode.substring(0,levelCode.length()-3);
                    CustomerPO v1 = customerDao.findV1(code);
                    CustomerPO v2 = customerDao.findV2(code);
                    CustomerPO v3 = customerDao.findV3(code);
                    List<CustomerPO> custList = new ArrayList<>();
                    if(v1!=null){
                        custList.add(v1);
                    }
                    if(v2!=null){
                        custList.add(v2);
                    }
                    if(v3!=null){
                        custList.add(v3);
                    }
                    if(custList.isEmpty()){
                        return;
                    }
                    Collections.sort(custList);
                    int bigLevel = 0;
                    for(int i=0;i<custList.size();i++){
                        CustomerPO p = custList.get(i);
                        int level = produceLevel(p);
                        if(level>bigLevel){
                            int t = level;
                            level = level-bigLevel;
                            bigLevel = t;
                        }else {
                            continue;
                        }
                        IncomeUtils incomeUtils = new IncomeUtils(applyFee, p, level).invoke();
                        CustIncomePO custIncomePO = incomeUtils.getCustIncomePO();
                        CustIncomeDetailPO custIncomeDetailPO = incomeUtils.getCustIncomeDetailPO();
                        custIncomeDao.saveAndFlush(custIncomePO);
                        custIncomeDetailDao.saveAndFlush(custIncomeDetailPO);
                    }
                }
            }
        } else if ("02".equals(orderStatus)) {
            SMSUtils.sendRegist("15202814761", "transNo=" + consumeOrderId + "消费失败");
            paymentPlanDao.updatePlanStatusByTranNo(consumeOrderId, "REPAY_FAIL");
            withdrawPlanDao.cancelByTransNo(consumeOrderId);
        }
    }
    private int produceLevel(CustomerPO p1) {
        int d = p1.getDirectNum();
        int p = p1.getGroupNum();
        int level = 0;
        if((d>=10 && d<20 && p>=100) || (d>=10 && p>=100 && p<200)){
            level = 1;
        }else if((d>=20 && d<30 && p>=200) || (d>=20 && p>=200 && p<300)){
            level = 2;
        }else if(d>=30 && p>=300){
            level = 3;
        }
        return level;
    }

    private List doProfitNew(ApplyFee applyFee) {
        PaymentApplyPO paymentApply = paymentApplyDao.findPaymentApplyPOById(applyFee.getApplyId());
        CustomerPO customerPO = customerDao.findByCustId(paymentApply.getCustId());
        if (customerPO == null || StringUtils.isEmpty(customerPO.getCustLevelSample()) || "AGENT".equals(customerPO.getCustLevelSample()) || StringUtils.isEmpty(customerPO.getFatherMobile())) {
            return null;
        }
        //为了兼容原来的代码 如果是普通用户且其直推超过10人 分润是按会员处理
        if("NORMAL".equals(customerPO.getCustLevelSample()) && "1".equals(customerPO.getFreeMember())){
            customerPO.setCustLevelSample("MEMBER");
        }
        List<CustomerPO> custList = customerDao.findNotNormal(customerPO.getLevelCode());
        CustomerPO m = null;//上级第一个会员
        CustomerPO v = null;//上级第一个VIP
        CustomerPO a = null;//上级第一个代理
        switch (customerPO.getCustLevelSample()){
            case "NORMAL":
                m = fetch(custList,"MEMBER");
                v = fetch(custList,"VIP");
                a = fetch(custList,"AGENT");
                break;
            case "MEMBER":
                v = fetch(custList,"VIP");
                a = fetch(custList,"AGENT");
                break;
            case "VIP":
                a = fetch(custList,"AGENT");
                break;
            default:
                break;
        }
        List<CustIncomePO> incomeList = new ArrayList<>();
        List<CustIncomeDetailPO> detailList = new ArrayList<>();
        double dif = 0.0;
        if(m != null){
            List list = getList(applyFee, customerPO, m,dif);
            if(list != null && !list.isEmpty()){
                incomeList.add((CustIncomePO) list.get(1));
                detailList.add((CustIncomeDetailPO) list.get(0));
                dif = (double) list.get(2);
            }
        }
        if(v != null){
            List list = getList(applyFee, customerPO, v,dif);
            incomeList.add((CustIncomePO) list.get(1));
            detailList.add((CustIncomeDetailPO) list.get(0));
            dif = (double) list.get(2);
        }
        if(a != null){
            List list = getList(applyFee, customerPO, a,dif);
            incomeList.add((CustIncomePO) list.get(1));
            detailList.add((CustIncomeDetailPO) list.get(0));
        }
        if(incomeList.isEmpty()){
            return null;
        }
        List list = new ArrayList();
        list.add(incomeList);
        list.add(detailList);
        return list;
    }

    private List getList(ApplyFee applyFee, CustomerPO customerPO, CustomerPO father,double dif) {
        if (father == null || StringUtils.isEmpty(father.getCustLevelSample())) {
            return null;
        }
        String level1 = customerPO.getCustLevelSample();
        String level2 = father.getCustLevelSample();
        double difRatio = 0.00;
        double diff = 0.00;
        if (level1.equals(level2)) {
            return null;
        } else if ("NORMAL".equals(level1)) {
            if ("MEMBER".equals(level2)) {
                difRatio = new BigDecimal(this.normal).subtract(new BigDecimal(this.member)).doubleValue();
            } else if ("VIP".equals(level2)) {
                difRatio = new BigDecimal(this.normal).subtract(new BigDecimal(this.vip)).doubleValue();
            } else {
                difRatio = new BigDecimal(this.normal).subtract(new BigDecimal(this.agent)).doubleValue();
            }
        } else if ("MEMBER".equals(level1)) {
            if ("VIP".equals(level2)) {
                difRatio = new BigDecimal(this.member).subtract(new BigDecimal(this.vip)).doubleValue();
            } else {
                difRatio = new BigDecimal(this.member).subtract(new BigDecimal(this.agent)).doubleValue();
            }

        } else {
            difRatio = new BigDecimal(this.vip).subtract(new BigDecimal(this.agent)).doubleValue();
        }
        diff = difRatio;
        difRatio = new BigDecimal(difRatio).subtract(new BigDecimal(dif)).doubleValue();
        double totalIncome = Math.floor(applyFee.getTotalFee() * difRatio);
        CustIncomeDetailPO custIncomeDetailPO = new CustIncomeDetailPO();
        custIncomeDetailPO.setCustId(father.getCustId());
        custIncomeDetailPO.setCreatedTime(new Timestamp(new Date().getTime()));
        custIncomeDetailPO.setIncomeAmt(totalIncome);
        custIncomeDetailPO.setCustName(father.getCustName());
        custIncomeDetailPO.setTransType("C");
        custIncomeDetailPO.setTransIncomeId(applyFee.getId());
        custIncomeDetailPO.setDescription("计划收益");
        CustIncomePO custIncomePO = instanceIncome(father.getCustId());
        double currentIncomeAmt = custIncomePO.getTotalIncomeAmt();
        double nowIncomeAmt = currentIncomeAmt + custIncomeDetailPO.getIncomeAmt();
        custIncomePO.setTotalIncomeAmt(nowIncomeAmt);
        List list = new ArrayList();
        list.add(custIncomeDetailPO);
        list.add(custIncomePO);
        list.add(diff);
        return list;
    }

    private CustomerPO fetch(List<CustomerPO> custList, String agent) {
        for(CustomerPO c: custList){
            if(agent.equals(c.getCustLevelSample())){
                return c;
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCardOpenStatus(String busiId, String status) {
        payCardDao.updateCardOpenStatus(busiId, status);
    }

    @Override
    public Map<String, Object> findFinAmt(String id) {
        return payCardDao.finFinAmt(id);
    }

    @Override
    public boolean haveBegun(String applyId) {
        List list = paymentPlanDao.haveBegun(applyId);
        return !list.isEmpty();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doProcessApply(PaymentApply paymenApply, ApplyFee applyFee, List<PaymentPlanPO> paymentListList, List<WithdrawPlanPO> withdrawPlanList) {
       /* List<PaymentApplyPO> list = paymentApplyDao.queryByCustId(paymenApply.getCustId());
        if (list == null || list.isEmpty()) {
            Random random = new Random();
            int num = random.nextInt(500) + 500;
            //第一次申请还款计划 分钱了
            CustomerPO self = customerDao.findByCustId(paymenApply.getCustId());//申请人
            CustomerPO inviteCustomer = customerDao.findByCustMobile(self.getInviterId());//申请人推荐人
            if (inviteCustomer != null && !StringUtils.isEmpty(inviteCustomer.getCustLevelSample())) {
                Redpack redpack = redpackDao.findRedpackByCustId(inviteCustomer.getCustId());
                redpack.setBalance(String.valueOf(redpack.getBalance() == null ? 0 : (Integer.valueOf(redpack.getBalance()) + num)));
                redpackDao.save(redpack);
                RedpackDetail redpackDetail = new RedpackDetail(UUID.randomUUID().toString().replaceAll("-", ""), redpack.getId(), num, "邀请用户成功还款奖励", new Timestamp(new Date().getTime()));
                redpackDetailDao.saveAndFlush(redpackDetail);
            }
        }*/
        PaymentApplyPO paymentApplyPO = new PaymentApplyPO();
        BeanUtils.copyProperties(paymenApply, paymentApplyPO);
        paymentApplyPO.setStatus("PROCESSING");
        paymentApplyDao.saveAndFlush(paymentApplyPO);
        BankCardPO bankCard = bankCardDao.findBankCardByCustIdAndAccountCode(paymentApplyPO.getCustId(), paymentApplyPO.getCardCode());
        boolean flag = false;
        if(bankCard.getDefaultArea()==null || "".equals(bankCard.getDefaultArea()) || !bankCard.getDefaultArea().equals(paymentApplyPO.getCity())){
            bankCard.setDefaultArea(paymentApplyPO.getCity());
            flag = true;
        }
        if(paymenApply.getAccountantBillDate() != null && paymenApply.getAccountantBillDate()>0 && paymenApply.getAccountantBillDate()<31){
            bankCard.setAccountantBillDate(paymenApply.getAccountantBillDate());
            flag = true;
        }
        if(paymenApply.getAccountantDays() != null && paymenApply.getAccountantDays() > 0){
            bankCard.setAccountantDays(paymenApply.getAccountantDays());
            flag = true;
        }
        if(flag){
            bankCardDao.save(bankCard);
        }
        applyFeeDao.saveAndFlush(applyFee);
        List<Dictionary> dictList = dictionaryDao.findAllByParentCodeOrderByCodeAsc("MCC");
        int size = dictList.size();
        if (size > 0) {
            for (PaymentPlanPO p : paymentListList) {
                Date paymentTime = p.getPaymentTime();
                int index = randomIndex(paymentTime);
                p.setMccCode(dictList.get(index).getCode());
            }
        }
        paymentPlanDao.save(paymentListList);
        withdrawPlanDao.save(withdrawPlanList);
    }

    private int randomIndex(Date paymentTime) {
        int index = 0;
        int[] array = null;
        if (isBetween(paymentTime, "09:00", "11:00")) {
            array = new int[]{0,2,3,4,5,8};
        } else if (isBetween(paymentTime, "11:00", "13:00")) {
            array = new int[]{0,2,3,5,4,8,1,10,11};
        } else if (isBetween(paymentTime, "13:00", "17:00")) {
           array = new int[]{0,2,3,5,4,8,1,10,11,6};
        } else if (isBetween(paymentTime, "17:00", "19:00")) {
            array = new int[]{0,3,5,4,8,1,10,11};
        } else if (isBetween(paymentTime, "19:00", "22:00")) {
            array = new int[]{0,2,3,5,4,8,1,10,11,6};
        } else {
            array = new int[]{8,9,10};
        }
        Random random = new Random();
        int i = random.nextInt(array.length);
        return array[i];
    }
    private boolean isBetween(Date paymentTime, String s, String s1) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date start = df1.parse(df.format(paymentTime) + " " + s);
            Date end = df1.parse(df.format(paymentTime) + " " + s1);
            return paymentTime.getTime() >= start.getTime() && paymentTime.getTime() <= end.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    @Transactional
    public void updateApplyStatus(String status, Object applyId) {
        if (applyId != null) {
            paymentApplyDao.updateStatusById(status, applyId.toString());
        }
    }

    @Override
    @Transactional
    public void saveWithdraw(WithdrawPlanPO withdrawPlanPO) {
        withdrawPlanDao.saveAndFlush(withdrawPlanPO);
        if ("SUCCESS".equalsIgnoreCase(withdrawPlanPO.getWithdrawStatus())) {
            List list = withdrawPlanDao.isLastOne(withdrawPlanPO.getId());
            if (list != null && !list.isEmpty()) {
                paymentApplyDao.updateStatusById("SUCCESS", withdrawPlanPO.getApplyId());
            }
        }
    }

    @Override
    public List<Map<String, Object>> queryHistoryByCardNo(String cardNo) {
        return paymentApplyDao.queryHistoryByCardNo(cardNo);
    }

    @Override
    public PaymentApplyPO findById(String applyId) {
        return paymentApplyDao.findPaymentApplyPOById(applyId);
    }

    @Override
    public Map<String, Object> findPaymentObject(String applyId) {
        return paymentApplyDao.findPenmentObject(applyId);
    }

    @Override
    @Transactional
    public int syncStatus() {
        List<PaymentApplyPO> list = paymentApplyDao.findAll();
        for (PaymentApplyPO paymentApplyPO : list) {
//            List<WithdrawPlanPO> l1 = withdrawPlanDao.findByApplyId("",)
        }
        return 0;
    }

    @Override
    public List<Map<String, Object>> findWyrList(String applyId) {
        return paymentApplyDao.findWyrList(applyId);
    }

    @Override
    public Integer findBalance(String applyId) {
        return paymentApplyDao.findBalance(applyId);
    }

    @Override
    public Map<String, Object> findWithdrawMap(String applyId) {
        return paymentApplyDao.findWithdrawMap(applyId);
    }

    @Override
    public Map<String, Object> findLatestPlanStatus(String accountCode) {
        return paymentApplyDao.findLatestPlanStatus(accountCode);
    }

    private CustIncomePO instanceIncome(String custId) {
        CustIncomePO custIncomePO = custIncomeDao.findByCustId(custId);
        if (custIncomePO == null) {
            custIncomePO = new CustIncomePO();
            custIncomePO.setTotalIncomeAmt(0);
            custIncomePO.setCreatedTime(new Timestamp(new Date().getTime()));
            custIncomePO.setCustId(custId);
            custIncomePO.setTransType("C");
        }
        return custIncomePO;
    }

    /**
     * 保留两位小数
     *
     * @param num
     * @return
     */
    private double fix2dot(double num) {
        String strD = String.valueOf(num * 100);
        String[] strArr = strD.split("\\.");

        return Double.parseDouble(strArr[0]) / 100;

    }

    /**
     * 递归查询推荐人返回第一个vip 或 agent用户
     *
     * @param mobile
     * @return
     */
    private CustomerPO recursionInviter(String mobile, int maxTimes) {
        if (maxTimes > 10) {
            return null;
        }
        List<CustomerPO> customerPOList = customerDao.findAllByCustMobile(mobile);
        if (customerPOList.isEmpty()) {
            return null;
        }

        CustomerPO customer = customerPOList.get(0);
        if (customer == null) {
            return null;
        } else if ("VIP".equals(customer.getCustLevelSample()) || "AGENT".equals(customer.getCustLevelSample())) {
            return customer;
        } else {
            return recursionInviter(customer.getInviterId(), ++maxTimes);
        }
    }


    /**
     * 进位，如果小数点后多于两位，直接进位
     *
     * @param n
     * @return
     */
    private double roundUp(double n) {
        if (sizeOf(n) <= 2) {
            return n;
        }
        return (Math.floor(100 * n) + 1) / 100;
    }

    /**
     * 计算小数点后的位数
     *
     * @param d
     * @return
     */
    private int sizeOf(double d) {
        String str = String.valueOf(d);
        String s = str.substring(str.indexOf(".") + 1);
        return s.length();
    }

    private class IncomeUtils {
        private ApplyFee applyFee;
        private CustomerPO p1;
        private int level;
        private CustIncomePO custIncomePO;
        private CustIncomeDetailPO custIncomeDetailPO;

        public IncomeUtils(ApplyFee applyFee, CustomerPO p1, int level) {
            this.applyFee = applyFee;
            this.p1 = p1;
            this.level = level;
        }

        public CustIncomePO getCustIncomePO() {
            return custIncomePO;
        }

        public CustIncomeDetailPO getCustIncomeDetailPO() {
            return custIncomeDetailPO;
        }

        public IncomeUtils invoke() {
            double profit = Math.floor(applyFee.getTotalFee()*level/10000);
            custIncomePO = instanceIncome(p1.getCustId());
            custIncomePO.setTotalIncomeAmt(custIncomePO.getTotalIncomeAmt()+profit);
            custIncomeDetailPO = new CustIncomeDetailPO();
            custIncomeDetailPO.setCustId(p1.getCustId());
            custIncomeDetailPO.setCreatedTime(new Timestamp(new Date().getTime()));
            custIncomeDetailPO.setIncomeAmt(profit);
            custIncomeDetailPO.setCustName(p1.getCustName());
            custIncomeDetailPO.setTransType("C");
            custIncomeDetailPO.setTransIncomeId(applyFee.getId());
            custIncomeDetailPO.setDescription("团队奖励");
            return this;
        }
    }
}
