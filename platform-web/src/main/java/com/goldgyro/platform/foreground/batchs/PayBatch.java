package com.goldgyro.platform.foreground.batchs;

import com.alibaba.fastjson.JSONObject;
import com.goldgyro.platform.core.client.entity.InterfaceLogPO;
import com.goldgyro.platform.core.client.entity.PaymentPlanPO;
import com.goldgyro.platform.core.client.entity.WithdrawPlanPO;
import com.goldgyro.platform.core.client.service.*;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.core.comm.utils.SMSUtils;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;
import com.goldgyro.platform.foreground.utils.Api;
import com.goldgyro.platform.foreground.utils.JPushClientUtil;
import com.goldgyro.platform.foreground.utils.RequestUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Controller
@Configurable
@EnableScheduling
@ConditionalOnProperty("enableSync")
public class PayBatch {
    Logger logger = Logger.getLogger(PayBatch.class);

    @Autowired
    private PayCardService payCardService;

    @Autowired
    private PaymentPlanService paymentPlanService;

    @Autowired
    private WithdrawPlanService withdrawPlanService;

    // 是否正在处理客户还款业务
    public static boolean isPaymentProcessing = false;

    // 提现
    public static boolean isWithdrawProcessing = false;

    @Autowired
    private InterfaceLogService interfaceLogService;

    @Autowired
    private Environment env;

    @Autowired
    private RestTemplateBuilder builder;

    @Autowired
    private CustomerService customerService;

