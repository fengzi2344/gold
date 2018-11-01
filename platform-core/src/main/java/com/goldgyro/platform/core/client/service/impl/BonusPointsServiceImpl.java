package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.BonusPointsDao;
import com.goldgyro.platform.core.client.entity.BonusPoints;
import com.goldgyro.platform.core.client.service.BonusPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BonusPointsServiceImpl implements BonusPointsService {
    @Autowired
    private BonusPointsDao bonusPointsDao;
    @Override
    public BonusPoints findByCustId(String custId) {
        return bonusPointsDao.findByCustId(custId);
    }
}
