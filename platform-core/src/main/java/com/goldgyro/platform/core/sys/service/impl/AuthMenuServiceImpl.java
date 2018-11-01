package com.goldgyro.platform.core.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goldgyro.platform.core.comm.utils.BeanMapUtils;
import com.goldgyro.platform.core.sys.dao.AuthMenuDao;
import com.goldgyro.platform.core.sys.domain.AuthMenu;
import com.goldgyro.platform.core.sys.entity.AuthMenuPO;
import com.goldgyro.platform.core.sys.service.AuthMenuService;

@Service
@Transactional
public class AuthMenuServiceImpl implements AuthMenuService {
	
	@Autowired
	private AuthMenuDao authMenuDao;

	@Override
	public List<AuthMenu> findByParentId(long parentId) {
		// TODO Auto-generated method stub
		List<AuthMenu> authMenus = new ArrayList<>();
		List<AuthMenuPO> authMenuPOs = authMenuDao.findAllByParentIdOrderBySortAsc(parentId);
		if(authMenuPOs!=null){
			for(AuthMenuPO po : authMenuPOs){
				AuthMenu authMenu = BeanMapUtils.copy(po);
				authMenus.add(authMenu);
			}
		}
		return authMenus;
	}

	/*@Override
	public List<AuthMenu> tree(Long id) {

		List<AuthMenu> menus = new ArrayList<>();
		AuthMenuPO authMenuPO = authMenuDao.findOne(id);
		AuthMenu authMenu = BeanMapUtils.copy(authMenuPO);
		menus.add(authMenu);
		if(authMenu.getChildren()!=null){
//			List<AuthMenu> sortedList = sort(authMenu.getChildren());
			for (AuthMenu po: authMenu.getChildren()){
				menus.addAll(tree(po.getId()));
			}
		}
		return menus;
	}*/

	@Override
	public List<AuthMenu> listAll() {
		List<AuthMenuPO> list = authMenuDao.findAll();
		List<AuthMenu> rets = new ArrayList<>();
		list.forEach(po -> {
			AuthMenu a = new AuthMenu();
			BeanUtils.copyProperties(po, a, "parent", "roles", "children");
			rets.add(a);
		});
		return rets;
	}

	/*@Override
	public AuthMenu get(Long id) {
		AuthMenu authMenu = BeanMapUtils.copy(authMenuDao.findOne(id));
		return authMenu;
	}*/

	@Override
	public void save(AuthMenu authMenu) {
		AuthMenuPO po = new AuthMenuPO();
		BeanUtils.copyProperties(authMenu, po);
		authMenuDao.save(po);
	}

	@Override
	public List<AuthMenu> tree(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthMenu get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}

	/*@Override
	public void delete(Long id) {
		AuthMenuPO authMenuPO = authMenuDao.findOne(id);
		if(authMenuPO.getChildren()!=null){
			for(AuthMenuPO po : authMenuPO.getChildren()){
				delete(po.getId());
			}
		}
		authMenuDao.delete(authMenuPO);
	}*/

//	private List<AuthMenu> sort(List<AuthMenu> list) {
//		for(int i=0;i<list.size();i++){
//			for(int j=list.size()-1;j>i;j--){
//				if(list.get(i).getSort()>list.get(j).getSort()){
//					AuthMenu temp = list.get(i);
//					list.set(i,list.get(j));
//					list.set(j,temp);
//				}
//			}
//		}
//		return list;
//	}

}
