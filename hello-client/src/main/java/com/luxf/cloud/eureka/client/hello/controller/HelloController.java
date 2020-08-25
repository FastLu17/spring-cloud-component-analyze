package com.luxf.cloud.eureka.client.hello.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 小66
 * @date 2020-08-22 12:49
 **/
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello(HttpServletRequest request) {
        int localPort = request.getLocalPort();
        int serverPort = request.getServerPort();
        System.out.println("serverPort = " + serverPort);
        System.out.println("localPort = " + localPort);
        return String.valueOf(serverPort);
    }
}
