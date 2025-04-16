package top.nanguomm.mewMarket.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.nanguomm.mewMarket.constant.AuthConstants;
import top.nanguomm.mewMarket.factory.LoginStrategyFactory;
import top.nanguomm.mewMarket.strategy.LoginStrategy;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final LoginStrategyFactory loginStrategyFactory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 获取请求对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        //从请求头中获取登录类型
        String loginType = request.getHeader(AuthConstants.LOGIN_TYPE);
        if (!StringUtils.hasText(loginType)) {
            throw new InternalAuthenticationServiceException("非法登录，登录类型不匹配");
        }
        // 通过登录策略工程获取具体的登录策略对象
        LoginStrategy instance = loginStrategyFactory.getInstance(loginType);
        return instance.realLogin(username);
    }
}
