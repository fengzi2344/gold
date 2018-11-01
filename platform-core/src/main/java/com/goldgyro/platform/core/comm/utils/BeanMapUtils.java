package com.goldgyro.platform.core.comm.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.sys.domain.AccountProfile;
import com.goldgyro.platform.core.sys.domain.AuthMenu;
import com.goldgyro.platform.core.sys.domain.Group;
import com.goldgyro.platform.core.sys.domain.Role;
import com.goldgyro.platform.core.sys.entity.AuthMenuPO;
import com.goldgyro.platform.core.sys.entity.GroupPO;
import com.goldgyro.platform.core.sys.entity.RolePO;

/**
 * @author wg2993
 * @version 2018/07/08
 *
 */
public class BeanMapUtils {
	public static String[] USER_IGNORE = new String[]{"password", "extend", "roles"};

	public static String[] POST_IGNORE_LIST = new String[]{"markdown", "content"};

	/*public static User copy(UserPO po, int level) {
		if (po == null) {
			return null;
		}
		User ret = new User();
		BeanUtils.copyProperties(po, ret, USER_IGNORE);
		
		if (level > 0) {
			List<RolePO> rolePOs = po.getRoles();
			List<Role> roles = new ArrayList<Role>();
			for(RolePO rolePo :rolePOs){
				Role role = copy(rolePo);
				roles.add(role);
			}
			ret.setRoles(roles);
		}
		return ret;
	}*/

	public static AccountProfile copyPassport(Customer cust) {
		AccountProfile passport = new AccountProfile(cust.getCustId(), cust.getCustName());
		passport.setName(cust.getCustName());
		passport.setEmail(cust.getCustEmail());
		passport.setLastLogin(cust.getLastLogin());
		passport.setStatus(cust.getCustStatus());

		List<AuthMenu> menus = new ArrayList<AuthMenu>();
		/*List<Role> roleList = cust.getRoleList();
		for(Role role :roleList){
			menus.addAll(role.getAuthMenus());
		}*/
		passport.setAuthMenus(menus);
		
		return passport;
	}

	public static Group copy(GroupPO po) {
		Group r = new Group();
		BeanUtils.copyProperties(po, r);
		return r;
	}

	public static AuthMenu copy(AuthMenuPO po) {
		AuthMenu am = new AuthMenu();
		BeanUtils.copyProperties(po, am, "children");
		return am;
	}

	public static Role copy(RolePO po) {
		Role r = new Role();
		BeanUtils.copyProperties(po, r, "users", "authMenus");
		List<AuthMenu> authMenus = new ArrayList<>();
		for (AuthMenuPO authMenuPO : po.getAuthMenus()) {
			AuthMenu authMenu = new AuthMenu();
			BeanUtils.copyProperties(authMenuPO, authMenu, "roles", "children");
			authMenus.add(authMenu);
		}
		r.setAuthMenus(authMenus);
		return r;
	}

	
}