    @Scheduled(cron = "0 */5 09-22 * * * ")
    public void paymentCard() {
        if (isPaymentProcessing) {
            logger.info("客户还款任务正在执行！");
            return;
        }
        isPaymentProcessing = true;

        logger.info("=========================客户还款任务开始执行！=============================");
        // 获取需要开卡的信息
        List<Map<String, Object>> planList = payCardService.findPaymentPlanList(PayChannelConstants.PAYMENT_TIME_OFFSET);
        if (null == planList || planList.size() == 0) {
            isPaymentProcessing = false;
            logger.info("没有获取到需要待还款信息！");
            return;
        }
        try {
            for (Map<String, Object> plan : planList) {
                String transNo = plan.get("transNo").toString();

                Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("consumeOrderId", transNo);
                paramsMap.put("platMerchantCode", plan.get("platMerchantCode"));

                // 注：第三方平台的交互金额以分为单位
                paramsMap.put("payAmount", plan.get("payAmount").toString());
//				paramsMap.put("cardNo", plan.get("cardNo"));
                paramsMap.put("notifyUrl", env.getProperty("domainUrl")+PayChannelConstants.ASYN_PAYMENT_STATUS_BACK_URL);
                paramsMap.put("openCardId", plan.get("openCardId"));
                paramsMap.put("remark", "消费");
                paramsMap.put("productCode", "QUICKPAY_NOSMS");
                Object city = null;
                if ((city = plan.get("city")) != null) {
                    paramsMap.put("city", city.toString());
                }
                Object mccId = null;
                if((mccId=plan.get("mccId")) != null){
                    paramsMap.put("mccId",mccId.toString());
                }
                Map<String, Object> headMap = RequestUtil.preHead(Api.SMALLPAY.get());
                JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
                paramsMap.put("head", json);
                InterfaceLogPO interfaceLogPO = new InterfaceLogPO();
                interfaceLogPO.setBusiId(plan.get("id").toString());
                interfaceLogPO.setTableName("T_PAYMENT_PLAN");
                interfaceLogPO.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                interfaceLogPO.setCreatedTime(new Timestamp(new Date().getTime()));
                interfaceLogPO.setBusiType("消费");
                Map<String, Object> resMap = RequestUtil.executeAndSaveLog(Api.SMALLPAY.get(), JSONObject.toJSONString(paramsMap).trim(), null, headMap.get("orderId").toString(),
                        interfaceLogService, interfaceLogPO);
                if (resMap != null) {
                    Object head = resMap.get("head");
                    Map<String, Object> resHeadMap = BaseCtl.getHeadMap(head.toString());
                    String workId = "";
                    String orderId = headMap.get("orderId").toString();
                    if (resMap.get("workId") != null) {
                        workId = resMap.get("workId").toString();
                    }
                    PaymentPlanPO paymentPlanPO = paymentPlanService.findByTransNo(transNo);
                    if (paymentPlanPO != null) {
                        paymentPlanPO.setWorkId(workId);
                        paymentPlanPO.setOrderId(orderId);
                        paymentPlanService.savePlan(paymentPlanPO);
                    }
                    if (head != null) {
                        String respMsg = resHeadMap.get("respMsg").toString();
                        paymentPlanService.updatePlanStatusByTransNo("PROCESSING", transNo);
                        //由于ts侠提供的扯淡的接口 做如下操作处理

                        Timer timer = new Timer();// 实例化Timer类
                        timer.schedule(new TimerTask() {
                            public void run() {
                                System.out.println("==========延时执行============");
                                Map<String, Object> finalMap = RequestUtil.queryStatus(orderId);
                                if (finalMap != null) {
                                    String status = finalMap.get("status").toString();
                                    System.out.println("status" + status);
                                    if ("02".equals(status) || "03".equals(status)) {
                                        String statusDesc = finalMap.get("statusDesc").toString();
                                        paymentPlanService.cancelConfirmedPaymentPlan((String) plan.get("applyId"), "REPAY_FAIL");
                                        withdrawPlanService.cancelByTransNo(transNo);
                                        paymentPlanService.updateDescr(statusDesc, transNo);
                                        payCardService.updateApplyStatus("FAIL", plan.get("applyId"));
                                        SMSUtils.sendSecurityCode("15202814761", "transNo=" + transNo + "消费失败");
                                        String v = "商户"+ plan.get("custName").toString() +"消费失败，请及时处理！";
                                        try{
                                            builder.build().getForEntity(env.getProperty("managerUrl") + "/sendMsg/" + v, String.class);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        String custId = plan.get("custId").toString();
                                        String amt = new BigDecimal(plan.get("payAmount").toString()).divide(new BigDecimal("100"),2).doubleValue()+"";
                                        JPushClientUtil.pushMsg(custId,"新信息", "您有一笔计划消费失败，金额"+amt+"元");
                                    }
                                }
                                this.cancel();
                            }
                        }, 5000);

                    }
                }
            }
        } catch (Exception ex) {
            logger.info("客户还款任务执行异常！");
            ex.printStackTrace();
        } finally {
            isPaymentProcessing = false;
        }
    }

    @GetMapping("/cancelWithdraw")
    @ResponseBody
    public InterfaceResponseInfo cancelWithdraw(String transNo) {
        withdrawPlanService.cancelByTransNo(transNo);
        return new InterfaceResponseInfo("1", "SUCCESS", null);
    }

    /**
     * 提现
     */
    @Scheduled(cron = "0 */10 09-23 * * * ")
    public void withdraw() {
        if (isWithdrawProcessing) {
            logger.info("客户提现任务正在执行！");
            return;
        }
        isWithdrawProcessing = true;
        List<Map<String, Object>> planList = withdrawPlanService.findWithdrawPlanList();
        if (null == planList || planList.size() == 0) {
            isWithdrawProcessing = false;
            logger.info("【提现】没有获取到需要待提现信息！");
            return;
        }
        logger.info("=========================客户提现任务开始执行！=============================");

        try {
            for (Map<String, Object> plan : planList) {
                Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("platMerchantCode", plan.get("platMerchantCode"));
                paramsMap.put("amount", plan.get("amount"));
                paramsMap.put("openCardId", plan.get("openCardId"));
                paramsMap.put("backUrl", env.getProperty("domainUrl")+"/payment/withdrawBackUrl");
                paramsMap.put("productCode", "QUICKPAY_NOSMS");
                paramsMap.put("remark", "还款");
                Map<String, Object> headMap = RequestUtil.preHead(Api.SMALLWITHDRAW.get());
                JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
                paramsMap.put("head", json);
                InterfaceLogPO interfaceLogPO = new InterfaceLogPO();
                interfaceLogPO.setBusiId(plan.get("withdrawOrderId").toString());
                interfaceLogPO.setTableName("T_WITHDRAW_PLAN");
                interfaceLogPO.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                interfaceLogPO.setCreatedTime(new Timestamp(new Date().getTime()));
                interfaceLogPO.setBusiType("还款");
                Map<String, Object> resMap = RequestUtil.executeAndSaveLog(Api.SMALLWITHDRAW.get(), JSONObject.toJSONString(paramsMap).trim(), null, headMap.get("orderId").toString(),interfaceLogService,interfaceLogPO);
                if (resMap != null) {
                    String workId = resMap.get("workId").toString();
                    String status = resMap.get("status").toString();
                    WithdrawPlanPO withdrawPlanPO = withdrawPlanService.findById(plan.get("withdrawOrderId").toString());
                    if (withdrawPlanPO != null) {
                        withdrawPlanPO.setWorkId(workId);
                        if ("01".equals(status)) {
                            withdrawPlanPO.setWithdrawStatus("SUCCESS");
                        } else if ("02".equals(status)) {
                            withdrawPlanPO.setWithdrawStatus("FAIL");
                            SMSUtils.sendSecurityCode("15202814761", "withdrawOrderId=" + plan.get("withdrawOrderId").toString() + "还款失败");
                        } else {
                            withdrawPlanPO.setWithdrawStatus("PROCCESSING");
                        }
                        payCardService.saveWithdraw(withdrawPlanPO);
                    }
                }
            }
        } catch (Exception ex) {
            logger.info("客户提现任务执行异常！");
        } finally {
            isWithdrawProcessing = false;
        }
    }

    @RequestMapping("/ttt/{value}")
    @ResponseBody
    public String test(@PathVariable("value") String value){
        String v = "商户"+ value +"消费失败，请及时处理！";
        try {
            System.out.println(env.getProperty("managerUrl"));
            ResponseEntity<String> entity = builder.build().getForEntity(env.getProperty("managerUrl") + "/sendMsg/" + v, String.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    @ResponseBody
    @GetMapping("/query/{orderId}")
    public Map<String,Object> queryPay(@PathVariable("orderId") String orderId){
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("consumeOrderId",orderId);
        Map<String, Object> headMap = RequestUtil.preHead(Api.QUERYPAY.get());
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramsMap.put("head", json);
        Map<String, Object> resMap = RequestUtil.execute(headMap.get("txnCode").toString(), JSONObject.toJSONString(paramsMap).trim(), null, headMap.get("orderId").toString());
        return resMap;
    }

    public static void main(String[] args) {
        /*String[] str = new String[]{"201810291725011540805101070","201810301140001540870800007","201810301525011540884301351","201810291210011540786201445","201810301535001540884900023"};
        Stack<String> list= new Stack<>();
        for(String s:str){
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> entity = restTemplate.getForEntity("http://www.tuoluo718.com/query/"+s, Map.class);
            Map m = entity.getBody();
            list.push(m.get("status").toString());
        }
       while(!list.isEmpty()){
           System.out.println(list.pop());
       }*/
        HashMap<String,String> map = new HashMap<>();
       /* map.put("A1","male");
        map.put("A2","male");
        map.put("a3","male");
        map.put("a4","male");
        map.put("a5","male");
        map.put("a6","male");
        map.put("a7","male");
        map.put("a8","male");
        map.put("a9","male");
        map.put("a10","male");
        map.put("a11","male");
        map.put("a12","male");
        map.put("a13","male");
        map.put("a14","male");
        map.put("a15","male");
        map.put("a16","male");
        map.put("name","gys");
        map.put("age","15");
        map.put("sex","male");*/

    }
}