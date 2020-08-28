package com.luxf.cloud.alibaba.nacos.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * nacos server 默认自带一个derby数据库进行持久化. 正常情况下,集群的时候需要,不能每个nacos server 使用默认的derby,数据不一致.
 * nacos server支持MySQL数据库。
 *
 * nacos持久化详情：官方文档 https://nacos.io/zh-cn/docs/cluster-mode-quick-start.html
 */
@EnableDiscoveryClient
@SpringBootApplication
public class NacosConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosConfigApplication.class, args);
    }

}
