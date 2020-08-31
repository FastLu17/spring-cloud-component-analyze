package com.luxf.cloud.alibaba.seata.service;

/**
 * @author 小66
 * @date 2020-08-31 19:44
 **/
public interface OrderService {
    void createOrder(Integer num, String productId);
}
