package com.luxf.cloud.eureka.client.hello.controller;

import com.luxf.cloud.api.entity.UserInfo;
import com.luxf.cloud.api.service.HelloApiController;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 利用 hello-service-api 提供的对外接口、
 *
 * @author 小66
 * @date 2020-08-23 12:44
 **/
@RestController
public class HelloApiControllerImpl extends BaseController implements HelloApiController {
    @Override
    public String getRequestPort() {
        int serverPort = this.getCurrentRequest().getServerPort();
        System.out.println("serverPort in HelloApiController = " + serverPort);
        return String.valueOf(serverPort);
    }

    @Override
    public UserInfo getUserInfoById(Long id) {
        System.out.println("UserInfo#id = " + id);
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(18);
        userInfo.setName("JACK");
        userInfo.setId(System.currentTimeMillis());
        return userInfo;
    }
}
