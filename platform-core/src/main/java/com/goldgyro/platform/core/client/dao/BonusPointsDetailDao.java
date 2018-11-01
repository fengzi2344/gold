package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.BonusPointsDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BonusPointsDetailDao extends JpaRepository<BonusPointsDetail,String>, JpaSpecificationExecutor<BonusPointsDetail> {
    List<BonusPointsDetail> findByPointIdOrderByCreateTimeDesc(String pointId);
}
