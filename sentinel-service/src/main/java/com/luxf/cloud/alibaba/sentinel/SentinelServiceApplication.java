package com.luxf.cloud.alibaba.sentinel;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.property.PropertyListener;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlotChain;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.List;

/**
 * sentinel通过{@link SentinelResourceAspect}这个拦截器处理{@link SentinelResource}注解的逻辑.
 *
 * <P>sentinel不存在 '半开' 的状态</P>
 * <p>
 * TODO：Spring Cloud Alibaba Sentinel Wiki -> https://github.com/alibaba/spring-cloud-alibaba/wiki/Sentinel
 * TODO：需要配置持久化,否则重启项目,原先配置对应的5种Rule都会消失。
 * 文档中详细描述如何支持持久化、Feign、RestTemplate、Gateway等..
 * <p>
 * TODO：Sentinel适配Feign组件,除了引入相关jar包外, 还需要配置 feign.sentinel.enabled=true
 * Feign对应的接口中的资源名策略定义：httpmethod:protocol://requesturl。@FeignClient 注解中的所有属性，Sentinel都做了兼容。
 * 比如：EchoService 接口中方法echo()对应的资源名为 GET:http://service-provider/echo/{str}。
 * <p>
 * TODO：如果使用RestTemplate调用服务,则需要在构造RestTemplate bean的时候需要加上 @SentinelRestTemplate注解。
 *
 * @Bean
 * @SentinelRestTemplate(blockHandler = "handleException", blockHandlerClass = ExceptionUtil.class)
 * public RestTemplate restTemplate() {
 * return new RestTemplate();
 * }
 * <p>
 * sentinel-server页面配置的规则：5个.具体如下：
 * TODO：都是通过{@link PropertyListener}监听配置的改变.
 * 1、流控规则{@link FlowRule}：对应{@link FlowRuleManager},该Manager会监控{@link FlowRule}的变更,并通过{@link FlowRuleManager.FlowPropertyListener#configUpdate(List)}方法监听并改变对应app的{@link FlowRule}.
 * 2、降级规则{@link DegradeRule}：对应{@link DegradeRuleManager}
 * 3、热点规则{@link ParamFlowRule}：对应{@link ParamFlowRuleManager}
 * 4、系统规则{@link SystemRule}：对应{@link SystemRuleManager}
 * 5、授权规则{@link AuthorityRule}：对应{@link AuthorityRuleManager}
 * <p>
 * 以上5个规则,分别对应自己的Slot和Checker->{@link AbstractLinkedProcessorSlot}的实现类.
 * 通过sentinel的核心骨架{@link ProcessorSlotChain}将不同的Slot按顺序串在一起。
 * TODO：Sentinel核心类解析官方文档 -> https://github.com/alibaba/Sentinel/wiki/Sentinel-核心类解析
 */
@EnableDiscoveryClient
@SpringBootApplication
public class SentinelServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelServiceApplication.class, args);
    }

}
