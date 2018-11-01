package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.ApplyFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ApplyFeeDao  extends JpaRepository<ApplyFee, String>, JpaSpecificationExecutor<ApplyFee> {
    @Query(nativeQuery = true, value = "SELECT\n" +
            "  *\n" +
            "FROM\n" +
            "  t_apply_fee t\n" +
            "WHERE EXISTS\n" +
            "  (SELECT\n" +
            "    1\n" +
            "  FROM\n" +
            "    t_payment_plan pp\n" +
            "  WHERE pp.APPLY_ID = t.apply_id\n" +
            "    AND pp.TRANS_NO = ?\n" +
            "    AND pp.PAYMENT_TIME =\n" +
            "    (SELECT\n" +
            "      MIN(ppp.payment_time)\n" +
            "    FROM\n" +
            "      t_payment_plan ppp\n" +
            "    WHERE ppp.APPLY_ID = pp.APPLY_ID))")
    ApplyFee findByTranNo(String transNo);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\tt_apply_fee t\n" +
            "WHERE\n" +
            "\tEXISTS (\n" +
            "\t\tSELECT\n" +
            "\t\t\t1\n" +
            "\t\tFROM\n" +
            "\t\t\tt_payment_plan pp\n" +
            "\t\tWHERE\n" +
            "\t\t\tpp.APPLY_ID = t.apply_id\n" +
            "\t\tAND pp.TRANS_NO = ?\n" +
            "\t\tAND pp.PAYMENT_TIME = (\n" +
            "\t\t\tSELECT\n" +
            "\t\t\t\tMAX(ppp.payment_time)\n" +
            "\t\t\tFROM\n" +
            "\t\t\t\tt_payment_plan ppp\n" +
            "\t\t\tWHERE\n" +
            "\t\t\t\tppp.APPLY_ID = pp.APPLY_ID\n" +
            "\t\t)\n" +
            "\t)")
    ApplyFee findByMaxTime(String transNo);
}
