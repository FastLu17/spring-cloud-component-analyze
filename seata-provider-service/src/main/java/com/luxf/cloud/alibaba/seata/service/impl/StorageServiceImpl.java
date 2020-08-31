package com.luxf.cloud.alibaba.seata.service.impl;

import com.luxf.cloud.alibaba.seata.mapper.StorageMapper;
import com.luxf.cloud.alibaba.seata.service.StorageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 小66
 * @date 2020-08-31 19:17
 **/
@Service
public class StorageServiceImpl implements StorageService {
    @Resource
    private StorageMapper storageMapper;

    @Override
    public void decrement(Integer num, String productId) {
        storageMapper.decrement(num, productId);
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
