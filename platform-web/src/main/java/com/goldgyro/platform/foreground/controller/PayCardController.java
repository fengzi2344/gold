package com.goldgyro.platform.foreground.controller;

import com.alibaba.fastjson.JSON;
import com.goldgyro.platform.base.utils.DateTimeUtils;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.domain.*;
import com.goldgyro.platform.core.client.entity.*;
import com.goldgyro.platform.core.client.service.*;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.foreground.annotation.GoldAnnotation;
import com.goldgyro.platform.foreground.batchs.AES;
import com.goldgyro.platform.foreground.batchs.Base64;
import com.goldgyro.platform.foreground.batchs.BaseCtl;
import com.goldgyro.platform.foreground.constants.CardConstant;
import com.goldgyro.platform.foreground.constants.MessageConstant;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;
import com.goldgyro.platform.foreground.utils.BankCardValidator;
import com.goldgyro.platform.foreground.utils.JPushClientUtil;
import com.goldgyro.platform.foreground.utils.RequestUtil;
import com.goldgyro.platform.foreground.wechat.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 处理还卡相关业务
 *
 * @author wg2993
 * @2018/07/14
 */
@RestController
@RequestMapping(value = "/payment")
public class PayCardController {
    Logger logger = Logger.getLogger(PayCardController.class);

    public static final String ASYNC_RETURN_CODE = "000000";

    @Autowired
    private PayCardService payCardService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private BankCardService bCardService;

    @Autowired
    private InterfaceLogService interfaceLogService;

    @Autowired
    private WithdrawPlanService withdrawPlanService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private PaymentPlanService paymentPlanService;

    @Autowired
    private Environment env;
    /**
     * 查询卡的正在执行的计划的还款进度
     *
     * @return
     */
    @RequestMapping(value = "/paymentSchedule", method = RequestMethod.GET)
    @ResponseBody
    public InterfaceResponseInfo paymentSchedule(@RequestParam(value = "custId", required = false) String custId) {
        //参数验证
        if (StringUtils.isEmpty(custId)) {
            logger.error("【还款进度查询】客户编号不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "客户编号不能为空！", null);
        }
        List<Map<String, Object>> scheduleList = bCardService.findScheduleList(custId);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : scheduleList) {
            Object cardNo = map.get("cardNo");
            if (cardNo == null) {
                map.put("icon", "");
            } else {
                String icon = BaseCtl.queryBankCode(cardNo.toString());
                if (!StringUtils.isEmpty(icon)) {
                    map.put("icon", CardConstant.ICON_QUERY_URL + icon);
                }
            }
            list.add(map);
        }
        return new InterfaceResponseInfo(MessageConstant.OK, "", list);
    }

