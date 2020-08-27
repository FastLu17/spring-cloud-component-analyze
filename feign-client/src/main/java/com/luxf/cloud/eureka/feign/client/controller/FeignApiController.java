package com.luxf.cloud.eureka.feign.client.controller;

import com.luxf.cloud.api.entity.UserInfo;
import com.luxf.cloud.eureka.feign.client.factory.INamedContextFactory;
import com.luxf.cloud.eureka.feign.client.helper.ApplicationContextHelper;
import com.luxf.cloud.eureka.feign.client.helper.ReflectHelper;
import com.luxf.cloud.eureka.feign.client.service.FeignApiService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author 小66
 * @date 2020-08-23 13:02
 **/
@RestController
@RequestMapping("/feign-client/secure")
public class FeignApiController {

    /**
     * 使用List的方式,可以注入容器中所有的{@link NamedContextFactory}对象(包含子类)、
     */
    @Resource
    private List<NamedContextFactory> contextFactoryList = Collections.emptyList();

    @Resource
    private FeignApiService apiService;

    /**
     * 如果getPort()方法内出现错误/超时等情况,触发Hystrix、会使用getPort()方法上的@HystrixCommand的fallback方法进行降级。
     * 如果是由于{@link FeignApiService#getRequestPort()}方法引起的问题触发Hystrix, 则会使用FeignApiService对应的fallback方法进行降级。
     * <p>
     * TODO：服务内方法降级和远程服务调用降级的处理的不同。
     */
    @GetMapping("api-port")
    @HystrixCommand(fallbackMethod = "getPortFallbackMethod")// 使用@HystrixCommand注解也可以配置降级方法。
    public String getPort() {
        String currentPort = ApplicationContextHelper.getEnvironment().getProperty("server.port");
        System.out.println("currentPort = " + currentPort);

        System.out.println("contextFactoryList.size() = " + contextFactoryList.size());
        /**
         *
         * 模拟{@link NamedContextFactory}的实现、可以在使用{@link INamedContextFactory#getContext(String)}时,才进行注入{@link com.luxf.cloud.eureka.config.ApplicationConfiguration}
         * 因为{@link com.luxf.cloud.eureka.config.ApplicationConfiguration}的packagePath没有被扫描到,不会自动注入到容器中！
         */
        INamedContextFactory namedContextFactory = ApplicationContextHelper.getBean(INamedContextFactory.class);
        AnnotationConfigApplicationContext feign = namedContextFactory.getContext("FEIGN");

        /**
         * TODO：{@link FeignContext,SpringClientFactory}会被自动注入、注入时,分别传入FeignClientsConfiguration和RibbonClientConfiguration的Class对象、
         * @see RibbonAutoConfiguration#springClientFactory()
         * @see FeignAutoConfiguration#feignContext()
         *
         * TODO: 在进行远程服务调用时,{@link RibbonLoadBalancerClient#clientFactory}(不止这一个类中)会调用到{@link NamedContextFactory#getInstance(String, Class)},触发主动加载{@link RibbonClientConfiguration}
         * @see org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient#clientFactory
         *
         * TODO：在Spring容器的{@link FactoryBean#getObject()}方法被调用时,会进行初始化
         * @see org.springframework.cloud.openfeign.FeignClientFactoryBean#getObject(), 该方法中获取了{@link FeignContext}对象、最终由{@link HystrixTargeter}触发主动加载{@link FeignClientsConfiguration}
         * @see org.springframework.cloud.openfeign.HystrixTargeter 该Bean对象由{@link FeignAutoConfiguration}进行初始化。
         */
        Map<String, NamedContextFactory> beansOfType = ApplicationContextHelper.getBeansOfType(NamedContextFactory.class);
        FeignContext feignContext = ApplicationContextHelper.getBean(FeignContext.class);
        SpringClientFactory clientFactory = ApplicationContextHelper.getBean(SpringClientFactory.class);
        System.out.println("clientFactory = " + clientFactory);
        Map<String, AnnotationConfigApplicationContext> contextsMap = ReflectHelper.getFieldValue(clientFactory, ReflectHelper.findField(clientFactory.getClass(), "contexts"));
        contextsMap.forEach((key, val) -> {
            try {
                // 在FeignClientsConfiguration和RibbonClientConfiguration对应的ApplicationContext中才有Ribbon相关的实例对象.
                // 以上2个@Configuration对象不支持@EnableAutoConfiguration,又没有被@Import和@ComponentScan使用到,因此不会自动注入到容器中.
                IRule bean = val.getBean(IRule.class);
                System.out.println("bean = " + bean);
            } catch (Exception e) {
                System.out.println("getBean(IRule.class) = " + e.getMessage());
            }
        });

        // 如果FeignApiService#getRequestPort()也触发了服务降级,则先执行FeignApiService#getRequestPort()的降级方法。
        // 由于getPort()方法最后又出现错误、因此会触发getPort()方法上的@HystrixCommand、
        String requestPort = apiService.getRequestPort();
        System.out.println("requestPort = " + requestPort);
        // 此时由于是getPort()方法内触发Hystrix,执行的降级方法是@HystrixCommand对应的getPortFallbackMethod()、
        // int i = 10 / 0;
        return currentPort;
    }

    @GetMapping("api-user/{id}")
    public UserInfo getUserInfoById(@PathVariable Long id) {
        UserInfo userInfoById = apiService.getUserInfoById(id);
        System.out.println("userInfoById = " + userInfoById);
        // 由于该getUserInfoById()方法并没有维护 服务降级的方法、不会处理异常。此处直接报错！
        int i = 10 / 0;
        return userInfoById;
    }

    public String getPortFallbackMethod() {
        return "0000";
    }
}
