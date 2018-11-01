package com.goldgyro.platform.foreground.controller;

import com.alibaba.fastjson.JSONObject;
import com.goldgyro.platform.core.client.entity.WithdrawPlanPO;
import com.goldgyro.platform.core.client.service.PayCardService;
import com.goldgyro.platform.core.client.service.WithdrawPlanService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.core.comm.utils.SMSUtils;
import com.goldgyro.platform.foreground.utils.Api;
import com.goldgyro.platform.foreground.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/manage")
public class ManageContoller {
    @Autowired
    private PayCardService payCardService;

    @Autowired
    private WithdrawPlanService withdrawPlanService;

    @GetMapping("/payback/{applyId}")
    public Map<String,Object> retainBalance(@PathVariable String applyId){
        Map<String,Object> map = payCardService.findWithdrawMap(applyId);
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("platMerchantCode", map.get("platMerchantCode"));
        paramsMap.put("amount", map.get("amount"));
        paramsMap.put("openCardId", map.get("openCardId"));
        paramsMap.put("backUrl", "http://www.tuoluo718.com/payment/withdrawBackUrl");
        paramsMap.put("productCode", "QUICKPAY_NOSMS");
        paramsMap.put("remark", "还款");
        Map<String, Object> headMap = RequestUtil.preHead(Api.SMALLWITHDRAW.get());
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramsMap.put("head", json);
        Map<String, Object> resMap = RequestUtil.execute(Api.SMALLWITHDRAW.get(), JSONObject.toJSONString(paramsMap).trim(), null, headMap.get("orderId").toString());
        String status = "";
        if (resMap != null) {
            String workId = resMap.get("workId").toString();
            status = resMap.get("status").toString();
            WithdrawPlanPO withdrawPlanPO = withdrawPlanService.findById(map.get("withdrawOrderId").toString());
            if (withdrawPlanPO != null) {
                withdrawPlanPO.setWorkId(workId);
                if ("01".equals(status)) {
                    withdrawPlanPO.setWithdrawStatus("SUCCESS");
                } else if ("02".equals(status)) {
                    withdrawPlanPO.setWithdrawStatus("FAIL");
                } else {
                    withdrawPlanPO.setWithdrawStatus("PROCCESSING");
                }
                payCardService.saveWithdraw(withdrawPlanPO);
            }
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("status",status);
        return resultMap;
    }
}
