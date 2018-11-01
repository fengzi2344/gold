package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.RedpackDetailDao;
import com.goldgyro.platform.core.client.entity.RedpackDetail;
import com.goldgyro.platform.core.client.service.RedpackDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RedpackDetailServiceImpl implements RedpackDetailService {
    @Autowired
    private RedpackDetailDao redpackDetailDao;
    @Override
    public List<RedpackDetail> findByRedpackId(String redpackId) {
        return redpackDetailDao.findByRedpackIdOrderByCreateTimeDesc(redpackId);
    }
}
