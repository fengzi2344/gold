package com.goldgyro.platform.core.sys.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.goldgyro.platform.core.sys.entity.GroupPO;

/**
 * @author wg2993
 *
 */
public interface GroupDao extends JpaRepository<GroupPO, Integer>, JpaSpecificationExecutor<GroupPO> {
	List<GroupPO> findAllByStatus(int status);
	GroupPO findByKey(String key);
}
