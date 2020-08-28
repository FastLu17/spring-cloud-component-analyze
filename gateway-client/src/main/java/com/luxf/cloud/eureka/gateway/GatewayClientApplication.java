package com.luxf.cloud.eureka.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Gateway的特性：基于spring framework 5, project reactor, spring boot 2.0, Spring web flux。响应式编程！
 * Gateway的作用：鉴权,流量控制,日志监控,反向代理,限流,集成Hystrix...
 * <p>
 * TODO：Gateway的三大核心：Routes(路由), Predicates(断言), Filters(过滤器).
 * Route：构建网关的基本模块,由ID,URI,一系列的Predicates和Filters组成。如果断言结果为true,则匹配该路由！
 * Predicate：类似{@link java.util.function.Predicate},可以根据不同的Predicate对HTTP请求中的对应内容(请求头,请求参数,URL等等)进行匹配.
 * Filter：{@link GatewayFilter}的实例,可以在请求或者路由前/后对请求进行修改。
 *
 * @see GatewayPredicate
 * @see RoutePredicateFactory
 * @see AsyncPredicate
 * @see GatewayFilter
 * @see GatewayFilterFactory
 * @see GatewayFilterChain 唯一实现{@link org.springframework.cloud.gateway.handler.FilteringWebHandler.DefaultGatewayFilterChain}
 * @see GatewayAutoConfiguration TODO：配置了很多内容
 * <p>
 * @see com.luxf.cloud.eureka.gateway.config.GatewayRouteConfiguration 对Predicate的解读.
 * @see com.luxf.cloud.eureka.gateway.filter.IGatewayFilter 对Filter的解读.
 */
@SpringBootApplication
@EnableEurekaClient
public class GatewayClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayClientApplication.class, args);
    }

}
