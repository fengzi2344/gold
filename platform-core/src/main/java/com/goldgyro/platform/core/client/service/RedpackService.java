package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.Redpack;

import java.util.List;

public interface RedpackService {
    Redpack findByCustId(String custId);

    void transfer(Redpack redpack, String money);

    int[] doUnpack(Redpack redpack);
}
