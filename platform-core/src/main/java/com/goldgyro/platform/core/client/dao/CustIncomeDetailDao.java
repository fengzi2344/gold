package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.CustIncomeDetailPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface CustIncomeDetailDao extends JpaRepository<CustIncomeDetailPO,String>, JpaSpecificationExecutor<CustIncomeDetailPO> {
//    select * from t_cust_income_detail where CUST_ID = ? order by CREATED_TIME desc
    List<CustIncomeDetailPO> findAllByCustIdOrderByCreatedTimeDesc(String custId);
    @Query(nativeQuery = true,value = "SELECT\n" +
            "\tci.total_income_amt totalIncomeAmount,\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tsum(1)\n" +
            "\t\tFROM\n" +
            "\t\t\tt_cust_income_detail detail\n" +
            "\t\tWHERE\n" +
            "\t\t\tdetail.cust_id = info.cust_id\n" +
            "\t) AS incomeCount,\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tsum(1)\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info tc\n" +
            "\t\tWHERE\n" +
            "\t\t\ttc.father_mobile = info.cust_mobile\n" +
            "\t) AS custNum\n" +
            "FROM\n" +
            "\tt_cust_income ci,\n" +
            "\tt_customer_info info\n" +
            "WHERE\n" +
            "\tci.cust_id = info.CUST_ID\n" +
            "AND info.cust_id = ?")
    Map<String,Object> finIncomeInfo(String custId);

    /**
     * 交易分析页面数据 by Day
     * @param custId
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT\n" +
            "\tsum(\n" +
            "\t\tcast(\n" +
            "\t\t\td.income_amt AS DECIMAL (18, 2)\n" +
            "\t\t)\n" +
            "\t) amount,\n" +
            "\tcount(d.id) tradeCount,\n" +
            "\tdate_format(d.created_time, '%Y-%m-%d') tradeTime,\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tcount(*)\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info s\n" +
            "\t\tWHERE\n" +
            "\t\t\ts.CUST_ID = c.cust_id\n" +
            "\t\tAND date_format(s.CREATED_TIME, '%Y-%m-%d') = date_format(d.created_time, '%Y-%m-%d')\n" +
            "\t) AS addedNum\n" +
            "FROM\n" +
            "\tt_cust_income_detail d,\n" +
            "\tt_customer_info c\n" +
            "WHERE\n" +
            "\td.cust_id = c.CUST_ID\n" +
            "AND c.CUST_ID = ?\n" +
            "GROUP BY\n" +
            "\tdate_format(d.created_time, '%Y-%m-%d')\n" +
            "ORDER BY\n" +
            "\tdate_format(d.created_time, '%Y-%m-%d') DESC")
    List<Map<String,Object>> queryDataByDays(String custId);

    /**
     * 交易分析页面数据 by Month
     * @param custId
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT\n" +
            "\tsum(\n" +
            "\t\tcast(\n" +
            "\t\t\td.income_amt AS DECIMAL (18, 2)\n" +
            "\t\t)\n" +
            "\t) amount,\n" +
            "\tcount(d.id) tradeCount,\n" +
            "\tdate_format(d.created_time, '%Y-%m') tradeTime,\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tcount(*)\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info s\n" +
            "\t\tWHERE\n" +
            "\t\t\ts.CUST_ID = c.cust_id\n" +
            "\t\tAND date_format(s.CREATED_TIME, '%Y-%m') = date_format(d.created_time, '%Y-%m')\n" +
            "\t) AS addedNum\n" +
            "FROM\n" +
            "\tt_cust_income_detail d,\n" +
            "\tt_customer_info c\n" +
            "WHERE\n" +
            "\td.cust_id = c.CUST_ID\n" +
            "AND c.CUST_ID = ?\n" +
            "GROUP BY\n" +
            "\tdate_format(d.created_time, '%Y-%m')\n" +
            "ORDER BY\n" +
            "\tdate_format(d.created_time, '%Y-%m') DESC")
    List<Map<String,Object>> queryDataByMonth(String custId);
    @Query(nativeQuery = true, value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\tt_cust_income_detail\n" +
            "WHERE\n" +
            "\tcust_id = ?\n" +
            "AND DATE_FORMAT(created_time, '%Y-%m-%d') = ?\n" +
            "ORDER BY\n" +
            "\tcreated_time DESC")
    List<CustIncomeDetailPO> fiinDetailByDay(String custId, String time);
    @Query(nativeQuery = true, value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\tt_cust_income_detail\n" +
            "WHERE\n" +
            "\tcust_id = ?\n" +
            "AND DATE_FORMAT(created_time, '%Y-%m') = ?\n" +
            "ORDER BY\n" +
            "\tcreated_time DESC")
    List<CustIncomeDetailPO> fiinDetailByMonth(String custId, String time);
}
