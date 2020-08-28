package com.luxf.cloud.alibaba.nacos.config.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * nacos config center：应用可以动态获取nacos配置中心的配置文件中配置的值.
 * TODO：必须要有{@link RefreshScope}注解.
 *
 * @author 小66
 * @date 2020-08-28 20:26
 **/
@RestController
@RequestMapping("/nacos/config")
@RefreshScope// Spring cloud 原生注解, 用于动态刷新配置文件.
public class NacosConfigController {

    /**
     * web-config：这个pair是nacos配置中的配置文件的值。
     */
    @Value("${web-config}")
    private String webConfig;

    @Resource
    private Environment environment;

    @RequestMapping("/config-client")
    public String get() {
        /**
         * 通过{@link Environment}获得的属性, 也是nacos-config-center中的值.
         */
        String port = environment.getProperty("server.port");
        System.out.println("port = " + port);
        System.out.println("webConfig = " + webConfig);
        return webConfig;
    }
}
