package com.luxf.cloud.alibaba.seata.controller;

import com.luxf.cloud.alibaba.seata.service.StorageService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 小66
 * @date 2020-08-31 19:11
 **/
@RestController
public class StorageController {

    @Resource
    private StorageService storageService;

    @DeleteMapping("/storage")
    public Boolean decrement(Integer num, String productId) {
        storageService.decrement(num, productId);
        return true;
    }
}
