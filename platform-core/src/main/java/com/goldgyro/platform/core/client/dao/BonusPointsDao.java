package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.BonusPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BonusPointsDao extends JpaRepository<BonusPoints,String>, JpaSpecificationExecutor<BonusPoints> {

    @Query(nativeQuery = true, value = "select * from t_bonus_points where cust_id = ?")
    BonusPoints findByCustId(String custId);
}
