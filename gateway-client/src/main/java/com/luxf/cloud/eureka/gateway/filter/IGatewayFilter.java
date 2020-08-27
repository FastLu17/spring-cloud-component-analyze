package com.luxf.cloud.eureka.gateway.filter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author 小66
 * @date 2020-08-27 16:22
 **/
@Component
@Order(0)
public class IGatewayFilter implements GlobalFilter, Ordered {
    private static final Log LOG = LogFactory.getLog(IGatewayFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        RequestPath path = exchange.getRequest().getPath();
        LOG.info("path = " + path);
        String userName = exchange.getRequest().getQueryParams().getFirst("userName");
        LOG.info("userName = " + userName);
        if (StringUtils.isEmpty(userName)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            // 校验不通过,就直接结束(complete)请求.
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    /**
     * 定义过滤器的顺序, 实现{@link Ordered}接口,或者直接使用{@link Order}注解均可、
     *
     * @return current filter order
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
