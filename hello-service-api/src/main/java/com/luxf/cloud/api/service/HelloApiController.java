package com.luxf.cloud.api.service;

import com.luxf.cloud.api.entity.UserInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 提供对于eureka的HELLO-SERVICE服务的API接口、
 *
 * @author 小66
 * @date 2020-08-23 12:32
 **/
@RequestMapping("/feign-api/hello-service")
public interface HelloApiController {

    @GetMapping("/hello")
    String getRequestPort();

    @GetMapping("/user/{id}")
    UserInfo getUserInfoById(@PathVariable("id") Long id);
}
