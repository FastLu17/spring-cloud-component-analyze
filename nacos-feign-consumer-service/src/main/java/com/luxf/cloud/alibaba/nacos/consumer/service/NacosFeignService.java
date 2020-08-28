package com.luxf.cloud.alibaba.nacos.consumer.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 小66
 * @date 2020-08-28 19:18
 **/
@FeignClient("nacos-provider")
@RequestMapping("/nacos/provider")
public interface NacosFeignService {

    @GetMapping("/port")
    String getProviderPort();
}
