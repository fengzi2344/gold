package com.goldgyro.platform.core.client.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.goldgyro.platform.core.client.entity.InterfaceLogPO;

public interface InterfaceLogDao extends JpaRepository<InterfaceLogPO, String>, JpaSpecificationExecutor<InterfaceLogPO> {

	InterfaceLogPO findInterfaceLogBySerialNo(String serialNo);

}
