package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.BonusPointsDetailDao;
import com.goldgyro.platform.core.client.entity.BonusPointsDetail;
import com.goldgyro.platform.core.client.service.BonusPointsDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BonusPointsDetailServiceImpl implements BonusPointsDetailService {
    @Autowired
    private BonusPointsDetailDao bonusPointsDetailDao;
    @Override
    public List<BonusPointsDetail> findByBonus(String bonusId) {
        return bonusPointsDetailDao.findByPointIdOrderByCreateTimeDesc(bonusId);
    }
}
