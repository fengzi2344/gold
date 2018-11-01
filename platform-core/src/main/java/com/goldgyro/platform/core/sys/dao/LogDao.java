package com.goldgyro.platform.core.sys.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.goldgyro.platform.core.sys.entity.LogPO;

/**
 * @author wg2993
 *
 */
public interface LogDao extends JpaRepository<LogPO, Long>, JpaSpecificationExecutor<LogPO> {
}
