package com.luxf.cloud.alibaba.seata.service.impl;

import com.luxf.cloud.alibaba.seata.api.StorageApiService;
import com.luxf.cloud.alibaba.seata.mapper.OrderMapper;
import com.luxf.cloud.alibaba.seata.service.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 小66
 * @date 2020-08-31 19:44
 **/
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    StorageApiService storageApiService;

    /**
     * 利用{@link GlobalTransactional}开启分布式全局事务.
     */
    @Override
    @GlobalTransactional
    public void createOrder(Integer num, String productId) {
        orderMapper.createOrder(num, productId);
        // 远程调用出现问题, 如果只使用@Transactional,order表数据回滚,但是storage表的数据不会回滚！
        Boolean decrement = storageApiService.decrement(num, productId);
        System.out.println("decrement = " + decrement);
        String xid = RootContext.getXID();
        System.out.println("xid = " + xid);
    }
}
