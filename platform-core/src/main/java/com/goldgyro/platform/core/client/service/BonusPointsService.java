package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.BonusPoints;

public interface BonusPointsService {
    BonusPoints findByCustId(String custId);
}
