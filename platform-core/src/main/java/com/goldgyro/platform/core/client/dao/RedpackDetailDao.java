package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.RedpackDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RedpackDetailDao extends JpaRepository<RedpackDetail,String>, JpaSpecificationExecutor<RedpackDetail> {
    List<RedpackDetail> findByRedpackIdOrderByCreateTimeDesc(String redpackId);
}
