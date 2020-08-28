package com.luxf.cloud.alibaba.nacos.provider.controller;

import com.luxf.cloud.alibaba.nacos.provider.helper.ApplicationContextHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 小66
 * @date 2020-08-28 15:13
 **/
@RestController
@RequestMapping("/nacos/provider")
public class NacosController {

    @GetMapping("/port")
    public String getCurrentPort() {
        String serverPort = ApplicationContextHelper.getEnvironmentProperty("server.port");
        System.out.println("serverPort = " + serverPort);
        return serverPort;
    }
}
