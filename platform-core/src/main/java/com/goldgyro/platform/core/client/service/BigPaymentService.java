package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.BigPayment;

public interface BigPaymentService {
    void saveBigPayment(BigPayment bigPayment);
    BigPayment findByWorkId(String workId);

    void process(String workId, String orderStatus);
}
