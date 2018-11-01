package com.goldgyro.platform.foreground.controller;

import com.goldgyro.platform.base.utils.DateTimeUtils;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.domain.PaymentApply;
import com.goldgyro.platform.core.client.domain.PaymentPlan;
import com.goldgyro.platform.core.client.entity.ApplyFee;
import com.goldgyro.platform.core.client.entity.FeeRatePO;
import com.goldgyro.platform.core.client.entity.PaymentPlanPO;
import com.goldgyro.platform.core.client.entity.WithdrawPlanPO;
import com.goldgyro.platform.core.client.service.BankCardService;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.client.service.PayCardService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.foreground.constants.MessageConstant;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/apply")
public class ApplyScheduleContoller {
    @Autowired
    private PayCardService payCardService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BankCardService bCardService;

    @RequestMapping(value = "/paymentApply", method = RequestMethod.POST)
    public InterfaceResponseInfo paymentApplyNew(@RequestBody(required = false) PaymentApply paymenApply) {
        if (null == paymenApply) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还卡】还款申请信息必须填写！", null);
        }
        if (StringUtils.isEmpty(paymenApply.getCustId())) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还卡】客户信息为空！", null);
        }
        if (StringUtils.isEmpty(paymenApply.getCardCode())) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还卡】客户银行卡号为空！", null);
        }
        if (0 == paymenApply.getPaymentAmt()) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还卡】还款金额不能小于等0！", null);
        }
        if (StringUtils.isEmpty(paymenApply.getPaymentDate())) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还卡】还款日期不能为空！", null);
        }

        //验证卡是否已开卡
        BankCard bCard = bCardService.findBankCardByCustIdAndAccountCode(paymenApply.getCustId(), paymenApply.getCardCode());
        if (!"OPEN_SUCCESS".equalsIgnoreCase(bCard.getOpenStatus())) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还款申请】卡还未开通！", null);
        }

        //时间验证
        Date maxTime = DateTimeUtils.formatDateTime(DateTimeUtils.formatDateTime(new Date(), "yyyy-MM-dd") + " 18:00", "yyyy-MM-dd HH:mm");

        if (paymenApply.getPaymentDate().indexOf(DateTimeUtils.formatDateTime(new Date(), "yyyy-MM-dd")) >= 0
                && DateTimeUtils.calcTimeSub(maxTime, new Date()) > 0) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还款申请】当天还款，预留时间不足,请在18:00之前申请！", null);
        }


        //验证是否存在正在执行的还款计划
        List<Map<String, Object>> validPaymentPlanList = payCardService.findPaymentPlanListByStatus(paymenApply.getCustId(), paymenApply.getCardCode(), "CONFIRMED");

        if (null != validPaymentPlanList && validPaymentPlanList.size() > 0) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还款申请】当前存在有效的还款计划，不能添加新的还款计划！", null);
        }
        List<PaymentPlanPO> paymentListList = new ArrayList<>();
        List<WithdrawPlanPO> withdrawPlanList = new ArrayList<>();
        String applyId = UUIDUtils.getUUIDNum(24);
        paymenApply.setId(applyId);
        ApplyFee applyFee = initApplyFee(paymenApply);
        //还款日期
        String[] paymentDates = paymenApply.getPaymentDate().split(",");
        boolean[] dayOfWeeks = getDayOfWeeks(paymentDates);
        for (int i = 0; i < dayOfWeeks.length; i++) {
//            dayOfWeeks[i] = bCard.getBankName().indexOf("光大") == -1;
            dayOfWeeks[i] = true;
        }
        double maxApply = 0;
        double minApply = 0;
        for (boolean b : dayOfWeeks) {
            if (b) {
                maxApply += 3 * PayChannelConstants.MAX_TRANS_AMT - Math.ceil(applyFee.getTotalFee() / 100) + paymenApply.getPaymentAmt();
                minApply += 3 * PayChannelConstants.MIN_TRANS_AMT;
            } else {
                maxApply += 2 * PayChannelConstants.MAX_TRANS_AMT - Math.ceil(applyFee.getTotalFee() / 100) + paymenApply.getPaymentAmt();
                minApply += 2 * PayChannelConstants.MIN_TRANS_AMT;
            }
        }
