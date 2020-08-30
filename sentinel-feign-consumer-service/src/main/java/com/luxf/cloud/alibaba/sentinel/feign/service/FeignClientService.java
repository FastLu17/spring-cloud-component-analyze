package com.luxf.cloud.alibaba.sentinel.feign.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import feign.hystrix.FallbackFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * sentinel整合{@link FeignClient}后,可以在sentinel页面配置中,对该资源名'GET:http://sentinel-service/flow-limit/e'进行相关限流降级等操作.
 *
 * @author 小66
 * @date 2020-08-30 10:35
 **/
@FeignClient(value = "sentinel-service", fallbackFactory = FeignClientService.FeignClientFactory.class)
@RequestMapping("/flow-limit")
public interface FeignClientService {

    /**
     * 服务提供方的'/flow-limit/a'对应的方法,进行fallback处理,远程调用不会出错,因而不会触发{@link FeignClient#fallbackFactory}实现的fallback.
     */
    @GetMapping("/a")
    String testA();

    /**
     * 服务提供方的'/flow-limit/e'对应的方法,没有进行fallback处理,远程调用出错,就会触发{@link FeignClient#fallbackFactory}实现的fallback.
     * <p>
     * 在{@link FeignClient}接口的方法上,存在{@link SentinelResource}会报错：Wrong state for SentinelResource annotation.
     * <p>
     * 是不是无法自定义Feign远程调用的请求相关的{@link SentinelResource#blockHandler}？
     *
     * @see SentinelResourceAspect#invokeResourceWithSentinel(ProceedingJoinPoint) 从方法上没有获取到{@link SentinelResource}注解.
     * TODO：但是为什么又通过{@link SentinelResource}注解被{@link SentinelResourceAspect}拦截到了此方法呢？
     *
     * 通过{@link FeignClient}对应的接口,注入到Spring容器中,生成的是JDK代理类(sun.proxy.$Proxy). JDK代理类无法获取被代理接口方法上的注解。
     * 如果是使用的CGLIB代理,则生成的CGLIB代理类同样无法获取被代理对象(接口、类)方法上的注解。
     * @see com.luxf.cloud.alibaba.sentinel.feign.proxy.CglibProxyHelper#main(String[]) TODO：无论何种代理方式,生成的代理类都无法获取被代理对象中的的注解.
     */
    @GetMapping("/e")
    //@SentinelResource("rpc-e")
    String testE();

    @Component
    class FeignClientFactory implements FallbackFactory<FeignClientService> {

        @Override
        public FeignClientService create(Throwable cause) {
            return new FeignClientService() {
                @Override
                public String testA() {
                    return "Trigger Feign Fallback, Method is testA! ExceptionName is：" + cause.getClass().getSimpleName() +
                            ", ExceptionMessage is：" + cause.getMessage();
                }

                /**
                 * TODO：在sentinel中,FeignClient对应的接口中的方法资源名策略定义：HttpMethodName:protocol://requestUrl
                 * 如果在sentinel中,对Feign进行远程调用的资源名''进行相关规则管控后,
                 * 当触发对资源名'GET:http://sentinel-service/flow-limit/e'的降级规则后, 会输出:Exception is null.
                 *
                 * 可以在sentinel页面配置中,对该资源名'GET:http://sentinel-service/flow-limit/e'进行相关限流降级等操作.
                 */
                @Override
                public String testE() {
                    return "Trigger Feign Fallback, Method is testE! ExceptionName is：" + cause.getClass().getSimpleName() +
                            ", ExceptionMessage is：" + cause.getMessage();
                }
            };
        }
    }

}
