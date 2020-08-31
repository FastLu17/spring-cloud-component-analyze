package com.luxf.cloud.alibaba.seata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableTransactionManagement
public class SeataConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataConsumerApplication.class, args);
    }

}
