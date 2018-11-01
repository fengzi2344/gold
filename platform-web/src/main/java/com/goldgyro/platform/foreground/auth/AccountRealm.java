package com.goldgyro.platform.foreground.auth;

import com.goldgyro.platform.base.utils.MD5;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.goldgyro.platform.base.lang.Consts;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.sys.domain.AccountProfile;

/**
 * 
 * @author wg2993
 *
 */
public class AccountRealm extends AuthorizingRealm{

	@Autowired
    private CustomerService custService;

    public AccountRealm() {
        super(new AllowAllCredentialsMatcher());
        setAuthenticationTokenClass(UsernamePasswordToken.class);

        setCachingEnabled(false);
    }

    public static void main(String[] args) {
        System.out.println( MD5.md5("111111"));

    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.fromRealm(getName()).iterator().next();
        if (username != null) {
            Customer cust = custService.findByCustMobile(username);
            if (cust != null){
                SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
                /*List<AuthMenu> menuList = custService.getMenuList(cust.getId());
                for (AuthMenu menu : menuList){
                    if (StringUtils.isNotBlank(menu.getPermission())){
                        // 添加基于Permission的权限信息
                        for (String permission : StringUtils.split(menu.getPermission(),",")){
                            info.addStringPermission(permission);
                        }
                    }
                }*/
//                info.addRole(role.getKey());
//                for (Role r : user.getRoles()) {
//                    info.addRole(r.getName());
//                    ArrayList<String> ps = new ArrayList<String>();
//                    for(Permission p: r.getPermissions()){
//                        ps.add(p.getName());
//                    }
//                    
//                    info.addStringPermissions(ps);
//                }
                return info;
            }
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        AccountProfile profile = getAccount(custService, token);

        if(profile == null) {
        	return null;
        }
        
        if(profile.getStatus() == Consts.STATUS_CLOSED){
            throw new LockedAccountException(profile.getName());
        }

        AccountAuthenticationInfo info = new AccountAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
        info.setProfile(profile);

        return info;
    }

    protected AccountProfile getAccount(CustomerService custService, AuthenticationToken token){
        UsernamePasswordToken upToken = (UsernamePasswordToken)token;
        return custService.login(upToken.getUsername(), String.valueOf(upToken.getPassword()));
    }
}
