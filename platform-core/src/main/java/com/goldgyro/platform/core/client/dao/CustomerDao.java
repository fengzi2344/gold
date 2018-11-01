package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.goldgyro.platform.core.client.entity.CustomerPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * 客户信息处理DAO
 *
 * @author wg2993
 * @version 2018/07/09
 */
public interface CustomerDao extends JpaRepository<CustomerPO, String>, JpaSpecificationExecutor<CustomerPO> {
    /**
     * 查找 客户
     *
     * @param custId
     * @return CustomerPO
     */
    CustomerPO findByCustId(String custId);

    /**
     * 通过客户手机号号码获取客户信息
     *
     * @param custMobile
     * @return
     */
    @Query(nativeQuery = true, value = "select * from t_customer_info where CUST_MOBILE = :custMobile or CUST_CODE= :custMobile")
    CustomerPO findByCustMobile(@Param("custMobile") String custMobile);

    @Modifying
    @Query(nativeQuery = true, value = "update t_customer_info t set cust_status = ? where exists(select 1 from t_virtual_merchant s where s.cust_id = t.cust_id and s.merchant_code = ?)")
    void updateCustStatus(String custStatus, String merchantCode);

    @Modifying
    @Query(nativeQuery = true, value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\tt_customer_info t\n" +
            "WHERE\n" +
            "\tt.CUST_ID = ?\n" +
            "AND EXISTS (\n" +
            "\tSELECT\n" +
            "\t\t1\n" +
            "\tFROM\n" +
            "\t\tt_bank_card c1\n" +
            "\tWHERE\n" +
            "\t\tc1.cust_id = t.cust_id\n" +
            "\tAND c1.id = ?\n" +
            ")\n" +
            "AND EXISTS (\n" +
            "\tSELECT\n" +
            "\t\t1\n" +
            "\tFROM\n" +
            "\t\tt_bank_card c2\n" +
            "\tWHERE\n" +
            "\t\tc2.cust_id = t.cust_id\n" +
            "\tAND c2.id = ?\n" +
            ")")
    List<CustomerPO> findCustomer(String custId, String inCardId, String outCardId);

