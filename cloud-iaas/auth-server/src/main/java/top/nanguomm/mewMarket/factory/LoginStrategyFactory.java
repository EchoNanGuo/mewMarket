package top.nanguomm.mewMarket.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.nanguomm.mewMarket.strategy.LoginStrategy;

import java.util.Map;

/**
 * 登录策略工厂类
 */
@Component
@RequiredArgsConstructor
public class LoginStrategyFactory {
    private final Map<String, LoginStrategy> loginStrategies;

    /**
     * 根据用户登录类型获取具体的登录策略
      * @param loginType
     * @return
     */
    public LoginStrategy getInstance(String loginType) {
        return loginStrategies.get(loginType);
    }

}
