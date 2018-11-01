package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.Redpack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RedpackDao extends JpaRepository<Redpack, String>, JpaSpecificationExecutor<Redpack> {
    Redpack findRedpackByCustId(String custId);
    @Modifying
    @Query(nativeQuery = true, value = "update t_redpack set balance = ? where id = ?")
    void updateBalance(String balance, String id);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\tt_redpack r\n" +
            "WHERE\n" +
            "\tEXISTS (\n" +
            "\t\tSELECT\n" +
            "\t\t\t1\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info t\n" +
            "\t\tWHERE\n" +
            "\t\t\tt.cust_id = r.cust_id\n" +
            "\t\tAND t.cust_mobile = ?\n" +
            "\t)")
    Redpack findRedpackByMobile(String custMobile);
}
