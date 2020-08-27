package com.luxf.cloud.eureka.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author 小66
 * @date 2020-08-27 16:21
 **/
//@Configuration
public class GatewayConfiguration {

    /**
     * 使用Bean的方式配置routes、实际使用application配置文件.
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();
        routes.route("route_id", pre -> pre.path("/feign/**").uri("lb://FEIGN-SERVICE"))
                .route("route_id", pre -> pre.path("/hello/**").uri("lb://HELLO-SERVICE"));
        return routes.build();
    }

}
