package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.IncomeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IncomeDetailDao extends JpaRepository<IncomeDetail, String>, JpaSpecificationExecutor<IncomeDetail> {
    IncomeDetail findByCustIncomeId(String incomeId);
}
