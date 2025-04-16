package top.nanguomm.mewMarket.strategy.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import top.nanguomm.mewMarket.constant.AuthConstants;
import top.nanguomm.mewMarket.strategy.LoginStrategy;

/**
 * 商城购物系统登录具体实现
 */
@Service(AuthConstants.MEMBER_LOGIN)
public class MemberLoginStrategy implements LoginStrategy {
    @Override
    public UserDetails realLogin(String username) {
        return null;
    }
}
