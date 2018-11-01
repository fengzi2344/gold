package com.goldgyro.platform.foreground.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.entity.TxPayment;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.client.service.MerchantService;
import com.goldgyro.platform.core.client.service.TixianService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.foreground.batchs.AES;
import com.goldgyro.platform.foreground.batchs.Base64;
import com.goldgyro.platform.foreground.batchs.BaseCtl;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;
import com.goldgyro.platform.foreground.utils.Api;
import com.goldgyro.platform.foreground.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tx")
public class TxWithdrawController {
    //支付回调url
    private static final String CALL_BACK_URL = "http://www.tuoluo718.com/tx/payCallback";
    //提现回调url
    private static final String BACK_URL = "http://www.tuoluo718.com/tx/withdrawCallback";
    @Autowired
    private TixianService tixianService;

    @PostMapping("/withdraw")
    public InterfaceResponseInfo doWithdraw(String custId, String inCardId, String outCardId, Double amount) {

        //数据校验主要由客户端控制
        /**
         * 验证用户和银行卡信息是匹配
         */
        if(!tixianService.validate(custId,inCardId,outCardId)){
            return new InterfaceResponseInfo("0","信息错误",null);
        }

        TxPayment txPayment = tixianService.doProcess(custId, inCardId, outCardId,amount);
        doPay(txPayment.getConsumeOrderId());
        return new InterfaceResponseInfo("1","请求已提交",txPayment);
    }
    /**
     * 支付方法
     * @param consumeOrderId
     */
    private void doPay(String consumeOrderId) {
        Map<String,Object> headMap = RequestUtil.preHead(Api.SMALLPAY.get());
        Map<String,Object> origMap = tixianService.findParamMap(consumeOrderId);
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("consumeOrderId",origMap.get("consumeOrderId"));
        paramMap.put("openCardId",origMap.get("openCardId"));
        paramMap.put("payAmount",origMap.get("payAmount"));
        paramMap.put("productCode","QUICKPAY_NOSMS");
        paramMap.put("remark","提现");
        paramMap.put("notifyUrl",CALL_BACK_URL);
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        Map<String,Object> resMap = RequestUtil.execute(Api.SMALLPAY.get(),JSONObject.toJSON(paramMap).toString(),null,paramMap.get("orderId").toString());
        /*try{
            String restr = BaseCtl.execute(PayChannelConstants.KEY, PayChannelConstants.URL, PayChannelConstants.PAYMENT,
                    JSONObject.toJSONString(paramMap).trim(), null, PayChannelConstants.PARTNER_NO, headMap.get("orderId").toString());
            tixianService.updatePayTbMsg(restr,consumeOrderId);
        }catch (Exception e){
            tixianService.updatePayTbMsg("支付申请异常",consumeOrderId);
        }*/
        JSONObject resJson = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        /*paramsMap.put("head", json);
        Map<String, Object> resMap = RequestUtil.execute(Api.SMALLWITHDRAW.get(), JSONObject.toJSONString(paramsMap).trim(), null, headMap.get("orderId").toString());
        if(resMap != null){
            String workId = resMap.get("workId").toString();
            String status = resMap.get("status").toString();
            WithdrawPlanPO withdrawPlanPO = withdrawPlanService.findById(plan.get("withdrawOrderId").toString());
            if(withdrawPlanPO != null){
                withdrawPlanPO.setWorkId(workId);
                if("01".equals(status)){
                    withdrawPlanPO.setWithdrawStatus("SUCCESS");
                }else if("02".equals(status)){
                    withdrawPlanPO.setWithdrawStatus("FAIL");
                }else{
                    withdrawPlanPO.setWithdrawStatus("PROCCESSING");
                }
            }
        }*/
    }

    @RequestMapping("/payCallback")
    public String payCallback(String signature, String encryptData){
        String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0, 16));
        Map<String, Object> map = ((Map<String, Object>) JSON.parse(msg));
        String consumeOrderId = map.get("consumeOrderId").toString();

        Map<String,Object> headMap = BaseCtl.initHeadParam(PayChannelConstants.PAY_STATUS_QUERY);
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("consumeOrderId",consumeOrderId);
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        String resStr = BaseCtl.execute(PayChannelConstants.KEY, PayChannelConstants.URL, PayChannelConstants.PAY_STATUS_QUERY,
                JSONObject.toJSONString(paramMap).trim(), null, PayChannelConstants.PARTNER_NO, headMap.get("orderId").toString());
        Map<String,Object> returnMap = BaseCtl.getReturnMap(resStr);
        String orderStatus = returnMap.get("orderStatus").toString();
        tixianService.updatePayment(consumeOrderId,orderStatus,resStr);
        if("01".equals(orderStatus)){
            doWithdraw(consumeOrderId);
        }
        return "000000";
    }

    /**
     * 套现实现
     * @param consumeOrderId
     */
    private void doWithdraw(String consumeOrderId) {
        Map<String,Object> headMap = BaseCtl.initHeadParam(PayChannelConstants.WITHDRAWAL);
        Map<String,Object> paramMap = tixianService.findWithDrawMap(consumeOrderId);
        paramMap.put("backURL",BACK_URL);
        paramMap.put("callBackUrl",CALL_BACK_URL);
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        try{
            String restr = BaseCtl.execute(PayChannelConstants.KEY, PayChannelConstants.URL, PayChannelConstants.WITHDRAWAL,
                    JSONObject.toJSONString(paramMap).trim(), null, PayChannelConstants.PARTNER_NO, headMap.get("orderId").toString());
            tixianService.updateWithdrawTbMsg(restr,consumeOrderId);
        }catch (Exception e){
            tixianService.updateWithdrawTbMsg("提现申请异常",consumeOrderId);
        }

    }

    /**
     * 套现异步回调方法
     * @param signature
     * @param encryptData
     * @return
     */
    @RequestMapping("/withdrawCallback")
    public String withdrawCallback(String signature, String encryptData){
        String msg = AES.decode(Base64.decode(encryptData.getBytes()), PayChannelConstants.KEY.substring(0, 16));
        Map<String, Object> map = ((Map<String, Object>) JSON.parse(msg));
        String withdrawOrderId = map.get("withDrawOrderId").toString();

        Map<String,Object> headMap = BaseCtl.initHeadParam(PayChannelConstants.WITHDRAW_STATUS_QUERY);
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("withdrawOrderId",withdrawOrderId);
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        String resStr = BaseCtl.execute(PayChannelConstants.KEY, PayChannelConstants.URL, PayChannelConstants.WITHDRAW_STATUS_QUERY,
                JSONObject.toJSONString(paramMap).trim(), null, PayChannelConstants.PARTNER_NO, headMap.get("orderId").toString());
        Map<String,Object> returnMap = BaseCtl.getReturnMap(resStr);
        String orderStatus = returnMap.get("drawStatus").toString();
        tixianService.updateWithdraw(withdrawOrderId,orderStatus,resStr);
        return "000000";
    }
}
