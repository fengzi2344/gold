package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.VersionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface VersionInfoDao  extends JpaRepository<VersionInfo, String>, JpaSpecificationExecutor<VersionInfo> {

    @Query(nativeQuery = true, value = "SELECT \n" +
            "  * \n" +
            "FROM\n" +
            "  t_version_info t \n" +
            "WHERE t.release_time = \n" +
            "  (SELECT \n" +
            "    MAX(s.release_time) \n" +
            "  FROM\n" +
            "    t_version_info s \n" +
            "  )")
    VersionInfo findNewestOne();
}
