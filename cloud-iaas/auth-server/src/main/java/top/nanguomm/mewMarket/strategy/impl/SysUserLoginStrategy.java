package top.nanguomm.mewMarket.strategy.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import top.nanguomm.mewMarket.constant.AuthConstants;
import top.nanguomm.mewMarket.domain.LoginSysUser;
import top.nanguomm.mewMarket.mapper.LoginSysUserMapper;
import top.nanguomm.mewMarket.model.SecurityUser;
import top.nanguomm.mewMarket.strategy.LoginStrategy;

import java.util.Set;

/**
 * 商城后台管理系统登录策略的具体实现
 */
@Service(AuthConstants.SYS_USER_LOGIN)
@RequiredArgsConstructor
public class SysUserLoginStrategy implements LoginStrategy {

    private final LoginSysUserMapper loginSysUserMapper;

    @Override
    public UserDetails realLogin(String username) {
        // 根据用户名称查询用户对象
        LoginSysUser loginSysUser = loginSysUserMapper.selectOne(new LambdaQueryWrapper<LoginSysUser>()
                .eq(LoginSysUser::getUsername, username));
        if (ObjectUtil.isNotNull(loginSysUser)) {
            // 根据用户标识查询权限集合
            Set<String> perms = loginSysUserMapper.selectPermsByUserId(loginSysUser.getUserId());
            // 创建安全用户对象
            SecurityUser securityUser = new SecurityUser();
            securityUser.setUserId(loginSysUser.getUserId());
            securityUser.setPassword(loginSysUser.getPassword());
            securityUser.setShopId(loginSysUser.getShopId());
            securityUser.setStatus(loginSysUser.getStatus());
            securityUser.setLoginType(AuthConstants.SYS_USER_LOGIN);
            // 判断用户是否有值
            if (CollectionUtil.isNotEmpty(perms) && !perms.isEmpty()) {
                securityUser.setPerms(perms);
            }
            return securityUser;
        }
        return null;
    }
}
