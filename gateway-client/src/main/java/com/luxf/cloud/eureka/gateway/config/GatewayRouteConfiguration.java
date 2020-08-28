package com.luxf.cloud.eureka.gateway.config;

import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 路由根据Predicate匹配来生成.
 * {@link org.springframework.cloud.gateway.config.GatewayAutoConfiguration#routeDefinitionRouteLocator(GatewayProperties, List, List, RouteDefinitionLocator, ConfigurationService)}
 * <p>
 * 在初始化时,会获取容器中所有的{@link RoutePredicateFactory}, 总共13个.
 * {@link RouteDefinitionRouteLocator#convertToRoute(RouteDefinition)}这个方法中,TODO：application配置文件中的route相关predicates和filters进行处理。
 * <p>
 * private Route convertToRoute(RouteDefinition routeDefinition) {
 *      // TODO：合并当前route下的所有的predicates,由{@link AsyncPredicate.DefaultAsyncPredicate}代理{@link RoutePredicateFactory}对应的Predicate.
 *      AsyncPredicate<ServerWebExchange> predicate = combinePredicates(routeDefinition);
 *
 *      // TODO：获取当前route下的所有filters.
 *      List<GatewayFilter> gatewayFilters = getFilters(routeDefinition);
 *
 *      // TODO：生成异步路由.
 *      return Route.async(routeDefinition).asyncPredicate(predicate)
 *              .replaceFilters(gatewayFilters).build();
 * }
 *
 * TODO：{@link RouteDefinitionRouteLocator#combinePredicates(RouteDefinition)}方法的解读
 * private AsyncPredicate<ServerWebExchange> combinePredicates(RouteDefinition routeDefinition) {
 * 		List<PredicateDefinition> predicates = routeDefinition.getPredicates();
 *
 * 	    // {@link RouteDefinitionRouteLocator#lookup(RouteDefinition, PredicateDefinition)}
 * 	    // TODO：在lookup()方法内,获取与{@link PredicateDefinition}对应的{@link RoutePredicateFactory}, 生成代理对象{@link AsyncPredicate.DefaultAsyncPredicate}
 * 		AsyncPredicate<ServerWebExchange> predicate = lookup(routeDefinition, predicates.get(0));
 *
 * 		for (PredicateDefinition andPredicate : predicates.subList(1, predicates.size())) {
 * 			AsyncPredicate<ServerWebExchange> found = lookup(routeDefinition, andPredicate);
 * 		    // TODO：and()方法 -> 类似以'&&'的方法连接所有的Predicate.
 * 			predicate = predicate.and(found);
 *      }
 *
 * 		return predicate;
 * }
 *
 * @see DiscoveryClientRouteDefinitionLocator#getRouteDefinitions() TODO：生成{@link RouteDefinition}和{@link PredicateDefinition}
 * @author 小66
 * @date 2020-08-27 16:21
 **/
//@Configuration
public class GatewayRouteConfiguration {

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
