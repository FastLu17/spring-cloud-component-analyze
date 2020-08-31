package com.luxf.cloud.alibaba.seata.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 小66
 * @date 2020-08-31 19:43
 **/
@FeignClient(value = "seata-provider-service")
public interface StorageApiService {

    @DeleteMapping("/storage")
    Boolean decrement(@RequestParam("num") Integer num, @RequestParam("productId") String productId);

}
