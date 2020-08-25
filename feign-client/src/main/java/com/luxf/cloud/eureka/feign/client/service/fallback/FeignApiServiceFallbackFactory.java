package com.luxf.cloud.eureka.feign.client.service.fallback;

import com.luxf.cloud.api.entity.UserInfo;
import com.luxf.cloud.eureka.feign.client.service.FeignApiService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author 小66
 * @date 2020-08-23 13:59
 **/
@Component
public class FeignApiServiceFallbackFactory implements FallbackFactory<FeignApiService> {
    /**
     * FallbackFactory的优点是可以获取到异常信息
     *
     * @param throwable 具体异常信息
     */
    @Override
    public FeignApiService create(Throwable throwable) {
        return new FeignApiService() {
            // 方法参数类型不一致的问题, 在实际开发中, 会定义一个接口通用返回类型。
            @Override
            public String getRequestPort() {
                return throwable.getMessage();
            }

            @Override
            public UserInfo getUserInfoById(Long id) {
                return new UserInfo();
            }
        };
    }
}
