package com.luxf.cloud.alibaba.sentinel.handler;

import com.alibaba.csp.sentinel.annotation.SentinelResource;

/**
 * {@link SentinelResource}通用的降级方法.
 *
 * @author 小66
 * @date 2020-08-29 19:47
 **/
public class GenericFallback {

    /**
     * @see SentinelResource#fallbackClass -> Note that the shared fallback method must be static.
     * @param throwable 通用的fallback,不能有除了{@link Throwable}之外的其他参数
     */
    public static String handleFallback(Throwable throwable) {
        return "Trigger GenericFallback! Exception is：" + throwable.getMessage();
    }

    /**
     * 通用的defaultFallback()'不会'在{@link SentinelResource#defaultFallback}上生效.
     */
    public static String defaultFallback() {
        return "defaultFallback.....";
    }
}
