package com.luxf.cloud.eureka.client.user.controller;

import com.luxf.cloud.eureka.client.user.anno.MyLoadBalanced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * @author 小66
 * @date 2020-08-22 13:01
 **/
@RestController
public class RibbonController {

    @Autowired
    private RestTemplate restTemplate;

    @MyLoadBalanced
    @Autowired
    private List<RestTemplate> templateList = Collections.emptyList();

    @GetMapping("/load-balance")
    public String loadBalanceTest() {
        System.out.println("templateList.size() = " + templateList.size());
        /**
         * Ribbon主要是用于客户端负载均衡,微服务之间的调用,API网关请求转发等内容.
         * Feign也是基于Ribbon实现的工具。
         *
         * 通过Ribbon的方式实现服务消费.
         * Ribbon默认采用<P>轮询方式</P>的负载均衡方式调用服务.
         */
        ResponseEntity<String> forEntity = restTemplate
                .getForEntity("http://HELLO-SERVICE/hello", String.class);

//        restTemplate.getForEntity("http://HELLO-SERVICE/hello?name={1}", String.class,"Jack");
        String forObject = restTemplate.getForObject("http://HELLO-SERVICE/hello", String.class);

        String body = forEntity.getBody();
        System.out.println("body = " + body);
        return body;
    }
}
