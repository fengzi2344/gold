package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.VersionInfo;

public interface VersionInfoService {
    VersionInfo findNewestOne();
}
