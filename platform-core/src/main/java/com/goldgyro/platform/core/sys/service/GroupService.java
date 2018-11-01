package com.goldgyro.platform.core.sys.service;

import java.util.List;

import com.goldgyro.platform.core.sys.domain.Group;

/**
 *暂时添加修改都在数据库操作
 * 
 * @author wg2993
 *@version 2018/07/08
 */
public interface GroupService {
	List<Group> findAll(int status);
	Group getById(int id);
	Group getByKey(String key);
	void update(Group group);
	void delete(int id);
}
