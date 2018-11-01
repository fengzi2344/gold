package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.BonusPointsDetail;

import java.util.List;

public interface BonusPointsDetailService {
    List<BonusPointsDetail> findByBonus(String bonusId);
}
