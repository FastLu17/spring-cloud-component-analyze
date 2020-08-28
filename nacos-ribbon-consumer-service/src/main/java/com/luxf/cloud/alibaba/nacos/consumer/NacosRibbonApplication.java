package com.luxf.cloud.alibaba.nacos.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NacosRibbonApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosRibbonApplication.class, args);
    }

}
