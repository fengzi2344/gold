package com.goldgyro.platform.core.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goldgyro.platform.base.lang.Consts;
import com.goldgyro.platform.core.comm.utils.BeanMapUtils;
import com.goldgyro.platform.core.sys.dao.GroupDao;
import com.goldgyro.platform.core.sys.domain.Group;
import com.goldgyro.platform.core.sys.entity.GroupPO;
import com.goldgyro.platform.core.sys.service.GroupService;

/**
 * @author langhsu
 *
 */
@Service
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {
	@Autowired
	private GroupDao groupDao;

	@Override
	public List<Group> findAll(int status) {
		List<GroupPO> list;
		if (status > Consts.IGNORE) {
			list = groupDao.findAllByStatus(status);
		} else {
			list = groupDao.findAll();
		}
		List<Group> rets = new ArrayList<>();
		list.forEach(po -> rets.add(BeanMapUtils.copy(po)));
		return rets;
	}

	/*@Override
	@Cacheable(value = "groupsCaches", key = "'g_' + #id")
	public Group getById(int id) {
		return BeanMapUtils.copy(groupDao.findOne(id));
	}*/

	@Override
	@Cacheable(value = "groupsCaches", key = "'g_' + #key")
	public Group getByKey(String key) {
		return BeanMapUtils.copy(groupDao.findByKey(key));
	}

	@Override
	public Group getById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Group group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		
	}

	/*@Override
	@Transactional
	public void update(Group group) {
		GroupPO po = groupDao.findOne(group.getId());
		if (po != null) {
			BeanUtils.copyProperties(group, po);
		} else {
			po = new GroupPO();
			BeanUtils.copyProperties(group, po);
			groupDao.save(po);
		}
	}

	@Override
	@Transactional
	public void delete(int id) {
		groupDao.delete(id);
	}
*/
}
