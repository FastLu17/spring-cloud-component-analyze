package com.luxf.cloud.alibaba.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.luxf.cloud.alibaba.sentinel.handler.GenericBlockExceptionHandler;
import com.luxf.cloud.alibaba.sentinel.handler.GenericFallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO：需要配置持久化,否则重启项目,原先配置对应的5种Rule都会消失。
 *
 * @author 小66
 * @date 2020-08-29 11:27
 **/
@RestController
@RequestMapping("/flow-limit")
public class FlowLimitController extends BaseController {

    /**
     * {@link SentinelResource#defaultFallback}和{@link SentinelResource#fallback}同时存在时, 只有fallback对应的方法生效.
     */
    @GetMapping("/a")
    @SentinelResource(value = "/flow-limit/a", defaultFallback = "defaultFallback", fallback = "handleFallback",
            fallbackClass = GenericFallback.class, blockHandler = "exceptionHandler")
    public String testA() {
        int x = 2 / 0;
        return "flow-limit------testA";
    }

    /**
     * 如果存在{@link SentinelResource}注解,并且{@link SentinelResource#value}的值与完整的请求URL路径不同时,
     * 在sentinel-server的rule配置页面,针对请求URL配置流控降级等Rule不会生效.
     * <p>
     * TODO：因此 配置rule的资源名,都需要使用{@link SentinelResource#value}属性值
     */
    @GetMapping("/b")
    @SentinelResource(value = "resource-b", blockHandlerClass = GenericBlockExceptionHandler.class, blockHandler = "handleException",
            fallback = "handleFallback", fallbackClass = GenericFallback.class)
    public String testB() {
        int i = 10 / 0;

        return "flow-limit------testB";
    }

    @GetMapping("/c")
    @SentinelResource(value = "/flow-limit/c", defaultFallback = "defaultFallback",
            fallback = "fallback", blockHandler = "exceptionHandler")
    public String testC(Long id) {
        int y = 3 / 0;
        return "flow-limit------testC, id is：" + id;
    }

    /**
     * 可以利用继承的{@link BaseController#baseDefaultFallback(Throwable)}方法,配置默认{@link SentinelResource#defaultFallback}.
     */
    @GetMapping("/d")
    @SentinelResource(value = "/flow-limit/d", defaultFallback = "baseDefaultFallback")
    public String testD(Long id) {
        int z = 5 / 0;
        return "flow-limit------testD, id is：" + id;
    }

    /**
     * {@link SentinelResource#defaultFallback}不允许存在{@link Throwable}以外的任何参数.
     *
     * @return 返回值类型要与方法的相同. 实际中,都是自定义统一的返回值类型。
     */
    public String defaultFallback() {
        return "defaultFallback...defaultFallback...";
    }

    /**
     * fallback函数,函数签名与原函数(存在@SentinelResource注解的方法)一致或加一个Throwable类型的参数.
     * 如果参数不同,则fallback不会生效.
     */
    public String fallback(Long id, Throwable thw) {
        return "Trigger Fallback! Param is：" + id + ", Exception is：" + thw.getMessage();
    }

    // Block异常处理函数,参数最后多一个BlockException,其余与原函数一致. 参数不同,不会生效.
    public String exceptionHandler(Long id, BlockException ex) {
        // Do some log here.
        return "Trigger BlockHandler! Param is：" + id +
                ", Resource is：" + ex.getRule().getResource() + ", Exception is：" + ex.getMessage();
    }
}
