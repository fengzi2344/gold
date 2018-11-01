package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.RedpackDetail;

import java.util.List;

public interface RedpackDetailService {
    List<RedpackDetail> findByRedpackId(String redpackId);
}
