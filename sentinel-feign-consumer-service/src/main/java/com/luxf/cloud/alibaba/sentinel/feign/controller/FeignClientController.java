package com.luxf.cloud.alibaba.sentinel.feign.controller;

import com.luxf.cloud.alibaba.sentinel.feign.service.FeignClientService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author 小66
 * @date 2020-08-30 10:42
 **/
@RestController
@RequestMapping("/sentinel-feign")
public class FeignClientController {

    @Resource
    private FeignClientService clientService;

    @GetMapping("/rpc-a")
    public String rpcA() {
        return clientService.testA();
    }

    @GetMapping("/rpc-e")
    public String rpcE() throws NoSuchMethodException {
        /**
         * 通过{@link FeignClient}注入到Spring容器中的{@link FeignClientService}是JDK的代理类(sun.proxy.$Proxy),无法通过JDK代理类获取接口方法上的注解。
         */
        Method[] methods = clientService.getClass().getMethods();
        for (Method method : methods) {
            Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
            System.out.println("declaredAnnotations.length = " + declaredAnnotations.length);

            Annotation[] methodAnnotations = method.getAnnotations();
            System.out.println("methodAnnotations.length = " + methodAnnotations.length);
        }
        return clientService.testE();
    }
}
