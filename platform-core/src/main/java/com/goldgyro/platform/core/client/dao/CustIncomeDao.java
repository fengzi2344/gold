package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.CustIncomePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface CustIncomeDao extends JpaRepository<CustIncomePO,String>,JpaSpecificationExecutor<CustIncomePO> {
    @Modifying
    @Query(nativeQuery=true, value = "update T_CUST_INCOME ti set ti.TOTAL_INCOME_AMT=? where ti.ID=?")
    void updateIncomeAmt(double incomeAmt, String id);

    CustIncomePO findByCustId(String custId);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "\ttmp.tradeTime AS tradeTime,\n" +
            "\ttmp.money AS money,\n" +
            "\ttmp.note AS note,\n" +
            "\ttmp.sts AS sts\n" +
            "FROM\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tcreated_time tradeTime,\n" +
            "\t\t\tincome_amt money,\n" +
            "\t\t\tIFNULL(description, '暂无') note,\n" +
            "\t\t\t'' sts\n" +
            "\t\tFROM\n" +
            "\t\t\tt_cust_income_detail\n" +
            "\t\tWHERE\n" +
            "\t\t\tcust_id = :custId\n" +
            "\t\tUNION\n" +
            "\t\t\tSELECT\n" +
            "\t\t\t\tapply_time tradeTime,\n" +
            "\t\t\t\t0 - amount money,\n" +
            "\t\t\t\t'提现' note,\n" +
            "\t\t\t\ttcd. STATUS sts\n" +
            "\t\t\tFROM\n" +
            "\t\t\t\tt_income_detail tcd\n" +
            "\t\t\tWHERE\n" +
            "\t\t\t\tEXISTS (\n" +
            "\t\t\t\t\tSELECT\n" +
            "\t\t\t\t\t\t1\n" +
            "\t\t\t\t\tFROM\n" +
            "\t\t\t\t\t\tt_cust_income tci\n" +
            "\t\t\t\t\tWHERE\n" +
            "\t\t\t\t\t\ttci.cust_id = :custId\n" +
            "\t\t\t\t\tAND tcd.cust_income_id = tci.id\n" +
            "\t\t\t\t)\n" +
            "\t) tmp\n" +
            "ORDER BY\n" +
            "\ttradeTime DESC")
    List<Object> findTradeDetail(@Param("custId") String custId);
}
