package com.luxf.cloud.alibaba.nacos.consumer.controller;

import com.luxf.cloud.alibaba.nacos.consumer.service.NacosFeignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 小66
 * @date 2020-08-28 19:17
 **/
@RestController
@RequestMapping("/nacos/consumer")
public class FeignConsumerController {
    @Resource
    private NacosFeignService feignService;

    @GetMapping("/feign/port")
    public String getProviderPortByFeign() {
        String providerPort = feignService.getProviderPort();
        System.out.println("feign providerPort = " + providerPort);
        return providerPort;
    }
}
