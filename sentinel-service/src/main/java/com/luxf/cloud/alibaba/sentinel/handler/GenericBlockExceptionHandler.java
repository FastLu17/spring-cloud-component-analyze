package com.luxf.cloud.alibaba.sentinel.handler;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * {@link SentinelResource#blockHandler}：请求触发限流时的处理方法.
 * {@link SentinelResource#fallback}：请求触发降级时的超计划里方法.
 * <p>
 * {@link SentinelResource#blockHandler} 该属性的对应的方法,如果是通用的方法(不是在本类中构造的同名方法),
 * 则需要指定{@link SentinelResource#blockHandlerClass},并且需要是静态方法.
 *
 * @see SentinelResource#blockHandlerClass -> Note that the block handler method must be static.
 *
 * @author 小66
 * @date 2020-08-29 19:34
 **/
public class GenericBlockExceptionHandler {
    public static String handleException(BlockException ex) {
        return "Trigger GenericBlockExceptionHandler!  Resource is：" +
                ex.getRule().getResource() + ", Exception is：" + ex.getMessage();
    }
}
