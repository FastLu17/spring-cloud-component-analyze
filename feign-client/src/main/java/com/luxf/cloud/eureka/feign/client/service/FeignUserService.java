package com.luxf.cloud.eureka.feign.client.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 可以直接调用 user-client 服务的接口、
 *
 * 没有继承API接口的{@link FeignClient},'fallback'和'fallbackFactory' 都可以使用.
 *
 * @author 小66
 * @date 2020-08-23 0:19
 **/
//@FeignClient(value = "USER-SERVICE", fallback = FeignServiceFallback.class)
@FeignClient(value = "USER-SERVICE")
public interface FeignUserService {

    /**
     * 注意：对于feign接口来说、{@link HttpServletRequest}等参数,不需要传递、
     * TODO: 定义在{@link FeignClient}中的接口的方法名,不一定要与被调用的服务的方法名相同,
     * 只需要请求路径、请求类型和请求参数相同即可。如果参数有注解标识,则注解也需要相同。
     *
     * @return port
     */
    @GetMapping("/load-balance")
    String hello();// user-client的controller中, "/hello"对应的方法名字是"loadBalanceTest"
}
