package com.luxf.cloud.alibaba.seata.controller;

import com.luxf.cloud.alibaba.seata.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 小66
 * @date 2020-08-31 19:41
 **/
@RestController
public class OrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("/order")
    public void createOrder(Integer num, String productId) {
        orderService.createOrder(num, productId);
    }
}
