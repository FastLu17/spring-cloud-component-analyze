package com.luxf.cloud.alibaba.nacos.consumer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author 小66
 * @date 2020-08-28 15:50
 **/
@RestController
@RequestMapping("/nacos/consumer")
public class RibbonConsumerController {

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/ribbon/port")
    public String getProviderPortByRibbon() {
        String providerPort = restTemplate.getForObject("http://nacos-provider/nacos/provider/port", String.class);
        System.out.println("providerPort = " + providerPort);
        return providerPort;
    }
}

