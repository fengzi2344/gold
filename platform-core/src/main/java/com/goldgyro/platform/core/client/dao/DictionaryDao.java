package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface DictionaryDao extends JpaRepository<Dictionary,String>,JpaSpecificationExecutor<Dictionary> {
    Dictionary findOne(String id);
    List<Dictionary> findAllByParentCodeOrderByCodeAsc(String parentCode);
    @Query(value = "select code,name from t_dictionary where name like ? and parent_code = ? order by code",nativeQuery = true)
    List<Map<String,String>> findByTypeAndLevelOrderByCode(String li ,String parent_code);

    @Query(nativeQuery = true, value = "select * from t_dictionary where code = ? limit 0,1")
    Dictionary finByCode(String code);
}
