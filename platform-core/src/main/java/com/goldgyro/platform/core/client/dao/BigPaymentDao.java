package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.BigPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BigPaymentDao extends JpaRepository<BigPayment,String>, JpaSpecificationExecutor<BigPayment> {

    BigPayment findByWorkId(String workId);
}
