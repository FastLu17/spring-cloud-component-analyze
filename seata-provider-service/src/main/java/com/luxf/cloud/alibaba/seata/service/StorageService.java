package com.luxf.cloud.alibaba.seata.service;

/**
 * @author 小66
 * @date 2020-08-31 19:17
 **/
public interface StorageService {

    void decrement(Integer num, String productId);
}
