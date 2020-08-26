package com.luxf.cloud.eureka.client.user;

import com.luxf.cloud.eureka.client.user.anno.MyLoadBalanced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 正常情况下：eureka client既是服务提供者,又是服务消费者。
 * <p>
 * 声明式服务调用：Spring Cloud Feign,需要{@link EnableFeignClients}注解标识
 */
@SpringCloudApplication
public class UserClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserClientApplication.class, args);
    }

    /**
     * {@link LoadBalanced}：表示支持负载均衡、
     * 如果没有配置{@link LoadBalanced}注解,则在{@link LoadBalancerAutoConfiguration#restTemplates}中,
     * 该属性的size为0,无法为{@link RestTemplate}注册{@link LoadBalancerInterceptor},在使用Ribbon的方式进行远程调用时,无法进行负载均衡。
     *
     * TODO：为何在{@link LoadBalancerAutoConfiguration#restTemplates}字段上,多一个@LoadBalanced后,无法注入不具有@LoadBalanced的{@link RestTemplate}
     *
     * 由于{@link LoadBalanced}定义时,存在{@link Qualifier}注解、该注解的高级功能。
     * 可以在使用{@link Autowired}时,导入只有指定注解(类似{@link LoadBalanced},该注解在定义时具有{@link Qualifier})的Bean、
     */
    @Bean
    //@LoadBalanced
    @MyLoadBalanced
    public RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate();
        // 如果不添加@LoadBalanced注解、就需要手动注入LoadBalancerInterceptor拦截器
        // restTemplate.setInterceptors(new LoadBalancerInterceptor(client));
        return restTemplate;
    }

}
