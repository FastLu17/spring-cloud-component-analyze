package com.luxf.cloud.alibaba.seata.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 小66
 * @date 2020-08-31 19:18
 **/
@Mapper
public interface StorageMapper {
    void decrement(@Param(value = "num") Integer num, @Param(value = "productId") String productId);
}
