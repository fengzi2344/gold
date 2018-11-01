package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TradeDao extends JpaRepository<Trade, String>, JpaSpecificationExecutor<Trade> {
}