//        maxApply -= Math.ceil(applyFee.getTotalFee() / 100) - paymenApply.getPaymentAmt();
        if (minApply > paymenApply.getPaymentAmt()) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还款申请】还款计划失败，所选日期最低还款额为：￥" + minApply + "元，建议缩短还款日期。", null);
        }
        if (maxApply < paymenApply.getPaymentAmt()) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还款申请】还款计划失败，所选日期最大还款额为：￥" + maxApply + "元，建议延长还款日期。", null);
        }
        //每天还款金额
        double[] dayPaymentAmts = calcAvgAmt(applyFee, paymenApply.getPaymentAmt(), paymentDates.length, dayOfWeeks);
        int i = 0;
        for (double paymentAmt : dayPaymentAmts) {
            if (i == 0) {
                generateData(paymenApply, paymentDates[i], paymentAmt, paymentListList, withdrawPlanList, applyFee, dayOfWeeks[i]);
            } else {
                generateData(paymenApply, paymentDates[i], paymentAmt, paymentListList, withdrawPlanList, null, dayOfWeeks[i]);
            }
            i++;
        }
        payCardService.doProcessApply(paymenApply, applyFee, paymentListList, withdrawPlanList);
        Map<String, String> map = new HashMap<>();
        map.put("applyId", paymenApply.getId());
        return new InterfaceResponseInfo(MessageConstant.OK, "【还款申请】还款申请信息保存成功！", map);
    }

    private double[] calcAvgAmt(ApplyFee applyFee, double paymentAmt, int length, boolean[] dayOfWeeks) {
        double totalFee = Math.ceil(applyFee.getTotalFee() / 100 - paymentAmt);
        double[] results = new double[length];
        double average = Math.ceil(paymentAmt / length);
        if (average + totalFee > 3 * PayChannelConstants.MAX_TRANS_AMT) {
//            if ((paymentAmt - 3 * PayChannelConstants.MIN_TRANS_AMT) > (length - 1) * (3 * PayChannelConstants.MAX_TRANS_AMT)) {
            average = 3 * PayChannelConstants.MAX_TRANS_AMT;
            /*}else {

            }*/
        }
        for (int i = 1; i < length; i++) {
            results[i] = average;
        }
        results[0] = paymentAmt - average * (length - 1);
        return results;
    }

    private boolean[] getDayOfWeeks(String[] paymentDates) {
        boolean[] days = new boolean[paymentDates.length];
        Calendar c = Calendar.getInstance();
        int i = 0;
        for (String date : paymentDates) {
            Date d = DateTimeUtils.formatDateTime(date, "yyyy-MM-dd");
            c.setTime(d);
            if (i > 0) {
                days[i++] = c.get(Calendar.DAY_OF_WEEK) == 1 || c.get(Calendar.DAY_OF_WEEK) > 5;
            } else {
                days[i++] = false;
            }
        }
        return days;
    }

    private void generateData(PaymentApply apply, String paymentDate, double paymentAmt, List<PaymentPlanPO> paymentListList, List<WithdrawPlanPO> withdrawPlanList, ApplyFee applyFee, boolean flag) {
        boolean f = applyFee != null;
        int timesOneDay = flag ? 3 : 2;
        double[] onceAmount = calcRandomAmount(paymentAmt, PayChannelConstants.MAX_TRANS_AMT, timesOneDay);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        boolean isToday = paymentDate.equals(dateFormat.format(new Date()));
        Date[] transTime = null;
        if (!isToday) {
            transTime = transTime(f, onceAmount, paymentDate, PayChannelConstants.TRANS_START_TIME, PayChannelConstants.MIN_TIME_INTERVAL, PayChannelConstants.MAX_TIME_INTERVAL);
        } else {
            int maxInterval = calcInterval(f, timesOneDay);
            int minInterval = 0;
            if (maxInterval > 180) {
                minInterval = 90;
            } else {
                minInterval = maxInterval / 2;
            }
            transTime = transTime(f, onceAmount, paymentDate, PayChannelConstants.TRANS_START_TIME, minInterval, maxInterval);
        }
        WithdrawPlanPO withdrawPlanPO = initWithdrawApply(paymentAmt, apply, transTime[transTime.length - 1]);
        if (f) {
            onceAmount = calcRandomAmount(paymentAmt + applyFee.getTotalFee() / 100.0 - apply.getPaymentAmt(), PayChannelConstants.MAX_TRANS_AMT, timesOneDay);
        }
        processPaymentPlan(onceAmount, transTime, apply, paymentListList);
        withdrawPlanList.add(withdrawPlanPO);
    }

    private int calcInterval(boolean f, int timesOneDay) {
        Date now = new Date();
        if (f) {
            timesOneDay = 3;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endStr = df.format(now) + " 23:59:59";
        Date end = null;
        try {
            end = dateFormat.parse(endStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long interval = (end.getTime() - now.getTime()) / (1000 * 60) - 120;
        return (int) (interval / timesOneDay);
    }

    public static Date[] transTime(boolean isFirstDay, double[] paymentAmts, String currDate, String startTime, int minTimeInterval, int maxTimeInterval) {
        Date[] transDateTimes = new Date[paymentAmts.length];
        Random random = new Random();
        //交易开始时间
        Date tranStartDate = DateTimeUtils.formatDateTime((currDate + " " + startTime), "yyyy-MM-dd hh:mm");

        if (tranStartDate.before(new Date())) {
            tranStartDate = new Date();
        }
        Date today = new Date();
        int interval = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < transDateTimes.length; i++) {
            if (i == 0) {
                if (isFirstDay && currDate.equals(df.format(today))) {
                    transDateTimes[0] = DateTimeUtils.addTime(isBeforeNineOclock(today) ? tranStartDate : today, 30);
                } else {
                    interval = random.nextInt(maxTimeInterval - minTimeInterval) + minTimeInterval;
                    transDateTimes[0] = DateTimeUtils.addTime(tranStartDate, interval);
                }
            } else {
                interval = random.nextInt(maxTimeInterval - minTimeInterval) + minTimeInterval;
                transDateTimes[i] = DateTimeUtils.addTime(transDateTimes[i - 1], interval);
            }
        }
        return transDateTimes;
    }

    private static boolean isBeforeNineOclock(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String str = df.format(date) + " 09:00";
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd hh").parse(str);
            return date.getTime() < date1.getTime();
        } catch (ParseException e) {
            return false;
        }
    }

    private static double[] calcRandomAmount(double paymentAmt, double maxTransAmt, int timesOneDay) {
        double[] amts = new double[timesOneDay];
        if (paymentAmt > 1000) {
            double average = Math.ceil(paymentAmt / timesOneDay);
            double left = paymentAmt;
            for (int i = 0; i < timesOneDay - 1; i++) {
                amts[i] = average;
                left -= average;
            }
            amts[timesOneDay - 1] = left;

            double factor = Math.ceil((maxTransAmt - average) / (timesOneDay - 1));
            for (int i = 0; i < timesOneDay; i++) {
                amts[i] -= factor;
            }
            double leftTotal = factor * timesOneDay;
            left = leftTotal;
            double div = 0;
            double ratio = 0;
            for (int i = 0; i < timesOneDay - 1; i++) {
                ratio = Math.random() / timesOneDay;
                div = Math.ceil(leftTotal * ratio);
                amts[i] += div;
                left -= div;
            }
            amts[timesOneDay - 1] += left;
        } else {
            double left = paymentAmt;
            for (int i = 0; i < timesOneDay; i++) {
                amts[i] = PayChannelConstants.MIN_TRANS_AMT;
                left -= PayChannelConstants.MIN_TRANS_AMT;
            }
            for (int i = 0; i < timesOneDay - 1; i++) {
                double d = Math.ceil(left * Math.random() / timesOneDay);
                amts[i] += d;
                left -= d;
            }
            amts[amts.length - 1] += left;
        }
        return amts;
    }

    @RequestMapping("/history")
    public InterfaceResponseInfo history(String cardNo) {
        List<Map<String, Object>> list = payCardService.queryHistoryByCardNo(cardNo);

        if (list == null || list.isEmpty()) {
            return new InterfaceResponseInfo(list);
        }
        List<Map<String, Object>> resList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            Map m = new HashMap();
            m.put("month", map.get("mth"));
            String[] statuses = map.get("statuses").toString().split(",");
            String[] ids = map.get("ids").toString().split(",");
            String[] startDates = map.get("startDates").toString().split(",");
            String[] endDates = map.get("endDates").toString().split(",");
            String[] terms = map.get("terms").toString().split(",");
            String[] paymentAmts = map.get("paymentAmts").toString().split(",");
            String[] finTerms = map.get("finTerms").toString().split(",");
            String[] balanceAmts = map.get("balanceAmts").toString().split(",");
            List<Map<String, String>> lt = new ArrayList<>();
            for (int i = 0; i < statuses.length; i++) {
                Map<String, String> im = new HashMap();
                im.put("status", statuses[i]);
                im.put("id", ids[i]);
                im.put("startDate", startDates[i]);
                im.put("endDate", endDates[i]);
                im.put("term", terms[i]);
                im.put("paymentAmt", paymentAmts[i]);
                im.put("finTerm", finTerms[i]);
                im.put("balanceAmt", balanceAmts[i]);
                lt.add(im);
            }
            m.put("data", lt);
            resList.add(m);

        }
        return new InterfaceResponseInfo(resList);
    }

    public InterfaceResponseInfo syncStatus() {
        int num = payCardService.syncStatus();
        Map<String, Integer> map = new HashMap<>();
        map.put("success", num);
        return new InterfaceResponseInfo(map);
    }

    private void processPaymentPlan(double[] onceAmount, Date[] transTime, PaymentApply apply, List<PaymentPlanPO> paymentListList) {
        for (int i = 0; i < onceAmount.length; i++) {
            PaymentPlanPO paymentPlan = new PaymentPlanPO();
            paymentPlan.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            paymentPlan.setApplyId(apply.getId());
            paymentPlan.setCardNo(apply.getCardCode());
            paymentPlan.setCreatedTime(new Timestamp(new Date().getTime()));
            paymentPlan.setCustId(apply.getCustId());
            paymentPlan.setPlanStatus("CONFIRMED");
            paymentPlan.setPaymentAmt(onceAmount[i]);
            paymentPlan.setPaymentTime(transTime[i]);
            paymentPlan.setCusumerOrderId(UUIDUtils.getUUIDNum(24));
            paymentPlan.setTransNo(UUIDUtils.getUUIDNum(20));
            paymentListList.add(paymentPlan);
        }
    }

    private WithdrawPlanPO initWithdrawApply(double paymentAmt, PaymentApply apply, Date date) {
        Random random = new Random();
        WithdrawPlanPO withdrawPlan = new WithdrawPlanPO();
        withdrawPlan.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        withdrawPlan.setApplyId(apply.getId());
        withdrawPlan.setCustId(apply.getCustId());
        withdrawPlan.setCreatedTime(new Timestamp(new Date().getTime()));
        withdrawPlan.setWithdrawAmt(paymentAmt + 1);
        withdrawPlan.setWithdrawStatus("PLAN");
        withdrawPlan.setPaymentDate(DateTimeUtils.formatDate(date, "yyyy-MM-dd"));
        withdrawPlan.setWithdrawDate(DateUtils.addMinutes(date, 40 + random.nextInt(80)));
        return withdrawPlan;
    }

    private ApplyFee initApplyFee(PaymentApply paymenApply) {
        int terms = paymenApply.getPaymentDate().split(",").length;
        double applyAmt = paymenApply.getPaymentAmt();
        Customer customer = customerService.findByCustId(paymenApply.getCustId());
        String level = customer.getCustLevelSample();
        if("NORMAL".equals(level) && "1".equals(customer.getFreeMember())){
            level = "MEMBER";
        }
        FeeRatePO feeRatePO = payCardService.transRate(level, "C");
        double total = Math.ceil(((applyAmt + terms) * 100) / (1 - feeRatePO.getCfeeRate()));
        int _total = (int) total;
        if (_total % 10 < 5) {
            _total = (_total / 10) * 10 + 5;
        } else {
            _total = (_total / 10) *10+10;
        }
        System.out.println(_total);
        int totalFee = (int) (Math.ceil(_total * feeRatePO.getCfeeRate())) + terms * 100;
        int thirdFee = (int) (Math.ceil(_total * feeRatePO.getOutRate())) + terms * 100;
        int incomeAmt = totalFee - thirdFee;
        return new ApplyFee(UUID.randomUUID().toString().replaceAll("-", ""), paymenApply.getId(), incomeAmt,  _total, thirdFee, new Timestamp(new Date().getTime()), "0");
    }
}
