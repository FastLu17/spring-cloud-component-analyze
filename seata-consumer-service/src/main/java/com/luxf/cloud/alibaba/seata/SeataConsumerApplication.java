package com.luxf.cloud.alibaba.seata;

import com.alibaba.nacos.client.config.NacosConfigService;
import com.alibaba.nacos.client.config.impl.ClientWorker;
import com.alibaba.nacos.client.config.impl.LocalConfigInfoProcessor;
import io.seata.config.ConfigType;
import io.seata.config.ConfigurationFactory;
import io.seata.config.ConfigurationProvider;
import io.seata.config.nacos.NacosConfiguration;
import io.seata.config.nacos.NacosConfigurationProvider;
import io.seata.spring.annotation.GlobalLock;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.spring.annotation.GlobalTransactionalInterceptor;
import io.seata.tm.api.TransactionalExecutor;
import io.seata.tm.api.TransactionalTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * TODO：如何处理全局事务？
 * {@link GlobalTransactionalInterceptor}处理{@link GlobalTransactional,GlobalLock}注解
 * {@link TransactionalTemplate#execute(TransactionalExecutor)}处理整个全局事务的begin,commit,rollback流程.
 *
 * seata加载配置文件顺序： TODO：优先使用本地配置,再去获取远程配置中心的配置！
 * 1、{@link ConfigurationFactory}静态代码块初始化{@link ConfigurationFactory#CURRENT_FILE_INSTANCE}属性.
 * 2、{@link ConfigurationFactory#getInstance()}被调用时,会调用{@link ConfigurationFactory#buildConfiguration()}.
 * 3、在{@link ConfigurationFactory#buildConfiguration()}中判断是否为{@link ConfigType#File},不是就会根据{@link ConfigurationProvider#provide()}不同实现,调用对应的方法.比如{@link NacosConfigurationProvider#provide()}
 * 4、执行{@link NacosConfiguration#getInstance()},初始化{@link NacosConfiguration},同时会初始化{@link NacosConfiguration#configService}属性,该属性用于加载本地和远端的配置.
 * 5、{@link NacosConfiguration#getConfig(String, String, long)}被调用时,会利用{@link NacosConfiguration#configService}加载对应的配置属性.
 * 6、{@link NacosConfigService#getConfigInner(String, String, String, long)}加载对应的配置属性。
 *
 * // TODO：{@link NacosConfigService#getConfigInner(String, String, String, long)}的简单解读
 * private String getConfigInner(String tenant, String dataId, String group, long timeoutMs) throws NacosException {
 *         group = null2defaultGroup(group);
 *         ParamUtils.checkKeyParam(dataId, group);
 *         ConfigResponse cr = new ConfigResponse();
 *
 *         cr.setDataId(dataId);
 *         cr.setTenant(tenant);
 *         cr.setGroup(group);
 *
 *         // 优先使用本地配置. C:\Users\luxf\nacos\config\fixed-127.0.0.1_8848_nacos\data\config-data\SEATA_GROUP\service.vgroupMapping.seata_test_tx_group
 *         String content = LocalConfigInfoProcessor.getFailover(agent.getName(), dataId, group, tenant);
 *         if (content != null) { // 使用远端配置时,此处为则null.
 *             cr.setContent(content);
 *             configFilterChainManager.doFilter(null, cr);
 *             content = cr.getContent();
 *             return content;
 *         }
 *
 *         try {
 *             // 通过{@link ClientWorker#agent}发起HTTP请求,获取远端的配置.如果请求成功,会先保存本地快照.
 *             // TODO：{@link LocalConfigInfoProcessor#saveSnapshot(String, String, String, String, String)}
 *             content = worker.getServerConfig(dataId, group, tenant, timeoutMs);
 *
 *             cr.setContent(content);
 *             configFilterChainManager.doFilter(null, cr);
 *             content = cr.getContent();
 *             return content;
 *         } catch (NacosException ioe) {
 *             if (NacosException.NO_RIGHT == ioe.getErrCode()) {
 *                 throw ioe;
 *             }
 *         }
 *
 *         // 远程配置请求异常,就从本地快照中获取.
 *         content = LocalConfigInfoProcessor.getSnapshot(agent.getName(), dataId, group, tenant);
 *         cr.setContent(content);
 *         configFilterChainManager.doFilter(null, cr);
 *         content = cr.getContent();
 *         return content;
 *     }
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableTransactionManagement
public class SeataConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataConsumerApplication.class, args);
    }

}
