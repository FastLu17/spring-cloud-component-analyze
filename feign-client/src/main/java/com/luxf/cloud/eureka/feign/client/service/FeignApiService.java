package com.luxf.cloud.eureka.feign.client.service;

import com.luxf.cloud.api.service.HelloApiController;
import com.luxf.cloud.eureka.feign.client.service.fallback.FeignApiServiceFallbackFactory;
import com.netflix.hystrix.HystrixCommand;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;

/**
 * {@link FeignClient}注解内容不可少、对于可以访问HELLO-SERVICE服务的接口具体内容已由{@link HelloApiController}定义完成
 * <p>
 * TODO: 只能使用{@link FeignClient#fallbackFactory()}处理Hystrix降价、 FallbackFactory 的优点是可以获取到异常信息。
 * 如果{@link FeignClient}的接口继承了对应的服务的API接口, 只能使用'fallbackFactory',不能使用'fallback',使用'fallback'会报错：存在映射路径相同的问题！
 * <p>
 * 对于{@link FeignClient}的接口的Bean对象,与{@link feign.hystrix.HystrixInvocationHandler}拦截器绑定,
 * 当在调用{@link FeignClient}接口的方法时,都会被{@link feign.hystrix.HystrixInvocationHandler#invoke(Object, Method, Object[])}所拦截,
 * 由{@link HystrixCommand#run()}进行具体方法的invoke()操作！
 * <p>
 * Ribbon是利用{@link LoadBalancerInterceptor}拦截器,绑定到{@link RestTemplate}上,拦截{@link RestTemplate}的方法,进行负载均衡。
 *
 * @author 小66
 * @date 2020-08-23 12:59
 **/
//@FeignClient(value = "HELLO-SERVICE", fallback = FeignApiServiceFallback.class) // 由于FeignApiService继承了HelloApiController接口、使用fallback会报错
@FeignClient(value = "HELLO-SERVICE", fallbackFactory = FeignApiServiceFallbackFactory.class)
public interface FeignApiService extends HelloApiController {
}
