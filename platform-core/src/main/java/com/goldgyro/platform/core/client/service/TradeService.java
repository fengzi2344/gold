package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.Trade;

public interface TradeService {
    void save(Trade trade);
    Trade findByOrderId(String orderId);

    void updateFeeAnStatus(Trade trade, String type);
}
