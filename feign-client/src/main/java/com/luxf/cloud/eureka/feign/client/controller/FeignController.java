package com.luxf.cloud.eureka.feign.client.controller;

import com.luxf.cloud.eureka.feign.client.service.FeignUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 小66
 * @date 2020-08-23 0:23
 **/
@RestController
@RequestMapping("/feign-client/secure")
public class FeignController {

    @Resource
    private FeignUserService feignUserService;

    @GetMapping(value = "/feign-hello")
    public String sayHello() {
        String sayHello = feignUserService.hello();
        System.out.println("sayHello = " + sayHello);
        return sayHello;
    }
}