    /**
     * 预留金计算
     *
     * @param custId
     * @param paymentAmt
     * @param paymentDates
     * @return
     */
    @RequestMapping(value = "/calcReserveMoney", method = RequestMethod.POST)
    public InterfaceResponseInfo calcReserveMoney(@RequestParam(value = "custId", required = false) String custId,
                                                  @RequestParam(value = "paymentAmt", required = false) Double paymentAmt,
                                                  @RequestParam(value = "paymentDates", required = false) String paymentDates) {
        //参数验证
        if (StringUtils.isEmpty(custId)) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "客户编号不能为空", null);
        }
        if (null == paymentAmt || paymentAmt <= PayChannelConstants.MIN_TRANS_AMT) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "还款金额必须大于" + PayChannelConstants.MIN_TRANS_AMT + "元", null);
        }
        if (StringUtils.isEmpty(paymentDates)) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "还款日期计算不能为空！", null);
        }

        //每天应还本金额
        double princAmt = Math.ceil(paymentAmt / paymentDates.split(",").length);

        //获取客户信息
        Customer cust = customerService.findByCustId(custId);

        //每次还款额
        FeeRatePO feeRatePO = payCardService.transRate(cust.getCustLevelSample(), "C");
        double rate = feeRatePO.getCfeeRate();

        Map<String, Double> results = new HashMap<String, Double>();
        int times = paymentDates.split(",").length;
        //总服务费
        double serviceFee = (paymentAmt + times) / (1 - rate) - paymentAmt;
        //一次的手续费和服务费
        double lowerestMoney = Math.ceil(serviceFee) + princAmt;
        if(lowerestMoney>3000){
            lowerestMoney = 3000;
        }
        double fee = Math.ceil(serviceFee*100)/100;
        results.put("totalReserveMoney", lowerestMoney);
        results.put("rate", rate);
        results.put("fee", fee);
        results.put("oncePay",Math.ceil(paymentAmt/times));

        return new InterfaceResponseInfo(MessageConstant.OK, "应预留：" + lowerestMoney, results);
    }


    /**
     * 还款关支付：还款申请关生成还款计划
     *
     * @param paymenApply
     * @return
     */
    @RequestMapping(value = "/paymentApply", method = RequestMethod.POST)
    @ResponseBody
    public InterfaceResponseInfo paymentApply(@RequestBody(required = false) PaymentApply paymenApply) {
        //验证
        if (null == paymenApply) {
            logger.error("【还款申请】还款申请信息为空，必须填写！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还卡】还款申请信息必须填写！", null);
        }
        if (StringUtils.isEmpty(paymenApply.getCustId())) {
            logger.error("【还款申请】客户信息为空，必须传入！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还卡】客户信息为空！", null);
        }
        if (StringUtils.isEmpty(paymenApply.getCardCode())) {
            logger.error("【还款申请】客户银行卡号为空，必须传入！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还卡】客户银行卡号为空！", null);
        }
        if (0 == paymenApply.getPaymentAmt()) {
            logger.error("【还款申请】还款金额不能小于等0！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还卡】还款金额不能小于等0！", null);
        }
        if (StringUtils.isEmpty(paymenApply.getPaymentDate())) {
            logger.error("【还款申请】还款日期不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还卡】还款日期不能为空！", null);
        }

        //验证卡是否已开卡
        BankCard bCard = bCardService.findBankCardByCustIdAndAccountCode(paymenApply.getCustId(), paymenApply.getCardCode());
        if (!"OPEN_SUCCESS".equalsIgnoreCase(bCard.getOpenStatus())) {
            logger.error("【还款申请】卡还未开通！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还款申请】卡还未开通！", null);
        }

        //时间验证
        Date maxTime = DateTimeUtils.formatDateTime(DateTimeUtils.formatDateTime(new Date(), "yyyy-MM-dd") + " 12:00", "yyyy-MM-dd HH:mm");

        if (paymenApply.getPaymentDate().indexOf(DateTimeUtils.formatDateTime(new Date(), "yyyy-MM-dd")) >= 0
                && DateTimeUtils.calcTimeSub(maxTime, new Date()) > 0) {
            logger.error("【还款申请】当天还款，预留时间不足！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还款申请】当天还款，预留时间不足！", null);
        }


        //验证是否存在正在执行的还款计划
        List<Map<String, Object>> validPaymentPlanList = payCardService.findPaymentPlanListByStatus(paymenApply.getCustId(), paymenApply.getCardCode(), "CONFIRMED");

        if (null != validPaymentPlanList && validPaymentPlanList.size() > 0) {
            logger.error("【还款申请】当前存在有效的还款计划，不能添加新的还款计划！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还款申请】当前存在有效的还款计划，不能添加新的还款计划！", null);
        }

        //持久化
        try {
            //获取客户信息
            Customer cust = customerService.findByCustId(paymenApply.getCustId());

            FeeRatePO feeRatePO = payCardService.transRate(cust.getCustLevelSample(), "C");

            //还款计划
            List<PaymentPlan> paymentPlanList = new ArrayList<PaymentPlan>();
            //提现计划
            List<WithdrawPlan> withdrawPlanList = new ArrayList<WithdrawPlan>();
            //计提
            List<Income> incomeList = new ArrayList<Income>();

            //还款日期
            String[] paymentDates = paymenApply.getPaymentDate().split(",");
            //每天还款金额
            double[] dayPaymentAmts = payCardService.calcAvgAmt(paymenApply.getPaymentAmt(), paymentDates.length, null);
            //还款计划
            int i = 0;
            //每日还款：生成还款计划
            BigDecimal left = new BigDecimal("0.0");
            for (double dayPaymentAmt : dayPaymentAmts) {

                double[] oncePaymentAmts = payCardService.calcRandomAmt(dayPaymentAmt, PayChannelConstants.MIN_TRANS_AMT, PayChannelConstants.MAX_TRANS_AMT, PayChannelConstants.DAILY_MAX_TRANS_NUM);

                //每次还款时间
                Date[] transTimes = payCardService.transTime(i == 0, oncePaymentAmts, paymentDates[i], PayChannelConstants.TRANS_START_TIME,
                        PayChannelConstants.TRANS_END_TIME, PayChannelConstants.MIN_TIME_INTERVAL, PayChannelConstants.MAX_TIME_INTERVAL);

                payCardService.generPaymentPlan(i == dayPaymentAmts.length - 1, paymentPlanList, withdrawPlanList, incomeList, oncePaymentAmts, transTimes, paymenApply, (i == 0 ? true : false), feeRatePO, PayChannelConstants.SERVICE_CHARGE, left);

                i++;
            }
            double lastWithdrawAmount = calcLastWithdraw(paymentPlanList, withdrawPlanList, feeRatePO);
            withdrawPlanList.get(withdrawPlanList.size() - 1).setWithdrawAmt(lastWithdrawAmount);
            payCardService.savePaymentApplyAndPlan(paymenApply, paymentPlanList, withdrawPlanList, incomeList);
        } catch (Exception ex) {
            logger.error("【还款申请】保存还款申请信息时出报错！", ex);
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还款申请】保存还款申请信息时出报错！", null);
        }

        logger.info("【还款申请】还款申请信息保存成功！");
        return new InterfaceResponseInfo(MessageConstant.OK, "【还款申请】还款申请信息保存成功！", null);
    }

    private double calcLastWithdraw(List<PaymentPlan> paymentPlanList, List<WithdrawPlan> withdrawPlanList, FeeRatePO feeRatePO) {
        double total = 0;
        for (int i = 0; i < paymentPlanList.size(); i++) {
            total += paymentPlanList.get(i).getPaymentAmt();
        }
        double money = 0;
        for (int i = 0; i < withdrawPlanList.size() - 1; i++) {
            money += withdrawPlanList.get(i).getWithdrawAmt();
        }
        //总费用
        double totalFee = total * feeRatePO.getCfeeRate();
        return Math.floor((total - money - totalFee) * 100) / 100.0;
    }

    /**
     * 获取还款计划列表
     *
     * @param custId
     * @return
     */
    @RequestMapping(value = "/paymentPlanList", method = RequestMethod.GET)
    @ResponseBody
    public InterfaceResponseInfo paymentPlanLis(@RequestParam(value = "custId", required = false) String custId, @RequestParam(value = "cardNo", required = false) String cardNo) {
        if (null == custId) {
            logger.info("【还卡-计划】custId参数不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "【还卡】客户CUSTID不能为空！", null);
        }
        if (null == cardNo) {
            logger.info("【还卡-计划】银行卡号不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "客户银行卡号为空！", null);
        }

        return new InterfaceResponseInfo(MessageConstant.OK, "还款计划查询成功！", payCardService.findPaymentPlanList(custId, cardNo));
    }

    /**
     * 获取还款计划列表
     *
     * @param custId
     * @param cardNo
     * @return
     */
    @RequestMapping(value = "/paymentPlanListByDate", method = RequestMethod.GET)
    public InterfaceResponseInfo paymentPlanListGroupDate(@RequestParam(value = "custId", required = false) String custId, @RequestParam(value = "cardNo", required = false) String cardNo) {
        if (null == custId) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "客户CUSTID不能为空！", null);
        }
        if (null == cardNo) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "客户银行卡号不能为空！", null);
        }
        Map<String, Object> paymentApplyMap = payCardService.findPaymentApply(custId, cardNo);
        Map<String, Object> finAmtMap = getStringObjectMap(null, custId, cardNo, paymentApplyMap);

        return new InterfaceResponseInfo(MessageConstant.OK, "还款计划信息查询成功！", finAmtMap);
    }

    private Map<String, Object> getStringObjectMap(String applyId, String custId, String cardNo, Map<String, Object> paymentApplyMap) {
        long startTime = System.currentTimeMillis();
        List<Object> resultsList = new ArrayList<Object>();
        Map<String, Object> finAmtMap = payCardService.findFinAmt(paymentApplyMap.get("ID").toString());
        if (finAmtMap == null || finAmtMap.get("finAmt") == null) {
            finAmtMap = new HashMap<String, Object>();
            finAmtMap.put("finAmt", "0.00");
        }
        if (!StringUtils.isEmpty(applyId)) {
            PaymentApplyPO paymentApply = payCardService.findById(applyId);
            List<Map<String, Object>> wyrList = payCardService.findWyrList(applyId);
            for (Map<String, Object> wyrMap : wyrList) {
                Map<String, Object> inerMap = new HashMap<>();
                List<Map<String, Object>> planList = new ArrayList<>();
                String[] ids = wyrMap.get("ids").toString().split(",");
                String[] cardNos = wyrMap.get("cardNos").toString().split(",");
                String[] paymentAmts = wyrMap.get("paymentAmts").toString().split(",");
                String[] paymentTimes = wyrMap.get("paymentTimes").toString().split(",");
                String[] createdTimes = wyrMap.get("createdTimes").toString().split(",");
                String[] paymentStatuses = wyrMap.get("paymentStatuses").toString().split(",");
                String[] hyNames = wyrMap.get("hyNames").toString().split(",");
                String[] transNos = wyrMap.get("transNos").toString().split(",");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i = 0; i < ids.length; i++) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ID", ids[i]);
                    map.put("cardNo", cardNos[i]);
                    map.put("paymentAmt", paymentAmts[i]);
                    try {
                        map.put("paymentTime", dateFormat.parse(paymentTimes[i]));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        map.put("createdTime", dateFormat.parse(createdTimes[i]));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    map.put("paymentStatus", paymentStatuses[i]);
                    map.put("hyName", hyNames[i]);
                    map.put("applyId", paymentApply.getId());
                    map.put("custId", paymentApply.getCustId());
                    map.put("tranNo", transNos[i]);
                    map.put("transFee", 0);
                    planList.add(map);
                }
                inerMap.put("planList", planList);
                inerMap.put("planMoney", wyrMap.get("planMoney"));
                inerMap.put("withdrawTime", DateTimeUtils.formatDateTime((Date) wyrMap.get("withdrawTime"), "HH:mm"));
                resultsList.add(inerMap);
            }
        } else {
            int i = 0;
            for (String strPaymentDate : ((String) paymentApplyMap.get("paymentDate")).split(",")) {
                Map<String, Object> onceDayMap = new HashMap<String, Object>();
                List<Map<String, Object>> paymentPlanList = payCardService.findPaymentPlanListByDate(applyId, custId, cardNo, strPaymentDate);
                onceDayMap.put("planList", paymentPlanList);
                Map<String, Object> withdrawPlanMap = withdrawPlanService.findWithdrawPlan((String) paymentApplyMap.get("ID"), strPaymentDate);
                onceDayMap.put("withdrawTime", DateTimeUtils.formatDateTime((Date) withdrawPlanMap.get("withdrawDate"), "HH:mm"));
                onceDayMap.put("planMoney", Double.valueOf(withdrawPlanMap.get("withdrawAmt").toString()) - 1);
                resultsList.add(onceDayMap);
                i++;
            }
        }
        finAmtMap.put("dataList", resultsList);
        finAmtMap.put("totalFee", paymentApplyMap.get("totalFee"));
        long interval = System.currentTimeMillis() - startTime;
        finAmtMap.put("interval", interval);
        return finAmtMap;
    }

    @RequestMapping("/applyDetail")
    public InterfaceResponseInfo applyDetail(String applyId) {
        Map<String, Object> paymentApplyMap = payCardService.findPaymentObject(applyId);
        Map<String, Object> finAmtMap = getStringObjectMap(applyId, null, null, paymentApplyMap);

        return new InterfaceResponseInfo(MessageConstant.OK, "查询成功！", finAmtMap);
    }

    /**
     * 查询还款计划详细信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/paymentPlanDetail", method = RequestMethod.GET)
    @ResponseBody
    public InterfaceResponseInfo paymentPlan(@RequestParam(value = "id", required = false) String id) {
        if (null == id) {
            logger.info("【还卡-计划】交易流水号不能为空！");
            return new InterfaceResponseInfo(MessageConstant.OK, "交易流水号不能为空！", null);
        }

        return new InterfaceResponseInfo(MessageConstant.OK, "还款计划详细信息查询成功！", payCardService.findPaymentPlanDetail(id));
    }

    @RequestMapping("/updateMcc")
    public InterfaceResponseInfo updateMccById(@RequestParam(value = "id", required = true) String id, @RequestParam(value = "mccCode", required = true) String mccCode) {
        if (mccCode.trim().length() == 5) {
            mccCode = mccCode.replaceFirst("0", "");
        }
        paymentPlanService.updateMccById(mccCode, id);
        return new InterfaceResponseInfo("1", "成功", null);
    }

    /**
     * 取消还款计划
     *
     * @param custId
     * @return
     */
    @RequestMapping(value = "/cancelPaymentPlan", method = RequestMethod.POST)
    @ResponseBody
    public InterfaceResponseInfo cancelPaymentPlan(@RequestParam(value = "custId", required = false) String custId,
                                                   @RequestParam(value = "applyId", required = false) String applyId) {
        if (null == custId) {
            logger.info("【还卡-计划】custId参数不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "custId参数不能为空！", null);
        }
        if (null == applyId) {
            logger.info("【还卡-计划】还款申请号参数不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "还款申请号不能为空！", null);
        }

        if (null == customerService.findByCustId(custId)) {
            logger.info("【还卡-计划】没有找到对应的还款计划！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "custId参数不能为空！", null);
        }
        if (payCardService.haveBegun(applyId)) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "计划执行中，不能取消！", null);
        }
        try {
            payCardService.cancelConfirmedPaymentPlan(applyId, "CANCELED");
        } catch (Exception ex) {
            logger.error("【还卡-计划】撤销还款计划时过程中出现异常！", ex);
            return new InterfaceResponseInfo(MessageConstant.FAIL, "撤销还款计划时过程中出现异常，请与客服联系！", null);
        }

        logger.info("【还卡-计划】撤销还款计划成功！");
        return new InterfaceResponseInfo(MessageConstant.OK, "撤销还款计划成功！", null);
    }

    /**
     * 添卡、开卡
     *
     * @param bCard
     * @param request
     * @return
     */
    @RequestMapping(value = "/addCard", method = RequestMethod.POST)
    @ResponseBody
    public InterfaceResponseInfo addCard(@RequestBody(required = false) BankCard bCard, HttpServletRequest request, HttpServletResponse response) {
        InterfaceResponseInfo msg = validatorCard(bCard);
        if (!MessageConstant.OK.equals(msg.getCode())) {
            logger.error(msg.getCode());
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(msg.getCode()), null);
        }

        try {
            if (null != bCardService.findBankCardByCustIdAndAccountCode(bCard.getCustId(), bCard.getAccountCode())) {
                logger.error(MessageConstant.BCARD_ERROR_CODE_001);
                return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.BCARD_ERROR_CODE_001), null);
            }

            Customer cust = customerService.findByCustId(bCard.getCustId());
            if (null == cust) {
                logger.error("此客户不存在！");
                return new InterfaceResponseInfo(MessageConstant.FAIL, "此客户不存在！", null);
            }

            //应做唯一性验证：若已存在收入或支出的默认卡，则不应设置
            if (StringUtils.isEmpty(bCard.getDefaultCard())) {
                bCard.setDefaultCard(CardConstant.YES);
            }
            bCard.setId(UUIDUtils.getUUIDNum());
            bCard.setCustId(cust.getCustId());
            bCard.setAccountName(cust.getCustName());
            bCard.setValid("Y");
            bCard.setOpenStatus("INIT");
            bCard.setBindStatus("INIT");
            if ("C".equalsIgnoreCase(bCard.getBankCardType())) {
                bCard.setIoType(CardConstant.IN_OUT_CARD);
                bCard.setOpenningBankProvince("四川");
                bCard.setOpenningBankCity("成都");
                bCard.setOpeningSubBankName("信用卡中心");
            } else {
                bCard.setIoType(CardConstant.IN_CARD);
                bCard.setOpenningBankProvince("四川");
                bCard.setOpenningBankCity("成都");
                bCard.setOpeningSubBankName("春熙路支行");
            }
            String appcode = env.getProperty("aliyun.appcode");
            if(appcode != null && !StringUtils.isEmpty(appcode)){
                Map<String,String> map = BankCardValidator.check4Elements(bCard,appcode,cust.getIdCardNo());
                if(!"01".equals(map.get("status"))){
                    return new InterfaceResponseInfo(MessageConstant.FAIL, map.get("msg"), null);
                }
                if("准贷记卡".equals(map.get("cardType"))){
                    return new InterfaceResponseInfo(MessageConstant.FAIL, "本应用暂不支持准贷记卡", null);
                }
            }
            bCardService.save(bCard, false);
        } catch (Exception ex) {
            logger.info(MessageConstant.BCARD_ERROR_CODE_003);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.BCARD_ERROR_CODE_003), null);
        }
        return new InterfaceResponseInfo(MessageConstant.OK, MessageConstant.getMessage(MessageConstant.OK), null);
    }

    /**
     * 开卡
     *
     * @param custId
     * @param cardId
     * @param request
     * @return
     */
   /* @RequestMapping(value = "/openCard", method = RequestMethod.GET)
    @ResponseBody
    public InterfaceResponseInfo openCard(@RequestParam(value = "custId", required = false) String custId,
                                          @RequestParam(value = "cardId", required = false) String cardId, HttpServletRequest request, HttpServletResponse response) {
        logger.info("开卡开始============================");

        BankCard bCard = bCardService.findBankCardById(cardId);

        request.setAttribute("custId", custId);
        request.setAttribute("accountNo", bCard.getAccountCode());

        try {
            request.getRequestDispatcher("/open/card").forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("开卡========转页面提交====================");
        return null;
    }*/

    /**
     * 查询商户注册状态
     *
     * @param metchantCode
     * @param request
     * @return
     */
   /* @RequestMapping(value = "/merchantStatus", method = RequestMethod.POST)
    @ResponseBody
    public InterfaceResponseInfo queryMerchantRegistStuts(@RequestParam(value = "metchantCode", required = false) String metchantCode, HttpServletRequest request) {
        if (StringUtils.isEmpty("metchantCode")) {
            logger.error("商户号为空。查询商户注册状态必须输入商户号！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage("商户号为空。查询商户注册状态必须输入商户号！"), null);
        }
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("metchantCode", metchantCode);

        String results = "";
        try {
            results = new CardProcess().queryQJInterface(paramsMap, UUIDUtils.getUUIDNum(), "710007", null, interfaceLogService);
        } catch (Exception ex) {
            logger.error("与快捷交互时出现异常！", ex);
        }

        return new InterfaceResponseInfo(MessageConstant.OK, "你的商户注册状态：" + results, null);
    }*/

    /**
     * 验证器
     *
     * @return boolean
     */
    private InterfaceResponseInfo validatorCard(BankCard card) {
        if (null == card) {
            logger.error("【绑卡开卡】没接收到银行卡信息！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "没接收到银行卡信息！", null);
        }
        if (StringUtils.isEmpty(card.getAccountCode())) {
            logger.error("【绑卡开卡】银行卡编码不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "银行卡编码不能为空！", null);
        }
        if (StringUtils.isEmpty(card.getMobileNo())) {
            logger.error("【绑卡开卡】银行卡预留电话不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "银行卡预留电话不能为空！", null);
        }
        if (StringUtils.isEmpty(card.getBankName())) {
            logger.error("【绑卡开卡】银行名称不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "银行卡名称不能为空！", null);
        }
        if (StringUtils.isEmpty(card.getBankCardType())) {
            logger.error("【绑卡开卡】银行卡类型不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "银行卡类型不能为空！", null);
        }

        return new InterfaceResponseInfo(MessageConstant.OK, "银行卡信息验证通过！", null);
    }

    /**
     * 用户注册回调方法
     *
     * @param signature
     * @param encryptData
     * @return
     */
   /* @RequestMapping("/ayncMerchantRegist")
    public String ayncMerchantRegist(String signature, String encryptData) {
        LogUtils.writeStringToFile("==========用户注册回调方法 参数============");
        LogUtils.writeStringToFile(encryptData);
        String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0, 16));
        Map<String, Object> map = ((Map<String, Object>) JSON.parse(msg));
        String status = map.get("status").toString();
        String merchantCode = map.get("merchantCode").toString();
        String platMerchantCode = map.get("platMerchantCode").toString();
        merchantService.processReistStatus(merchantCode, platMerchantCode, status);
        return ASYNC_RETURN_CODE;
    }*/

    /**
     * 用户消费回调方法
     *
     * @param encryptData
     * @return
     */
    @RequestMapping("/ayncPayStatus")
    @GoldAnnotation
    public String ayncPayStatus(String encryptData) {
        String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0, 16));
        Map<String, Object> map = ((Map<String, Object>) JSON.parse(msg));
        String orderStatus = map.get("orderStatus").toString();
        String workId = map.get("workId").toString();
        PaymentPlanPO paymentPlanPO = paymentPlanService.findByWorkId(workId);
        if (paymentPlanPO != null) {
            if("04".equals(orderStatus)){
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Map<String, Object> finalMap = RequestUtil.queryStatus(paymentPlanPO.getOrderId());
                        if (finalMap != null) {
                            String status = finalMap.get("status").toString();
                            System.out.println("status" + status);
                            if ("02".equals(status) || "01".equals(status)) {
                                payCardService.doProcessAfter(paymentPlanPO.getTransNo(), status);
                            }
                        }
                        this.cancel();
                    }
                },60000);
            }else{
                JPushClientUtil.pushMsg(paymentPlanPO.getCustId(),"新信息", "您有一笔计划消费成功，金额"+paymentPlanPO.getPaymentAmt()+"元");
                payCardService.doProcessAfter(paymentPlanPO.getTransNo(), orderStatus);
            }
        }
        return ASYNC_RETURN_CODE;
    }
    @GetMapping("/a/{transNo}/{orderStatus}")
    public String test(@PathVariable String transNo,@PathVariable String orderStatus){
        payCardService.doProcessAfter(transNo, orderStatus);
        return "success";
    }
    /**
     * 信用卡消费返现回调方法
     *
     * @param encryptData
     * @return
     */
    @RequestMapping("/withdrawBackUrl")
    @GoldAnnotation
    public String withDrawCallBak(String encryptData) {
        String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0, 16));
        Map<String, Object> map = ((Map<String, Object>) JSON.parse(msg));
        String status = map.get("status").toString();
        String workId = map.get("workId").toString();
        WithdrawPlanPO withdrawPlanPO = withdrawPlanService.findByWorkId(workId);
        JPushClientUtil.pushMsg(withdrawPlanPO.getCustId(),"新信息", "您有一笔还款已入账，金额"+withdrawPlanPO.getWithdrawAmt()+"元");
        if ("01".equals(status)) {
            status = "SUCCESS";
        } else if ("02".equals(status)) {
            status = "FAIL";
        }
        if ("SUCCESS".equals(status) || "FAIL".equals(status)) {
            if (withdrawPlanPO != null) {
                withdrawPlanPO.setWithdrawStatus(status);
                payCardService.saveWithdraw(withdrawPlanPO);
            }
        }
        return ASYNC_RETURN_CODE;
    }
    @GetMapping("/bzj/{levelCode}")
    public InterfaceResponseInfo slt(@PathVariable String levelCode){
        List<CustomerPO> custList = this.customerService.findNotNormal(levelCode);
        return new InterfaceResponseInfo(custList);
    }
}
