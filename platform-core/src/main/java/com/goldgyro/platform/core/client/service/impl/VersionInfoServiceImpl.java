package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.VersionInfoDao;
import com.goldgyro.platform.core.client.entity.VersionInfo;
import com.goldgyro.platform.core.client.service.VersionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VersionInfoServiceImpl implements VersionInfoService {
    @Autowired
    private VersionInfoDao versionInfoDao;
    @Override
    public VersionInfo findNewestOne() {
        return versionInfoDao.findNewestOne();
    }
}
