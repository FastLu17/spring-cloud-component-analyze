package com.luxf.cloud.alibaba.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;

/**
 * @author 小66
 * @date 2020-08-29 21:29
 **/
public class BaseController {

    /**
     * {@link SentinelResource#defaultFallback}不允许存在{@link Throwable}以外任何参数.
     * @param thw 唯一参数. 可以不需要此参数,正常需要此参数记录详细日志.
     * @return 返回值类型要与方法的相同. 实际中,都是自定义统一的返回值类型。
     */
    public String baseDefaultFallback(Throwable thw) {
        return "Trigger baseDefaultFallback! Exception is：" + thw.getMessage();
    }
}
