package top.nanguomm.mewMarket.filter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.nanguomm.mewMarket.config.WhiteUrlsConfig;
import top.nanguomm.mewMarket.constant.AuthConstants;
import top.nanguomm.mewMarket.constant.BusinessEnum;
import top.nanguomm.mewMarket.constant.HttpConstants;
import top.nanguomm.mewMarket.model.Result;

import java.util.Date;

/**
 * 全局token过滤器
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private final WhiteUrlsConfig whiteUrlsConfig;
    private final StringRedisTemplate stringRedisTemplate;
    //序列化
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        // 获取请求路径
        String path = request.getPath().toString();
        // 判断当前请求是否需要放行，是否在白名单中存在
        if (whiteUrlsConfig.getAllowUrls().contains(path)) {
            // 包含：请求路径在白名单，放行
            return chain.filter(exchange);
        }

        // 请求路径不在白名单，需进行身份验证
        // 检查请求头中身份验证信息
        String authorizationValue = request.getHeaders().getFirst(AuthConstants.AUTHORIZATION);
        // 判断是否有值
        if (StringUtils.hasText(authorizationValue)) {
            // 从Authorization中获取token
            String tokenValue = authorizationValue.replace(AuthConstants.BEARER, "");
            // 判断token是否有值，并在redis中存在
            if (StringUtils.hasText(tokenValue) && Boolean.TRUE.equals(stringRedisTemplate.hasKey(AuthConstants.LOGIN_TOKEN_PREFIX + tokenValue))) {
               // 身份通过，放行
                return chain.filter(exchange);
            }
        }

        // 流程如果走到这，说明身份验证没有通过或请求不合法
        log.error("拦截非法请求，时间：{}，请求API路径为{}",new Date(),path);

        // 获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        //设置响应头信息
        response.getHeaders().set(HttpConstants.CONTENT_TYPE,HttpConstants.APPLICATION_JSON);
        // 设置响应的消息
        Result<Object> result = Result.fail(BusinessEnum.UN_AUTHORIZATION);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
