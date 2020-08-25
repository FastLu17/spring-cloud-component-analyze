package com.luxf.cloud.eureka.feign.client;

import com.netflix.loadbalancer.*;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClientName;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;


/**
 * Ribbon的负载均衡策略默认是{@link ZoneAvoidanceRule}、
 *
 * Feign 已经集成了 Ribbon的负载均衡策略。
 * 在具体开发中, 不会使用Ribbon的请求方式进行服务间的调用, 而是使用Feign的声明式接口服务调用！
 * <p>
 * {@link EnableFeignClients}注解内部通过{@link org.springframework.cloud.openfeign.FeignClientsRegistrar}实现, 该Registrar会扫描具有{@link FeignClient}注解的对象注入到容器中、
 */
@EnableFeignClients
@EnableHystrix
@SpringCloudApplication
public class FeignClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignClientApplication.class, args);
    }

    /**
     * 如果没有直接注入{@link IRule,ILoadBalancer,IPing,ServerList,ServerListFilter}等ribbon负载均衡相关的Bean对象时、
     * 在发送远程调用请求时,才会进行Bean的初始化！当向不同的微服务调用时,都会进行第一次初始化！
     * @see RibbonClientConfiguration 该配置文件会在发送请求的时候,进行初始化。
     * {@link RibbonClientConfiguration#name}：name字段上的{@link RibbonClientName}注解会被初始化为被请求的微服务的名字,即{@link FeignClient}注解的value、
     *
     * @see PropertiesFactory#getClassName(Class, String) TODO: 可以通过yml配置文件配置请求不同微服务时,配置不同的负载均衡策略！
     * @see PropertiesFactory#classToProperty 该Map对象中配置了对应Bean可用的value,如'NFLoadBalancerRuleClassName','NFLoadBalancerClassName'
     *
     * TODO: 可以在yml文件中配置多个不同的策略！ 如果自定义注入了Ribbon负载均衡相关的Bean,则配置文件中的对应Bean规则不再生效、
     * // 请求HELLO-SERVICE微服务时,使用随机的策略、
     * HELLO-SERVICE:
     * ribbon:
     * NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
     * // 请求USER-SERVICE微服务时,使用高可用(最佳可用)的策略
     * USER-SERVICE:
     * ribbon:
     * NFLoadBalancerRuleClassName: com.netflix.loadbalancer.BestAvailableRule
     */
//    @Bean
//    public IRule iRule() {
//        TODO：如果此时初始化 负载均衡的IRule,则全局都会使用该策略. 在yml配置的IRule将不再生效。
//        return new RandomRule();
//    }
}
