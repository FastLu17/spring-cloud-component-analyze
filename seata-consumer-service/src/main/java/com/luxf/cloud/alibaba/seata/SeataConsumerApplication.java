package com.luxf.cloud.alibaba.seata;

import com.alibaba.nacos.client.config.NacosConfigService;
import com.alibaba.nacos.client.config.impl.ClientWorker;
import com.alibaba.nacos.client.config.impl.LocalConfigInfoProcessor;
import io.seata.config.ConfigType;
import io.seata.config.ConfigurationFactory;
import io.seata.config.ConfigurationProvider;
import io.seata.config.nacos.NacosConfiguration;
import io.seata.config.nacos.NacosConfigurationProvider;
import io.seata.core.context.RootContext;
import io.seata.core.model.BranchStatus;
import io.seata.core.model.BranchType;
import io.seata.core.protocol.transaction.BranchCommitRequest;
import io.seata.core.protocol.transaction.BranchRollbackRequest;
import io.seata.core.protocol.transaction.UndoLogDeleteRequest;
import io.seata.core.rpc.netty.AbstractRpcRemotingClient;
import io.seata.core.rpc.netty.RmRpcClient;
import io.seata.core.rpc.netty.TmRpcClient;
import io.seata.rm.AbstractRMHandler;
import io.seata.rm.AbstractResourceManager;
import io.seata.rm.DefaultRMHandler;
import io.seata.rm.RMClient;
import io.seata.rm.datasource.*;
import io.seata.rm.datasource.exec.AbstractDMLBaseExecutor;
import io.seata.rm.datasource.exec.BaseTransactionalExecutor;
import io.seata.rm.datasource.exec.ExecuteTemplate;
import io.seata.rm.datasource.exec.StatementCallback;
import io.seata.rm.datasource.undo.AbstractUndoLogManager;
import io.seata.rm.datasource.undo.SQLUndoLog;
import io.seata.rm.datasource.undo.UndoLogManager;
import io.seata.rm.datasource.undo.UndoLogParser;
import io.seata.spring.annotation.GlobalLock;
import io.seata.spring.annotation.GlobalTransactionScanner;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.spring.annotation.GlobalTransactionalInterceptor;
import io.seata.tm.api.DefaultGlobalTransaction;
import io.seata.tm.api.GlobalTransactionRole;
import io.seata.tm.api.TransactionalExecutor;
import io.seata.tm.api.TransactionalTemplate;
import io.seata.tm.api.transaction.TransactionHook;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * TODO：如何处理全局事务？  核心：{@link GlobalTransactionScanner}
 * {@link GlobalTransactionScanner}继承了{@link AbstractAutoProxyCreator},并且重写了{@link AbstractAutoProxyCreator#wrapIfNecessary(Object, String, Object)}和{@link AbstractAutoProxyCreator#getAdvicesAndAdvisorsForBean(Class, String, TargetSource)}
 * {@link GlobalTransactionalInterceptor}处理{@link GlobalTransactional,GlobalLock}注解
 * {@link TransactionalTemplate#execute(TransactionalExecutor)}处理整个全局事务的begin,commit,rollback流程.
 * <p>
 * seata加载配置文件顺序： TODO：优先使用本地配置,再去获取远程配置中心的配置！
 * 1、{@link ConfigurationFactory}静态代码块初始化{@link ConfigurationFactory#CURRENT_FILE_INSTANCE}属性.
 * 2、{@link ConfigurationFactory#getInstance()}被调用时,会调用{@link ConfigurationFactory#buildConfiguration()}.
 * 3、在{@link ConfigurationFactory#buildConfiguration()}中判断是否为{@link ConfigType#File},不是就会根据{@link ConfigurationProvider#provide()}不同实现,调用对应的方法.比如{@link NacosConfigurationProvider#provide()}
 * 4、执行{@link NacosConfiguration#getInstance()},初始化{@link NacosConfiguration},同时会初始化{@link NacosConfiguration#configService}属性,该属性用于加载本地和远端的配置.
 * 5、{@link NacosConfiguration#getConfig(String, String, long)}被调用时,会利用{@link NacosConfiguration#configService}加载对应的配置属性.
 * 6、{@link NacosConfigService#getConfigInner(String, String, String, long)}加载对应的配置属性。
 * <p>
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

    /**
     * 1、在{@link GlobalTransactionScanner#initClient()}初始化时,会初始化{@link AbstractRpcRemotingClient}的实现{@link TmRpcClient,RmRpcClient},用于TM和RM远程访问TC的客户端.
     *
     * 2、在{@link RMClient#init(String, String)}时,会注册{@link DefaultRMHandler}
     * public static void init(String applicationId, String transactionServiceGroup) {
     *      RmRpcClient rmRpcClient = RmRpcClient.getInstance(applicationId, transactionServiceGroup);
     *      rmRpcClient.setResourceManager(DefaultResourceManager.get());
     *      // TODO：利用{@link DefaultRMHandler}注册监听器、监听TC发出二阶段的commit/rollback的指令时,做出对应的处理.
     *      // {@link DefaultRMHandler#handle(BranchCommitRequest)},{@link DefaultRMHandler#handle(BranchRollbackRequest)},{@link DefaultRMHandler#handle(UndoLogDeleteRequest)}
     *      rmRpcClient.setClientMessageListener(new RmMessageListener(DefaultRMHandler.get(), rmRpcClient));
     *      rmRpcClient.init();
     * }
     *
     * // TODO：{@link DefaultRMHandler}的不同handle()方法具体由{@link AbstractResourceManager}完成处理。
     * 3、由{@link AbstractResourceManager}具体实现执行commit/rollback等.如TA模式的{@link DataSourceManager}
     *
     * @see DataSourceManager#branchCommit(BranchType, String, long, String, String) TODO：'二阶段'分支事务提交, 利用{@link AsyncWorker#ASYNC_COMMIT_BUFFER}的队列,配合{@link ScheduledExecutorService}完成分支事务异步commit操作、
     * @see DataSourceManager#branchRollback(BranchType, String, long, String, String) TODO：'二阶段'分支事务回滚, 利用{@link UndoLogManager#undo(DataSourceProxy, String, long)}删除undo_log日志。
     * 在undo()方法中,会根据不同情况会<p>反向补偿并删除日志</p>或者生成防资源悬挂的日志{@link AbstractUndoLogManager#insertUndoLogWithGlobalFinished(String, long, UndoLogParser, Connection)}
     *
     * TODO：{@link AbstractResourceManager#branchRegister(BranchType, String, String, String, String, String)} '一阶段'分支事务注册、由{@link ConnectionProxy#processGlobalTransactionCommit()}发起注册.
     * TODO：{@link AbstractResourceManager#branchReport(BranchType, String, long, BranchStatus, String)} '一阶段'分支事务报告完成/失败
     *
     * 4、由{@link GlobalTransactionalInterceptor#invoke(MethodInvocation)}处理{@link GlobalTransactional,GlobalLock}注解,利用{@link TransactionalTemplate#execute(TransactionalExecutor)}发起/参与全局事务
     * @see GlobalTransactionRole.Launcher 全局事务发起者
     * @see GlobalTransactionRole.Participant 全局事务参与者
     * // TODO：{@link TransactionalTemplate#execute(TransactionalExecutor)}方法解析, 存在{@link TransactionHook}接口,可以实现对事务的begin,commit,rollback进行前置及后置处理.
     * public Object execute(TransactionalExecutor business) throws Throwable {
     *      // get transactionInfo
     *      TransactionInfo txInfo = business.getTransactionInfo();
     *
     *      // Launcher：创建发起者全局事务,此时{@link RootContext#getXID() == null}, TODO：在{@link DefaultGlobalTransaction#begin(int, String)}会利用{@link RootContext#bind(String)}初始化XID.
     *      // Participant：创建参与者全局事务,此时{@link RootContext#getXID() != null}
     *      GlobalTransaction tx = GlobalTransactionContext.getCurrentOrCreate();
     *
     *      // 处理事务传播行为和分支类型{@link BranchType.AT,BranchType.TCC,BranchType.SAGA,BranchType.XA}
     *      ...
     *
     *      try {
     *          // 开始事务,不是'Launcher'就直接退出方法. {@link DefaultGlobalTransaction#begin(int, String)} TODO：初始化XID
     *          beginTransaction(txInfo, tx);
     *          Object rs = null;
     *          try {
     *              // 执行{@link GlobalTransactional}的方法具体业务逻辑.
     *              rs = business.execute();
     *          } catch (Throwable ex) {
     *
     *              // the needed business exception to rollback. 支持重试的回滚！
     *              completeTransactionAfterThrowing(txInfo, tx, ex);
     *              throw ex;
     *          }
     *
     *          // everything is fine, commit. 支持重试的提交！
     *          commitTransaction(tx);
     *
     *          return rs;
     *      } finally {
     *          //5. clear
     *          triggerAfterCompletion();
     *          cleanUp();
     *      }
     * }
     *
     * // 关于JDBC数据库操作的代理.
     * 5、{@link AbstractDataSourceProxy,AbstractConnectionProxy,AbstractStatementProxy}
     * @see StatementProxy#execute(String, String[]) TODO：execute()方法具有多种实现,内部由{@link ExecuteTemplate#execute(List, StatementProxy, StatementCallback, Object...)}执行具体的SQL.
     * public static <T, S extends Statement> T execute(List<SQLRecognizer> sqlRecognizers, StatementProxy<S> statementProxy,
     *                                                      StatementCallback<T, S> statementCallback,
     *                                                      Object... args) throws SQLException {
     *
     *         if (!RootContext.inGlobalTransaction() && !RootContext.requireGlobalLock()) {
     *             // 不是全局事务时,正常执行(original statement).
     *             return statementCallback.execute(statementProxy.getTargetStatement(), args);
     *         }
     *
     *         // 解析SQL
     *         if (sqlRecognizers == null) {
     *             sqlRecognizers = SQLVisitorFactory.get( statementProxy.getTargetSQL(),
     *                     statementProxy.getConnectionProxy().getDbType());
     *         }
     *         Executor<T> executor;
     *         // 根据对不同类型的SQL,初始化不同的执行器. TODO：SELECT_FOR_UPDATE,INSERT,UPDATE,DELETE等
     *         ...
     *
     *         T rs;
     *         try {
     *             // 执行SQL. TODO：{@link BaseTransactionalExecutor}两个实现.
     *             // 主要看{@link AbstractDMLBaseExecutor#doExecute(Object...)}
     *             // 内部最终执行{@link AbstractDMLBaseExecutor#executeAutoCommitFalse(Object[])} TODO：生成beforeImage,afterImage,UndoLog
     *             rs = executor.execute(args);
     *         } catch (Throwable ex) {
     *             throw (SQLException) ex;
     *         }
     *         return rs;
     *     }
     * TODO：{@link AbstractDMLBaseExecutor#executeAutoCommitFalse(Object[])} 生成beforeImage,afterImage,UndoLog.
     * protected T executeAutoCommitFalse(Object[] args) throws Exception {
     *     // SQL执行前快照.
     *     TableRecords beforeImage = beforeImage();
     *     // 执行SQL.
     *     T result = statementCallback.execute(statementProxy.getTargetStatement(), args);
     *     // SQL执行后快照.
     *     TableRecords afterImage = afterImage(beforeImage);
     *     // 准备undoLog. TODO：将undoLog绑定到{@link ConnectionProxy#context}中.
     *     prepareUndoLog(beforeImage, afterImage);
     *     return result;
     * }
     *
     * @see ConnectionProxy#processGlobalTransactionCommit() TODO：处理'Phase One'分支事务提交.
     * private void processGlobalTransactionCommit() throws SQLException {
     *      try {
     *          // TODO：利用{@link RmRpcClient}向TC注册分支事务,获得BranchId.
     *          register();
     *      } catch (TransactionException e) {
     *          // 注册失败.
     *          recognizeLockKeyConflictException(e, context.buildLockKeys());
     *      }
     *      try {
     *          // {@link UndoLogManager#flushUndoLogs(ConnectionProxy)}
     *          // 通过{@link AbstractUndoLogManager#insertUndoLogWithNormal(String, long, String, byte[], Connection)}新增 undo_log日志.
     *          UndoLogManagerFactory.getUndoLogManager(this.getDbType()).flushUndoLogs(this);
     *          targetConnection.commit();
     *      } catch (Throwable ex) {
     *          // TODO：通过RMClient向TC汇报分支事务提交异常.
     *          report(false);
     *          throw new SQLException(ex);
     *      }
     *      // TODO：默认值为false,不报告成功. TC默认没有汇报即成功,降低开销！
     *      if (IS_REPORT_SUCCESS_ENABLE) {
     *          report(true);
     *      }
     *      // 重置、
     *      context.reset();
     * }
     *
     * 6、利用初始化{@link RmRpcClient}时,绑定到监听器上的{@link AbstractRMHandler}TODO：处理 'Phase Two'事务内容. 前面2,3部分已描述.
     */
    public static void main(String[] args) {
        SpringApplication.run(SeataConsumerApplication.class, args);
    }

}
