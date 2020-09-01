package com.luxf.cloud.alibaba.seata.api;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author 小66
 * @date 2020-09-01 16:11
 **/
@Component
public class StorageApiFallback implements FallbackFactory<StorageApiService> {
    @Override
    public StorageApiService create(Throwable cause) {
        return (num, productId) -> {
            System.out.println("Trigger Fallback!");
            return false;
        };
    }
}
