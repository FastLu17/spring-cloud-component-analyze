package com.luxf.cloud.eureka.client.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 小66
 * @date 2020-08-23 12:52
 **/
@Component
public class BaseController {
    @Autowired
    private HttpServletRequest request;


    public HttpServletRequest getCurrentRequest() {
        return request;
    }
}