    List<CustomerPO> findAllByCustMobile(String mobile);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tCOUNT(*)\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info s\n" +
            "\t\tWHERE\n" +
            "\t\t\tLOCATE(t.level_code, s.level_code) > 0\n" +
            "\t\tAND s.level_code != t.level_code\n" +
            "\t) AS totalNum,\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tCOUNT(*)\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info s\n" +
            "\t\tWHERE\n" +
            "\t\t\ts.INVITER_ID = t.cust_mobile\n" +
            "\t) AS ztNum,\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tCOUNT(*)\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info s\n" +
            "\t\tWHERE\n" +
            "\t\t\ts.INVITER_ID = t.cust_mobile\n" +
            "\t\tAND s.CUST_LEVEL_SAMPLE = 'VIP'\n" +
            "\t) AS vipNum,\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tCOUNT(*)\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info s\n" +
            "\t\tWHERE\n" +
            "\t\t\ts.INVITER_ID = t.cust_mobile\n" +
            "\t\tAND s.CUST_LEVEL_SAMPLE = 'MEMBER'\n" +
            "\t) AS memberNum,\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tCOUNT(*)\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info s\n" +
            "\t\tWHERE\n" +
            "\t\t\ts.INVITER_ID = t.CUST_MOBILE\n" +
            "\t\tand s.CUST_STATUS = 'AUTH'\n" +
            "\t) realNum\n" +
            "FROM\n" +
            "\tt_customer_info t\n" +
            "WHERE\n" +
            "\tt.CUST_ID = ?")
    Map<String, Object> queryCustomerAnalysisData(String custId);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\tt_customer_info t\n" +
            "WHERE\n" +
            "\tEXISTS (\n" +
            "\t\tSELECT\n" +
            "\t\t\t1\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info s\n" +
            "\t\tWHERE\n" +
            "\t\t\ts.cust_id = ?\n" +
            "\t\tAND s.father_mobile = t.CUST_MOBILE\n" +
            "\t)")
    CustomerPO queryCustPo(String custId);

    @Modifying
    @Query(nativeQuery = true, value = "update t_customer_info  set CUST_LEVEL_SAMPLE = ? where cust_id = ?")
    void updateCustType(String type, String custId);

    @Query(nativeQuery = true, value = "SELECT \n" +
            "  * \n" +
            "FROM\n" +
            "  t_customer_info t \n" +
            "WHERE EXISTS \n" +
            "  (SELECT \n" +
            "    1 \n" +
            "  FROM\n" +
            "    t_payment_apply s,\n" +
            "    t_withdraw_plan p \n" +
            "  WHERE p.apply_id = s.id \n" +
            "    AND s.CUST_ID = t.CUST_ID \n" +
            "    AND p.id = ?)")
    CustomerPO queryByWithdrawId(String withdrawId);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "\tm.PLAT_MERCHANT_CODE platMerchantCode,\n" +
            "\tb.ACCOUNT_CODE bankAccountNo,\n" +
            "\tb.MOBILE_NO phoneno\n" +
            "FROM\n" +
            "\tt_customer_info t,\n" +
            "\tt_virtual_merchant m,\n" +
            "\tt_bank_card b\n" +
            "WHERE\n" +
            "\tt.cust_id = m.cust_id\n" +
            "AND t.cust_id = b.cust_id\n" +
            "AND t.cust_id = ?\n" +
            "AND b.is_default = 'YES'")
    Map<String, Object> queryParamMap(String custId);

    @Procedure(name = "modify_merchant")
    void modifyMerchant(@Param("v_code") String code);

    @Query(nativeQuery = true, value = "select count(1) from t_customer_info where level_code <> :levelCode and level_code like :levelCode1 and LENGTH(level_code) = LENGTH(:levelCode) +3")
    int findNumByLevelCode(@Param("levelCode") String levelCode, @Param("levelCode1") String levelCode1);

    @Query(nativeQuery = true, value = "select * from t_customer_info where INVITER_ID = 'root'")
    List<CustomerPO> findCustList();

    @Query(nativeQuery = true, value = "select * from t_customer_info where inviter_id = ? order by CREATED_TIME desc")
    List<CustomerPO> findAllByInviterId(String custMobile);

    @Query(nativeQuery = true, value = "select * from t_customer_info t where exists(select 1 from t_payment_plan p where p.cust_id = t.cust_id and p.trans_no = ?)")
    CustomerPO findByTranNo(String consumeOrderId);

    @Query(nativeQuery = true, value = "select * from t_customer_info where LOCATE(level_code,?) > 0 order by level_code")
    List<CustomerPO> findUpperNote(String levelCode);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\t*\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info\n" +
            "\t\tWHERE\n" +
            "\t\t\tLOCATE(\n" +
            "\t\t\t\tLEVEL_CODE,\n" +
            "\t\t\t\t?\n" +
            "\t\t\t) > 0\n" +
            "\t\tAND (\n" +
            "\t\t\t(\n" +
            "\t\t\t\t(\n" +
            "\t\t\t\t\tifnull(direct_num, 0) >= 10 && ifnull(direct_num, 0) < 20\n" +
            "\t\t\t\t)\n" +
            "\t\t\t\tAND ifnull(group_num, 0) >= 100\n" +
            "\t\t\t)\n" +
            "\t\t\tOR (\n" +
            "\t\t\t\t(\n" +
            "\t\t\t\t\tifnull(group_num, 0) >= 100 && ifnull(group_num, 0) < 200\n" +
            "\t\t\t\t)\n" +
            "\t\t\t\tAND ifnull(direct_num, 0) >= 10\n" +
            "\t\t\t)\n" +
            "\t\t)\n" +
            "\t\tORDER BY\n" +
            "\t\t\tlength(LEVEL_CODE) ASC\n" +
            "\t) tmp\n" +
            "LIMIT 0,\n" +
            " 1")
    CustomerPO findV1(String levelCode);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\t*\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info\n" +
            "\t\tWHERE\n" +
            "\t\t\tLOCATE(\n" +
            "\t\t\t\tLEVEL_CODE,\n" +
            "\t\t\t\t?\n" +
            "\t\t\t) > 0\n" +
            "\t\tAND (\n" +
            "\t\t\t(\n" +
            "\t\t\t\t(\n" +
            "\t\t\t\t\tifnull(direct_num,0) >= 20 && ifnull(direct_num,0) < 30\n" +
            "\t\t\t\t)\n" +
            "\t\t\t\tAND group_num >= 200\n" +
            "\t\t\t)\n" +
            "\t\t\tOR (\n" +
            "\t\t\t\t(\n" +
            "\t\t\t\t\tgroup_num >= 200 && group_num < 300\n" +
            "\t\t\t\t)\n" +
            "\t\t\t\tAND ifnull(direct_num,0) >= 20\n" +
            "\t\t\t)\n" +
            "\t\t)\n" +
            "\t\tORDER BY\n" +
            "\t\t\tlength(LEVEL_CODE) ASC\n" +
            "\t) tmp\n" +
            "LIMIT 0,\n" +
            " 1")
    CustomerPO findV2(String levelCode);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\t*\n" +
            "\t\tFROM\n" +
            "\t\t\tt_customer_info\n" +
            "\t\tWHERE\n" +
            "\t\t\tLOCATE(LEVEL_CODE, ?) > 0\n" +
            "\t\tAND ifnull(direct_num, 0) >= 30\n" +
            "\t\tAND IFNULL(group_num, 0) >= 300\n" +
            "\t\tORDER BY\n" +
            "\t\t\tlength(LEVEL_CODE) ASC\n" +
            "\t) tmp\n" +
            "LIMIT 0,\n" +
            " 1")
    CustomerPO findV3(String levelCode);

    @Query(nativeQuery = true, value = "select max(cust_code) from t_customer_info")
    Integer findMaxCode();

    @Query(nativeQuery = true, value = "select * from t_customer_info where inviter_id = ? and CUST_LEVEL_SAMPLE = 'VIP' order by CREATED_TIME desc")
    List<CustomerPO> findVipList(String custMobile);
    @Query(nativeQuery = true, value = "select * from t_customer_info t where t.cust_level_sample <> 'NORMAL' and locate(t.level_code,?) > 0  ORDER BY level_code  desc")
    List<CustomerPO> findNotNormal(String levelCode);

    List<CustomerPO> findAllByInviterIdAndCustStatus(String inviterId, String custStatus);

    @Query(nativeQuery = true, value = "select * from t_customer_info where inviter_id = ? and CUST_STATUS = 'AUTH' order by CREATED_TIME desc")
    List<CustomerPO> findDirectValidList(String mobile);
}
