package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.TxWithdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;

public interface TxWithdrawDao extends JpaRepository<TxWithdraw, String>, JpaSpecificationExecutor<TxWithdraw> {
    @Query(nativeQuery = true, value = "SELECT\n" +
            "\ttvm.PLAT_MERCHANT_CODE platMerchantCode,\n" +
            "\tttw.withdraw_order_id withdrawOrderId,\n" +
            "\tttw.amount,\n" +
            "\ttbc.ACCOUNT_CODE bankAccountNo\n" +
            "FROM\n" +
            "\tt_tx_withdraw ttw,\n" +
            "\tt_virtual_merchant tvm,\n" +
            "\tt_bank_card tbc\n" +
            "WHERE\n" +
            "\tttw.cust_id = tvm.CUST_ID\n" +
            "AND ttw.card_id = tbc.id\n" +
            "AND ttw.consume_order_id = ?")
    Map<String, Object> findParamMap(String consumeOrderId);

    @Modifying
    @Query(nativeQuery = true, value = "update t_tx_withdraw set draw_status = ?,msg = ? where withdraw_order_id = ?")
    void updateStatus(String status,String msg, String consumeOrderId);
    @Modifying
    @Query(nativeQuery = true, value = "update t_tx_withdraw set tb_msg = ? where consume_order_id = ?")
    void updateWithdrawTbMsg(String tbMsg, String consumeOrderId);
}
