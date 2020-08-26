package com.luxf.cloud.eureka.feign.client;

import com.netflix.client.IClient;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import feign.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClientName;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.ribbon.FeignLoadBalancer;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;

import java.util.List;


/**
 * TODO：对于{@link FeignClient}的接口发起HTTP请求大致流程：
 * 1、先是openfeign开放了一个{@link Client}接口用于http请求，并且{@link LoadBalancerFeignClient}作为该HTTP请求Client的实现类.
 * 2、利用{@link LoadBalancerFeignClient#lbClient(String)}直接构造了有关Ribbon的{@link IClient}接口的实现类{@link FeignLoadBalancer},执行executeWithLoadBalancer()方法.
 * 3、执行Ribbon的request之前，先委托{@link ILoadBalancer}负载均衡器选择一个{@link Server},然后回调执行request请求.
 * 4、ILoadBalancer会选择{@link IRule}实现的负载均衡算法来获取一个Server,并返回.
 * <p>
 * TODO：
 * Ribbon的{@link IRule}默认是{@link ZoneAvoidanceRule},再进行Server过滤后,会调用{@link AbstractServerPredicate#chooseRoundRobinAfterFiltering(List, Object)} --> TODO：该方法的底层依然是使用CAS算法实现轮询。
 * Ribbon的{@link ILoadBalancer}默认是{@link ZoneAwareLoadBalancer}
 *
 * TODO：{@link PredicateBasedRule#choose(Object)}方法的具体实现、 {@link ZoneAvoidanceRule}虽然实现{@link PredicateBasedRule}类,但并没有重写choose()方法、
 * public Server choose(Object key) {
 *      ILoadBalancer lb = getLoadBalancer();
 *      Optional<Server> server = getPredicate().chooseRoundRobinAfterFiltering(lb.getAllServers(), key);
 *      if (server.isPresent()) {
 *          return server.get();
 *      } else {
 *          return null;
 *      }
 * }
 *
 * @see RibbonClientConfiguration#ribbonRule(IClientConfig)
 * @see RibbonClientConfiguration#ribbonClientConfig() 会初始化{@link DefaultClientConfigImpl}
 * <p>
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
     *
     * @see RibbonClientConfiguration 该配置文件会在发送请求的时候,进行初始化。
     * {@link RibbonClientConfiguration#name}：name字段上的{@link RibbonClientName}注解会被初始化为被请求的微服务的名字,即{@link FeignClient}注解的value、
     * @see PropertiesFactory#getClassName(Class, String) TODO: 可以通过yml配置文件配置请求不同微服务时,配置不同的负载均衡策略！
     * @see PropertiesFactory#classToProperty 该Map对象中配置了对应Bean可用的value,如'NFLoadBalancerRuleClassName','NFLoadBalancerClassName'
     * <p>
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

    private static final String SERVICE_NAME = "HELLO-SERVICE";

    /**
     * 简单解析各种{@link IRule}负载均衡规则、主要是{@link IRule#choose(Object)}方法选取一个可用的{@link Server}.
     * {@link AbstractLoadBalancerRule} 是{@link IRule}接口的直接实现类, 内部维护{@link ILoadBalancer}接口对象.
     *
     * TODO：如果要自定义IRule算法, 则继承{@link AbstractLoadBalancerRule}即可、
     */
    private void loadBalancerRule() {
        RandomRule randomRule = new RandomRule();
        /**
         * TODO: 随机规则
         * 具体实现：利用{@link java.util.concurrent.ThreadLocalRandom}对象,产生一个随机的index.
         * @see RandomRule#chooseRandomInt(int)
         */
        randomRule.choose(SERVICE_NAME);

        RoundRobinRule roundRobinRule = new RoundRobinRule();
        /**
         * TODO：轮询规则
         * 具体实现：利用{@link java.util.concurrent.atomic.AtomicInteger#compareAndSet(int, int)}CAS算法,获取轮询的next index.
         * @see RoundRobinRule#incrementAndGetModulo(int)
         *
         * RoundRobinRule有2个直接实现：
         * @see WeightedResponseTimeRule 根据请求响应时间的权重进行负载均衡.
         * @see ResponseTimeWeightedRule 弃用、推荐{@link WeightedResponseTimeRule}
         */
        roundRobinRule.choose(SERVICE_NAME);

        /**
         * TODO：重试规则、{@link RetryRule#subRule}内部默认维护一个{@link RoundRobinRule}(可被覆盖)作为子规则.
         * 使用subRule执行choose(),没有获取Server时,在最大的重试时间{@link RetryRule#maxRetryMillis}范围内,都可以subRule进行重试.
         * @see RetryRule#subRule
         * @see RetryRule#maxRetryMillis
         */
        RetryRule retryRule = new RetryRule();
        retryRule.choose(SERVICE_NAME);

        /**
         * TODO：可配置的轮询规则、{@link ClientConfigEnabledRoundRobinRule#roundRobinRule}内部默认维护一个{@link RoundRobinRule}.
         * 该对象有4种实现、如果没有不使用具体的实现,则默认使用{@link RoundRobinRule#choose(Object)}
         *
         * @see BestAvailableRule 最佳可用(最空闲)规则. 跳过带有"跳闸"断路器的服务器并选择并发请求最少的服务器的规则。
         * @see PredicateBasedRule 抽象类、将Server过滤逻辑委派给{@link AbstractServerPredicate}实例的规则. 在实现类中,都维护了{@link AbstractServerPredicate}实例.
         * @see ZoneAvoidanceRule Ribbon的{@link IRule}接口的默认实现
         * @see AvailabilityFilteringRule
         *
         * TODO：{@link PredicateBasedRule}的2个实现类, 过滤后会调用{@link AbstractServerPredicate#chooseRoundRobinAfterFiltering(List, Object)} --> 该方法的底层依然是使用CAS算法实现轮询。
         */
        ClientConfigEnabledRoundRobinRule clientConfigRule = new ClientConfigEnabledRoundRobinRule();
        clientConfigRule.choose(SERVICE_NAME);

        /**
         * TODO：高可用规则、内部维护一个{@link LoadBalancerStats}对象,用于统计{@link ILoadBalancer}中的每个Server的相关特性,以便于选出最空闲的Server.
         * 需要通过{@link BestAvailableRule#setLoadBalancer(ILoadBalancer)} 初始化{@link BestAvailableRule#loadBalancerStats}属性, 否则使用父类的RoundRobinRule进行选取、
         * @see BestAvailableRule#loadBalancerStats
         */
        BestAvailableRule bestAvailableRule = new BestAvailableRule();
        /**
         * 对所有的{@link Server}进行遍历,选取{@link ServerStats#getActiveRequestsCount()}'请求数量最少'的Server
         */
        bestAvailableRule.choose(SERVICE_NAME);
    }
}
