package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.TxPayment;

import java.util.Map;

public interface TixianService {
    TxPayment doProcess(String custId, String inCardId, String outCardId, Double amount);

    Map<String,Object> findParamMap(String consumeOrderId);

    Map<String,Object> findWithDrawMap(String consumeOrderId);

    void updatePayment(String consumeOrderId, String orderStatus, String msg);

    void updateWithdraw(String withdrawOrderId, String orderStatus,String msg);

    void updatePayTbMsg(String tbMsg,String consumeOrderId);

    void updateWithdrawTbMsg(String tbMsg,String withdrawOrderId);

    boolean validate(String custId, String inCardId, String outCardId);
}
