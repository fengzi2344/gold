package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.TxPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface TxPaymentDao extends JpaRepository<TxPayment, String>, JpaSpecificationExecutor<TxPayment> {
    @Query(nativeQuery = true,value = "SELECT\n" +
            "\tttp.consume_order_id consumeOrderId,\n" +
            "\ttvm.PLAT_MERCHANT_CODE AS platMerchantCode,\n" +
            "\tttp.pay_amount payAmount,\n" +
            "\ttbc.ACCOUNT_CODE cardNo,\n" +
            "\ttbc.open_card_id openCardId\n" +
            "FROM\n" +
            "\tt_tx_payment ttp,\n" +
            "\tt_virtual_merchant tvm,\n" +
            "\tt_bank_card tbc\n" +
            "WHERE\n" +
            "\tttp.cust_id = tvm.CUST_ID\n" +
            "AND ttp.card_id = tbc.id\n" +
            "AND ttp.consume_order_id = ?")
    Map<String,Object> findParamMap(String consumeOrderId);

    @Modifying
    @Query(nativeQuery = true,value = "update t_tx_payment set order_status = ?,msg = ? where consume_order_id = ?")
    void updateStatus(String orderStatus,  String msg, String consumeOrderId);

    @Modifying
    @Query(nativeQuery = true, value = "update t_tx_payment set tb_msg = ? where consume_order_id = ?")
    void updatePayTbMsg(String tbMsg, String consumeOrderId);
}
